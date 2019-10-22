package Server;

import SharedCode.ClientRemoteImpl;
import SharedCode.ClientRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ServerRemoteImpl extends UnicastRemoteObject implements ServerRemoteInterface {

    ClientRemoteInterface manager;
    String managerName;
    HashMap<String, ClientRemoteInterface> users;

    public ServerRemoteImpl() throws RemoteException {
        users = new HashMap<>();
    }

    @Override
    public boolean clientConnect(boolean isManager, String username, ClientRemoteInterface client) throws RemoteException {
        if (isManager) {
            System.out.println("manager " + username + " joined");
            manager = client;
            managerName = username;
            return true;
        } else {
            System.out.println("user " + username + " joined");
            if (!users.keySet().contains(username)) { //unique
                users.put(username, client);
                return manager.getApproval(username);
            }
        }
        //client.test();
        return false;
    }

}
