package server;

import sharedCode.ClientRemoteInterface;
import sharedCode.Shape;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerRemoteImpl extends UnicastRemoteObject implements ServerRemoteInterface {

    // Online user management
    ClientRemoteInterface manager;
    String managerName;
    HashMap<String, ClientRemoteInterface> users;

    // draw
    ArrayList<sharedCode.Shape> shapeArrayList = new ArrayList<>();

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

    @Override
    public ArrayList<Shape> getWhiteBoard() throws RemoteException {
        //System.out.println("get");
        return shapeArrayList;
    }

    @Override
    public void addShape(Shape shape) throws RemoteException {
        // System.out.println("add shape");
        shapeArrayList.add(shape);
        manager.drawNewShape(shape);
        for (ClientRemoteInterface client : users.values()) {
            client.drawNewShape(shape);
        }
    }
//    @Override
//    public void editWhiteBoard(ArrayList<sharedCode.Shape> shape) throws RemoteException {
//        shapeArrayList = shape;
//    }
}
