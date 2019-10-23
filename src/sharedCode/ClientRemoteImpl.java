package sharedCode;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Set;

public class ClientRemoteImpl extends UnicastRemoteObject implements ClientRemoteInterface {

    private Whiteboard thisWb;

    private String username;

    public ClientRemoteImpl(Whiteboard wb) throws RemoteException {
        this.thisWb = wb;
    }

    public boolean getApproval(String username) throws RemoteException {
        //System.out.println(username);
        int input = JOptionPane.showConfirmDialog(null,
                "user " + username + " would like to join the room.",
                "User join request",JOptionPane.YES_NO_OPTION);
        // 0 = yes, 1 = no
        return input==0?true:false;
    }

    public void drawNewShape(Shape shape) {
        thisWb.getDrawListener().paint(shape);
    }

    @Override
    public void displayMsg(String msg) throws RemoteException {
        System.out.println("server call back to display message");
        thisWb.getDrawListener().displayMes(msg);
    }

    @Override
    public void displayUserList(String managerName, ArrayList<String> users) throws RemoteException {
        System.out.println("server call back to display user list");
        thisWb.getDrawListener().updateOnlineUsers(managerName, users);
    }

    @Override
    public void setUsername(String username) throws RemoteException {
        System.out.println("set user name as " + username);
        this.username = username;
    }

    @Override
    public String getUsername() throws RemoteException {
        System.out.println(username);
        return username;
    }


}
