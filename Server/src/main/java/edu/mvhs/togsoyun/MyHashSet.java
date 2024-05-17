package edu.mvhs.togsoyun;

public class MyHashSet <E> {
    private Object[] arr;
    private DLList<E> list;
    private int size;

    public MyHashSet() {
        arr = new Object[1000];
        list = new DLList<>();

        size = 0;
    }

    public MyHashSet(int n) {
        arr = new Object[n];
        list = new DLList<>();

        size = 0;
    }

    @SuppressWarnings("unchecked")
    public E get(int ind) {
        return (E) arr [list.get(ind).hashCode()];
    }

    @SuppressWarnings("unchecked")
    public E get (E data) {
        return (E) arr [data.hashCode()];
    }
    public boolean add (E data) {
        if (this.contains(data)) {
            return false;
        } else {
            arr [data.hashCode()] = data;
            list.add(data);

            size ++;

            return true;
        }
    }

    public boolean remove(Object obj) {
        arr [obj.hashCode()] = null;
        list.remove((E) obj);

        size --;

        return true;
    }

    public boolean contains (Object obj) {
        if (arr[obj.hashCode()] != null) return true;
        else return false;
    }

    public void clear() {
        for (int i = 0; i < size; i ++) {
            arr [list.get(i).hashCode()] = null;
        }
        list.clear();

        size = 0;
    }

    public boolean isEmpty() {
        if (size == 0) return true;
        else return false;
    }

    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return list.toString();
    }
}