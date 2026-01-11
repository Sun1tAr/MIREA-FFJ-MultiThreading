package my.learn.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorExample {

    public static void main(String[] args) {
        ExecutorService fixedPool = Executors.newFixedThreadPool(2); //пул из 2х потоков
        for (int i = 0; i < 10; i++) {
            fixedPool.submit(new Task(i));
        }
        fixedPool.shutdown();
    }
}

class Task implements Runnable {
    private int number;

    public Task(int number) { this.number = number; }

    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + " Я родился! " + number);
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + " Я помер( " + number);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
