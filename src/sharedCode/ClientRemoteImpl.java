package sharedCode;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ClientRemoteImpl extends UnicastRemoteObject implements ClientRemoteInterface {

    private WhiteboardListener wbl;

    private String username;
    private boolean isManager;

    public ClientRemoteImpl() throws RemoteException {
    }

    // Connection & Disconnection
    @Override
    public boolean getApproval(String username) throws RemoteException {
        //System.out.println(username);
        int input = JOptionPane.showConfirmDialog(null,
                "user " + username + " would like to join the room.",
                "User join request",JOptionPane.YES_NO_OPTION);
        // 0 = yes, 1 = no
        return input==0?true:false;
    }

    @Override
    public void displayAllMessages() throws RemoteException {
        ArrayList<String> allMessages = wbl.getAllMessages();
        wbl.displayAllMessages(allMessages);
    }

    @Override
    public void forceQuit(String message) throws RemoteException {
        wbl.forceQuit(message);
    }

    // Users
    @Override
    public void displayUserList(String managerName, ArrayList<String> users) throws RemoteException {
        System.out.println("server call back to display user list");
        wbl.updateOnlineUsers(managerName, users);
    }

    // Draw
    public void drawNewShape(Shape shape) {
        wbl.paint(shape);
    }

    @Override
    public void updateCanvas() {
//        wbl.getboardfromServer(200);
        Thread queryThread = new Thread() {
            public void run() {
//                wbl.setCanvas(canvasPanel);
//                System.out.println("connected");
                wbl.getboardfromServer(100);

            }
        };
        queryThread.start();
    }

    // Chat
    @Override
    public void displayMsg(String msg) throws RemoteException {
        System.out.println("server call back to display message");
        wbl.displayMes(msg);
    }


    // Accessors & Mutators
    @Override
    public void setWhiteboardListener(WhiteboardListener wbl) throws RemoteException {
        this.wbl = wbl;
    }

    @Override
    public void setIsManager(boolean isManager) throws RemoteException {
        this.isManager = isManager;
    }

    @Override
    public void setUsername(String username) throws RemoteException {
        System.out.println("set user name as " + username);
        this.username = username;
    }

    @Override
    public boolean getIsManager() throws RemoteException {
        return isManager;
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }


}
