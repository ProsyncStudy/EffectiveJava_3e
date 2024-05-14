package study.prosync.ch3;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PointAutoValue {
    public static Builder builder() {
        //return new AutoValue_PointAutoValue.Builder();
        return null;
    }

    public abstract int x();

    public abstract int y();


    //@AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder x(int x);

        public abstract Builder y(int y);


        public abstract PointAutoValue build();
    }
}
