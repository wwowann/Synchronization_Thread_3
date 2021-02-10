import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Restaurant {
    int MAX = 5;
    List<OrderWaiter> orderWaiters = new ArrayList<>(10);
    List<OrderCook> orderCooks = new ArrayList<>(10);
    List<ReadyDish> readyDish = new ArrayList<>(10);

    final Object customerWaiter = new Object();
    final Object waiterCook = new Object();
    final Object waiterCustomer = new Object();

    public void getOrder() {
        int count = 0;
        while (count < MAX) {
            synchronized (customerWaiter) {
                if (orderWaiters.size() == 0) {
                    try {
                        System.out.println(Thread.currentThread().getName() +
                                " свободен и ждет от посетителя сигала");
                        customerWaiter.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized (waiterCook) {
                orderCooks.add(new OrderCook());
                System.out.println(Thread.currentThread().getName() +
                        " взял заказ и отнес к повару");
                if (readyDish.size() == 0) {
                    try {
                        waiterCook.notify();// сигнал повару готовить
                        System.out.println(Thread.currentThread().getName() +
                                " ждет блюдо от повара");
                        waiterCook.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName() + " отнес блюдо посетителю");
                synchronized (waiterCustomer) {
                    readyDish.remove(0);
                    waiterCustomer.notify();
                }

            }
            count++;
        }
    }

    public void customer() {
        synchronized (customerWaiter) {
            orderWaiters.add(new OrderWaiter());
            try {
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() +
                        " зовет официанта и делает заказ");
                customerWaiter.notify();
                orderWaiters.remove(0);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() +
                " ждет блюдо");
        synchronized (waiterCustomer) {
            try {
                waiterCustomer.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(Thread.currentThread().getName() + " приступил к еде");
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(8 * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " поел и вышел из ресторана");
    }

    public void cookCooks() {
        int count = 0;
        while (count < MAX) {
            synchronized (waiterCook) {
                if (orderCooks.size() == 0) {
                    try {
                        waiterCook.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName() + " готовит блюдо");
                readyDish.add(new ReadyDish());
                try {
                    Thread.sleep(3000);
                    System.out.println("блюдо готово");
                    waiterCook.notify();
                    System.out.println(Thread.currentThread().getName() + " ждет заказ");
                    waiterCook.wait();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            count++;
        }
    }
}

