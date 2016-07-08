package controller;

import model.FloatingMsg;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

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
            MyLogic.appLog.add("New Connection, remote port: "+incommingSocket.getRemoteSocketAddress());
            ServersController.getInstance().addClient(msg.getSender(), incommingSocket);
            //initiate the address of the sender, this thread will be stored in address lociation
            //in the threads array
            address = msg.getSender();
        }

//-----------------------------------------------------------------------------------------------------

        while(true) {
            //FloatingMsg msg = null;
            try {
                sleep(500);
                in = new ObjectInputStream(incommingSocket.getInputStream());
                msg = (FloatingMsg) in.readObject();
                MyLogic.appLog.add(msg.getMessage() + " from address: " + msg.getSender() + " to address: " + msg.getRecipient());
                System.out.println("msg to: " + msg.getRecipient() + " on received port " + incommingSocket.getRemoteSocketAddress()
                +" msg: "+msg.getMessage()+" special info: "+msg.getSpecialInfo());

                //message to close the thread
                if(msg.getSpecialInfo()==9){
                    Thread.currentThread().interrupt();
                    ServersController.getInstance().killThread(msg.getSender());
                    ServersController.getInstance().removeClient(msg.getSender());
                    System.out.println("closing the thread, I hope");
                }
                //checks msg.getRecipients availability
                else if(msg.getSpecialInfo()==8){
                    try {
                        //Thread.sleep(4000);
                        out = new ObjectOutputStream(incommingSocket.getOutputStream());
                        //System.out.println(ServersController.getInstance().getOpenThreads());
                        if(ServersController.getInstance().isUserOnline(msg.getRecipient())){
                            out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),
                                    ServersController.getInstance().getClient(msg.getRecipient()).getDescription()
                                    , 8, "Server","available"));
                        }
                        else{
                            out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),
            //TODO: create file for storing statuses of logged off users, replace offline with entry from that file
                                    "offline"
                                    , 8, "Server","notAvailable"));
                        }
                        //ServersController.getInstance().killThread(msg.getSender());
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                //sets a description for a client
                else if(msg.getSpecialInfo()==2){
                    ServersController.getInstance().getClient(msg.getSender()).setDescription(msg.getMessage());
                    System.out.println(msg.getMessage());
                }
                else {
                    //outGoingSocket = ServersController.getInstance().getSocket(msg.getRecipient());
                    outGoingSocket = ServersController.getInstance().getClient(msg.getRecipient()).getSocket();
                    MyLogic.appLog.add(outGoingSocket + " -port to recipient");

                    out = new ObjectOutputStream(outGoingSocket.getOutputStream());
                    out.writeObject(msg);
                    // out.writeObject(new FloatingMsg(msg.getSender(), msg.getRecipient(), msg.getMessage(), 0));
                }
//SocketException is thrown if the recipients socket reference the none exist thread
            } catch (SocketException e){
                try {
                    out = new ObjectOutputStream(incommingSocket.getOutputStream());
                    out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),"User is offline", 0, "Server",""));
                    System.out.println("SocketException From ForEveryClientThread");
                } catch (SocketException brokenPipe){
                    System.out.println("pipe broken");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
//NullPointerException is thrown if the recipients socket doesn't exist (recipient is not online)
            } catch (NullPointerException e){
                try {
                    out = new ObjectOutputStream(incommingSocket.getOutputStream());
                    out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),"User is offline", 0, "Server",""));
                    System.out.println("NullPointerException From ForEveryClientThread");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
//InterruptedException is thrown if thread is to be stopped message with special info 9 is received
            } catch (InterruptedException e) {
                e.printStackTrace();

                break;
            } catch (EOFException e){
                System.out.println("Client Disconnected");
                ServersController.getInstance().killThread(msg.getSender());
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
