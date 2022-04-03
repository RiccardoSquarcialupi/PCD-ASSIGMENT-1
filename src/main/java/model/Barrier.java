package model;

import controller.Simulator;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Barrier {

    private final int totalTasks;
    private int arrivedTasks;
    private final Simulator mainTask;
    private final Semaphore semaphore = new Semaphore(0);
    private final Lock lock = new ReentrantLock();

    public Barrier(Simulator mainTask, int totalTasks) {
        this.mainTask = mainTask;
        this.totalTasks = totalTasks;
    }

    public void hitAndWait() throws InterruptedException {
        lock.lock();
        arrivedTasks++;
        if (arrivedTasks >= totalTasks) {
            arrivedTasks = 0;
            mainTask.awake();
            wakeAll();
        }
        lock.unlock();
        semaphore.acquire(1);
    }

    public void wakeAll(){
        semaphore.release(totalTasks);
    }
}
