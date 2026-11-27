package clases;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Prestamo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private Usuario usuario;
	private MaterialBibliografico material;
	private LocalDate fechaPrestamo;
	private LocalDate fechaLimite;
	private LocalDate fechaDevolucion;
	private double moraGenerada;
	private EstadoPrestamo estado;

	public Prestamo(Usuario usuario, MaterialBibliografico material) {

		int diasBase, diasPrestamo;

		if (usuario == null) {
			throw new IllegalArgumentException("El usuario no puede ser nulo.");
		}
		if (material == null) {
			throw new IllegalArgumentException("El material no puede ser nulo.");
		}

		this.id = UUID.randomUUID().toString();
		this.usuario = usuario;
		this.material = material;
		this.fechaPrestamo = LocalDate.now();

		diasBase = this.material.obtenerDiasPrestamoBase();
		diasPrestamo = this.usuario.calcularDiasPrestamo(diasBase);

		this.fechaLimite = this.fechaPrestamo.plusDays(diasPrestamo);
		this.fechaDevolucion = null;
		this.moraGenerada = 0.0;
		this.estado = EstadoPrestamo.ACTIVO;
	}

	public Prestamo(String id, Usuario usuario, MaterialBibliografico material, LocalDate fechaPrestamo, LocalDate fechaLimite, LocalDate fechaDevolucion, double moraGenerada, EstadoPrestamo estado) {
		
		if (id == null || id.trim().isEmpty()) {
			throw new IllegalArgumentException("El ID no puede estar vacio.");
		}
		
		if (usuario == null || material == null || fechaPrestamo == null || fechaLimite == null || estado == null) {
			throw new IllegalArgumentException("Los datos obligatorios no pueden ser nulos.");
		}
		
		if (moraGenerada < 0) {
			throw new IllegalArgumentException("La mora generada no puede ser negativa.");
		}

		this.id = id.trim();
		this.usuario = usuario;
		this.material = material;
		this.fechaPrestamo = fechaPrestamo;
		this.fechaLimite = fechaLimite;
		this.fechaDevolucion = fechaDevolucion;
		this.moraGenerada = moraGenerada;
		this.estado = estado;
	}

	public int calcularDiasRetraso(LocalDate fechaEvaluacion) {

		long dias;

		if (fechaEvaluacion == null) {
			return 0;
		}

		dias = ChronoUnit.DAYS.between(this.fechaLimite, fechaEvaluacion);
		if (dias <= 0) {
			return 0;
		} else {
			return (int) dias;
		}
	}

	public double registrarDevolucion(LocalDate fechaReal) {

		int diasRetraso;
		double mora;

		if (this.estado == EstadoPrestamo.DEVUELTO) {
			throw new IllegalArgumentException("Este prestamo ya fue devuelto anteriormente.");
		}

		if (fechaReal == null) {
			throw new IllegalArgumentException("La fecha de devolucion no puede ser nula.");
		}

		diasRetraso = calcularDiasRetraso(fechaReal);
		mora = this.usuario.calcularMoraTotal(diasRetraso);

		this.fechaDevolucion = fechaReal;
		this.moraGenerada = mora;
		this.estado = EstadoPrestamo.DEVUELTO;

		if (mora > 0) {
			this.usuario.agregarMulta(mora);
		}
		this.material.devolverEjemplar();

		return mora;
	}

	public String getId() {
		
		return this.id;
	}

	public Usuario getUsuario() {
		
		return this.usuario;
	}

	public MaterialBibliografico getMaterial() {
		
		return this.material;
	}

	public LocalDate getFechaPrestamo() {
		
		return this.fechaPrestamo;
	}

	public LocalDate getFechaLimite() {
		
		return this.fechaLimite;
	}

	public LocalDate getFechaDevolucion() {
		
		return this.fechaDevolucion;
	}

	public double getMoraGenerada() {
		
		return this.moraGenerada;
	}

	public EstadoPrestamo getEstado() {
		
		return this.estado;
	}
}