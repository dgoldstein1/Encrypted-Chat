package client;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;
import server.ServerInformation;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client {

    private ObjectInputStream sInput;		// to read from the socket
    private ObjectOutputStream sOutput;		// to write on the socket
    private Socket socket;

    private ClientGUI cg;

    private String server, username;
    private int port;
    private ChatCipher cipher;
    private ClientInformation ci;
    private ServerInformation si;

    Client(String server, int port, String username, ClientGUI cg) {
        while(username.length() < 5) username += " ";
        this.server = server;
        this.port = port;
        this.username = username;
        this.cg = cg;
        cipher = new ChatCipher(EncryptionType.RSA);
        ci = new ClientInformation(username, cipher.getEncryption(), cipher.RSApublic(), cipher.DHpublic());        
    }

    /*
	 * To start the dialog
     */
    public boolean start() {
        // try to connect to the server
        try {
            display("Connecting to " + server.toString() + "..");
            socket = new Socket(server, port);
        } catch (Exception ec) {
            display("Error connecting to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        new ListenFromServer().start();  //at start      
        try {
            sOutput.writeObject(ci);

        } catch (IOException eIO) {
            display("Exception doing login : " + eIO);
            disconnect();
            return false;
        }
        return true;
    }

    /*
	 * To send a message to the console or the GUI
     */
    private void display(String msg) {
        cg.append(msg);
    }

    /**
     * sends message across server to specific person encrypted for each client
     * individually
     *
     * @param msg
     * @param clientId
     */
    void sendMessage(String msg, ClientInformation recieverci) {        
        String encryptedMsg = cipher.encryptCommunication(msg, recieverci);
        Communication c = new Communication(CommunicationType.MESSAGE, encryptedMsg, this.ci.encryption(), this.ci, recieverci);
        try {
            sOutput.writeObject(c);
        } catch (Exception e) {
            display("Exception writing to server: " + e);
            e.printStackTrace();
        }
    }

    /**
     * broadcasts to everyone on server
     *
     * @param msg
     */
    void broadcast(String msg) {
        for (ClientInformation ci : si.clients) {
            if(ci.id().equals(this.ci.id())){
                cg.append(username + ":  " + msg);
            }
            else sendMessage(msg, ci);
        }
    }

    void performAction(CommunicationType ct) {
        try {
            sOutput.writeObject(new Communication(ct, username + " " + ct.toString(), EncryptionType.None, ci, null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            sInput.close();
            sOutput.close();
            socket.close();
            cg.append("Disconnted from " + si.servername());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * syncs with server
     */
    class ListenFromServer extends Thread {

        public void run() {
            while (true) {
                try {
                    Object obj = sInput.readObject();
                    if (obj instanceof String) {
                        String msg = (String) obj;
                        cg.append(msg);
                    } else if (obj instanceof Communication) {
                        Communication cm = (Communication) obj;
                        if (!cm.encryption().equals(EncryptionType.None)) {
                            cg.append(cm.sender().name() + ":  " + cipher.decryptCommunication(cm));
                        } else if(cm.getType().equals(CommunicationType.REMOVE_CLIENT)){//another client disconnected
                            for(ClientInformation ci : si.clients){
                                if(ci.equals(cm.sender())){
                                    si.clients.remove(ci);
                                    break;
                                }                                
                            }
                        }
                        
                        else {//plain message
                            cg.append(cm.getMessage());
                        }

                    } else if (obj instanceof ServerInformation) {//sent to init
                        si = (ServerInformation) obj;

                    } else if (obj instanceof ClientInformation){
                        if (si!=null){
                            si.clients.add((ClientInformation) obj);                            
                        }                        
                    }

                } catch (IOException e) {
                    display("closed connection");
                    break;
                } catch (ClassNotFoundException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    public EncryptionType getEncryption(){
        return ci.encryption();        
    }
    
    public void setEncryption(EncryptionType et){
        ci.setEncryption(et);
        cipher.setEncryption(et);
        broadcast("-- changed encryption to: " + et.toString() + " --");
    }

}
