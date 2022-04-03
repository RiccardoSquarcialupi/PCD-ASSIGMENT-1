package controller;

import gov.nasa.jpf.vm.Verify;
import model.*;
import view.SimulationView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Simulator {

    public static void main(String[] args) {
        SimulationView viewer = new SimulationView(620, 620);
        Simulator sim = new Simulator(viewer);
        sim.execute(50000);
    }

    private final SimulationView viewer;
    private final List<List<Body>> partitions = new LinkedList<>();

    /* bodies in the field */
    ArrayList<Body> bodies;

    /* boundary of the field */
    private Boundary bounds;

    /* virtual time step */
    double dt;

    public Simulator(SimulationView viewer) {
        this.viewer = viewer;

        /* initializing boundary and bodies */
        testBodySet4_many_bodies();
        partitionateBodies();
    }

    public void execute(long nSteps) {
        /* init virtual time */

        /* virtual time */
        double vt = 0;
        dt = 0.001;

        long iter = 0;

        /* simulation loop */
        while (iter < nSteps) {
            var threads = new LinkedList<Thread>();
            //Verify.beginAtomic();
            for (var partition : partitions) {
                var thread = new SimulatorSubTask(partition, bounds, dt);
                thread.start();
                threads.add(thread);
            }
            //Verify.endAtomic();
            threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException ignored) {
                }
            });

            /* update virtual time */

            vt = vt + dt;
            iter++;

            /* display current stage */

            viewer.display(bodies, vt, iter, bounds);

        }
    }

    private void partitionateBodies(){
        int cores = Runtime.getRuntime().availableProcessors();
        int partitionSize = (int) Math.floor((float)bodies.size()/cores);
        for (int i = 0; i < bodies.size(); i += partitionSize) {
            partitions.add(bodies.subList(i,
                    Math.min(i + partitionSize, bodies.size())));
        }
    }

    private void testBodySet1_two_bodies() {
        bounds = new Boundary(-4.0, -4.0, 4.0, 4.0);
        bodies = new ArrayList<>();
        bodies.add(new Body(0, new P2d(-0.1, 0), new V2d(0, 0), 1));
        bodies.add(new Body(1, new P2d(0.1, 0), new V2d(0, 0), 2));
    }

    private void testBodySet2_three_bodies() {
        bounds = new Boundary(-1.0, -1.0, 1.0, 1.0);
        bodies = new ArrayList<>();
        bodies.add(new Body(0, new P2d(0, 0), new V2d(0, 0), 10));
        bodies.add(new Body(1, new P2d(0.2, 0), new V2d(0, 0), 1));
        bodies.add(new Body(2, new P2d(-0.2, 0), new V2d(0, 0), 1));
    }

    private void testBodySet3_some_bodies() {
        bounds = new Boundary(-4.0, -4.0, 4.0, 4.0);
        int nBodies = 100;
        testBodySet(nBodies);
    }

    private void testBodySet4_many_bodies() {
        bounds = new Boundary(-6.0, -6.0, 6.0, 6.0);
        int nBodies = 1000;
        testBodySet(nBodies);
    }

    private void testBodySet(int nBodies) {
        Random rand = new Random(System.currentTimeMillis());
        bodies = new ArrayList<>();
        for (int i = 0; i < nBodies; i++) {
            double x = bounds.getX0() * 0.25 + rand.nextDouble() * (bounds.getX1() - bounds.getX0()) * 0.25;
            double y = bounds.getY0() * 0.25 + rand.nextDouble() * (bounds.getY1() - bounds.getY0()) * 0.25;
            Body b = new Body(i, new P2d(x, y), new V2d(0, 0), 10);
            bodies.add(b);
        }
    }


}
