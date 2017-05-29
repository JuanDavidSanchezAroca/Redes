package com.uniquindio;

import java.net.SocketException;

public class Main {

	public static void main(String[] args) {
		
		try {

			Nmap x = new Nmap();

			x.listarTarjetas();
			for(int i=0; i < x.getInterfacesLista().size();i++) System.out.println(x.getInterfacesLista().get(i));
			x.obtenerInformacion(0);
			
			System.out.println(x.imprimirInformacion());
			// System.out.println(mascaraRed);
			// x.hostDisponibles();
			System.out.println("Despues de host disponibles");

			x.port("196.168.1.54");
			
			for(int i=0; i < x.getPortDisponibles().size();i++) System.out.println(x.getPortDisponibles().get(i));
			x.obtenerInformacion(0);
			/*
			 * System.out.println(hostDisponibles);
			 */
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
