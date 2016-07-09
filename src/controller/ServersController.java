package controller;

import javafx.collections.ObservableList;
import model.ArrayOfClients;
import model.Client;

import java.net.Socket;

/**
 * Created by marcin on 28/05/2016.
 */
public class ServersController {

    private static ServersController instance = null;
    //array of clients (messages are routed using the sockets store in client object)
    private ArrayOfClients connectedClients;

    private static Logic logic;

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
//----------------------------------Non Singleton Methods-------------------------------------------
    //Starts server on a given port
    public void startServer(int port){
        logic = new Logic(port);
    }

    //Retrieves a server log
    public ObservableList<String> getLog(){
            return logic.getLog();
    }

    //Stops the server
    public void stop(){
        logic.stop();
    }

    //Checks if server is running (currently unused)
    //public static boolean isItRunning(){
    //    return ServersController.getInstance().isItRunning();
    //}

    //Adds a new client to a clients array
    public void addClient(int position, Socket socket){
        connectedClients.addClient(position, socket);
    }

    //Removes a client from the array (if client is disconnected)
    public void removeClient(int position){
        connectedClients.removeClient(position);
    }

    //retrieves a client at selected position in the array
    public Client getClient(int address){
        return connectedClients.getClient(address);
    }

    //Checks if client is connected (Thread array at position client is pointing to something)
    public boolean isUserOnline(int address){
        return logic.checkIfOnline(address);
    }

    //Closes the thread for a client if disconnected
    public void killThread(int address){
        logic.killThread(address);
    }

    //Kills all clients threads
    public void killAllThreads(){
        logic.killAllThreads();
    }

}
