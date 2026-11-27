package interfaz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import clases.EstadoPrestamo;
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 960, 660);
		setResizable(false);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(244, 246, 249));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panelEncabezado = new JPanel();
		panelEncabezado.setBackground(new Color(28, 57, 94));
		panelEncabezado.setBounds(0, 0, 960, 64);
		panelEncabezado.setLayout(null);
		contentPane.add(panelEncabezado);

		JLabel lblTituloApp = new JLabel("Biblioteca Universitaria");
		lblTituloApp.setForeground(Color.WHITE);
		lblTituloApp.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTituloApp.setBounds(20, 10, 320, 24);
		panelEncabezado.add(lblTituloApp);

		lblInfoUsuario = new JLabel("Usuario: - | Multa: $0.0");
		lblInfoUsuario.setForeground(new Color(218, 228, 240));
		lblInfoUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblInfoUsuario.setBounds(20, 35, 720, 20);
		panelEncabezado.add(lblInfoUsuario);

		btnCerrarSesion = new JButton("Cerrar Sesion");
		btnCerrarSesion.setBackground(new Color(185, 55, 55));
		btnCerrarSesion.setForeground(Color.WHITE);
		btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnCerrarSesion.setFocusPainted(false);
		btnCerrarSesion.setOpaque(true);
		btnCerrarSesion.setBorder(new LineBorder(new Color(155, 42, 42), 1));
		btnCerrarSesion.setBounds(794, 17, 130, 30);
		panelEncabezado.add(btnCerrarSesion);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setForeground(new Color(28, 57, 94));
		tabbedPane.setBounds(20, 80, 904, 521);
		contentPane.add(tabbedPane);

		panelCatalogo = new JPanel();
		panelCatalogo.setBackground(Color.WHITE);
		tabbedPane.addTab("Catalogo", null, panelCatalogo, null);
		panelCatalogo.setLayout(null);

		JLabel lblBuscar = new JLabel("Buscar:");
		lblBuscar.setForeground(new Color(74, 85, 104));
		lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblBuscar.setBounds(18, 17, 55, 28);
		panelCatalogo.add(lblBuscar);

		txtBuscador = new JTextField();
		txtBuscador.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtBuscador.setForeground(new Color(33, 43, 54));
		txtBuscador.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtBuscador.setBounds(78, 17, 360, 28);
		panelCatalogo.add(txtBuscador);
		txtBuscador.setColumns(10);

		JLabel lblTipoFiltro = new JLabel("Tipo:");
		lblTipoFiltro.setForeground(new Color(74, 85, 104));
		lblTipoFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblTipoFiltro.setBounds(460, 17, 42, 28);
		panelCatalogo.add(lblTipoFiltro);

		cbTipoFiltro = new JComboBox<String>();
		cbTipoFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cbTipoFiltro.setBackground(Color.WHITE);
		cbTipoFiltro.setForeground(new Color(33, 43, 54));
		cbTipoFiltro.setBounds(506, 17, 150, 28);
		panelCatalogo.add(cbTipoFiltro);

		btnActualizarCatalogo = new JButton("Actualizar");
		btnActualizarCatalogo.setBackground(new Color(235, 240, 246));
		btnActualizarCatalogo.setForeground(new Color(33, 43, 54));
		btnActualizarCatalogo.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnActualizarCatalogo.setFocusPainted(false);
		btnActualizarCatalogo.setOpaque(true);
		btnActualizarCatalogo.setBorder(new LineBorder(new Color(195, 205, 218), 1));
		btnActualizarCatalogo.setBounds(750, 16, 130, 30);
		panelCatalogo.add(btnActualizarCatalogo);

		JScrollPane scrollCatalogo = new JScrollPane();
		scrollCatalogo.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		scrollCatalogo.setBounds(18, 60, 862, 362);
		panelCatalogo.add(scrollCatalogo);

		tablaCatalogo = new JTable();
		scrollCatalogo.setViewportView(tablaCatalogo);

		btnAgregarCarrito = new JButton("Agregar al Carrito");
		btnAgregarCarrito.setBackground(new Color(28, 78, 128));
		btnAgregarCarrito.setForeground(Color.WHITE);
		btnAgregarCarrito.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnAgregarCarrito.setFocusPainted(false);
		btnAgregarCarrito.setOpaque(true);
		btnAgregarCarrito.setBorder(new LineBorder(new Color(20, 60, 100), 1));
		btnAgregarCarrito.setBounds(18, 438, 170, 32);
		panelCatalogo.add(btnAgregarCarrito);

		lblSumarStock = new JLabel("Sumar Stock:");
		lblSumarStock.setForeground(new Color(74, 85, 104));
		lblSumarStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblSumarStock.setBounds(524, 440, 90, 28);
		panelCatalogo.add(lblSumarStock);

		txtSumarStock = new JTextField();
		txtSumarStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtSumarStock.setForeground(new Color(33, 43, 54));
		txtSumarStock.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtSumarStock.setBounds(620, 440, 90, 28);
		panelCatalogo.add(txtSumarStock);
		txtSumarStock.setColumns(10);

		btnActualizarStock = new JButton("Actualizar Stock");
		btnActualizarStock.setBackground(new Color(28, 78, 128));
		btnActualizarStock.setForeground(Color.WHITE);
		btnActualizarStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnActualizarStock.setFocusPainted(false);
		btnActualizarStock.setOpaque(true);
		btnActualizarStock.setBorder(new LineBorder(new Color(20, 60, 100), 1));
		btnActualizarStock.setBounds(720, 438, 160, 32);
		panelCatalogo.add(btnActualizarStock);

		panelCarrito = new JPanel();
		panelCarrito.setBackground(Color.WHITE);
		tabbedPane.addTab("Carrito", null, panelCarrito, null);
		panelCarrito.setLayout(null);

		JLabel lblTituloCarrito = new JLabel("Materiales seleccionados para prestamo");
		lblTituloCarrito.setForeground(new Color(28, 57, 94));
		lblTituloCarrito.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblTituloCarrito.setBounds(18, 16, 400, 24);
		panelCarrito.add(lblTituloCarrito);

		JScrollPane scrollCarrito = new JScrollPane();
		scrollCarrito.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		scrollCarrito.setBounds(18, 48, 862, 374);
		panelCarrito.add(scrollCarrito);

		tablaCarrito = new JTable();
		scrollCarrito.setViewportView(tablaCarrito);

		btnQuitarCarrito = new JButton("Quitar Seleccionado");
		btnQuitarCarrito.setBackground(new Color(235, 240, 246));
		btnQuitarCarrito.setForeground(new Color(33, 43, 54));
		btnQuitarCarrito.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnQuitarCarrito.setFocusPainted(false);
		btnQuitarCarrito.setOpaque(true);
		btnQuitarCarrito.setBorder(new LineBorder(new Color(195, 205, 218), 1));
		btnQuitarCarrito.setBounds(18, 438, 170, 32);
		panelCarrito.add(btnQuitarCarrito);

		btnVaciarCarrito = new JButton("Vaciar Carrito");
		btnVaciarCarrito.setBackground(new Color(235, 240, 246));
		btnVaciarCarrito.setForeground(new Color(33, 43, 54));
		btnVaciarCarrito.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnVaciarCarrito.setFocusPainted(false);
		btnVaciarCarrito.setOpaque(true);
		btnVaciarCarrito.setBorder(new LineBorder(new Color(195, 205, 218), 1));
		btnVaciarCarrito.setBounds(200, 438, 140, 32);
		panelCarrito.add(btnVaciarCarrito);

		btnConfirmarPrestamo = new JButton("Confirmar Prestamo");
		btnConfirmarPrestamo.setBackground(new Color(28, 78, 128));
		btnConfirmarPrestamo.setForeground(Color.WHITE);
		btnConfirmarPrestamo.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnConfirmarPrestamo.setFocusPainted(false);
		btnConfirmarPrestamo.setOpaque(true);
		btnConfirmarPrestamo.setBorder(new LineBorder(new Color(20, 60, 100), 1));
		btnConfirmarPrestamo.setBounds(700, 438, 180, 32);
		panelCarrito.add(btnConfirmarPrestamo);

		panelPrestamos = new JPanel();
		panelPrestamos.setBackground(Color.WHITE);
		tabbedPane.addTab("Mis Prestamos", null, panelPrestamos, null);
		panelPrestamos.setLayout(null);

		JLabel lblTituloPrestamos = new JLabel("Historial de prestamos del usuario");
		lblTituloPrestamos.setForeground(new Color(28, 57, 94));
		lblTituloPrestamos.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblTituloPrestamos.setBounds(18, 16, 400, 24);
		panelPrestamos.add(lblTituloPrestamos);

		JScrollPane scrollPrestamos = new JScrollPane();
		scrollPrestamos.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		scrollPrestamos.setBounds(18, 48, 862, 374);
		panelPrestamos.add(scrollPrestamos);

		tablaPrestamos = new JTable();
		scrollPrestamos.setViewportView(tablaPrestamos);

		JLabel lblDiasSimulados = new JLabel("Dias simulados:");
		lblDiasSimulados.setForeground(new Color(74, 85, 104));
		lblDiasSimulados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblDiasSimulados.setBounds(18, 440, 105, 28);
		panelPrestamos.add(lblDiasSimulados);

		txtDiasSimulados = new JTextField();
		txtDiasSimulados.setText("0");
		txtDiasSimulados.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtDiasSimulados.setForeground(new Color(33, 43, 54));
		txtDiasSimulados.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtDiasSimulados.setBounds(128, 440, 90, 28);
		panelPrestamos.add(txtDiasSimulados);
		txtDiasSimulados.setColumns(10);

		btnDevolver = new JButton("Devolver Material");
		btnDevolver.setBackground(new Color(28, 78, 128));
		btnDevolver.setForeground(Color.WHITE);
		btnDevolver.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnDevolver.setFocusPainted(false);
		btnDevolver.setOpaque(true);
		btnDevolver.setBorder(new LineBorder(new Color(20, 60, 100), 1));
		btnDevolver.setBounds(232, 438, 170, 32);
		panelPrestamos.add(btnDevolver);

		panelAdmin = new JPanel();
		panelAdmin.setBackground(new Color(244, 246, 249));
		tabbedPane.addTab("Administracion", null, panelAdmin, null);
		panelAdmin.setLayout(null);

		JPanel panelNuevoMaterial = new JPanel();
		panelNuevoMaterial.setBackground(Color.WHITE);
		panelNuevoMaterial.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		panelNuevoMaterial.setBounds(16, 14, 330, 250);
		panelNuevoMaterial.setLayout(null);
		panelAdmin.add(panelNuevoMaterial);

		JLabel lblNuevoMaterial = new JLabel("Agregar Nuevo Material");
		lblNuevoMaterial.setForeground(new Color(28, 57, 94));
		lblNuevoMaterial.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblNuevoMaterial.setBounds(16, 12, 298, 22);
		panelNuevoMaterial.add(lblNuevoMaterial);

		JLabel lblTipoMaterial = new JLabel("Tipo:");
		lblTipoMaterial.setForeground(new Color(74, 85, 104));
		lblTipoMaterial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblTipoMaterial.setBounds(16, 42, 78, 26);
		panelNuevoMaterial.add(lblTipoMaterial);

		cbTipoMaterial = new JComboBox<String>();
		cbTipoMaterial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		cbTipoMaterial.setBackground(Color.WHITE);
		cbTipoMaterial.setForeground(new Color(33, 43, 54));
		cbTipoMaterial.setBounds(98, 42, 216, 26);
		panelNuevoMaterial.add(cbTipoMaterial);

		JLabel lblTitulo = new JLabel("Titulo:");
		lblTitulo.setForeground(new Color(74, 85, 104));
		lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblTitulo.setBounds(16, 76, 78, 26);
		panelNuevoMaterial.add(lblTitulo);

		txtTitulo = new JTextField();
		txtTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtTitulo.setForeground(new Color(33, 43, 54));
		txtTitulo.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtTitulo.setBounds(98, 76, 216, 26);
		panelNuevoMaterial.add(txtTitulo);
		txtTitulo.setColumns(10);

		JLabel lblAutor = new JLabel("Autor:");
		lblAutor.setForeground(new Color(74, 85, 104));
		lblAutor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblAutor.setBounds(16, 110, 78, 26);
		panelNuevoMaterial.add(lblAutor);

		txtAutor = new JTextField();
		txtAutor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtAutor.setForeground(new Color(33, 43, 54));
		txtAutor.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtAutor.setColumns(10);
		txtAutor.setBounds(98, 110, 216, 26);
		panelNuevoMaterial.add(txtAutor);

		JLabel lblCantidad = new JLabel("Cantidad:");
		lblCantidad.setForeground(new Color(74, 85, 104));
		lblCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblCantidad.setBounds(16, 144, 78, 26);
		panelNuevoMaterial.add(lblCantidad);

		txtCantidad = new JTextField();
		txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtCantidad.setForeground(new Color(33, 43, 54));
		txtCantidad.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtCantidad.setColumns(10);
		txtCantidad.setBounds(98, 144, 216, 26);
		panelNuevoMaterial.add(txtCantidad);

		lblDatoExtra = new JLabel("Editorial:");
		lblDatoExtra.setForeground(new Color(74, 85, 104));
		lblDatoExtra.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblDatoExtra.setBounds(16, 178, 78, 26);
		panelNuevoMaterial.add(lblDatoExtra);

		txtDatoExtra = new JTextField();
		txtDatoExtra.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtDatoExtra.setForeground(new Color(33, 43, 54));
		txtDatoExtra.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtDatoExtra.setColumns(10);
		txtDatoExtra.setBounds(98, 178, 216, 26);
		panelNuevoMaterial.add(txtDatoExtra);

		btnAgregarMaterial = new JButton("Agregar Material");
		btnAgregarMaterial.setBackground(new Color(28, 78, 128));
		btnAgregarMaterial.setForeground(Color.WHITE);
		btnAgregarMaterial.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnAgregarMaterial.setFocusPainted(false);
		btnAgregarMaterial.setOpaque(true);
		btnAgregarMaterial.setBorder(new LineBorder(new Color(20, 60, 100), 1));
		btnAgregarMaterial.setBounds(98, 210, 216, 28);
		panelNuevoMaterial.add(btnAgregarMaterial);

		JPanel panelUsuariosCard = new JPanel();
		panelUsuariosCard.setBackground(Color.WHITE);
		panelUsuariosCard.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		panelUsuariosCard.setBounds(360, 14, 522, 250);
		panelUsuariosCard.setLayout(null);
		panelAdmin.add(panelUsuariosCard);

		JLabel lblUsuarios = new JLabel("Usuarios y Multas");
		lblUsuarios.setForeground(new Color(28, 57, 94));
		lblUsuarios.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblUsuarios.setBounds(16, 12, 250, 22);
		panelUsuariosCard.add(lblUsuarios);

		JScrollPane scrollUsuariosAdmin = new JScrollPane();
		scrollUsuariosAdmin.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		scrollUsuariosAdmin.setBounds(16, 40, 490, 158);
		panelUsuariosCard.add(scrollUsuariosAdmin);

		tablaUsuariosAdmin = new JTable();
		scrollUsuariosAdmin.setViewportView(tablaUsuariosAdmin);

		JLabel lblMontoMulta = new JLabel("Monto:");
		lblMontoMulta.setForeground(new Color(74, 85, 104));
		lblMontoMulta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblMontoMulta.setBounds(16, 210, 55, 26);
		panelUsuariosCard.add(lblMontoMulta);

		txtMontoMulta = new JTextField();
		txtMontoMulta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtMontoMulta.setForeground(new Color(33, 43, 54));
		txtMontoMulta.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtMontoMulta.setBounds(76, 210, 130, 26);
		panelUsuariosCard.add(txtMontoMulta);
		txtMontoMulta.setColumns(10);

		btnPagarMulta = new JButton("Pagar Multa");
		btnPagarMulta.setBackground(new Color(28, 78, 128));
		btnPagarMulta.setForeground(Color.WHITE);
		btnPagarMulta.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnPagarMulta.setFocusPainted(false);
		btnPagarMulta.setOpaque(true);
		btnPagarMulta.setBorder(new LineBorder(new Color(20, 60, 100), 1));
		btnPagarMulta.setBounds(366, 208, 140, 30);
		panelUsuariosCard.add(btnPagarMulta);

		JPanel panelPrestamosCard = new JPanel();
		panelPrestamosCard.setBackground(Color.WHITE);
		panelPrestamosCard.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		panelPrestamosCard.setBounds(16, 278, 866, 198);
		panelPrestamosCard.setLayout(null);
		panelAdmin.add(panelPrestamosCard);

		JLabel lblPrestamosAdmin = new JLabel("Todos los Prestamos");
		lblPrestamosAdmin.setForeground(new Color(28, 57, 94));
		lblPrestamosAdmin.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblPrestamosAdmin.setBounds(16, 12, 250, 24);
		panelPrestamosCard.add(lblPrestamosAdmin);

		btnActualizarAdmin = new JButton("Actualizar Datos");
		btnActualizarAdmin.setBackground(new Color(235, 240, 246));
		btnActualizarAdmin.setForeground(new Color(33, 43, 54));
		btnActualizarAdmin.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnActualizarAdmin.setFocusPainted(false);
		btnActualizarAdmin.setOpaque(true);
		btnActualizarAdmin.setBorder(new LineBorder(new Color(195, 205, 218), 1));
		btnActualizarAdmin.setBounds(700, 10, 150, 28);
		panelPrestamosCard.add(btnActualizarAdmin);

		JScrollPane scrollPrestamosAdmin = new JScrollPane();
		scrollPrestamosAdmin.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		scrollPrestamosAdmin.setBounds(16, 44, 834, 140);
		panelPrestamosCard.add(scrollPrestamosAdmin);

		tablaPrestamosAdmin = new JTable();
		scrollPrestamosAdmin.setViewportView(tablaPrestamosAdmin);

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
		JTable[] tablas = { this.tablaCatalogo, this.tablaCarrito, this.tablaPrestamos, this.tablaUsuariosAdmin, this.tablaPrestamosAdmin };
		JTable tablaActual;
		JTableHeader encabezado;

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

		for (int ind = 0; ind < tablas.length; ind++) {
			tablaActual = tablas[ind];
			tablaActual.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			tablaActual.setRowHeight(25);
			tablaActual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			tablaActual.setForeground(new Color(33, 43, 54));
			tablaActual.setGridColor(new Color(230, 235, 240));
			tablaActual.setSelectionBackground(new Color(220, 234, 248));
			tablaActual.setSelectionForeground(new Color(28, 57, 94));

			encabezado = tablaActual.getTableHeader();
			encabezado.setBackground(new Color(235, 240, 246));
			encabezado.setForeground(new Color(28, 57, 94));
			encabezado.setFont(new Font("Segoe UI", Font.BOLD, 12));
			encabezado.setPreferredSize(new Dimension(0, 28));
			encabezado.setReorderingAllowed(false);
		}
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
	public void iniciarSesionExitosa(String nombre, String rol, double multa) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				nombreUsuario = nombre;
				rolUsuario = rol;
				multaUsuario = multa;

				lblInfoUsuario.setText("Usuario: " + nombreUsuario + " (" + rolUsuario + ") | Multa: $" + multaUsuario);
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
				LocalDate fechaPrestamo, fechaLimite, fechaDevolucion;
				EstadoPrestamo estadoActual;
				String textoDevolucion;
				Object[] fila;

				listaPrestamos = prestamos;
				modeloPrestamos.setRowCount(0);

				for (int ind = 0; ind < listaPrestamos.size(); ind++) {
					prestamo = listaPrestamos.get(ind);
					material = prestamo.getMaterial();
					fechaPrestamo = prestamo.getFechaPrestamo();
					fechaLimite = prestamo.getFechaLimite();
					fechaDevolucion = prestamo.getFechaDevolucion();
					estadoActual = prestamo.getEstado();

					if (fechaDevolucion == null) {
						textoDevolucion = "-";
					} else {
						textoDevolucion = fechaDevolucion.toString();
					}

					fila = new Object[6];
					fila[0] = material.getTitulo();
					fila[1] = fechaPrestamo.toString();
					fila[2] = fechaLimite.toString();
					fila[3] = textoDevolucion;
					fila[4] = "$" + prestamo.getMoraGenerada();
					fila[5] = estadoActual.name();
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
				LocalDate fechaLimite;
				EstadoPrestamo estadoActual;
				Object[] fila;

				listaPrestamosAdmin = prestamos;
				modeloPrestamosAdmin.setRowCount(0);

				for (int ind = 0; ind < listaPrestamosAdmin.size(); ind++) {
					prestamo = listaPrestamosAdmin.get(ind);
					usuario = prestamo.getUsuario();
					material = prestamo.getMaterial();
					fechaLimite = prestamo.getFechaLimite();
					estadoActual = prestamo.getEstado();

					fila = new Object[5];
					fila[0] = usuario.getNombre();
					fila[1] = material.getTitulo();
					fila[2] = fechaLimite.toString();
					fila[3] = "$" + prestamo.getMoraGenerada();
					fila[4] = estadoActual.name();
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
				lblInfoUsuario.setText("Usuario: " + nombreUsuario + " (" + rolUsuario + ") | Multa: $" + multaUsuario);
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