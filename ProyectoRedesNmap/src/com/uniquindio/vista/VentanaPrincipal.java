package com.uniquindio.vista;

import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.JFrame;
import com.uniquindio.Nmap;
import javax.swing.JTabbedPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VentanaPrincipal {
	JList<String> list;
	JPanel panel;
	public Nmap logica;

	private JFrame frame;
	// Campo donde se ingresa la direccion ip para escanear sus puertos
	private JTextField textField;
	private JTextField textField_1;
	private JTable jTable1;
	// Este combobox contiene una lista de las tarjetas de red disponibles
	JComboBox cbSeleccionarTarjeta;
	private JTable jtPuertos;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VentanaPrincipal window = new VentanaPrincipal();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public VentanaPrincipal() {
		logica = new Nmap();

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 728, 514);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 702, 464);
		frame.getContentPane().add(tabbedPane);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Lista de equipos", null, panel_1, null);
		panel_1.setLayout(null);

		JLabel lbTitulo = new JLabel("Interfaces de red");
		lbTitulo.setBounds(40, 12, 200, 15);
		panel_1.add(lbTitulo);
		cbSeleccionarTarjeta = new JComboBox();
		cbSeleccionarTarjeta.setBounds(40, 38, 394, 30);
		panel_1.add(cbSeleccionarTarjeta);
		llenarComboBoxTarjetasRed();

		JButton btnIniciarEscaneo = new JButton("Iniciar Escaneo");
		btnIniciarEscaneo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				listarEquiposEnRed();
			}
		});
		btnIniciarEscaneo.setBounds(467, 120, 150, 40);
		panel_1.add(btnIniciarEscaneo);

		JButton btnInformacion = new JButton("Informacion");
		btnInformacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					int num = Integer.parseInt(cbSeleccionarTarjeta.getSelectedItem().toString().substring(0, 1));
					logica.obtenerInformacion(num);
					JOptionPane.showMessageDialog(null, logica.imprimirInformacion());
				} catch (HeadlessException | SocketException e) {
					JOptionPane.showMessageDialog(null, "la solicitud no se pudo procesar");
				}
			}
		});
		btnInformacion.setBounds(467, 55, 130, 40);
		panel_1.add(btnInformacion);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(40, 132, 394, 275);
		panel_1.add(scrollPane);

		jTable1 = new JTable();
		scrollPane.setViewportView(jTable1);

		JLabel label = new JLabel("Interfaces de red");
		label.setBounds(40, 12, 200, 15);
		panel_1.add(label);

		JLabel lblListaDeHost = new JLabel("Lista de host disponibles en la red");
		lblListaDeHost.setBounds(40, 105, 290, 15);
		panel_1.add(lblListaDeHost);

		panel = new JPanel();
		tabbedPane.addTab("Puertos disponibles", null, panel, null);
		panel.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(41, 81, 394, 275);
		panel.add(scrollPane_1);

		jtPuertos = new JTable();
		scrollPane_1.setViewportView(jtPuertos);

		JLabel lblDireccionIpDel = new JLabel("Direccion IP del host");
		lblDireccionIpDel.setBounds(20, 21, 118, 14);
		panel.add(lblDireccionIpDel);

		textField = new JTextField();
		textField.setBounds(144, 18, 154, 20);
		panel.add(textField);
		textField.setColumns(10);

		JButton btnEscanear = new JButton("Escanear");
		btnEscanear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				escanearPuertos();
			}
		});
		btnEscanear.setBounds(346, 21, 89, 23);
		panel.add(btnEscanear);

		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Servicios por puerto", null, panel_3, null);

	}

	/**
	 * mETODO PARA LLENAR LA TABLA CON las direcciones ip de LOS HOSTs QUE ESTAN
	 * DIPONIBLES EN LA RED
	 */
	public void listarEquiposEnRed() {

		// Enviar por parametro al metodo
		// para el boton escanear, se obtendria
		// luego se llama logica.obtener informacion(indicedelatarjeta) enviado
		// la tarjeta
		try {
			int num = Integer.parseInt(cbSeleccionarTarjeta.getSelectedItem().toString().substring(0, 1));
			logica.obtenerInformacion(num);
		} catch (NumberFormatException | SocketException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		// luego se coge la lista de hostlogica.
		// luego se llama el metodo logica.hostdiponibles
		logica.hostDisponibles();

		System.out.println(logica.getHostDisponible());
		llenarTablaHosts();
	}

	/**
	 * Este metodo se encarga de listar en un combobox todas las tarjetas de red
	 * disponibles
	 */
	public void llenarComboBoxTarjetasRed() {
		try {
			logica.listarTarjetas();

			int indice = 0;
			for (String tarjeta : logica.getInterfacesLista()) {
				cbSeleccionarTarjeta.addItem(indice + " - " + tarjeta);
				indice++;
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Metodo para llenar la tabla con las direccones ip disponibles
	 */
	public void llenarTablaHosts() {

		try {
			DefaultTableModel temp = (DefaultTableModel) jTable1.getModel();
		} catch (Exception e) {
			System.out.println(e);
		}

		DefaultTableModel modelo = new DefaultTableModel();

		// jTable1.removeAll();
		modelo.setRowCount(0);
		modelo.addColumn("Direccion IP");
		jTable1.setModel(modelo);
		ArrayList<String> datos = (ArrayList) logica.getHostDisponible();

		// LE PASO AL ARRAY LOS DATOS DEL ARRAYLIST
		Object[] fila = new Object[1];
		for (int i = 0; i < datos.size(); i++) {

			fila[0] = (datos.get(i));

			modelo.addRow(fila);

		}

	}

	/**
	 * Este metodo se encarga de listar en una tabla todos los puertos que estan
	 * disponibles para el numero de host ingresado en el campo de texto
	 */
	public void escanearPuertos() {
		String ip = textField.getText();

		if (Nmap.realizarPing(ip)) {
			DefaultTableModel modelo = new DefaultTableModel();
			modelo.addColumn("Numero de puerto");
			jtPuertos.setModel(modelo);

			Object[] fila = new Object[1];
			for (int puerto : Nmap.port(ip)) {
				fila[0] = puerto;
				modelo.addRow(fila);
			}
		}else{
			JOptionPane.showMessageDialog(null, "La direccion ip no es alcanzable");
		}
	}
}
