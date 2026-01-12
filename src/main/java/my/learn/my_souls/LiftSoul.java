package my.learn.my_souls;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class LiftSoul {

    public static void main(String[] args) {

        final int COUNT_OF_PASSENGERS = 15;
        final int MAX_LIFT_WEIGHT = 400;

        List<Floor> floors = new ArrayList<>();
        Floor floor;
        for (int i = 1; i <= 5; i++) {
            floor = new Floor(i);
            floors.add(floor);
        }

        Thread t;
        Random rand = new Random();
        AtomicInteger counter = new AtomicInteger();


        for (int i = 1; i <= COUNT_OF_PASSENGERS; i++) {
            Floor startFloor = floors.get(rand.nextInt(0, floors.size()-1));
            Floor endFloor = floors.get(rand.nextInt(0, floors.size()-1));

            while (endFloor.equals(startFloor)) {
                endFloor = floors.get(rand.nextInt(0, floors.size()-1));
            }

            int weight = rand.nextInt((int) (0.1*MAX_LIFT_WEIGHT), (int) (0.25*MAX_LIFT_WEIGHT));

            t = new Thread(new Passenger(
                    i,
                    startFloor,
                    endFloor,
                    counter,
                    weight
            ));

            t.setPriority(10);
            t.start();
        }

        t = new Thread(new Lift(
                floors,
                new ArrayList<>(),
                floors.get(0).getValue(),
                Lift.Direction.UP,
                counter,
                COUNT_OF_PASSENGERS,
                MAX_LIFT_WEIGHT
                ));
        t.start();
    }

}

@RequiredArgsConstructor
class Floor {
    @Getter
    private final int value;

    @Getter @Setter
    private volatile Lift lift;
}

@AllArgsConstructor
class Lift implements Runnable {

    enum Direction {
        UP, DOWN
    }

    private final List<Floor> floors;
    private final List<Passenger> passengers;
    @Getter
    private volatile int currentFloor;
    private Direction direction;

    private AtomicInteger counter;
    private Integer maxCounts;

    private int maxWeight;



    @Override
    public void run() {

        while (maxCounts > counter.get()) {
            goNext();
        }

    }

    @SneakyThrows
    private void goNext() {
        Thread.sleep(1000);
        System.out.println("Лифт приехал на этаж " + currentFloor);
        Floor floor = floors.get(currentFloor - 1);

        if (currentFloor == floors.get(floors.size()-1).getValue()) {
            direction = Direction.DOWN;
        } else if (currentFloor == floors.get(0).getValue()) {
            direction = Direction.UP;
        }

        synchronized (floor) {
            floor.setLift(this);
            floor.notifyAll();
        }

        Thread.sleep(5000);

        synchronized (floor) {
            switch (direction) {
                case UP -> currentFloor++;
                case DOWN -> currentFloor--;
            }
            floor.setLift(null);
            System.out.println("Лифт отправляется на этаж " + currentFloor);
        }

    }

    @SneakyThrows
    public synchronized boolean tryComeIn(Passenger passenger) {
        Thread.sleep(1500);

        int currentWeight = 0;
        for (Passenger p: passengers) {
            currentWeight += p.getWeight();
        }

        if (currentWeight + passenger.getWeight() <= maxWeight) {
            passengers.add(passenger);
            System.out.println("Пассажир " + passenger + " cел в лифт [" + (currentWeight + passenger.getWeight()) + "]");
            return true;
        } else {
            System.err.println("Пассажир " + passenger + " слишком толст для этого лифта: "+
                    (currentWeight + passenger.getWeight()) + "/" + maxWeight);
            return false;
        }

    }

    @SneakyThrows
    public synchronized void goOut(Passenger passenger) {
        Thread.sleep(1500);
        passengers.remove(passenger);
        System.out.println("Пассажир " + passenger + " покинул лифт");
    }
}

@AllArgsConstructor
class Passenger implements Runnable {

    @Getter
    private final int id;
    private final Floor startFloor;
    private final Floor endFloor;

    private AtomicInteger counter;
    @Getter
    private final int weight;

    @SneakyThrows
    @Override
    public void run() {
        System.out.println("Пассажир " + this + " ждет лифта");

        synchronized (startFloor) {
            while (startFloor.getLift() == null || !startFloor.getLift().tryComeIn(this)) {
                startFloor.wait();
            }
        }

        synchronized (endFloor) {
            while (endFloor.getLift() == null) {
                endFloor.wait();
            }
            endFloor.getLift().goOut(this);
        }

        System.err.println("Пассажиров доехало: " + counter.incrementAndGet());


    }

    @Override
    public String toString() {
        return id + "[" + startFloor.getValue() +
                "->" + endFloor.getValue() + "]";
    }
}

























