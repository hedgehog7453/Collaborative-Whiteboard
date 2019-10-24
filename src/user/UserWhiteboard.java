package user;

import sharedCode.Whiteboard;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserWhiteboard {

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        // Initialise app
        String ip = args[1];
        String port = args[2];
        Whiteboard wb = new Whiteboard();
        wb.initialiseApp(false,ip,port);
    }
}
