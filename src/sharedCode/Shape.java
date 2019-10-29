package sharedCode;

import java.awt.*;
import java.io.Serializable;

public class Shape implements Serializable {
    private int x1,y1,x2,y2;
    private String name;
    private Color color;
    private String input;
    private int stroke = 1;
    private int fontsize = 12;


    public Shape(String name,Color color,int x1, int y1,int x2,int y2, String text, int stroke, int fontsize){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.name = name;
        this.color = color;
        this.input = text;
        this.stroke = stroke;
        this.fontsize = fontsize;
    }

    public void drawshape(Graphics2D g){
        g.setColor(color);
        g.setStroke(new BasicStroke(this.stroke));
        g.setFont(new Font("Arial", Font.PLAIN, this.fontsize));
        switch(name){
            case "BRUSH":
                g.drawLine(x1, y1, x2, y2);
                break;
            case "LINE":
                g.drawLine(x1, y1, x2, y2);
                break;
            case "ERASER":
                g.drawLine(x1, y1, x2, y2);
                break;
            case "CIRCLE":
                int radius = Math.abs(x2-x1);
                if (x2 > x1) {
                    if (y2 > y1) { // drag to bottom right
                        g.drawOval(x1, y1, radius, radius);
                    } else { // drag to top right
                        g.drawOval(x1, y2, radius, radius);
                    }
                } else {
                    if (y1 > y2){ // drag to top left
                        g.drawOval(x2, y2, radius, radius);
                    } else { // drag to bottom left
                        g.drawOval(x2, y1, radius, radius);
                    }
                }
                break;
            case "RECTANGLE":
                if (x2 > x1) {
                    if (y2 > y1) { // drag to bottom right
                        g.drawRect(x1, y1, x2 - x1, y2 - y1);
                    } else { // drag to top right
                        g.drawRect(x1, y2, x2 - x1, y1 - y2);
                    }
                } else {
                    if (y1 > y2){ // drag to top left
                        g.drawRect(x2, y2, x1 - x2, y1 - y2);
                    } else { // drag to button left
                        g.drawRect(x2, y1, x1 - x2, y2 - y1);
                    }
                }
                break;
            case "OVAL":
                int width = Math.abs(x2-x1);
                int height = Math.abs(y2-y1);
                if (x2 > x1) {
                    if (y2 > y1) { // drag to bottom right
                        g.drawOval(x1, y1, width, height);
                    } else { // drag to top right
                        g.drawOval(x1, y2, width, height);
                    }
                } else {
                    if (y1 > y2){ // drag to top left
                        g.drawOval(x2, y2, width, height);
                    } else { // drag to bottom left
                        g.drawOval(x2, y1, width, height);
                    }
                }
                break;
            case "TEXT":
                g.drawString(this.input, x1,y1);
        }
    }

}