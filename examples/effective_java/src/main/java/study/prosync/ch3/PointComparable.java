package study.prosync.ch3;

public class PointComparable implements Comparable<PointComparable> {

    private final int x;
    private final int y;

    public PointComparable(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "PointComparable{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PointComparable)) {
            return false;
        }
        PointComparable p = (PointComparable) o;
        return p.x == x && p.y == y;
    }

    @Override
    public int compareTo(PointComparable o) {
        /* 직접적인 >, < 사용은 지양. 대신 박싱클래스의 정적 compare 메소드 */
        int res = Integer.compare(x, o.x); // 우선순위 1번째 필드
        if (res == 0) {
            /* 직접적인 >, < 사용은 지양. 대신 박싱클래스의 정적 compare 메소드 */
            res = Integer.compare(y, o.y); // 우선순위 2번째 필드
        }
        return res;
    }
}