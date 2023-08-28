package study.prosync.ch3;

public class PointHashBad {

    private final int x;
    private final int y;

    public PointHashBad(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointHashBad that = (PointHashBad) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}