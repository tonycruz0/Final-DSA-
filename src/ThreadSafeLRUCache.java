import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class ThreadSafeLRUCache<K, V> {

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
    private Node head;
    private Node tail;
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

    private V getInternal(K key) {
        if (cache.containsKey(key)) {
            Node gotNode = cache.get(key);
            movetoHead(gotNode);
            return gotNode.value;
        } else {
            System.out.println("-1");
            return null;
        }
    }

    private void putInternal(K key, V value) {
        if (cache.containsKey(key)) {
            Node existingNode = cache.get(key);
            existingNode.value = value;
            movetoHead(existingNode);
            cache.put(key, existingNode);
        }
        else{
            if(cache.size() == capacity){
                cache.remove(tail.prev.key);
                removeTail();
            }
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            movetoHead(newNode);
        }
    }

    private V removeInternal(K key) {
        Node removeNode = cache.get(key);
        removeNode(cache.get(key));
        remove(key);
        return removeNode.value;
    }

    public V get(K key) {
        readLock.lock();
        try {
            return getInternal(key);
        } finally {
            readLock.unlock();
        }
    }

    public void put(K key, V value) {
        writeLock.lock();
        try {
            putInternal(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean containsKey(K key) {
        readLock.lock();
        try {
            return cache.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    public int size() {
        readLock.lock();
        try {
            return cache.size();
        } finally {
            readLock.unlock();
        }
    }

    public void clear() {
        writeLock.lock();
        try {
            cache.clear();
            head.next = tail;
            tail.prev = head;
        } finally {
            writeLock.unlock();
        }
    }

    public V remove(K key) {
        writeLock.lock();
        try {
            if (cache.containsKey(key)) {
                return removeInternal(key);
            }
            return null;
        } finally {
            writeLock.unlock();
        }
    }

    private void removeNode(Node node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
    }

    private void movetoHead(Node node) {
        removeNode(node);
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private Node removeTail() {
        Node removed = tail.prev;
        if (removed == head) {
            return null;
        }
        removeNode(removed);
        return removed;
    }
}
