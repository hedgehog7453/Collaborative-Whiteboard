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

    // RMI
    private ServerRemoteInterface server;
    private ClientRemoteInterface client;

    // GUI
    private WhiteboardWindow wbw;
    private WhiteboardListener wl;

    public void initialiseApp(boolean isManager) {
        // RMI
        try {
            // find server
            server = (ServerRemoteInterface) Naming.lookup("rmi://localhost:8081/server");

            // Client
            client = new ClientRemoteImpl();
            if (!isManager) {
                LocateRegistry.createRegistry(8082);
                Naming.rebind("rmi://localhost:8082/client", client);
            } else {
                Naming.rebind("rmi://localhost:8081/client", client);
            }
            client.setIsManager(isManager);
            wl = new WhiteboardListener(server, client);
            wbw = new WhiteboardWindow(wl, isManager);
            wl.setWindow(wbw);
            client.setWhiteboardListener(wl);
        } catch (RemoteException | NotBoundException | MalformedURLException e){
            //TODO
            e.printStackTrace();
            System.out.println("RMI connection failed.");
            System.exit(0);
        }

        boolean isConnected = wl.connectToServer();
        if (isConnected) {
            // GUI
            try {
                wbw.showWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
