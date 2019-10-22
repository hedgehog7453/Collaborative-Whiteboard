package Server;

import SharedCode.ClientRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class ServerRemoteImpl extends UnicastRemoteObject implements ServerRemoteInterface {

    ClientRemoteInterface manager;
    String managerName;
    HashMap<String, ClientRemoteInterface> users;

    public ServerRemoteImpl() throws RemoteException {
        managerName = "";
        users = new HashMap<>();
    }


    public boolean isUsernameUnique(String username) {
        if (managerName.equals(username)) {
            return false;
        }
        return !users.keySet().contains(username);
    }

    @Override
    public boolean clientConnect(boolean isManager, String username, ClientRemoteInterface client) throws RemoteException {
        if (isManager) {
            manager = client;
            managerName = username;
            System.out.println("manager " + username + " joined");
            return true;
        } else {
            users.put(username, client);
            boolean managerApproved = manager.getApproval(username);
            if (managerApproved) {
                System.out.println("user " + username + " joined");
                return true;
            }
            return false;
        }
    }

}
