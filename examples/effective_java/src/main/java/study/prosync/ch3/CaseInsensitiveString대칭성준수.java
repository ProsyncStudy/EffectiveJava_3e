package study.prosync.ch3;

import java.util.Objects;

public class CaseInsensitiveString대칭성준수 {

    private final String s;

    public CaseInsensitiveString대칭성준수(String s) {
        this.s = Objects.requireNonNull(s);
    }

    // 대칭성 위배
    @Override
    public boolean equals(Object o) {
        return o instanceof CaseInsensitiveString대칭성준수 && ((CaseInsensitiveString대칭성준수) o).s.equalsIgnoreCase(s);
    }
}