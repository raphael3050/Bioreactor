package org.example.bioreactor.client;

import org.json.simple.parser.ParseException;

public interface StrategyDataParser {

    Data parseData(String data);

}
