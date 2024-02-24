package servPattern.sensor;

import servPattern.ISensor;

public class PH implements ISensor {
    private double measuredValue;
    private double initValue;
    private double commandValue;


    public PH(){
        this.measuredValue = this.initValue = this.commandValue = 0.0;
    }

    public PH(double measuredValue, double initValue, double commandValue){
        this.measuredValue = measuredValue;
        this.initValue = initValue;
        this.commandValue = commandValue;
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
