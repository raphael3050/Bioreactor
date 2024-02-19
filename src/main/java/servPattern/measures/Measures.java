package servPattern.measures;

import servPattern.sensor.Sensor;

import java.time.LocalDateTime;
import java.util.List;

public class Measures {

    private LocalDateTime localDateTime;
    private List<Sensor> sensors;
    private String comment;

    public Measures(LocalDateTime localDateTime, List<Sensor> sensors, String comment){
        this.localDateTime = localDateTime;
        this.sensors = sensors;
        this.comment = comment;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public String getComment() {
        return comment;
    }
}
