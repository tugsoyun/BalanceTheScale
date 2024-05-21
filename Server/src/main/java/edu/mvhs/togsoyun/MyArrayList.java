package edu.mvhs.togsoyun;

public class MyArrayList<E> {
    protected Object[] list;
    private int capacity, size;

    public MyArrayList() {
        list = new Object[10];
        capacity = 10;
        size = 0;
    }

    public boolean add (E obj) {
        if (size >= capacity) {
            increaseCapacity();
        }
        list [size] = obj;
        size ++;

        return true;
    }

    public void add(int index, E obj) {
        if (index >= size) {
            return;
        } else if ( size >= capacity) {
            increaseCapacity();
        }
        for (int i = size; i > index; i --) {
            list [i] = list [i - 1];
        }
        list [index] = obj;

        size ++;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        return (E) list[index];
    }

    public E remove (int ind) {
        E removedObj = (E) list[ind];

        for (int i = ind; i < size - 1; i ++) {
            list [i] = list [i + 1];
        }
        list [size] = null;
        size --;

        return removedObj;
    }

    public E remove (E obj) {
        int ind = -1;
        for (int i = 0; i < size; i ++) {
            if (obj.equals((E) list[i])) {
                ind = i;

                break;
            }
        }

        if (ind != -1) {
            return remove (ind);
        } else return null;
    }

    public void set(int i, E obj) {
        if (i < size) {
            list[i] = obj;
        }
    }

    public String toString() {
        String string = "";
        for (int i = 0; i < size; i++) {
            string += list[i].toString() + "\n";
        }
        return string;
    }

    public int size() {
        return size;
    }

    public void increaseCapacity () {
        Object[] tempList = list;
        list = new Object [capacity * 2];
        capacity *= 2;
        for (int i = 0; i < tempList.length; i ++) {
            list [i] = tempList [i];
        }
    }

    public Object [] getArr () {
        return list;
    }

    public void clear () {
        for (int i =0; i < size; i ++) {
            list [i] = null;
        }
        size = 0;
    }
}

