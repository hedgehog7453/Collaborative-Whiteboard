package User;

import SharedCode.Whiteboard;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserWhiteboard {

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        // Initialise app
        Whiteboard wb = new Whiteboard();
        wb.initialiseApp(false);
    }
}
