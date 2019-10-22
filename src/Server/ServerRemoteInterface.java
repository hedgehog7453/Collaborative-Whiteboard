package Server;

import SharedCode.ClientRemoteImpl;
import SharedCode.ClientRemoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemoteInterface extends Remote {
    boolean clientConnect(boolean isManager, String username, ClientRemoteInterface client) throws RemoteException;
}
