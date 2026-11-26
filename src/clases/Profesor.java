package clases;

public class Profesor extends Usuario {
	private static final long serialVersionUID = 1L;
	
	private String departamento;

	public Profesor(String nombre, String username, String password, String departamento) {
	
		super(nombre, username, password);
		validarDepartamento(departamento);
		this.departamento = departamento.trim();
	}

	public Profesor(String id, String nombre, String username, String password, double multa, String departamento) {
		
		super(id, nombre, username, password, multa);
		validarDepartamento(departamento);
		this.departamento = departamento.trim();
	}

	private void validarDepartamento(String departamento) {
		
		if (departamento == null || departamento.trim().isEmpty() || departamento.contains("|") || departamento.contains(";")) {
			throw new IllegalArgumentException("El departamento es invalido.");
		}
	}

	public String getDepartamento() {
		
		return this.departamento;
	}

	@Override
	public int calcularDiasPrestamo(int diasBase) {
		
		return diasBase + 7;
	}

	@Override
	public double calcularMoraPorDia() {
		
		return 25.0;
	}

	@Override
	public int obtenerLimitePrestamos() {
		
		return 6;
	}

	@Override
	public String obtenerRol() {
		
		return "PROFESOR";
	}

	@Override
	public String obtenerDatoExtra() {
		
		return this.departamento;
	}
}