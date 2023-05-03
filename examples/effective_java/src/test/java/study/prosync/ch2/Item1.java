package study.prosync.ch2;

import java.math.BigInteger;

import org.junit.Test;

import lombok.extern.log4j.Log4j;

@Log4j
public class Item1 {
  @Test
  public void t1() {
    Boolean b1 = new Boolean(false);
    Boolean b2 = Boolean.TRUE;

    BigInteger bi1 = BigInteger.probablePrime(0, null);
  }
}
