package sharedCode;

import gui.WhiteboardWindow;
import server.ServerRemoteInterface;

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
    private WhiteboardWindow wbw;

    private DrawListener dl;

    public void initialiseApp(boolean isManager) {
        this.isManager = isManager;

        // RMI
        try {
            // find server
            server = (ServerRemoteInterface) Naming.lookup("rmi://10.12.54.34:8081/server");

            // Client
            client = new ClientRemoteImpl(this);
            if (!isManager) {
                LocateRegistry.createRegistry(8082);
                Naming.rebind("rmi://localhost:8082/client", client);
            } else {
                Naming.rebind("rmi://localhost:8081/client", client);
            }
        } catch (RemoteException | NotBoundException | MalformedURLException e){
            e.printStackTrace();
            System.out.println("RMI connection failed.");
            System.exit(0);
        }

        isConnected = connectToServer();
        if (isConnected) {
            // GUI
            try {
                dl = new DrawListener(server);
                wbw = new WhiteboardWindow(dl, isManager);
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
                if (username == null) {
                    System.out.println("Bye");
                    System.exit(0);
                }
                if (username.length() < 4) { // TODO: more validation (no space, etc.)
                    JOptionPane.showConfirmDialog(null, "Please enter a valid username. Your username needs to be longer than 4 letters.", "", JOptionPane.DEFAULT_OPTION);
                    continue;
                }
                isUnique = server.isUsernameUnique(username);
                if (!isUnique) {
                    JOptionPane.showConfirmDialog(null, "Username already exists. ", "", JOptionPane.DEFAULT_OPTION);
                }
            }
            boolean status = server.clientConnect(isManager, username, client);
            if (status) {
                JOptionPane.showConfirmDialog(null, "You are now in the room!", "Congratulations", JOptionPane.DEFAULT_OPTION);
            } else {
                JOptionPane.showConfirmDialog(null, "You are rejected by the manager.", "Oh no :(", JOptionPane.DEFAULT_OPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public DrawListener getDrawListener() {
        return dl;
    }


}