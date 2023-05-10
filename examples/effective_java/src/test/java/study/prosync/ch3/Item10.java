package study.prosync.ch3;

import org.junit.Test;

import java.net.URL;
import java.sql.Timestamp;

import static org.junit.Assert.*;

public class Item10 {
    @Test
    public void 대칭성위배1() throws Exception {
        // given
        CaseInsensitiveString대칭성위배 cis = new CaseInsensitiveString대칭성위배("Test");
        String s = "test";

        // when

        // then
        assertNotEquals(s, cis);
        assertEquals(cis, s);
    }

    @Test
    public void 대칭성준수() throws Exception {
        // given
        CaseInsensitiveString대칭성준수 cis = new CaseInsensitiveString대칭성준수("Test");
        String s = "test";

        // when

        // then
        assertNotEquals(s, cis);
        assertNotEquals(cis, s);
    }

    @Test
    public void 대칭성위배2() throws Exception {
        // given
        Point p = new Point(1, 2);
        ColorPoint대칭성위배 cp = new ColorPoint대칭성위배(1, 2, Color.RED);

        // when

        // then
        assertEquals(p, cp);
        assertNotEquals(cp, p);
    }

    @Test
    public void 추이성위배() throws Exception {
        // given
        Point p = new Point(1, 2);
        ColorPoint추이성위배 cp = new ColorPoint추이성위배(1, 2, Color.RED);
        ColorPoint추이성위배 cp2 = new ColorPoint추이성위배(1, 2, Color.BLACK);

        // when

        // then
        assertEquals(cp, p);
        assertEquals(p, cp2);
        assertNotEquals(cp, cp2); // cp는 cp2와 같아야 함
    }

    @Test
    public void 추이성위배무한재귀() throws Exception {
        // given
        ColorPoint추이성위배 cp = new ColorPoint추이성위배(1, 2, Color.RED);
        ColorPoint추이성위배2 cp2 = new ColorPoint추이성위배2(1, 2, Color.BLACK);

        // when

        // then
        assertEquals(cp, cp2); // 무한재귀
    }

    @Test
    public void 추이성준수() throws Exception {
        Point p = new Point(1, 2);
        ColorPoint추이성준수 cp = new ColorPoint추이성준수(1, 2, Color.RED);
        ColorPoint추이성준수 cp2 = new ColorPoint추이성준수(1, 2, Color.BLACK);

        // given

        // when

        // then
        assertNotEquals(cp, p);
        assertNotEquals(p, cp2);
        assertNotEquals(cp2, cp);
    }

    @Test
    public void 추이성준수_타임스탬프() throws Exception {
        /**
         * Timestamp 클래스는 java.util.Date와 별도의 나노초(nanoseconds) 값을 갖는 합성 타입(composite type)입니다.
         * Date 컴포넌트에는 정수로 된 초 단위만 저장되며, 소수점 이하의 나노초 값은 별도의 변수로 저장됩니다.
         *
         * Timestamp.equals(Object) 메서드는 객체를 비교할 때 java.sql.Timestamp 클래스의 인스턴스가 아니면 항상 false를 반환합니다.
         * 이는 Date 컴포넌트에 속한 초 단위 값은 동일하지만, 나노초 값은 다를 수 있기 때문입니다.
         * 그 결과로, Timestamp.equals(Object) 메서드는 Date.equals(Object) 메서드와 대칭적이지 않습니다.
         *
         * 또한, hashCode() 메서드는 Date의 구현을 사용하므로, 나노초 값을 계산하지 않습니다.
         * 따라서 Timestamp 객체를 HashMap 또는 HashSet 등의 컬렉션에 사용할 때 주의해야 합니다.
         */
        Timestamp ts; // Date를 상속받고, nano 필드를 추가. NOTE 참조

    }

    @Test
    public void 일관성위배() throws Exception {
        // given
        URL url1 = new URL("http://example.com");
        URL url2 = new URL("http://example.com/");

        // when

        // then
        assertNotEquals(url1, url2);
        assertNotEquals(url1.hashCode(), url2.hashCode());
    }

    @Test
    public void autoValue() throws Exception {
        PointAutoValue autoValue = PointAutoValue.builder()
                .x(1)
                .y(2)
                .build();
        assertEquals(1, autoValue.x());
        assertEquals(2, autoValue.y());

        // You probably don't need to write assertions like these; just illustrating.
        PointAutoValue x1_y2 = PointAutoValue.builder()
                .x(1)
                .y(2)
                .build();
        assertEquals(x1_y2, autoValue);

        PointAutoValue x2_y2 = PointAutoValue.builder()
                .x(2)
                .y(2)
                .build();
        assertNotEquals(x2_y2, autoValue);

        PointAutoValue x1_y4 = PointAutoValue.builder()
                .x(1)
                .y(4)
                .build();
        assertNotEquals(x1_y4, autoValue);
        assertEquals("PointAutoValue{x=1, y=2}", autoValue.toString());
    }

}