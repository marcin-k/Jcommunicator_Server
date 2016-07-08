package model;

import java.net.Socket;

/**
 * Created by marcin on 19/06/2016.
 */
public class Client {

    private Socket socket;

    //private int address;

    //Status description displayed by the client
    private String description;

    //Class constructor
    public Client(Socket socket, int address){
        this.socket = socket;
        //this.address = address;
        description = "";
    }

    //returns each clients socket used for routing messages
    public Socket getSocket() {
        return socket;
    }

    //for debugging purposes only each clients thread is always stored on address position in the treads array
    //public int getAddress() {
    //    return address;
    //}

    //return the status (description)
    public String getDescription() {
        return description;
    }

    //sets status (description)
    public void setDescription(String description) {
        this.description = description;
    }
}
