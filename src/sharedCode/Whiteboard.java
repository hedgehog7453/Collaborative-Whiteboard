package sharedCode;

import gui.WhiteboardWindow;
import server.ServerRemoteInterface;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

public class Whiteboard {

    // RMI
    private ServerRemoteInterface server;
    private ClientRemoteInterface client;

    // GUI
    private WhiteboardWindow wbw;
    private WhiteboardListener wl;

    public void initialiseApp(boolean isManager, String ip, String port) {

        try {
            // find server
            server = (ServerRemoteInterface) Naming.lookup("rmi://" + ip + ":" + port + "/server");

            // Client
            client = new ClientRemoteImpl();
            if (isManager) {
                Naming.rebind("rmi://localhost:" + port + "/client", client);
            } else {
                // Allows only one user on manager's machine to join
                LocateRegistry.createRegistry(Integer.parseInt(port));
                Naming.rebind("rmi://localhost:" + port + "/client", client);
            }

            client.setIsManager(isManager);
            wl = new WhiteboardListener(server, client);
            wbw = new WhiteboardWindow(wl);
            wl.setWindow(wbw);
            client.setWhiteboardListener(wl);
        } catch (RemoteException e){
            JOptionPane.showMessageDialog(null,"No whiteboard room found (RemoteException).");
            System.exit(0);
        } catch (NotBoundException e) {
            JOptionPane.showMessageDialog(null,"No whiteboard room found (NotBoundException).");
            System.exit(0);
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(null,"No whiteboard room found (MalformedURLException).");
            System.exit(0);
        }

        // Connect to server
        boolean isConnected = wl.connectToServer(true);
        if (isConnected) {
            try {
                // show gui window
                wbw.showWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
