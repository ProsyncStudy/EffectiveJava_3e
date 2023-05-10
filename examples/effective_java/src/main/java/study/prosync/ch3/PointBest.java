package study.prosync.ch3;

public class PointBest {

    private final int x;
    private final int y;

    public PointBest(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PointBest)) {
            return false;
        }
        PointBest p = (PointBest) o;
        return p.x == x && p.y == y;
    }
}