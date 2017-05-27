package com.uniquindio.vista;

import java.awt.EventQueue;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import com.uniquindio.Nmap;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;

public class VentanaPrincipal {
	JList<String> list;
	JPanel panel;
	public Nmap logica;

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTable jTable1;
	// Este combobox contiene una lista de las tarjetas de red disponibles
	JComboBox cbSeleccionarTarjeta;

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
		btnIniciarEscaneo.setBounds(467, 42, 124, 23);
		panel_1.add(btnIniciarEscaneo);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(40, 105, 394, 275);
		panel_1.add(scrollPane);

		jTable1 = new JTable();
		scrollPane.setViewportView(jTable1);

		panel = new JPanel();
		tabbedPane.addTab("Puertos disponibles", null, panel, null);
		panel.setLayout(null);

		JList<String> list_1 = new JList<>(new DefaultListModel<String>());
		list_1.setBounds(30, 106, 215, 171);
		((DefaultListModel) list_1.getModel()).addElement("carlos");

		panel.add(list_1);

		JLabel lblDireccionIpDel = new JLabel("Direccion IP del host");
		lblDireccionIpDel.setBounds(20, 21, 118, 14);
		panel.add(lblDireccionIpDel);

		textField = new JTextField();
		textField.setBounds(144, 18, 154, 20);
		panel.add(textField);
		textField.setColumns(10);

		JButton btnEscanear = new JButton("Escanear");
		btnEscanear.setBounds(346, 21, 89, 23);
		panel.add(btnEscanear);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Interfaces de red", null, panel_2, null);
		panel_2.setLayout(null);

		JLabel label = new JLabel("Direccion IP del host");
		label.setBounds(21, 9, 118, 14);
		panel_2.add(label);

		textField_1 = new JTextField();
		textField_1.setBounds(161, 6, 160, 20);
		textField_1.setColumns(10);
		panel_2.add(textField_1);

		JButton button = new JButton("Escanear");
		button.setBounds(331, 5, 106, 23);
		panel_2.add(button);

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
			System.out.println(num);
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
	

		 try{
		 DefaultTableModel temp = (DefaultTableModel) jTable1.getModel();
		 int a =temp.getRowCount()-1;
		 for(int i=0; i<a; i++)
		 temp.removeRow(i);
		 }catch(Exception e){
		 System.out.println(e);
		 }

		DefaultTableModel modelo = new DefaultTableModel();
		
		//jTable1.removeAll();
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
}
