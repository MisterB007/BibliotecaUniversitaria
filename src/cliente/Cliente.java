package cliente;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import clases.MaterialBibliografico;
import clases.Prestamo;
import clases.Usuario;

public class Cliente {

	private String ip;
	private int puerto;
	private Socket socket;
	private ObjectOutputStream out;
	private Thread hiloEscucha;
	private ClienteObserver observer;
	private boolean conectado;

	public Cliente(String ip, int puerto, ClienteObserver observer) {
		
		this.ip = ip;
		this.puerto = puerto;
		this.observer = observer;
		this.conectado = false;
	}

	public synchronized void configurarConexion(String ip, int puerto) {
		
		this.ip = ip;
		this.puerto = puerto;
	}

	public synchronized boolean estaConectado() {
		
		if (this.conectado && this.socket != null && !this.socket.isClosed() && this.out != null) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized boolean conectar() {
		
		if (estaConectado()) {
			return true;
		}

		try {
			
			this.socket = new Socket(this.ip, this.puerto);
			this.out = new ObjectOutputStream(this.socket.getOutputStream());
			this.out.flush();
			this.conectado = true;

			this.hiloEscucha = new Thread(new Runnable() {
			
				@Override
				public void run() {
					escucharServidor();
				}
			});
			this.hiloEscucha.start();

			return true;
		} catch (IOException e) {
			this.conectado = false;
			notificarError("No se pudo conectar con el servidor en " + this.ip + ":" + this.puerto);
			return false;
		}
	}

	public synchronized boolean enviarPeticion(String peticion) {
		
		if (peticion == null || peticion.trim().isEmpty()) {
			return false;
		}

		if (!estaConectado()) {
			notificarError("No hay conexion activa con el servidor.");
			return false;
		}

		try {
			
			this.out.writeObject(peticion);
			this.out.flush();
			return true;
		} catch (IOException e) {
			desconectar();
			notificarError("Error al enviar peticion al servidor.");
			return false;
		}
	}

	public synchronized void desconectar() {
		
		this.conectado = false;
		this.hiloEscucha = null;

		try {
			if (this.out != null) {
				this.out.close();
				this.out = null;
			}
		} catch (IOException e) {
			System.out.println("Error al cerrar flujo: " + e.getMessage());
		}

		try {
			
			if (this.socket != null && !this.socket.isClosed()) {
				this.socket.close();
				this.socket = null;
			}
		} catch (IOException e) {
			System.out.println("Error al cerrar socket: " + e.getMessage());
		}
	}

	private void escucharServidor() {
		
		Object objetoRecibido;
		String respuesta;

		try (ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream())) {
			while (this.conectado) {
				objetoRecibido = in.readObject();

				if (objetoRecibido instanceof String) {
					respuesta = (String) objetoRecibido;
					procesarRespuesta(respuesta, in);
				}
			}
		} catch (EOFException e) {
			
			if (this.conectado) {
				desconectar();
				notificarError("El servidor cerro la conexion.");
				
				if (this.observer != null) {
					this.observer.cerrarSesionExitosa();
				}
			}
		} catch (SocketException e) {
		
			if (this.conectado) {
				desconectar();
				notificarError("Se perdio la conexion con el servidor.");
			
				if (this.observer != null) {
					this.observer.cerrarSesionExitosa();
				}
			}
		} catch (Exception e) {
			
			if (this.conectado) {
				desconectar();
				notificarError("Error de comunicacion: " + e.getMessage());
				
				if (this.observer != null) {
					this.observer.cerrarSesionExitosa();
				}
			}
		}
	}

	private void procesarRespuesta(String respuesta, ObjectInputStream in) throws IOException, ClassNotFoundException {
		
		String[] partes;
		String estado, tipoRespuesta;

		if (respuesta == null || respuesta.trim().isEmpty()) {
			return;
		}

		partes = respuesta.trim().split("\\|");
		if (partes.length == 0) {
			return;
		}

		estado = partes[0].trim().toUpperCase();

		if (estado.equals("ERROR")) {
			procesarError(partes);
			return;
		}

		if (!estado.equals("OK") || partes.length < 2) {
			return;
		}

		tipoRespuesta = partes[1].trim();

		if (tipoRespuesta.equals("COLECCION")) {
			procesarColeccion(partes, in);
		} else if (tipoRespuesta.equals("desconectado")) {
			
			if (this.observer != null) {
				this.observer.cerrarSesionExitosa();
			}
		} else if (tipoRespuesta.equals("prestamos_realizados")) {
			procesarConfirmacionPrestamo(partes);
		} else if (tipoRespuesta.equals("devuelto")) {
			procesarConfirmacionDevolucion(partes);
		} else if (tipoRespuesta.equals("material_agregado")) {
			procesarMaterialAgregado(partes);
		} else if (tipoRespuesta.equals("stock_actualizado")) {
			procesarStockActualizado(partes);
		} else if (tipoRespuesta.equals("multa_actualizada")) {
			procesarMultaActualizada(partes);
		} else if (partes.length == 6) {
			procesarLoginExitoso(partes);
		}
	}

	private void procesarLoginExitoso(String[] partes) {
		
		String id, nombre, rol;
		double multa;
		int limitePrestamos;

		id = partes[1].trim();
		nombre = partes[2].trim();
		rol = partes[3].trim();

		try {
			multa = Double.parseDouble(partes[4].trim());
			limitePrestamos = Integer.parseInt(partes[5].trim());
		} catch (NumberFormatException e) {
			notificarError("Respuesta de inicio de sesion invalida.");
			return;
		}

		if (this.observer != null) {
			this.observer.iniciarSesionExitosa(id, nombre, rol, multa, limitePrestamos);
		}
	}

	private void procesarColeccion(String[] partes, ObjectInputStream in) throws IOException, ClassNotFoundException {
		
		String tipoColeccion;
		Object objetoLista;
		List<MaterialBibliografico> catalogo;
		List<Prestamo> listaPrestamos;
		List<Usuario> listaUsuarios;

		if (partes.length < 3) {
			return;
		}

		tipoColeccion = partes[2].trim().toUpperCase();
		objetoLista = in.readObject();

		if (this.observer == null) {
			return;
		}

		if (tipoColeccion.equals("CATALOGO")) {
			catalogo = (List<MaterialBibliografico>) objetoLista;
			this.observer.actualizarCatalogo(catalogo);
		} else if (tipoColeccion.equals("PRESTAMOS")) {
			listaPrestamos = (List<Prestamo>) objetoLista;
			this.observer.actualizarPrestamos(listaPrestamos);
		} else if (tipoColeccion.equals("PRESTAMOS_ADMIN")) {
			listaPrestamos = (List<Prestamo>) objetoLista;
			this.observer.actualizarPrestamosAdmin(listaPrestamos);
		} else if (tipoColeccion.equals("USUARIOS_ADMIN")) {
			listaUsuarios = (List<Usuario>) objetoLista;
			this.observer.actualizarUsuariosAdmin(listaUsuarios);
		}
	}

	private void procesarConfirmacionPrestamo(String[] partes) {
		
		String cantidadTexto;

		if (partes.length < 3) {
			cantidadTexto = "";
		} else {
			cantidadTexto = partes[2].trim();
		}

		if (this.observer != null) {
			this.observer.notificarOperacionExitosa("PRESTAMO", "Prestamo realizado con exito. Cantidad de materiales: " + cantidadTexto);
		}
	}

	private void procesarConfirmacionDevolucion(String[] partes) {

		double mora, multaActual;

		if (partes.length < 4) {
			return;
		}

		try {
			mora = Double.parseDouble(partes[2].trim());
			multaActual = Double.parseDouble(partes[3].trim());
		} catch (NumberFormatException e) {
			notificarError("Error al leer los datos de devolucion.");
			return;
		}

		if (this.observer != null) {
			this.observer.actualizarMulta(multaActual);
			this.observer.notificarOperacionExitosa("DEVOLUCION", "Devolucion registrada correctamente. Mora generada: $" + mora);
		}
	}

	private void procesarMaterialAgregado(String[] partes) {
		
		String titulo;

		if (partes.length < 3) {
			titulo = "";
		} else {
			titulo = partes[2].trim();
		}

		if (this.observer != null) {
			this.observer.notificarOperacionExitosa("MATERIAL", "Material agregado al catalogo: " + titulo);
		}
	}

	private void procesarStockActualizado(String[] partes) {
		
		String disponibles, totales;

		if (partes.length < 4) {
			return;
		}

		disponibles = partes[2].trim();
		totales = partes[3].trim();

		if (this.observer != null) {
			this.observer.notificarOperacionExitosa("STOCK", "Stock actualizado. Ejemplares disponibles: " + disponibles + "/" + totales);
		}
	}

	private void procesarMultaActualizada(String[] partes) {
		
		String saldoRestante;

		if (partes.length < 3) {
			return;
		}

		saldoRestante = partes[2].trim();

		if (this.observer != null) {
			this.observer.notificarOperacionExitosa("MULTA", "Pago registrado. Multa restante del usuario: $" + saldoRestante);
		}
	}

	private void procesarError(String[] partes) {
		
		String codigo, mensaje;

		if (partes.length < 2) {
			notificarError("Ocurrio un error en el servidor.");
			return;
		}

		codigo = partes[1].trim();

		if (codigo.equals("credenciales_invalidas")) {
			mensaje = "Usuario o contrasena incorrectos.";
		} else if (codigo.equals("sesion_no_activa")) {
			mensaje = "Debe iniciar sesion para realizar esta accion.";
		} else if (codigo.equals("acceso_denegado")) {
			mensaje = "No tiene permisos de administrador para realizar esta accion.";
		} else if (codigo.equals("peticion_invalida")) {
			mensaje = "La peticion enviada no es valida.";
		} else if (codigo.equals("usuario_con_multa") || codigo.equals("material_no_disponible")) {
			if (partes.length >= 3) {
				mensaje = partes[2].trim();
			} else {
				mensaje = "No se pudo completar el prestamo.";
			}
		} else {
			mensaje = codigo;
		}

		notificarError(mensaje);
	}

	private void notificarError(String mensaje) {
		if (this.observer != null) {
			this.observer.mostrarError(mensaje);
		}
	}

}