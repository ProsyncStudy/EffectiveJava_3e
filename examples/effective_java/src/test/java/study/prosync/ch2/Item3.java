package study.prosync.ch2;

import java.util.function.Supplier;

import org.junit.Test;

import lombok.extern.log4j.Log4j;

@Log4j
public class Item3 {

  public static Elvis2 tmp() {
    return Elvis2.getInstance();
  }

  @Test
  public void ElvisTest() {
    Elvis ev = Elvis.INSTANCE;
    Elvis2 ev2 = Elvis2.getInstance();
    Supplier<Elvis2> sp = Item3::tmp;

    log.info(ev);
    log.info(ev2);
    log.info(sp.get());
  }
}
