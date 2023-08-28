package study.prosync.ch6;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import lombok.extern.log4j.Log4j;

@Log4j
public class Item40 {
    @Test
    public void test1() {
        Set<Bigram1> s = new HashSet<>();
        for (int i = 0; i < 10; i++)
            for (char ch = 'a'; ch <= 'z'; ch++)
                s.add(new Bigram1(ch, ch));
        log.info(s.size());
    }
}
