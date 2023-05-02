package study.prosync.ch2;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import lombok.extern.log4j.Log4j;
import study.prosync.util.TimeCheck;

@Log4j
public class Item6 {
  @Test
  public void StringTest1() {
    long a = TimeCheck.getTime();
    for (int i = 0; i < 10000000; i++) {
      String s = new String("bikini");
      s += "asdfasdfasdf";
      // log.info(s);
    }
    long b = TimeCheck.getTime();
    log.info(b-a);
  }

  @Test
  public void StringTest2() {
    long a = TimeCheck.getTime();
    for (int i = 0; i < 10000000; i++) {
      String s = "bikini";
      s += "asdfasdfasdf";
      // log.info(s.hashCode());
    }
    long b = TimeCheck.getTime();
    log.info(b-a);
  }

  @Test
  public void RomanNumeral1Test() {
    long a = TimeCheck.getTime();
    for (int i = 0; i < 1000000; i++)
      RomalNumeral1.isRomanNumeral("asdfasdfasdfasdf");
      long b = TimeCheck.getTime();
    log.info(b-a);
  }

  @Test
  public void RomanNumeral2Test() {
    long a = TimeCheck.getTime();
    for (int i = 0; i < 1000000; i++)
      RomalNumeral2.isRomanNumeral("asdfasdfasdfasdf");
    long b = TimeCheck.getTime();
    log.info(b-a);
  }

  @Test
  public void keySetTest() {
    Map mp = new HashMap<Integer,String>();
    for (int i = 0; i < 10; i++) {
       mp.put(i, "" + i);
    }

    mp.keySet().remove(0);
    log.info(mp.keySet());
    mp.keySet().remove(1);
    log.info(mp.keySet());
    mp.keySet().remove(2);
    log.info(mp.keySet());
  }

  @Test
  public void autoboxingTest() {
    long a = TimeCheck.getTime();
    Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++)
      sum += i;
    long b = TimeCheck.getTime();
    log.info(b-a);
    log.info(sum);

    a = TimeCheck.getTime();
    long sum2 = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++)
      sum2 += i;
    b = TimeCheck.getTime();
    log.info(b-a);
    log.info(sum2);

  }
}

