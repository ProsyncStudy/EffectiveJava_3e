package study.prosync.ch3;

public class PointWrongEquals {

    private final int x;
    private final int y;

    public PointWrongEquals(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // @Override
    // public boolean equals(PointWrongEquals o) { // Object가 아닌 PointWrongEquals
    //     if (!(o instanceof PointWrongEquals)) {
    //         return false;
    //     }
    //     PointWrongEquals p = (PointWrongEquals) o;
    //     return p.x == x && p.y == y;
    // }
}