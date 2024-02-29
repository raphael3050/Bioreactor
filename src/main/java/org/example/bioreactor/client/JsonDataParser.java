package org.example.bioreactor.client;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonDataParser implements StrategyDataParser{

    private final JSONParser parser = new JSONParser();

    private String date;
    private Double temperature;
    private Double oxygen;
    private Double ph;

    @Override
    public Object parseData(String data){
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(data);
            date = (String) jsonObject.get("date");
            temperature = Double.parseDouble((String) jsonObject.get("temperature"));
            oxygen = Double.parseDouble((String) jsonObject.get("oxygen"));
            ph = Double.parseDouble((String) jsonObject.get("ph"));
        } catch (ClassCastException | NumberFormatException | org.json.simple.parser.ParseException e) {
            System.err.println(e.getLocalizedMessage());
        }
        return this;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public Double getTemperature() {
        return temperature;
    }

    @Override
    public Double getOxygen() {
        return oxygen;
    }

    @Override
    public Double getPh() {
        return ph;
    }

}
