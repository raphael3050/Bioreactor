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
    public Data parseData(String data){
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(data);
            date = (String) jsonObject.get("date");
            temperature = Double.parseDouble((String) jsonObject.get("temperature"));
            oxygen = Double.parseDouble((String) jsonObject.get("oxygen"));
            ph = Double.parseDouble((String) jsonObject.get("ph"));
        } catch (ClassCastException | NumberFormatException | org.json.simple.parser.ParseException e) {
            System.err.println(e.getLocalizedMessage());
        }
        return new Data(date, temperature, oxygen, ph);
    }

}
