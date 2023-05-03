package study.prosync.ch2;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import org.junit.Test;

import lombok.extern.log4j.Log4j;
import study.prosync.util.TimeCheck;

@Log4j
public class Item7 {
  @Test
  public void stacktest1() {
    Stack s = new Stack();
    TimeCheck.START();
    for (int i = 0; i < 20480; i++) {
      s.push(new String("" + i));
    }
    for (int i = 0; i < 20480; i++) {
      try {
        s.pop();
      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }

    TimeCheck.END();
  }

  @Test
  public void weakTest() {
    WeakHashMap<Integer, String> wh = new WeakHashMap<>();
    Integer ii = 1;
    String s = "12341234";
    wh.put(ii, s);

    ii = null;
    System.gc();
    log.info(wh.get(1).toString());

    WeakReference<String> ws = new WeakReference<String>(s);
    s = null;
    System.gc();
    log.info(ws.get());
  }
}
