import java.util.LinkedList;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private final static int DEFAULT_INITIAL_CAPACITY = 4;
    private final static int MAXIMUM_CAPICTY  = 1 << 30;
    private int capacity;
    private final static float DEFAULT_MAX_LOAD_FACTOR = 0.75f;
    private float loadFactorThreshold;
    private int size = 0;

    LinkedList<MyMap.Entry<K, V>>[] table; // array w/ each cell being linked list

    public MyHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_MAX_LOAD_FACTOR);
    }

    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_MAX_LOAD_FACTOR);
    }

    public MyHashMap(int initialCapacity, float loadFactorThreshold) {
        if (initialCapacity > MAXIMUM_CAPICTY) {
            this.capacity = MAXIMUM_CAPICTY;
        } else {
            this.capacity = trimToPowerOf2(initialCapacity);
        }

        this.loadFactorThreshold = loadFactorThreshold;
        table = new LinkedList[capacity];
    }

    @Override
    public void clear() {
        size = 0;
        removeEntries();
    }

    @Override
    public boolean containsKey(K key) {
        if (get(key) != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(V value) {
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null) {
                LinkedList<Entry<K, V>> bucket = table[i];
                for (Entry<K, V> entry: bucket)
                    if (entry.getValue().equals(value))
                        return true;
            }
        }
        return false;
    }

    // return a set of entries in the map
    @Override
    public java.util.Set<MyMap.Entry<K, V>> entrySet() {
        java.util.Set<MyMap.Entry<K, V>> set = 
            new java.util.HashSet<>();

        for (int i = 0; i < capacity; i++) {
            if (table[i] != null) {
                LinkedList<Entry<K, V>> bucket = table[i];
                for (Entry<K, V> entry: bucket)
                    set.add(entry);
            }
        }
        return set;
    }

    // return value that matches specified key
    @Override
    public V get(K key) {
        int bucketIndex = hash(key.hashCode());
        if (table[bucketIndex] != null) {
            LinkedList<Entry<K, V>> bucket = table[bucketIndex];
            for (Entry<K, V> entry: bucket)
                if (entry.getKey().equals(key))
                    return entry.getValue();
        }
        return null;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // return set containing keys in this map
    @Override
    public java.util.Set<K> keySet() {
        java.util.Set<K> set = new java.util.HashSet<>();
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null) {
                LinkedList<Entry<K, V>> bucket = table[i];
                for (Entry<K, V> entry: bucket)
                    set.add(entry.getKey());
            }
        }
        return set;
    }

    // add an entry (key, value) into the map
    @Override
    public V put(K key, V value) {
        if (get(key) != null) {
            int bucketIndex = hash(key.hashCode());
            LinkedList<Entry<K, V>> bucket = table[bucketIndex];
            for (Entry<K, V> entry: bucket)
                if (entry.getKey().equals(key)) {
                    // replace old value w/ new value then return old value
                    V oldValue = entry.getValue();
                    entry.value = value;
                    return oldValue;
                }
        }

        // check load factor
        if (size >= capacity * loadFactorThreshold) {
            if (capacity == MAXIMUM_CAPICTY)
                throw new RuntimeException("Exceeding maximum capacity");
            rehash();
        }

        int bucketIndex = hash(key.hashCode());

        // create a linklist for bucket if not already created
        if (table[bucketIndex] == null) {
            table[bucketIndex] = new LinkedList<Entry<K, V>>();
        }

        // add new entry (key, value) to hashTable[index]
        table[bucketIndex].add(new MyMap.Entry<K, V>(key, value));

        size++;

        return value;
    }

    // remove entry for specified key
    @Override
    public void remove(K key) {
        int bucketIndex = hash(key.hashCode());

        // remove entry that matches key from bucket
        if (table[bucketIndex] != null) {
            LinkedList<Entry<K, V>> bucket = table[bucketIndex];
            for (Entry<K, V> entry: bucket)
                if (entry.getKey().equals(key)) {
                    bucket.remove(entry);
                    size--;
                    break;
                }
        }
    }

    @Override
    public int size() {
        return size;
    }

    // return set w/ values in this map
    @Override
    public java.util.Set<V> values() {
        java.util.Set<V> set = new java.util.HashSet<>();

        for (int i = 0; i < capacity; i++) {
            if (table[i] != null) {
                LinkedList<Entry<K, V>> bucket = table[i];
                for (Entry<K, V> entry: bucket)
                    set.add(entry.getValue());
            }
        }
        return set;
    }

    private int hash(int hashCode) {
        return supplementalHash(hashCode) & (capacity - 1);
    }

    private static int supplementalHash(int h) {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    private int trimToPowerOf2(int initialCapacity) {
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        return capacity;
    }

    private void removeEntries() {
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null) {
                table[i].clear();
            }
        }
    }

    private void rehash() {
        java.util.Set<Entry<K, V>> set = entrySet(); // get entries
        capacity <<= 1; // x2 capacity
        table = new LinkedList[capacity]; // new hash table
        size = 0; // reset size

        for (Entry<K, V> entry: set) {
            put(entry.getKey(), entry.getValue()); // store to new table
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && table[i].size() > 0)
                for (Entry<K, V> entry: table[i])
                    builder.append(entry);
        }
        builder.append("]");
        return builder.toString();
    }
}