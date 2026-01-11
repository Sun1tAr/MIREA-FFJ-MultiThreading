package my.learn.my_souls;

import lombok.SneakyThrows;

public class JavaBeans {

    public static void main(String[] args) {
        CoffeeMachine coffeeMachine = new CoffeeMachine();
        Thread t1 = new Barista(
                5_000,
                1_000,
                "Иван",
                coffeeMachine
        );
        Thread t2 = new Barista(
                5_000,
                1_000,
                "Сергей",
                coffeeMachine
        );
        t1.start();
        t2.start();
    }

}

class Barista extends Thread {
    private long cookTime;
    private long waitTime;
    private CoffeeMachine coffeeMachine;



    public Barista(long cookTime, long waitTime, String name, CoffeeMachine coffeeMachine) {
        super(name);
        this.cookTime = cookTime;
        this.waitTime = waitTime;
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public void run() {
        cookCoffee();
        waitCoffeePause();
    }

    @SneakyThrows
    public void cookCoffee() {
        System.out.println(Thread.currentThread().getName() + " подошел к кофемашине");
        coffeeMachine.cookCoffee(cookTime);
    }

    @SneakyThrows
    public void waitCoffeePause() {
        Thread.sleep(waitTime);
    }

}

class CoffeeMachine {

    @SneakyThrows
    public synchronized void cookCoffee(long cookTime) {
        System.out.println(Thread.currentThread().getName() + " начинает готовить кофе");
        Thread.sleep(cookTime);
        System.out.println("Кофе готово! - " +  Thread.currentThread().getName());
    }

}
