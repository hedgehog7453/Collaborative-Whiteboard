package sharedCode;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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

}
