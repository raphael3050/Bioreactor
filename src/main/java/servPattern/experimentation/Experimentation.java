package servPattern.experimentation;

import servPattern.IContext;
import servPattern.ISensor;
import servPattern.fileParser.FileParser;
import servPattern.measures.Measures;
import servPattern.sensor.Oxygen;
import servPattern.sensor.PH;
import servPattern.sensor.Temperature;
import servPattern.serverException.EndOfSimulationException;
import servPattern.serverException.StartOfSimulationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONObject;


public class Experimentation implements IContext {
    private List<Measures> measuresList;
    private Measures lastUpdate;
    private ScheduledExecutorService scheduler;

    private int indice; //indice related to which file is being read.

    public Experimentation(){
        FileParser fp = new FileParser("2022-10-03-Act2-1.xlsx");
        this.measuresList = fp.getMeasuresList();
        this.indice = 0;
        scheduler = Executors.newSingleThreadScheduledExecutor();

    }

    public Experimentation(List<Measures> measuresList, Measures lastUpdate){
        this.measuresList = measuresList;
        this.lastUpdate = lastUpdate;
        this.indice = 0;
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public Experimentation(String filename) throws FileNotFoundException {
        FileParser fp = new FileParser(filename);
        this.measuresList = fp.getMeasuresList();
        this.indice = 0;
        scheduler = Executors.newSingleThreadScheduledExecutor();

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
    @Override
    public void play(Socket clientSocket, int delayS) throws IOException, EndOfSimulationException {
        int numberOfMeasures = this.measuresList.size();
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
                while (this.indice < this.measuresList.size() && !this.scheduler.isShutdown()){
                    JSONObject jsonObject = this.convertToJSON(this.measuresList.get(this.indice));
                    os.println(jsonObject);
                    os.flush();
                    this.indice++;
                }
            }, 0, delayS, TimeUnit.SECONDS);

        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        os.close(); //close the stream
        //reset the indice once the end of the simulation is reached
        if (this.indice == this.measuresList.size() - 1){
            this.resetIndice();
            throw new EndOfSimulationException("End of simulation reached");
        }
    }


    /**
     * Pauses the simulation, keeps the indice at the same place.
     */
    @Override
    public void pause(){
        this.scheduler.shutdown();
    }


    /**
     * Interrupts the simulation. Resets the indice of lecture.
     */
    @Override
    public void stop(){
        this.scheduler.shutdown();
        this.resetIndice();
    }


    /**
     * Gets the next measure of the simulation. If the simulation reaches its end, throws an exception
     * @return the next measure (object of type Measures)
     * @throws EndOfSimulationException: a message to specify that the user reached the end of the simulation
     */
    @Override
    public Measures goForward() throws EndOfSimulationException {
        if (this.indice == this.measuresList.size() - 1){
            throw new EndOfSimulationException("The simulation reached its last values");
        }
        this.indice++;
        return this.measuresList.get(this.indice);
    }


    /**
     * Returns the previous measure. If the indice is already at the beginning of the list of measures, throws
     * an exception.
     * @return the previous measure, as an object of type Measures
     * @throws StartOfSimulationException: a specific message explaining that the beginning of the list was reached
     */
    @Override
    public Measures goBackwards() throws StartOfSimulationException {
        if (this.indice == 0) {
            throw new StartOfSimulationException("The simulation reached its first measure");
        }
        this.indice--;
        return this.measuresList.get(this.indice);
    }


    /**
     * Private method to reset the indice related to the list of measures.
     */
    private void resetIndice(){
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
