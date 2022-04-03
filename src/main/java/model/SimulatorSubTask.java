package model;

import controller.Simulator;

import java.util.List;
import java.util.concurrent.Semaphore;

import static gov.nasa.jpf.vm.Verify.println;

public class SimulatorSubTask extends Thread{

    private final Barrier barrier;
    private final List<Body> bodies;
    private final Boundary bounds;
    private final double dt;

    public SimulatorSubTask(Barrier barrier, List<Body> bodies, Boundary bounds, double dt) {
        this.barrier = barrier;
        this.bodies = bodies;
        this.bounds = bounds;
        this.dt = dt;
    }

    @Override
    public void run() {
        while(true){
            for(var body : bodies){
                /* compute total force on bodies */
                V2d totalForce = computeTotalForceOnBody(body);

                /* compute instant acceleration */
                V2d acc = new V2d(totalForce).scalarMul(1.0 / body.getMass());
                /* update velocity */
                body.updateVelocity(acc, dt);

                /* compute bodies new pos */
                body.updatePos(dt);

                /* check collisions with boundaries */
                body.checkAndSolveBoundaryCollision(bounds);
                try {
                    barrier.hitAndWait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private V2d computeTotalForceOnBody(Body b) {

        V2d totalForce = new V2d(0, 0);

        /* compute total repulsive force */

        for (Body otherBody : bodies) {
            if (!b.equals(otherBody)) {
                try {
                    V2d forceByOtherBody = b.computeRepulsiveForceBy(otherBody);
                    totalForce.sum(forceByOtherBody);
                } catch (Exception ignored) {
                }
            }
        }
        /* add friction force */
        totalForce.sum(b.getCurrentFrictionForce());

        return totalForce;
    }
}
