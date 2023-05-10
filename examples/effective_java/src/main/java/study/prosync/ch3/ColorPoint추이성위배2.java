package study.prosync.ch3;

public class ColorPoint추이성위배2 extends Point {
    private final Color color;

    public ColorPoint추이성위배2(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        if (!(o instanceof ColorPoint추이성위배2)) {
            return o.equals(this);
        }
        return super.equals(o) && ((ColorPoint추이성위배2) o).color == color;
    }
}