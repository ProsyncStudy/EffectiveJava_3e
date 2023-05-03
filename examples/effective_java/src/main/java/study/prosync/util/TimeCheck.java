package study.prosync.util;

import lombok.extern.log4j.Log4j;

@Log4j
public class TimeCheck {
  private static long startT, endT;

  public static long getTime() {
    return System.currentTimeMillis();
  }

  public static void START() {
    log.info(Thread.currentThread().getStackTrace()[2].toString());
    startT = System.currentTimeMillis();
  }

  public static void END() {
    endT = System.currentTimeMillis();
    log.info("Time Costs: " + (endT - startT) + "(ms)");
  }

}
