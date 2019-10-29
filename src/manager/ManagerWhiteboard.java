package manager;

import server.ServerRemoteImpl;
import sharedCode.Whiteboard;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ManagerWhiteboard {

    public static void main(String[] args) {

        String ip = args[0]; // IP Address
        String port = args[1]; // port number

        // setup RMI server
        System.setProperty("java.rmi.server.hostname", ip);
        ServerRemoteImpl server;
        try {
            server = new ServerRemoteImpl();
            LocateRegistry.createRegistry(Integer.parseInt(port));
            Naming.rebind("rmi://localhost:" + port + "/server", server);
            //System.out.println("Object bound to name");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Initialise app
        Whiteboard wb = new Whiteboard();
        wb.initialiseApp(true, ip, port);
    }

}
