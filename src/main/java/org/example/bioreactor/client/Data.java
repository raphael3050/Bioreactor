package org.example.bioreactor.client;

public class Data {

    private String date;
    private Double temperature;
    private Double oxygen;
    private Double ph;

    public Data(String date, Double temperature, Double oxygen, Double ph) {
        this.date = date;
        this.temperature = temperature;
        this.oxygen = oxygen;
        this.ph = ph;
    }

    public String getDate() {
        return date;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getOxygen() {
        return oxygen;
    }

    public Double getPh() {
        return ph;
    }
}
