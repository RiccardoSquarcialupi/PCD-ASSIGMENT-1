package controller;

import controller.Simulator;
import model.Body;
import model.Boundary;
import model.V2d;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import static gov.nasa.jpf.vm.Verify.println;

public class SimulatorSubTask implements Runnable{

    private final List<Body> bodies;
    private final Simulator master;

    public SimulatorSubTask(List<Body> bodies, Simulator master) {
        this.bodies = bodies;
        this.master = master;
    }

    @Override
    public void run() {
        while(master.getIter() < master.getnSteps()){
            while(!master.btnClicked){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for(var body : bodies){
                /* compute total force on bodies */
                V2d totalForce = computeTotalForceOnBody(body);

                /* compute instant acceleration */
                V2d acc = new V2d(totalForce).scalarMul(1.0 / body.getMass());
                /* update velocity */
                body.updateVelocity(acc, master.getDt());

                /* compute bodies new pos */
                body.updatePos(master.getDt());

                /* check collisions with boundaries */
                body.checkAndSolveBoundaryCollision(master.getBounds());
                try {
                    master.getBarrier().await();
                } catch (InterruptedException | BrokenBarrierException e) {
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
