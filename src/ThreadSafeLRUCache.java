import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class ThreadSafeLRUCache<K, V> {

    /**
     * Node class & Linked List
     * doubly linked list tracks cache order
     * Each node contains the key and value pair and pointers to aj nodes
     */

    private class Node {
        K key;
        V value;
        Node prev;
        Node next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node> cache;
    private Node head; // Most recently used
    private Node tail; // Least recently used
    private final ReentrantReadWriteLock rwLock;
    private final Lock readLock;
    private final Lock writeLock;

    public ThreadSafeLRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be above 0 ");
        }
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);

        head = new Node(null, null);
        tail = new Node(null, null);
        head.next = tail;
        tail.prev = head;

        this.rwLock = new ReentrantReadWriteLock();
        this.readLock = rwLock.readLock();
        this.writeLock = rwLock.writeLock();
    }

    /**
     * Person 2
     */

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void movetoHead(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private Node removeTail() {
        Node removed = tail.prev;
        if (removed == head) {
            return null; // cache empty
        }
        removeNode(removed);
        return removed;
    }
    /*
     * Person 2: Cache Operations (non thread safe)
     * - Implement the internal cache methods that will use my linked list
     * operations:
     * - getInternal(K key) - Retrieves a value and moves the node to the front
     * - putInternal(K key, V value) - Adds/updates a value and handles eviction
     * - removeInternal(K key) - Removes a specific key
     * - Make sure to use the linked list methods I created (addToHead, moveToHead,
     * etc.)
     * - Don't worry about thread safety yet - Person 3 will handle that
     * 
     * ### Person 3 (Thread Safety):
     * - Add concurrency controls using ReadWriteLock or similar
     * - Create the public API methods that wrap Person 2's internal methods:
     * - get(K key) - Thread-safe version that uses readLock
     * - put(K key, V value) - Thread-safe version that uses writeLock
     * - Other operations like containsKey(), size(), clear(), etc.
     * - Implement multi-threaded tests to verify thread safety
     * 
     */











}
