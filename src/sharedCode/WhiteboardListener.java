package sharedCode;

import gui.WhiteboardWindow;
import server.ServerRemoteInterface;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WhiteboardListener extends Component
        implements ActionListener, MouseListener, MouseMotionListener {

    private final ClientRemoteInterface client;
    private ServerRemoteInterface server;

    // Canvas to draw on
    private JPanel canvas;
    private Graphics2D g;

    // default paint tools
    private String tool = "BRUSH";
    private Color color = Color.BLACK;
    private int stroke = 1;
    private int fontSize = 12;

    private int x1, y1, x2, y2;
    private Shape shape;

    // save path
    private String path = "";

    // status
    private boolean isDisconnect = false;
    private boolean reconnect = false;

    // GUI window
    private WhiteboardWindow window;


    public WhiteboardListener(ServerRemoteInterface server, ClientRemoteInterface client) {
        this.server = server;
        this.client = client;
        initToolData();
    }

    // =============================== connection ===============================
    public boolean connectToServer(boolean firstConnection) {
        try {
            boolean isManager = client.getIsManager();
            String opTitle = "";
            if (isManager) {
                opTitle = "Creating a room ... ";
            } else {
                opTitle = "Joining a room ...";
            }
            boolean isUnique = false;
            String username = "";
            while (!isUnique) {
                JFrame frame = new JFrame("Connection");
                username = JOptionPane.showInputDialog(frame, "Please enter your username: ",
                        opTitle, JOptionPane.QUESTION_MESSAGE);
                if (username == null) {
                    if (firstConnection) {
                        System.out.println("Bye");
                        System.exit(0);
                    } else {
                        return false;
                    }
                }
                if (username.length() < 4) { // TODO: more validation (no space, etc.)
                    JOptionPane.showConfirmDialog(null, "Please enter a valid username. Your username needs to be longer than 4 letters.", "", JOptionPane.DEFAULT_OPTION);
                    continue;
                }
                isUnique = server.isUsernameUnique(username);
                if (!isUnique) {
                    JOptionPane.showConfirmDialog(null, "Username already exists. ", "", JOptionPane.DEFAULT_OPTION);
                }
            }
            boolean status = server.clientConnect(isManager, username, client);
            if (status) {
                JOptionPane.showConfirmDialog(null, "You are now in the room!", "Congratulations", JOptionPane.DEFAULT_OPTION);
                if (!firstConnection){
                    this.isDisconnect = false;
                    this.reconnect = true;
                }
            } else {
                JOptionPane.showConfirmDialog(null, "You are rejected by the manager.", "Oh no :(", JOptionPane.DEFAULT_OPTION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean confirmDisconnection() {
        try {
            boolean isManager = client.getIsManager();
            int answer;
            if (isManager) {
                answer = JOptionPane.showConfirmDialog(null,
                        "Disconnect from the room?\nApplication will be closed, all users will be disconnected from the room, and your work will not be saved.",
                        "", JOptionPane.YES_NO_OPTION);
            } else {
                answer = JOptionPane.showConfirmDialog(null,
                        "Disconnect from the room?\nYour work will not be saved unless it's saved by other collaborators.",
                        "", JOptionPane.YES_NO_OPTION);
            }
            return (answer==0)?true:false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean disconnectFromServer() {
        try {
            boolean isManager = client.getIsManager();
            if (isManager) {
                boolean removeAll = server.removeAllUsers();
                if (removeAll){
                    JOptionPane.showConfirmDialog(null,"Disconnection successful.","",JOptionPane.DEFAULT_OPTION);
                    System.exit(0);
                }else{
                    JOptionPane.showConfirmDialog(null,"Failed to notify all users.","",JOptionPane.DEFAULT_OPTION);
                    return removeAll;
                }
            } else {
                boolean disconnect = server.clientDisconnect(getUsername());
                if (disconnect){
                    this.isDisconnect = true;
                }
                return disconnect;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void forceQuit(String message){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showConfirmDialog(null,message,"",JOptionPane.DEFAULT_OPTION);
                System.exit(0);
            }
        });
    }

    // ============================ file ==============================
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "New":
                try {
                    if (server.getWhiteBoard().size() != 0) { // if nothing has been drawn
                        boolean save = promptSave();
                        if (save) {
                            boolean saveSuccess = saveFile(path); // TODO: if save is canceled, abort current operation
                            if (saveSuccess) {
                                server.clearAllShapes();
                                //server.updateClientCanvas();
                                server.broadcastMessage("Manager cleared the canvas. ");
                            }
                        } else {
                            server.clearAllShapes();
                            //server.updateClientCanvas();
                            server.broadcastMessage("Manager cleared the canvas. ");
                        }
                    }
                    path = "";
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Open":
                try {
                    if (server.getWhiteBoard().size() == 0){ // if nothing has been drawn
                        openFile();
                    } else {
                        boolean save = promptSave();
                        if (save) {
                            boolean saveSuccess = saveFile(path);
                            if (saveSuccess) {
                                openFile();
                            }
                        } else {
                            openFile();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Save":
                try {
                    saveFile(path);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Save as":
                try {
                    saveFile("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            case "Close":
                boolean confirm = confirmDisconnection();
                if (confirm) {
                    disconnectFromServer();
                    System.exit(0);
                }
                break;
            default:
                //System.out.println(cmd + " clicked");
        }
    }

    private boolean promptSave() {
        int value = JOptionPane.showConfirmDialog(null,
                "Save current drawing?", "Save? ", 0);
        if (value == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }

    public void openFile() throws IOException {
        // TODO: return false if save is canceled
        try {
            // alert user to choose file
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(null);
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File file = chooser.getSelectedFile();
            if (file == null) {
                path = "";
                JOptionPane.showMessageDialog(null, "Please select a file.");
            }
            else {
                path = file.getPath();
                // create output stream
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                // cast to shape type
                ArrayList<Shape> shapes = (ArrayList<Shape>)ois.readObject();
                ois.close();
                server.updateShapes(shapes);
                // re-paint canvas
                //server.updateClientCanvas();
                server.broadcastMessage("Manager opened the file \"" + file.getName() + "\". ");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public boolean saveFile(String path) throws IOException {
        if (path.equals("")){
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                return false;
            }
            File fileName = fileChooser.getSelectedFile();
            if (fileName.getName().equals("")) {
                JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                        "Invalid File Name", JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                try {
                    FileOutputStream fis = new FileOutputStream(fileName);
                    ObjectOutputStream oos = new ObjectOutputStream(fis);
                    // re-paint shapearray to canvas
                    oos.writeObject(server.getWhiteBoard());
                    JOptionPane.showMessageDialog(null, "Successfully saved. ");
                    oos.close();
                    this.path = fileName.getPath();
                } catch (Exception e){
                    JOptionPane.showMessageDialog(null, "Failed to save file. ");
                    return false;
                }
            }
        } else {
            try {
                FileOutputStream fis = new FileOutputStream(path);
                ObjectOutputStream oos = new ObjectOutputStream(fis);
                // re-paint shapearray to canvas
                oos.writeObject(server.getWhiteBoard());
                JOptionPane.showMessageDialog(null, "Successfully saved. ");
                oos.close();
            } catch (Exception e){
                JOptionPane.showMessageDialog(null, "Failed to save file. ");
                return false;
            }
        }
        return true;
    }

    // ============================ draw ==============================

    // Called by client remote
    public void updateCanvas(int millis, ArrayList<Shape> shapes){
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    canvas.repaint();
//                    drawAllShapes(shapes);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        try{
            Thread thread = new MyThread();
            Thread.currentThread().sleep(millis);//毫秒
            thread.start();
            Thread.currentThread().sleep(20);//毫秒
        } catch (InterruptedException e){
            System.out.println("failed to get board from server");
        }
    }

    // called by local classes
    public void updateCanvasFromServer() {
        synchronized (canvas) {
            try {
                System.out.println("retrieve canvas from server and show");
                updateCanvas(100, server.getWhiteBoard());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            synchronized (canvas) {
                try {
                    System.out.println("showing canvas");
                    canvas.repaint();
                    drawAllShapes(server.getWhiteBoard());
                } catch (RemoteException ex) {
                    System.out.println("failed to repaint graph given by server");
                }
            }
        }
    }

    public void drawAllShapes(ArrayList<Shape> allShapes) {
        // TODO: 清空canvas然后重新画一遍所有shapes
        if (g == null){
            System.out.println("g doesn't exist");
        } else {
            paint(g, allShapes);
        }
    }

    // 重写panel的paint方法，让repaint能够调用
    public void paint(Graphics2D g, ArrayList<Shape> array) {
        super.paint(g);
        System.out.println("重绘全部shape " + array.size());
        if (array.size()>0){
            for (Shape a: array) {
                if(a != null) {
//                System.out.println(a.);
                    a.drawshape(g);
                } else {
                    break;
                }
            }
        }
    }

    // 重绘单个shape
    public void paint(Shape shape) {
        shape.drawshape(g);
    }

    public Graphics2D getG() {
        return g;
    }

    /**
     * Set the canvas to draw on
     */
    public void setCanvas(JPanel canvas) {
        this.canvas = canvas;
        setG(canvas.getGraphics());
    }


    public void setG(Graphics g) {
        this.g = ((Graphics2D) g);
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public int getStroke() {
        return stroke;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void toolBtnClicked(String text) {
        tool = text;
        System.out.println("text = " + text);
    }

    public void paletteBtnClicked(Color c) {
        color = c;
        System.out.println("palette color");
    }

    public void colourBtnClicked(String text) {
        color = getColourByStr(text);
        System.out.println("color = " + text);
    }

    public void sizeBtnClicked(int Stroke, int font) {
        stroke = Stroke;
        g.setStroke(new BasicStroke(stroke));
        fontSize = font;
        g.setFont(new Font("Arial", Font.PLAIN, font));
    }

    public void editUserDrawStatus(ArrayList<String> users) {
        window.setDrawStatusText(users);
    }

    public void mousePressed(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        try {
            server.addEditingUser(client.getUsername());
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Execute this method when you have a mouse button release on the event source object
     */
    public void mouseReleased(MouseEvent e) {
        // Get the coordinate value when the mouse is released
        x2 = e.getX();
        y2 = e.getY();
        // Draw graphics based on coordinate values, colors, and thickness
        try {
            if (!isDisconnect){
                switch (tool) {
                    case "LINE":
                        shape = new Shape(tool,color,x1, y1, x2, y2, null, stroke,fontSize );
                        server.addShape(shape);
                        break;
                    case "CIRCLE":
                    case "OVAL":
                    case "RECTANGLE":
                        // 如果向右拖拽
                        if (x2 > x1) {
                            shape = new Shape(tool,color,x1, y1, x2, y2, null, stroke, fontSize);
                            server.addShape(shape);
                        } else {
                            // 如果向左拖拽
                            shape = new Shape(tool,color,x1, y1, x2, y2, null, stroke, fontSize);
                            server.addShape(shape);
                        }
                        break;
                }
                server.removeEditingUser(client.getUsername());
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Perform this method when the mouse presses a button on the form and drags it
     */
    public void mouseDragged(MouseEvent e) {
        x2 = e.getX();
        y2 = e.getY();
        g.setColor(g.getColor());
        try {
            if (!isDisconnect){
                if (tool.equals("BRUSH")) {
                    shape = new Shape(tool,color,x1, y1, x2, y2, null, stroke, fontSize);
                    server.addShape(shape);
                    x1 = x2;
                    y1 = y2;
                } else if (tool.equals("ERASER")) {
                    shape = new Shape(tool,canvas.getBackground(),x1, y1, x2, y2, null, stroke, fontSize);
                    server.addShape(shape);
                    x1 = x2;
                    y1 = y2;
                }
                server.addEditingUser(client.getUsername());
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * Execute this method when you make a mouse click on the event source object
     * (click and release actions must be in the same position)
     */
    public void mouseClicked(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        try {
            if (!isDisconnect){
                if (tool.equals("TEXT")) {
                    String input;
                    input = JOptionPane.showInputDialog(
                            "Please input the text you want!");
                    if (input != null) {
                        shape = new Shape(tool,color,x1, y1, x2, y2, input, stroke, fontSize);
                        server.addShape(shape);
                    }
                }}
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Perform this method when the mouse moves the mouse over the form
     */
    public void mouseMoved(MouseEvent e) {}


    public void mouseEntered(MouseEvent e) {
    }


    public void mouseExited(MouseEvent e) {
    }


    // ============================ chat ==============================

    public void displayMes(String msg){
        this.window.appendTextToMessages(msg);
    }

    public void displayAllMessages(ArrayList<String> allMessages) {
        window.displayAllMessages(allMessages);
    }

    public void postMessage(String msg) {
        try{
            server.sendMessage(msg, this.client.getUsername());
        } catch (RemoteException e3){
            System.out.println("failed to send message");
        }
    }

    public void updateOnlineUsers(String managerName, ArrayList<String> usernames) {
        this.window.displayOnlineUsers(managerName, usernames);
    }

    public void kickUser(String username) {
        try {
            server.kickUser(username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    // Accessors & Mutators
    public boolean getReconnect(){
        return reconnect;
    }

    public void setWindow(WhiteboardWindow win){
        this.window = win;
    }

    public String getUsername() {
        try {
            return client.getUsername();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean getIsManager() {
        try {
            return client.getIsManager();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getIsConnected() {
        try {
            return server.getIsConnected(client.getUsername());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<String> getAllMessages() {
        try {
            return server.getAllMessages();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public ArrayList<Shape> getAllShapes() {
//        try {
//            return server.getWhiteBoard();
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//

    // Paint data
    private ArrayList<String> paintToolsOrder;
    private ArrayList<String> coloursOrder;
    private HashMap<String, Color> colourData;

    /**
     * Get the paint tool string at the input index in paintToolsOrder ArrayList
     */
    public String getPaintToolStr(int index) {
        return paintToolsOrder.get(index);
    }

    /**
     * Get the colour string at the input index in coloursOrder ArrayList
     */
    public String getColourStr(int index) {
        return coloursOrder.get(index);
    }

    /**
     * Return Color by colour string from coloursOrder ArrayList
     */
    public Color getColourByStr(String str) {
        return colourData.get(str);
    }

    private void initToolData() {
        paintToolsOrder = new ArrayList<String>(Arrays.asList(
                "BRUSH", "ERASER", "LINE", "CIRCLE", "RECTANGLE", "OVAL", "TEXT"));
        coloursOrder = new ArrayList<String>(Arrays.asList(
                "RED", "BLUE", "GREEN", "YELLOW", "ORANGE", "LIGHTBLUE",
                "LIGHTGREEN", "PURPLE", "PINK", "CYAN", "TAN", "BROWN",
                "BLACK", "DARKGREY", "LIGHTGREY", "WHITE"));
        colourData = new HashMap<String, Color>();
        colourData.put("RED", Color.RED);
        colourData.put("BLUE", Color.BLUE);
        colourData.put("GREEN", Color.GREEN);
        colourData.put("YELLOW", Color.YELLOW);
        colourData.put("ORANGE", Color.ORANGE);
        colourData.put("LIGHTBLUE", new Color(157, 175, 255));
        colourData.put("LIGHTGREEN", new Color(129, 197, 122));
        colourData.put("PURPLE", new Color(129, 38, 192));
        colourData.put("PINK", Color.PINK);
        colourData.put("CYAN", Color.CYAN);
        colourData.put("TAN", new Color(233, 222, 187));
        colourData.put("BROWN", new Color(129, 74, 25));
        colourData.put("BLACK", Color.BLACK);
        colourData.put("DARKGREY", Color.DARK_GRAY);
        colourData.put("LIGHTGREY", Color.LIGHT_GRAY);
        colourData.put("WHITE", Color.WHITE);
    }
}

