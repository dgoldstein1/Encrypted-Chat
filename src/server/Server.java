package server;

import client.Communication;
import client.Client;
import client.ClientInformation;
import client.CommunicationType;
import client.EncryptionType;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Server class for group / direct chat using java sockets
 */
public class Server {

    private ArrayList<ClientThread> al;
    private boolean keepGoing;
    private ServerGUI sg;
    private ServerInformation si;

    public Server(int port, int maxUsers, String name, ServerGUI sg) {
        this.sg = sg;

        al = new ArrayList<ClientThread>(maxUsers);
        String ip;
        try {
            ip = InetAddress.getLocalHost() + "";
        } catch (UnknownHostException e) {
            e.printStackTrace();
            ip = "-1";
        }

        si = new ServerInformation(name, maxUsers, port, ip, "216.15.57.237");

    }

    /**
     * central loop for listening to clients
     */
    public void start() {
        keepGoing = true;
        try {
            ServerSocket serverSocket = new ServerSocket(si.port());
            while (keepGoing) {
                display(si.servername() + " waiting for Clients on port " + si.port() + ".");

                if (!keepGoing) {
                    break;
                }
                Socket socket = serverSocket.accept();
                ClientThread t = new ClientThread(socket);
                al.add(t);
                t.start();
                t.writeServerInfo(si);
                si.clients.add(t.info);                

                for (ClientThread ct : al) {
                    ct.writeClientInfo(t.info);
                }
                broadcast(t.info.name() + " connected.");

                if (al.size() > si.maxUsers()) {
                    t.writeMsg("Could not join. Chat is full");
                    this.remove(t);
                    broadcast("user " + t.username + " denied connect: chat full.");
                    t.close();
                }

            }
            try {
                serverSocket.close();
                for (int i = 0; i < al.size(); ++i) {
                    ClientThread tc = al.get(i);
                    try {
                        tc.sInput.close();
                        tc.sOutput.close();
                        tc.socket.close();
                    } catch (IOException ioE) {
                        ioE.printStackTrace();
                    }
                }
            } catch (Exception e) {
                display("Exception closing the server and clients: " + e);
            }
        } // something went bad
        catch (IOException e) {
            String msg = si.sdf().format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
            display(msg);
        }
    }

    /*
     * For the GUI to stop the server
     */
    public void stop() {
        keepGoing = false;
        try {
            new Socket("localhost", si.port());
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            display(sw.toString());
        }
    }

    /*
     * Display an event (not a message) to the console or the GUI
     */
    public void display(String msg) {
        sg.appendEvent(si.sdf().format(new Date()) + " " + msg);
    }

    /*
     *  to broadcast a message to all Clients
     */
    private synchronized void broadcast(String message) {
        String time = si.sdf().format(new Date());
        String messageLf = time + " " + message;
        sg.appendRoom(messageLf);

        for (int i = al.size(); --i >= 0;) {
            ClientThread ct = al.get(i);
            if (!ct.writeMsg(messageLf)) {
                al.remove(i);
            }
        }
    }

    /**
     * removes client from list of those served
     *
     * @param ci in clientThread
     */
    synchronized void remove(ClientThread toRemove) {
        al.stream().forEach((ct) -> {
            ct.removeClient(toRemove.info);
        });
        si.clients.remove(toRemove.info);
        al.remove(toRemove);
        System.out.println("server " + al.size());
        
    }

    /**
     * One instance of this thread will run for each client
     */
    class ClientThread extends Thread {

        ClientInformation info;
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        Communication cm;
        String date;

        ClientThread(Socket socket) {
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                info = (ClientInformation) sInput.readObject();
                this.info = info;
                username = info.name();
                sg.appendEvent(username + " connected");
            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            date = new Date().toString() + "\n";
        }

        public void run() {
            boolean keepGoing = true;
            while (keepGoing) {
                try {
                    Object obj = sInput.readObject();
                    if (obj instanceof Communication) {
                        cm = (Communication) obj;
                        String message = cm.getMessage();
                        CommunicationType mt = cm.getType();
                        if (mt == CommunicationType.LOGOUT) {
                            broadcast(username + " disconnected");
                            keepGoing = false;
                        } else if (mt == CommunicationType.WHOISIN) {
                            writeMsg("List of the users connected at " + si.sdf().format(new Date()) + "\n");
                            // scan al the users connected
                            for (int i = 0; i < al.size(); ++i) {
                                ClientThread ct = al.get(i);
                                writeMsg((i + 1) + ") " + ct.username + " since " + ct.date);
                            }
                        } else {//communication
                            ClientThread sender = null;
                            ClientThread reciever = null;
                            for (ClientThread ct : al) {
                                if (ct.info.id().equals(cm.recipient().id())) {
                                    reciever = ct;
                                }
                                if (ct.info.id().equals(cm.sender().id())) {
                                    sender = ct;
                                }
                            }
                            if (sender == null || reciever == null) {
                                display("no such sender/reciever id in: " + cm.sender().name() + "/>" + cm.getMessage() + " to " + cm.recipient().name() + "encryption: " + cm.encryption());
                                break;
                            } else {
                                reciever.sOutput.writeObject(cm);
                                String s = sender.info.name() + " to " + reciever.info.name() + ": '" + cm.getMessage() + "' encryption: " + cm.encryption();
                                sg.appendRoom(s);

                            }

                        }
                    } else if (obj instanceof ClientInformation) {
                        ClientInformation ci = (ClientInformation) obj;
                        this.info = (ClientInformation) obj;

                    }

                } catch (IOException e) {
                    display(username + " Exception reading Streams: " + e);
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }

            }
            remove(this);
            close();
        }

        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (sInput != null) {
                    sInput.close();
                }
            } catch (Exception e) {
            };
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
            }
        }

        /**
         * sends message to client
         *
         * @param msg
         * @return
         */
        private boolean writeMsg(String msg) {
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                sOutput.writeObject(msg);
            } catch (IOException e) {
                display("Error sending message to " + username);
                display(e.toString());
            }
            return true;
        }

        private boolean writeClientInfo(ClientInformation ci) {

            try {
                sOutput.writeObject(ci);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }

        private boolean removeClient(ClientInformation ci) {
            try {
                sOutput.writeObject(new Communication(CommunicationType.REMOVE_CLIENT, "client " + ci.name() + " disconnected",
                        EncryptionType.None, ci, ci));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        private boolean writeServerInfo(ServerInformation si) {
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                sOutput.writeObject(si);
            } catch (IOException e) {
                display("Error updating server info with" + username);
                display(e.toString());
            }
            return true;
        }

    }

}
