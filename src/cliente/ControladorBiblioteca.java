package cliente;

import java.util.ArrayList;
import java.util.List;
import clases.MaterialBibliografico;

public class ControladorBiblioteca {

	private ClienteObserver vista;
	private Cliente cliente;
	private List<MaterialBibliografico> carrito;

	public ControladorBiblioteca(ClienteObserver vista, Cliente cliente) {
		
		this.vista = vista;
		this.cliente = cliente;
		this.carrito = new ArrayList<>();
	}

	public void iniciarSesion(String ip, String puertoTexto, String username, String password) {
		
		String ipLimpia, userLimpio;
		int puerto;
		boolean conectado;

		if (ip == null || ip.trim().isEmpty() || puertoTexto == null || puertoTexto.trim().isEmpty()) {
			this.vista.mostrarError("Debe ingresar la IP y el puerto del servidor.");
			return;
		}
		
		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			this.vista.mostrarError("Debe completar el usuario y la contrasena.");
			return;
		}

		ipLimpia = ip.trim();
		userLimpio = username.trim();

		try {
		
			puerto = Integer.parseInt(puertoTexto.trim());
		} catch (NumberFormatException e) {
			this.vista.mostrarError("El puerto debe ser un numero entero valido.");
			return;
		}

		if (puerto < 1024 || puerto > 65535) {
			
			this.vista.mostrarError("El puerto debe estar entre 1024 y 65535.");
			return;
		}

		if (contieneCaracterProhibido(userLimpio) || contieneCaracterProhibido(password)) {
			
			this.vista.mostrarError("No se permiten los caracteres '|' ni ';' en los datos.");
			return;
		}

		if (!this.cliente.estaConectado()) {
			
			this.cliente.configurarConexion(ipLimpia, puerto);
			conectado = this.cliente.conectar();
			
			if (!conectado) {
				return;
			}
		}

		this.cliente.enviarPeticion("AUTH_LOGIN|" + userLimpio + "|" + password);
	}

	public void cerrarSesion() {
		
		vaciarCarrito();
		
		if (this.cliente.estaConectado()) {
			this.cliente.enviarPeticion("AUTH_LOGOUT");
		} else {
			this.vista.cerrarSesionExitosa();
		}
	}

	public void desconectar() {
		
		vaciarCarrito();
		this.cliente.desconectar();
	}

	public void cargarCatalogo() {
		
		this.cliente.enviarPeticion("CATALOGO_LISTAR");
	}

	public void buscarCatalogo(String tipo, String filtro) {
		
		String tipoLimpio, filtroLimpio;

		if (tipo == null || tipo.trim().isEmpty()) {
			tipoLimpio = "TODOS";
		} else {
			tipoLimpio = tipo.trim().toUpperCase();
		}

		if (filtro == null) {
			filtroLimpio = "";
		} else {
			filtroLimpio = filtro.trim();
		}

		if (filtroLimpio.contains("|")) {
			this.vista.mostrarError("No se permite el caracter '|' en el buscador.");
			return;
		}

		this.cliente.enviarPeticion("CATALOGO_BUSCAR|" + tipoLimpio + "|" + filtroLimpio);
	}

	public void agregarAlCarrito(MaterialBibliografico material) {
		
		MaterialBibliografico actual;
		String idNuevo, idActual;

		if (material == null) {
			this.vista.mostrarError("Debe seleccionar un material del catalogo.");
			return;
		}

		if (!material.estaDisponible()) {
			this.vista.mostrarError("No hay ejemplares disponibles de: " + material.getTitulo());
			return;
		}

		idNuevo = material.getId();
		
		for (int ind = 0; ind < this.carrito.size(); ind++) {
			actual = this.carrito.get(ind);
			idActual = actual.getId();
		
			if (idActual.equals(idNuevo)) {
				this.vista.mostrarError("Este material ya se encuentra en el carrito.");
				return;
			}
		}

		this.carrito.add(material);
		this.vista.actualizarCarrito(this.carrito);
	}

	public void quitarDelCarrito(String idMaterial) {
		
		MaterialBibliografico actual;
		String idLimpio, idActual;

		if (idMaterial == null || idMaterial.trim().isEmpty()) {
			this.vista.mostrarError("Debe seleccionar un material del carrito.");
			return;
		}

		idLimpio = idMaterial.trim();

		for (int ind = 0; ind < this.carrito.size(); ind++) {
			actual = this.carrito.get(ind);
			idActual = actual.getId();
			if (idActual.equals(idLimpio)) {
				this.carrito.remove(ind);
				this.vista.actualizarCarrito(this.carrito);
				return;
			}
		}
	}

	public void vaciarCarrito() {
		
		this.carrito.clear();
		this.vista.actualizarCarrito(this.carrito);
	}

	public List<MaterialBibliografico> getCarrito() {
		
		return this.carrito;
	}

	public void confirmarCarrito() {
		
		MaterialBibliografico material;
		String idsTexto;

		if (this.carrito.isEmpty()) {
			this.vista.mostrarError("El carrito de prestamos esta vacio.");
			return;
		}

		idsTexto = "";
		
		for (int ind = 0; ind < this.carrito.size(); ind++) {
			material = this.carrito.get(ind);
		
			if (ind > 0) {
				idsTexto = idsTexto + ",";
			}
			idsTexto = idsTexto + material.getId();
		}

		this.cliente.enviarPeticion("PRESTAMO_CONFIRMAR|" + idsTexto);
	}

	public void cargarMisPrestamos() {
		
		this.cliente.enviarPeticion("PRESTAMO_MIS_PRESTAMOS");
	}

	public void devolverPrestamo(String idPrestamo, String diasTexto) {
		
		int diasSimulados;

		if (idPrestamo == null || idPrestamo.trim().isEmpty()) {
			this.vista.mostrarError("Debe seleccionar un prestamo para devolver.");
			return;
		}

		if (diasTexto == null || diasTexto.trim().isEmpty()) {
			diasSimulados = 0;
		} else {
			try {
		
				diasSimulados = Integer.parseInt(diasTexto.trim());
			} catch (NumberFormatException e) {
				this.vista.mostrarError("Los dias simulados deben ser un numero entero valido.");
				return;
			}
		}

		if (diasSimulados < 0) {
			
			this.vista.mostrarError("Los dias simulados no pueden ser negativos.");
			return;
		}

		this.cliente.enviarPeticion("PRESTAMO_DEVOLVER|" + idPrestamo.trim() + "|" + diasSimulados);
	}

	public void agregarMaterialAdmin(String tipo, String titulo, String autor, String cantidadTexto, String datoExtra) {
		
		String tipoLimpio, tituloLimpio, autorLimpio, extraLimpio;
		int cantidad, numeroEdicion;

		if (tipo == null || tipo.trim().isEmpty() || titulo == null || titulo.trim().isEmpty() || autor == null || autor.trim().isEmpty() || cantidadTexto == null || cantidadTexto.trim().isEmpty() || datoExtra == null || datoExtra.trim().isEmpty()) {
			
			this.vista.mostrarError("Debe completar todos los campos del material.");
			return;
		}

		tipoLimpio = tipo.trim().toUpperCase();
		tituloLimpio = titulo.trim();
		autorLimpio = autor.trim();
		extraLimpio = datoExtra.trim();

		if (contieneCaracterProhibido(tituloLimpio) || contieneCaracterProhibido(autorLimpio) || contieneCaracterProhibido(extraLimpio)) {
			
			this.vista.mostrarError("No se permiten los caracteres '|' ni ';'.");
			return;
		}

		try {
			
			cantidad = Integer.parseInt(cantidadTexto.trim());
		} catch (NumberFormatException e) {
			this.vista.mostrarError("La cantidad debe ser un numero entero valido.");
			return;
		}

		if (cantidad <= 0) {
			
			this.vista.mostrarError("La cantidad de ejemplares debe ser mayor que cero.");
			return;
		}

		if (tipoLimpio.equals("REVISTA")) {
			
			try {
			
				numeroEdicion = Integer.parseInt(extraLimpio);
			} catch (NumberFormatException e) {
				this.vista.mostrarError("El numero de edicion de la revista debe ser un numero entero.");
				return;
			}
			
			if (numeroEdicion <= 0) {
				
				this.vista.mostrarError("El numero de edicion debe ser mayor que cero.");
				return;
			}
		}

		this.cliente.enviarPeticion("ADMIN_AGREGAR_MATERIAL|" + tipoLimpio + "|" + tituloLimpio + "|" + autorLimpio + "|" + cantidad + "|" + extraLimpio);
	}

	public void actualizarStockAdmin(String idMaterial, String cantidadTexto) {
		
		int cantidad;

		if (idMaterial == null || idMaterial.trim().isEmpty() || cantidadTexto == null || cantidadTexto.trim().isEmpty()) {
		
			this.vista.mostrarError("Debe seleccionar un material e ingresar una cantidad.");
			return;
		}

		try {
			
			cantidad = Integer.parseInt(cantidadTexto.trim());
		} catch (NumberFormatException e) {
			this.vista.mostrarError("La cantidad debe ser un numero entero valido.");
			return;
		}

		if (cantidad <= 0) {
			
			this.vista.mostrarError("La cantidad a sumar debe ser mayor que cero.");
			return;
		}

		this.cliente.enviarPeticion("ADMIN_ACTUALIZAR_STOCK|" + idMaterial.trim() + "|" + cantidad);
	}

	public void cargarPrestamosAdmin() {
		
		this.cliente.enviarPeticion("ADMIN_LISTAR_PRESTAMOS");
	}

	public void cargarUsuariosAdmin() {
		
		this.cliente.enviarPeticion("ADMIN_LISTAR_USUARIOS");
	}

	public void pagarMultaAdmin(String idUsuario, String montoTexto) {
		
		double monto;

		if (idUsuario == null || idUsuario.trim().isEmpty() || montoTexto == null || montoTexto.trim().isEmpty()) {
			
			this.vista.mostrarError("Debe seleccionar un usuario e ingresar el monto.");
			return;
		}

		try {
		
			monto = Double.parseDouble(montoTexto.trim());
		} catch (NumberFormatException e) {
			this.vista.mostrarError("El monto debe ser un numero valido.");
			return;
		}

		if (monto <= 0) {
			
			this.vista.mostrarError("El monto a pagar debe ser mayor que cero.");
			return;
		}

		this.cliente.enviarPeticion("ADMIN_PAGAR_MULTA|" + idUsuario.trim() + "|" + monto);
	}

	private boolean contieneCaracterProhibido(String texto) {
		
		if (texto.contains("|") || texto.contains(";")) {
			return true;
		} else {
			return false;
		}
	}

}