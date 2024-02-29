package org.example.bioreactor.client;

import org.json.simple.parser.ParseException;

public interface StrategyDataParser {

    Object parseData(String data);

    String getDate();

    Double getTemperature();

    Double getOxygen();

    Double getPh();

}
