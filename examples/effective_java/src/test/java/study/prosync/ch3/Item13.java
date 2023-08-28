package study.prosync.ch3;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Item13 {
    @Test(expected = CloneNotSupportedException.class)
    public void clone재정의x() throws Exception {
        Point p = new Point(1, 2);
        /**
         * java: clone() has protected access in java.lang.Object
         * 예외가 아닌 컴파일 에러?
         */
        // Point p2 = p.clone();
    }

    @Test
    public void clone재정의o() throws Exception {
        PointClone p = new PointClone(1, 2);
        PointClone p2 = p.clone();

        assertEquals(p, p2);
    }

    @Test
    public void 가변객체cloneWrong() throws Exception {
        // given
        StackCloneWrong s = new StackCloneWrong();
        for (int i = 0; i < 5; i++) {
            s.push(i);
        }

        // when
        StackCloneWrong cloned = s.clone();
        cloned.getElements()[0] = 9;

        // then
        assertEquals(cloned.getElements(), s.getElements());
        assertEquals(9, s.getElements()[0]);
    }

    @Test
    public void 가변객체clone() throws Exception {
        // given
        StackClone s = new StackClone();
        for (int i = 0; i < 5; i++) {
            s.push(i);
        }

        // when
        StackClone cloned = s.clone();
        cloned.getElements()[0] = 9;

        // then
        assertNotEquals(cloned.getElements(), s.getElements());
        assertEquals(0, s.getElements()[0]);
    }

    @Test
    public void clone대안책() throws Exception {
        // given
        int x = 1;
        int y = 2;
        PointCloneAlternative p1 = new PointCloneAlternative(x, y);
        PointCloneAlternative cp1 = PointCloneAlternative.valueOf(p1);
        PointCloneAlternative cp2 = new PointCloneAlternative(p1);

        // then
        assertEquals(p1, cp1);
        assertEquals(cp1, cp2);
    }
}