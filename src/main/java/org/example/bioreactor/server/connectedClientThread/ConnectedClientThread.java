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
import java.util.Arrays;


public class ConnectedClientThread extends Thread implements PropertyChangeListener {

    private Socket clientSocket;
    private TCPServer myServer;
    int etat = 0;
    private boolean endOfTransmission = false;
    private String [] chaines;

    public enum Command {
        PLAY,
        PAUSE,
        FORWARD,
        BACKWARD,
        STOP,
        END_OF_TRANSMISSION,
        END_OF_SIMULATION,
        BEGINNING_OF_SIMULATION,
    }

    public ConnectedClientThread( Socket aClientSocket , TCPServer aServer ) {
        this.clientSocket = aClientSocket;
        this.myServer = aServer;
        this.myServer.getIContext().getPropertyChangeSupport().addPropertyChangeListener(this);
        this.chaines = null;
    }

    public int getEtat() {
        return etat;
    }

    public void run() {
        String inputReq;

        BufferedReader is = null;
        PrintStream os = null;
        try {
            /* Ouverture des objets de type Stream sur la socket du client réseau  */
            is = new BufferedReader ( new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());

            System.out.println( "Client Thread " );

            // TODO : Debug this section, it is not working as expected
            while ( (inputReq = is.readLine()) != null && etat != 3 ) {
                String[] chaines = inputReq.split(" ");
                System.out.println("Message reçu : "+Arrays.toString(chaines));
                this.chaines = inputReq.split(" ");


                //TODO ADJUST THE DATA
                if (this.chaines[0].equals(Command.PLAY.toString())) {                //play
                    if (this.etat == 1){ //make sure not to be able to play twice
                        continue;
                    }
                    this.etat = 1;
                    int delayMS = Integer.parseInt(this.chaines[1]);
                    this.myServer.getIContext().play(clientSocket, delayMS);
                    this.resetChaines();

                } else if (chaines[0].equals(Command.PAUSE.toString())) {        //pause
                    if (this.etat == 2){ //pausing twice does nothing
                        continue;
                    }
                    this.etat = 2;
                    this.myServer.getIContext().pause(clientSocket);
                    this.resetChaines();
                } else if (chaines[0].equals(Command.FORWARD.toString())) {      //forward
                    this.etat = 3;
                    this.myServer.getIContext().goForward();
                    this.resetChaines();
                } else if (chaines[0].equals(Command.BACKWARD.toString())) {    //backwards
                    this.etat = 4;
                    this.myServer.getIContext().goBackwards();
                    this.resetChaines();
                } else if (chaines[0].equals(Command.STOP.toString())) {         //stop
                    if (this.etat == 5){ //stopping is allowed only once per playing process
                        continue;
                    }
                    this.etat = 5;
                    this.myServer.getIContext().stop(clientSocket);
                    this.resetChaines();
                } else if (chaines[0].equals(Command.END_OF_SIMULATION)){     //client leaves
                    this.etat = 0;
                    System.out.println("End of the simulation with the client");
                    this.resetChaines();
                    break;
                }

                if (endOfTransmission){
                    os.println(Command.END_OF_TRANSMISSION);
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
            os.println(Command.END_OF_TRANSMISSION);
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
        if (evt.getPropertyName().equals(Command.END_OF_TRANSMISSION)) {
            this.endOfTransmission = (Boolean) evt.getNewValue();
        }
    }


    public void resetChaines(){
        this.chaines = null;
    }
}
