package user;

import sharedCode.Whiteboard;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserWhiteboard {

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        // Initialise app
        String ip = args[0];
        String port = args[1];
        String port2 = args[2];

        System.out.println(ip + " " + port);

        Whiteboard wb = new Whiteboard();
        wb.initialiseApp(false,ip,port, port2);
    }
}
