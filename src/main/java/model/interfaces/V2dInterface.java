package model.interfaces;

import model.V2d;
import res.NullVectorException;

public interface V2dInterface {
    V2d scalarMul(double k);

    void sum(V2d v);

    V2d normalize() throws NullVectorException;

    void change(double x, double y);

    double getX();

    double getY();
}
