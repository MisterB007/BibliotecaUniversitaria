package clases;

public class Estudiante extends Usuario {
	private static final long serialVersionUID = 1L;
	
	private String carrera;

	public Estudiante(String nombre, String username, String password, String carrera) {

		super(nombre, username, password);
		
		if (carrera == null || carrera.trim().isEmpty() || carrera.contains("|") || carrera.contains(";")) {
			throw new IllegalArgumentException("La carrera es invalida.");
		}
		this.carrera = carrera.trim();
	}

	public Estudiante(String id, String nombre, String username, String password, double multa, String carrera) {

		super(id, nombre, username, password, multa);
	
		if (carrera == null || carrera.trim().isEmpty() || carrera.contains("|") || carrera.contains(";")) {
			throw new IllegalArgumentException("La carrera es invalida.");
		}
		this.carrera = carrera.trim();
	}

	public String getCarrera() {
		
		return this.carrera;
	}

	@Override
	public int calcularDiasPrestamo(int diasBase) {
		
		return diasBase;
	}

	@Override
	public double calcularMoraPorDia() {
		
		return 50.0;
	}

	@Override
	public int obtenerLimitePrestamos() {
		
		return 3;
	}

	@Override
	public String obtenerRol() {
		
		return "ESTUDIANTE";
	}

	@Override
	public String obtenerDatoExtra() {
		
		return this.carrera;
	}

}