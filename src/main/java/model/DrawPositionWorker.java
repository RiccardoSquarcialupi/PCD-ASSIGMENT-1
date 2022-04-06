package model;

import model.interfaces.BasicSwingWorker;

import java.awt.*;

public class DrawPositionWorker extends BasicSwingWorker {

    Graphics2D g2;
    Body b;
    Double scale;
    long dx;
    long dy;

    public DrawPositionWorker(Graphics2D g2, Body b, Double scale, long dx, long dy) throws Exception {
        this.g2 = g2;
        this.b = b;
        this.scale = scale;
        this.dx = dx;
        this.dy = dy;
        doInBackground();
    }

    @Override
    public Void doInBackground() throws Exception {
        P2d p = b.getPos();
        int radius = (int) (10 * scale);
        if (radius < 1) {
            radius = 1;
        }
        g2.drawOval((int) (dx + p.getX() * dx * scale), (int) (dy - p.getY() * dy * scale), radius, radius);
        return null;
    }

}
