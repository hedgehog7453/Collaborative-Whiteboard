package sharedCode;

import server.ServerRemoteInterface;

import javax.print.DocFlavor;
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
import java.util.Iterator;

public class DrawListener extends Component
        implements ActionListener, MouseListener, MouseMotionListener {

    private ServerRemoteInterface server;

    private JPanel canvas;
    private Graphics2D g;
    private String tool = "BRUSH";
    private Color color = Color.BLACK;
    private int x1, y1, x2, y2;
    private int stroke = 1;
    private int fontSize = 12;
    private Shape shape;
    private String defaultPath = "/Users/jiayuli/Desktop/";


    public DrawListener(ServerRemoteInterface server) {
        this.server = server;
        initToolData();
    }

    public Graphics2D getG() {
        return g;
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

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "New":
//                System.out.println("click save as");
                int value=JOptionPane.showConfirmDialog(null, "save current work？", "Warning", 0);
                if(value==0){
                    try{
                        saveFile("");
                    } catch (IOException e1){
                        System.out.println("failed to save");
                    }
                } else {
                    canvas.repaint(); // clear canvas
                }

                break;
            case "Open":
                try {
                    openFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.out.println("click open");
                break;
            case "Save":
//                System.out.println("click save");
                try {
                    saveFile(defaultPath);
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
//                System.out.println("click save as");
                break;
            default:
                System.out.println(cmd + " clicked");
        }
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

    public void mousePressed(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        //g.setColor(color);
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
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }

    }

    /**
     * Perform this method when the mouse moves the mouse over the form
     */
    public void mouseMoved(MouseEvent e) {}

    /**
     * Execute this method when you make a mouse click on the event source object
     * (click and release actions must be in the same position)
     */
    public void mouseClicked(MouseEvent e) {
        x1 = e.getX();
        y1 = e.getY();
        try {
            if (tool.equals("TEXT")) {
                String input;
                input = JOptionPane.showInputDialog(
                        "Please input the text you want!");
                if (input != null) {
                    shape = new Shape(tool,color,x1, y1, x2, y2, input, stroke, fontSize);
                    server.addShape(shape);
                }
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    public void mouseEntered(MouseEvent e) {
    }


    public void mouseExited(MouseEvent e) {
    }

    public void openFile() throws IOException {
        int value=JOptionPane.showConfirmDialog(null, "save current work？", "Warning", 0);
        if(value==0){
            saveFile("");
        }
        if(value==1){
            canvas.repaint(); // clear canvas
            try {
                // alert user to choose file
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(null);
                File file =chooser.getSelectedFile();
                if(file==null){
                    JOptionPane.showMessageDialog(null, "Didn't select file");
                }
                else {
                    // create output stream
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    // cast to shape type
                    ArrayList<Shape> list =(ArrayList<Shape>)ois.readObject();
                    // re-paint canvas
                    paint(g, list);
                    ois.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void saveFile(String path) throws IOException {
        if (path.equals("")){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        File fileName = fileChooser.getSelectedFile();
        if (fileName.getName().equals("")) {
            JOptionPane.showMessageDialog(fileChooser, "Invalid File Name",
                    "Invalid File Name", JOptionPane.ERROR_MESSAGE);
        } else {
            fileName.delete();
            try {
                FileOutputStream fis = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fis);
                // re-paint shapearray to canvas
                oos.writeObject(server.getWhiteBoard());
                JOptionPane.showMessageDialog(null, "Success！");
                oos.close();
            } catch (Exception e){
            }
        }
    } else {
            try {
                FileOutputStream fis = new FileOutputStream(defaultPath);
                ObjectOutputStream oos = new ObjectOutputStream(fis);
                // re-paint shapearray to canvas
                oos.writeObject(server.getWhiteBoard());
                JOptionPane.showMessageDialog(null, "Success！");
                oos.close();
            } catch (Exception e){
            }
        }
    }

    // 重写panel的paint方法，让repaint能够调用
    public void paint(Graphics g, ArrayList<Shape> array) {
        super.paint(g);
                for (Shape a: array) {
                    if(a != null) {
                        a.drawshape((Graphics2D) g);
                    } else {
                        break;
                    }
                }
    }

    public JPanel getCanvas() {
        return canvas;
    }

    /**
     * Set the canvas to draw on
     */
    public void setCanvas(JPanel canvas) {
        this.canvas = canvas;
        System.out.println(canvas.getGraphics());
        setG(canvas.getGraphics());
    }

    public void paint(Shape shape) {
        shape.drawshape(g);
    }

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

