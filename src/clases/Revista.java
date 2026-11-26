package clases;

public class Revista extends MaterialBibliografico {
	private static final long serialVersionUID = 1L;
	
	private int numeroEdicion;

	public Revista(String titulo, String autor, int ejemplaresTotales, int numeroEdicion) {
		
		super(titulo, autor, ejemplaresTotales);
		
		if (numeroEdicion <= 0) {
			throw new IllegalArgumentException("El numero de edicion debe ser mayor que cero.");
		}
		
		this.numeroEdicion = numeroEdicion;
	}

	public Revista(String id, String titulo, String autor, int ejemplaresTotales, int ejemplaresDisponibles, int numeroEdicion) {
		
		super(id, titulo, autor, ejemplaresTotales, ejemplaresDisponibles);
		
		if (numeroEdicion <= 0) {
			throw new IllegalArgumentException("El numero de edicion debe ser mayor que cero.");
		}
		
		this.numeroEdicion = numeroEdicion;
	}

	public int getNumeroEdicion() {
		
		return this.numeroEdicion;
	}

	@Override
	public int obtenerDiasPrestamoBase() {
		
		return 3;
	}

	@Override
	public String obtenerTipoMaterial() {
		
		return "REVISTA";
	}

	@Override
	public String obtenerDetalle() {
		
		return "Edicion #" + this.numeroEdicion;
	}
}