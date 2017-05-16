/**
 * 
 */
package com.uniquindio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Juan David Sanchez A.
 * @author Univerisdad Del Quindio
 * @author Armenia - Quindio
 */
public class Nmap {

	/***
	 * Atributos
	 */
	private static NetworkInterface nombreNIC;
	private static List<String> interfacesLista;
	private static List<String> hostDisponible;
	private static String mascaraRed;
	private static String mac;
	private static InetAddress miIp;
	private static InetAddress inetAddress;
	private static NetworkInterface networkIntefrface = null;
	private static int hostDisponibles;

	/**
	 * Constructor de la clase Nmap
	 */
	public Nmap() {
		interfacesLista = new ArrayList<>();
		hostDisponible = new ArrayList<>();
	}

	/**
	 * Metodo que permite listar las tarjetas disponibles
	 * 
	 * @throws SocketException
	 */
	public void listarTarjetas() throws SocketException {
		Enumeration<NetworkInterface> interfaces = nombreNIC.getNetworkInterfaces();
		NetworkInterface networkIntefrface = null;
		while (interfaces.hasMoreElements()) { // mientras exista otra tarjeta
			networkIntefrface = (NetworkInterface) interfaces.nextElement();
			if (networkIntefrface.isUp()) // verifica que la tarje este activa
				interfacesLista.add(networkIntefrface.toString());
		}
	}

	/**
	 * Metodo que obtiene la informacion del host
	 * 
	 * @param intRed
	 *            numero de interface de red
	 * @throws SocketException
	 */
	public void obtenerInformacion(int intRed) throws SocketException {

		Enumeration<NetworkInterface> interfaces = nombreNIC.getNetworkInterfaces();
		int contador = -1;
		while (interfaces.hasMoreElements() && contador != intRed) {
			networkIntefrface = (NetworkInterface) interfaces.nextElement();
			if (networkIntefrface.isUp()) // verifica que la tarje este activa
				contador++;
		}

		Enumeration<InetAddress> direccionesIp = networkIntefrface.getInetAddresses();
		inetAddress = (InetAddress) direccionesIp.nextElement();

		for (InterfaceAddress iface : networkIntefrface.getInterfaceAddresses()) {
			if (iface == null) {
				continue;
			}
			if (iface.getAddress() instanceof Inet4Address) {
				mascaraRed = iface.getNetworkPrefixLength() + "";
			}
		}

		while ((inetAddress instanceof Inet6Address)) {
			inetAddress = (InetAddress) direccionesIp.nextElement();
		}

		mac = getMacAdress(inetAddress);
	}

	/**
	 * Metodo que obtiene la direccion mac
	 * 
	 * @param ip
	 *            ip del host
	 * @return mac
	 * @throws SocketException
	 */
	public String getMacAdress(InetAddress ip) throws SocketException {
		String address = null;
		NetworkInterface network = NetworkInterface.getByInetAddress(ip);
		byte[] mac = network.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
		}

		address = sb.toString();
		return address;
	}

	/**
	 * Metodo que reopila la informacion del host
	 *
	 * @return msj con la informacion del host
	 */
	public String imprimirInformacion() {
		String inf = "";
		inf += "Interfaces:" + networkIntefrface.toString() + "\n";
		inf += "Mi direccion IP es:" + inetAddress.getHostAddress() + "\n";
		inf += "Mi Mascara de red es:" + mascaraRed + "\n";
		inf += "Mi direccion MAC es: " + mac + "\n";
		return inf;

	}

	/**
	 * Metodo que calcula los host disponibles de una red a partir de la
	 * direccion de la red
	 * 
	 */
	public void calcularHostDisponibles(int i, int numeroE, int red[]) {

		if (numeroE == 255) {
			return;
		} else {

			for (int j = i; j < red.length; j++) {
				for (int k = numeroE; k <= 255; k++) {
					red[i] = k;
					if (i != 3) {
						calcularHostDisponibles(i + 1, 0, red);
					} else {
						red[i] = k;
					}
					System.out.println("esperando-...");
					String ip = red[0] + "." + red[1] + "." + red[2] + "." + red[3];
					if (realizarPing(ip)) {
						hostDisponible.add(ip);
						System.out.println(ip);
					}

				}
			}
		}
	}

	/**
	 * Metodo que calcula la direccion de la red y llama el metodo verificar
	 * host disponibles
	 */
	public void hostDisponibles() {
		hostDisponibles = (int) Math.pow(2, 32 - Integer.parseInt(mascaraRed));
		int aux = Integer.parseInt(mascaraRed);
		int[] mascaraIP = new int[4];
		String[] ipCadena = inetAddress.getHostAddress().split("\\.");
		int cont = 0;
		while (aux != 0) {

			if (aux >= 8) {
				mascaraIP[cont] = 255;
				aux -= 8;
			} else {
				int res = 0;
				for (int i = 7; i >= 8 - aux; i--) {
					res += (int) Math.pow(2, i);
				}
				mascaraIP[cont] = res;
				aux -= aux;
			}
			cont++;
		}

		// se clacula l adirecion de la red realizando una operacion and entre
		// la direcion ip y la mascara de la subred jd
		int[] dirRed = new int[4];
		for (int i = 0; i < mascaraIP.length; i++) {
			dirRed[i] = mascaraIP[i] & Integer.parseInt(ipCadena[i]);
			System.out.println(dirRed[i]);
		}

		int indice = 3;
		while (mascaraIP[indice] != 255) {
			indice--;
		}

		calcularHostDisponibles(3 - indice, dirRed[3 - indice], dirRed);

	}

	/**
	 * Metodo que realiza ping a un host
	 */
	public static boolean realizarPing(String ip) {
		InetAddress direccion;
		try {
			direccion = InetAddress.getByName(ip);
			boolean alcanzable = direccion.isReachable(1500);
			if (alcanzable)
				return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;

	}

	/**
	 * Metodo que verfica los puertos disponibles de un host
	 */
	public static void port(String ip) {
		
		 final ExecutorService es = Executors.newFixedThreadPool(6000000);
		  final int timeout = 1000;
		  for (int port = 1; port <= 65535; port++) {
		    portIsOpen(es, ip, port, timeout);
		  }	
		 es.shutdown();
	}
	
	public static Future<Boolean> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
		  return es.submit(new Callable<Boolean>() {
		
		     public Boolean call() {
		        try {
		          Socket socket = new Socket();
		          socket.connect(new InetSocketAddress(ip, port), timeout);
		          System.out.println("Port " + port + " is open");
		          socket.close();
		          
		          return true;
		        } catch (Exception ex) {
		          return false;
		        }
		      }
		   });
		}

	


	public static void main(String[] args) throws IOException {

		try {

			Nmap x = new Nmap();

			x.listarTarjetas();
			for (int i = 0; i < interfacesLista.size(); i++)
				System.out.println(interfacesLista.get(i));

			x.obtenerInformacion(1);
			System.out.println(x.imprimirInformacion());
		//	x.hostDisponibles();
								 x.port("10.0.48.60");
								 /*
								 * System.out.println(hostDisponibles);
								 */
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static List<String> getInterfacesLista() {
		return interfacesLista;
	}

	public static void setInterfacesLista(List<String> interfacesLista) {
		Nmap.interfacesLista = interfacesLista;
	}

	/**
	 * package nmap;
	 * 
	 * import java.io.BufferedReader; import java.io.IOException; import
	 * java.io.InputStream; import java.io.InputStreamReader; import
	 * java.io.OutputStreamWriter; import java.net.DatagramPacket; import
	 * java.net.DatagramSocket; import java.net.Inet4Address; import
	 * java.net.InetAddress; import java.net.InetSocketAddress; import
	 * java.net.NetPermission; import java.net.NetworkInterface; import
	 * java.net.Socket; import java.net.SocketException; import
	 * java.net.UnknownHostException; import java.util.ArrayList; import
	 * java.util.Enumeration; import java.util.List; import
	 * java.util.concurrent.Callable; import
	 * java.util.concurrent.ExecutionException; import
	 * java.util.concurrent.ExecutorService; import
	 * java.util.concurrent.Executors; import java.util.concurrent.Future;
	 * import java.util.logging.Level;
	 * 
	 * public class Main { public static void main(String[] args) throws
	 * InterruptedException, ExecutionException {
	 * 
	 * final ExecutorService es = Executors.newFixedThreadPool(65000); final
	 * String ip = "192.168.1.254"; final int timeout = 400; final List<Future
	 * <Boolean>> futures = new ArrayList<>(); for (int port = 1; port <= 65535;
	 * port++) { futures.add(portIsOpen(es, ip, port, timeout)); }
	 * es.shutdown(); int openPorts = 0; int number=1; for (final Future
	 * <Boolean> f : futures) { try { if (f.get()) { System.out.println(number+
	 * " " +f.get()); openPorts++; } } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); continue; } catch
	 * (ExecutionException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); continue; } //System.out.println(number+" "
	 * +f.get()); number++;
	 * 
	 * } System.out.println("There are " + openPorts + " open ports on host " +
	 * ip + " (probed with a timeout of " + timeout + "ms)");
	 * 
	 * 
	 * } // public static Future<Boolean> portIsOpen(final ExecutorService es,
	 * final String ip, final int port, final int timeout) { return
	 * es.submit(new Callable<Boolean>() {
	 * 
	 * @Override public Boolean call() { try { Socket socket = new Socket();
	 *           socket.connect(new InetSocketAddress(ip, port), timeout);
	 *           socket.close();
	 * 
	 *           return true; } catch (Exception ex) { return false; } } }); }
	 * 
	 * 
	 * 
	 *           }
	 */

}
