package clases;

public class Libro extends MaterialBibliografico {
	private static final long serialVersionUID = 1L;
	
	private String editorial;

	public Libro(String titulo, String autor, int ejemplaresTotales, String editorial) {
		
		super(titulo, autor, ejemplaresTotales);
		validarEditorial(editorial);
		this.editorial = editorial.trim();
	}

	public Libro(String id, String titulo, String autor, int ejemplaresTotales, int ejemplaresDisponibles, String editorial) {
		
		super(id, titulo, autor, ejemplaresTotales, ejemplaresDisponibles);
		validarEditorial(editorial);
		this.editorial = editorial.trim();
	}

	private void validarEditorial(String editorial) {
	
		if (editorial == null || editorial.trim().isEmpty() || editorial.contains("|") || editorial.contains(";")) {
			throw new IllegalArgumentException("La editorial es invalida o contiene caracteres no permitidos.");
		}
	}

	public String getEditorial() {
		
		return this.editorial;
	}

	@Override
	public int obtenerDiasPrestamoBase() {
		
		return 7;
	}

	@Override
	public String obtenerTipoMaterial() {
		
		return "LIBRO";
	}

	@Override
	public String obtenerDetalle() {
		
		return "Editorial: " + this.editorial;
	}
}