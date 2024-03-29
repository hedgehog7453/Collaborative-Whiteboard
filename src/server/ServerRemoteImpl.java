package server;

import sharedCode.ClientRemoteInterface;
import sharedCode.Shape;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerRemoteImpl extends UnicastRemoteObject implements ServerRemoteInterface {

    // Online user management
    String managerName;
    ClientRemoteInterface manager;
    HashMap<String, ClientRemoteInterface> users;
    ArrayList<String> pendingUsers;
    ArrayList<String> editingUsers;
    // draw
    ArrayList<sharedCode.Shape> shapeArrayList;
    // chat
    ArrayList<String> messages;


    public ServerRemoteImpl() throws RemoteException {
        managerName = "";
        users = new HashMap<String, ClientRemoteInterface>();
        pendingUsers = new ArrayList<String>();
        editingUsers = new ArrayList<String>();
        messages = new ArrayList<>();
        shapeArrayList = new ArrayList<>();
    }

    // Connection & Disconnection
    @Override
    public boolean isUsernameUnique(String username) {
        if (managerName.equals(username)) {
            return false;
        }
        if (!users.keySet().contains(username) && !pendingUsers.contains(username)) {
            //System.out.println("Adding user " + username + " to pending user list");
            pendingUsers.add(username);
            return true;
        }
        return false;
    }

    @Override
    public boolean getIsConnected(String username) throws RemoteException {
        if (managerName.equals(username)) {
            return true;
        }
        if (!users.isEmpty()) {
            return users.keySet().contains(username);
        }
        return false;
    }

    @Override
    public boolean clientConnect(boolean isManager, String username, ClientRemoteInterface client) throws RemoteException {
        if (isManager) {
            manager = client;
            managerName = username;
            client.setUsername(username);
            broadcastMessage(username + " just joined the room as manager.");
            return true;
        } else {
            boolean managerApproved = manager.getApproval(username);
            if (managerApproved) {
                pendingUsers.remove(username);
                users.put(username, client);
                client.setUsername(username);
                updateAllUserlists();
                broadcastMessage(username + " just joined the room.");
                client.displayAllMessages();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean clientDisconnect(String username) throws RemoteException{
        broadcastMessage(username + " left the room.");
        return removeUser(username);
    }

    @Override
    public boolean kickUser(String username) throws RemoteException {
        ClientRemoteInterface client = users.get(username);
        //System.out.println(client);
        if (client != null) {
            client.forceQuit("You are kicked out of the room by the manager.");
            //System.out.println("forced");
            broadcastMessage(username + " is kicked by the manager.");
            removeUser(username);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeUser(String username) throws RemoteException {
        if(users.containsKey(username)){
            users.remove(username);
            updateAllUserlists();
            return true;
        } else {
            //System.out.println("user does not exist.");
            return false;
        }
    }

    @Override
    public boolean removeAllUsers() throws RemoteException{
        for (ClientRemoteInterface client : users.values()) {
            client.forceQuit("Manager has left the room. You are forced to quit.");
        }
        return true;
    }

    @Override
    public void updateAllUserlists() throws RemoteException {
        try {
            //System.out.println("update user list");
            if (!users.isEmpty()) {
                //System.out.println(users.keySet());
                manager.displayUserList(managerName, new ArrayList<String>(users.keySet()));
                for (ClientRemoteInterface client : users.values()) {
                    client.displayUserList(managerName, new ArrayList<String>(users.keySet()));
                }
            } else {
                manager.displayUserList(managerName, null);
            }
        } catch (Exception e){
            e.printStackTrace();
            //System.out.println("server failed to update user list");
        }
    }

    // Draw
    @Override
    public ArrayList<Shape> getWhiteBoard() throws RemoteException {
        synchronized (shapeArrayList) {
            //System.out.println("getting whiteboard " + shapeArrayList.size());
            return shapeArrayList;
        }
    }

    @Override
    public void addShape(Shape shape) throws RemoteException {
        // System.out.println("add shape");
        synchronized (shapeArrayList) {
            shapeArrayList.add(shape);
            manager.drawNewShape(shape);
            for (ClientRemoteInterface client : users.values()) {
                client.drawNewShape(shape);
            }
        }
    }

    @Override
    public void clearAllShapes() throws RemoteException {
        synchronized (shapeArrayList) {
            //System.out.println("remove all shapes");
            shapeArrayList = new ArrayList<Shape>();
            updateClientCanvas();
        }
    }

    @Override
    public void updateShapes(ArrayList<Shape> shapes) throws RemoteException {
        synchronized (shapeArrayList) {
            this.shapeArrayList = new ArrayList<Shape>(shapes);
            updateClientCanvas();
        }
    }

    @Override
    public void updateClientCanvas() throws RemoteException {
        synchronized (shapeArrayList) {
            manager.updateCanvas(shapeArrayList);
            if (!users.isEmpty()) {
                for (ClientRemoteInterface user : users.values()) {
                    user.updateCanvas(shapeArrayList);
                }
            }
        }
    }

    @Override
    public void addEditingUser(String username) throws RemoteException {
        if (!editingUsers.contains(username)) {
            editingUsers.add(username);
            manager.updateUserDrawStatus(editingUsers);
            if (!users.isEmpty()) {
                for (ClientRemoteInterface user : users.values()) {
                    user.updateUserDrawStatus(editingUsers);
                }
            }
        }
    }

    @Override
    public void removeEditingUser(String username) throws RemoteException {
        if (editingUsers.contains(username)) {
            editingUsers.remove(username);
            manager.updateUserDrawStatus(editingUsers);
            if (!users.isEmpty()) {
                for (ClientRemoteInterface user : users.values()) {
                    user.updateUserDrawStatus(editingUsers);
                }
            }
        }
    }

    // Chat
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
            //System.out.println("server failed to broadcast message");
        }
    }


    // Accessors
    @Override
    public ArrayList<String> getAllMessages() throws RemoteException {
        return messages;
    }

}
