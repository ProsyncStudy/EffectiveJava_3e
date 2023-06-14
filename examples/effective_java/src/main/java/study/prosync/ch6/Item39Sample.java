package study.prosync.ch6;

public class Item39Sample {
    @Item39Test public static void m1() {}
    public static void m2() {}
    @Item39Test public static void m3() {}
    @Item39Test public static void m4() throws Exception {
        throw new Exception("failed");
    }
    // @Item39Test public void m5() {}
    public void m6() {}
}
