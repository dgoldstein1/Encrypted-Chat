package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.StyledDocument;
import server.ConnectToServerGUI;

/**
 * gui for client.java display decryption in private / group chat
 *
 * @author dave on 8/1/2016
 */
public class ClientGUI extends javax.swing.JFrame {

    /**
     * Creates new form ClientGUI
     */
    private String host, username;
    private int port;
    private Client client;
    private boolean connected;

    public ClientGUI(String host, int port, String userName) {
        this();
        this.host = host;
        this.port = port;
        this.username = username;
    }

    public ClientGUI() {
        super("Encrypted Chat");
        username = (String) JOptionPane.showInputDialog(
                this,
                "Enter Username:",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "anonymous");

        if (username == null || username.equals("")) {
            username = "anonymous";
        }
        initComponents();
        this.setVisible(true);
        host = null;
        port = -1;
    }

    /**
     * login to server using default values
     */
    public void login() {
        login(host, port);
    }

    /**
     *
     * @param host
     * @param str
     */
    public void login(String host, int port) {
        if (client != null) {
            client.performAction(CommunicationType.LOGOUT);
        }

        client = new Client(host, port, username, this);
        if (!client.start()) {
            System.out.println("could not start client");
            client.performAction(CommunicationType.LOGOUT);
            client = null;
            return;
        }
        connected = true;

    }

    /**
     * write text to textPane
     *
     * @param str
     */
    void append(String str) {
        chatArea.append(str + "\n");
        chatArea.setCaretPosition(chatArea.getText().length() - 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chatPanel = new javax.swing.JPanel();
        messageTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        connectMenu = new javax.swing.JMenu();
        disconnectMenu = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        encryptionMenu = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(300, 250));

        chatPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        chatPanel.setLayout(new java.awt.BorderLayout());

        messageTextField.setText("Send Message..");
        messageTextField.setToolTipText("Send Message...");
        messageTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                messageTextFieldMouseClicked(evt);
            }
        });
        messageTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messageTextFieldActionPerformed(evt);
            }
        });
        chatPanel.add(messageTextField, java.awt.BorderLayout.CENTER);

        getContentPane().add(chatPanel, java.awt.BorderLayout.PAGE_END);

        chatArea.setEditable(false);
        chatArea.setColumns(20);
        chatArea.setRows(5);
        jScrollPane1.setViewportView(chatArea);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("New Chat");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Save Chat");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem1);

        jMenuItem3.setText("Exit");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        fileMenu.add(jMenuItem3);

        jMenuBar1.add(fileMenu);

        connectMenu.setText("Connection");

        disconnectMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        disconnectMenu.setText("Disconnect");
        disconnectMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectMenuActionPerformed(evt);
            }
        });
        connectMenu.add(disconnectMenu);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem6.setText("Create Server");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        connectMenu.add(jMenuItem6);

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem4.setText("Connect to Server");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        connectMenu.add(jMenuItem4);

        jMenuBar1.add(connectMenu);

        encryptionMenu.setText("Encryption");

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setText("Set Encryption");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        encryptionMenu.add(jMenuItem8);

        jMenuItem7.setText("Details");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        encryptionMenu.add(jMenuItem7);

        jMenuBar1.add(encryptionMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * messageTextField pressed
     *
     * @param evt
     */
    private void messageTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageTextFieldActionPerformed
        if (client == null) {
            append("Not Connected to Server");
            return;
        }
        client.broadcast(messageTextField.getText());
        messageTextField.setText("");
    }//GEN-LAST:event_messageTextFieldActionPerformed

    /**
     * save pushed
     *
     * @param evt
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        String filename = username + " chat log " + System.currentTimeMillis();
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setSelectedFile(new File(filename));

        int retrival = chooser.showSaveDialog(null);
        if (retrival == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".txt")) {
                fw.write(chatArea.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        if (host != null && port != -1) {
            new ConnectToServerGUI(this, host, port, null);
        } else {
            new ConnectToServerGUI(this);
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    /**
     * details pressed
     * @param evt 
     */
    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        new EncryptionDetailsFrame();
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    /**
     * exit pushed
     *
     * @param evt
     */
    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    /**
     * new pushed
     *
     * @param evt
     */
    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        new ClientGUI(host, port, username);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
     * message textField clicked
     *
     * @param evt
     */
    private void messageTextFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_messageTextFieldMouseClicked
        messageTextField.setText("");
    }//GEN-LAST:event_messageTextFieldMouseClicked
    /**
     * new server pressed
     *
     * @param evt
     */
    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        new server.ServerGUI();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void disconnectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectMenuActionPerformed
        client.performAction(CommunicationType.LOGOUT);
    }//GEN-LAST:event_disconnectMenuActionPerformed

    /**
     * set encryption menu option pushed
     *
     * @param evt
     */
    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        EncryptionType[] options = EncryptionType.values();
        EncryptionType et = (EncryptionType) JOptionPane.showInputDialog(
                this,
                "Choose Encryption",
                "Customized Dialog",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                client.getEncryption().toString());
        
        client.setEncryption(et);

    }//GEN-LAST:event_jMenuItem8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chatArea;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JMenu connectMenu;
    private javax.swing.JMenuItem disconnectMenu;
    private javax.swing.JMenu encryptionMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField messageTextField;
    // End of variables declaration//GEN-END:variables
}