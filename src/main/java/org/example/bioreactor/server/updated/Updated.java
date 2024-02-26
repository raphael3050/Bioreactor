package org.example.bioreactor.server.updated;

import org.example.bioreactor.server.sensor.Sensor;
import org.example.bioreactor.server.serverException.SensorException;

import java.time.LocalDateTime;
import java.util.List;

public class Updated {

    private LocalDateTime localDateTime;
    private List<Sensor> sensorList;
    private String lastComment;

    public Updated(LocalDateTime dateTime, List<Sensor> sensors, String lastComment){
        this.localDateTime = dateTime;
        this.sensorList = sensors;
        this.lastComment = lastComment;
    }

    /**
     * Getter: gets the localdatetime parameter.
     * @return the value of the date as a LocalDateTime object.
     */
    public LocalDateTime getLocalDateTime(){
        return this.localDateTime;
    }


    /**
     * Getter : gets all the sensors contained in the Change object
     * @return all Sensors of the sensorList
     */
    public List<Sensor> getSensorList(){
        return this.sensorList;
    }

    /**
     * Returns a specific sensor which name is given as an argument. If the sensor is not found, an exception
     * is thrown.
     * @param sensorName: the sensor that is researched, as a String.
     * @return the sensorName
     * @throws SensorException if the sensor name has no correspondance in the sensorList.
     */
    public Sensor getSensor(String sensorName) throws SensorException {
        for (Sensor sensor : this.sensorList){
            if (sensor.getName().equals(sensorName)){
                return sensor;
            }
        }
        throw new SensorException("The sensor you are looking for was not found : " + sensorName);
    }
}
