package study.prosync.ch6;

import org.junit.Test;

import lombok.extern.log4j.Log4j;

@Log4j
public class Item39 {

    @Test
    public void test() {
        try {
            Item39Sample hihi = new Item39Sample();
            Item39Sample.m4();
            log.info("hihi");
        } catch (Throwable t) {
            log.info(t);
        }
    }

    @Test
    public void test2() {
        try {
            Item39RunTests.func();
            log.info("Success");
        } catch (Throwable t) {
            log.info(t);
        }
    }

    @Test
    public void test3() {
        try {
            Item39RunTests.func2();
            log.info("Success");
        } catch (Throwable t) {
            log.info(t);
        }
    }

    @Test
    public void test4() {
        try {
            Item39RunTests.func3();
            log.info("Success");
        } catch (Throwable t) {
            log.info(t);
        }
    }
}
