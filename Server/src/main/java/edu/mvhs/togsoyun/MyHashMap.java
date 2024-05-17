package edu.mvhs.togsoyun;

public class MyHashMap<K, V> {
    private Object[] arr;
    private MyHashSet<K> keySet;
    private int size;

    public MyHashMap() {
        arr = new Object[1000];
        keySet = new MyHashSet<>();

        size = 0;
    }

    public MyHashMap(int n) {
        arr = new Object[n];
        keySet = new MyHashSet<>(n);

        size = 0;
    }

    @SuppressWarnings("unchecked")
    public V get (Object obj) {
        if (keySet().contains(obj)) {
            return (V) arr [obj.hashCode()];
        } else return null;
    }

    @SuppressWarnings("unchecked")
    public V put (K key, V value) {
        V prev = (V) arr [key.hashCode()];

        arr [key.hashCode()] = value;
        keySet.add(key);
        size ++;

        return prev;
    }

    @SuppressWarnings("unchecked")
    public V remove (Object obj) {
        if (keySet().contains(obj)) {
            V prev = (V) arr [obj.hashCode()];

            arr [obj.hashCode()] = null;
            keySet.remove(obj);
            size --;

            return prev;
        } else return null;
    }

    public void clear() {
        for (int i = 0; i < size; i ++) {
            arr [keySet.get(i).hashCode()] = null;
        }
        keySet.clear();

        size = 0;
    }

    public int size() {
        return size;
    }

    public MyHashSet<K> keySet() {
        return keySet;
    }

    @Override
    public String toString() {
        String s = "";

        for (int i = 0; i < size; i ++) {
            s += keySet.get(i) + " " + arr[keySet.get(i).hashCode()] + "\n";
        }

        return s;
    }
}