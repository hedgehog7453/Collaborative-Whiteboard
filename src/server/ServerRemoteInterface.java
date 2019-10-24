package server;

import sharedCode.ClientRemoteInterface;
import sharedCode.Shape;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerRemoteInterface extends Remote {

    boolean isUsernameUnique(String username) throws RemoteException;
    boolean getIsConnected(String username) throws RemoteException;
    boolean clientConnect(boolean isManager, String username, ClientRemoteInterface client) throws RemoteException;
    boolean clientDisconnect(String username) throws RemoteException;
    boolean kickUser(String username) throws RemoteException;
    boolean removeUser(String username) throws RemoteException;
    boolean removeAllUsers() throws RemoteException;
    void updateAllUserlists() throws RemoteException;

    ArrayList<Shape> getWhiteBoard() throws RemoteException;
    void addShape(Shape shape) throws RemoteException;
    void clearAllShapes() throws RemoteException;
    void updateShapes(ArrayList<Shape> shapes) throws RemoteException;
    void updateClientCanvas() throws RemoteException;
    void addEditingUser(String username) throws RemoteException;
    void removeEditingUser(String username) throws RemoteException;

    void sendMessage(String msg, String username) throws RemoteException;
    void broadcastMessage(String msg) throws RemoteException;

    ArrayList<String> getAllMessages() throws RemoteException;
    ArrayList<Shape> getAllShapes() throws RemoteException;

//    String getManagerName() throws RemoteException;
//    Set<String> getUserList() throws RemoteException;


}
