package org.example.bioreactor.server.experimentation;

import org.example.bioreactor.server.IContext;
import org.example.bioreactor.server.ISensor;
import org.example.bioreactor.server.connectedClientThread.ConnectedClientThread;
import org.example.bioreactor.server.fileParser.FileParser;
import org.example.bioreactor.server.measures.Measures;
import org.example.bioreactor.server.sensor.Oxygen;
import org.example.bioreactor.server.sensor.PH;
import org.example.bioreactor.server.sensor.Temperature;
import org.example.bioreactor.server.serverException.EndOfSimulationException;
import org.example.bioreactor.server.serverException.StartOfSimulationException;

import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.*;
import org.json.simple.JSONObject;


public class Experimentation implements IContext {
    private List<Measures> measuresList;
    private Measures lastUpdate;
    private ScheduledExecutorService scheduler;
    private PropertyChangeSupport pcs;
    private int indice; //indice related to which file is being read.
    private static final Logger LOGGER =  LogManager.getLogger( Experimentation.class );

    public Experimentation(String filename) throws IOException {
        FileParser fp = new FileParser(filename);
        fp.parse();
        this.measuresList = fp.getMeasuresList();
        this.indice = 0;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        pcs = new PropertyChangeSupport(this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.pcs;
    }

    //TODO remove this method?
    /**
     *
     * @return: the last command that was sent to the server.
     */
    public Measures getLastUpdate() {
        return this.lastUpdate;
    }


    /**
     * Sends in a cyclic way the objects belonging to the measuresList after they got converted into JSON objects.
     * Any interruption through the 'pause' or 'stop' methods kill the scheduler. If the play method is ran again,
     * creates a new scheduler to finish all data sendings. Once all the data is sent, throws an exception.
     * @param clientSocket: the client whom to sent the data
     * @param delayS: delay between two measures are sent.
     * @throws IOException: if there is a matter with the Stream.
     * @throws EndOfSimulationException: a specific message is thrown once the end of the simualtion is reached
     */
/*
    @Override
    public void play(Socket clientSocket, int delayS) throws IOException, EndOfSimulationException {
        PrintStream os;
        try {
            os = new PrintStream(clientSocket.getOutputStream());
            // verify if the scheduler is running or not. if not, re-enable it.
            if (this.scheduler.isShutdown()){
                this.scheduler = Executors.newSingleThreadScheduledExecutor();
            }

            //keep sending data until the simulation gets manually interrupted or the end of the simulation is reached
            this.scheduler.scheduleAtFixedRate( () -> {

                //loop on the measures list
                if (this.getIndice() < this.measuresList.size() && !this.scheduler.isShutdown()){
                    JSONObject jsonObject = this.convertToJSON(this.measuresList.get(this.indice));
                    os.println(jsonObject);
                    os.println('\n');
                    os.flush();
                    this.incrementIndice();
                }
                if (this.getIndice() == this.measuresList.size() - 1){
                    this.resetIndice();
                    os.println(Command.END_OF_TRANSMISSION);
                    this.scheduler.shutdown();
                }
            }, 0, delayS, TimeUnit.SECONDS);

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }
*/

    @Override
    public void play(Socket clientSocket, int delayMS) throws IOException, EndOfSimulationException {
        try {
            LOGGER.info("Starting the simulation [play command received]");
            // Création d'un writer pour écrire dans le flux de sortie du socket
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Vérification si le planificateur est en cours d'exécution ou non. Si non, réactivez-le.
            if (this.scheduler.isShutdown()) {
                this.scheduler = Executors.newSingleThreadScheduledExecutor();
            }

            // Envoyer les données JSON à intervalles réguliers jusqu'à la fin de la simulation
            this.scheduler.scheduleAtFixedRate(() -> {
                // Parcourir la liste des mesures
                if (this.getIndice() < this.measuresList.size() && !this.scheduler.isShutdown()) {
                    JSONObject jsonObject = this.convertToJSON(this.measuresList.get(this.getIndice()));
                    String jsonString = jsonObject.toString()+"\n";
                    LOGGER.info("Sending the following JSON object: " + jsonString);
                    writer.print(jsonString);
                    writer.flush();
                    this.incrementIndice();
                }
                if (this.getIndice() == this.measuresList.size() - 1) {
                    LOGGER.info("End of the simulation, sending the end of transmission command : "+ ConnectedClientThread.Command.END_OF_TRANSMISSION);
                    this.resetIndice();
                    writer.print(ConnectedClientThread.Command.END_OF_SIMULATION+"\n");
                    writer.flush();
                    this.scheduler.shutdown();
                }
            }, 0, delayMS, TimeUnit.MILLISECONDS);

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }


    /**
     * Pauses the simulation, keeps the indice at the same place.
     */
    @Override
    public void pause(Socket clientSocket){
        this.scheduler.shutdown();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.schedule(() -> {
            try{
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                writer.println(ConnectedClientThread.Command.END_OF_TRANSMISSION);
                this.scheduler.shutdown();
            } catch (IOException e){
                //todo raise specific error?
                e.printStackTrace();
            }
        }, 0, TimeUnit.SECONDS);
    }

    public synchronized int getIndice(){
        return this.indice;
    }

    public synchronized void incrementIndice(){
        this.indice++;
    }

    /**
     * Interrupts the simulation. Resets the indice of lecture.
     */
    @Override
    public void stop(Socket clientSocket){
        this.pause(clientSocket);
        this.resetIndice();
    }


    /**
     * Gets the next measure of the simulation. If the simulation reaches its end, throws an exception
     * @return the next measure (object of type Measures)
     * @throws EndOfSimulationException: a message to specify that the user reached the end of the simulation
     */
    @Override
    public String goForward(Socket clientSocket) throws EndOfSimulationException {
        if (this.indice == this.measuresList.size() - 1){
            throw new EndOfSimulationException("INFO: The simulation reached its last values");
        }
        this.indice++;
        JSONObject jsonObject = this.convertToJSON(this.measuresList.get(this.indice));
        return jsonObject.toString();
    }


    /**
     * Returns the previous measure. If the indice is already at the beginning of the list of measures, throws
     * an exception.
     * @return the previous measure, as an object of type Measures
     * @throws StartOfSimulationException: a specific message explaining that the beginning of the list was reached
     */
    @Override
    public String goBackwards() throws StartOfSimulationException {
        if (this.indice == 0) {
            throw new StartOfSimulationException("INFO: The simulation reached its first measure");
        }
        this.indice--;
        JSONObject jsonObject = this.convertToJSON(this.measuresList.get(this.indice));
        return jsonObject.toString();
    }


    /**
     * Private method to reset the indice related to the list of measures.
     */
    private synchronized void resetIndice(){
        this.indice = 0;
    }


    /**
     * Converts a measure into a JSON object and returns the newly created JSON object
     * @param measures: the object of type Measures to convert
     * @return a jsonObject corresponding to the Measures object's content.
     */
    private JSONObject convertToJSON(Measures measures){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("date", measures.getLocalDateTime());
        for (ISensor iSensor : measures.getSensors()){
            if (iSensor instanceof Temperature) {
                jsonObject.put("temperature", Double.toString(iSensor.getMeasuredValue()));
            } else if (iSensor instanceof Oxygen) {
                jsonObject.put("oxygen", Double.toString(iSensor.getMeasuredValue()));
            } else if (iSensor instanceof PH) {
                jsonObject.put("ph", Double.toString(iSensor.getMeasuredValue()));
            }
        }
        jsonObject.put("comment", measures.getComment());

        return jsonObject;
    }


}
