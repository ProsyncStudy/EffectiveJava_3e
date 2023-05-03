package study.prosync.ch2;

import org.junit.Test;

import lombok.extern.log4j.Log4j;

@Log4j
public class Item8 {

    @Test
    public void finalizerTest1() {
        FinTest f1 = FinTest.builder().tmp(1234).tmp2("asdfasdfasdfasdf").build();
        FinTest f2 = FinTest.builder().tmp(431412).tmp2("iausdvbiab").build();

        try {
            f1.finalize();
            f2.finalize();
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
    }

    @Test
    public void cleanerTest1() {
        try (Room myRoom = new Room(7)) {
            log.info("GoodBye");
        }
    }

    @Test
    public void cleanerTest2() {
        Room myRoom = new Room(99);
        myRoom = null; // ?
        log.info("PeaceOut");
        // myRoom.close();
    }
}
