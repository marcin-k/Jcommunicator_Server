package myServer;

import javafx.collections.ObservableList;
import myServer.model.ArrayOfClients;

import java.net.Socket;

/**
 * Created by marcin on 28/05/2016.
 */
public class ServersController {
//------------Singleton elements of the class---------------------
    private static ServersController instance = null;
    private ArrayOfClients connectedClients;
    private ServersController(){
        connectedClients = new ArrayOfClients();
    }
    private static MyLogic logic;

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
    public Socket getSocket(int address){
        return connectedClients.getSocket(address);
    }
    public boolean isUserOnline(int address){
        return logic.checkIfOnline(address);
    }
    public void killThread(int address){
        logic.killThread(address);
    }

}
