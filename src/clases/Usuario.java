package clases;

import java.io.Serializable;
import java.util.UUID;

public abstract class Usuario implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String nombre;
	private String username;
	private transient String password;
	private double multa;

	public Usuario(String nombre, String username, String password) {
		
		if (nombre == null || nombre.trim().isEmpty() || nombre.contains("|") || nombre.contains(";")) {
			throw new IllegalArgumentException("El nombre del usuario es invalido.");
		}
		
		if (username == null || username.trim().isEmpty() || username.contains("|") || username.contains(";")) {
			throw new IllegalArgumentException("El nombre de usuario es invalido.");
		}
		
		if (password == null || password.trim().isEmpty() || password.contains("|") || password.contains(";")) {
			throw new IllegalArgumentException("La contrasena del usuario es invalida.");
		}

		this.id = UUID.randomUUID().toString();
		this.nombre = nombre.trim();
		this.username = username.trim();
		this.password = password;
		this.multa = 0.0;
	}

	public Usuario(String id, String nombre, String username, String password, double multa) {
		
		if (id == null || id.trim().isEmpty()) {
			throw new IllegalArgumentException("El ID del usuario no puede estar vacio.");
		}
		
		if (nombre == null || nombre.trim().isEmpty() || nombre.contains("|") || nombre.contains(";")) {
			throw new IllegalArgumentException("El nombre del usuario es invalido.");
		}
		
		if (username == null || username.trim().isEmpty() || username.contains("|") || username.contains(";")) {
			throw new IllegalArgumentException("El nombre de usuario es invalido.");
		}
		
		if (password == null || password.trim().isEmpty() || password.contains("|") || password.contains(";")) {
			throw new IllegalArgumentException("La contrasena del usuario es invalida.");
		}
		
		if (multa < 0) {
			throw new IllegalArgumentException("La multa no puede ser negativa.");
		}

		this.id = id.trim();
		this.nombre = nombre.trim();
		this.username = username.trim();
		this.password = password;
		this.multa = multa;
	}
	
	public abstract int calcularDiasPrestamo(int diasBase);

	public abstract double calcularMoraPorDia();

	public abstract int obtenerLimitePrestamos();

	public abstract String obtenerRol();

	public abstract String obtenerDatoExtra();

	public boolean validarPassword(String clave) {
	
		if (this.password == null || clave == null) {
			return false;
		}
		
		return this.password.equals(clave);
	}

	public boolean tieneMultaPendiente() {
		
		return this.multa > 0;
	}

	public void agregarMulta(double monto) {
		
		if (monto <= 0) {
			throw new IllegalArgumentException("El monto de la multa debe ser mayor que cero.");
		}
		
		this.multa += monto;
	}

	public void pagarMulta(double monto) {
		
		if (monto <= 0) {
			throw new IllegalArgumentException("El monto a pagar debe ser mayor que cero.");
		}
		
		if (monto > this.multa) {
			throw new IllegalArgumentException("El monto supera la multa acumulada.");
		}
		
		this.multa -= monto;
	}

	public double calcularMoraTotal(int diasRetraso) {
		
		if (diasRetraso <= 0) {
			return 0.0;
		}
		
		return diasRetraso * calcularMoraPorDia();
	}

	public String getId() {
		
		return this.id;
	}

	public String getNombre() {
		
		return this.nombre;
	}

	public String getUsername() {
		
		return this.username;
	}

	public String getPassword() {
		
		return this.password;
	}

	public double getMulta() {
		
		return this.multa;
	}

	@Override
	public String toString() {
		
		return this.nombre + " (" + obtenerRol() + ")";
	}

}