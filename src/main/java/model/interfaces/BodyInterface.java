package model.interfaces;

import model.Body;
import model.Boundary;
import model.P2d;
import model.V2d;
import res.InfiniteForceException;

public interface BodyInterface {
    double getMass();

    P2d getPos();

    V2d getVel();

    int getId();

    boolean equals(Body b);

    void updatePos(double dt);

    void updateVelocity(V2d acc, double dt);

    void changeVel(double vx, double vy);

    double getDistanceFrom(Body b);

    V2d computeRepulsiveForceBy(Body b) throws InfiniteForceException;

    V2d getCurrentFrictionForce();

    void checkAndSolveBoundaryCollision(Boundary bounds);
}
