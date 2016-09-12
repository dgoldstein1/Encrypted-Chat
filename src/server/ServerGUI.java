package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import server.Server;

/*
 * The server as a GUI
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = 1L;
    private JButton stopStart;
    private JTextArea chat, event;
    private JTextField tPortNumber, serverName, maxUsersField;
    private Server server;

    
    public ServerGUI(){
        this(1500,15,"New Server");
    }
    
    // server constructor that receive the port to listen to for connection as parameter
    public ServerGUI(int port, int maxUsers, String name) {
        super(name);
        server = null;
        JPanel north = new JPanel(new GridLayout(4,2));
        
        north.add(new JLabel("Port number: "));
        tPortNumber = new JTextField(port + "");
        north.add(tPortNumber);
        
        maxUsersField = new JTextField(maxUsers + "");
        north.add(new JLabel("Max Users: "));
        north.add(maxUsersField);
        
        serverName = new JTextField(name);
        north.add(new JLabel("Server Name: "));
        north.add(serverName);

        // to stop or start the server, we start with "Start"
        stopStart = new JButton("Start");
        stopStart.addActionListener(this);
        north.add(stopStart);
        add(north, BorderLayout.NORTH);

        // the event and chat room
        JPanel center = new JPanel(new GridLayout(2, 1));
        chat = new JTextArea(80, 50);
        chat.setEditable(false);
        chat.setWrapStyleWord(true);
        appendRoom("Server Traffic.\n");
        center.add(new JScrollPane(chat));
        event = new JTextArea(80, 50);
        event.setEditable(false);
        event.setWrapStyleWord(true);
        appendEvent("Events log.\n");
        center.add(new JScrollPane(event));
        add(center);

        // need to be informed when the user click the close button on the frame
        addWindowListener(this);
        setSize(400, 600);
        setVisible(true);

    }

    // append message to the two JTextArea
    // position at the end
    void appendRoom(String str) {
        chat.append(str + "\n");
        chat.setCaretPosition(0);
        chat.setCaretPosition(chat.getText().length() - 1);
    }

    void appendEvent(String str) {
        event.append(str + "\n");
        chat.setCaretPosition(chat.getText().length() - 1);;

    }

    public void startServer() {
        int port;
        try {
            port = Integer.parseInt(tPortNumber.getText().trim());
        } catch (Exception er) {
            appendEvent("Invalid port number");
            return;
        }
        // ceate a new Server
        int maxUsers;
        int portN;
        try {
            maxUsers = Integer.parseInt(maxUsersField.getText());
            portN = Integer.parseInt(tPortNumber.getText());
        } catch (NumberFormatException e) {
            maxUsers = 15;
            portN = 1500;
            System.out.println("Could not parse maxUsers or port Number");
        }
        server = new Server(
            portN,
            maxUsers,
            serverName.getText(),
            this);
        new ServerRunning().start();
        stopStart.setText("Stop");
        tPortNumber.setEditable(false);
        serverName.setEditable(false);
        maxUsersField.setEditable(false);

    }

    // start or stop where clicked
    public void actionPerformed(ActionEvent e) {
        // if running we have to stop
        if (server != null) {
            server.stop();
            server = null;
            tPortNumber.setEditable(true);
            serverName.setEditable(true);
            maxUsersField.setEditable(true);
            stopStart.setText("Start");
            return;
        }
        startServer();

    }


    /*
     * If the user click the X button to close the application
     * I need to close the connection with the server to free the port
     */
    public void windowClosing(WindowEvent e) {
        // if my Server exist
        if (server != null) {
            try {
                server.stop();			// ask the server to close the conection
            } catch (Exception eClose) {
            }
            server = null;
        }
        // dispose the frame
        dispose();
        System.exit(0);
    }

    // I can ignore the other WindowListener method
    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    /*
     * A thread to run the Server
     */
    class ServerRunning extends Thread {

        public void run() {
            server.start();         // should execute until if fails
            // the server failed
            stopStart.setText("Start");
            tPortNumber.setEditable(true);
            appendEvent("Server crashed\n");
            server = null;
        }
    }

}
