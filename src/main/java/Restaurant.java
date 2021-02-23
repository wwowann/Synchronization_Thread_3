import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Restaurant {
    final int MAX = 5;
    private int counterDish = 0;
    List<OrderWaiter> orderWaiters = new ArrayList<>(10);
    List<OrderCook> orderCooks = new ArrayList<>(10);
    List<ReadyDish> readyDish = new ArrayList<>(10);
    private final ReentrantLock lock = new ReentrantLock();
    private final ReentrantLock lock1 = new ReentrantLock();
    private final ReentrantLock lock2 = new ReentrantLock();
    private final Condition customerWaiter = lock2.newCondition();
    private final Condition waiter = lock.newCondition();
    private final Condition waiterCook = lock1.newCondition();

    public void getOrder() {
        while (counterDish < MAX) {
            lock.lock();
            if (orderWaiters.size() == 0 || readyDish.size() == 0) {
                System.out.println(Thread.currentThread().getName() +
                        " свободен и ждет от посетителя или повара сигнала ");
                try {
                    try {
                        waiter.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (orderWaiters.size() > 0) {
                        lock1.lock();
                        try {
                            orderCooks.add(new OrderCook());
                            System.out.println(Thread.currentThread().getName() + " принял заказ у посетителя " +
                                    "и отнес повару для приготовления блюда");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            orderWaiters.remove(0);
                            waiterCook.signal();
                        } finally {
                            lock1.unlock();
                        }
                    }
                    if (readyDish.size() > 0) {
                        lock2.lock();
                        try {
                            System.out.println(Thread.currentThread().getName() + " забрал блюдо у повара " +
                                    "и отнес посетителю");
                            System.out.println("counterDish " + (1 + counterDish++));
                            readyDish.remove(0);
                            customerWaiter.signal();
                        } finally {
                            lock2.unlock();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void customer() {
        System.out.println(Thread.currentThread().getName() +
                " зовет официанта и делает заказ");
        lock.lock();
        try {
            orderWaiters.add(new OrderWaiter());
            Thread.sleep(2000);
            waiter.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        System.out.println(Thread.currentThread().getName() +
                " ждет блюдо");
        lock2.lock();
        try {
            customerWaiter.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock2.unlock();
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
        while (count < 5) {
            lock1.lock();
            if (orderCooks.size() == 0) {
                try {
                    waiterCook.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock1.unlock();
                }
            }

            System.out.println(Thread.currentThread().getName() + " принял заказ и готовит блюдо");
            lock.lock();
            try {
                readyDish.add(new ReadyDish());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " приготовил блюдо");
                orderCooks.remove(0);
                count++;
                waiter.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
        System.out.println("Повар " + Thread.currentThread().getName() +
                " приготовил 5 блюд и закончил свою работу");
    }
}





