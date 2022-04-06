package model;/*
 *   model.V2d.java
 *
 * Copyright 2000-2001-2002  aliCE team at deis.unibo.it
 *
 * This software is the proprietary information of deis.unibo.it
 * Use is subject to license terms.
 *
 */

import model.interfaces.V2dInterface;
import res.NullVectorException;

/**
 * 2-dimensional vector
 * objects are completely state-less
 */
public class V2d implements V2dInterface {

    public double x, y;

    public V2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public V2d(V2d v) {
        this.x = v.x;
        this.y = v.y;
    }

    public V2d(P2d from, P2d to) {
        this.x = to.getX() - from.getX();
        this.y = to.getY() - from.getY();
    }

    @Override
    public V2d scalarMul(double k) {
        x *= k;
        y *= k;
        return this;
    }

    @Override
    public void sum(V2d v) {
        x += v.x;
        y += v.y;
    }

    @Override
    public V2d normalize() throws NullVectorException {
        double mod = Math.sqrt(x * x + y * y);
        if (mod > 0) {
            x /= mod;
            y /= mod;
            return this;
        } else {
            throw new NullVectorException();
        }

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
