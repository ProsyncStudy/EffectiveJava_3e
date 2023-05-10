package study.prosync.ch3;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Item14 {
    @Test
    public void 규약4번위배() throws Exception {
        // given
        BigDecimal a = new BigDecimal("1.0");
        BigDecimal b = new BigDecimal("1.00");
        Set<BigDecimal> hs = new HashSet<>();
        Set<BigDecimal> ts = new TreeSet<>();

        // when
        hs.add(a);
        hs.add(b);
        ts.add(a);
        ts.add(b);
        int compare = a.compareTo(b);

        // then
        assertNotEquals(a, b); // 1.0과 1.00은 다름
        assertEquals(0, compare); // compare에서는 동일함
        assertEquals(2, hs.size()); // 2, HashSet에는 내부적으로 equals를
        assertEquals(1, ts.size()); // 1, TreeSet에서는 내부적으로 compareTo를
    }

    @Test
    public void comparable() throws Exception {
        // given
        ArrayList<PointComparable> l = new ArrayList<>();
        for (int x = 2; x > 0; x--) {
            for (int y = 2; y > 0; y--) {
                PointComparable p = new PointComparable(x, y);
                System.out.println(p);
                l.add(p);
            }
        }

        // when
        Collections.sort(l);
        System.out.println("--- after sort ---");

        // then
        for (PointComparable p : l) {
            System.out.println(p);
        }
    }

    @Test
    public void comparator비교자생성메소드() throws Exception {
        // given
        Comparator<Point> COMPARATOR = // 비교자 정의
                Comparator.comparingInt((Point p) -> p.x)
                        .thenComparingInt(p -> p.y);

        ArrayList<Point> l = new ArrayList<>();
        for (int x = 2; x > 0; x--) {
            for (int y = 2; y > 0; y--) {
                Point p = new Point(x, y);
                System.out.println(p);
                l.add(p);
            }
        }

        // when
        l.sort(COMPARATOR); // 정렬
        System.out.println("--- after sort ---");

        // then
        for (Point p : l) {
            System.out.println(p);
        }
    }

    @Test
    public void comparator정적compare메소드() throws Exception {
        // given
        /* Comparable의 compareTo 재정의와 동일 */
        Comparator<Point> COMPARATOR = new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                int res = Integer.compare(o1.x, o2.x); // 우선순위 1번째 필드
                if (res == 0) {
                    res = Integer.compare(o1.y, o2.y); // 우선순위 2번째 필드
                }
                return res;
            }
        };

        ArrayList<Point> l = new ArrayList<>();
        for (int x = 2; x > 0; x--) {
            for (int y = 2; y > 0; y--) {
                Point p = new Point(x, y);
                System.out.println(p);
                l.add(p);
            }
        }

        // when
        l.sort(COMPARATOR); // 정렬
        System.out.println("--- after sort ---");

        // then
        for (Point p : l) {
            System.out.println(p);
        }
    }
}
