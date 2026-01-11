package my.learn.thread_creating;

public class MyThread extends Thread {

    @Override
    public void run() {
        System.out.println("MyThread " +  Thread.currentThread() + " is running");
    }



}
