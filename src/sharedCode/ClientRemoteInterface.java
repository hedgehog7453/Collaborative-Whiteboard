package sharedCode;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemoteInterface extends Remote {

    boolean getApproval(String username) throws RemoteException;

    void drawNewShape(Shape shape) throws RemoteException;
}
