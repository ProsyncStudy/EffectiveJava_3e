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
    TimeCheck.START();
    for (int i = 0; i < 10000000; i++) {
      String s = new String("bikini");
      s += "asdfasdfasdf";
      // log.info(s);
    }
    TimeCheck.END();
  }

  @Test
  public void StringTest2() {
    TimeCheck.START();
    for (int i = 0; i < 10000000; i++) {
      String s = "bikini";
      s += "asdfasdfasdf";
      // log.info(s.hashCode());
    }
    TimeCheck.END();
  }

  @Test
  public void RomanNumeral1Test() {
    TimeCheck.START();
    for (int i = 0; i < 1000000; i++)
      RomalNumeral1.isRomanNumeral("asdfasdfasdfasdf");
    TimeCheck.END();
  }

  @Test
  public void RomanNumeral2Test() {
    TimeCheck.START();
    for (int i = 0; i < 1000000; i++)
      RomalNumeral2.isRomanNumeral("asdfasdfasdfasdf");
    TimeCheck.END();
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
    TimeCheck.START();
    Long sum = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++)
      sum += i;
    TimeCheck.END();
    log.info(sum);

    TimeCheck.START();
    long sum2 = 0L;
    for (long i = 0; i <= Integer.MAX_VALUE; i++)
      sum2 += i;
    TimeCheck.END();
    log.info(sum2);
  }
}

