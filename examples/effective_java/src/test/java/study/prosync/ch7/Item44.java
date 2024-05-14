package study.prosync.ch7;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class Item44 {
    @Test
    public void 개수제한map_removeEldestEntry_재정의() throws Exception {
        LinkedHashMap<Integer, Integer> map = new ExtendedLinkedHashMap1<>();

        for (int i = 0; i < 10; i++) {
            map.put(i, i);

            System.out.println("-----------size: " + map.size());
            map.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }
    }

    @Test
    public void 개수제한map_함수형인터페이스_정의() throws Exception {
        // eldest는 LinkedHashMap에서 head
        // 1) size 5로 유지
        LinkedHashMap<Integer, Integer> map1 = new ExtendedLinkedHashMap2<>((m, eldest) -> m.size() > 5);

        System.out.println("-----map1");
        for (int i = 0; i < 10; i++) {
            map1.put(i, i);

            System.out.println("-----------size: " + map1.size());
            map1.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }

        // 2) size 1로 유지
        LinkedHashMap<Integer, Integer> map2 = new ExtendedLinkedHashMap2<>((m, eldest) -> m.size() > 1);

        System.out.println("-----map2");
        for (int i = 0; i < 10; i++) {
            map2.put(i, i);

            System.out.println("-----------size: " + map2.size());
            map2.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }
    }

    @Test
    public void 개수제한map_표준함수형인터페이스_정의() throws Exception {
        // eldest는 LinkedHashMap에서 head
        // 1) size 5로 유지
        LinkedHashMap<Integer, Integer> map1 = new ExtendedLinkedHashMap3<>((m, eldest) -> m.size() > 5);

        System.out.println("-----map1");
        for (int i = 0; i < 10; i++) {
            map1.put(i, i);

            System.out.println("-----------size: " + map1.size());
            map1.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }

        // 2) size 1로 유지
        LinkedHashMap<Integer, Integer> map2 = new ExtendedLinkedHashMap3<>((m, eldest) -> m.size() > 1);

        System.out.println("-----map2");
        for (int i = 0; i < 10; i++) {
            map2.put(i, i);

            System.out.println("-----------size: " + map2.size());
            map2.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }
    }


    @FunctionalInterface
    interface EldestEntryRemovalFunction<K, V> {
        boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
    }

    public class ExtendedLinkedHashMap1<K, V> extends LinkedHashMap<K, V> {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 5;
        }
    }

    public class ExtendedLinkedHashMap2<K, V> extends LinkedHashMap<K, V> {
        private final EldestEntryRemovalFunction<K, V> predicate;

        public ExtendedLinkedHashMap2(EldestEntryRemovalFunction<K, V> predicate) {
            this.predicate = predicate;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return predicate.remove(this, eldest);
        }
    }

    public class ExtendedLinkedHashMap3<K, V> extends LinkedHashMap<K, V> {
        private final BiPredicate<Map<K, V>, Map.Entry<K, V>> predicate;

        public ExtendedLinkedHashMap3(BiPredicate<Map<K, V>, Map.Entry<K, V>> predicate) {
            this.predicate = predicate;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return predicate.test(this, eldest);
        }
    }
}
