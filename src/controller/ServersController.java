package controller;

import javafx.collections.ObservableList;
import model.ArrayOfClients;
import model.Client;

import java.net.Socket;

/**
 * Created by marcin on 28/05/2016.
 */
public class ServersController {
//------------Singleton elements of the class---------------------
    private static ServersController instance = null;
    private ArrayOfClients connectedClients;
    private static MyLogic logic;

    //Constructor
    private ServersController(){
        connectedClients = new ArrayOfClients();
    }

    public static ServersController getInstance(){
        if(instance==null){
            instance = new ServersController();
        }
        return instance;
    }
//----------------------------------------------------------------
    public void startServer(int port){
        logic = new MyLogic(port);
    }
    public ObservableList<String> getLog(){
            return logic.getLog();
    }
    public void stop(){
        logic.stop();
    }
    public static boolean isItRunning(){
        return ServersController.getInstance().isItRunning();
    }
//-----------------Client Related Methods-----------------------------------------------
    public void addClient(int position, Socket socket){
        connectedClients.addClient(position, socket);
    }
    public void removeClient(int position){
        connectedClients.removeClient(position);
    }

    public Client getClient(int address){
        return connectedClients.getClient(address);
    }
    public boolean isUserOnline(int address){
        return logic.checkIfOnline(address);
    }
    public void killThread(int address){
        logic.killThread(address);
    }

}
