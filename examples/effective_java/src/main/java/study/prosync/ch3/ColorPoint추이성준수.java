package study.prosync.ch3;

import java.util.Objects;

public class ColorPoint추이성준수 {
    private final Color color; // composition
    private final Point point; // composition

    public ColorPoint추이성준수(int x, int y, Color color) {
        this.point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }

    public Point asPoint() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint추이성준수)) {
            return false;
        }
        ColorPoint추이성준수 cp = (ColorPoint추이성준수) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }
}