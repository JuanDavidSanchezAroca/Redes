/**
 * 
 */
package com.uniquindio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Juan David Espitia A - Juan David Sanchez A.
 * @author Univerisdad Del Quindio
 * @author Armenia - Quindio
 */
public class Nmap {

	/**
	 * Libreria encaraga de capturar mi IP
	 */
	 static InetAddress miIP;
	 
	 static NetworkInterface nombreNIC;
	 static List<String> host=new ArrayList<>();
	 static String mascaraRed;
	 static String mac;
	 static byte[] red;
	 static String miRed;
	/**
	 * @param args
	 */
	public Nmap()
	{
		
	}
	
	public static void obtenerInfoRed() throws UnknownHostException, SocketException
	{
		miIP=InetAddress.getLocalHost();
		for(int i=0;i<red.length;i++)System.out.println(red[i]);
		nombreNIC = NetworkInterface.getByInetAddress(miIP);
		mascaraRed=nombreNIC.getInterfaceAddresses().get(0).getNetworkPrefixLength() + "";
		mac=nombreNIC.getHardwareAddress().toString() + "";
		
	}
	
	public static void  calcalarRed(){
		int i,index=0;
		for(i=1;i<=32;i+=8){
			if(i <= Integer.parseInt(mascaraRed)){
				miRed+=red[index];
				System.out.println(red[index]);
				index++;
			}
		}
		
	}
	
	private static String imprimirInformacion()
	{
		String informacion="";
		informacion+="Mi Direccion IP es: "+ miIP +" \n";
		informacion+="Mi Mascara de Red  es: "+ mascaraRed +" \n";
		informacion+="Mi Direccion MAC es: "+ getMacAddress(miIP) +" \n";
		
		return informacion;
	}
	
	
	 private static String getMacAddress(InetAddress ip) {
	        String address = null;
	        try {
	            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
	            byte[] mac = network.getHardwareAddress();
	            StringBuilder sb = new StringBuilder();
	            for (int i = 0; i < mac.length; i++) {
	                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
	            }
	            address = sb.toString();
	        } catch (SocketException ex) {
	            ex.printStackTrace();
	        }
	        return address;
	    }

	 public static boolean realizarPing(String ip)
	 {
		 InetAddress direccion;
		try {
			direccion = InetAddress.getByName(ip);
			 boolean alcanzable=direccion.isReachable(1000);
			 if(alcanzable)return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 return false;
		
		 
	 }
	 
	 
	public static void main(String[] args) throws SocketException 
	{
		
		
		try {
			obtenerInfoRed();
			System.out.println(imprimirInformacion());
			System.out.println(realizarPing("192.168.0.17"));
			calcalarRed();
			System.out.println(miRed);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
