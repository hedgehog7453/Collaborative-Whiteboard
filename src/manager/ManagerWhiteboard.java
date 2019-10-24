package manager;

import server.ServerRemoteImpl;
import sharedCode.Whiteboard;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ManagerWhiteboard {

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        // RMI
        String port = args[1];
        String ip = "localhost";
        ServerRemoteImpl server;
        try {
            server = new ServerRemoteImpl();
            LocateRegistry.createRegistry(Integer.parseInt(port));
            Naming.rebind("rmi://localhost:"+port+"/server", server);
            //System.out.println("Object bound to name");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // Initialise app
        Whiteboard wb = new Whiteboard();
        wb.initialiseApp(true,ip,port);
    }

}
