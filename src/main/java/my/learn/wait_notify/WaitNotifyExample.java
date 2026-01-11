package my.learn.wait_notify;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

public class WaitNotifyExample {
    public static void main(String[] args) {
        Message message = new Message(); //объект, через который будут общаться потоки

        Thread threadCook = new Thread(new Cook(message), "Повар");
        Thread threadWaiter = new Thread(new Waiter(message), "Официант");
        threadCook.start();
        threadWaiter.start();
    }
}

@Getter @Setter
class Message {
    private String msg;
}


class Cook implements Runnable {
    public Cook(Message msg) {
        this.msg = msg;
    }

    private Message msg;
    private AtomicInteger counter =  new AtomicInteger(1);

    @Override
    public void run() {
        while (true) {
            System.out.println("Повар начал готовить заказ " + counter.get());
            sleep(2000);
            synchronized (msg) {
                msg.setMsg("Блюда готовы, можно забирать заказ " + counter.getAndIncrement());
                msg.notify();
            }
        }
    }

    private void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

@AllArgsConstructor
class Waiter implements Runnable {
    private Message msg;

    @Override
    public void run() {
        while (true) {
            synchronized (msg) {
                try {
                    System.out.println("Официант ждет, пока повар начнет готовить");
                    msg.wait();
                    System.out.println("Сообщение от повара получено: " + msg.getMsg());
                    System.out.println("___________________________");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
