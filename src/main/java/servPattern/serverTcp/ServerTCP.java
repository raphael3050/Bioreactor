package servPattern.serverTcp;

import servPattern.IContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTCP {


    /**
     * Représente un serveur TCP, qui écoute sur un numéro de port
     *
     */
    private static int nbConnexions = 0;

    /** Maximum de connexions client autorisées */
    private int maxConnexions;

    private Socket clientSocket;

    public IContext iContext;

    private int portNumber;

    //TODO protocole
    protected enum Protocole {

    }

    private Protocole currentProtocole;

    public ServerTCP(int portNumber) {
        portNumber = portNumber;
        maxConnexions = 10;
    }

    public ServerTCP(IContext iContext, int port) {
        this(port);
        this.iContext = iContext;
    }


    public Protocole getProtocole(){
        return currentProtocole;
    }

    public void setProtocole(Protocole protocole){
        this.currentProtocole = protocole;
    }

    public void setIContext(IContext iContext){ this.iContext = iContext; }

    public IContext getiContext() { return this.iContext.getIContext(); }

    @Override
    public String toString() {
        return "[ServeurTCP] Port : " + this.portNumber + ", Contexte: " + this.iContext;
    }

    /* l'ancienne methode go est remplacee par run */
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.portNumber);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + this.portNumber + ", " + e);
            System.exit(1);
        }

        /* On autorise maxConnexions traitements */
        while (nbConnexions <= maxConnexions) {
            try {
                System.out.println(" Attente du serveur pour la communication d'un client ");
                clientSocket = serverSocket.accept();
                nbConnexions++;
                System.out.println("Nb automates : " + nbConnexions);
            } catch (IOException e) {
                System.out.println("Accept failed: " + serverSocket.getLocalPort() + ", " + e);
                System.exit(1);
            }
            //créer tous les protocoles, lesquels se lanceront ou pas en fonction du statut de ce qui est demandé.
            IProtocole protocoleTransactionsBancaires = null;
            IProtocole protocoleHistorique = null;
            Socket historiqueSocket = new Socket();
            Socket transactionSocket = new Socket();
            protocoleTransactionsBancaires = new ProtocoleTransactionsBancaires(clientSocket, historiqueSocket, this);
            protocoleHistorique = new ProtocoleHistorique(clientSocket, transactionSocket, this);
            protocoleHistorique.start();
            protocoleTransactionsBancaires.start();
        }
        System.out.println("Deja " + nbConnexions + " clients. Maximum autorisé atteint");

        try {
            serverSocket.close();
            nbConnexions--;
        } catch (IOException e) {
            System.out.println("Could not close");
        }

    }


}
