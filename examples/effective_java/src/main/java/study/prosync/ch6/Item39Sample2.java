package study.prosync.ch6;

// Program containing annotations with a parameter
public class Item39Sample2 {
    @Item39ExceptionTest(ArithmeticException.class)
    public static void m1() { // Test should pass
        int i = 0;
        i = i / i;
    }

    @Item39ExceptionTest(ArithmeticException.class)
    public static void m2() { // Should fail (wrong exception)
        int[] a = new int[0];
        int i = a[1];
    }

    @Item39ExceptionTest(ArithmeticException.class)
    public static void m3() {
    } // Should fail (no exception)


}