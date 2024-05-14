package study.prosync.ch7;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

import static java.util.Comparator.comparingInt;

public class Item42 {
    @Test
    public void 익명클래스로_함수객체_만들기() throws Exception {
        List<String> words = new ArrayList<>();
        Collections.sort(words, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(s1.length(), s2.length());
            }
        });
    }

    @Test
    public void 람다로_함수객체_만들기() throws Exception {
        List<String> words = new ArrayList<>();
        Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
    }

    @Test
    public void 정적비교자생성메소드로_함수객체_만들기() throws Exception {
        List<String> words = new ArrayList<>();
        Collections.sort(words, comparingInt(String::length));
    }

    @Test
    public void list의_sort_이용() throws Exception {
        List<String> words = new ArrayList<>();
        words.sort(comparingInt(String::length));
    }

    public enum OP_클래스body {
        PLUS("+") {
            public double apply(double x, double y) {
                return x + y;
            }
        },
        MINUS("-") {
            public double apply(double x, double y) {
                return x - y;
            }
        },
        TIMES("*") {
            public double apply(double x, double y) {
                return x * y;
            }
        },
        DIVIDE("/") {
            public double apply(double x, double y) {
                System.out.println(this.s1);
                return x / y;
            }
        };
        private final String symbol;
        public String s1;

        OP_클래스body(String s) {
            this.symbol = s;
        }

        public abstract double apply(double x, double y);
    }

    public enum OP_람다 {
        PLUS("+", (x, y) -> x + y),
        MINUS("-", (x, y) -> x - y),
        TIMES("*", (x, y) -> x * y),
        DIVIDE("/", (x, y) -> {
            //System.out.println(this.s1);
            return x / y;
        });

        private final String symbol;
        private final DoubleBinaryOperator op;
        public String s1;

        OP_람다(String s, DoubleBinaryOperator op) {
            this.symbol = s;
            this.op = op;
        }

        public double apply(double x, double y) {
            return op.applyAsDouble(x, y);
        }
    }
}
