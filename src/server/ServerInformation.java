package server;

import client.Client;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import server.Server.ClientThread;

/**
 * /*
 * tells client about the server refreshes on timer
 *
 * @author dave
 */
public class ServerInformation implements Serializable {

    private String serverName, localIP, externalIP;
    public ArrayList<client.ClientInformation> clients; //todo: make private?
    private int maxUsers,port; //used for DH exchange 
    private SimpleDateFormat sdf;


    public ServerInformation(String serverName, int maxUsers, int port, String localIP, String externalIP) {
        this.serverName = serverName;
        this.localIP = localIP;
        this.externalIP = externalIP;
        this.maxUsers = maxUsers;
        this.port = port;
        
        this.sdf = new SimpleDateFormat("HH:mm:ss");
        clients = new ArrayList<>(maxUsers);
    }


    /**
     * getters and setters *
     */
    public String servername() {
        return serverName;
    }

    public String localIP() {
        return localIP;
    }

    public String externalIP() {
        return externalIP;
    }

    public int maxUsers() {
        return maxUsers;
    }
    
    public int port(){
        return port;
    }
    
    public SimpleDateFormat sdf(){
        return sdf;
    }
    

}
