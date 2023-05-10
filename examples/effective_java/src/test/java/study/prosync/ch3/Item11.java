package study.prosync.ch3;

import org.junit.Test;
import study.prosync.util.TimeCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Item11 {
    @Test
    public void hashCode재정의안함() throws Exception {
        // given
        Map<Point, Integer> hm = new HashMap<>();

        // when
        hm.put(new Point(1, 2), 1);

        // then
        Integer get = hm.get(new Point(1, 2));
        assertEquals(null, get); // get의 반환값은 null
    }

    @Test
    public void hashCode재정의함() throws Exception {
        // given
        Map<PointHashGood, Integer> hmg = new HashMap<>();
        Map<PointHashBad, Integer> hmb = new HashMap<>();

        // when
        hmg.put(new PointHashGood(1, 2), 1);
        hmb.put(new PointHashBad(1, 2), 1);

        // then
        Integer get1 = hmg.get(new PointHashGood(1, 2));
        assertEquals(Integer.valueOf(1), get1); // get == 1, 다른 버킷
        Integer get2 = hmb.get(new PointHashBad(1, 2));
        assertEquals(Integer.valueOf(1), get2); // get == 1, 같은 버킷
    }

    @Test
    public void hashCode에따른성능비교() throws Exception {
        // given
        Map<PointHashGood, Integer> hmg = new HashMap<>();
        ArrayList<PointHashGood> lg = new ArrayList<>();
        int loop = 10000;
        for (int i = 0; i < loop; i++) {
            lg.add(new PointHashGood(i, i));
        }

        // when
        for (int i = 0; i < lg.size(); i++) {
            hmg.put(lg.get(i), i);
        }

        // then
        TimeCheck.START();
        for (PointHashGood pointHashGood : lg) {
            hmg.get(pointHashGood); // get하는 성능 체크
        }
        TimeCheck.END();


        // given
        Map<PointHashBad, Integer> hmb = new HashMap<>();
        ArrayList<PointHashBad> lb = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            lb.add(new PointHashBad(i, i));
        }

        // when
        for (int i = 0; i < lb.size(); i++) {
            hmb.put(lb.get(i), i);
        }

        // then
        TimeCheck.START();
        for (PointHashBad pointHashBad : lb) {
            hmb.get(pointHashBad); // get하는 성능 체크
        }
        TimeCheck.END();
    }
}