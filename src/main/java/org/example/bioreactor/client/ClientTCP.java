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

	private ClientController clientController;

	private DataStorage dataStorage;

	public enum Command {
		PLAY,
		END_OF_SIMULATION,
	}
	
	/** Un client se connecte a un serveur identifie par un nom (unNomServeur), sur un port unNumero */
	public  ClientTCP(ClientController myController, String unNomServeur, int unNumero, DataStorage.DataType communicationType){
		clientController = myController;
		numeroPort = unNumero;
		nomServeur = unNomServeur;
		dataStorage = new DataStorage(communicationType);
	} 

	public boolean connecterAuServeur() {        
		boolean ok = false;
		try {
			System.out.println("Tentative : " + nomServeur + " -- " + numeroPort);
			socketServeur = new Socket( nomServeur, numeroPort);
			socOut = new PrintStream(socketServeur.getOutputStream());
			socIn = new BufferedReader ( 
					new InputStreamReader (socketServeur.getInputStream()));
			System.out.println("Connexion établie");
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
		} catch (Exception e) {
			System.err.println("Exception lors de la deconnexion :  " + e);
		}
	} 	
	
	public String transmettreChaine(String uneChaine) {        
		String dataReceived = null;
		try {
			System.out.println( "Requete client : " + uneChaine );
			socOut.println( uneChaine );
			socOut.flush();
			while(!Objects.equals(socIn.readLine(), Command.END_OF_SIMULATION.toString()))
			{
				dataReceived = socIn.readLine();
				dataStorage.addData(dataReceived);
				// TODO : Ajouter la donnée reçue au tableau de l'IHM

				clientController.addDataToTable(dataReceived);
			}
			System.out.println( "Reponse serveur : " + dataReceived );

		} catch (UnknownHostException e) {
			System.err.println("Serveur inconnu : " + e);
		} catch (IOException e) {
			System.err.println("Exception entree/sortie:  " + e.getLocalizedMessage());
		}
		return dataReceived;
	} 

	/* A utiliser pour ne pas deleguer la connexion aux interfaces GUI */
	public String transmettreChaineConnexionPonctuelle(String uneChaine) {
		String msgServeur = null;
		StringBuilder chaineRetour = new StringBuilder();
		System.out.println("\nClient connexionTransmettreChaine " + uneChaine);
		if (connecterAuServeur()) {
			try {
				socOut.println(uneChaine);
				socOut.flush();
				msgServeur = socIn.readLine();
				while( msgServeur != null && !msgServeur.isEmpty()) {
					chaineRetour.append(msgServeur).append("\n");
					msgServeur = socIn.readLine();
				}
				System.out.println("Client msgServeur " + chaineRetour);
				deconnecterDuServeur();
			} catch (Exception e) {
				System.err.println("Exception lors de la connexion client:  " + e);
			}
		}
		else
		{	
			System.err.println("Connexion echouee");
		}
		return chaineRetour.toString();
	}
	
}
