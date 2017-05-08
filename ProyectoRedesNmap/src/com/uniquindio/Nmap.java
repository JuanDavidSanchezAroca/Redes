/**
 * 
 */
package com.uniquindio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Juan David Espitia A - Juan David Sanchez A.
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

	public Nmap() {
		interfacesLista = new ArrayList<>();
		hostDisponible=new ArrayList<>();
	}

	/**
	 * Metodo que permite listar las tarjetas disponibles
	 * 
	 * @throws SocketException
	 */
	public void listarTarjetas() throws SocketException {
		Enumeration<NetworkInterface> interfaces = nombreNIC.getNetworkInterfaces();
		NetworkInterface networkIntefrface = null;
		while (interfaces.hasMoreElements()) {
			networkIntefrface = (NetworkInterface) interfaces.nextElement();
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
			contador++;
		}
		Enumeration<InetAddress> direccionesIp = networkIntefrface.getInetAddresses();
		inetAddress = (InetAddress) direccionesIp.nextElement();
		while ((inetAddress instanceof Inet6Address)) {

			inetAddress = (InetAddress) direccionesIp.nextElement();
		}

		mascaraRed = networkIntefrface.getInterfaceAddresses().get(1).getNetworkPrefixLength() + "";
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

	public void calcularHostDisponibles(int i, int numeroE, int red[]) {

		if (numeroE == 255) {
			return;
		} else {
			
              for(int j=i; j< red.length;j++){
            	  for(int k=numeroE; k<= 255; k++){
            		  red[i]=k;
            		  if(i!=3){
            			  calcularHostDisponibles(i+1,0, red);
            		  }else{
            			 red[i]=k; 
            		  }
            		  
            		  String ip=red[0]+"."+red[1]+"."+red[2]+"."+red[3];
            		 // System.out.println(ip);
            		 if(realizarPing(ip)){
            			  hostDisponible.add(ip);
            		  }
            		 
            	  }
              }
		}
	}

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
		int[] dirRed = new int[4];
		for (int i = 0; i < mascaraIP.length; i++) {
			dirRed[i] = mascaraIP[i] & Integer.parseInt(ipCadena[i]);
		}

		int indice = 3;

		while (mascaraIP[indice] != 255) {
			indice--;
		}
		
		calcularHostDisponibles(3-indice, dirRed[3-indice],dirRed);

	}

	public static boolean realizarPing(String ip) {
		InetAddress direccion;
		try {
			direccion = InetAddress.getByName(ip);
			boolean alcanzable = direccion.isReachable(1000);
			if (alcanzable)
				return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	public static void main(String[] args) throws IOException {

		try {

			Nmap x = new Nmap();

			x.listarTarjetas();
			x.obtenerInformacion(0);
			System.out.println(x.imprimirInformacion());
			x.hostDisponibles();
			System.out.println(hostDisponibles);

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

}
