package sharedCode;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ClientRemoteImpl extends UnicastRemoteObject implements ClientRemoteInterface {

    private Whiteboard thisWb;

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
        System.out.println("server call back");
        thisWb.getDrawListener().broadcastMes(msg);
    }

    @Override
    public void displayUserList(ArrayList<String> users) throws RemoteException {
        System.out.println("server call back to display user list");
        thisWb.getDrawListener().displayOnlineUsers(users, false);
    }

    @Override
    public String getUsername() throws RemoteException {
        return thisWb.getUsername();
    }


}
