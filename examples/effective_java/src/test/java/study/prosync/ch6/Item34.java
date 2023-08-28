package study.prosync.ch6;

import java.util.EnumSet;

import org.junit.Test;

import lombok.extern.log4j.Log4j;

@Log4j
public class Item34 {
    public enum Apple {
        FUJI, PIPPIN, GRANNY_SMITH
    };

    public enum Orange {
        NAVEL, TEMPLE, BLOOD
    };

    @Test
    public void t1() throws Exception {
        EnumSet<Apple> appleSet = EnumSet.allOf(Apple.class);
        EnumSet<Orange> orangeSet = EnumSet.allOf(Orange.class);

        for (Apple apple : appleSet) {
            log.info(apple.toString() + ": " + apple.ordinal());
        }

        for (Orange orang : orangeSet) {
            log.info(orang.toString() + ": " + orang.ordinal());
        }
    }

    @Test
    public void WeightTable() {
        double earthWeight = 80;
        double mass = earthWeight / Planet.EARTH.surfaceGravity();
        for (Planet p : Planet.values())
            log.info(String.format("Weight on %s is %f%n",
                    p, p.surfaceWeight(mass)));
        log.info(Planet.EARTH);
        // log.info(Planet.HIHI);
    }

    @Test
    public void printOperation1() {
        double x = 1000.0;
        double y = 2000.0;
        for (Operation1 op : Operation1.values())
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
    }

    @Test
    public void printOperation2() {
        double x = 1000.0;
        double y = 2000.0;
        for (Operation2 op : Operation2.values())
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
    }

    @Test
    public void printOperation3() {
        double x = 1000.0;
        double y = 2000.0;
        for (Operation3 op : Operation3.values())
            System.out.printf("%f %s %f = %f%n",
                    x, op, y, op.apply(x, y));
    }

    @Test
    public void fromStringOperation() {
        for (Operation3 op : Operation3.values()) {
            log.info(Operation3.fromString(op.toString()));
            log.info(Operation3.fromString(op.toString()).get());
        }
    }
}
