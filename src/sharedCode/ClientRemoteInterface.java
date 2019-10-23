package sharedCode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClientRemoteInterface extends Remote {

    boolean getApproval(String username) throws RemoteException;

    void drawNewShape(Shape shape) throws RemoteException;

    void displayMsg(String msg) throws RemoteException;

    void displayUserList(ArrayList<String> users) throws RemoteException;

    String getUsername() throws RemoteException;
}
