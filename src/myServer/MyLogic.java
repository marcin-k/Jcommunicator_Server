package myServer;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by marcin on 28/05/2016.
 */
public class MyLogic implements Runnable, Serializable{
    private ServerSocket serverSocket;
    static MyList appLog = new MyList();
    int port=0;
    Thread t;


    // constructor sets socket on the given port
    public MyLogic(int port){
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
//----------------------------Modifications 20JUN----------------------------------
int numberOfOpenThread = 0;
ForEveryClientThread[] myArry = new ForEveryClientThread[10];



//----------------------------Modifications 20JUN---------------------------------
    public void run() {
        appLog.add("Waiting for client on port " + port + "...");

//---------------------------------------MODIFICATIONS----------------------------------------------------------------------------

        Socket socket = null;
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new threat for a client
            //----------------------------Modifications 20JUN---------------------------------
            //new ForEveryClientThread(socket).start();
            myArry[numberOfOpenThread] = new ForEveryClientThread(socket);
            myArry[numberOfOpenThread].start();
            System.out.println("Thread created  "+numberOfOpenThread);
            numberOfOpenThread++;
            //----------------------------Modifications 20JUN---------------------------------
        }

//---------------------------------------MODIFICATIONS----------------------------------------------------------------------------
    }
        public static ObservableList<String> getLog(){
        return appLog.get();
    }

    public void stop(){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public static boolean isItRunning(){
        return Thread.currentThread().isAlive();
    }
}
