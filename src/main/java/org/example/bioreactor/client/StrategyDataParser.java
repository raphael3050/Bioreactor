package org.example.bioreactor.client;

/**
 * Interface for different data parsers
 */
public interface StrategyDataParser {

    Data parseData(String data);

}
