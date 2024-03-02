package org.example.bioreactor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ClientTCP  {


	private int numeroPort;

	private String nomServeur;

	private Socket socketServeur;

	private PrintStream socOut;

	private BufferedReader socIn;

	private boolean isConnected = false;

	private DataStorage dataStorage;

	public enum Command {
		PLAY,
		END_OF_TRANSMISSION,
	}
	
	/** Un client se connecte a un serveur identifie par un nom (unNomServeur), sur un port unNumero */
	public  ClientTCP(String unNomServeur, int unNumero, DataStorage.DataType communicationType){
		numeroPort = unNumero;
		nomServeur = unNomServeur;
		dataStorage = new DataStorage(communicationType);
	}


	public DataStorage getDataStorage() {
		return dataStorage;
	}

	public boolean connecterAuServeur() {        
		boolean ok = false;
		try {
			System.out.println("Tentative : " + nomServeur + " -- " + numeroPort);
			socketServeur = new Socket( nomServeur, numeroPort);
			socOut = new PrintStream(socketServeur.getOutputStream());
			socIn = new BufferedReader ( 
					new InputStreamReader (socketServeur.getInputStream()));
			System.out.println("\u001B[32mConnexion établie\u001B[0m");
			this.isConnected = true;
			ok = true;
		} catch (UnknownHostException e) {
			System.err.println("Serveur inconnu : " + e);
		} catch (ConnectException e) {
			System.err.println("Exception lors de la connexion:" + e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("Exception lors de l'echange de donnees:" + e.getLocalizedMessage());
		}
		return ok;
	} 	
	
	public void deconnecterDuServeur() {        
		try {
			System.out.println("[ClientTCP] CLIENT : " + socketServeur);
			socOut.close();
			socIn.close();
			socketServeur.close();
			this.isConnected = false;
		} catch (Exception e) {
			System.err.println("Exception lors de la deconnexion :  " + e);
		}
	} 	
	
	public String transmettreChaine(String uneChaine) {
		String dataReceived = null;
		try {
			System.out.println( "[Requete client] : " + uneChaine );
			socOut.println( uneChaine );
			socOut.flush();
			boolean endOfTransmission = false;
			while(!endOfTransmission)
			{
				dataReceived = socIn.readLine();
				System.out.println( "[Réponse serveur] : " + dataReceived );
				if(dataReceived.equals(Command.END_OF_TRANSMISSION.toString()))
					endOfTransmission = true;
				else
					if(!dataReceived.isEmpty())
						dataStorage.addData(dataReceived);
			}

		} catch (UnknownHostException e) {
			System.err.println("Serveur inconnu : " + e);
		} catch (IOException e) {
			System.err.println("Exception entree/sortie:  " + e.getLocalizedMessage());
		}
		return dataReceived;
	}



	public boolean isConnected() {
		return isConnected;
	}
}
