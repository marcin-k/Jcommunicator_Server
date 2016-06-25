package myServer;

import msg.FloatingMsg;

import java.io.*;
import java.net.Socket;

import static myServer.MyLogic.appLog;

/**
 * Created by marcin on 18/06/2016.
 */
public class ForEveryClientThread extends Thread {
    //!
    protected Socket incommingSocket;

    public ForEveryClientThread(Socket clientSocket) {
        this.incommingSocket = clientSocket;
    }

    ObjectInputStream in = null;
    ObjectOutputStream out = null;
    FloatingMsg msg = null;

    public Socket outGoingSocket;
    public void run() {

//----------------------------Trying to catch Hello packet from client--------------------------------
        try {
            in = new ObjectInputStream(incommingSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FloatingMsg msg = null;
        try {
            msg = (FloatingMsg) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(msg.getSpecialInfo()!=1){
            System.out.println("communication error");
        }
        else{
            //record user address and socket
            System.out.println(msg.getSender()+" establishes connections" );
            appLog.add("New Connection, remote port: "+incommingSocket.getRemoteSocketAddress());
            ServersController.getInstance().addClient(msg.getSender(), incommingSocket);
        }

//-----------------------------------------------------------------------------------------------------
        while(true) {
            //FloatingMsg msg = null;
            try {
                sleep(500);
                in = new ObjectInputStream(incommingSocket.getInputStream());
                msg = (FloatingMsg) in.readObject();
                appLog.add(msg.getMessage() + " from address: " + msg.getSender() + " to address: " + msg.getRecipient());
                System.out.println("msg to: " + msg.getRecipient() + " on received port " + incommingSocket.getRemoteSocketAddress());

                outGoingSocket = ServersController.getInstance().getSocket(msg.getRecipient());
                appLog.add(outGoingSocket + " -port to recipient");

                out = new ObjectOutputStream(outGoingSocket.getOutputStream());
                out.writeObject(msg);
                // out.writeObject(new FloatingMsg(msg.getSender(), msg.getRecipient(), msg.getMessage(), 0));

            } catch (NullPointerException e){
                try {
                    out = new ObjectOutputStream(incommingSocket.getOutputStream());
                    out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),"Offline", 0, "Server",""));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (EOFException e){
                System.out.println("Client Disconnected");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }

        }

    }

}
