package study.prosync.ch2;

import org.junit.Test;

import lombok.extern.log4j.Log4j;

@Log4j
public class Item9 {

    @Test
    public void test1() {
        try (CloseTest ct = CloseTest.builder().aaa(134).build();) {
            log.info("HIHI~");
        } catch (Exception e) {
            for (StackTraceElement se : e.getStackTrace()) {
                log.error(se.toString());
            }
        }
    }

    @Test
    public void test2() {
        try (CloseTest ct = CloseTest.builder().aaa(134).build();) {
            log.info("HIHI~");
        } catch (Exception e) {
            Throwable[] th = e.getSuppressed();
            log.info(th.length);
            for (Throwable se : th) {
                log.error(se.toString());
            }
        }
    }
}
