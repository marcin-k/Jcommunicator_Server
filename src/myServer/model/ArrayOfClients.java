package myServer.model;

import java.net.Socket;

/**
 * Created by marcin on 19/06/2016.
 */
public class ArrayOfClients {
    private Client[] connectedClients;
    private final int MAX_NUMBER_OF_CLIENTS = 20;

    public ArrayOfClients(){
        connectedClients = new Client[MAX_NUMBER_OF_CLIENTS];
    }

    //int positions refers to clients address server sores each client and its address position
    //e.g. clients address is 4, the client would be store on position [4] in the array
    public void addClient(int position, Socket socket){
        connectedClients[position]= new Client(socket, position);
    }
    public void removeClient(int position){
        connectedClients[position]=null;
    }
    public Socket getSocket(int address){
        return connectedClients[address].getSocket();
    }
}
