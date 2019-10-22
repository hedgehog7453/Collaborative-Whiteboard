package SharedCode;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemoteInterface extends Remote {

    boolean getApproval(String username) throws RemoteException;
}
