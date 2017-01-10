package controller;

import javafx.collections.ObservableList;
import model.MyList;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by marcin on 28/05/2016.
 *
 * Class is used to:
 * - open a new Threads for each incoming connection (ClientsThread)
 * - maintains a status of each connection
 * - determines the online/offline users
 */
@SuppressWarnings("serial")
public class Logic implements Runnable, Serializable{
    //Socket on the server listening for connections
    private ServerSocket serverSocket;

    //Instance of a list (server log)
    static MyList appLog = new MyList();

    //initialize the server port (further changed by gui)
    int port=0;

    Thread t;

    //array of threads for each client connection
    ClientsThread[] clientThreadArray = new ClientsThread[10];

    // constructor sets socket on the given port
    public Logic(int port){
        try {
            this.port = port;
        	t = new Thread(this, "Server Thread");
            t.start();
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    //method displays open and closed thread (for debugging purposys only)
    /*public String getOpenThreads(){
        String threadInfo = "+-----------------------------------------------------------+"+System.getProperty("line.separator");
        int i =0;
        for(Thread t: clientThreadArray){
            if(t==null){
                threadInfo += i+" - is off" + System.getProperty("line.separator");
            }
            else{
                threadInfo += i+" - is on" + System.getProperty("line.separator");
            }
            i++;
        }
        threadInfo += "+-----------------------------------------------------------+";
        return threadInfo;
    }
    */

//-------------------------------------------Run Method---------------------------------------------------
    //For each new connection new thread is opened, that deals with messages received from that connection
    //each thread is then places in a array of thread at clients address position,
    public void run() {
        appLog.add("Waiting for client on port " + port + "...");
        Socket socket = null;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            clientThreadArray[0] = new ClientsThread(socket);
            clientThreadArray[0].start();
            //sleep allows new thread to process the message, before address address can be retried
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int address = clientThreadArray[0].getAddress();
            appLog.add("Thread created  "+address);
            clientThreadArray[address]= clientThreadArray[0];
            clientThreadArray[0]=null;

        }

    }
//-------------------------------------------End of Run Method--------------------------------------------
    //closes the socket upon application closure
    public void stop(){
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //retrieves server log
    public static ObservableList<String> getLog(){
        return appLog.get();
    }

    //Currently unused
    //public static boolean isItRunning(){
    //    return Thread.currentThread().isAlive();
    //}

    //checks if client is online
    public boolean checkIfOnline(int address){
        return (clientThreadArray[address]!=null);
    }

    //removes a reference to client
    public void killThread(int address){
        appLog.add("killing thread "+address);
        clientThreadArray[address].interrupt();
        clientThreadArray[address]=null;
    }

    //kills all threads
    public void killAllThreads(){
        for(ClientsThread t :clientThreadArray){
            if(t!=null){
                t.interrupt();
                clientThreadArray[t.getAddress()]=null;
            }
        }
    }

}
