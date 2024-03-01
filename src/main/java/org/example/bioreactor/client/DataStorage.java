package org.example.bioreactor.client;

import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;


/**
 * Class that stores the data received from the server
 */
public class DataStorage {

    /**
     * The type of language used to communicate with the server
     */
    public enum DataType {
        JSON, /* JSON data */
        // Other formats can be added here
    }

    private final PropertyChangeSupport pcs;

    private List<Data> data;
    private StrategyDataParser parser;

    public DataStorage(DataType type){
        this.data = new LinkedList<>();
        this.setDataType(type);
        pcs = new PropertyChangeSupport(this);
    }

    public void addData(String data){
        this.data.add(parser.parseData(data));
        pcs.firePropertyChange("new_data", null,this.data.get(this.data.size()-1));
    }


    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }


    public void setDataType(DataType type){
        switch (type){
            case JSON:
                this.parser = new JsonDataParser();
                break;
            default:
                throw new IllegalArgumentException("** Invalid data type");
        }
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return this.pcs;
    }
}
