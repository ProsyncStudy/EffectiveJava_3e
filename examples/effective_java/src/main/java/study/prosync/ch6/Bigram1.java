package study.prosync.ch6;

public class Bigram1 {
    private final char first;
    private final char second;

    public Bigram1(char first, char second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Bigram1 b) {
        return b.first == first && b.second == second;
    }

    // @Override
    // public boolean equals(Object o) {
    //     if (!(o instanceof Bigram1))
    //         return false;
    //     Bigram1 b = (Bigram1) o;
    //     return b.first == first && b.second == second;
    // }

    public int hashCode() {
        return 31 * first + second;
    }
}