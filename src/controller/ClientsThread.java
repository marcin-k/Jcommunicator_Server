package controller;

import model.FloatingMsg;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by marcin on 18/06/2016.
 *
 * Instance of this class is crated for each client connection
 * Class routes the messages, updates users status descriptions,
 * controls online/offline status
 */
public class ClientsThread extends Thread {

    private Socket outGoingSocket;
    protected Socket incomingSocket;
    ObjectInputStream in = null;
    ObjectOutputStream out = null;
    FloatingMsg msg = null;
    int address = 0;

    //Constructor
    public ClientsThread(Socket clientSocket) {
        this.incomingSocket = clientSocket;
    }

//-------------------------------Run Method-------------------------------------------------------------------------
    public void run() {
        //----------------------------Trying to catch Hello packet from client--------------------------
        try {
            in = new ObjectInputStream(incomingSocket.getInputStream());
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
            Logic.appLog.add("New Connection, remote port: "+ incomingSocket.getRemoteSocketAddress());
            ServersController.getInstance().addClient(msg.getSender(), incomingSocket);
            //initiate the address of the sender, this thread will be stored at address position
            //in the threads array
            address = msg.getSender();
        }
        //------------------------------------------------------------------------------------------------
        while(true) {
            //FloatingMsg msg = null;
            try {
                sleep(500);
                in = new ObjectInputStream(incomingSocket.getInputStream());
                msg = (FloatingMsg) in.readObject();
                Logic.appLog.add(msg.getMessage() + " from address: " + msg.getSender() + " to address: " + msg.getRecipient());
                System.out.println("msg to: " + msg.getRecipient() + " on received port " + incomingSocket.getRemoteSocketAddress()
                +" msg: "+msg.getMessage()+" special info: "+msg.getSpecialInfo());

                //message to close the thread
                if(msg.getSpecialInfo()==9){
                    Thread.currentThread().interrupt();
                    ServersController.getInstance().killThread(msg.getSender());
                    ServersController.getInstance().removeClient(msg.getSender());
                }
                //checks msg.getRecipients availability
                else if(msg.getSpecialInfo()==8){
                    try {
                        //Thread.sleep(4000);
                        out = new ObjectOutputStream(incomingSocket.getOutputStream());
                        if(ServersController.getInstance().isUserOnline(msg.getRecipient())){
                            out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),
                                    ServersController.getInstance().getClient(msg.getRecipient()).getDescription()
                                    , 8, "Server","available"));
                        }
                        else{
                            //TODO: create file for storing statuses of logged off users, replace offline with entry from that file
                            out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),
                                    "offline", 8, "Server","notAvailable"));
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                //sets a description for a client
                else if(msg.getSpecialInfo()==2){
                    ServersController.getInstance().getClient(msg.getSender()).setDescription(msg.getMessage());
                    System.out.println(msg.getMessage());
                }
                //conversation message
                else {
                    outGoingSocket = ServersController.getInstance().getClient(msg.getRecipient()).getSocket();
                    Logic.appLog.add(outGoingSocket + " -port to recipient");
                    out = new ObjectOutputStream(outGoingSocket.getOutputStream());
                    out.writeObject(msg);
                }
            }
            //SocketException is thrown if the recipients socket reference the none exist thread
            catch (SocketException e){
                sentUserOffline(msg, incomingSocket);
            }
            //NullPointerException is thrown if the recipients socket doesn't exist (recipient is not online)
            catch (NullPointerException e){
                sentUserOffline(msg, incomingSocket);
            }
            //InterruptedException is thrown if thread is to be stopped message with special info 9 is received
            catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            //InterruptedException is thrown if communication pipe is broken between client and server
            catch (EOFException e){
                Logic.appLog.add("Client Disconnected");
                ServersController.getInstance().killThread(msg.getSender());
                break;
            }
            //Input/output exception
            catch (IOException e) {
                e.printStackTrace();
                break;
            }
            //Missing class
            catch (ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }
//------------------------------------------------------------------------------------------------------------------
    //Sends the message back to sender informing that recipient is offline
    private void sentUserOffline(FloatingMsg msg, Socket incomingSocket){
        try {
            out = new ObjectOutputStream(incomingSocket.getOutputStream());
            out.writeObject(new FloatingMsg(msg.getRecipient(),msg.getSender(),"User is offline", 0, "Server",""));
        } catch (SocketException brokenPipe){
            System.out.println("pipe broken");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method used to place a thread on the correct position in the array
    public int getAddress(){
        return address;
    }

}
