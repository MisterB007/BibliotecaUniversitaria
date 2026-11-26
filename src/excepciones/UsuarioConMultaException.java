package excepciones;

public class UsuarioConMultaException extends Exception {
	private static final long serialVersionUID = 1L;

	public UsuarioConMultaException(String mensaje) {
		
		super(mensaje);
	
	}

}