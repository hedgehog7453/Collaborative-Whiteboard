package sharedCode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

public interface ClientRemoteInterface extends Remote {

    boolean getApproval(String username) throws RemoteException;

    void drawNewShape(Shape shape) throws RemoteException;

    void displayMsg(String msg) throws RemoteException;

    void displayUserList(String managerName, ArrayList<String> users) throws RemoteException;

    void forceQuit(String message) throws RemoteException;

    void setWhiteboardListener(WhiteboardListener wbl) throws RemoteException;
    void setIsManager(boolean isManager) throws RemoteException;
    void setIsConnected(boolean isConnected) throws RemoteException;
    void setUsername(String username) throws RemoteException;

    boolean getIsManager() throws RemoteException;
    boolean getIsConnected() throws RemoteException;
    String getUsername() throws RemoteException;
}
