package study.prosync.ch3;

public class PointCloneAlternative {

    private final int x;
    private final int y;

    public PointCloneAlternative(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 복사 생성자
    public PointCloneAlternative(PointCloneAlternative other) {
        this.x = other.x;
        this.y = other.y;
    }

    // 정적 복사 팩토리
    public static PointCloneAlternative valueOf(PointCloneAlternative other) {
        return new PointCloneAlternative(other.x, other.y);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PointCloneAlternative)) {
            return false;
        }
        PointCloneAlternative p = (PointCloneAlternative) o;
        return p.x == x && p.y == y;
    }
}