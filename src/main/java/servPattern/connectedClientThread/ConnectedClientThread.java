package servPattern.connectedClientThread;

import servPattern.tcpServer.TCPServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ConnectedClientThread extends Thread {

    private Socket clientSocket;
    private TCPServer myServer;
    int etat = 0;

    public ConnectedClientThread( Socket aClientSocket , TCPServer aServer ) {
        clientSocket = aClientSocket;
        myServer = aServer;
    }

    public int getEtat() {
        return etat;
    }

    public void run() {
        String inputReq,outputReq;

        try {
            /* Ouverture des objets de type Stream sur la socket du client réseau  */
            BufferedReader is = new BufferedReader ( new InputStreamReader(clientSocket.getInputStream()));
            PrintStream os = new PrintStream(clientSocket.getOutputStream());

            System.out.println( "Client Thread " );

            while ( (inputReq = is.readLine()) != null && etat != 3 ) {
                System.out.println( " Msg 2 Recu " + inputReq );
                String chaines[] = inputReq.split( " " );
                outputReq = "Msg From Server ";

                for( int i = 0 ; i < chaines.length ; i++ ) {
                    outputReq = outputReq + " Indice : " + i + " Mot : " + chaines[i];
                    System.out.println( outputReq );
                }
                os.println(outputReq + "\n");
                os.flush();

                //TODO ADJUST THE DATA
                int delayMS = 1000;
                if ( chaines[0].equals("demandeRetrait") ) {
                    etat = 1;
                    myServer.getIContext().play(clientSocket, delayMS);
                    System.out.println( outputReq + " val de etat " + etat );

                } else if ( chaines[0].equals("demandeDepot") ) {
                    etat = 2;
                    myServer.getIContext().pause();
                    System.out.println( outputReq + " val de etat " + etat );
                } else if ( chaines[0].equals("stop") ) {
                    etat = 3;
                    myServer.getIContext().goForward();
                    System.out.println( outputReq + " val de etat " + etat );
                } else if(){
                    etat = 4;
                    myServer.getIContext().goBackwards();
                    System.out.println( outputReq + " val de etat " + etat );
                } else if() {
                    etat = 0;
                    myServer.getIContext().stop();
                }

            }

            clientSocket.close();
            os.close();
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
