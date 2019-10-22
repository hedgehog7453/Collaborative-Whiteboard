package SharedCode;

import Server.ServerRemoteInterface;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Whiteboard {

    private boolean isManager;
    private boolean isConnected;

    // RMI
    private ServerRemoteInterface server;
    private ClientRemoteInterface client;

    // GUI
    //private WhiteboardWindow wbw;

    public void initialiseApp(boolean isManager) {
        this.isManager = isManager;

        // RMI
        try {
            // find server
            server = (ServerRemoteInterface) Naming.lookup("rmi://10.12.54.34:8081/server");

            // Client
            client = new ClientRemoteImpl();
            if (!isManager) {
                LocateRegistry.createRegistry(8081);
            }
            Naming.rebind("rmi://localhost:8081/client", client);
        } catch (RemoteException | NotBoundException | MalformedURLException e){
            e.printStackTrace();
            System.out.println("RMI connection failed.");
            System.exit(0);
        }

        isConnected = connectToServer();


        if (isConnected) {
            // GUI
            try {
                //wbw = new WhiteboardWindow(isManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private boolean connectToServer() {
        try {
            String opTitle = "";
            if (isManager) {
                opTitle = "Creating a room ... ";
            } else {
                opTitle = "Joining a room ...";
            }
            boolean isUnique = false;
            String username = "";
            while (!isUnique) {
                JFrame frame = new JFrame("Connection");
                username = JOptionPane.showInputDialog(frame, "Please enter your username: ",
                        opTitle, JOptionPane.QUESTION_MESSAGE);
                isUnique = server.isUsernameUnique(username);
            }
            boolean status = server.clientConnect(isManager, username, client);
            if (status) {
                JOptionPane.showConfirmDialog(null, "You are now in the room!", "Congratulations", JOptionPane.DEFAULT_OPTION);
            } else {
                JOptionPane.showConfirmDialog(null, "You are rejected by the manager.", "Oh no :(", JOptionPane.DEFAULT_OPTION);
            }
            //wbw = new WhiteboardWindow(isManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


}
