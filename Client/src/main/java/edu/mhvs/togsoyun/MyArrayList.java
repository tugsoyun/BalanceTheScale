package edu.mhvs.togsoyun;

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
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        if (size >= capacity) {
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
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        return (E) list[index];
    }

    public E remove (int ind) {
        if (ind < 0 || ind >= size) {
            throw new IndexOutOfBoundsException("Index: " + ind + ", Size: " + size);
        }

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
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + size);
        }

        list[i] = obj;

    }

    public boolean contains(E obj) {
        for (int i = 0; i < size; i ++) {
            if (obj.equals((E) list[i])) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        MyArrayList<E> arr = (MyArrayList<E>) o;
        if (arr.size() == this.size) {
            for(int i = 0; i < size; i ++) {
                if (!this.get(i).equals(arr.get(i))) return false;
            }
        } else return false;

        return true;
    }

    public String toString() {
        String string = "";
        for (int i = 0; i < size; i++) {
            string += list[i].toString() + " ";
        }
        return string;
    }

    public int size() {
        return size;
    }

    public void increaseCapacity () {
        Object[] tempList = new Object[capacity * 2];
        for (int i = 0; i < size; i ++) {
            tempList [i] = list[i];
        }
        list = tempList;
        capacity *= 2;
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
