package sharedCode;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClientRemoteInterface extends Remote {

    boolean getApproval(String username) throws RemoteException;
    void displayAllMessages() throws RemoteException;
    //void drawAllShapes() throws RemoteException;

    void forceQuit(String message) throws RemoteException;
    void displayUserList(String managerName, ArrayList<String> users) throws RemoteException;

    void drawNewShape(Shape shape) throws RemoteException;
    void updateCanvas() throws RemoteException;
    void updateUserDrawStatus(ArrayList<String> username) throws RemoteException;

    void displayMsg(String msg) throws RemoteException;

    void setWhiteboardListener(WhiteboardListener wbl) throws RemoteException;
    void setIsManager(boolean isManager) throws RemoteException;
    void setUsername(String username) throws RemoteException;
    boolean getIsManager() throws RemoteException;
    String getUsername() throws RemoteException;

}
