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
	private String previousCommand;
	private boolean inPlay;

	public enum Command {
		PLAY,
		PAUSE,
		STOP,
		PREVIOUS,
		NEXT,
		END_OF_TRANSMISSION,
		BEGINNING_OF_SIMULATION,
		END_OF_SIMULATION,
	}
	
	/** Un client se connecte a un serveur identifie par un nom (unNomServeur), sur un port unNumero */
	public  ClientTCP(String unNomServeur, int unNumero, DataStorage.DataType communicationType){
		numeroPort = unNumero;
		nomServeur = unNomServeur;
		dataStorage = new DataStorage(communicationType);
		previousCommand = null;
		inPlay = false;
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
		// make sure the play button cannot be ran more than one time in a row.
		if (this.previousCommand != null && this.previousCommand.equals(Command.PLAY.toString()) && uneChaine.equals(Command.PLAY.toString())){
			return null;
		}
		// make the pause button because a play button to resume a simulation without having to move
		if (this.previousCommand != null && this.previousCommand.equals(Command.PAUSE.toString()) && uneChaine.equals(Command.PAUSE.toString()) && this.inPlay){
			//TODO USE A GLOBAL VARIABLE TO REACH THE DELAY IN MS?
			uneChaine = String.valueOf(Command.PLAY) + " 5"; //clicking twice on pause resumes the simulation
		}
		this.previousCommand = uneChaine;
		// procedure to control the previous step
		if (uneChaine.equals(Command.PLAY)){
			this.inPlay = true;
		} else if (uneChaine.equals(Command.STOP)){
			this.inPlay = false;
		}

		try {
			System.out.println( "[Requete client] : " + uneChaine );
			socOut.println( uneChaine );
			socOut.flush();
			boolean endOfTransmission = false;
			while(!endOfTransmission)
			{
				dataReceived = socIn.readLine();
				System.out.println( "[Réponse serveur] : " + dataReceived );
				if(dataReceived.equals(Command.END_OF_TRANSMISSION.toString()) || dataReceived.equals(Command.END_OF_SIMULATION.toString()) || dataReceived.equals(Command.BEGINNING_OF_SIMULATION.toString())) {
					endOfTransmission = true;
					//TODO manage END_OF_SIMULATION to display a message in the GUI?
					if (dataReceived.equals(Command.END_OF_SIMULATION.toString())){
						System.out.println("\u001B[36mEnd of the simulation\u001B[0m");
					} else if (dataReceived.equals(Command.BEGINNING_OF_SIMULATION.toString())){
						System.out.println("\u001B[36mBeginning of the simulation\u001B[0m");
					}
				} else {
					if (!dataReceived.isEmpty()){
						dataStorage.addData(dataReceived);
					}
				}
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
