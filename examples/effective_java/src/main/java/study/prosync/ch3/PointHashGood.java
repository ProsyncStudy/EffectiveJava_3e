package study.prosync.ch3;

public class PointHashGood {

    private final int x;
    private final int y;

    public PointHashGood(int x, int y) {
        this.x = x;
        this.y = y;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        PointHashGood that = (PointHashGood) o;
//
//        if (x != that.x) return false;
//        return y == that.y;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = x;
//        result = 31 * result + y;
//        return result;
//    }
}