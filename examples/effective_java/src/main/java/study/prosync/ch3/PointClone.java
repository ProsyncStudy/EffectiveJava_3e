package study.prosync.ch3;

public class PointClone implements Cloneable {

    private final int x;
    private final int y;

    public PointClone(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PointClone)) {
            return false;
        }
        PointClone p = (PointClone) o;
        return p.x == x && p.y == y;
    }

    @Override
    public PointClone clone() throws CloneNotSupportedException {
        PointClone clone = (PointClone) super.clone();
        return clone;
    }
}