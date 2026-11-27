package interfaz;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import cliente.ControladorBiblioteca;

public class LoginGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtIp;
	private JTextField txtPuerto;
	private JTextField txtUsuario;
	private JPasswordField txtPassword;
	private JButton btnIngresar;
	private ControladorBiblioteca controlador;

	public LoginGUI() {
		setTitle("Iniciar Sesion - Biblioteca Universitaria");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 440, 385);
		setResizable(false);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(244, 246, 249));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panelEncabezado = new JPanel();
		panelEncabezado.setBackground(new Color(28, 57, 94));
		panelEncabezado.setBounds(0, 0, 440, 56);
		panelEncabezado.setLayout(null);
		contentPane.add(panelEncabezado);

		JLabel lblTitulo = new JLabel("Biblioteca Universitaria");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTitulo.setBounds(20, 14, 380, 28);
		panelEncabezado.add(lblTitulo);

		JPanel panelFormulario = new JPanel();
		panelFormulario.setBackground(Color.WHITE);
		panelFormulario.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		panelFormulario.setBounds(20, 76, 384, 248);
		panelFormulario.setLayout(null);
		contentPane.add(panelFormulario);

		JLabel lblIp = new JLabel("IP Servidor:");
		lblIp.setForeground(new Color(74, 85, 104));
		lblIp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblIp.setBounds(20, 20, 100, 28);
		panelFormulario.add(lblIp);

		txtIp = new JTextField();
		txtIp.setText("127.0.0.1");
		txtIp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtIp.setForeground(new Color(33, 43, 54));
		txtIp.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtIp.setBounds(132, 20, 232, 28);
		panelFormulario.add(txtIp);
		txtIp.setColumns(10);

		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setForeground(new Color(74, 85, 104));
		lblPuerto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblPuerto.setBounds(20, 62, 100, 28);
		panelFormulario.add(lblPuerto);

		txtPuerto = new JTextField();
		txtPuerto.setText("7777");
		txtPuerto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtPuerto.setForeground(new Color(33, 43, 54));
		txtPuerto.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtPuerto.setBounds(132, 62, 232, 28);
		panelFormulario.add(txtPuerto);
		txtPuerto.setColumns(10);

		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setForeground(new Color(74, 85, 104));
		lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblUsuario.setBounds(20, 104, 100, 28);
		panelFormulario.add(lblUsuario);

		txtUsuario = new JTextField();
		txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtUsuario.setForeground(new Color(33, 43, 54));
		txtUsuario.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtUsuario.setBounds(132, 104, 232, 28);
		panelFormulario.add(txtUsuario);
		txtUsuario.setColumns(10);

		JLabel lblPassword = new JLabel("Contrasena:");
		lblPassword.setForeground(new Color(74, 85, 104));
		lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblPassword.setBounds(20, 146, 100, 28);
		panelFormulario.add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtPassword.setForeground(new Color(33, 43, 54));
		txtPassword.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtPassword.setBounds(132, 146, 232, 28);
		panelFormulario.add(txtPassword);

		btnIngresar = new JButton("Ingresar");
		btnIngresar.setBackground(new Color(28, 78, 128));
		btnIngresar.setForeground(Color.WHITE);
		btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 13));
		btnIngresar.setFocusPainted(false);
		btnIngresar.setOpaque(true);
		btnIngresar.setBorder(new LineBorder(new Color(20, 60, 100), 1));
		btnIngresar.setBounds(112, 194, 160, 34);
		panelFormulario.add(btnIngresar);

		configurarEventos();
	}

	public void setControlador(ControladorBiblioteca controlador) {
		this.controlador = controlador;
	}

	public void limpiarCampos() {
		this.txtUsuario.setText("");
		this.txtPassword.setText("");
	}

	private void configurarEventos() {
		this.btnIngresar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ip, puerto, usuario, password;

				if (controlador == null) {
					return;
				}

				ip = txtIp.getText();
				puerto = txtPuerto.getText();
				usuario = txtUsuario.getText();
				password = new String(txtPassword.getPassword());

				controlador.iniciarSesion(ip, puerto, usuario, password);
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

}