package servidor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import clases.Libro;
import clases.MaterialBibliografico;
import clases.Prestamo;
import clases.Revista;
import clases.Tesis;
import clases.Usuario;
import excepciones.MaterialNoDisponibleException;
import excepciones.UsuarioConMultaException;
import logica.GestorBiblioteca;

// PROTOCOLO DE COMUNICACION

// PETICION (CLIENTE -> SERVIDOR) => ACCION|arg1|arg2|...|argN
// RESPUESTA (SERVIDOR -> CLIENTE) => ESTADO|arg1|arg2|...|argN
// AUTH_LOGIN|username|password => OK|id|nombre|rol|multa|limitePrestamos
// AUTH_LOGOUT => OK|desconectado
// CATALOGO_LISTAR => OK|COLECCION|CATALOGO|cantidad  (+ List<MaterialBibliografico>)
// CATALOGO_BUSCAR|tipo|filtro => OK|COLECCION|CATALOGO|cantidad  (+ List<MaterialBibliografico>)
// PRESTAMO_CONFIRMAR|idMat1,idMat2,... => OK|prestamos_realizados|cantidad
// PRESTAMO_MIS_PRESTAMOS => OK|COLECCION|PRESTAMOS|cantidad  (+ List<Prestamo>)
// PRESTAMO_DEVOLVER|idPrestamo|diasSimulados => OK|devuelto|mora|multaActual
// ADMIN_AGREGAR_MATERIAL|tipo|titulo|autor|cantidad|datoExtra => OK|material_agregado|titulo
// ADMIN_ACTUALIZAR_STOCK|idMaterial|cantidadSumar => OK|stock_actualizado|disponibles|totales
// ADMIN_LISTAR_PRESTAMOS => OK|COLECCION|PRESTAMOS_ADMIN|cantidad  (+ List<Prestamo> serializada)
// ADMIN_LISTAR_USUARIOS => OK|COLECCION|USUARIOS_ADMIN|cantidad  (+ List<Usuario> serializada)
// ADMIN_PAGAR_MULTA|idUsuario|monto => OK|multa_actualizada|saldoRestante
// CREDENCIALES_INVALIDAS => ERROR|credenciales_invalidas
// SESION_NO_ACTIVA => ERROR|sesion_no_activa
// ACCESO_DENEGADO => ERROR|acceso_denegado
// PETICION_INVALIDA => ERROR|peticion_invalida
// USUARIO_CON_MULTA => ERROR|usuario_con_multa|mensaje_error
// MATERIAL_NO_DISPONIBLE => ERROR|material_no_disponible|mensaje_error
// ERROR_VALIDACION => ERROR|mensaje_error

public class Servidor {

	private static final int MAX_HILOS = 20;
	private static final int TIEMPO_ESPERA_MS = 300000;

	private int puerto;
	private GestorBiblioteca gestor;
	private ServidorObserver observer;
	private ExecutorService pool;
	private List<Socket> clientesConectados;
	private ServerSocket serverSocket;
	private boolean activo;

	public Servidor(int puerto, ServidorObserver observer) {
		this.puerto = puerto;
		this.observer = observer;
		this.gestor = new GestorBiblioteca();
		this.pool = Executors.newFixedThreadPool(MAX_HILOS);
		this.clientesConectados = new CopyOnWriteArrayList<>();
		this.activo = false;
	}

	public void iniciarServidor() {
		
		this.activo = true;

		this.pool.execute(new Runnable() {
			
			@Override
			public void run() {
				
				Socket socketCliente;

				try (ServerSocket servidor = new ServerSocket(puerto)) {
					serverSocket = servidor;
					notificar("Servidor de Biblioteca iniciado en el puerto " + puerto);

					while (activo) {
						socketCliente = servidor.accept();
						socketCliente.setSoTimeout(TIEMPO_ESPERA_MS);
						clientesConectados.add(socketCliente);
						notificar("Cliente conectado desde: " + socketCliente.getInetAddress());

						pool.execute(new ManejadorCliente(socketCliente));
					}
				} catch (IOException e) {
					if (activo) {
						notificar("Error en el servidor: " + e.getMessage());
					}
				}
			}
		});
	}

	public void detenerServidor() {
		
		Socket cliente;

		this.activo = false;

		try {
			if (this.serverSocket != null && !this.serverSocket.isClosed()) {
				this.serverSocket.close();
			}
		} catch (IOException e) {
			notificar("Error al cerrar el servidor: " + e.getMessage());
		}

		for (int ind = 0; ind < this.clientesConectados.size(); ind++) {
			cliente = this.clientesConectados.get(ind);
			try {
				if (!cliente.isClosed()) {
					cliente.close();
				}
			} catch (IOException e) {
				notificar("Error al cerrar conexion de cliente: " + e.getMessage());
			}
		}

		this.clientesConectados.clear();
		this.pool.shutdownNow();
		notificar("Servidor detenido.");
	}

	private void notificar(String mensaje) {
		
		if (this.observer != null) {
			this.observer.notificarEvento(mensaje);
		}
	}

	private String autenticarUsuario(String username, String password, ManejadorCliente sesion) {
	
		Usuario usuario;

		usuario = this.gestor.autenticarUsuario(username, password);
		if (usuario == null) {
			notificar("Inicio de sesion fallido para: " + username);
			return "ERROR|credenciales_invalidas";
		}

		sesion.setUsuarioActual(usuario);
		notificar("Usuario autenticado: " + usuario.getUsername() + " (" + usuario.obtenerRol() + ")");

		return "OK|" + usuario.getId() + "|" + usuario.getNombre() + "|" + usuario.obtenerRol() + "|" + usuario.getMulta() + "|" + usuario.obtenerLimitePrestamos();
	}

	private String procesarPrestamo(Usuario usuario, String idsTexto) {
		String[] arregloIds;
		String idLimpio;
		List<String> listaIds;
		List<Prestamo> nuevosPrestamos;
		

		if (idsTexto == null || idsTexto.trim().isEmpty()) {
			return "ERROR|El carrito esta vacio.";
		}

		listaIds = new ArrayList<>();
		arregloIds = idsTexto.split(",");

		for (int ind = 0; ind < arregloIds.length; ind++) {
			idLimpio = arregloIds[ind].trim();
			if (!idLimpio.isEmpty()) {
				listaIds.add(idLimpio);
			}
		}

		if (listaIds.isEmpty()) {
			return "ERROR|El carrito esta vacio.";
		}

		try {
			nuevosPrestamos = this.gestor.prestarMateriales(usuario, listaIds);
			notificar("Prestamo confirmado para " + usuario.getUsername() + " (" + nuevosPrestamos.size() + " materiales)");
			return "OK|prestamos_realizados|" + nuevosPrestamos.size();
		} catch (UsuarioConMultaException e) {
			return "ERROR|usuario_con_multa|" + e.getMessage();
		} catch (MaterialNoDisponibleException e) {
			return "ERROR|material_no_disponible|" + e.getMessage();
		} catch (IllegalArgumentException e) {
			return "ERROR|" + e.getMessage();
		}
	}

	private String procesarDevolucion(Usuario usuario, String idPrestamo, String diasTexto) {
		int diasSimulados;
		double mora, multaActual;
		Usuario usuarioActualizado;

		try {
			diasSimulados = Integer.parseInt(diasTexto.trim());
		} catch (NumberFormatException e) {
			return "ERROR|Los dias simulados deben ser un numero entero valido.";
		}

		try {
			mora = this.gestor.devolverMaterial(usuario, idPrestamo, diasSimulados);
			usuarioActualizado = this.gestor.obtenerUsuarioPorId(usuario.getId());

			if (usuarioActualizado != null) {
				multaActual = usuarioActualizado.getMulta();
			} else {
				multaActual = 0.0;
			}

			notificar("Devolucion realizada por " + usuario.getUsername() + ". Mora: $" + mora);
			return "OK|devuelto|" + mora + "|" + multaActual;
		} catch (IllegalArgumentException e) {
			return "ERROR|" + e.getMessage();
		} catch (IllegalStateException e) {
			return "ERROR|" + e.getMessage();
		}
	}

	private String agregarMaterialAdmin(String tipo, String titulo, String autor, String cantidadTexto, String datoExtra) {
		String tipoNormalizado;
		int cantidad, numeroEdicion;
		MaterialBibliografico nuevoMaterial;

		try {
			cantidad = Integer.parseInt(cantidadTexto.trim());
		} catch (NumberFormatException e) {
			return "ERROR|La cantidad debe ser un numero entero valido.";
		}

		tipoNormalizado = tipo.trim().toUpperCase();

		try {
			if (tipoNormalizado.equals("LIBRO")) {
				nuevoMaterial = new Libro(titulo, autor, cantidad, datoExtra);
			} else if (tipoNormalizado.equals("TESIS")) {
				nuevoMaterial = new Tesis(titulo, autor, cantidad, datoExtra);
			} else if (tipoNormalizado.equals("REVISTA")) {
				try {
					numeroEdicion = Integer.parseInt(datoExtra.trim());
				} catch (NumberFormatException e) {
					return "ERROR|El numero de edicion debe ser un numero entero valido.";
				}
				nuevoMaterial = new Revista(titulo, autor, cantidad, numeroEdicion);
			} else {
				return "ERROR|Tipo de material no valido.";
			}

			this.gestor.agregarMaterial(nuevoMaterial);
			notificar("Admin agrego material: " + nuevoMaterial.getTitulo());
			return "OK|material_agregado|" + nuevoMaterial.getTitulo();
		} catch (IllegalArgumentException e) {
			return "ERROR|" + e.getMessage();
		}
	}

	private String actualizarStockAdmin(String idMaterial, String cantidadTexto) {
		int cantidadSumar;
		MaterialBibliografico material;

		try {
			cantidadSumar = Integer.parseInt(cantidadTexto.trim());
		} catch (NumberFormatException e) {
			return "ERROR|La cantidad debe ser un numero entero valido.";
		}

		try {
			material = this.gestor.actualizarStock(idMaterial, cantidadSumar);
			notificar("Admin actualizo stock de: " + material.getTitulo());
			return "OK|stock_actualizado|" + material.getEjemplaresDisponibles() + "|" + material.getEjemplaresTotales();
		} catch (IllegalArgumentException e) {
			return "ERROR|" + e.getMessage();
		}
	}

	private String pagarMultaAdmin(String idUsuario, String montoTexto) {
		double monto, saldoRestante;

		try {
			monto = Double.parseDouble(montoTexto.trim());
		} catch (NumberFormatException e) {
			return "ERROR|El monto debe ser un numero valido.";
		}

		try {
			saldoRestante = this.gestor.pagarMultaUsuario(idUsuario, monto);
			notificar("Admin registro pago de multa para usuario ID: " + idUsuario);
			return "OK|multa_actualizada|" + saldoRestante;
		} catch (IllegalArgumentException e) {
			return "ERROR|" + e.getMessage();
		}
	}

	private boolean esAdministrador(Usuario usuario) {
		if (usuario == null) {
			return false;
		}
		if (usuario.obtenerRol().equals("ADMINISTRADOR")) {
			return true;
		} else {
			return false;
		}
	}

	private class ManejadorCliente implements Runnable {

		private Socket socket;
		private Usuario usuarioActual;

		public ManejadorCliente(Socket socket) {
			this.socket = socket;
			this.usuarioActual = null;
		}

		public void setUsuarioActual(Usuario usuarioActual) {
			this.usuarioActual = usuarioActual;
		}

		@Override
		public void run() {
			
			Object objetoRecibido;
			String peticion;

			try (Socket soc = this.socket;
					ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
					ObjectInputStream in = new ObjectInputStream(soc.getInputStream())) {

				out.flush();

				while (activo) {
					objetoRecibido = in.readObject();

					if (!(objetoRecibido instanceof String)) {
						enviarTexto(out, "ERROR|peticion_invalida");
						continue;
					}

					peticion = (String) objetoRecibido;
					procesarPeticion(peticion, out);
				}

			} catch (EOFException e) {
				notificar("Cliente desconectado: " + this.socket.getInetAddress());
			} catch (SocketTimeoutException e) {
				notificar("Tiempo de espera agotado para cliente: " + this.socket.getInetAddress());
			} catch (SocketException e) {
				notificar("Conexion cerrada con cliente: " + this.socket.getInetAddress());
			} catch (Exception e) {
				notificar("Error con cliente " + this.socket.getInetAddress() + ": " + e.getMessage());
			} finally {
				clientesConectados.remove(this.socket);
			}
		}

		private void enviarTexto(ObjectOutputStream out, String respuesta) throws IOException {
			out.writeObject(respuesta);
			out.flush();
		}

		private void enviarColeccion(ObjectOutputStream out, String encabezado, Object lista) throws IOException {
			out.writeObject(encabezado);
			out.reset();
			out.writeObject(lista);
			out.flush();
		}

		private void procesarPeticion(String peticion, ObjectOutputStream out) throws IOException {
			String peticionLimpia, accion, respuesta, tipo, filtro;
			String[] partes;
			List<MaterialBibliografico> catalogo;
			List<Prestamo> listaPrestamos;
			List<Usuario> listaUsuarios;

			if (peticion == null || peticion.trim().isEmpty()) {
				enviarTexto(out, "ERROR|peticion_invalida");
				return;
			}

			peticionLimpia = peticion.trim();
			partes = peticionLimpia.split("\\|");

			if (partes.length == 0) {
				enviarTexto(out, "ERROR|peticion_invalida");
				return;
			}

			accion = partes[0].trim().toUpperCase();

			if (accion.equals("AUTH_LOGIN")) {
				if (partes.length != 3) {
					enviarTexto(out, "ERROR|peticion_invalida");
					return;
				}
				respuesta = autenticarUsuario(partes[1], partes[2], this);
				enviarTexto(out, respuesta);
				return;
			}

			if (this.usuarioActual == null) {
				enviarTexto(out, "ERROR|sesion_no_activa");
				return;
			}

			if (accion.equals("AUTH_LOGOUT")) {
				notificar("Sesion cerrada: " + this.usuarioActual.getUsername());
				this.usuarioActual = null;
				enviarTexto(out, "OK|desconectado");
			} else if (accion.equals("CATALOGO_LISTAR")) {
				catalogo = gestor.buscarCatalogo("", "TODOS");
				enviarColeccion(out, "OK|COLECCION|CATALOGO|" + catalogo.size(), catalogo);
			} else if (accion.equals("CATALOGO_BUSCAR")) {
				
				if (partes.length < 2 || partes.length > 3) {
					enviarTexto(out, "ERROR|peticion_invalida");
					return;
				}
				
				tipo = partes[1];
				if (partes.length == 3) {
					filtro = partes[2];
				} else {
					filtro = "";
				}
				
				catalogo = gestor.buscarCatalogo(filtro, tipo);
				enviarColeccion(out, "OK|COLECCION|CATALOGO|" + catalogo.size(), catalogo);
			} else if (accion.equals("PRESTAMO_CONFIRMAR")) {
				if (partes.length != 2) {
					enviarTexto(out, "ERROR|peticion_invalida");
					return;
				}
				
				respuesta = procesarPrestamo(this.usuarioActual, partes[1]);
				enviarTexto(out, respuesta);
			} else if (accion.equals("PRESTAMO_MIS_PRESTAMOS")) {
				listaPrestamos = gestor.obtenerPrestamosPorUsuario(this.usuarioActual.getId());
				enviarColeccion(out, "OK|COLECCION|PRESTAMOS|" + listaPrestamos.size(), listaPrestamos);
			} else if (accion.equals("PRESTAMO_DEVOLVER")) {
				if (partes.length != 3) {
					enviarTexto(out, "ERROR|peticion_invalida");
					return;
				}
				
				respuesta = procesarDevolucion(this.usuarioActual, partes[1], partes[2]);
				enviarTexto(out, respuesta);
			} else if (accion.equals("ADMIN_AGREGAR_MATERIAL")) {
				if (!esAdministrador(this.usuarioActual)) {
					enviarTexto(out, "ERROR|acceso_denegado");
					return;
				}
				
				if (partes.length != 6) {
					enviarTexto(out, "ERROR|peticion_invalida");
					return;
				}
				
				respuesta = agregarMaterialAdmin(partes[1], partes[2], partes[3], partes[4], partes[5]);
				enviarTexto(out, respuesta);
			} else if (accion.equals("ADMIN_ACTUALIZAR_STOCK")) {
				if (!esAdministrador(this.usuarioActual)) {
					enviarTexto(out, "ERROR|acceso_denegado");
					return;
				}
				
				if (partes.length != 3) {
					enviarTexto(out, "ERROR|peticion_invalida");
					return;
				}
				
				respuesta = actualizarStockAdmin(partes[1], partes[2]);
				enviarTexto(out, respuesta);
			} else if (accion.equals("ADMIN_LISTAR_PRESTAMOS")) {
				if (!esAdministrador(this.usuarioActual)) {
					enviarTexto(out, "ERROR|acceso_denegado");
					return;
				}
				
				listaPrestamos = gestor.obtenerTodosLosPrestamos();
				enviarColeccion(out, "OK|COLECCION|PRESTAMOS_ADMIN|" + listaPrestamos.size(), listaPrestamos);
			} else if (accion.equals("ADMIN_LISTAR_USUARIOS")) {
				if (!esAdministrador(this.usuarioActual)) {
					enviarTexto(out, "ERROR|acceso_denegado");
					return;
				}
				
				listaUsuarios = gestor.obtenerTodosLosUsuarios();
				enviarColeccion(out, "OK|COLECCION|USUARIOS_ADMIN|" + listaUsuarios.size(), listaUsuarios);
			} else if (accion.equals("ADMIN_PAGAR_MULTA")) {
				if (!esAdministrador(this.usuarioActual)) {
					enviarTexto(out, "ERROR|acceso_denegado");
					return;
				}
				
				if (partes.length != 3) {
					enviarTexto(out, "ERROR|peticion_invalida");
					return;
				}
				
				respuesta = pagarMultaAdmin(partes[1], partes[2]);
				enviarTexto(out, respuesta);
			} else {
				enviarTexto(out, "ERROR|peticion_invalida");
			}
		}

	}
}