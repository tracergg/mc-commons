package gg.tracer.commons.util;

/**
 * @author Bradley Steele
 */
public class Pair<A, B> {

    public A first;
    public B second;

    public Pair() {
    }

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair{first=" + first + ", second=" + second + "}";
    }
}