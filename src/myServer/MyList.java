package myServer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Created by marcin on 29/05/2016.
 */
public class MyList {
    private static ObservableList<String> appLog;
    MyList(){
    	appLog = FXCollections.observableList(new ArrayList<>());
    }
    public void add(String text){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                appLog.add(text);
            }
        });
    }
    public static ObservableList<String> get(){
        return appLog;
    }
}

