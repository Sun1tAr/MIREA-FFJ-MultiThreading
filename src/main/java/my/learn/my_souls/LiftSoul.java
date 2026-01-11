package my.learn.my_souls;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LiftSoul { //todo

    public static void main(String[] args) {
        List<Passenger> passengers = new ArrayList<>();

        Passenger p;
        Random rand = new Random();

        int maxFloor = 20;
        int minFloor = 1;
        Phaser phaser = new Phaser(1);

         for (int i = 1; i <= 10; i++) {
             p = new Passenger(
                     rand.nextInt(minFloor, maxFloor),
                     rand.nextInt(minFloor, maxFloor),
                     rand.nextInt(40, 80),
                     phaser,
                     0
             );
             passengers.add(p);
         }

        Thread lift = new Thread(new Lift(
                passengers,
                maxFloor,
                minFloor,
                minFloor,
                Lift.Movement.UP,
                400,
                phaser,
                new ReentrantLock()
        ));

         lift.start();
    }

}

@Builder
class Lift implements Runnable {

    private List<Passenger> passengers;

    private int maxFloor;
    private int minFloor;

    private int currentFloor;
    private Movement lastMove;

    private int maxWeight;

    private Phaser phaser;
    private Lock lock;



    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Лифт приехал на этаж: " + currentFloor);


        Thread.sleep(5000);
        if (passengers.isEmpty()) {
            // лифт закончил работу
            phaser.arriveAndDeregister();
        }
        for (Passenger passenger : passengers) {
            if (passenger.getEndFloor() == currentFloor) {
                passengers.remove(passenger);
            }

            if (passenger.getStartFloor() == currentFloor) {
                phaser.register();
                new Thread(passenger).start();
            }
        }


        phaser.arriveAndAwaitAdvance();
        System.out.println("Пассажиры сели в лифт на этаже: " + currentFloor);
        if (currentFloor == minFloor) {
            lastMove = Movement.UP;
        } else if (currentFloor == maxFloor) {
            lastMove = Movement.DOWN;
        }
        goNext();
        System.out.println("Лифт поехал на этаж: " + currentFloor);
        Thread.sleep(3000);
    }

    private void goNext() {
        lock.lock();
        if (lastMove == Movement.UP) {
            currentFloor++;
        }  else if (lastMove == Movement.DOWN) {
            currentFloor--;
        }
        lock.unlock();
    }

    enum Movement {
        UP, DOWN
    }

}

@AllArgsConstructor
@Getter
class Passenger implements Runnable {

    private int startFloor;
    private int endFloor;
    private int weight;

    private Phaser phaser;
    private volatile int currentFloor;

    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Пассажир " + startFloor + "->" + endFloor + " сел в лифт");

        while (currentFloor != endFloor) {
            phaser.arriveAndAwaitAdvance();
        }
        Thread.sleep(1000);
        System.out.println("Пассажир " + startFloor + "->" + endFloor + " покинул лифт");
        phaser.arriveAndDeregister();
    }


}

