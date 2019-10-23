package server;

import sharedCode.ClientRemoteInterface;
import sharedCode.Shape;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ServerRemoteImpl extends UnicastRemoteObject implements ServerRemoteInterface {

    // Online user management
    String managerName;
    ClientRemoteInterface manager;
    HashMap<String, ClientRemoteInterface> users;

    // draw
    ArrayList<sharedCode.Shape> shapeArrayList = new ArrayList<>();

    // chat
    ArrayList<String> messages;


    public ServerRemoteImpl() throws RemoteException {
        managerName = "";
        users = new HashMap<String, ClientRemoteInterface>();
        messages = new ArrayList<>();
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
            client.setUsername(username);
            updateAllUserlists();
            broadcastMessage(username + " just joined the room as manager.");
            return true;
        } else {
            boolean managerApproved = manager.getApproval(username);
            if (managerApproved) {
                users.put(username, client);
                client.setUsername(username);
                updateAllUserlists();
                broadcastMessage(username + " just joined the room.");
                return true;
            }
            return false;
        }
    }

    @Override
    public void updateAllUserlists() throws RemoteException {
        try {
            System.out.println("update user list");
            if (!users.isEmpty()) {
                System.out.println(users.keySet());
                manager.displayUserList(managerName, new ArrayList<String>(users.keySet()));
                for (ClientRemoteInterface client : users.values()) {
                    client.displayUserList(managerName, new ArrayList<String>(users.keySet()));
                }
            } else {
                manager.displayUserList(managerName, null);
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("server failed to update user list");
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

    @Override
    public void sendMessage(String msg, String username) throws RemoteException {
        String thismsg = "";
        if (username.equals(managerName)) {
            thismsg = username +" (Manager): " + msg;
        } else {
            thismsg = username +": " + msg;
        }
        broadcastMessage(thismsg);
    }

    public void broadcastMessage(String msg) throws RemoteException {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("[HH:mm:ss] ");
            String thismsg = sdf.format(timestamp) + msg;
            messages.add(thismsg);
            manager.displayMsg(thismsg);
            if (!users.isEmpty()) {
                for (ClientRemoteInterface client : users.values()) {
                    client.displayMsg(thismsg);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("server failed to broadcast message");
        }
    }

    @Override
    public String getManagerName() throws RemoteException {
        return managerName;
    }

    @Override
    public Set<String> getUserList() throws RemoteException {
        if (users.isEmpty()) {
            System.out.println("userlist is null");
            return null;
        } else {
            System.out.println("return userlist size: " + users.keySet().size());
            return users.keySet();
        }
    }

    public ArrayList<String> getAllMessages() throws RemoteException {
        return messages;
    }
//    @Override
//    public void editWhiteBoard(ArrayList<sharedCode.Shape> shape) throws RemoteException {
//        shapeArrayList = shape;
//    }
}
