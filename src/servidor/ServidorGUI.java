package servidor;

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
import javax.swing.border.EmptyBorder;

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
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtPuerto = new JTextField();
		txtPuerto.setText("7777");
		txtPuerto.setBounds(64, 10, 96, 18);
		contentPane.add(txtPuerto);
		txtPuerto.setColumns(10);

		btnIniciar = new JButton("Iniciar");
		btnIniciar.setBounds(170, 9, 84, 20);
		contentPane.add(btnIniciar);

		btnDetener = new JButton("Detener");
		btnDetener.setEnabled(false);
		btnDetener.setBounds(264, 9, 84, 20);
		contentPane.add(btnDetener);

		JLabel lblPuerto = new JLabel("Puerto:");
		lblPuerto.setBounds(10, 13, 44, 12);
		contentPane.add(lblPuerto);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 56, 395, 179);
		contentPane.add(scrollPane);

		areaEventos = new JTextArea();
		areaEventos.setEditable(false);
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