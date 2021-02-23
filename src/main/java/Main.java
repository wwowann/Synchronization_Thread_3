import java.util.Random;

public class Main {
    public static void main(String[] args) {
        final int MAX = 5;
        Restaurant restaurant = new Restaurant();
        new Thread(null,restaurant::cookCooks, "Абдулрахим").start();
        message("Повар ", "Абдулрахим");
        new Thread(null, restaurant::getOrder, "Вася").start();
        message("Официант ", "Вася");
        new Thread(null, restaurant::getOrder, "Коля").start();
        message("Официант ", "Коля");
        new Thread(null, restaurant::getOrder, "Федя").start();
        message("Официант ", "Федя");
        for (int i = 0; i < MAX; i++) {
            new Thread(null, restaurant::customer, "Посетитель " + (i + 1)).start();
            System.out.println("Посетитель " + (i + 1) + " пришел в ресторан");
            Random random = new Random();
            try {
                Thread.sleep(random.nextInt(10 * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void message(String name, String name1) {
        System.out.println(name +
                name1 + " на работе");
    }
}
