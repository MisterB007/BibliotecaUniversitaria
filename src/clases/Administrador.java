package clases;

public class Administrador extends Usuario {
	private static final long serialVersionUID = 1L;
	
	private String codigoEmpleado;

	public Administrador(String nombre, String username, String password, String codigoEmpleado) {
	
		super(nombre, username, password);
		validarCodigoEmpleado(codigoEmpleado);
		this.codigoEmpleado = codigoEmpleado.trim();
	}

	public Administrador(String id, String nombre, String username, String password, double multa, String codigoEmpleado) {
		
		super(id, nombre, username, password, multa);
		validarCodigoEmpleado(codigoEmpleado);
		this.codigoEmpleado = codigoEmpleado.trim();
	}

	private void validarCodigoEmpleado(String codigoEmpleado) {
		
		if (codigoEmpleado == null || codigoEmpleado.trim().isEmpty() || codigoEmpleado.contains("|") || codigoEmpleado.contains(";")) {
			throw new IllegalArgumentException("El codigo de empleado es invalido.");
		}
	}

	public String getCodigoEmpleado() {
		
		return this.codigoEmpleado;
	}

	@Override
	public int calcularDiasPrestamo(int diasBase) {
		
		return diasBase + 10;
	}

	@Override
	public double calcularMoraPorDia() {
		
		return 0.0;
	}

	@Override
	public int obtenerLimitePrestamos() {
		
		return 10;
	}

	@Override
	public String obtenerRol() {
		
		return "ADMINISTRADOR";
	}

	@Override
	public String obtenerDatoExtra() {
		
		return this.codigoEmpleado;
	}
}