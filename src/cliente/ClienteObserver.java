package cliente;

import java.util.List;

import clases.MaterialBibliografico;
import clases.Prestamo;
import clases.Usuario;

public interface ClienteObserver {

	public void iniciarSesionExitosa(String nombre, String rol, double multa);

	public void cerrarSesionExitosa();

	public void actualizarCatalogo(List<MaterialBibliografico> materiales);

	public void actualizarPrestamos(List<Prestamo> prestamos);

	public void actualizarPrestamosAdmin(List<Prestamo> prestamos);

	public void actualizarUsuariosAdmin(List<Usuario> usuarios);

	public void actualizarCarrito(List<MaterialBibliografico> carrito);

	public void actualizarMulta(double multaActual);

	public void notificarOperacionExitosa(String operacion, String mensaje);

	public void mostrarError(String mensaje);

}