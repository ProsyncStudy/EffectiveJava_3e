package study.prosync.ch3;

public class ColorPoint대칭성위배 extends Point {
    private final Color color;

    public ColorPoint대칭성위배(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint대칭성위배)) {
            return false;
        }
        return super.equals(o) && ((ColorPoint대칭성위배) o).color == color;
    }
}