package org.example.bioreactor.server.connectedClientThread;

import org.example.bioreactor.server.serverException.EndOfSimulationException;
import org.example.bioreactor.server.serverException.StartOfSimulationException;
import org.example.bioreactor.server.tcpServer.TCPServer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;


public class ConnectedClientThread extends Thread implements PropertyChangeListener {

    private Socket clientSocket;
    private TCPServer myServer;
    int etat = 0;
    private boolean endOfTransmission = false;

    public ConnectedClientThread( Socket aClientSocket , TCPServer aServer ) {
        clientSocket = aClientSocket;
        myServer = aServer;
        myServer.getIContext().getPropertyChangeSupport().addPropertyChangeListener(this);
    }

    public int getEtat() {
        return etat;
    }

    public void run() {
        String inputReq,outputReq;

        BufferedReader is = null;
        PrintStream os = null;
        try {
            /* Ouverture des objets de type Stream sur la socket du client réseau  */
            is = new BufferedReader ( new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());

            System.out.println( "Client Thread " );

            while ( (inputReq = is.readLine()) != null && etat != 3 ) {
                System.out.println(" Msg 2 Recu " + inputReq);
                String chaines[] = inputReq.split(" ");
                outputReq = "Msg From Server ";

                for (int i = 0; i < chaines.length; i++) {
                    outputReq = outputReq + " Indice : " + i + " Mot : " + chaines[i];
                    System.out.println(outputReq);
                }
                os.println(outputReq + "\n");
                os.flush();

                //TODO ADJUST THE DATA
                if (chaines[0].equals("play")) {                //play
                    etat = 1;
                    int delayS = Integer.parseInt(chaines[1]);
                    myServer.getIContext().play(clientSocket, delayS);
                    System.out.println(outputReq + " val de etat " + etat);

                } else if (chaines[0].equals("pause")) {        //pause
                    etat = 2;
                    myServer.getIContext().pause();
                    System.out.println(outputReq + " val de etat " + etat);
                } else if (chaines[0].equals("forward")) {      //forward
                    etat = 3;
                    myServer.getIContext().goForward();
                    System.out.println(outputReq + " val de etat " + etat);
                } else if (chaines[0].equals("backwards")) {    //backwards
                    etat = 4;
                    myServer.getIContext().goBackwards();
                    System.out.println(outputReq + " val de etat " + etat);
                } else if (chaines[0].equals("stop")) {         //stop
                    etat = 0;
                    myServer.getIContext().stop();
                } else if (chaines[0].equals("END_OF_SIMULATION")){     //client leaves
                    System.out.println("End of the simulation with the client");
                    break;
                }

                if (endOfTransmission){
                    os.println("END_OF_TRANSMISSION");
                    endOfTransmission = false; //reset status
                }

            }
            clientSocket.close();
            os.close();
            is.close();

        //EXCEPTIONS MANAGEMENT
        } catch (IOException e) {
            e.printStackTrace();

        } catch (EndOfSimulationException e){
            String message = e.getMessage();
            os.println("INFO " + message);
            os.println("END_OF_TRANSMISSION");
            os.close();
            try {
                is.close();
            } catch (IOException err){
                err.printStackTrace();;
            }

        } catch (StartOfSimulationException e){
            String message = e.getMessage();
            os.println("INFO " + message);
            os.close();
            try {
                is.close();
            } catch (IOException err){
                err.printStackTrace();
            }
        } catch (NumberFormatException e){
            os.println("The delay you have sent should be an integer");
            os.close();
            try {
                is.close();
            } catch (IOException err){
                err.printStackTrace();
            }
        }

    }
    @Override
    public void propertyChange(PropertyChangeEvent evt){
        if (evt.getPropertyName().equals("END_OF_TRANSMISSION")){
            this.endOfTransmission = (Boolean)evt.getNewValue();
        }

    }
}
