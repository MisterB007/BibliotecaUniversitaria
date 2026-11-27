package cliente;

import javax.swing.SwingUtilities;

import interfaz.ClienteGUI;
import interfaz.LoginGUI;

public class ClienteMain {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				LoginGUI loginGUI;
				ClienteGUI clienteGUI;
				Cliente cliente;
				ControladorBiblioteca controlador;

				loginGUI = new LoginGUI();
				clienteGUI = new ClienteGUI();
				cliente = new Cliente("127.0.0.1", 7777, clienteGUI);
				controlador = new ControladorBiblioteca(clienteGUI, cliente);

				loginGUI.setControlador(controlador);
				clienteGUI.setControlador(controlador);
				clienteGUI.setLoginGUI(loginGUI);

				loginGUI.setVisible(true);
			}
		});
	}

}