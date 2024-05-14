package study.prosync.ch7;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Item43 {
    @Test
    public void 람다_이용() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        String key = null;
        map.merge(key, 1, (count, incr) -> count + incr);
    }

    @Test
    public void 메소드참조_이용() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        String key = null;
        map.merge(key, 1, Integer::sum);
    }

    @Test
    public void 메소드참조보다_람다가_간결하다() throws Exception {
        // GoshThisClassNameIsHumongous 라는 클래스가 있고
        // 그 안에 action이라는 메소드가 있을때

        // 1) 메소드 참조
        //service.execute(GoshThisClassNameIsHumongous::action);
        // 2) 람다
        //service.execute(() -> action());
    }
}
