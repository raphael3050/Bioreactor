package org.example.bioreactor.server.fileParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.bioreactor.server.ISensor;
import org.example.bioreactor.server.experimentation.Experimentation;
import org.example.bioreactor.server.measures.Measures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.bioreactor.server.sensor.Oxygen;
import org.example.bioreactor.server.sensor.PH;
import org.example.bioreactor.server.sensor.Temperature;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileParser {
    private String filename;
    private List<Measures> measuresList;

    private static final Logger LOGGER =  LogManager.getLogger( FileParser.class );


    public FileParser(String filename){
        this.filename = filename;
        this.measuresList = new ArrayList<>();
    }

    public List<Measures> parse() throws FileNotFoundException, IOException {

        try {
            LOGGER.info("Parsing the file " + this.filename);
            FileInputStream file = new FileInputStream(new File(this.filename));
            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheetAt(0);
            int i = 0;
            int numRow = 0;
            int numCell = 0;
            DataFormatter dataFormatter = new DataFormatter();

            //parse the file line by line
            for (Row row : sheet) {
                LocalDateTime localDateTime = null;
                String comment = null;
                Temperature temperature = new Temperature();
                Oxygen oxygen = new Oxygen();
                PH pH = new PH();

                numRow += 1;

                //numRow 5 is where all the titles are displayed //TODO NUMROW 6 TO GRAB COLUMNS TITLES
                if (numRow < 6){ continue; }

                numCell = 0;
                // check all cells one by one
                for (Cell cell : row) {
                    numCell += 1;
                    //TODO REMOVE
                    /*//declare the used stuff
                    if (cell.getCellType() == CellType.NUMERIC) {
                        System.out.println("row " + numRow + ", cell " + numCell + ": " + cell.getNumericCellValue() + ", type : " + cell.getCellType());
                    } else if (cell.getCellType() == CellType.STRING){
                        System.out.println("row " + numRow + ", cell " + numCell + ": " + cell.getStringCellValue());
                    }*/

                    switch (numCell){
                        //datetime
                        case 1 -> {
                            String dateString = dataFormatter.formatCellValue(cell); //convert the cell content into a readable string
                            localDateTime = this.extractLocalDateTime(dateString);
                        }
                        //temperature
                        case 14 -> temperature.setMeasuredValue(cell.getNumericCellValue());
                        case 15 -> temperature.setinitValue(cell.getNumericCellValue());
                        case 16 -> temperature.setCommandValue(cell.getNumericCellValue());
                        //oxygen
                        case 17 -> oxygen.setMeasuredValue(cell.getNumericCellValue());
                        case 18 -> oxygen.setinitValue(cell.getNumericCellValue());
                        case 19 -> oxygen.setCommandValue(cell.getNumericCellValue());
                        //pH
                        case 20 -> pH.setMeasuredValue(cell.getNumericCellValue());
                        case 21 -> pH.setinitValue(cell.getNumericCellValue());
                        case 22 -> pH.setCommandValue(cell.getNumericCellValue());
                        //comment
                        case 56 -> {
                            if (cell.getStringCellValue() != null) {
                                comment = cell.getStringCellValue();
                            }
                        }
                    }
                }
                this.createMeasure(localDateTime.toString(), temperature, oxygen, pH, comment);
                i++;
            }
            LOGGER.info("File " + this.filename + " has been parsed successfully. Total of " + i + " measures have been extracted.");
        } catch (FileNotFoundException e){
            throw new FileNotFoundException("the file " + this.filename + " could not be found");
        } catch (IOException e){
            throw new IOException("There was a mistake while reading the xls file");
        }
        return null;
    }


    /**
     * Method made to extract from a xlsx file a LocalDateTime formatted date.
     * A few different patterns are proposed so the extraction works properly
     * @param dateString: a String containing the datetime to convert
     * @return a LocalDateTime object
     * @throws DateTimeException if the formatting pattern is not recognized
     */
    private LocalDateTime extractLocalDateTime(String dateString) throws DateTimeException {
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendOptional(DateTimeFormatter.ofPattern("dd/M/y H:m"))
                    .appendOptional(DateTimeFormatter.ofPattern("dd/M/y H:m:s"))
                    .toFormatter();
            return LocalDateTime.parse(dateString, formatter);
        } catch (DateTimeException e) {
            throw new DateTimeException(e.getMessage());
        }
    }

    /**
     * Creates a Measure object and adds it to the measuresList class variable.
     * @param localDateTime
     * @param temperature : a Temperature object
     * @param oxygen : an Oxygen object
     * @param pH : a PH object
     * @param comment : a String, eventually null.
     */
    private void createMeasure(String localDateTime, Temperature temperature, Oxygen oxygen, PH pH, String comment){
        Measures aMeasure = new Measures();
        aMeasure.setLocalDateTime(localDateTime);
        if (comment != null){
            aMeasure.setComment(comment);
        }
        aMeasure.addSensors((ISensor) temperature);
        aMeasure.addSensors((ISensor) oxygen);
        aMeasure.addSensors((ISensor) pH);
        this.measuresList.add(aMeasure);
    }

    /**
     * Getter on the measures list
     * @return a list of Measures objects.
     */
    public List<Measures> getMeasuresList(){ return this.measuresList; }

    /*
    public static void main(String[] args) {
        String filename = "2022-10-03-Act2-1.xlsx";
        FileParser fp = new FileParser(filename);
        try {
            fp.parse();
            List<Measures>myMeasures = fp.getMeasuresList();
            for (Measures m : myMeasures){
                System.out.println("****");
                System.out.println("Date : " + m.getLocalDateTime());
                List<ISensor>ISensors = m.getSensors();
                for (ISensor is : ISensors){
                    if (is instanceof Temperature){
                        System.out.println("Temp meas: " + is.getMeasuredValue());
                        System.out.println("Temp init: " + is.getInitValue());
                        System.out.println("Temp comm: " + is.getCommandValue());
                    } else if (is instanceof Oxygen){
                        System.out.println("O2 meas: " + is.getMeasuredValue());
                        System.out.println("O2 init: " + is.getInitValue());
                        System.out.println("O2 comm: " + is.getCommandValue());
                    } else if (is instanceof PH){
                        System.out.println("pH meas: " + is.getMeasuredValue());
                        System.out.println("pH init: " + is.getInitValue());
                        System.out.println("pH comm: " + is.getCommandValue());
                    }
                }
                if (m.getComment() != null){
                    System.out.println(m.getComment());
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }*/

}
