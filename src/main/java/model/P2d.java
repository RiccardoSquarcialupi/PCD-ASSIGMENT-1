package model;

import model.interfaces.P2dInterface;

public class P2d implements P2dInterface {

    private double x, y;

    public P2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void sum(V2d v) {
        x += v.x;
        y += v.y;
    }

    @Override
    public void change(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}
