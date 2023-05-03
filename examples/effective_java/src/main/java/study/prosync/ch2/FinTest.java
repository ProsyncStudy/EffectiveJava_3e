package study.prosync.ch2;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinTest {
    private Integer tmp;
    private String tmp2;

    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        tmp = null;
        tmp2 = null;
        super.finalize();
    }
}
