package study.prosync.ch3;

public class Null아님NotGood {
    @Override
    public boolean equals(Object o) {
        if (o == null) { // 명시적 null 체크보다는
            return false;
        }
        Null아님NotGood n = (Null아님NotGood) o;
        return n.equals(this);
    }
}
