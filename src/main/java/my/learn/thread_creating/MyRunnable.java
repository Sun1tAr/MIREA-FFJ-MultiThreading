package my.learn.thread_creating;

public class MyRunnable implements Runnable {


    @Override
    public void run() {
        System.out.println("MyRunnable " +  Thread.currentThread() + " is running");
    }


}
