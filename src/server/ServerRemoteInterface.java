package server;

import sharedCode.ClientRemoteInterface;
import sharedCode.Shape;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServerRemoteInterface extends Remote {
    boolean isUsernameUnique(String username) throws RemoteException;
    boolean clientConnect(boolean isManager, String username, ClientRemoteInterface client) throws RemoteException;

    ArrayList<Shape> getWhiteBoard() throws RemoteException;
    void addShape(Shape shape) throws RemoteException;
    void sendMessage(String msg, String username) throws RemoteException;
    void getUserList() throws RemoteException;
    //void editWhiteBoard(ArrayList<sharedCode.Shape> shape) throws RemoteException;
}
