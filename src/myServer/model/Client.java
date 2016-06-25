package myServer.model;

import java.net.Socket;

/**
 * Created by marcin on 19/06/2016.
 */
public class Client {
    private Socket socket;
    private int address;
    //Something user can update and share with other
    private String description;

    public Client(Socket socket, int address){
        this.socket = socket;
        this.address = address;
    }

    public Socket getSocket() {
        return socket;
    }

//for debugging purposes only
    public int getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
