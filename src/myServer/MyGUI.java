package myServer;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MyGUI extends Application{
    TextField statusColor = new TextField();
	@Override
	public void start(Stage primaryStage) throws Exception {
        BorderPane rootNode = new BorderPane();

        Button start = new Button("Start Server");
        Button pause = new Button("Pause Server");


        statusColor.setEditable(false);
        statusColor.setStyle("-fx-background-color: red");
        statusColor.setText("Servers OFF");

        start.setOnAction(e -> {
            //passing port number
            if(!statusColor.getText().equals("Servers ON")){
                ServersController.getInstance().startServer(1777);
                statusColor.setStyle("-fx-background-color: green");
                statusColor.setText("Servers ON");
            }

        });
        pause.setOnAction(e -> {
            if(!statusColor.getText().equals("Servers OFF")){
                ServersController.getInstance().stop();
                statusColor.setStyle("-fx-background-color: red");
                statusColor.setText("Servers OFF");
            }
        });

        ObservableList<String> log = ServersController.getInstance().getLog();
        ListView<String> myLog = new ListView<>(log);


        rootNode.setPadding(new Insets(10,10,10,10));
        HBox buttons = new HBox();
        buttons.getChildren().addAll(start, pause, statusColor);
        rootNode.setTop(buttons);
        rootNode.setCenter(myLog);

        Scene myScene = new Scene(rootNode, 400, 450);
        primaryStage.setTitle("Server Side JCommunicator");
        primaryStage.setScene(myScene);
        primaryStage.show();
    }
    //kills the other thread upon window close
    public void stop(){
        if(statusColor.getText().equals("Servers ON"))
            ServersController.getInstance().stop();
    }
	public static void main(String[] args){
		launch(args);
	}
}
