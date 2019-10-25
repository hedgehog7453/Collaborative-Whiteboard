package user;

import sharedCode.Whiteboard;

public class UserWhiteboard {

    public static void main(String[] args) {

        String ip = args[0]; // IP address
        String port = args[1]; // port number
        //String port2 = args[2]; // another port number if there is a user connecting from the same machine as manager
        //System.out.println(ip + " " + port);

        // Initialise app
        Whiteboard wb = new Whiteboard();
        wb.initialiseApp(false, ip, port);
    }
}
