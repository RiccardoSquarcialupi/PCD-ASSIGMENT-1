package controller;

import gov.nasa.jpf.vm.Verify;
import model.*;
import view.SimulationView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Simulator {

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        Simulator sim = new Simulator();
        sim.execute(500000);
    }

    private final SimulationView viewer;
    private List<List<Body>> partitions = new LinkedList<>();
    private CyclicBarrier barrier;
    private boolean shouldAwake = false;
    boolean btnClicked;

    /* bodies in the field */
    private ArrayList<Body> bodies;
    /* boundary of the field */
    private Boundary bounds;

    /* virtual time step */
    private double dt;
    private long nSteps;
    private long iter = 0;


    public Simulator() {
        btnClicked=false;
        viewer = new SimulationView(620, 620,this);
        /* initializing boundary and bodies */
        testBodySet4_many_bodies();
    }

    public Boundary getBounds() {
        return bounds;
    }

    public double getDt() {
        return dt;
    }

    public long getnSteps() {
        return nSteps;
    }

    public long getIter() {
        return iter;
    }

    public CyclicBarrier getBarrier() {
        return barrier;
    }

    public void execute(long nSteps) throws InterruptedException, BrokenBarrierException {
        this.nSteps = nSteps;
        int cores = Runtime.getRuntime().availableProcessors()/2;
        //cores = 1;
        partitions = partition(bodies, cores);
        barrier = new CyclicBarrier(partitions.size()+1);
        /* init virtual time */
        /* virtual time */
        double vt = 0;
        dt = 0.001;
        long iter = 0;

        //Verify.beginAtomic();
        for (var partition : partitions) {
            var thread = new SimulatorSubTask(partition, this);
            thread.start();
        }
        //Verify.endAtomic();
        /* simulation loop */
        int counter = 0;
        var startTime = System.currentTimeMillis();
        while (iter < nSteps) {
            while(!btnClicked){
                Thread.sleep(1000);
            }
            barrier.await();
            /* update virtual time */
            vt = vt + dt;
            iter++;
            /* display current stage */
            if(counter++ > 1000){
                viewer.display(bodies, vt, iter, bounds);
                counter = 0;
            }
        }
        var endTime = System.currentTimeMillis();
        System.out.format("Total time with %d cores: %d\n",cores,endTime-startTime);
        System.exit(0);
    }

    private <T> List<List<T>> partition(List<T> list, int n)
    {
        List<List<T>> partitions = new ArrayList<>();
        for(int i = 0; i < n; i++){
            int from = i*(list.size()/n);
            int to = from+(list.size()/n);
            if(to > list.size()) to = list.size();
            partitions.add(list.subList(from, to));
        }
        return partitions;
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
        int nBodies = 5000;
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


    public void setBtnClicked(boolean b) {
        this.btnClicked=b;
    }
}
