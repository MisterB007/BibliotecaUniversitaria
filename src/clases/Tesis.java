package clases;

public class Tesis extends MaterialBibliografico {
	private static final long serialVersionUID = 1L;
	
	private String facultad;

	public Tesis(String titulo, String autor, int ejemplaresTotales, String facultad) {

		super(titulo, autor, ejemplaresTotales);
		
		if (facultad == null || facultad.trim().isEmpty() || facultad.contains("|") || facultad.contains(";")) {
			throw new IllegalArgumentException("La facultad es invalida o contiene caracteres no permitidos.");
		}
		this.facultad = facultad.trim();
	}

	public Tesis(String id, String titulo, String autor, int ejemplaresTotales, int ejemplaresDisponibles, String facultad) {

		super(id, titulo, autor, ejemplaresTotales, ejemplaresDisponibles);
		
		if (facultad == null || facultad.trim().isEmpty() || facultad.contains("|") || facultad.contains(";")) {
			throw new IllegalArgumentException("La facultad es invalida o contiene caracteres no permitidos.");
		}
		this.facultad = facultad.trim();
	}
	
	public String getFacultad() {
		
		return this.facultad;
	}

	@Override
	public int obtenerDiasPrestamoBase() {
		
		return 5;
	}

	@Override
	public String obtenerTipoMaterial() {
		
		return "TESIS";
	}

	@Override
	public String obtenerDetalle() {
		
		return "Facultad: " + this.facultad;
	}
}