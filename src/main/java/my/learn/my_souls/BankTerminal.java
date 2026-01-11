package my.learn.my_souls;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class BankTerminal {

    private Map<String, Integer> lastTickets = new ConcurrentHashMap<>();
    private Queue<Ticket> activeTickets = new PriorityBlockingQueue<>(1000, new Comparator<Ticket>() {
        @Override
        public int compare(Ticket t1, Ticket t2) {
            int i = t1.prefix.compareTo(t2.prefix);
            if (i == 0) {
                return Integer.compare(t1.number, t2.number);
            } else {
                return i;
            }
        }
    });


    public static void main(String[] args) {
        Window w;
        Visitor v;
        BankTerminal bt = new BankTerminal();
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            w = new Window(bt);
            threads.add(new Thread(w, "Окно №" + i));
        }

        for (int i = 1; i < 20; i++) {
            v = new Visitor(bt);
            threads.add(new Thread(v, "Клиент №" + i));
        }

        threads.forEach(Thread::start);

    }


    public Ticket getTicketForTarget(VisitTarget visitTarget) {
        if (lastTickets.containsKey(visitTarget.symbol)) {
            lastTickets.put(visitTarget.symbol, lastTickets.get(visitTarget.symbol) + 1);
        } else {
            lastTickets.put(visitTarget.symbol, 1);

        }
        Ticket ticket = new Ticket(visitTarget.name(), lastTickets.get(visitTarget.symbol));
        System.out.println("Выдан новый талон:" + ticket);
        activeTickets.add(ticket);
        return ticket;
    }

    public Ticket getNextTicketByPriority() {
        if  (activeTickets.isEmpty()) {
            throw new RuntimeException("Tickets hasnt");
        }
        return activeTickets.poll();
    }

    enum VisitTarget {
        CARD_GETTING("A"),
        CREDIT("B"),
        CONTRIBUTION("C");

        private String symbol;

        VisitTarget(String Symbol) {
            this.symbol = Symbol;
        }
    }

    @AllArgsConstructor
    class Ticket {
        private String prefix;
        private int number;

        @Override
        public String toString() {
            return "Талон " + prefix + "-" + number;
        }
    }


}

@RequiredArgsConstructor
class Visitor implements Runnable {

    private final BankTerminal bankTerminal;
    private BankTerminal.Ticket ticket;

    @SneakyThrows
    @Override
    public void run() {
        Random random = new Random();
        Thread.sleep(random.nextLong(5_000, 10_000));
        ticket = bankTerminal.getTicketForTarget(getRandomVisitTarget());
    }

    private static BankTerminal.VisitTarget getRandomVisitTarget() {
        Random random = new Random();
        if  (random.nextBoolean()) {
            return BankTerminal.VisitTarget.CARD_GETTING;
        } else if (random.nextBoolean()) {
            return BankTerminal.VisitTarget.CREDIT;
        } else {
            return BankTerminal.VisitTarget.CONTRIBUTION;
        }
    }

}

@RequiredArgsConstructor
class Window implements Runnable {

    private final BankTerminal bankTerminal;

    @Override
    public void run() {
        while (true) {
            serveNextClient();
        }
    }

    @SneakyThrows
    private void serveNextClient() {
        BankTerminal.Ticket ticket = null;
        try {
            ticket = bankTerminal.getNextTicketByPriority();
        } catch (Exception e) {
            System.out.println("Клиентов пока нет!");
            Thread.sleep(3_000);
            return;
        }
        System.out.println("Клиент с талоном " + ticket + " приглашается к окну " + Thread.currentThread().getName());
        Random random = new Random();
        Thread.sleep(random.nextLong(3_000, 15_000));
        System.out.println("Клиент с талоном " + ticket + " обслужен в окне " + Thread.currentThread().getName());
    }
}
