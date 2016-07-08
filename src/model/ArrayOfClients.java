package model;

import java.net.Socket;

/**
 * Created by marcin on 19/06/2016.
 */
public class ArrayOfClients {

    //Array stores socket and status for every connected client
    private Client[] connectedClients;

    //Constant stores maximum number of clients that can be stored in the array
    private final int MAX_NUMBER_OF_CLIENTS = 20;

    //Constructor
    public ArrayOfClients(){
        connectedClients = new Client[MAX_NUMBER_OF_CLIENTS];
    }

    //Adds client to the array
    //int positions refers to clients address, server stores each client on its address position
    //e.g. clients address is 4, the client would be store on position [4] in the array
    public void addClient(int position, Socket socket){
        connectedClients[position]= new Client(socket, position);
    }

    //Removes a client from the array (upon client disconnection)
    public void removeClient(int position){
        connectedClients[position]=null;
    }

    //Returns specific Client object
    public Client getClient(int position){
        return connectedClients[position];
    }
}
