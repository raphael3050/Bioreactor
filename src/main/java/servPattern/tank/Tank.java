package servPattern.tank;

import servPattern.IContext;
import servPattern.fileParser.FileParser;
import servPattern.measures.Measures;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Tank implements IContext {
    private List<Measures> measuresList;
    private Measures lastUpdate;

    public Tank(){
        FileParser fp = new FileParser("2022-10-03-Act2-1.xlsx");
        this.measuresList = fp.getMeasuresList();
    }

    public Tank(List<Measures> measuresList, Measures lastUpdate){
        this.measuresList = measuresList;
        this.lastUpdate = lastUpdate;
    }

    public Tank(String filename) throws FileNotFoundException {
        FileParser fp = new FileParser(filename);
        this.measuresList = fp.getMeasuresList();

    }




}
