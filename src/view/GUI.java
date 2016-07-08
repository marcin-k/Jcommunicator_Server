package view;

import controller.ServersController;
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

public class GUI extends Application{
    //Text field used to indicate server status (on/off)
    TextField statusColor = new TextField();

	@Override
	public void start(Stage primaryStage) throws Exception {
        //initiate the status indicator (not running state)
        statusColor.setEditable(false);
        statusColor.setStyle("-fx-background-color: red");
        statusColor.setText("Servers OFF");

        //--------------Start button----------------------------------------
        Button start = new Button("Start Server");
        start.setOnAction(e -> {
            //passing port number
            if(!statusColor.getText().equals("Servers ON")){
                ServersController.getInstance().startServer(1777);
                statusColor.setStyle("-fx-background-color: green");
                statusColor.setText("Servers ON");
            }

        });
        //--------------Pause button----------------------------------------
        Button pause = new Button("Pause Server");
        pause.setOnAction(e -> {
            if(!statusColor.getText().equals("Servers OFF")){
                ServersController.getInstance().stop();
                statusColor.setStyle("-fx-background-color: red");
                statusColor.setText("Servers OFF");
            }
        });
        //-------------------------------------------------------------------

        //observable list display log of operations/messages received by a server
        ObservableList<String> log = ServersController.getInstance().getLog();
        ListView<String> myLog = new ListView<>(log);

        //buttons container (start/stop)
        HBox buttons = new HBox();
        buttons.getChildren().addAll(start, pause, statusColor);

        //Main container stores all other nodes
        BorderPane rootNode = new BorderPane();
        rootNode.setPadding(new Insets(10,10,10,10));
        rootNode.setTop(buttons);
        rootNode.setCenter(myLog);

        Scene myScene = new Scene(rootNode, 400, 450);

        primaryStage.setTitle("Server Side JCommunicator");
        primaryStage.setScene(myScene);
        primaryStage.show();
    }
//Kills all running threads upon application close
    public void stop(){
        if(statusColor.getText().equals("Servers ON"))
            ServersController.getInstance().stop();
    }
//Starter, triggers the start of application
	public static void main(String[] args){
		launch(args);
	}
}
