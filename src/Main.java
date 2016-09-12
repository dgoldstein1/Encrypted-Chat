
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.NoSuchPaddingException;
import server.ServerGUI;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/*
 * Direct/Group chat application using standard encrpytion
 * Created by Dave on 7.22.2016
 */
/**
 *
 * @author dave
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, Exception {

        try {
            for (LookAndFeelInfo info
                    : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        int port = 1500;
        String sName = "localhost";
        int nClients = 2;

        ServerGUI sg = new server.ServerGUI(port, nClients, sName);
        sg.startServer();

        ArrayList<client.ClientGUI> clients = new ArrayList<>(nClients);
        for (int i = 0; i < nClients; i++) {
            clients.add(new client.ClientGUI(sName, port, i + "test"));
            clients.get(i).login();
        }

    }

}
