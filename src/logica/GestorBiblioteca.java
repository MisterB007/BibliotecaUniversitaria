package logica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import clases.Administrador;
import clases.EstadoPrestamo;
import clases.Estudiante;
import clases.Libro;
import clases.MaterialBibliografico;
import clases.Prestamo;
import clases.Profesor;
import clases.Revista;
import clases.Tesis;
import clases.Usuario;
import excepciones.MaterialNoDisponibleException;
import excepciones.UsuarioConMultaException;

public class GestorBiblioteca {

	private Path carpetaDatos;
	private Path rutaInventario;
	private Path rutaUsuarios;
	private Path rutaPrestamos;

	private List<MaterialBibliografico> inventario;
	private List<Usuario> usuarios;
	private List<Prestamo> prestamos;

	public GestorBiblioteca() {

		this.carpetaDatos = Path.of("datos");
		this.rutaInventario = Path.of("datos", "inventario.txt");
		this.rutaUsuarios = Path.of("datos", "usuarios.txt");
		this.rutaPrestamos = Path.of("datos", "prestamos.txt");

		this.inventario = new ArrayList<>();
		this.usuarios = new ArrayList<>();
		this.prestamos = new ArrayList<>();

		cargarDatos();
	}

	public synchronized void cargarDatos() {

		try {
			Files.createDirectories(this.carpetaDatos);
		} catch (IOException e) {
			System.out.println("Error al crear carpeta: " + e.getMessage());
		}

		cargarUsuariosDesdeArchivo();
		cargarInventarioDesdeArchivo();
		cargarPrestamosDesdeArchivo();
	}

	public synchronized Usuario autenticarUsuario(String username, String password) {

		Usuario actual;
		String usernameLimpio, usernameActual;

		if (username == null || password == null) {
			return null;
		}

		usernameLimpio = username.trim();

		for (int ind = 0; ind < this.usuarios.size(); ind++) {
			actual = this.usuarios.get(ind);
			usernameActual = actual.getUsername();

			if (usernameActual.equalsIgnoreCase(usernameLimpio) && actual.validarPassword(password)) {
				return actual;
			}
		}

		return null;
	}

	public synchronized List<MaterialBibliografico> buscarCatalogo(String filtro, String tipo) {
		List<MaterialBibliografico> resultados;
		MaterialBibliografico material;
		String tipoLimpio, tipoNormalizado, tipoMaterial;
		boolean coincideTipo;

		resultados = new ArrayList<>();

		if (tipo == null) {
			tipoNormalizado = "TODOS";
		} else {
			tipoLimpio = tipo.trim();

			if (tipoLimpio.isEmpty()) {
				tipoNormalizado = "TODOS";
			} else {
				tipoNormalizado = tipoLimpio.toUpperCase();
			}
		}

		for (int ind = 0; ind < this.inventario.size(); ind++) {
			material = this.inventario.get(ind);
			tipoMaterial = material.obtenerTipoMaterial();

			if (tipoNormalizado.equals("TODOS")) {
				coincideTipo = true;
			} else if (tipoMaterial.equalsIgnoreCase(tipoNormalizado)) {
				coincideTipo = true;
			} else {
				coincideTipo = false;
			}

			if (coincideTipo && material.coincideConFiltro(filtro)) {
				resultados.add(material);
			}
		}

		return resultados;
	}

	public synchronized List<Prestamo> prestarMateriales(Usuario usuario, List<String> idsMateriales) throws MaterialNoDisponibleException, UsuarioConMultaException {

		Usuario usuarioReal;
		List<MaterialBibliografico> materialesConfirmados;
		List<Prestamo> nuevosPrestamos;
		MaterialBibliografico material;
		Prestamo nuevoPrestamo;
		String idActual, idLimpio, idComparar, idCompararLimpio;
		int prestamosActivos, limitePrestamos;

		if (usuario == null) {
			throw new IllegalArgumentException("Debe iniciar sesion para realizar un prestamo.");
		}
		if (idsMateriales == null || idsMateriales.isEmpty()) {
			throw new IllegalArgumentException("El carrito esta vacio.");
		}

		materialesConfirmados = new ArrayList<>();
		nuevosPrestamos = new ArrayList<>();

		usuarioReal = buscarUsuarioPorId(usuario.getId());
		if (usuarioReal == null) {
			throw new IllegalArgumentException("El usuario no existe en el sistema.");
		}

		if (usuarioReal.tieneMultaPendiente()) {
			throw new UsuarioConMultaException("El usuario tiene una multa pendiente de $" + usuarioReal.getMulta());
		}

		prestamosActivos = contarPrestamosActivos(usuarioReal.getId());
		limitePrestamos = usuarioReal.obtenerLimitePrestamos();

		if (prestamosActivos + idsMateriales.size() > limitePrestamos) {
			throw new IllegalArgumentException("Excede el limite maximo de prestamos activos (" + limitePrestamos + ").");
		}

		for (int ind = 0; ind < idsMateriales.size(); ind++) {
			idActual = idsMateriales.get(ind);
			if (idActual == null || idActual.trim().isEmpty()) {
				throw new IllegalArgumentException("ID de material vacio.");
			}

			idLimpio = idActual.trim();

			for (int ind_2 = ind + 1; ind_2 < idsMateriales.size(); ind_2++) {
				idComparar = idsMateriales.get(ind_2);

				if (idComparar != null) {
					idCompararLimpio = idComparar.trim();

					if (idLimpio.equals(idCompararLimpio)) {
						throw new IllegalArgumentException("El carrito tiene materiales duplicados.");
					}
				}
			}

			material = buscarMaterialPorId(idLimpio);

			if (material == null) {
				throw new MaterialNoDisponibleException("El material no existe en el catalogo.");
			}

			if (!material.estaDisponible()) {
				throw new MaterialNoDisponibleException("No hay ejemplares disponibles de: " + material.getTitulo());
			}

			materialesConfirmados.add(material);
		}

		for (int ind = 0; ind < materialesConfirmados.size(); ind++) {
			material = materialesConfirmados.get(ind);
			material.prestarEjemplar();
			nuevoPrestamo = new Prestamo(usuarioReal, material);
			this.prestamos.add(nuevoPrestamo);
			nuevosPrestamos.add(nuevoPrestamo);
		}

		guardarInventarioEnArchivo();
		guardarPrestamosEnArchivo();

		return nuevosPrestamos;
	}

	public synchronized double devolverMaterial(Usuario solicitante, String idPrestamo, int diasSimulados) {
		Prestamo prestamo;
		Usuario duenoPrestamo;
		LocalDate fechaActual, fechaDevolucion;
		String idLimpio, idDueno, idSolicitante;
		double mora;

		if (solicitante == null) {
			throw new IllegalArgumentException("Debe iniciar sesion para devolver un prestamo.");
		}
		if (idPrestamo == null || idPrestamo.trim().isEmpty()) {
			throw new IllegalArgumentException("El ID del prestamo no puede estar vacio.");
		}
		if (diasSimulados < 0) {
			throw new IllegalArgumentException("Los dias simulados no pueden ser negativos.");
		}

		idLimpio = idPrestamo.trim();
		prestamo = buscarPrestamoPorId(idLimpio);
		if (prestamo == null) {
			throw new IllegalArgumentException("Prestamo no encontrado.");
		}

		duenoPrestamo = prestamo.getUsuario();
		idDueno = duenoPrestamo.getId();
		idSolicitante = solicitante.getId();

		if (!idDueno.equals(idSolicitante)) {
			throw new IllegalArgumentException("No tiene permiso para devolver un prestamo de otro usuario.");
		}

		fechaActual = LocalDate.now();
		fechaDevolucion = fechaActual.plusDays(diasSimulados);
		mora = prestamo.registrarDevolucion(fechaDevolucion);

		guardarUsuariosEnArchivo();
		guardarInventarioEnArchivo();
		guardarPrestamosEnArchivo();

		return mora;
	}

	public synchronized void agregarMaterial(MaterialBibliografico material) {
		if (material == null) {
			throw new IllegalArgumentException("El material no puede ser nulo.");
		}

		this.inventario.add(material);
		guardarInventarioEnArchivo();
	}

	public synchronized MaterialBibliografico actualizarStock(String idMaterial, int cantidadSumar) {
		MaterialBibliografico material;
		String idLimpio;

		if (idMaterial == null || idMaterial.trim().isEmpty()) {
			throw new IllegalArgumentException("El ID del material no puede estar vacio.");
		}

		idLimpio = idMaterial.trim();
		material = buscarMaterialPorId(idLimpio);
		if (material == null) {
			throw new IllegalArgumentException("Material no encontrado.");
		}

		material.agregarEjemplares(cantidadSumar);
		guardarInventarioEnArchivo();

		return material;
	}

	public synchronized double pagarMultaUsuario(String idUsuario, double monto) {
		Usuario usuario;
		String idLimpio;
		double saldoRestante;

		if (idUsuario == null || idUsuario.trim().isEmpty()) {
			throw new IllegalArgumentException("El ID del usuario no puede estar vacio.");
		}

		idLimpio = idUsuario.trim();
		usuario = buscarUsuarioPorId(idLimpio);
		if (usuario == null) {
			throw new IllegalArgumentException("Usuario no encontrado.");
		}

		usuario.pagarMulta(monto);
		saldoRestante = usuario.getMulta();
		guardarUsuariosEnArchivo();

		return saldoRestante;
	}

	public synchronized List<Prestamo> obtenerPrestamosPorUsuario(String idUsuario) {
		List<Prestamo> resultado;
		Prestamo prestamo;
		Usuario usuarioPrestamo;
		String idLimpio, idUsuarioPrestamo;

		resultado = new ArrayList<>();
		if (idUsuario == null || idUsuario.trim().isEmpty()) {
			return resultado;
		}

		idLimpio = idUsuario.trim();

		for (int ind = 0; ind < this.prestamos.size(); ind++) {
			prestamo = this.prestamos.get(ind);
			usuarioPrestamo = prestamo.getUsuario();
			idUsuarioPrestamo = usuarioPrestamo.getId();

			if (idUsuarioPrestamo.equals(idLimpio)) {
				resultado.add(prestamo);
			}
		}

		return resultado;
	}

	public synchronized List<Prestamo> obtenerTodosLosPrestamos() {
		return this.prestamos;
	}

	public synchronized List<Usuario> obtenerTodosLosUsuarios() {
		return this.usuarios;
	}

	public synchronized Usuario obtenerUsuarioPorId(String idUsuario) {
		if (idUsuario == null || idUsuario.trim().isEmpty()) {
			return null;
		}

		return buscarUsuarioPorId(idUsuario.trim());
	}

	private Usuario buscarUsuarioPorId(String idUsuario) {
		Usuario actual;
		String idActual;

		for (int ind = 0; ind < this.usuarios.size(); ind++) {
			actual = this.usuarios.get(ind);
			idActual = actual.getId();
			if (idActual.equals(idUsuario)) {
				return actual;
			}
		}
		return null;
	}

	private MaterialBibliografico buscarMaterialPorId(String idMaterial) {
		MaterialBibliografico actual;
		String idActual;

		for (int ind = 0; ind < this.inventario.size(); ind++) {
			actual = this.inventario.get(ind);
			idActual = actual.getId();

			if (idActual.equals(idMaterial)) {
				return actual;
			}
		}
		return null;
	}

	private Prestamo buscarPrestamoPorId(String idPrestamo) {
		Prestamo actual;
		String idActual;

		for (int ind = 0; ind < this.prestamos.size(); ind++) {
			actual = this.prestamos.get(ind);
			idActual = actual.getId();

			if (idActual.equals(idPrestamo)) {
				return actual;
			}
		}
		return null;
	}

	private int contarPrestamosActivos(String idUsuario) {
		int contador = 0;
		Prestamo actual;
		Usuario usuarioPrestamo;
		String idUsuarioPrestamo;
		EstadoPrestamo estadoActual;

		for (int ind = 0; ind < this.prestamos.size(); ind++) {
			actual = this.prestamos.get(ind);
			usuarioPrestamo = actual.getUsuario();
			idUsuarioPrestamo = usuarioPrestamo.getId();
			estadoActual = actual.getEstado();

			if (idUsuarioPrestamo.equals(idUsuario) && estadoActual == EstadoPrestamo.ACTIVO) {
				contador++;
			}
		}
		return contador;
	}

	private void cargarUsuariosDesdeArchivo() {
		String[] partes;
		String linea, lineaLimpia, rolTexto, rol, id, nombre, username, password, multaTexto, datoExtra;
		double multa;

		this.usuarios.clear();

		try {
			if (!Files.exists(this.rutaUsuarios) || Files.size(this.rutaUsuarios) == 0) {
				crearUsuariosIniciales();
				guardarUsuariosEnArchivo();
				return;
			}

			try (BufferedReader br = Files.newBufferedReader(this.rutaUsuarios, StandardCharsets.UTF_8)) {
				while ((linea = br.readLine()) != null) {
					lineaLimpia = linea.trim();
					if (lineaLimpia.isEmpty()) {
						continue;
					}
					try {
						partes = linea.split(";");
						if (partes.length != 7) {
							continue;
						}

						rolTexto = partes[0].trim();
						rol = rolTexto.toUpperCase();
						id = partes[1].trim();
						nombre = partes[2].trim();
						username = partes[3].trim();
						password = partes[4].trim();
						multaTexto = partes[5].trim();
						multa = Double.parseDouble(multaTexto);
						datoExtra = partes[6].trim();

						if (rol.equals("ESTUDIANTE")) {
							this.usuarios.add(new Estudiante(id, nombre, username, password, multa, datoExtra));
						} else if (rol.equals("PROFESOR")) {
							this.usuarios.add(new Profesor(id, nombre, username, password, multa, datoExtra));
						} else if (rol.equals("ADMINISTRADOR")) {
							this.usuarios.add(new Administrador(id, nombre, username, password, multa, datoExtra));
						}
					} catch (Exception e) {
						System.out.println("Error al leer: " + e.getMessage());
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error al abrir usuarios.txt: " + e.getMessage());
		}

		if (this.usuarios.isEmpty()) {
			crearUsuariosIniciales();
			guardarUsuariosEnArchivo();
		}
	}

	private void cargarInventarioDesdeArchivo() {
		String[] partes;
		String linea, lineaLimpia, tipoTexto, tipo, id, titulo, autor, totalesTexto, disponiblesTexto, datoExtra;
		int totales, disponibles, edicion;

		this.inventario.clear();

		try {
			if (!Files.exists(this.rutaInventario) || Files.size(this.rutaInventario) == 0) {
				crearInventarioInicial();
				guardarInventarioEnArchivo();
				return;
			}

			try (BufferedReader br = Files.newBufferedReader(this.rutaInventario, StandardCharsets.UTF_8)) {
				while ((linea = br.readLine()) != null) {
					lineaLimpia = linea.trim();
					if (lineaLimpia.isEmpty()) {
						continue;
					}
					try {
						partes = linea.split(";");
						if (partes.length != 7) {
							continue;
						}

						tipoTexto = partes[0].trim();
						tipo = tipoTexto.toUpperCase();
						id = partes[1].trim();
						titulo = partes[2].trim();
						autor = partes[3].trim();
						totalesTexto = partes[4].trim();
						disponiblesTexto = partes[5].trim();
						totales = Integer.parseInt(totalesTexto);
						disponibles = Integer.parseInt(disponiblesTexto);
						datoExtra = partes[6].trim();

						if (tipo.equals("LIBRO")) {
							this.inventario.add(new Libro(id, titulo, autor, totales, disponibles, datoExtra));
						} else if (tipo.equals("TESIS")) {
							this.inventario.add(new Tesis(id, titulo, autor, totales, disponibles, datoExtra));
						} else if (tipo.equals("REVISTA")) {
							edicion = Integer.parseInt(datoExtra);
							this.inventario.add(new Revista(id, titulo, autor, totales, disponibles, edicion));
						}
					} catch (Exception e) {
						System.out.println("Error al leer material: " + e.getMessage());
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error al abrir inventario.txt: " + e.getMessage());
		}

		if (this.inventario.isEmpty()) {
			crearInventarioInicial();
			guardarInventarioEnArchivo();
		}
	}

	private void cargarPrestamosDesdeArchivo() {
		String[] partes;
		String linea, lineaLimpia, idPrestamo, idUsuario, idMaterial, textoPrestamo, textoLimite, textoDevolucion, textoMora, textoEstado, estadoMayuscula;
		LocalDate fechaPrestamo, fechaLimite, fechaDevolucion;
		double moraGenerada;
		EstadoPrestamo estado;
		Usuario usuario;
		MaterialBibliografico material;

		this.prestamos.clear();

		try {
			if (!Files.exists(this.rutaPrestamos) || Files.size(this.rutaPrestamos) == 0) {
				return;
			}

			try (BufferedReader br = Files.newBufferedReader(this.rutaPrestamos, StandardCharsets.UTF_8)) {
				while ((linea = br.readLine()) != null) {
					lineaLimpia = linea.trim();
					if (lineaLimpia.isEmpty()) {
						continue;
					}
					try {
						partes = linea.split(";");
						if (partes.length != 8) {
							continue;
						}

						idPrestamo = partes[0].trim();
						idUsuario = partes[1].trim();
						idMaterial = partes[2].trim();
						textoPrestamo = partes[3].trim();
						textoLimite = partes[4].trim();
						textoDevolucion = partes[5].trim();
						textoMora = partes[6].trim();
						textoEstado = partes[7].trim();

						fechaPrestamo = LocalDate.parse(textoPrestamo);
						fechaLimite = LocalDate.parse(textoLimite);

						if (textoDevolucion.equals("NULL")) {
							fechaDevolucion = null;
						} else {
							fechaDevolucion = LocalDate.parse(textoDevolucion);
						}

						moraGenerada = Double.parseDouble(textoMora);
						estadoMayuscula = textoEstado.toUpperCase();
						estado = EstadoPrestamo.valueOf(estadoMayuscula);

						usuario = buscarUsuarioPorId(idUsuario);
						material = buscarMaterialPorId(idMaterial);

						if (usuario != null && material != null) {
							this.prestamos.add(new Prestamo(idPrestamo, usuario, material, fechaPrestamo,
									fechaLimite, fechaDevolucion, moraGenerada, estado));
						}
					} catch (Exception e) {
						System.out.println("Error al leer: " + e.getMessage());
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error al abrir prestamos.txt: " + e.getMessage());
		}
	}

	private void guardarUsuariosEnArchivo() {
		Usuario usuario;
		String multaTexto, linea;

		try (BufferedWriter bw = Files.newBufferedWriter(this.rutaUsuarios, StandardCharsets.UTF_8)) {
			for (int ind = 0; ind < this.usuarios.size(); ind++) {
				usuario = this.usuarios.get(ind);
				multaTexto = String.valueOf(usuario.getMulta());
				linea = usuario.obtenerRol() + ";" + usuario.getId() + ";" + usuario.getNombre() + ";" + usuario.getUsername() + ";" + usuario.getPassword() + ";" + multaTexto + ";" + usuario.obtenerDatoExtra();
				bw.write(linea);
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error al guardar usuarios.txt: " + e.getMessage());
		}
	}

	private void guardarInventarioEnArchivo() {
		MaterialBibliografico material;
		Libro libro;
		Tesis tesis;
		Revista revista;
		String datoExtra, totalesTexto, disponiblesTexto, linea;

		try (BufferedWriter bw = Files.newBufferedWriter(this.rutaInventario, StandardCharsets.UTF_8)) {
			for (int ind = 0; ind < this.inventario.size(); ind++) {
				material = this.inventario.get(ind);
				datoExtra = "";

				if (material instanceof Libro) {
					libro = (Libro) material;
					datoExtra = libro.getEditorial();
				} else if (material instanceof Tesis) {
					tesis = (Tesis) material;
					datoExtra = tesis.getFacultad();
				} else if (material instanceof Revista) {
					revista = (Revista) material;
					datoExtra = String.valueOf(revista.getNumeroEdicion());
				}

				totalesTexto = String.valueOf(material.getEjemplaresTotales());
				disponiblesTexto = String.valueOf(material.getEjemplaresDisponibles());

				linea = material.obtenerTipoMaterial() + ";" + material.getId() + ";" + material.getTitulo() + ";" + material.getAutor() + ";" + totalesTexto + ";" + disponiblesTexto + ";" + datoExtra;
				bw.write(linea);
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error al guardar inventario.txt: " + e.getMessage());
		}
	}

	private void guardarPrestamosEnArchivo() {
		Prestamo prestamo;
		Usuario usuario;
		MaterialBibliografico material;
		LocalDate fechaPrestamo, fechaLimite, fechaDevolucion;
		EstadoPrestamo estadoActual;
		String textoDevolucion, moraTexto, linea;

		try (BufferedWriter bw = Files.newBufferedWriter(this.rutaPrestamos, StandardCharsets.UTF_8)) {
			for (int ind = 0; ind < this.prestamos.size(); ind++) {
				prestamo = this.prestamos.get(ind);
				usuario = prestamo.getUsuario();
				material = prestamo.getMaterial();
				fechaPrestamo = prestamo.getFechaPrestamo();
				fechaLimite = prestamo.getFechaLimite();
				fechaDevolucion = prestamo.getFechaDevolucion();
				estadoActual = prestamo.getEstado();

				if (fechaDevolucion == null) {
					textoDevolucion = "NULL";
				} else {
					textoDevolucion = fechaDevolucion.toString();
				}

				moraTexto = String.valueOf(prestamo.getMoraGenerada());
				linea = prestamo.getId() + ";" + usuario.getId() + ";" + material.getId() + ";" + fechaPrestamo.toString() + ";" + fechaLimite.toString() + ";" + textoDevolucion + ";" + moraTexto + ";" + estadoActual.name();
				bw.write(linea);
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error al guardar prestamos.txt: " + e.getMessage());
		}
	}

	private void crearUsuariosIniciales() {
		this.usuarios.add(new Estudiante("Juan Perez", "estudiante1", "1234", "Ingenieria en Sistemas"));
		this.usuarios.add(new Estudiante("Maria Lopez", "estudiante2", "1234", "Arquitectura"));
		this.usuarios.add(new Estudiante("EST-MULTA-01", "Carlos Ruiz", "estudiante3", "1234", 150.0, "Derecho"));
		this.usuarios.add(new Profesor("Ana Gomez", "profesor1", "1234", "Ciencias de la Computacion"));
		this.usuarios.add(new Administrador("Admin Biblioteca", "admin", "admin123", "EMP-001"));
	}

	private void crearInventarioInicial() {
		this.inventario.add(new Libro("Programacion Orientada a Objetos en Java", "Herbert Schildt", 3, "McGraw-Hill"));
		this.inventario.add(new Libro("Estructuras de Datos y Algoritmos", "Robert Lafore", 1, "Pearson"));
		this.inventario.add(new Libro("Ingenieria de Software", "Ian Sommerville", 2, "Addison-Wesley"));
		this.inventario.add(new Tesis("Optimizacion de Redes Distribuidas", "Luis Martinez", 2, "Ingenieria"));
		this.inventario.add(new Tesis("Arquitectura Sostenible en el Caribe", "Sofia Herrera", 1, "Arquitectura"));
		this.inventario.add(new Revista("IEEE Software", "IEEE Computer Society", 4, 42));
		this.inventario.add(new Revista("National Geographic Ciencia", "NatGeo", 2, 118));
	}
}