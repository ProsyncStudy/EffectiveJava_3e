package study.prosync.ch3;

import java.util.Objects;

public class CaseInsensitiveString대칭성위배 {

    private final String s;

    public CaseInsensitiveString대칭성위배(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 대칭성 위배
    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString대칭성위배) {
            return s.equalsIgnoreCase(((CaseInsensitiveString대칭성위배) o).s);
        }
        if (o instanceof String) {
            return s.equalsIgnoreCase((String) o);
        }
        return false;
    }
}