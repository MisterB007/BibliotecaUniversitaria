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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import servidor.Servidor;
import servidor.ServidorObserver;

public class ServidorGUI extends JFrame implements ServidorObserver {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField txtPuerto;
	private JButton btnIniciar;
	private JButton btnDetener;
	private JTextArea areaEventos;
	private Servidor servidor;

	public ServidorGUI() {
		setTitle("Servidor - Biblioteca Universitaria");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 460);
		setResizable(false);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(244, 246, 249));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panelEncabezado = new JPanel();
		panelEncabezado.setBackground(new Color(28, 57, 94));
		panelEncabezado.setBounds(0, 0, 640, 56);
		panelEncabezado.setLayout(null);
		contentPane.add(panelEncabezado);

		JLabel lblTitulo = new JLabel("Servidor - Biblioteca Universitaria");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTitulo.setBounds(20, 14, 580, 28);
		panelEncabezado.add(lblTitulo);

		JPanel panelControl = new JPanel();
		panelControl.setBackground(Color.WHITE);
		panelControl.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		panelControl.setBounds(20, 72, 584, 60);
		panelControl.setLayout(null);
		contentPane.add(panelControl);

		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setForeground(new Color(74, 85, 104));
		lblPuerto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblPuerto.setBounds(18, 16, 60, 28);
		panelControl.add(lblPuerto);

		txtPuerto = new JTextField();
		txtPuerto.setText("7777");
		txtPuerto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtPuerto.setForeground(new Color(33, 43, 54));
		txtPuerto.setBorder(new CompoundBorder(new LineBorder(new Color(195, 205, 218), 1), new EmptyBorder(2, 8, 2, 8)));
		txtPuerto.setBounds(84, 16, 130, 28);
		panelControl.add(txtPuerto);
		txtPuerto.setColumns(10);

		btnIniciar = new JButton("Iniciar");
		btnIniciar.setBackground(new Color(28, 78, 128));
		btnIniciar.setForeground(Color.WHITE);
		btnIniciar.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnIniciar.setFocusPainted(false);
		btnIniciar.setOpaque(true);
		btnIniciar.setBorder(new LineBorder(new Color(20, 60, 100), 1));
		btnIniciar.setBounds(324, 15, 115, 30);
		panelControl.add(btnIniciar);

		btnDetener = new JButton("Detener");
		btnDetener.setEnabled(false);
		btnDetener.setBackground(new Color(185, 55, 55));
		btnDetener.setForeground(Color.WHITE);
		btnDetener.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnDetener.setFocusPainted(false);
		btnDetener.setOpaque(true);
		btnDetener.setBorder(new LineBorder(new Color(155, 42, 42), 1));
		btnDetener.setBounds(451, 15, 115, 30);
		panelControl.add(btnDetener);

		JPanel panelEventos = new JPanel();
		panelEventos.setBackground(Color.WHITE);
		panelEventos.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		panelEventos.setBounds(20, 148, 584, 253);
		panelEventos.setLayout(null);
		contentPane.add(panelEventos);

		JLabel lblEventos = new JLabel("Registro de Eventos");
		lblEventos.setForeground(new Color(33, 43, 54));
		lblEventos.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblEventos.setBounds(16, 12, 220, 22);
		panelEventos.add(lblEventos);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new LineBorder(new Color(218, 224, 232), 1));
		scrollPane.setBounds(16, 40, 552, 197);
		panelEventos.add(scrollPane);

		areaEventos = new JTextArea();
		areaEventos.setEditable(false);
		areaEventos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		areaEventos.setForeground(new Color(33, 43, 54));
		areaEventos.setBackground(new Color(250, 251, 253));
		areaEventos.setBorder(new EmptyBorder(6, 8, 6, 8));
		scrollPane.setViewportView(areaEventos);

		configurarEventos();
	}

	private void configurarEventos() {

		this.btnIniciar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String puertoTexto;
				int puerto;

				puertoTexto = txtPuerto.getText().trim();

				if (puertoTexto.isEmpty()) {
					JOptionPane.showMessageDialog(null, "Debe ingresar un numero de puerto.");
					return;
				}

				try {
					puerto = Integer.parseInt(puertoTexto);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "El puerto debe ser un numero entero valido.");
					return;
				}

				if (puerto < 1024 || puerto > 65535) {
					JOptionPane.showMessageDialog(null, "El puerto debe estar entre 1024 y 65535.");
					return;
				}

				servidor = new Servidor(puerto, ServidorGUI.this);
				servidor.iniciarServidor();

				btnIniciar.setEnabled(false);
				btnDetener.setEnabled(true);
				txtPuerto.setEditable(false);
			}
		});

		this.btnDetener.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (servidor != null) {
					servidor.detenerServidor();
					servidor = null;
				}

				btnIniciar.setEnabled(true);
				btnDetener.setEnabled(false);
				txtPuerto.setEditable(true);
			}
		});

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (servidor != null) {
					servidor.detenerServidor();
					servidor = null;
				}
			}
		});
	}

	@Override
	public void notificarEvento(String mensaje) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				areaEventos.append(mensaje + "\n");
			}
		});
	}

}