package study.prosync.ch3;

public class Null아님Better {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Null아님Better)) { // 필드 검사를 위해 형변환을 해야 하니 instanceof가 낫다
            return false;
        }
        Null아님Better n = (Null아님Better) o;
        return n.equals(this);
    }
}
