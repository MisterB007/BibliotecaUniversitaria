package interfaz;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import clases.MaterialBibliografico;
import clases.Prestamo;
import clases.Usuario;
import cliente.ClienteObserver;
import cliente.ControladorBiblioteca;

public class ClienteGUI extends JFrame implements ClienteObserver {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblInfoUsuario;
	private JButton btnCerrarSesion;
	private JTabbedPane tabbedPane;

	private JPanel panelCatalogo;
	private JTextField txtBuscador;
	private JComboBox<String> cbTipoFiltro;
	private JButton btnActualizarCatalogo;
	private JTable tablaCatalogo;
	private JButton btnAgregarCarrito;
	private JLabel lblSumarStock;
	private JTextField txtSumarStock;
	private JButton btnActualizarStock;

	private JPanel panelCarrito;
	private JTable tablaCarrito;
	private JButton btnQuitarCarrito;
	private JButton btnVaciarCarrito;
	private JButton btnConfirmarPrestamo;

	private JPanel panelPrestamos;
	private JTable tablaPrestamos;
	private JTextField txtDiasSimulados;
	private JButton btnDevolver;
	private JButton btnActualizarPrestamos;

	private JPanel panelAdmin;
	private JComboBox<String> cbTipoMaterial;
	private JTextField txtTitulo;
	private JTextField txtAutor;
	private JTextField txtCantidad;
	private JLabel lblDatoExtra;
	private JTextField txtDatoExtra;
	private JButton btnAgregarMaterial;
	private JTable tablaUsuariosAdmin;
	private JTextField txtMontoMulta;
	private JButton btnPagarMulta;
	private JTable tablaPrestamosAdmin;
	private JButton btnActualizarAdmin;

	private DefaultTableModel modeloCatalogo;
	private DefaultTableModel modeloCarrito;
	private DefaultTableModel modeloPrestamos;
	private DefaultTableModel modeloUsuariosAdmin;
	private DefaultTableModel modeloPrestamosAdmin;

	private List<MaterialBibliografico> listaCatalogo;
	private List<MaterialBibliografico> listaCarrito;
	private List<Prestamo> listaPrestamos;
	private List<Usuario> listaUsuariosAdmin;
	private List<Prestamo> listaPrestamosAdmin;

	private ControladorBiblioteca controlador;
	private LoginGUI loginGUI;
	private String nombreUsuario;
	private String rolUsuario;
	private double multaUsuario;

	public ClienteGUI() {
		setTitle("Biblioteca Universitaria");
		setSize(new Dimension(850, 600));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setSize(new Dimension(850, 600));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblInfoUsuario = new JLabel("Usuario: - | Multa: $0.0");
		lblInfoUsuario.setBounds(29, 37, 440, 16);
		contentPane.add(lblInfoUsuario);

		btnCerrarSesion = new JButton("Cerrar Sesion");
		btnCerrarSesion.setBounds(492, 33, 116, 20);
		contentPane.add(btnCerrarSesion);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 93, 816, 419);
		contentPane.add(tabbedPane);

		panelCatalogo = new JPanel();
		tabbedPane.addTab("Catalogo", null, panelCatalogo, null);
		panelCatalogo.setLayout(null);

		JLabel lblBuscar = new JLabel("Buscar:");
		lblBuscar.setBounds(30, 29, 44, 12);
		panelCatalogo.add(lblBuscar);

		txtBuscador = new JTextField();
		txtBuscador.setBounds(84, 26, 96, 18);
		panelCatalogo.add(txtBuscador);
		txtBuscador.setColumns(10);

		JLabel lblTipoFiltro = new JLabel("Tipo:");
		lblTipoFiltro.setBounds(224, 29, 44, 12);
		panelCatalogo.add(lblTipoFiltro);

		cbTipoFiltro = new JComboBox<String>();
		cbTipoFiltro.setBounds(278, 25, 80, 20);
		panelCatalogo.add(cbTipoFiltro);

		btnActualizarCatalogo = new JButton("Actualizar");
		btnActualizarCatalogo.setBounds(398, 25, 84, 20);
		panelCatalogo.add(btnActualizarCatalogo);

		JScrollPane scrollCatalogo = new JScrollPane();
		scrollCatalogo.setBounds(10, 80, 665, 221);
		panelCatalogo.add(scrollCatalogo);

		tablaCatalogo = new JTable();
		scrollCatalogo.setViewportView(tablaCatalogo);

		btnAgregarCarrito = new JButton("Agregar al Carrito");
		btnAgregarCarrito.setBounds(10, 341, 123, 20);
		panelCatalogo.add(btnAgregarCarrito);

		lblSumarStock = new JLabel("Sumar Stock:");
		lblSumarStock.setBounds(437, 343, 74, 16);
		panelCatalogo.add(lblSumarStock);

		txtSumarStock = new JTextField();
		txtSumarStock.setBounds(521, 342, 96, 18);
		panelCatalogo.add(txtSumarStock);
		txtSumarStock.setColumns(10);

		btnActualizarStock = new JButton("Actualizar Stock");
		btnActualizarStock.setBounds(627, 341, 123, 20);
		panelCatalogo.add(btnActualizarStock);

		panelCarrito = new JPanel();
		tabbedPane.addTab("Carrito", null, panelCarrito, null);
		panelCarrito.setLayout(null);

		JScrollPane scrollCarrito = new JScrollPane();
		scrollCarrito.setBounds(28, 39, 531, 243);
		panelCarrito.add(scrollCarrito);

		tablaCarrito = new JTable();
		scrollCarrito.setViewportView(tablaCarrito);

		btnQuitarCarrito = new JButton("Quitar Seleccionado");
		btnQuitarCarrito.setBounds(32, 318, 123, 20);
		panelCarrito.add(btnQuitarCarrito);

		btnVaciarCarrito = new JButton("Vaciar Carrito");
		btnVaciarCarrito.setBounds(170, 318, 110, 20);
		panelCarrito.add(btnVaciarCarrito);

		btnConfirmarPrestamo = new JButton("Confirmar Prestamo");
		btnConfirmarPrestamo.setBounds(303, 318, 134, 20);
		panelCarrito.add(btnConfirmarPrestamo);

		panelPrestamos = new JPanel();
		tabbedPane.addTab("Mis Prestamos", null, panelPrestamos, null);
		panelPrestamos.setLayout(null);

		JScrollPane scrollPrestamos = new JScrollPane();
		scrollPrestamos.setBounds(35, 40, 580, 243);
		panelPrestamos.add(scrollPrestamos);

		tablaPrestamos = new JTable();
		scrollPrestamos.setViewportView(tablaPrestamos);

		JLabel lblDiasSimulados = new JLabel("Dias simulados:");
		lblDiasSimulados.setBounds(35, 319, 86, 20);
		panelPrestamos.add(lblDiasSimulados);

		txtDiasSimulados = new JTextField();
		txtDiasSimulados.setText("0");
		txtDiasSimulados.setBounds(111, 320, 96, 18);
		panelPrestamos.add(txtDiasSimulados);
		txtDiasSimulados.setColumns(10);

		btnDevolver = new JButton("Devolver Material");
		btnDevolver.setBounds(252, 319, 111, 20);
		panelPrestamos.add(btnDevolver);

		btnActualizarPrestamos = new JButton("Actualizar");
		btnActualizarPrestamos.setBounds(377, 319, 111, 20);
		panelPrestamos.add(btnActualizarPrestamos);

		panelAdmin = new JPanel();
		tabbedPane.addTab("Administracion", null, panelAdmin, null);
		panelAdmin.setLayout(null);

		JLabel lblTipoMaterial = new JLabel("Tipo:");
		lblTipoMaterial.setBounds(10, 10, 44, 12);
		panelAdmin.add(lblTipoMaterial);

		cbTipoMaterial = new JComboBox<String>();
		cbTipoMaterial.setBounds(64, 6, 96, 20);
		panelAdmin.add(cbTipoMaterial);

		JLabel lblTitulo = new JLabel("Titulo:");
		lblTitulo.setBounds(10, 33, 44, 12);
		panelAdmin.add(lblTitulo);

		txtTitulo = new JTextField();
		txtTitulo.setBounds(64, 36, 96, 18);
		panelAdmin.add(txtTitulo);
		txtTitulo.setColumns(10);

		JLabel lblAutor = new JLabel("Autor:");
		lblAutor.setBounds(10, 71, 44, 12);
		panelAdmin.add(lblAutor);

		txtAutor = new JTextField();
		txtAutor.setColumns(10);
		txtAutor.setBounds(64, 68, 96, 18);
		panelAdmin.add(txtAutor);

		JLabel lblCantidad = new JLabel("Cantidad:");
		lblCantidad.setBounds(10, 104, 44, 12);
		panelAdmin.add(lblCantidad);

		txtCantidad = new JTextField();
		txtCantidad.setColumns(10);
		txtCantidad.setBounds(64, 101, 96, 18);
		panelAdmin.add(txtCantidad);

		lblDatoExtra = new JLabel("Editorial:");
		lblDatoExtra.setBounds(0, 132, 60, 12);
		panelAdmin.add(lblDatoExtra);

		txtDatoExtra = new JTextField();
		txtDatoExtra.setColumns(10);
		txtDatoExtra.setBounds(64, 129, 96, 18);
		panelAdmin.add(txtDatoExtra);

		btnAgregarMaterial = new JButton("Agregar Material");
		btnAgregarMaterial.setBounds(64, 171, 119, 20);
		panelAdmin.add(btnAgregarMaterial);

		JLabel lblUsuarios = new JLabel("Usuarios:");
		lblUsuarios.setBounds(418, 10, 44, 12);
		panelAdmin.add(lblUsuarios);

		JScrollPane scrollUsuariosAdmin = new JScrollPane();
		scrollUsuariosAdmin.setBounds(409, 33, 363, 135);
		panelAdmin.add(scrollUsuariosAdmin);

		tablaUsuariosAdmin = new JTable();
		scrollUsuariosAdmin.setViewportView(tablaUsuariosAdmin);

		JLabel lblMontoMulta = new JLabel("Monto: ");
		lblMontoMulta.setBounds(418, 189, 44, 12);
		panelAdmin.add(lblMontoMulta);

		txtMontoMulta = new JTextField();
		txtMontoMulta.setBounds(472, 186, 96, 18);
		panelAdmin.add(txtMontoMulta);
		txtMontoMulta.setColumns(10);

		btnPagarMulta = new JButton("Pagar Multa");
		btnPagarMulta.setBounds(588, 185, 103, 20);
		panelAdmin.add(btnPagarMulta);

		JLabel lblPrestamosAdmin = new JLabel("Todos los Prestamos");
		lblPrestamosAdmin.setBounds(10, 211, 111, 12);
		panelAdmin.add(lblPrestamosAdmin);

		JScrollPane scrollPrestamosAdmin = new JScrollPane();
		scrollPrestamosAdmin.setBounds(10, 233, 306, 91);
		panelAdmin.add(scrollPrestamosAdmin);

		tablaPrestamosAdmin = new JTable();
		scrollPrestamosAdmin.setViewportView(tablaPrestamosAdmin);

		btnActualizarAdmin = new JButton("Actualizar Datos");
		btnActualizarAdmin.setBounds(20, 334, 119, 20);
		panelAdmin.add(btnActualizarAdmin);

		this.listaCatalogo = new ArrayList<>();
		this.listaCarrito = new ArrayList<>();
		this.listaPrestamos = new ArrayList<>();
		this.listaUsuariosAdmin = new ArrayList<>();
		this.listaPrestamosAdmin = new ArrayList<>();

		inicializarComponentes();
		configurarEventos();
	}

	public void setControlador(ControladorBiblioteca controlador) {
		this.controlador = controlador;
	}

	public void setLoginGUI(LoginGUI loginGUI) {
		this.loginGUI = loginGUI;
	}

	private void inicializarComponentes() {
		String[] colCatalogo = { "Tipo", "Titulo", "Autor", "Detalle", "Disponibles", "Totales" };
		String[] colCarrito = { "Tipo", "Titulo", "Autor", "Detalle" };
		String[] colPrestamos = { "Material", "Prestamo", "Limite", "Devolucion", "Mora", "Estado" };
		String[] colUsuarios = { "Nombre", "Usuario", "Rol", "Multa" };
		String[] colPrestamosAdmin = { "Usuario", "Material", "Limite", "Mora", "Estado" };

		this.cbTipoFiltro.addItem("TODOS");
		this.cbTipoFiltro.addItem("LIBRO");
		this.cbTipoFiltro.addItem("TESIS");
		this.cbTipoFiltro.addItem("REVISTA");

		this.cbTipoMaterial.addItem("LIBRO");
		this.cbTipoMaterial.addItem("TESIS");
		this.cbTipoMaterial.addItem("REVISTA");

		this.modeloCatalogo = crearModeloNoEditable(colCatalogo);
		this.modeloCarrito = crearModeloNoEditable(colCarrito);
		this.modeloPrestamos = crearModeloNoEditable(colPrestamos);
		this.modeloUsuariosAdmin = crearModeloNoEditable(colUsuarios);
		this.modeloPrestamosAdmin = crearModeloNoEditable(colPrestamosAdmin);

		this.tablaCatalogo.setModel(this.modeloCatalogo);
		this.tablaCarrito.setModel(this.modeloCarrito);
		this.tablaPrestamos.setModel(this.modeloPrestamos);
		this.tablaUsuariosAdmin.setModel(this.modeloUsuariosAdmin);
		this.tablaPrestamosAdmin.setModel(this.modeloPrestamosAdmin);

		this.tablaCatalogo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tablaCarrito.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tablaPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tablaUsuariosAdmin.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.tablaPrestamosAdmin.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private DefaultTableModel crearModeloNoEditable(String[] columnas) {
		DefaultTableModel modelo;

		modelo = new DefaultTableModel(columnas, 0) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		return modelo;
	}

	private void configurarEventos() {
		this.btnCerrarSesion.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controlador != null) {
					controlador.cerrarSesion();
				}
			}
		});

		this.txtBuscador.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String tipo, texto;

				if (controlador == null || !isVisible()) {
					return;
				}

				tipo = (String) cbTipoFiltro.getSelectedItem();
				texto = txtBuscador.getText();
				controlador.buscarCatalogo(tipo, texto);
			}
		});

		this.cbTipoFiltro.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tipo, texto;

				if (controlador == null || !isVisible()) {
					return;
				}

				tipo = (String) cbTipoFiltro.getSelectedItem();
				texto = txtBuscador.getText();
				controlador.buscarCatalogo(tipo, texto);
			}
		});

		this.btnActualizarCatalogo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tipo, texto;

				if (controlador == null) {
					return;
				}

				tipo = (String) cbTipoFiltro.getSelectedItem();
				texto = txtBuscador.getText();
				controlador.buscarCatalogo(tipo, texto);
			}
		});

		this.btnAgregarCarrito.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int fila;
				MaterialBibliografico seleccionado;

				if (controlador == null) {
					return;
				}

				fila = tablaCatalogo.getSelectedRow();
				if (fila == -1 || fila >= listaCatalogo.size()) {
					JOptionPane.showMessageDialog(ClienteGUI.this, "Debe seleccionar un material del catalogo.");
					return;
				}

				seleccionado = listaCatalogo.get(fila);
				controlador.agregarAlCarrito(seleccionado);
			}
		});

		this.btnActualizarStock.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int fila;
				MaterialBibliografico seleccionado;

				if (controlador == null) {
					return;
				}

				fila = tablaCatalogo.getSelectedRow();
				if (fila == -1 || fila >= listaCatalogo.size()) {
					JOptionPane.showMessageDialog(ClienteGUI.this, "Debe seleccionar un material del catalogo.");
					return;
				}

				seleccionado = listaCatalogo.get(fila);
				controlador.actualizarStockAdmin(seleccionado.getId(), txtSumarStock.getText());
			}
		});

		this.btnQuitarCarrito.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int fila;
				MaterialBibliografico seleccionado;

				if (controlador == null) {
					return;
				}

				fila = tablaCarrito.getSelectedRow();
				if (fila == -1 || fila >= listaCarrito.size()) {
					JOptionPane.showMessageDialog(ClienteGUI.this, "Debe seleccionar un material del carrito.");
					return;
				}

				seleccionado = listaCarrito.get(fila);
				controlador.quitarDelCarrito(seleccionado.getId());
			}
		});

		this.btnVaciarCarrito.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controlador != null) {
					controlador.vaciarCarrito();
				}
			}
		});

		this.btnConfirmarPrestamo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controlador != null) {
					controlador.confirmarCarrito();
				}
			}
		});

		this.btnDevolver.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int fila;
				Prestamo seleccionado;

				if (controlador == null) {
					return;
				}

				fila = tablaPrestamos.getSelectedRow();
				if (fila == -1 || fila >= listaPrestamos.size()) {
					JOptionPane.showMessageDialog(ClienteGUI.this, "Debe seleccionar un prestamo para devolver.");
					return;
				}

				seleccionado = listaPrestamos.get(fila);
				controlador.devolverPrestamo(seleccionado.getId(), txtDiasSimulados.getText());
			}
		});

		this.btnActualizarPrestamos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controlador != null) {
					controlador.cargarMisPrestamos();
				}
			}
		});

		this.cbTipoMaterial.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tipo;

				tipo = (String) cbTipoMaterial.getSelectedItem();
				if (tipo == null) {
					return;
				}

				if (tipo.equals("LIBRO")) {
					lblDatoExtra.setText("Editorial:");
				} else if (tipo.equals("TESIS")) {
					lblDatoExtra.setText("Facultad:");
				} else if (tipo.equals("REVISTA")) {
					lblDatoExtra.setText("Edicion:");
				}
			}
		});

		this.btnAgregarMaterial.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tipo;

				if (controlador == null) {
					return;
				}

				tipo = (String) cbTipoMaterial.getSelectedItem();
				controlador.agregarMaterialAdmin(tipo, txtTitulo.getText(), txtAutor.getText(),
						txtCantidad.getText(), txtDatoExtra.getText());
			}
		});

		this.btnPagarMulta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int fila;
				Usuario seleccionado;

				if (controlador == null) {
					return;
				}

				fila = tablaUsuariosAdmin.getSelectedRow();
				if (fila == -1 || fila >= listaUsuariosAdmin.size()) {
					JOptionPane.showMessageDialog(ClienteGUI.this, "Debe seleccionar un usuario de la tabla.");
					return;
				}

				seleccionado = listaUsuariosAdmin.get(fila);
				controlador.pagarMultaAdmin(seleccionado.getId(), txtMontoMulta.getText());
			}
		});

		this.btnActualizarAdmin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (controlador != null) {
					controlador.cargarUsuariosAdmin();
					controlador.cargarPrestamosAdmin();
					controlador.cargarCatalogo();
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (controlador != null) {
					controlador.desconectar();
				}
			}
		});
	}

	private void actualizarEtiquetaUsuario() {
		this.lblInfoUsuario.setText("Usuario: " + this.nombreUsuario + " (" + this.rolUsuario
				+ ") | Multa: $" + this.multaUsuario);
	}

	private void configurarPermisosPorRol(String rol) {
		int indiceAdmin;

		if (rol != null && rol.equals("ADMINISTRADOR")) {
			indiceAdmin = this.tabbedPane.indexOfComponent(this.panelAdmin);
			if (indiceAdmin == -1) {
				this.tabbedPane.addTab("Administracion", null, this.panelAdmin, null);
			}
			this.lblSumarStock.setVisible(true);
			this.txtSumarStock.setVisible(true);
			this.btnActualizarStock.setVisible(true);
		} else {
			indiceAdmin = this.tabbedPane.indexOfComponent(this.panelAdmin);
			if (indiceAdmin != -1) {
				this.tabbedPane.removeTabAt(indiceAdmin);
			}
			this.lblSumarStock.setVisible(false);
			this.txtSumarStock.setVisible(false);
			this.btnActualizarStock.setVisible(false);
		}
	}

	@Override
	public void iniciarSesionExitosa(String id, String nombre, String rol, double multa, int limitePrestamos) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				nombreUsuario = nombre;
				rolUsuario = rol;
				multaUsuario = multa;

				actualizarEtiquetaUsuario();
				configurarPermisosPorRol(rol);
				txtBuscador.setText("");
				cbTipoFiltro.setSelectedIndex(0);
				txtDiasSimulados.setText("0");
				tabbedPane.setSelectedIndex(0);

				if (loginGUI != null) {
					loginGUI.limpiarCampos();
					loginGUI.setVisible(false);
				}

				setVisible(true);

				if (controlador != null) {
					controlador.cargarCatalogo();
					controlador.cargarMisPrestamos();
					if (rol.equals("ADMINISTRADOR")) {
						controlador.cargarUsuariosAdmin();
						controlador.cargarPrestamosAdmin();
					}
				}
			}
		});
	}

	@Override
	public void cerrarSesionExitosa() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				listaCatalogo.clear();
				listaCarrito.clear();
				listaPrestamos.clear();
				listaUsuariosAdmin.clear();
				listaPrestamosAdmin.clear();

				modeloCatalogo.setRowCount(0);
				modeloCarrito.setRowCount(0);
				modeloPrestamos.setRowCount(0);
				modeloUsuariosAdmin.setRowCount(0);
				modeloPrestamosAdmin.setRowCount(0);

				setVisible(false);
				if (loginGUI != null) {
					loginGUI.setVisible(true);
				}
			}
		});
	}

	@Override
	public void actualizarCatalogo(List<MaterialBibliografico> materiales) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MaterialBibliografico material;
				Object[] fila;

				listaCatalogo = materiales;
				modeloCatalogo.setRowCount(0);

				for (int ind = 0; ind < listaCatalogo.size(); ind++) {
					material = listaCatalogo.get(ind);
					fila = new Object[6];
					fila[0] = material.obtenerTipoMaterial();
					fila[1] = material.getTitulo();
					fila[2] = material.getAutor();
					fila[3] = material.obtenerDetalle();
					fila[4] = material.getEjemplaresDisponibles();
					fila[5] = material.getEjemplaresTotales();
					modeloCatalogo.addRow(fila);
				}
			}
		});
	}

	@Override
	public void actualizarCarrito(List<MaterialBibliografico> carrito) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MaterialBibliografico material;
				Object[] fila;

				listaCarrito = carrito;
				modeloCarrito.setRowCount(0);

				for (int ind = 0; ind < listaCarrito.size(); ind++) {
					material = listaCarrito.get(ind);
					fila = new Object[4];
					fila[0] = material.obtenerTipoMaterial();
					fila[1] = material.getTitulo();
					fila[2] = material.getAutor();
					fila[3] = material.obtenerDetalle();
					modeloCarrito.addRow(fila);
				}
			}
		});
	}

	@Override
	public void actualizarPrestamos(List<Prestamo> prestamos) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Prestamo prestamo;
				MaterialBibliografico material;
				LocalDate fechaDevolucion;
				String textoDevolucion;
				Object[] fila;

				listaPrestamos = prestamos;
				modeloPrestamos.setRowCount(0);

				for (int ind = 0; ind < listaPrestamos.size(); ind++) {
					prestamo = listaPrestamos.get(ind);
					material = prestamo.getMaterial();
					fechaDevolucion = prestamo.getFechaDevolucion();

					if (fechaDevolucion == null) {
						textoDevolucion = "-";
					} else {
						textoDevolucion = fechaDevolucion.toString();
					}

					fila = new Object[6];
					fila[0] = material.getTitulo();
					fila[1] = prestamo.getFechaPrestamo().toString();
					fila[2] = prestamo.getFechaLimite().toString();
					fila[3] = textoDevolucion;
					fila[4] = "$" + prestamo.getMoraGenerada();
					fila[5] = prestamo.getEstado().name();
					modeloPrestamos.addRow(fila);
				}
			}
		});
	}

	@Override
	public void actualizarPrestamosAdmin(List<Prestamo> prestamos) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Prestamo prestamo;
				Usuario usuario;
				MaterialBibliografico material;
				Object[] fila;

				listaPrestamosAdmin = prestamos;
				modeloPrestamosAdmin.setRowCount(0);

				for (int ind = 0; ind < listaPrestamosAdmin.size(); ind++) {
					prestamo = listaPrestamosAdmin.get(ind);
					usuario = prestamo.getUsuario();
					material = prestamo.getMaterial();

					fila = new Object[5];
					fila[0] = usuario.getNombre();
					fila[1] = material.getTitulo();
					fila[2] = prestamo.getFechaLimite().toString();
					fila[3] = "$" + prestamo.getMoraGenerada();
					fila[4] = prestamo.getEstado().name();
					modeloPrestamosAdmin.addRow(fila);
				}
			}
		});
	}

	@Override
	public void actualizarUsuariosAdmin(List<Usuario> usuarios) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Usuario usuario;
				Object[] fila;

				listaUsuariosAdmin = usuarios;
				modeloUsuariosAdmin.setRowCount(0);

				for (int ind = 0; ind < listaUsuariosAdmin.size(); ind++) {
					usuario = listaUsuariosAdmin.get(ind);
					fila = new Object[4];
					fila[0] = usuario.getNombre();
					fila[1] = usuario.getUsername();
					fila[2] = usuario.obtenerRol();
					fila[3] = "$" + usuario.getMulta();
					modeloUsuariosAdmin.addRow(fila);
				}
			}
		});
	}

	@Override
	public void actualizarMulta(double multaActual) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				multaUsuario = multaActual;
				actualizarEtiquetaUsuario();
			}
		});
	}

	@Override
	public void notificarOperacionExitosa(String operacion, String mensaje) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String tipoFiltro, textoFiltro;

				JOptionPane.showMessageDialog(ClienteGUI.this, mensaje);

				if (controlador == null) {
					return;
				}

				tipoFiltro = (String) cbTipoFiltro.getSelectedItem();
				textoFiltro = txtBuscador.getText();

				if (operacion.equals("PRESTAMO")) {
					controlador.vaciarCarrito();
					controlador.buscarCatalogo(tipoFiltro, textoFiltro);
					controlador.cargarMisPrestamos();
					if (rolUsuario != null && rolUsuario.equals("ADMINISTRADOR")) {
						controlador.cargarPrestamosAdmin();
					}
				} else if (operacion.equals("DEVOLUCION")) {
					txtDiasSimulados.setText("0");
					controlador.buscarCatalogo(tipoFiltro, textoFiltro);
					controlador.cargarMisPrestamos();
					if (rolUsuario != null && rolUsuario.equals("ADMINISTRADOR")) {
						controlador.cargarPrestamosAdmin();
						controlador.cargarUsuariosAdmin();
					}
				} else if (operacion.equals("MATERIAL")) {
					txtTitulo.setText("");
					txtAutor.setText("");
					txtCantidad.setText("");
					txtDatoExtra.setText("");
					controlador.buscarCatalogo(tipoFiltro, textoFiltro);
				} else if (operacion.equals("STOCK")) {
					txtSumarStock.setText("");
					controlador.buscarCatalogo(tipoFiltro, textoFiltro);
				} else if (operacion.equals("MULTA")) {
					txtMontoMulta.setText("");
					controlador.cargarUsuariosAdmin();
				}
			}
		});
	}

	@Override
	public void mostrarError(String mensaje) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (isVisible()) {
					JOptionPane.showMessageDialog(ClienteGUI.this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
				} else if (loginGUI != null && loginGUI.isVisible()) {
					JOptionPane.showMessageDialog(loginGUI, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

}