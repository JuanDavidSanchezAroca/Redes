package co.edu.uniquindio.main;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import co.edu.uniquindio.logica.JNetMap;
import co.edu.uniquindio.vista.VentanaPrincipal;

public class Main {

	private static JNetMap x;

	public static void main(String[] args) {
		if (args.length > 0) {
			x = new JNetMap();
			executeTerminal(args);
		} else {
			VentanaPrincipal ventana = new VentanaPrincipal();
			ventana.run();
		}
	}

	private static void executeTerminal(String[] args) {
		try {

			if (args[0].equals("-ls")) {
				executeList(args);
			}

			else if (args[0].equals("-if")) {
				executeInterfaces(args);
			}

			else if (args[0].equals("-pl")) {
				executePorts(args);
			}
			
			else if (args[0].equals("-h")) {
				executeMenu();
			}
			
			else{
				System.out.println("Comando ingresado no valido. "
						+ "Para mas informacion ejecute \"-h\".\n");
			}

		} catch (UnknownHostException e) {
			System.out.println("El formato de IPHOST ingresado no es valido. " + 
			"Ejecute \"-h\" para mas informacion.");
		} catch (SocketException e) {
			System.out.println("Hubo un problema analizando los puertos.");
		} catch (IOException e) {
			System.out.println("Hubo un error de entrada/salida de datos.");
		} catch (NumberFormatException e) {
			System.out.println("Formato de NICID no valido. " + 
			"Ejecute \"-h\" para mas informacion.");
		}
	}

	private static void executeList(String[] args)
			throws SocketException, IOException, UnknownHostException, NumberFormatException {
		x.listarTarjetas();

		if (args.length > 1)
			x.obtenerInformacion(Integer.parseInt(args[1]));
		else
			x.obtenerInformacion(0);

		x.hostDisponibles();
		List<String> host = x.getHostDisponible();
		System.out.println("Fueron encontrados: " + host.size() + " host:");
		System.out.printf("Host\t\t\tIP\n");
		for (int i = 0; i < host.size(); i++)
			System.out.printf("%d\t\t\t%s\n", i + 1, host.get(i));

	}

	private static void executeInterfaces(String[] args) throws SocketException, IOException {
		x.listarTarjetas();
		List<String> interfaces = x.getInterfacesLista();
		for (int i = 0; i < interfaces.size(); i++)
			System.out.println(i + " " +interfaces.get(i));
	}
	
	private static void executePorts(String[]args) 
			throws NumberFormatException, SocketException, UnknownHostException, IOException{
		if (args.length < 2) {
			System.out.println("Faltan argumentos para ejecutar "
					+ "la operacion\n Ingrese -h para ver el menu " + "de ayuda");
		} else {
			if (args.length > 2)
				x.obtenerInformacion(Integer.parseInt(args[2]));
			else
				x.obtenerInformacion(0);
			if (x.realizarPing(args[1])) {
				List<Integer> ports = x.port(args[1]);
				System.out.println("Fueron encontrados: " + ports.size() + " puertos abiertos:");
				System.out.printf("Puerto\t\t\tServicio\n");
				for (int i = 0; i < ports.size(); i++)
					System.out.printf("%d\t\t\t%s\n", ports.get(i), "UNKNOWN");
			} else
				System.out.println("El host no se encuentra conectado " + "a la red de la NIC seleccionada.");
		}
	}
	
	private static void executeMenu(){
		System.out.println("Bienvenido al centro de ayuda de JNetMap!\n"
				+ "Si usted desea listar las interfaces de red de su equipo, ejecute el"
				+ " comando \"-if\".\nSi usted desea listar los host activos en la "
				+ "red, ejecute el comando \"-ls [NICID]\", "
				+ "donde la NICID es el numero con el cual se identificó previamente la NIC"
				+ " despues de usar el comando \"-if\".\nSi desea listar los puertos abiertos"
				+ " de un host, ejecute el comando \"-pl IPHOST [NICID]\",  "
				+ "donde la NICID es el numero con el cual se identificó previamente la NIC"
				+ " despues de usar el comando \"-if\".\n");
	}
}