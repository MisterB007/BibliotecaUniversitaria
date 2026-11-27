package interfaz;

import java.awt.Dimension;
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
import javax.swing.border.EmptyBorder;

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
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setSize(new Dimension(380, 260));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblIp = new JLabel("IP Servidor:");
		lblIp.setBounds(94, 46, 78, 12);
		contentPane.add(lblIp);

		txtIp = new JTextField();
		txtIp.setText("127.0.0.1");
		txtIp.setBounds(182, 43, 96, 18);
		contentPane.add(txtIp);
		txtIp.setColumns(10);

		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setBounds(94, 74, 44, 12);
		contentPane.add(lblPuerto);

		txtPuerto = new JTextField();
		txtPuerto.setText("7777");
		txtPuerto.setBounds(182, 71, 96, 18);
		contentPane.add(txtPuerto);
		txtPuerto.setColumns(10);

		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setBounds(94, 102, 60, 12);
		contentPane.add(lblUsuario);

		txtUsuario = new JTextField();
		txtUsuario.setBounds(182, 99, 96, 18);
		contentPane.add(txtUsuario);
		txtUsuario.setColumns(10);

		JLabel lblPassword = new JLabel("Contrasena:");
		lblPassword.setBounds(94, 136, 84, 12);
		contentPane.add(lblPassword);

		txtPassword = new JPasswordField();
		txtPassword.setBounds(182, 133, 96, 18);
		contentPane.add(txtPassword);

		btnIngresar = new JButton("Ingresar");
		btnIngresar.setBounds(178, 181, 84, 20);
		contentPane.add(btnIngresar);

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