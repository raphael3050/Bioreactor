package org.example.bioreactor.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
/**
 * This class is responsible for parsing JSON data.
 */
public class JsonDataParser implements StrategyDataParser{

    private String date;
    private Double temperature;
    private Double oxygen;
    private Double ph;

    /**
     * This method parses the data and returns a Data object.
     * @param data: the data to be parsed.
     * @return a Data object.
     */
    @Override
    public Data parseData(String data){
        try {
            // Créer un objet ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Lire la chaîne JSON dans un arbre JsonNode
            JsonNode rootNode = mapper.readTree(data);

            // Extraire les valeurs
            date = rootNode.get("date").asText();
            temperature = rootNode.get("temperature").asDouble();
            ph = rootNode.get("ph").asDouble();
            //comment = rootNode.get("comment").asText();
            oxygen = rootNode.get("oxygen").asDouble();

        } catch (ClassCastException | NumberFormatException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new Data(date, temperature, oxygen, ph);
    }

}
