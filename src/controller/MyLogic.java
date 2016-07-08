package controller;

import javafx.collections.ObservableList;
import model.MyList;

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

ForEveryClientThread[] clientThreadArray = new ForEveryClientThread[10];

    public String getOpenThreads(){
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


    public ForEveryClientThread[] getThreads(){
        return clientThreadArray;
    }
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
            clientThreadArray[0] = new ForEveryClientThread(socket);
            clientThreadArray[0].start();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int address = clientThreadArray[0].getAddress();
            System.out.println("Thread created  "+address);
            System.out.println("I expect that to happen only once for each client");
            clientThreadArray[address]= clientThreadArray[0];
            clientThreadArray[0]=null;
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
    public boolean checkIfOnline(int address){
        if (clientThreadArray[address]==null){
            return false;
        }
        else
            return true;
    }
    public void killThread(int address){
        System.out.println("killing thread "+address);
        clientThreadArray[address]=null;
    }
}
