package org.example.bioreactor.server.sensor;

public class Sensor {

    private String name;
    private double measuredValue;
    private double initValue;
    private double commandValue;


    public Sensor(){
        this.name = "";
        this.measuredValue = this.initValue = this.commandValue = 0.0;
    }

    public Sensor(String name, double measuredValue, double initValue, double commandValue){
        this.name = name;
        this.measuredValue = measuredValue;
        this.initValue = initValue;
        this.commandValue = commandValue;
    }

    public String getName(){
        return this.name;
    }

    public double getMeasuredValue(){
        return this.measuredValue;
    }

    public double getInitValue(){
        return this.initValue;
    }

    public double getCommandValue(){
        return this.commandValue;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public void setMeasuredValue(double newValue){
        this.measuredValue = newValue;
    }

    public void setinitValue(double newValue){
        this.initValue = newValue;
    }

    public void setCommandValue(double newValue){
        this.commandValue = newValue;
    }


}
