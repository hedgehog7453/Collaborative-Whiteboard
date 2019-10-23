package sharedCode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

public interface ClientRemoteInterface extends Remote {

    boolean getApproval(String username) throws RemoteException;

    void drawNewShape(Shape shape) throws RemoteException;

    void displayMsg(String msg) throws RemoteException;

    void displayUserList(String managerName, Set<String> users) throws RemoteException;

    void setUsername(String username) throws RemoteException;

    String getUsername() throws RemoteException;
}
