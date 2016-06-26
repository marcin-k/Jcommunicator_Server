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
    int address = 0;

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
            //initiate the address of the sender, this thread will be stored in address lociation
            //in the threads array
            address = msg.getSender();
        }

//-----------------------------------------------------------------------------------------------------
        boolean run = true;
        while(true && run) {
            //FloatingMsg msg = null;
            try {
                sleep(500);
                in = new ObjectInputStream(incommingSocket.getInputStream());
                msg = (FloatingMsg) in.readObject();
                appLog.add(msg.getMessage() + " from address: " + msg.getSender() + " to address: " + msg.getRecipient());
                System.out.println("msg to: " + msg.getRecipient() + " on received port " + incommingSocket.getRemoteSocketAddress());

                //message to close the thread
                if(msg.getSpecialInfo()==9){
                    Thread.currentThread().interrupt();
                    ServersController.getInstance().killThread(msg.getSender());
                    System.out.println("closing the thread, I hope");
                }
                else if(msg.getSpecialInfo()==8){
                    try {
                        out = new ObjectOutputStream(incommingSocket.getOutputStream());
                            if(ServersController.getInstance().isUserOnline(msg.getRecipient())){
                                out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),"no", 8, "Server",""));
                            }
                            else{
                                out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),"yes", 8, "Server",""));
                            }
                        ServersController.getInstance().killThread(msg.getSender());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
                else {
                    outGoingSocket = ServersController.getInstance().getSocket(msg.getRecipient());
                    appLog.add(outGoingSocket + " -port to recipient");

                    out = new ObjectOutputStream(outGoingSocket.getOutputStream());
                    out.writeObject(msg);
                    // out.writeObject(new FloatingMsg(msg.getSender(), msg.getRecipient(), msg.getMessage(), 0));
                }
//NullPointerException is thrown if the recipients socket doesn't exist (recipient is not online)
            } catch (NullPointerException e){
                try {
                    out = new ObjectOutputStream(incommingSocket.getOutputStream());
                    out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),"Offline", 0, "Server",""));
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
//InterruptedException is thrown if thread is to be stopped message with special info 9 is received
            } catch (InterruptedException e) {
                e.printStackTrace();
                run = false;
                break;
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
    public int getAddress(){
        return address;
    }

}
