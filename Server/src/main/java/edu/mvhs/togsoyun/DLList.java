package edu.mvhs.togsoyun;

public class DLList<E> {
    private Node<E> head, tail;
    private int size;

    public DLList() {
        head = new Node<E>(null);
        tail = new Node<E>(null);

        head.setPrev(null);
        head.setNext(tail);
        tail.setPrev(head);
        tail.setNext(null);

        size = 0;
    }

    public E get(int ind) {
        return getNode(ind).get();
    }

    private Node<E> getNode(int ind) {
        Node<E> curr;

        if (ind <= size / 2) {
            curr = head.next();
            for (int i = 0; i < ind && i < size; i ++) {
                curr = curr.next();
            }
        } else {
            curr = tail.prev();
            for (int i = size - 1; i > ind && i >= 0; i --) {
                curr = curr.prev();
            }
        }

        return curr;
    }

    public boolean add(E data) {
        Node<E> before = tail.prev();
        Node<E> n = new Node<E>(data);
        before.setNext(n);
        n.setPrev(before);
        n.setNext(tail);
        tail.setPrev(n);

        size ++;

        return true;
    }

    public void add(int ind, E data) {
        Node<E> after = getNode(ind);
        Node<E> before = after.prev();
        Node<E> n = new Node<E>(data);

        before.setNext(n);
        n.setPrev(before);
        n.setNext(after);
        after.setPrev(n);

        size ++;
    }

    public E remove(int ind) {
        Node<E> n = getNode(ind);
        Node<E> before = n.prev();
        Node<E> after = n.next();

        before.setNext(after);
        after.setPrev(before);

        size --;

        return n.get();
    }

    public boolean remove(E data) {
        Node<E> curr1 = head.next();
        Node<E> curr2 = tail.prev();
        Node<E> before = null;
        Node<E> after = null;

        for (int i = 0; i <= size / 2; i ++) {
            if (curr1.get().equals(data)){
                before = curr1.prev();
                after = curr1.next();

                break;
            } else curr1 = curr1.next();

            if (curr2.get().equals(data)) {
                before = curr2.prev();
                after = curr2.next();

                break;
            } else curr2 = curr2.prev();
        }

        if (before != null && after != null) {
            before.setNext(after);
            after.setPrev(before);

            size --;

            return true;
        } else return false;
    }

    public void clear() {
        head.setPrev(null);
        head.setNext(tail);
        tail.setPrev(head);
        tail.setNext(null);

        size = 0;
    }

    public E set (int ind, E data) {
        Node<E> n = getNode(ind);
        E replacedData = n.get();
        n.set(data);

        return replacedData;
    }

    public int size() {
        return size;
    }

    @Override
    public  String toString() {
        Node<E> curr = head.next();
        String s = "[";

        for (int i = 0; i < size - 1; i ++) {
            s += curr.get().toString() + ", ";
            curr = curr.next();
        }
        s += curr.get() + "]";

        return s;
    }

    public String playersToString() {
        Node<E> curr = head.next();
        String s = "";

        for (int i = 0; i < size - 1; i ++) {
            s += curr.get().toString() + " <br>";
            curr = curr.next();
        }
        s += curr.get();

        return s;
    }

}