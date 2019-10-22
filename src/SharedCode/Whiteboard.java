package SharedCode;

import Server.ServerRemoteInterface;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

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
            // Server
            server = (ServerRemoteInterface) Naming.lookup("rmi://localhost:8081/server");

            // Client
            client = new ClientRemoteImpl();
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
            JFrame frame = new JFrame("Connection");
            String username = JOptionPane.showInputDialog(frame, "Please enter your username: ",
                    opTitle, JOptionPane.QUESTION_MESSAGE);
            boolean status = server.clientConnect(isManager, username, client);
            //wbw = new WhiteboardWindow(isManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }


}
