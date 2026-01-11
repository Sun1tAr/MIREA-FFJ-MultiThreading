package my.learn.my_souls;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Cloakroom {
    public static void main(String[] args) {
        Cloakroom cloakroom = new Cloakroom();
        List<People> peoples = new ArrayList<>();
        People p;
        for (int i = 1; i < 11; i++) {
             p = new People(
                     i,
                     People.getRandomSex(),
                     cloakroom,
                     People.getRandomDelayToIncome(),
                     cloakroom.condition)
             ;
             peoples.add(p);
        }
        peoples.forEach(People::start);

    }

    @Getter @Setter
    private List<People> currentPeoples = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();


    @SneakyThrows
    public void tryComeIn(People people) {
        lock.lock();
        try {

            while (!currentPeoples.isEmpty() && !currentPeoples.get(0).getSex().equals(people.getSex())) {
                people.getCondition().await();
            }

            currentPeoples.add(people);

        } finally {
            lock.unlock();
        }
    }

    @SneakyThrows
    public void tryComeOut(People people) {
        lock.lock();
        try {

            currentPeoples.remove(people);
            condition.signalAll();

        } finally {
            lock.unlock();
        }
    }
}



@AllArgsConstructor
@Getter
class People extends Thread {
    private int number;
    private Sex sex;
    private Cloakroom cloakroom;
    private long delayToIncome;
    private Condition  condition;

    public static Sex getRandomSex() {
        Random rand = new Random();
        if  (rand.nextBoolean()) {
            return Sex.MALE;
        } else {
            return Sex.FEMALE;
        }
    }

    public static long getRandomDelayToIncome() {
        Random rand = new Random();
        return rand.nextInt(1000, 3000);
    }

    public void comeIn() {
        System.err.println(String.valueOf(this.getNumber()) + "-" + this.getSex() + " try come in");
        cloakroom.tryComeIn(this);
        System.out.println(String.valueOf(this.getNumber()) + "-" + this.getSex() + " coming in to cloakroom");
    }

    public void goOut() {
        cloakroom.tryComeOut(this);
        System.out.println(String.valueOf(this.getNumber()) + "-" + this.getSex() + " going out from cloakroom");
    }

    @SneakyThrows
    @Override
    public void run() {
        Thread.sleep(delayToIncome);
        comeIn();
        Thread.sleep(7_000);
        goOut();
    }

    enum Sex {
        MALE, FEMALE
    }

}