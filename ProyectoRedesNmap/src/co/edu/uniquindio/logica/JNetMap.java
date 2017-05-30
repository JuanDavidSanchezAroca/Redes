/**
 * 
 */
package co.edu.uniquindio.logica;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Clase principal de logica de la aplicacion
 * 
 * @author Juan David Sanchez A.
 * @author Juan Camilo Correa Pacheco
 * @author Carlos Alberto Cardona Beltran
 * @author Univerisdad Del Quindio
 * @author Armenia - Quindio
 */
public class JNetMap {

	/***
	 * Atributo auxiliar para obtener el nombre de una NIC
	 */
	private static NetworkInterface nombreNIC;
	/**
	 * Lista de interfaces de red despues del proceso
	 */
	private static List<String> interfacesLista;
	/**
	 * Lista de host disponibles de la red despues del proceso
	 */
	private static List<String> hostDisponible;
	/**
	 * Lista de puertos disponibles despues del proceso
	 */
	private static List<Integer> portDisponibles;
	/**
	 * Mascara de la red asociada a la NIC
	 */
	private static String mascaraRed;
	/**
	 * Direccion inet auxiliar
	 */
	private static InetAddress inetAddress;
	/**
	 * Lista de interfaces de red
	 */
	private static NetworkInterface networkIntefrface;
	/**
	 * Lista auxiliar para guardar algunas IP
	 */
	private static ArrayList<String> auxIp;

	/**
	 * Conjunto de hilos que se encargara de ejecutar tareas futuras
	 */
	final static ExecutorService es = Executors.newFixedThreadPool(100);
	final static List<Future<Boolean>> futures = new ArrayList<>();

	/**
	 * Constructor de la clase Nmap
	 */
	public JNetMap() {
		interfacesLista = new ArrayList<>();
		hostDisponible = new ArrayList<>();
		portDisponibles = new ArrayList<>();
		auxIp = new ArrayList<>();
	}

	/**
	 * Metodo que permite listar las tarjetas disponibles
	 * 
	 * @throws SocketException
	 */
	public void listarTarjetas() throws SocketException {
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
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
	 *            numero de interface de red seleccionada por el usuario
	 * @throws SocketException
	 */
	@SuppressWarnings("static-access")
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
	}

	/**
	 * Metodo que reopila la informacion del host
	 *
	 * @return msj con la informacion del host
	 * @throws SocketException
	 */
	public String imprimirInformacion() throws SocketException {
		String inf = "";
		inf += "Interfaces:" + networkIntefrface.toString() + "\n";
		inf += "Mi direccion IP es:" + inetAddress.getHostAddress() + "\n";
		inf += "Mi Mascara de red es:" + mascaraRed + "\n";
		return inf;
	}

	/**
	 * Metodo que calcula los host disponibles de una red a partir de la
	 * direccion de la red
	 * 
	 * @param i,
	 *            es el octeto a partir de el cual empieza a cambiar la
	 *            direccion que es la correspondiente para los hosts de la red
	 * @param nuemroE,
	 *            ultimo octeto de la direccion de la red
	 * @param direccion
	 *            de la red en arreglo de 4 posiciones
	 */
	private void calcularHostDisponibles(int i, int numeroE, int red[], int disponible) {
		if (numeroE == 255) {
			return;
		} else {
			for (int j = i; j < red.length; j++) {
				for (int k = numeroE; k <= disponible; k++) {
					red[i] = k;
					if (i != 3) {
						calcularHostDisponibles(i + 1, 0, red, 255);
					} else {
						red[i] = k;
					}
					String ip = red[0] + "." + red[1] + "." + red[2] + "." + red[3];
					// Se envia la tarea de hacer ping, pero se deja como una
					// tarea futura, cuyo resultado se leera despues

					if (!auxIp.contains(ip)) {
						futures.add(realizarPing2(ip, es));
						auxIp.add(ip);
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
		hostDisponible.clear();
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
		}

		int indice = 3;
		while (mascaraIP[indice] != 255) {
			indice--;
		}

		int posicionMascara = Integer.parseInt(mascaraRed) / 8;
		int distancia = (int) Math.pow(2, 8 - Integer.parseInt(mascaraRed) % 8);
		calcularHostDisponibles(posicionMascara, dirRed[posicionMascara], dirRed,
				distancia + dirRed[posicionMascara] - 1);

		// El numero actual hace se asocia con el resultado falso o
		// verdadedo obtenido de la tarea enviada con anterioridad
		// de los futures
		int contador = 0;

		// Se lee en el ciclo para identificar cuales fueron las direcciones ip
		// que respondieron ante el ping realizado con anterioridad
		for (final Future<Boolean> f : futures) {
			try {
				if (f.get()) {
					hostDisponible.add(auxIp.get(contador));
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			contador++;
		}
	}

	/**
	 * Metodo que realiza ping a un host
	 * 
	 * @throws IOException,
	 *             UnknownHostException
	 */
	public boolean realizarPing(String ip) throws IOException, UnknownHostException {
		InetAddress direccion;
		direccion = InetAddress.getByName(ip);
		boolean alcanzable = direccion.isReachable(1500);
		if (alcanzable)
			return true;
		return false;

	}

	/**
	 * Metodo que verfica los puertos disponibles de un host
	 * 
	 * @param ip,
	 *            la direccion ip del host en el que se va a escanear todos los
	 *            puertos
	 * @return listaPuertos, listado de puertos abiertos
	 */
	public ArrayList<Integer> port(String ip) {
		final ExecutorService es = Executors.newFixedThreadPool(1000);
		final int timeout = 400;
		final List<Future<Boolean>> tareas = new ArrayList<>();
		for (int port = 1; port <= 65535; port++) {
			tareas.add(portIsOpen(es, ip, port, timeout));
		}
		es.shutdown();
		// Variable auxiliar para saber cual es el puerto que se esta leyendo en
		// la lista de tareas
		//
		int auxPuerto = 1;
		// ArrayList con la lista de puertos activos para este host
		ArrayList<Integer> listadoPuertos = new ArrayList<>();
		for (final Future<Boolean> puertoActual : tareas) {
			try {
				if (puertoActual.get()) {
					listadoPuertos.add(auxPuerto);
				}
			} catch (Exception e) {

			}
			auxPuerto++;
		}
		return listadoPuertos;
	}

	/**
	 * Metodo encargado de veificar si un puerto est� abierto
	 * 
	 * @param es,
	 *            el ejecutor de tareas
	 * @param ip,
	 *            la ip del host al que se escanearan los puertos
	 * @param port,
	 *            el numero del puerto
	 * @param timeout,
	 *            tiempo limite de respuesta
	 * @return un hilo con el resultado
	 */

	private Future<Boolean> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
		return es.submit(new Callable<Boolean>() {

			public Boolean call() {
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(ip, port), timeout);
					portDisponibles.add(port);
					socket.close();
					return true;
				} catch (Exception ex) {
					return false;
				}
			}
		});
	}

	
	/**
	 * Metodo que verfica los puertos disponibles de un host
	 * 
	 * @param ip,
	 *            la ip del host a que se har� ping
	 */
	public ArrayList<Integer> portServicios(String ip) {
		final ExecutorService es = Executors.newFixedThreadPool(1000);
		final int timeout = 400;
		final List<Future<Boolean>> tareas = new ArrayList<>();
		for (int port = 1; port <= 65535; port++) {
			tareas.add(portIsOpenServicios(es, ip, port, timeout));
		}
		es.shutdown();
		// Variable auxiliar para saber cual es el puerto que se esta leyendo en
		// la lista de tareas
		//
		int auxPuerto = 1;
		// ArrayList con la lista de puertos activos para este host
		ArrayList<Integer> listadoPuertos = new ArrayList<>();
		for (final Future<Boolean> puertoActual : tareas) {
			try {
				if (puertoActual.get()) {
					listadoPuertos.add(auxPuerto);
				}
			} catch (Exception e) {

			}
			auxPuerto++;
		}
		return listadoPuertos;
	}

	/**
	 * Metodo encargado de veificar si un puerto est� abierto, y analizar su
	 * servicio
	 * 
	 * @param es,
	 *            el ejecutor de tareas
	 * @param ip,
	 *            la ip del host al que se escanearan los puertos
	 * @param port,
	 *            el numero del puerto
	 * @param timeout,
	 *            tiempo limite de respuesta
	 * @return un hilo con el resultado
	 */
	private Future<Boolean> portIsOpenServicios(final ExecutorService es, final String ip, final int port,
			final int timeout) {
		return es.submit(new Callable<Boolean>() {

			public Boolean call() {
				try {
					Socket socket = new Socket();
					socket.connect(new InetSocketAddress(ip, port), timeout);
					boolean autoflush = true;
					PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					// send an HTTP request to the web server
					out.println("GET / HTTP/1.1");
					out.println("Host: www.google.com:80");
					out.println("Connection: Close");
					out.println();

					// read the response
					boolean loop = true;
					StringBuilder sb = new StringBuilder(8096);
					while (loop) {
						if (in.ready()) {
							int i = 0;
							while (i != -1) {
								i = in.read();
								sb.append((char) i);
							}
							loop = false;
						}
					}
					System.out.println(sb.toString() + port);
					System.out.println("sales");
					socket.close();
					return true;
				} catch (Exception ex) {
					return false;
				}
			}
		});
	}

	
		
	/**
	 * Este metodo se encarga de realizar ping a la direccion ip recibida por
	 * parametro pero lo pospone como un
	 * 
	 * @param ip,
	 *            la ip del host al que se har� ping
	 * @param es,
	 *            el ejecutor de tareas
	 * @return un hilo con el resultado
	 */
	private Future<Boolean> realizarPing2(final String ip, final ExecutorService es) {

		return es.submit(new Callable<Boolean>() {

			public Boolean call() throws UnknownHostException, IOException {

				InetAddress direccion;
				direccion = InetAddress.getByName(ip);
				boolean alcanzable = direccion.isReachable(1500);
				if (alcanzable)
					return true;
				return false;

			}
		});

	}


	/**
	 * Metodo para obtener unlistado de las interfaces de red
	 * 
	 * @return lista string con la descripcion de la interfaces de red
	 */
	public List<String> getInterfacesLista() {
		return interfacesLista;
	}

	/**
	 * Metodo para obtener el listado de los host disponibles en mi red
	 * 
	 * @return lista de string con las direcciones ip de los host disponibles
	 */
	public List<String> getHostDisponible() {
		return hostDisponible;
	}

	/**
	 * Metodo para obtener el listado de los puertos disponibles de un host
	 * 
	 * @return lista de enteros con los puertos disponibless
	 */
	public List<Integer> getPortDisponibles() {
		return portDisponibles;
	}

}
