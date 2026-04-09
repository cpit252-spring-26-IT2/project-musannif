package org.app.musannif.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
    String currentDateTimeString = LocalDateTime.now().format(myFormatter);
    private final String logFile = currentDateTimeString + ".txt";
    private PrintWriter writer;

    private static Logger logger;


    private Logger(){
        try {
            FileWriter fw = new FileWriter(logFile);
            writer = new PrintWriter(fw, true);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static Logger getLogger(){
        if(logger == null){
            logger = new Logger();
        }
        return logger;
    }



    public void info (String message) {
        writer.println(currentDateTimeString + "\nINFO" + message);
    }

}
