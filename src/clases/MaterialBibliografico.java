package clases;

import java.io.Serializable;
import java.util.UUID;

import excepciones.MaterialNoDisponibleException;

public abstract class MaterialBibliografico implements Serializable, Prestable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String titulo;
	private String autor;
	private int ejemplaresTotales;
	private int ejemplaresDisponibles;

		public MaterialBibliografico(String titulo, String autor, int ejemplaresTotales) {
			
			if (titulo == null || titulo.trim().isEmpty() || titulo.contains("|") || titulo.contains(";")) {
				throw new IllegalArgumentException("El titulo es invalido o contiene caracteres no permitidos.");
			}
			
			if (autor == null || autor.trim().isEmpty() || autor.contains("|") || autor.contains(";")) {
				throw new IllegalArgumentException("El autor es invalido o contiene caracteres no permitidos.");
			}
			
			if (ejemplaresTotales <= 0) {
				throw new IllegalArgumentException("La cantidad total debe ser mayor que cero.");
			}

			this.id = UUID.randomUUID().toString();
			this.titulo = titulo.trim();
			this.autor = autor.trim();
			this.ejemplaresTotales = ejemplaresTotales;
			this.ejemplaresDisponibles = ejemplaresTotales;
		}

		public MaterialBibliografico(String id, String titulo, String autor, int ejemplaresTotales, int ejemplaresDisponibles) {
			
			if (id == null || id.trim().isEmpty()) {
				throw new IllegalArgumentException("El ID no puede estar vacio.");
			}
			
			if (titulo == null || titulo.trim().isEmpty() || titulo.contains("|") || titulo.contains(";")) {
				throw new IllegalArgumentException("El titulo es invalido o contiene caracteres no permitidos.");
			}
			
			if (autor == null || autor.trim().isEmpty() || autor.contains("|") || autor.contains(";")) {
				throw new IllegalArgumentException("El autor es invalido o contiene caracteres no permitidos.");
			}
			
			if (ejemplaresTotales <= 0) {
				throw new IllegalArgumentException("La cantidad total debe ser mayor que cero.");
			}
			
			if (ejemplaresDisponibles < 0 || ejemplaresDisponibles > ejemplaresTotales) {
				throw new IllegalArgumentException("Fuera de rango.");
			}

			this.id = id.trim();
			this.titulo = titulo.trim();
			this.autor = autor.trim();
			this.ejemplaresTotales = ejemplaresTotales;
			this.ejemplaresDisponibles = ejemplaresDisponibles;
		}

	@Override
	public boolean estaDisponible() {
		
		if (this.ejemplaresDisponibles > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void prestarEjemplar() throws MaterialNoDisponibleException {
		
		if (!estaDisponible()) {
			throw new MaterialNoDisponibleException("No hay ejemplares disponibles de: " + this.titulo);
		}
		this.ejemplaresDisponibles--;
	}

	@Override
	public void devolverEjemplar() {
		if (this.ejemplaresDisponibles < this.ejemplaresTotales) {
			this.ejemplaresDisponibles++;
		}
	}

	public void agregarEjemplares(int cantidad) {
		
		if (cantidad <= 0) {
			throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
		}
		this.ejemplaresTotales += cantidad;
		this.ejemplaresDisponibles += cantidad;
	}

	public boolean coincideConFiltro(String filtro) {

		String texto, tituloMin, autorMin, tipoMin, detalleMin;

		if (filtro == null || filtro.trim().isEmpty()) {
			return true;
		}

		texto = filtro.trim();
		texto = texto.toLowerCase();
		tituloMin = this.titulo.toLowerCase();
		autorMin = this.autor.toLowerCase();
		tipoMin = obtenerTipoMaterial().toLowerCase();
		detalleMin = obtenerDetalle().toLowerCase();

		if (tituloMin.contains(texto) || autorMin.contains(texto) || tipoMin.contains(texto) || detalleMin.contains(texto)) {
			return true;
		} else {
			return false;
		}
	}

	public abstract String obtenerDetalle();

	public String getId() {
		return this.id;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public String getAutor() {
		return this.autor;
	}

	public int getEjemplaresTotales() {
		return this.ejemplaresTotales;
	}

	public int getEjemplaresDisponibles() {
		return this.ejemplaresDisponibles;
	}

	@Override
	public String toString() {
		return "[" + obtenerTipoMaterial() + "] " + this.titulo + " - " + this.autor + " - Disponibles: " + this.ejemplaresDisponibles + "/" + this.ejemplaresTotales;
	}

}