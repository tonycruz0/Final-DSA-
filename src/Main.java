public class Main {
    public static void main(String[] args) {
        ThreadSafeLRUCache<Integer, String> cache = new ThreadSafeLRUCache<>(3);

        System.out.println("===== Single-Threaded Test =====");
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");

        System.out.println("Get 1: " + cache.get(1)); // Should print "One"
        cache.put(4, "Four"); // Evicts key 2 (LRU)

        System.out.println("Contains 2 (should be false): " + cache.containsKey(2));
        System.out.println("Contains 3 (should be true): " + cache.containsKey(3));
        System.out.println("Get 3: " + cache.get(3)); // Should print "Three"
        System.out.println("Cache Size: " + cache.size()); // Should be 3

        System.out.println("\n===== Multi-Threaded Test =====");

        Runnable writerTask = () -> {
            for (int i = 5; i < 8; i++) {
                cache.put(i, "Value " + i);
                System.out.println(Thread.currentThread().getName() + " put: " + i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Runnable readerTask = () -> {
            for (int i = 5; i < 8; i++) {
                String value = cache.get(i);
                System.out.println(Thread.currentThread().getName() + " get: " + i + " => " + value);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread writer1 = new Thread(writerTask, "Writer-1");
        Thread writer2 = new Thread(writerTask, "Writer-2");
        Thread reader1 = new Thread(readerTask, "Reader-1");
        Thread reader2 = new Thread(readerTask, "Reader-2");

        writer1.start();
        writer2.start();
        reader1.start();
        reader2.start();

        try {
            writer1.join();
            writer2.join();
            reader1.join();
            reader2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n===== Final Cache State =====");
        for (int i = 5; i < 8; i++) {
            System.out.println("Key: " + i + ", Value: " + cache.get(i));
        }
    }
}
