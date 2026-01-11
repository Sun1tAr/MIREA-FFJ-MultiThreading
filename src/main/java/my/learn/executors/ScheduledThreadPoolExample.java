package my.learn.executors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExample {
    public static void main(final String[] arguments) throws InterruptedException {

        new Thread(() -> {
           while (true) {
               System.out.println("Time!");
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   System.out.println(e.getMessage());
               }
           }
        }).start();

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.schedule(new Alarm(), 5, TimeUnit.SECONDS); // через 5 секунд сработает будильник
        scheduler.shutdown();
    }
}

class Alarm implements Runnable {

    public void run() {
        System.out.println("Дзинь-дзинь");
    }
}

