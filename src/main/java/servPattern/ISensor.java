package servPattern;

public interface ISensor {

    double getMeasuredValue();
    double getInitValue();
    double getCommandValue();
    void setMeasuredValue(double newValue);
    void setinitValue(double newValue);
    void setCommandValue(double newValue);

}
