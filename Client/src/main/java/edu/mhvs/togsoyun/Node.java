package edu.mhvs.togsoyun;

public class Node<E> {
    private E data;
    private Node<E> prev, next;

    public Node(E data) {
        this.data = data;

        this.prev = null;
        this.next = null;
    }

    public E get() {
        return data;
    }

    public Node<E> prev() {
        return prev;
    }

    public Node<E> next() {
        return next;
    }

    public void set(E data) {
        this.data = data;
    }

    public void setPrev(Node<E> n) {
        prev = n;
    }

    public void setNext(Node<E> n) {
        next = n;
    }
}
