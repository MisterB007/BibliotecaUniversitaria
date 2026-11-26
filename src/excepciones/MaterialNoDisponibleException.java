package excepciones;

public class MaterialNoDisponibleException extends Exception {
	private static final long serialVersionUID = 1L;

	public MaterialNoDisponibleException(String mensaje) {
		
		super(mensaje);
	
	}

}