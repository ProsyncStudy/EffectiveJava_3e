package study.prosync.ch2;

import java.io.Serializable;

public class Elvis {
  public static final Elvis INSTANCE = new Elvis();
  private Elvis() {
  }
}
