package study.prosync.ch2;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CloseTest implements AutoCloseable {
    private Integer aaa;
    @Override public void close() throws Exception {
        throw new Exception("close THROW");
    }
}
