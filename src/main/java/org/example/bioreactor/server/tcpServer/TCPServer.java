package org.example.bioreactor.server.tcpServer;

import org.example.bioreactor.server.IContext;
import org.example.bioreactor.server.connectedClientThread.ConnectedClientThread;
import org.example.bioreactor.server.experimentation.Experimentation;
import java.io.*;
import java.net.*;

/**
 * @author champejo
 *
 */
public class TCPServer extends Thread {

    private static int maxClients = 4;

    /**
     * Maximum de connexions client autorisées
     */
    private int maxConnexions;

    private int numeroPort;

    // Initialisation d'une expérimentation
    //TODO revoir
    private IContext iContext;

    private int nbConnexions;

    private boolean running = true;

    /**
     * constructors
     * @param unNumeroPort
     */
    public  TCPServer(int unNumeroPort) {

        numeroPort = unNumeroPort;
        maxConnexions = maxClients;
    }

    public TCPServer(int unNumeroPort, String filename){
        this.numeroPort = unNumeroPort;
        this.maxConnexions = maxClients;
        try {
            this.iContext = new Experimentation(filename);
        } catch (FileNotFoundException e){
            System.out.println("The file you are trying to access could not be found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public IContext getIContext() {
        return iContext;
    }


    public String toString() {

        return "[TCP Server] on Port : " +  numeroPort ;
    }

    public void run() {
        nbConnexions = 0;
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        while(running){
            try {
                serverSocket = new ServerSocket ( numeroPort );
            } catch (IOException e) {
                System.out.println("Could not listen on port: " + numeroPort + ", " + e);
                System.exit(1);
            }


            /* On autorise maxConnexions traitements*/
            while (nbConnexions <= maxConnexions) {
                try {
                    System.out.println(" Attente du serveur pour la communication d'un client " );
                    clientSocket = serverSocket.accept();
                    nbConnexions ++;
                    System.out.println("Nb connexions = " + nbConnexions);

                } catch (IOException e) {
                    System.out.println("Accept failed: " + serverSocket.getLocalPort() + ", " + e);
                    System.exit(1);
                }
                ConnectedClientThread st = new ConnectedClientThread( clientSocket , this );
                st.start();
            }
            System.out.println("Already " + nbConnexions + " clients. Maximum allowed reached");

        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Could not close");
        }

    }

    public void arret(){
        running = false;
    }

    public int getNbConnexions() {
        return nbConnexions;
    }
}
