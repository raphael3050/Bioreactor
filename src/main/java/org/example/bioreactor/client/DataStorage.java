package org.example.bioreactor.client;

import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;


public class DataStorage {

    /**
     * The type of data that the storage can hold
     */
    public enum DataType {
        JSON, /* JSON data */
        // Other formats can be added here
    }

    private final PropertyChangeSupport pcs;

    private List<Object> data;
    private StrategyDataParser parser;

    public DataStorage(DataType type){
        this.data = new LinkedList<>();
        this.setDataType(type);
        pcs = new PropertyChangeSupport(this);
    }

    public void addData(String data){
        this.data.add(parser.parseData(data));
        pcs.firePropertyChange("new_data", null,this.data);
    }


    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
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
}
