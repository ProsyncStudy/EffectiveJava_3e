- [Item 34. 상수 대신 열거 타입을 사용하라](#item-34-상수-대신-열거-타입을-사용하라)
- [Item 35. ordinal 메서드 대신 인스턴스 필드를 사용하라.](#item-35-ordinal-메서드-대신-인스턴스-필드를-사용하라)
- [Item 36. 비트 필드 대신 EnumSet을 사용하라.](#item-36-비트-필드-대신-enumset을-사용하라)
- [37. ordinal 인덱싱 대신 EnumMap을 사용해라.](#37-ordinal-인덱싱-대신-enummap을-사용해라)
- [Item 38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라.](#item-38-확장할-수-있는-열거-타입이-필요하면-인터페이스를-사용하라)
- [Item 39. 명명 패턴보다 애너테이션을 사용하라](#item-39-명명-패턴보다-애너테이션을-사용하라)
- [Item 40. 애너테이션을 일관되게 사용해라.](#item-40-애너테이션을-일관되게-사용해라)
- [Item 41. 정의하련느 것이 타입이라면 마커 인터페이스를 사용하라.](#item-41-정의하련느-것이-타입이라면-마커-인터페이스를-사용하라)
  - [마커 인터페이스 관련](#마커-인터페이스-관련)


# Item 34. 상수 대신 열거 타입을 사용하라
> 이번 장에선 enum type의 장점 및 사용 방법에 대해서 알려준다.
> 대체로 public static final 과 같은 방식의 열거 패턴을 사용하지 말라는게 주 내용이다.

```java
// The int enum pattern - severely deficient!
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;
public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;

int i = (APPLE_FUJI - ORANGE_TEMPLE) / APPLE_PIPPIN;
```

위와 같은 형태를 정수 열거 패턴 이라고 한다. 간단하지만 단점이 있다.
1. 타입 안전을 보장할 방법이 없다.
2. 표현이 좋지 않다.
3. 잘못 넣어도 컴파일러가 경고를 줄 수 없다.
4. 자바가 namespace를 지원하지 않아서 이름이 굉장히 복잡해진다.

즉, 정수 열거 패턴으로 만든 프로그램은 굉장히 취약하고 버그를 만들기 쉽다. 실제 프로싱크 매니저나 파일싱크 agent 코드쪽에도 이런 식으로 코드가 되어있는데 이로 인해 이름을 잘못쓰거나 비슷한 이름이 혼용되어 버그이지만 컴파일 단계에서 잡아주지 않아 망가진 경험이 더러 있다.

또한 **문자열로 출력하기가 굉장히 까다롭다.** 정수 열거 타입을 받아서 스트링을 출력하거나 (프로싱크 매니저나 파일싱크에서 tbmsg에 대한 string을 로그로 출력하는걸 상상해보자.) 따로 문자열 열거 패턴을 써야 한다.

아래는 간단한 enum type의 예시이다.
```java
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
public enum Orange { NAVEL, TEMPLE, BLOOD }
```

복잡하지 않고 간단하다. 또한 기본으로 제공되는 메소드들이 많아서 굉장히 편리하다. (예제 참조)

우선 enum 자체가 생성자를 제공하지 않는 final이며, 단 하나만 존재하는게 보장되는 static이다. 싱글턴이 원소가 하나뿐인 enum type이라면, enum은 싱글턴을 일반화한 놈이라고 생각하면 좋다.

열거 타입은 컴파일타임의 타입 안전성을 보장해주는데, Apple 열거 타입을 받는 메소드가 있다면 해당 메소드는 Apple 3가지 값 중 하나임이 분명하다. C랑 다르게 정수형이면 다 넘어갈 수 있는게 아니라, 타입 자체를 보기 때문에 Apple 타입이 아니면 넘길 수가 없어서 컴파일 타임에 감지가 가능하다.

또한 열거타입은 클래스나 마찬가지기 때문에 메서드나 필드 추가가 자유롭다. 위 예를 연장시키면, 과일의 색을 알려주거나 이미지 파일을 반환하는 메소드를 짜기가 유용하다.

다음은 아래 Planet 예시를 봐보자.
```java
// Enum type with data and behavior
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6),
    EARTH(5.975e+24, 6.378e6),
    MARS(6.419e+23, 3.393e6),
    JUPITER(1.899e+27, 7.149e7),
    SATURN(5.685e+26, 6.027e7),
    URANUS(8.683e+25, 2.556e7),
    NEPTUNE(1.024e+26, 2.477e7);

    private final double mass; // In kilograms
    private final double radius; // In meters
    private final double surfaceGravity; // In m / s^2
    // Universal gravitational constant in m^3 / kg s^2
    private static final double G = 6.67300E-11;

    // Constructor
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        surfaceGravity = G * mass / (radius * radius);
    }

    public double mass() {
        return mass;
    }

    public double radius() {
        return radius;
    }

    public double surfaceGravity() {
        return surfaceGravity;
    }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity; // F = ma
    }
}
```

앞서 언급한 바와 같이 열거타입은 모두 불변이기 때문에 final을 붙여야 한다.

만약 위 Planet 타입을 활용하여, 내 몸무게가 각 행성별로 몇kg인지를 아는 방법은 다음 코드로 간단하게 구현이 가능하다.
```java
public void WeightTable() {
        double earthWeight = 80;
        double mass = earthWeight / Planet.EARTH.surfaceGravity();
        for (Planet p : Planet.values())
            System.out.printf("Weight on %s is %f%n",
                    p, p.surfaceWeight(mass));
    }
```

위 코드를 조금 뜯어보면,

```double mass = earthWeight / Planet.EARTH.surfaceGravity();```
는 단순히 단위중력 (1)에서의 내 몸무게로 돌려놓는 것 뿐이고 봐야 할 부분은 ```Planet.values()``` 이다.

열거 타입은 자신 안에 정의된 상수타입을 배열로 저장하고 있다. 해당 배열은 values 메서드를 통해 반환받을 수 있으며, 열거타입 내에서 toString 등을
 기본적으로 제공하기 때문에 매우 간편하게 로그를 찍는게 가능하다.
 toString이 마음에 안들면 재정의하면 그만이고..

열거 타입에서 상수를 하나 제거하면 어떻게 될까? 책에선 명왕성을 예로 들고 있다.
 우선 결론은, 코드 상엔 아무런 문제가 없다.
 만약 실제로 문제가 되는 부분이 있다 한들, 컴파일러가 바로 잡아주고, 유용한 컴파일 에러를 배출해주기 때문에 문제가 없다.


다음 예는 상수마다 동작이 달라져야 하는 열거 타입의 예시이다. (Operation)
```java
public enum Operation1 {
    PLUS, MINUS, TIMES, DIVIDE;

    // Do the arithmetic operation represented by this constant
    public double apply(double x, double y) {
        switch (this) {
            case PLUS:
                return x + y;
            case MINUS:
                return x - y;
            case TIMES:
                return x * y;
            case DIVIDE:
                return x / y;
        }
        throw new AssertionError("Unknown op: " + this);
    }
}
```

동작은 하는데 그닥 예쁘진 않다고 한다. throw에는 실제 도달할 일은 없지만 기술적으론 도달할 수 있어서 생략하면 안된다.

더 나쁜 점은 확장성 문제가 있는데, Enum이 추가될 때 마다 apply 메소드 내의 switch문에도 하나 더 추가해야 한다. (tbmsg를 생각해보자...)

이를 극복할 수 있는 방법은 **상수별 메소드 구현** 이 있다. 거두절미하고 코드를 보자.
```java
// Enum type with constant-specific method implementations
public enum Operation2 {
    PLUS {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    public abstract double apply(double x, double y);
}
```

이렇게 해놓으면 일단 깜빡할 일이 없다. 또한 abstract 이기 때문에 구현을 안하면 컴파일러가 에러로 알려준다.
 자바에선 이렇듯 열거 타입에 상수별로 메소드를 구현할 수 있는 기능을 제공하기 때문에 열거 타입이 우수하다고 볼 수 있다.

여기에 toString까지 재정의하면 아주 깔끔하다. 출력은 예제코드를 참고하자. (Item34, Operation3 테스트)
```java
package study.prosync.ch6;

// Enum type with constant-specific class bodies and data
public enum Operation3 {
    PLUS("+") {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation3(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}
```

fromString 예시도 확인해보자. 출력은 예제 참조.
```java
// Implementing a fromString method on an enum type
private static final Map<String, Operation3> stringToEnum =
  Stream.of(values()).collect(Collectors.toMap(Object::toString, e -> e));

// Returns Operation3 for string, if any
public static Optional<Operation3> fromString(String symbol) {
  return Optional.ofNullable(stringToEnum.get(symbol));
}
```

fromString이 Optional을 리턴받는다는 점도 유의하자.
> [Optional 관련](http://www.tcpschool.com/java/java_stream_optional)
>
> 위 링크에서 여러 가지가 있지만, Null 체크를 하기 좋다.
>
> get 메소드를 통해 실제 값을 가져올 수 있으며, null 일 때 다른 값을 가져오게 하는 등의 처리가 가능하다.


열거 타입의 단점으론, 열거 타입의 상수끼리 코드를 공유하기 어렵다는 단점이 있다. 예를 들면 어떤게 있고, 어떻게 극복하는지를 보자.
```java
// Enum that switches on its value to share code - questionable
public enum PayrollDay1 {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
    SATURDAY, SUNDAY;

    private static final int MINS_PER_SHIFT = 8 * 60;

    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;
        int overtimePay;
        switch (this) {
            case SATURDAY:
            case SUNDAY: // Weekend
                overtimePay = basePay / 2;
                break;
            default: // Weekday
                overtimePay = minutesWorked <= MINS_PER_SHIFT ? 0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
        }
        return basePay + overtimePay;
    }
}
```

위 코드는 주말에 일하면 추가 수당을 주고, 평일엔 기본 수당을 받는 코드이다.
 이 코드의 한계점은 주말이 아니라 공휴일 등의 예외에 대해선 처리가 안되어있다는 점이 있다.
 요일 처럼 쓰고있는 상수 객체에 모든 공휴일을 추가하기엔 비효율적이다.
 만약 추가한다고 하더라도, switch 문에 모두 추가해주어야 하는 번거로움이 있다. (혹은 상수별 메소드 구현으로 할 수는 있는데 공휴일마다 다 하기엔 빡세다.)

아래의 코드를 보자.
```java
// The strategy enum pattern
public enum PayrollDay2 {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
    SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND);

    private final PayType payType;

    PayrollDay2(PayType payType) {
        this.payType = payType;
    }

    PayrollDay2() {
        this(PayType.WEEKDAY);
    } // Default

    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    // The strategy enum type
    private enum PayType {
        WEEKDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };

        abstract int overtimePay(int mins, int payRate);

        private static final int MINS_PER_SHIFT = 8 * 60;

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }
}
```

극복 방법은 **요일 + 전략** 을 사용한 것이다.
 nested된 enum을 통해 전략에 해당하는 열거 타입을 추가하고
 요일 별로 전략을 설정하게 하여, 방어를 하는 방법이다.
 이렇게 처리하면 베이스가 되는 열거 타입에 상수별 메소드 구현을 한다거나 switch문에 계속해서 추가하는 등의 작업을 안해도 된다.

정리하면 열거 타입에 switch문을 섞는건 열거 타입 추가 등에 취약해 그렇게 좋은 방법이 아닐 수 있지만,
 여기에 상수별로 동작 자체를 추가하여 혼합해버리면 switch문이 훨씬 좋은 선택으로 변하게 된다.

또 다른 예로 switch문이 유용한 경우는 열거 타입에 대해 열거 타입을 리턴하는 경우를 예시로 들 순 있다.
```java
public static Operation inverse(Operation op) {
     switch (op) {
         case PLUS:
             return Operation.MINUS;
         case MINUS:
             return Operation.PLUS;
         case TIMES:
             return Operation.DIVIDE;
         case DIVIDE:
             return Operation.TIMES;
         default:
             throw new AssertionError("Unknown op: " + op);
     }
 }
```
( 근데 개인적으론 이런 코드가 없는게 좋아보이긴 한다. 번거롭게 같이 바꿔주어야 하기 때문에.. )

이번 장을 정리하면 필요한 원소를 컴파일 타임에 다 알 수 있다면 열거타입을 그냥 쓰는게 좋다는 것이다.

# Item 35. ordinal 메서드 대신 인스턴스 필드를 사용하라.

백문이 불여일견으로 가보자.
```java
public enum Ensemble1 {
    SOLO, DUET, TRIO, QUARTET, QUINTET,
    SEXTET, SEPTET, OCTET, NONET, DECTET;

    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

멀쩡해 보이지만 열거 타입에 값이 추가/제거 될 때 마다 값이 변해버린다. 매우 좋지 않다.

아래처럼 써서, 값이 바뀌지 않게 하고, ordinal()을 쓰지 말아라.
ordinal()은 EnumSet / EnumMap 에서 내부적으로 쓰려고 만든 값이라는 개발자 피셜도 있다고 한다.

```java
public enum Ensemble2 {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
    NONET(9), DECTET(10), TRIPLE_QUARTET(12);

    private final int numberOfMusicians;

    Ensemble2(int size) {
        this.numberOfMusicians = size;
    }

    public int numberOfMusicians() {
        return numberOfMusicians;
    }
}
```

# Item 36. 비트 필드 대신 EnumSet을 사용하라.

다음 코드를 보자. (어디선가 많이 봤다..)

```java
public class Text {
   public static final int STYLE_BOLD = 1 << 0; // 1
   public static final int STYLE_ITALIC = 1 << 1; // 2
   public static final int STYLE_UNDERLINE = 1 << 2; // 4
   public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

   // Parameter is bitwise OR of zero or more STYLE_ constants
   public void applyStyles(int styles) { ... }
}
```

앞서 설명한 정수열거 타입의 단점을 그대로 다 가져온다.
거기에다가 타입이 int로 고정되어있어서 비트가 더 필요하면 코드를 싹~~다 바꿔야 한다. 벌써 거지같다.

추가로 출력된 값을 가지고 해석하기가 굉장히 어려워 디버깅하다 죽을 수도 있다.

따라서 이런 모든 문제를 해결해주는 EnumSet을 써라. (1장에서 나온 적 있다.)

내부는 모두 비트벡터로 구현되어있고, 원소가 64개 이하면 알아서 Long을 쓰고 아니면 데이터를 더 늘려서 알아서 관리를 해준다. 얼마나 간편하냐..
 또한 removeAll, retainAll같은 대량작업에 있어서도 효율적으로 처리하는 산술연산을 사용한다.
 이렇듯 난해한 작업은 EnumSet이 다 처리해주기 때문에 그냥 믿고 써라. 무지하면 프로싱크 매니저처럼 고통받는다.

아래 코드를 보자.
```java
// EnumSet - a modern replacement for bit fields
public class Text {
   public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

   // Any Set could be passed in, but EnumSet is clearly best
   public void applyStyles(Set<Style> styles) { ... }
}
```

Text가 그냥 enum 필드를 하나 가지고 있는 것 같지만

```java
text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
```

와 같이 사용하면 **Style.BOLD, Style.ITALIC** 두 값이 들어간 EnumSet을 입력으로 받아 처리가 가능하다.

내부에선 Set을 하나씩 까면서 적용시키면 된다.

# 37. ordinal 인덱싱 대신 EnumMap을 사용해라.

ordinal 을 사용하지 말라는 것의 연장이다. 아래 예를 보자.

```java
class Plant1 {
    enum LifeCycle {
        ANNUAL, PERENNIAL, BIENNIAL
    }

    final String name;
    final LifeCycle lifeCycle;

    Plant1(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }
}
```

그냥 봤을 땐 큰 문제가 없다. 식물의 이름 (String) 에 enum으로 생애주기를 받는 클래스이다.
그럼 이제 이 코드에, 생애 주기별로 식물을 관리하는 Set을 만든다고 하자.

```java
// Using ordinal() to index into an array - DON'T DO THIS!
Set<Plant1>[] plantsByLifeCycle = (Set<Plant1>[]) new Set[Plant1.LifeCycle.values().length];
for (int i = 0; i < plantsByLifeCycle.length; i++)
    plantsByLifeCycle[i] = new HashSet<>();
for (Plant1 p : garden)
    plantsByLifeCycle[p.lifeCycle.ordinal()].add(p); /////////////////// Caution!!
// Print the results
for (int i = 0; i < plantsByLifeCycle.length; i++) {
    System.out.printf("%s: %s%n",
            Plant1.LifeCycle.values()[i], plantsByLifeCycle[i]);
}
```

이렇게 하면, ordinal 값이 배열의 인덱스로 들어갈텐데, 기본적으론 문제가 없지만 열거 타입을 **반드시** 정수 타입을 사용해야 함을 코드단에서 보장해야한다.
 컴파일러가 잡아주지도 않는다. 즉 디버깅도 어렵고 문제 터지면 찾아내기도 귀찮다.

이런걸 극복하기 위해 EnumMap이 존재한다고 생각하면 된다. 열거 타입을 key로 하고, 이를 indexing하는 map이라고 생각하면 편하다.
 즉, **열거 타입 상수** 를 사용하여 매핑하는 Map 자료구조이다.


```java
Map<Plant1.LifeCycle, Set<Plant1>> plantsByLifeCycle = new EnumMap<>(Plant1.LifeCycle.class);
for (Plant1.LifeCycle lc : Plant1.LifeCycle.values())
   plantsByLifeCycle.put(lc, new HashSet<>());
for (Plant1 p : garden)
   plantsByLifeCycle.get(p.lifeCycle).add(p);
log.info(plantsByLifeCycle);
```
( 출력은 예제 참고 )

위와 같이 Key값에 LifeCycle이 들어가고, value에는 Set이 들어간다. Set에다가 p를 계속해서 더해주어 처리하도록 되어있다.

이렇게 하니, 위에처럼 출력 결과에다가 레이블을 달아 어떤 LifeCycle인 지 알려줄 필요도 없고 배열 인덱스를 계산하지 않기 때문에 오류날 일도 없다.

이를 다음 장에 나올 **Stream** 을 사용하여 더 멋있게 사용할 수 있다. 우선 스트림을 모르니 간단한 출력 코드를 보자.
```java
// Naive stream-based approach - unlikely to produce an EnumMap!
System.out.println(Arrays.stream(garden)
.collect(groupingBy(p -> p.lifeCycle)));

// Using a stream and an EnumMap to associate data with an enum
System.out.println(Arrays.stream(garden)
.collect(groupingBy(p -> p.lifeCycle,
() -> new EnumMap<>(LifeCycle.class), toSet())));
```

근데 위 코드가 제대로 컴파일이 안되어서 (Collectors 및 Function추가해도 잘 안됨.. 아직 숙달이 덜된듯)
 지피티선생님이 아래와 같이 고쳐주셨다.
```java
EnumMap<Plant1.LifeCycle, ArrayList<Plant1>> enumMap = new EnumMap<>(Plant1.LifeCycle.class);
for (Plant1 plant : garden) {
   Plant1.LifeCycle lifeCycle = plant.getLifeCycle();
   enumMap.computeIfAbsent(lifeCycle, k -> new ArrayList<>()).add(plant);
}
```

어쨋건 장점은, 데이터가 없는 놈도 우선 EnumSet에 넣었는데, 위와같이 만들면 garden 내의 식물들 모두가 특정 생애주기 값을 갖지 않는다면 따로 set을 파지 않는다 는 장점이 있다.


그럼 다음 장점을 보자.

```java
public enum Phase1 {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

        // Rows indexed by from-ordinal, cols by to-ordinal
        private static final Transition[][] TRANSITIONS = {
                { null, MELT, SUBLIME },
                { FREEZE, null, BOIL },
                { DEPOSIT, CONDENSE, null }
        };

        // Returns the phase transition from one phase to another
        public static Transition from(Phase1 from, Phase1 to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```

위 코드에서 Phase는 고/액/기 상태 및 상태변환 이름을 정의해 놓았다. 문제는 마찬가지로 ordinal을 통해 접근한다는 점이다.
 만약 여기서 열거 타입이 수정된다면 당연히 고쳐야될게 많아진다. 예를 들어 SOLID 앞에 상태가 추가된다거나, 중간 상태가 지워진다고 할 때
 배열 내 모든 값들의 위치 및 순서를 다바꿔야 한다.

EnumMap을 사용하면 훨씬 간단한 처리가 가능하다.
```java
public enum Phase2 {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase2 from;
        private final Phase2 to;

        Transition(Phase2 from, Phase2 to) {
            this.from = from;
            this.to = to;
        }

        // Initialize the phase transition map
        private static final Map<Phase2, Map<Phase2, Transition>> m = Stream.of(values())
                .collect(Collectors.groupingBy(t -> t.from,
                        () -> new EnumMap<>(Phase2.class),
                        Collectors.toMap(t -> t.to, t -> t,
                                (x, y) -> y, () -> new EnumMap<>(Phase2.class))));

        public static Transition from(Phase2 from, Phase2 to) {
            return m.get(from).get(to);
        }
    }
}
```

* groupingBy에선 Transition을 **전이 이전 상태** 기준으로 묶어주고 (중복제거)
* toMap에선 상태를 Transition에 대응시키는 EnumMap을 생성한다.
* (x,y)->y 는 맵팩터리가 필요해서 그냥 쓴것 뿐.

장황하긴 한데 훨씬 간단하고 이해하기 쉽다.

여기서 **PLASMA** 라는 상태를 추가한다고 하자. 코드 양이 적진 않은데, 그래도 런타임에서 map을 만들 때 에러를 바로 뱉어주어 디버깅은 훨씬 용이하다.

출력 및 코드는 예제를 참조하자.

# Item 38. 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라.

열거 타입의 장점을 쭉 봤는데, 단 하나 단점이 있다면 확장하기가 조금 힘들다는 것이다.
 위에 확장하는 예시들이 몇 개 나오긴 했는데, 사실 확장을 안하는게 제일 좋다.

본 Item에선 확장할 수 있는 좋은 방법을 알아본다. 예제는 확장하기 적당한 예시인 Operation(앞에나온거) 를 사용한다.
 기본 아이디어는 열거타입은 인터페이스로 활용할 수 있다는 것에서부터 시작한다.

```java
public interface Operation {
  double apply(double x, double y);
}

public enum BasicOperation implements Operation {
    PLUS("+") {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    BasicOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

이렇게 상속을 해버려서 사용하는 방법이다. 여기서 확장을 어떻게 하냐면,

```java
// Emulated extension enum
public enum ExtendedOperation implements Operation {
    EXP("^") {
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    },
    REMAINDER("%") {
        public double apply(double x, double y) {
            return x % y;
        }
    };

    private final String symbol;

    ExtendedOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
```

와 같이 수행한다. 즉 상속을 걍 받아버리고 다른 열거 타입을 만들어 버리는 것이다. 실제 사용은 모두 Operation만 사용하는 방법이다.
 따라서 기존에 Operation을 쓰던 곳이라면 어디든지 이 코드를 쓰면 된다.

만약 확장한 녀석만 가지고 무언갈 하고 싶다면 아래 두 방법을 참고해서 쓰면 될 것이다.

```java
public static void main(String[] args) {
    double x = Double.parseDouble(args[0]);
    double y = Double.parseDouble(args[1]);
    test(ExtendedOperation.class, x, y);
}


// <T extends Enum<T> & Operation> 열거 타입이면서 Operation의 하위타입인지 확인하며 들어감
private static <T extends Enum<T> & Operation> void test(Class<T> opEnumType, double x, double y) {
    for (Operation op : opEnumType.getEnumConstants())
        System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
}
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
public static void main(String[] args) {
    double x = Double.parseDouble(args[0]);
    double y = Double.parseDouble(args[1]);
    test(Arrays.asList(ExtendedOperation.values()), x, y);
}

// Class를 보는게 아니고 한정적 와일드카드 타입으로 넘긴다.
private static void test(Collection<? extends Operation> opSet, double x, double y) {
    for (Operation op : opSet)
        System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
}
```
둘다 복잡해 보이긴 하는데, 책에선 아래게 조금 더 간편해 보인다고 한다. 대신 특정 연산에서 EnumSet / Map 을 못쓴다는데 예제가 없다.

큰 단점은 열거타입끼린 구현을 상속할 수 없어서 중복로직을 두곳에 다 짜야된다는 점이 있고, 이로인해 공유하는 기능이 많으면 구현이 복잡해진다는 점이 있다.
 이걸 해결하려면 정적 도우미 클래스를 만들어 처리하면 될 것이다.


# Item 39. 명명 패턴보다 애너테이션을 사용하라
> 여기부턴 애너테이션 관련된 이야기가 쭉나온다.
>
> 애너테이션 챕터가 39 40 41인데, 여기가 걍 젤 중요해보임


애너테이션이 없던 시절 junit은 명명패턴(메소드의 prefix가 모두 동일한 패턴)을 사용했다. 따라서 test로 시작하는 메소드가 아니면 테스트가 실행이 안되었다.

이로인해 3가지 단점이 있다.
* 오타 방지가 어려움 (tset)
* 올바른 요소가 아니면 사용 보장이 안됨 (메소드 대신 클래스 넣는다고 해서, 클래스 내부 메소드들에 대해 처리 안함)
* 매개변수로 전달할 방법이 없음.

애너테이션은 이 문제를 모두 해결해주고, Junit 4부터는 **@Test** 를 붙여서 사용하는걸 다들 보았을 것이다.

이번 챕터에선 애너테이션 동작방식을 보고자 간단한 테스트 프레임을 구현할 것이다.


```java
import java.lang.annotation.*;

/**
* Indicates that the annotated method is a test method.
* Use only on parameterless static methods.
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Item39Test {
}
```

매우 간단하다. 적용 범위를 정하고, 적용 대상을 지정하면 끝이다.

**@Retention** 는 런타임에도 이 애너테이션이 살아있어야 함을 의미하고

**@Target** 은 이 애너테이션이 메소드 선언에서만 사용되어야 한다고 대상을 한정해준다.

위 코드는 매개변수가 없는 메소드 전용으로 사용되고, 만약 더 복잡하게 짤꺼면 API 문서를 참고하라고 한다..

예제는 적당히 변형해서 만들었다.

**테스트 에너테이션**
```java
import java.lang.annotation.*;

/**
* Indicates that the annotated method is a test method.
* Use only on parameterless static methods.
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Item39Test {
}

```

**테스트 클래스**
```java
public class Item39Sample {
    @Item39Test public static void m1() {} // succes
    public static void m2() {} // don't care
    @Item39Test public static void m4() throws Exception { // failed
        throw new Exception("failed");
    }
    // @Item39Test public void m5() {} // compilation failed.
    public void m6() {}
}
```

결과는 테스트 코드 참고

위와 같이 애너테이션 자체는 코드에 직접적인 영향을 주진 않고, 부가적인 기능만 얹어준다. 즉, 대상 코드의 의미는 그대로 두고, 애너테이션에 관심있는 도구에게(JUNIT) 특별한 기회를 준다.

다음 코드를 보자.

```java
public class Item39RunTests {
    public static void func() throws Exception {
        int tests = 0;
        int passed = 0;
        // Item39Sample

        Class<?> testClass = Item39Sample.class;
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Item39Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + " failed: " + exc);
                } catch (Exception exc) {
                    System.out.println("Invalid @Test: " + m);
                }
            }
        }
        System.out.printf("Passed: %d, Failed: %d%n",
                passed, tests - passed);
    }
}
```

특정 클래스를 받아 해당 클래스 내에 어노테이션이 있는 지를 체크하고 돌리는 프로그램이다. 테스트 실패 시 실패한 내역이 쭉 나오게 된다.


이제 특정 예외를 던져야만 통과하는 테스트를 만들어보자.

```java
/**
* Indicates that the annotated method is a test method.
* Use only on parameterless static methods.
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Item39ExceptionTest {
    Class<? extends Throwable> value();
}
```

이 애너테이션의 매개변수 타입은 **Class<? extends Throwable>** 인 와일드카드이다.

Throwable을 확장한 객체라는 의미가 되시겟다. 따라서 모든 예외 타입을 수용한다. 이걸 활용하면 아래와 같다.

```java
// Program containing annotations with a parameter
public class Item39Sample2 {
    @Item39ExceptionTest(ArithmeticException.class)
    public static void m1() { // Test should pass
        int i = 0;
        i = i / i;
    }

    @Item39ExceptionTest(ArithmeticException.class)
    public static void m2() { // Should fail (wrong exception)
        int[] a = new int[0];
        int i = a[1];
    }

    @Item39ExceptionTest(ArithmeticException.class)
    public static void m3() {
    } // Should fail (no exception)
}
```

이제 테스트 코드를 아래와같이 수정하자.

```java
Class<?> testClass = Item39Sample2.class;
for (Method m : testClass.getDeclaredMethods()) {
    if (m.isAnnotationPresent(Item39ExceptionTest.class)) {
        tests++;
        try {
            m.invoke(null);
            System.out.printf("Test %s failed: no exception%n", m);
        } catch (InvocationTargetException wrappedEx) {
            Throwable exc = wrappedEx.getCause();
            Class<? extends Throwable> excType = m.getAnnotation(Item39ExceptionTest.class).value();
            if (excType.isInstance(exc)) {
                passed++;
            } else {
                System.out.printf(
                        "Test %s failed: expected %s, got %s%n",
                        m, excType.getName(), exc);
            }
        } catch (Exception exc) {
            System.out.println("Invalid @Test: " + m);
        }
    }
}
System.out.printf("Passed: %d, Failed: %d%n",
        passed, tests - passed);
```

위 코드를 보면 마찬가지로 클래스를 받아 어노테이션 존재 여부를 체크한다. 체크 후 throw가 발생하지 않으면 실패처리가 된다.

발생 했다면, 애너테이션 인자로 넘어온 타입을 읽어서, 해당 타입들과 비교해 같은 Exception 인 지를 확인한다. 만약 다르다면 마찬가지로 실패이다.


여기서 더 나아가서 예외를 여러 개 받고, 그 중 하나만 발생해도 애러로 만들 수 있다.

```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Item39ExceptionTest2 {
    Class<? extends Throwable>[] value();
}



public class Item39Sample3 {
    // Code containing an annotation with an array parameter
    @Item39ExceptionTest2({ IndexOutOfBoundsException.class, NullPointerException.class })
    public static void doublyBad() {
        List<String> list = new ArrayList<>();
        // The spec permits this method to throw either
        // IndexOutOfBoundsException or NullPointerException
        list.addAll(5, null);
    }
}
```

와 같이 처리하면 된다. 과연 결과가 어떻게 나올까? 성공으로 잘 처리가 된다.

이렇게 쓰기 싫다면 **@Repeatable** 을 추가하여 사용하면 다르게 쓸 수도 있다.

주의할 점은 어노테이션 1개가 아니라 컨테이너 역할을 해주는 애너테이션을 하나 더 만들어 주어야 한다.
( 이 놈은 시간 관계상 테스트 코드까진 안짠다. )

```java
// Repeatable annotation type
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class) // Container.
public @interface ExceptionTest {
    Class<? extends Exception> value();
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value();
}
```

```java
// Code containing a repeated annotation
@ExceptionTest(IndexOutOfBoundsException.class)
@ExceptionTest(NullPointerException.class)
public static void doublyBad() { ... }
```

어떻게 쓸 지는 취향차이인 것 같다.

추가로, 이 **ExceptionTest** 는 기존 것들과 다르게 ExceptionTest의 어노테이션으로 체크되는게 아닌 컨테이너에 해당하는
**ExceptionTestContainer** 로써 체크가 된다. 따라서 if문을 아래와 같이 바꾸지 않으면 동작하지 않는다.

```java
if (m.isAnnotationPresent(ExceptionTest.class)|| m.isAnnotationPresent(ExceptionTestContainer.class))
```

굳이 애너테이션 사용이 가능한데 명명패턴으로 사용할 일은 없다. 추가로 자바가 제공하는 기본 애너테이션에 대해 개발자는 반드시 숙지하고 항상 사용하자. (Override)

# Item 40. 애너테이션을 일관되게 사용해라.

책에선 Override 를 주로 예시를 들고있다. 생략할거면 하고, 쓸거면 확실하게 쓰라는 것이다.

예시를 보자.

```java
// Can you spot the bug?
    public class Bigram {
        private final char first;
        private final char second;

        public Bigram(char first, char second) {
            this.first = first;
            this.second = second;
        }

        public boolean equals(Bigram b) {
            return b.first == first && b.second == second;
        }

        public int hashCode() {
            return 31 * first + second;
        }

        public static void main(String[] args) {
            Set<Bigram> s = new HashSet<>();
            for (int i = 0; i < 10; i++)
                for (char ch = 'a'; ch <= 'z'; ch++)
                    s.add(new Bigram(ch, ch));
            System.out.println(s.size());
        }
    }
```
(예제 참고)

그냥 봤을 땐 문제가 없어보인다. equals & hashcode 재정의도 동시에 했다.

문제는 재정의 (override) 가 아니라 다중 정의 (overload) 를 해버린 것이다.

왜냐면 equals 자체는 매개변수를 Object를 받는데, 다른 매개변수 타입의 equals를 선언하면 다중 정의가 되어버린다.
 이로인해 위 코드에선 26개가 아닌 260개의 데이터가 들어간다.

따라서 올바르게 하려면 아래와 같이 해야한다.

```java
@Override
public boolean equals(Object o) {
    if (!(o instanceof Bigram1))
        return false;
    Bigram1 b = (Bigram1) o;
    return b.first == first && b.second == second;
}
```

이런 문제 때문에 상위 클래스의 메서드를 재정의하려는 모든 메서드엔 @Override 어노테이션을 달아주자.

만약 구현하려는 인터페이스에 디폴트 메서드가 없다는걸 안다면 굳이 안달아도 된다. 코드가 깔끔해진다.

# Item 41. 정의하련느 것이 타입이라면 마커 인터페이스를 사용하라.

마커 인터페이스는 애너테이션 이후에 구식이 되었다고 생각하기 쉽지만, 이는 실제와는 다르다고 한다.
> 마커 인터페이스는 일반적인 인터페이스와 동일하지만 사실상 아무 메소드도 선언하지 않은 인터페이스이다.
>
> Serializable을 생각하면 편하다.

## 마커 인터페이스 관련
[블로그 참고](https://woovictory.github.io/2019/01/04/Java-What-is-Marker-interface/)
```java
public void serializableTest() throws IOException, ClassNotFoundException {
  File f= new File("a.txt");
  ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(f));
  objectOutputStream.writeObject(new SomeObject("wonwoo", "test@test.com"));
}

class SomeObject {
  private String name;
  private String email;
  //생성자 및 기타 메서드 생략
}
```
이렇게 쓰면 애러가 난다. 왜냐면 직렬화 할 수 있는 Serializable을 구현하지 않았기 때문이다.
```java
class SomeObject implements Serializable {
  private String name;
  private String email;
}
```
이렇게만 바꿔주어도 된다. 왜냐하면 ```writeObject``` 안에 타입별로 어떻게 Serialize할 지가 정의되어 있다.
즉, 간단하게 Serializable이 선언되어있는 지만 확인하도록 되어있다.


마커 인터페이스의 장점은 아래와 같다.
1. 마커 인터페이스는 이를 구현한 클래스의 인스턴스들을 구분하는 타입으로 쓸 수 있지만, 애너테이션은 구분까진 할 수 없다.

   마커 인터페이스는 엄연한 타입이기 때문에 컴파일 타임에 에러를 모두 잡을 수 있어서 좋다.
   책에서 문제점을 지적하는건 위 예에서 ```writeObject``` 가 ```Serializable``` 가 아닌 ```Object``` 를 받도록 설계되었다는 점에 있다. 이로 인해 컴파일 타임에
   타입을 체크할 수 있다는 장점을 스스로 버리게 되었다.



2. 적용 대상을 정밀하게 지정할 수 있다.

    어노테이션은 인터페이스냐 클래스냐 까진 지정이 되지만 그 이상 세밀하겐 안된다. 인터페이스는 된다.ㄴ


반대로 애너테이션의 장점은 무엇이냐?

1. 훨씬 거대한 자바 시스템의 지원을 받는다.

그럼 어떨때 무얼 써야 하냐?

**마킹이 된 객체를 매개변수로 받는 메서드를 작성할 일이 있을까?**  를 질문하면 좋다.

만약 그렇다면 인터페이스를 써서 타입체크를 해야 하고, 그렇지 않다면 애너테이션으로 충분할 것이다. (대부분 필요하지 않나..?)

