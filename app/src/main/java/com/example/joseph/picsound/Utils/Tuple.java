package com.example.joseph.picsound.Utils;

public class Tuple<T, U> {
    T first;
    U second;

    public Tuple(T firstElement, U secondElement) {
        first = firstElement;
        second = secondElement;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(U second) {
        this.second = second;
    }
}
