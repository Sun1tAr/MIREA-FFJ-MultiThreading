package my.learn;


import my.learn.thread_creating.MyRunnable;
import my.learn.thread_creating.MyThread;

public class Main {
    public static void main(String[] args) {
        Thread t = new MyThread();
        t.start();

        t = new Thread(new MyRunnable());
        try {
            t.sleep(10_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        t.start();

    }
}