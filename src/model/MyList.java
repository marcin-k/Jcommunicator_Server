package model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by marcin on 29/05/2016.
 */
//Class contains a list that is used as a log display in main window
public class MyList {
    //server events log
    private static ObservableList<String> appLog;

    //constructor initialize empty list (only one instance of this class is used for each run)
    public MyList(){
    	appLog = FXCollections.observableList(new ArrayList<>());
    }

    //adds the new entries to the log (appends the list)
    public void add(String text){
        Platform.runLater(() -> appLog.add(text));
    }

    //returns the log (to be displayed)
    public static ObservableList<String> get(){
        return appLog;
    }
}

