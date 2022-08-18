package org.uma.jmetal.parallel.asynchronous.multithreaded;

import org.uma.jmetal.parallel.asynchronous.task.ParallelTask;

import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class Worker<T extends ParallelTask<?>> extends Thread {
    private final BlockingQueue<T> completedTaskQueue;
    private final BlockingQueue<T> pendingTaskQueue;

    protected Function<T, T> computeFunction;

    public Worker(
            Function<T, T> computeFunction,
            BlockingQueue<T> pendingTaskQueue,
            BlockingQueue<T> completedTaskQueue) {
        this.computeFunction = computeFunction;
        this.completedTaskQueue = completedTaskQueue;
        this.pendingTaskQueue = pendingTaskQueue;
    }

    @Override
    public void run() {
        while (true) {
            T taskToCompute = null;

            try {
                taskToCompute = pendingTaskQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            T computedTask = computeFunction.apply(taskToCompute);

            completedTaskQueue.add(computedTask);
        }
    }

    public BlockingQueue<T> getCompletedTaskQueue() {
        return completedTaskQueue;
    }

    public BlockingQueue<T> getPendingTaskQueue() {
        return pendingTaskQueue;
    }
}
