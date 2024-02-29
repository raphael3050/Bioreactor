package org.example.bioreactor.server.measures;

import org.example.bioreactor.server.ISensor;
import org.example.bioreactor.server.sensor.Oxygen;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Measures {

    private LocalDateTime localDateTime;
    private List<ISensor> ISensors;
    private String comment;

    public Measures(){
        this.ISensors = new ArrayList<>();
    }
    public Measures(LocalDateTime localDateTime, List<ISensor> ISensors, String comment){
        this.localDateTime = localDateTime;
        this.ISensors = ISensors;
        this.comment = comment;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public List<ISensor> getSensors() {
        return ISensors;
    }

    public String getComment() {
        return comment;
    }

    public void addSensors(ISensor ISensor){
        this.ISensors.add(ISensor);
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
