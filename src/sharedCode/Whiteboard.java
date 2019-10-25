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

    public void initialiseApp(boolean isManager, String ip, String port, String user_port) {

        try {
            // find server
            server = (ServerRemoteInterface) Naming.lookup("rmi://"+ip+":"+port+"/server");

            // Client
            client = new ClientRemoteImpl();
            if (isManager) {
                Naming.rebind("rmi://"+ip+":"+port+"/client", client);
            } else {
                // Allows only one user on manager's machine to join
                LocateRegistry.createRegistry(Integer.parseInt(user_port));
                Naming.rebind("rmi://"+ip+":"+user_port+"/client", client);
            }

            client.setIsManager(isManager);
            wl = new WhiteboardListener(server, client);
            wbw = new WhiteboardWindow(wl);
            wl.setWindow(wbw);
            client.setWhiteboardListener(wl);
        } catch (RemoteException | NotBoundException | MalformedURLException e){
            JOptionPane.showMessageDialog(null,"The whiteboard room has not been created");
            //  System.out.println("RMI connection failed.");
            System.exit(0);
        }

        // Connect to server
        boolean isConnected = wl.connectToServer(true);
        if (isConnected) {
            try {
                // show gui window
                wbw.showWindow();
//                // update canvas
//                Thread queryThread = new Thread() {
//                    public void run() {
//                        wl.updateCanvasFromServer();
//                    }
//                };
//                queryThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
