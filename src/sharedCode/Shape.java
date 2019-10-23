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
        g.setFont(new Font("Arial", Font.PLAIN, this.fontsize));//设置字体大小
        switch(name){
            case "BRUSH":
                g.drawLine(x1, y1, x2, y2);
                break;
            case "LINE":
                g.drawLine(x1, y1, x2, y2);
                break;
            case "CIRCLE":
                int radius = Math.abs(x2-x1);
                // 如果向右下方拖拽
                // 如果向右上方拖拽
                if (x2 > x1) {
                    if (y2 > y1) {
                        g.drawOval(x1, y1, radius, radius);
                    } else {
                        g.drawOval(x1, y2, radius, radius);
                    }
                } else {
                    // 如果向左上方拖拽
                    // 如果向左下方拖拽
                    if (y1 > y2){
                        g.drawOval(x2, y2, radius, radius);
                    } else {
                        g.drawOval(x2, y1, radius, radius);
                    }
                }
                break;
            case "RECTANGLE":
                // 如果向右下方拖拽
                // 如果向右上方拖拽
                if (x2 > x1) {
                    if (y2 > y1) {
                        g.drawRect(x1, y1, x2 - x1, y2 - y1);
                    } else {
                        g.drawRect(x1, y2, x2 - x1, y1 - y2);
                    }
                } else {
                    // 如果向左上方拖拽
                    // 如果向左下方拖拽
                    if (y1 > y2){
                        g.drawRect(x2, y2, x1 - x2, y1 - y2);
                    } else {
                        g.drawRect(x2, y1, x1 - x2, y2 - y1);
                    }
                }
                break;
            case "OVAL":
                int width = Math.abs(x2-x1);
                int height = Math.abs(y2-y1);

                // 如果向右下方拖拽
                // 如果向右上方拖拽
                if (x2 > x1) {
                    if (y2 > y1) {
                        g.drawOval(x1, y1, width, height);
                    } else {
                        g.drawOval(x1, y2, width, height);
                    }
                } else {
                    // 如果向左上方拖拽
                    // 如果向左下方拖拽
                    if (y1 > y2){
                        g.drawOval(x2, y2, width, height);
                    } else {
                        g.drawOval(x2, y1, width, height);
                    }
                }
                break;
            case "ERASER":
                g.drawLine(x1, y1, x2, y2);
                break;
            case "TEXT":
                g.drawString(this.input, x1,y1);
        }
    }

}