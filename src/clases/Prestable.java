package clases;

import excepciones.MaterialNoDisponibleException;

public interface Prestable {

	public boolean estaDisponible();

	public void prestarEjemplar() throws MaterialNoDisponibleException;

	public void devolverEjemplar();

	public int obtenerDiasPrestamoBase();

	public String obtenerTipoMaterial();

}