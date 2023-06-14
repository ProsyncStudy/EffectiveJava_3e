package study.prosync.ch6;

import java.util.ArrayList;
import java.util.List;

// Program containing annotations with a parameter
public class Item39Sample3 {
    // Code containing an annotation with an array parameter
    @Item39ExceptionTest2({ IndexOutOfBoundsException.class, NullPointerException.class })
    public static void doublyBad() {
        List<String> list = new ArrayList<>();
        // The spec permits this method to throw either
        // IndexOutOfBoundsException or NullPointerException
        list.addAll(5, null);
    }
}
