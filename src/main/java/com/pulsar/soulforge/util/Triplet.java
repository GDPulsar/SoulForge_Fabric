package com.pulsar.soulforge.util;

public class Triplet<T, U, V> {
    private T first;
    private U second;
    private V third;

    public Triplet(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() { return first; }
    public U getSecond() { return second; }
    public V getThird() { return third; }

    public void setFirst(T value) { this.first = value; }
    public void setSecond(U value) { this.second = value; }
    public void setThird(V value) { this.third = value; }
}
