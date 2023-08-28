# Ch 4. 클래스와 인터페이스
# Item 15. 클래스와 멤버의 접근권한을 최소화해라
잘 설계된 컴포넌트는 모든 내부구현을 완벽히 숨겨, 내부구현과 API 를 깔끔히 분리한다.\
정보은닉, 혹은 캠슐화라 부르는 이 개념은 소프트웨어 설계의 근간이 된다.


## 15-1. 캡슐화의 장점
캡슐화의 장점은 다음과 같다.

* 컴포넌트를 병렬로 개발 가능하여, `개발의 속도를 높여준다`.
* `관리 비용을 낮춘다`. 컴포넌트를 파악하기 쉬워 디버깅을 용이하게하며, 컴포넌트 교체의 부담을 줄인다.
* `성능최적화에 유리하다`. 직접적인 이득은 없지만 시스템 프로파일링을 통해 최적화할 컴포넌트를 정하고 쉽게 교체할 수 있기 때문이다.
* `재사용성을 높인다`. 외부에 의존하지 않고 독자적으로 동작할 수 있는 컴포넌트라면 낯선 환경에서도 유용하게 쓰일 가능성이 높다.
* `개발 난이도를 낮춘다`. 시스템이 완성되지 않은 상태에서도 개별 컴포넌트에 대한 검증이 가능하다.


## 15-2. Java의 접근제한자
정보 은닉을 위한 Java 의 접근 제어 메커니즘은 요소가 선언된 위치와 접근제한자로 정해진다.\
톱레벨 클래스나 인터페이스에 부여할 수 있는 접근제한자는 `public` 과 `package-private` 이다. public 으로 선언하면 공개 API 가 되고, package-private 의 경우, 해당 패키지 내에서만 사용이 가능하다.\
멤버(***field, method, nested class, nested interface***)에게 부여할 수 있는 접근제한자는 다음과 같다.

* `private` : 멤버를 선언한 **톱 클래스 내에서만** 접근이 가능하다
* `package-private` : 멤버가 소속된 패키지 전체에서 접근 가능하며, 접근제한자를 명시하지 않을 경우 default 로 적용된다(단, interface 는 public 이 default 로 적용된다).
* `protected` : package-private 접근을 포함하며, 멤버를 선언한 클래스의 하위 클래스에서도 접근가능하다.
* `public` : 모든 곳에서 접근 가능하다.

```java
public class PrivateAccess {
    private String privateString = "you can't see me";
    
    public void printMemberPrivateString(StaticMemberClass staticMemberClass) {
        System.out.println(staticMemberClass.memberPrivateString);
    }
    
    public static class StaticMemberClass {
        private String memberPrivateString = "you can't see me too";
        
        public void printPrivateString(PrivateAccess privateAccess) {
            System.out.println(privateAccess.privateString);
        }
    }
}
```


## 15-3. 캡슐화 실천 방법론
기본원칙은 간단하다, 가능한 모든 클래스와 멤버의 접근성을 좁혀야 한다.\
세심하게 공개 API를 설계한 후, 그 외의 ***모든 멤버는 private 으로 선언한다***. 그 후 패키지 내의 다른 클래스에서 접근이 필요한 경우, package-private 으로 제한자를 풀어준다.\
<U>**만약 권한을 풀어주는 일이 빈번하게 발생한다면, 컴포넌트를 더 분해해야되는게 아닌지 고민해보자**.</U>

이제 주의사항에 대해 알아보자

* public class 의 instance field 는 되도록 public 이 아니여야한다. field 가 가변객체를 참조하거나, final 이 아닌 instance field 를 public 으로 선언할 경우 ***값을 제한할 힘을 잃는다***.
* 해당 클래스가 표현하는 추상개념을 완성하는데 꼭 필요한 경우 public static final 필드로 공개해도 좋다. 하지만 <U>반드시 primitive 혹은 immutable object 만을 참조해야한다</U>.
* 배열은 public 혹은 접근자 메서드를 제공해서는 안된다.

```java
//보안 허점이 숨어 있다.
public static final Thing[] VALUES = { ... };
```
해결 방법은 2가지이다.
```java
// public immutable list
private static final Thing[] PRIVATE_VALUES = { ... };
public static final List<Thing> VALUES = Collections.unmodifiableList(Arrays.asList(PRIVATE_VALUES));

// defensive copy
private static final Thing[] PRIVATE_VALUES = { ... };
public static final Thing[] values() {
    return PRIVATE_VALUES.clone(); // Ch3. 에서 clone 을 되도록 하용하지 말라했지만 배열은 제대로 사용하는 몇 안되는 예시 중 하나이다.
}
```

앞서 얘기한 4가지 접근제한자 외에 Java9에서 모듈시스템이 추가되면서 2가지 암묵적 접근수준이 추가되었으나, ***상당히 주의하여 사용한다하므로 스킵하자***.


# Item 16. Public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라


## 16-1. 접근자 메서드
```java
// public 이어서는 안된다
class Point {
    public double x;
    public double y;
}
```
이런 클래스는 API 를 수정하지 않고는 내부표현을 바꿀 수 없고, 불변식을 보장할 수 없으며, 필드 접근시 부수작업을 수행할 수도 없다.

```java
class Point {
    private double x;
    private double y;
    
    public double getX() { return x; }
    public double getY() { return y; }
    
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
```
public class 라면 접근자를 제공하므로써 추후 클래스 내부 표현 방식을 얼마든지 바꿀수 있다.\
하지만 **`package-private` 클래스 혹은 `private nested class` 라면 data field 를 노출한다해도 문제가 없다**. 오히려 클래스 선언 면에서나 클라이언트 코드 면에서나 접근자 방식보다 깔끔하다.\
클라이언트 코드가 묶이기는 하나 그 범위가 package 내로 제한되기에 패키지 바깥 코드는 전혀 신경쓰지 않고 수정이 가능하다.


## 16-2. 자바 플랫폼 라이브러리의 예시
public 클래스의 필드를 직접 노출하지 말라는 규칙을 어긴 사례가 종종 있다. 대표적으로 `java.awt.pacakge` 의 `point`, `Demension` 이 있다.\
`Dimension` 의 성능문제는 여전히 해결되지 못했다.
```java
public final class Time {
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_DAY = 60;
    
    public final int hour;
    public final int minute; // sseo : public 으로 오픈하지 않았다면 하나의 field 로 변경할 수 있지 않았을까
    
    public Time(int hour, int minute) {
        if (hour < 0 || hour >= HOURS_PER_DAY)
            throw new IllegalArgumentException("시간: " + hour);
        if (minute < 0 || minute >= MINUTES_PER_DAY)
            throw new IllegalArgumentException("분: " + minute);
        this.hour = hour;
        this.minute = minute;
    }
    // ... 나머지 코드 생략
}
```


# Item 17. 변경 가능성을 최소화하라
불변 클래스란 간단히 말해 그 인스턴스 내부의 값을 수정할 수 없는 클래스다.\
Java 의 대표적인 예로 String, primitive type 의 박싱된 클래스, BigInteger, BigDecimal 이 여기 속한다.\
클래스를 불변으로 만들기 위해서는 다음 5가지 규칙을 지키면 된다.

* 객체의 상태를 변경하는 메서드(변경자)를 제공하지 않는다.
* 클래스를 확장할 수 없도록 한다
* 모든 field 를 `final` 로 선언한다.
* 모든 field 를 private 으로 선언한다. public final 로 선언해도 필드의 불변성을 지킬 수는 있지만, 추후 내부 표현을 바꾸지 어려워진다.
* 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다. 외부에서 가변 객체의 참조를 얻을 수 없게하고, 외부에서 주입되는 가변 객체에는 ***방어적 복사***를 수행해라


## 17-1. 불변 객체의 이점
```java
public final class Complex {
    private final double re;
    private final double im;
    
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    
    public double realPart() { return re; }
    public double imaginaryPart() { return im; }
    
    public Complex plus(Complex c) {
        return new Complex(re + c.re, im + c.im);
    }
    
    public Complex minus(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }
    
    public Complex times(Complex c) {
        return new Complex(re * c.re - im * c.im, re * c.im + im * c.re);
    }
    
    public Complex dividedBy(Complex c) {
        double tmp = c.re * c.re + c.im * c.im;
        return new Complex((re * c.re + im * c.im) / tmp, (re * c.re - im * c.im) / tmp);
    }
    
    @Override
    public boolean equals(Objects o) {
        if (o == this)
            return true;
        if (!(o instanceof Complex))
            return false;
        Complex c = (Complex) o;
        return (Double.compare(c.re, re) && Double.compare(c.im, im));
    }
    
    @Override
    public int hashcode() {
        return 31 * Double.hashCode(re) + Double.hashCode(im);
    }
    
    @Override
    public String toString() {
        return "(" + re + "+" + im + "i)";
    }
}
```

예제코드의 사칙연산 메서드를 주목하자, 이처럼 피연산자에 함수를 적용해 결과를 반환하지만, 피연산자는 그대로인 프로그래밍 패턴을 함수형 프로그래밍이라한다.\
또한, 메서드명으로 **동사(add) 대신 전치사(plus) 를 사용한 것**또한 주목하자, 이는 해당 메서드가 객체의 값을 변경하지 않음을 강조하려는 의도이다.

불변객체는 단순하다, 생성부터 소멸까지 같은 상태를 가지는 것이 주는 이점은 상당하다. 

> 불변객체는 근본적으로 thread-safe 하여 따로 동기화할 필요가 없다, 즉 안심하고 공유할 수 있다.\
> 이러한 사실은 자연스럽게 ***방어적 복사가 불필요하다는 결론***으로 이어진다. 따라서 clone 이나 copy constructor 를 제공하지 않는 것이 좋다.\
> 불변클래스라면 한번 만든 인스턴스를 최대한 재활용하기를 권한다. 예컨데 다음과 같이 사용할 수 있다.
```java
public static final Complex ZERO = new Complex(0, 0);
public static final Complex ONE = new Complex(1, 0);
public static final Complex I = new Complex(0, 1);
```
<U>String 클래스의 copy constructor 는 이러한 사실이 이해되지 않은 초창기에 만들어진 것으로 되도록 사용하지 말아야한다.</U>

> 불변 객체끼리는 내부 데이터를 공유할 수 있다. 예컨데 BigInteger 클래스는 내부에서 값의 부호(sign)과 크기(magnitude)를 가진다.\
> 부호에는 int, 크기에는 int array 를 사용한다. 한편 negate 메서드는 크기가 같고 부호가 반대인 새로은 BigInteger 를 생성하는데,\
> 이 때 **배열이 비록 가변이지만 복사하지 않고 원본 인스턴스와 공유해도 된다**.

> 불변 객체는 그 자체로 실패 원자성(failure atomicity)[exception 발생후에도 여전히 유효한 상태여야 한다]를 보장한다.\
> 잠깐이라도 불일치에 빠질 가능성이 없다.


## 17-2. 불변 객체의 단점과 해결방안
불변 객체에도 단점은 있다. 값이 다르면 반드시 독립된 객체로 만들어야한다.
예를 들어 BigInteger 에서 bit 하나를 바꿔야 한다고 해보자
```java
BigInteger moby = ...;
moby = moby.flipBit(0);
```
``flipBit`` 메서드는 새로운 BitInteger 인스턴스를 생성한다. 이 연산은 BigInteger 의 크기에 비례해 시간과 공간을 잡아먹는다.\
반면 BitSet 도 BigInteger 처럼 임의의 길이의 비트순열일 표현하지만, BigInteger 와 달리 **가변**이다.
```java
BitSet moby = ...;
moby.flip(0);
```
해당 연산은 상수시간을 보장해준다.

원하는 객체를 완성하기까지의 단계가 많고, 그 중간에 만들어진 객체들이 모두 버려진다면 성능 문제가 더 불거진다. 이러한 문제의 대처방안은 2가지이다.

> 다단계 연산(multistep operation)을 **예측**하여 기본기능으로 제공하는 방법이 있다. 복잡한 연산이 필요한 연산을 기본기능으로 제공하여 중간의 객체 생성을 생략하는 것이다.\
> 대표적으로 BigInteger 는 모듈러 지수 같은 다단계 연산 속도를 높여주는 가변 동반클래스(companion class)를 ``package-private``으로 두고 있다.

> 클라이언트가 원하는 복잡한 연산을 예측할 수 없는 경우, 가변 동반클래스를 ``public``으로 제공하는 것이 최선일 것이다.\
> 대표적인 예로 String 클래스가 있다. 가변 동반클래스로 StringBuilder(~~구닥다리 전임자 StringBuffer~~)를 제공한다.


## 17-3. 정리
클래스를 불변으로 만들기 위한 5가지 규칙 중 클래스를 확장할 수 없게하라는 부분을 다시보자.\
가장 쉬운 방법은 `final`로 선언하는 것이지만 더 유연한 방법이 있다, 바로 모든 생성자를 `private` or `package-private`으로 만들고 `public static method`를 제공하는 것이다.
```java
public class Complex {
    private final double re;
    private final double im;
    
    private Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    
    public static Complex valueOf(double re, double im) {
        return new Complex(re, im);
    }
}
```
<U>이 방식이 최선일 경우가 많다.</U> 바깥에서는 볼 수 없으니 package-private 구현 클래스를 원하는 만큼 만들어 활용할 수 있다.\
`public` 이나 `protected`생성자가 없으니 이 클래스를 확장할 수 없고, 정적 팩터리 방식은 다수의 구현 클래스를 활용한 유언성을 제공할 수 있다. 또한 객체 캐싱을 통해 성능을 끌어올릴 수도 있다.

`BigInteger` 나 `BigDecimal`이 설계될 당시 불변 객체가 사실상 `final`이여야한다는 생각이 널리 퍼지지 않았고, 두 클래스 모두 재정의가 가능해지며 현재까지 하위 호환성이 발목을 잡고 있다.\
따라서 신뢰할 수 없는 클라이언트로부터 두 클래스의 인스턴스를 인수로 받는다면 주의해야한다
```java
public static BigInteger safeInstance(BigInteger val) {
    return val.getClass() == BigInteger.class ? val : new BigInteger(val.toByteArray());
}
```

규칙 중 모든 field 를 final 로 선언하라는 부분도 다시보자. 성능을 위해 이 부분을 살짝 완화하여, 계산 비용이 큰 값을 나중에 계산하여 final 이 아닌 field 에 캐싱할 수 있다.
```java
private int result;

public int getResult() {
    if (result != 0) {
        return result;
    }
    // heavy-cost job
}
```
<U>객체가 불변이므로 같은 인자에 대한 결과는 항상 같음이 보장되기에</U> 이러한 방법을 사용할 수 있다.

클래스는 꼭 필요한 경우가 아니라면 불변이어야 한다. 성능 때문에 어쩔 수 없다면 가변 동반 클래스를 public 으로 제공하는 것을 고려하자.\
불변으로 만들 수 없는 클래스라면 변경 가능한 부분을 최대한 줄이자, Item 15 와 종합하면 타당한 이유가 없다면 모든 필드는 `private final`이어야 한다.\
생성자는 불변식 설정이 모두 완료된 **초기화가 끝난 상태의 객체**를 생성해야한다. 생성자와 정적팩터리 외에 <U>그 어떤 초기화 메서드도 public 으로 제공해서는 안 된다. 재활용할 목적으로 상태를 초기화하는 메서드도 안 된다.</U>


# Item 18. 상속보다는 컴포지션을 사용하라
확장할 목적으로 설계되었고 문서화도 잘 된 클래스는 안전하다. 하지만 일반적으로 구체 클래스를 패키지 경계를 넘어 상속하는 것은 위험하다.\
여기서 상속이랑 `extends` 를 의미하며(다른 클래스의 확장), `implements`(인터페이스 구현) 혹은 인터페이스가 다른 인터페이스를 확장하는 것과는 무관하다.


## 18-1. 구현상속의 문제 예시
메서드 호출과 달리 ***상속은 캡슐화를 깨트린다***.\
구체적인 예로 우리에게 HashSet 을 사용하는 프로그램이 있고, 생성 이후 지금까지 몇 개의 원소가 더해졌는지 알 수 있어야한다 가정하자.
```java
import java.util.Collection;

public class InstrumentedHashSet<E> extends HashSet<E> {
    private int addCount = 0;

    public InstrumentedHashSet() { }

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
    
    public int getCount() { return addCount; }
    
    public static void main(String[] args) {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        
        s.addAll(Arrays.asList("a", "b", "c"));
        // s.addAll(List.of("a", "b", "c"));
    }
}
```
다음 예시는 언듯 보기에 제대로 동작할 것 같다. 이제 `getCount()` 를 호출하면 3을 반환하리라 기대하지만, 실제로는 6을 반환한다.\
원인은 HashSet 의 addAll 메서드가 add 메서드를 사용해 구현되있다는데 있다. 재정의된 add 메서드가 호출되면서 중복카운트가 되는 셈이다.\
이 경우 `addAll` 메서드를 재정의하지 않으면 문제를 해결할 수 있다. 하지만 이는 <U>`HashSet` 의 addAll 이 add 를 이용했음을 가정한 해법이라는 한계를 지닌다.</U>\
`addAll` 을 다른 방식으로 재정의하여 해결하는 것 또한 생각해볼 수 있다. 하지만 `addAll`을 다시 구현한다는 것은 어렵고 자칫하면 오류를 내거나 성능을 떨어트릴 수 있다. 또한 상위 클래스의 `private field`를 사용해야되는 상황이라면 구현 자체가 불가능하다.

보안 상의 이유로 컬렉션에 추가되는 원소가 특정조건을 만족해야만 하는 프로그램을 생각해보자. 모든 메서드를 재정의하여 조건을 검사하면 될 것 같지만 만약 다음 릴리즈에서 상위 클래스에 메서드가 추가되고 이를 재정의하지 않게된다면, 허용하지 않은 원소를 추가할 수 있는 구멍이 생기는 셈이다.\
재정의하지 않고 메서드를 추가하는 구현은 어떨까? 추후 상위 클래스에 추가되는 메서드와 시그니처가 같고 반환타입이 다르다면 컴파일 조차 안 될 것이다. 또한 반환타입마져 같다면 재정의한 꼴이니 위의 문제가 발생한다. 더해 상위 클래스에 메서드가 존재하지 않을 때 구현한 메서드이니 규약을 만족하지 못 할 가능성이 크다.


## 18-2. 컴포지션
이를 피해가는 묘안으로 컴포지션이 있다. 기존 클래스를 확장하는 대신 <U>새로운 클래스를 만들고, private 필드로 해당 인스턴스를 참조하게하자.</U>\
새 클래스의 인스턴스 메서드들은 기존 클래스에 대응하는 메서드를 호출해 그 결과를 반환하자. 이 방식을 전달(`forwarding`)이라하며, 새 클래스의 메서드들을 전달메서드`forwarding method`라 한다.

```java
import java.util.Iterator;

public class InstrumentedSet extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getCount() {
        return addCount;
    }
}
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s; // Set interface 를 상속해 구현하지만 구체 클래스를 상속 받는 것이 아니다. 구체 클래스는 생성자를 통해 주입 받는다.

    public ForwardingSet(Set<E> s) {
        this.s = s;
    }

    public void clear() { s.clear(); }

    public boolean contains(Object o) { return s.contains(o); }

    public boolean isEmpty() { return s.isEmpty(); }

    public int size() { return s.size(); }

    public Iterator<E> iterator() { return s.iterator(); }

    public boolean add(E e) { return s.add(e); }

    public boolean remove(Object o) { return s.remove(o); }

    public boolean containsAll(Collection<?> c) { return s.containsAll(c); }

    public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }

    public boolean removeAll(Collection<?> c) { return s.removeAll(c); }

    public boolean retainAll(Collection<?> c) { return s.retainAll(c); }

    public Object[] toArray() { return s.toArray(); }

    public <T> T[] toArray(T[] a) { return s.toArray(a); }

    @Override
    public boolean equals(Object o) { return s.equals(o); }

    @Override
    public int hashCode() { return s.hashCode(); }

    @Override
    public String toString() { return s.toString(); }
}
```
새로운 클래스는 기존 클래스의 내부구현 방식에 영향을 받지 않으며, 새로운 메서드가 추가되더라도 전혀 영향받지 않는다.\
상속 방식은 구체 클래스 각각을 따로 확장해야하며, 각 대응하는 생성자를 별도로 정의해줘야한다. 하지만 컴포지션 방식은 한번 구현하면 어떤 `Set`구현체라도 계측가능하다.
```java
Set<Instant> times = new InstrumentedSet<>(new TreeSet<>(cmp));
Set<E> s = new InstrumentedSet<>(new HashSet<>(INIT_CAPACITY));

static void walk(Set<Dog> dogs) {
    InstrumentedSet<Dog> iDogs = new InstrumentedSet<>(dogs);
}
```
다른 `Set` 인스턴스를 감싸고(wrap)있다는 뜻에서 래퍼 클래스라고하며, `Set`에 계측 기능을 추가했다는 뜻에서 데코레이터 패턴(Decorator Pattern) 이라고 한다.\
컴포지션과 전달의 조합은 넓은 의미로 위임(delegation)이라고 할 수 있다. 다만 엄일하게는 래퍼객체가 내부객체에 자신의 참조를 전달 할 때만 위임이라고한다.

래퍼 클래스의 단점은 거의 없지만 <U>콜백 패턴과는 어울리지 않는다.</U>\
콜백 패턴에서 인스턴스는 자신의 참조를 다른 객체에게 전달하여 다음호출(callback)에서 사용되게 한다, 하지만 내부 객체는 자신을 감싸는 래퍼의 존재를 모르기 때문에 자신에 대한 참조를 전달하게 된다. 이를 SELF Problem 이라한다.

상속은 하위 클래스가 상위 클래스의 '진짜'(as-is) 하위 클래스일 때만 한다. 예를 들어 B가 명백히 A일 경우에만 상속을 한다(리스코프 치환).\
자바 플랫폼 라이브러리에서도 이 원칙을 명백히 위반한 클래스들을 찾아볼 수 있다. `Stack`은 `Vector`가 아니므로 확장하지 말았어야했고, 마찬가지로 `Properties`는 `HashTable`을 확장하지 말았어야했다.\
컴포지션을 사용해야되는 상황에서 상속을 하는 것은 API를 내부 구현에 묶이게 만들고 영원히 성능 또한 제한된다. 더욱 심각한 것은 클라이언트가 내부 구현에 접근가능하다는 것이다.


# Item 19. 상속을 고려해 설계하고 문서화해라. 그러지 않았다면 상속을 금지하라.
상속을 고려한 설계와 문서화란 어떤 것일까? <U>상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지 문사화해야한다.</U> 여기서 재정의 가능한 메서드란 `public` or `protected` 접근자로 선언된 메서드 중 `final`이 아닌 것을 뜻한다.\
하지만 이런식의 문서화는 ***좋은 문서화는 '어떻게'가 아닌 '무엇'을 하는지 설명해야한다***는 격언과 위배된다. 안타깝게도 이는 <U>상속이 캡슐화를 깨트리기에</U> 발생하는 현실이다.\
더해, 효율적은 하위클래스를 만들게 하기위해서는 메서드의 동작 중 끼어들 수 있는 훅(hook)을 잘 선별해 `protected`로 공개해야할 수도 있다.

상속용 클래스를 검증하는 방법은 직접 하위 클래스를 만들어 보는 것이 유일하다. 그리니 상속용으로 설계한 클래스는 반드시 배포전에 하위 클래스를 만들어 검증해야한다.


## 19-1. 상속용 클래스의 생성자
상속용 클래스의 생성자는 직접적으로나 간접적으로 재정의 가능한 메서드를 호출해서는 안된다(`private`, `final` `static`은 재정의가 불가능하니 안심하고 호출할 수 있다).
```java
public class Super {
    public Super() { overrideMe(); }
    
    public void overrideMe() { }
}

public class Sub extends Super {
    private final String instant;
    
    public Sub() { instant = "hello world"; }
    
    @Override
    public void overrideMe() {
        System.out.println(instant);
    }
    
    public static void main(String[] args) {
        Sub sub = new Sub();
        
        sub.overrideMe();
    }
}
```
일반적으로 `Cloneable`과 `Serializable` 인터페이스 둘 중 하나라도 구현한 클래스를 상속할 수 있게 하는 것은 좋지 않은 생각이다.\
`clone`과 `readObject`메서드는 생성자와 비슷한 효과를 낸다. 따라서 제약 또한 생성자와 비슷하다, 이들도 직접 혹은 간접적으로 재정의 가능한 메서드를 호출해서는 안된다.\
마지막으로 `Serializable`을 구현한 상속용 클래스가 `readResolve`나 `writeReplace`메서드를 갖는다면 이 메서드들은 `private` 이 아닌 `protected` 로 선언하여 공개해야한다(hook).


## 19-2. 상속문제의 해결 방안
가장 좋은 해결방법은 상속용으로 설계하지 않은 클래스는 상속을 금지하는 것이다. 구체적으로는 `final`을 붙이거나, 모든 생성자를 `private` or `package-private`으로 제한한 뒤 정적 팩터리 메서드를 제공하는 것이다.\
핵심 기능을 정의한 인터페이스가 있고, 클래스가 그 인터페이스를 구현했다면 상속을 금지하더라도 개발에는 어려움이 없을 것이다. `Set` `List` `Map`이 좋은 예이다.

상속을 꼭 허용해야겠다면 클래스 내부에서 재정의 가능한 메서드를 사용하지 말고 이 사실을 문서화하자.\
클래스의 동작을 유지하면서 재정의 가능한 메서드의 사용을 제거하는 기계적인 방법으로 Helper Method 가 있다. 재정의 가능한 메서드를 Helper Method 로 옮기고 이 를 호출하도록 하자.


# Item 20. 추상클래스 보다는 인터페이스를 우선하라
Java 가 제공하는 다중 상속 메커니즘은 `interface`와 `abstract class` 두가지로 나뉜다.\
두 가지 방식의 가장 큰 차이로 추상 클래스의 경우 반드시 구현 객체가 추상 클래스의 하위 클래스가 되야하지만, 인터페이스의 경우는 일반 규약만 만족한다면 어떤 클래스를 상속하더라도 같은 타입으로 취급한다.


## 20-1. 인터페이스의 장점
> 인터페이스는 믹스인(mixin) 정의에 알맞다. 믹스인을 구현한 클래스에 원래의 주된 타입 외에도 특정 선택적 행위를 제공한다고 선언하는 효과를 준다.\
> 예를 들어 Comparable 은 자신을 구현한 클래스의 인스턴스들끼리는 순서를 정할 수 있다고 선언하는 믹스인 인터페이스이다.

> 계층구조가 없는 프레임워크를 만들 수 있다. 계층적 구저를 이용한다면 많은 개념을 구조적으로 표현할 수 있지만, 현실은 구분하기 애매한 경우가 있다.\
> 아래와 같이 interface 를 이용해 정의한다면 클래스가 Singer 와 SongWriter 를 모두 구현한다해도 문제가 되지않을 것이다.\
> 이를 추상클래스를 이용해 작성하고자 한다면 2^n 의 조합 폭팔(combinatorial explosion)이 발생한다.
```java
public interface Singer {
    AudioClip sing(Song s);
}

public interface SongWriter {
    Song compose(int chartPosition);
}
```
Item 18 에서 다룬 <U>래퍼클래스를 이용한다면 `interface`는 기능을 추가하는 안전하고 강력한 수단이다. 타입을 추상클래스로 작성한다면 이를 확장할 방법은 상속 뿐이다.</U> 이는 활용도가 더 떨어지고 꺠지기 쉽다.\
인터페이스의 메서드 중 구현방법이 명확한 것이 있다면 `default method`를 제공해 구현의 일감을 덜어줄 수 있다(디폴트 메서드를 제공할 때는 상속하려는 사람을 위해 `@implSpec` javadoc tag 를 붙여 문서화해야한다).\
디폴트 메서드에도 제약사항은 있다. `equals`와 `hashCode`를 정의해서는 안된다(Ch2 참조).


## 20-2. 추상 골격 구현(skeletal implementation)
인터페이스와 추상 골격 구현을 같이 제공하여 두 장점을 모두 취하는 방법도 있다. 인터페이스로는 타입을 정하고 필요하다면 `default method`를 정의한다.\
그리고 골격 구현 클래스에서는 나머지 메서드들까지 구현한다. 이로써 골격 구현을 확장하는 것만으로도 인터페이스 구현에 필요한 대부분이 끝난다, 이를 ***템플릿 메서드 패턴***이라 한다.\
관례상 인터페이스의 이름이 **Interface**라면 골격 구현 클래스의 이름은 AbstractInterface 라 짓는다. 좋은 예로 `AbstractMap` `AbstractList` `AbstractSet` `AbstractCollection` 각각이 핵심 컬렉션 인터페이스의 골격 구현이다.
```java
static List<Integer> intArrayAsList(int[] a) {
    Objects.requireNonNull(a);

    return new AbstractList<Integer>() { // 익명 클래스
        @Override
        public Integer get(int index) {
            return a[index];
        }
    
        @Override
        public Integer set(int i, Integer val) {
            int oldVal = a[i];
            a[i] = val;
            return oldVal;
        }
    
        @Override
        public int size() {
            return a.length;
        }
    };
}
```
구조상 골격 구현을 확장하지 못하는 처지라도 인터페이스가 제공하는 디폴트메서드의 이점을 누릴 수 있다. 또한 골격 구현클래스의 이점을 우회적으로 이용할 수 있다.\
인터페이스를 구현한 클래스에서 해당 골격 구현을 확장한 `private member class` 를 정의하고 각 메서드의 호출을 내부 클래스의 인스턴스에 전달하는 것이다. 이를 `simulated multiple inheritance`라 한다.

골격 구현을 작성하는 방법은 다음과 같다
* 인터페이스를 살펴 다른 메서드의 구현에서 사용되는 기반 메서드를 정한다. 이는 골격 구현에서 추상 메서드가 될 것이다.
* 기반 메서드를 이용해 직접 구현할 수 있는 메서드를 모두 디폴트 메서드로 제공한다.
* 남은 메서드들을 골격 구현에서 작성해 넣는다.

간단한 예로 Map.Entry 인터페이스를 살펴보자.
```java
public abstract class AbstractMapEntry<K, V> implements Map.Entry<K, V> {
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry<?, ?> e = (Map.Entry) o;
        return Objects.equals(e.getKey(), getKey()) && Objects.equals(e.getValue(), getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}
```
`getKey`와 `getValue`는 확실히 기반메서드이며, Object 메서드들은 디폴트메서드로 제공해서는 안 되므로 모두 골격 구현 클래스에서 구현한다.\
골격 구현 또한 추상클래스와 마찬가지로 상속을 가정하므로 문서화 지침을 모두 따라야한다.\
 단순 구현(simple implementation) 은 골격 구현의 작은 변종으로 상속을 위해 인터페이스를 구현하지만 추상 클래스가 아니다, 예로써 `AbstractMap.SimpleEntry` 가 있다.

# Item 21. 인터페이스는 구현하는 쪽을 생각해 설계하라
Java 8 부터 추가된 디폴트 메서드를 이용하면 인터페이스를 구현한 후 디폴트 메서드를 재정의하지 않은 모든 클래스에서 디폴트 구현이 쓰이게 된다.\
자바 라이브러리의 디폴트 메서드는 대부분의 상황에서 잘 작동하지만, 모든 상황에서 불변식을 만족하는 디폴트 메서드를 작성하기란 어려운 일이다.\
Java 8의 `Collection` 인터페이스의 removeIf 메서드를 예로 살펴보자.
```java
default boolean removeIf(Predicate<? super E> filter) {
    Objects.requireNonNull(filter);
    boolean removed = false;
    final Iterator<E> each = iterator();
    while (each.hasNext()) {
        if (filter.test(each.next())) {
            each.remove();
            removed = true;
        }
    }
    return removed;
}
```
`org.apache.commons.collections4.collection.SynchronizedCollection` 는 클라이언트가 제공한 객체로 락을 걸고 내부 컬렉션 객체에 기능을 위임하는 래퍼 클래스이다.\
책이 쓰여진 당시 기준(현재는 고쳤을 듯?)으로 removeIf 를 재정의하지 않았고, 만일 java 8 과 같이 쓴다면 동기화 역할에 구명이 생기는 셈이다.

인터페이스를 설계할 때에는 세심한 주의가 필요하다, 디폴트 메서드를 기존 인터페이스에 추가한다면 커다란 위험이 딸려온다. 릴리즈 후에라도 결함을 수정할 수 있지만 절대 그 가능성에 기대서는 안 된다.\
~~이번 아이템은 라이브러레/프레임 제공자가 아닌 우리랑은 먼 얘기 인듯...~~

# Item 22. 인터페이스는 타입을 정의하는 용도로만 사용하라
인터페이스를 구현한다는 것은 자신의 인스턴스로 ***무엇을 할 수 있는지*** 클라이언트에게 알려주는 것이다. <U>인터페이스는 오직 이 용도로만 사용해야한다.</U>\
안 좋은 예로 소위 상수 인터페이스가 있다. 상수 인터페이스란 메서드 없이, 상수를 뜻하는 `static final` field 로만 가득 찬 인터페이스를 말한다.\
그리고 이 상수들을 사용하려는 클래스에서는 정규화된 이름(qualified name, interface name 명시하는 것 말하는 듯)을 사용하는 것을 피하고자 그 인터페이스를 구현하곤 한다.
```java
public interface PhysicalConstants {
    static final double AVOCADO_NUMBER = 6.022;
    static final double BOLTZNUT_CONSTANT = 1.38;
    static final double ELECTRO_MARKET = 9.109;
}
```
안티패턴으로 인터페이스를 잘못 사용한 예이다. 클래스 내부에서 사용하는 상수에 상수 인터페이스를 사용하는 것은 내부 구현을 클래스의 API 로 노출하는 행위다.\
이는 클라이언트 코드가 이 상수에 종속되게 만들고, 추후에도 바이너리 호환성을 위해 제거할 수 도 없다. `java.io.ObjectStreamConstants` 등 자바 플랫폼 라이브러리에도 상수 인터페이스가 몇개 있으나, 인터페이스를 잘못 활용한 예이니 따라해서는 안 된다.

상수를 공개할 목적이라면 몇가지 타당한 선택지가 있다. 특정 클래스나 인터페이스와 강하게 연관되 있다면 해당 클래스나 인터페이스에 추가해야한다(ex. Integer 의 MAX_VALUE)\
enum type 으로 나타내기 적합하다면 enum 을 활용하고, 그것도 아니라면 인스턴스화 불가능한 utility class 에 담아 공개하자. 만약 상수를 빈번히 사용한다면 `static import`하여 클래스 이름을 생략할 수 있다.
```java
public class PhysicalConstants {
    private PhysicalConstants() { }

    static final double AVOCADO_NUMBER = 6.022;
    static final double BOLTZNUT_CONSTANT = 1.38;
    static final double ELECTRO_MARKET = 9.109;
}
```

# Item 23. 태그 달린 클래스보다는 클래스 계층구조를 활용하라
```java
class Figure {
    enum Shape { RECTANGLE, CIRCLE };
    
    // tag field
    final Shape shape;
    
    // only for rectangle
    double length;
    double width;
    
    // only for circle
    double radius;
    
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }
    
    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }
    
    double area() {
        switch(shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```
태그 달린 클래스에는 단점이 많다
* switch 문, tag field, 열거 타입 등 쓸데 없는 코드가 많아 가독성이 떨어진다. 
* 또한 다른 의미를 위한 코드도 언제나 함께 하니 메모리도 많이 사용한다.
* 필드들을 final 로 선언하기 위해서는 해당 의미에서 사용하지 않는 field 까지 초기화해줘야한다, 이 때 엉뚱한 field 를 초기화하더라도 바로 발견되지 않고 런타임 중에 문제가 발생할 것이다.

태그 달린 클래스는 비효율적이고 오류를 내기 쉬우며 가독성도 떨어진다. 이는 클래스 계층 구조를 어설프게 따라한 아류에 불과하다.\
이제 태그 달린 클래스를 클래스 계층구조로 바꾸는 방법을 알아보자, 우선 태그에 따라 동작이 바뀌는 메서드를 찾아 추상 메서드로 정의한다. 그 후 태그와 상관없이 동작하는 메서드들을 부모 클래스에 일반 메서드로 추가한다(공통적으로 쓰이는 field 또한 마찬가지다).\

위의 예제에서는 공통적으로 쓰이는 필드나 메서드가 없다, 오직 area 만이 태크에 따라 동작이 변한다. 원칙에 따라 변경한 모습은 다음과 같다.
```java
abstract class Figure {
    abstract double area();
}

public class Rectangle extends Figure {
    private final double length;
    private final double width;
    
    private Rectangle() { }
    
    private Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }
    
    public static Rectangle valueOf(double length, double width) {
        return new Rectangle(length, width);
    }
    
    @Override
    public double area() {
        return length * width;
    }
}

public class Circle extends Figure {
    private final double radius;
    
    private Circle() { }
    
    private Circle(double radius) { this.radius = radius; }
    
    public static Circle valueOf(double radius) {
        return new Circle(radius);
    }
    
    @Override
    public double area() {
        return Math.PI * (radius * radius);
    }
}
```

# Item 24. 멤버 클래스는 되도록 static 으로 만들어라
중첩 클래스(nested class)는 자신을 감싼 바깥 클래스에서만 쓰여야하며, 그 외에 쓰임이 있다면 톱레벨 클래스로 만들어야한다.\
중첩 클래스의 종류로는 `static member class` `(non-static) member class` `anonymous class` `local class` 4가지가 있다. 이 중 첫번째를 제외한 나머지는 내부 클래스(inner class)에 해당한다.

> 정적 멤버 클래스는 바깥 클래스의 private 멤버에 접근 가능한 것을 제외하고는 일반 클래스와 똑같은며, 여타 다른 static member 와 같은 적용을 받는다. 예컨데 private 으로 선언될 경우 바깥 클래스에서만 접근 가능하다.\
> 정적 멤버 클래스는 바깥 클래스와 같이 쓰이면 유용한 Public 도우미 클래스로 쓰인다. 예를 들어 Calculator 와 Operator enum 을 생각해보자. Operator 를 public 정적 멤버 클래스로 쓰면 Calculator.Operator.PLUS 와 같은 형태로 연산을 참조 할 수 있다.

> 비정적 멤버 클래스는 암묵적으로 바깥 클래스와 연결된다. 따라서 정규화된 this([className].this) 로 바깥 클래스를 참조할 수 있으며 독립적으로 존재할 수 없다. 비정적 멤버의 경우 바깥 클래스에서 생성자를 호출해 자동으로 만드는 것이 일반적이지만 드믈게는 [Outer Class instance].new MemberClass(args) 로 호출해 수동으로 만들기도 한다.\
> 비정적 맴베 클래스는 어댑터를 정의할 때 자주 쓰인다. 즉 <U>클래스의 인스턴스를 감까 마치 다른 클래스의 인스턴스로 보이게하는</U> 뷰로 쓰인다.\
> 예로 Map interface 의 구현체들은 보통 컬렉션 뷰(`keySet` `entrySet` `values` 메서드가 반환하는)를 구현 할 때 비정적 멤버 클래스를 사용한다.\
> 멤버 클래스에서 <U>바깥 클래스를 참조할 일이 없다면 무조건 `static`을 붙여서 정적 멤버로 만들자.</U>
```java
import java.util.AbstractSet;

public class MySet<E> extends AbstractSet<E> {
    ... // 생략
    
    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }
    
    private class MyIterator implements Iterator<E> {
        ...
    }
}
```

> 익명 클래스는 바깥 클래스의 멤버가 아니며, 오직 비정적 문맥에서만 바깥 클래스의 인스턴스를 참조할 수 있다. 또한 `instanceOf`나 클래스의 이름이 필요한 작업은 수행 할 수 없다.

> 지역 클래스는 지역변수를 선언할 수 있는 곳에 선언할 수 있으며, 유효 범위 또한 지역 변수와 같다. 멤버 클래스 처럼 이름을 가질 수 있고, 익명 클래스 처럼 비정적 문맥에서만 바깥 클래스의 인스턴스를 참조할 수 있다.


# Item 25. 톱레벨 클래스는 한 파일에 하나만 담아라
하나의 소스파일에 여러 톱레벨 클래스를 선언하는 경우 컴파일에 따라 동작이 달라질 수 있다.
```java
public class Main {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}
```
두 개의 톱클래스가 Utensil.java 안에 있다 가정하자
```java
public class Utensil {
    static final String NAME = "pan";
}

public class Dessert {
    static final String NAME = "cake";
}
```
이제 우연히 같은 톱클래스를 가지는 Dessert.java 가 있다 가정하자
```java
public class Utensil {
 static final String NAME = "pot";
}

public class Dessert {
 static final String NAME = "pie";
}
```
`javac Main.java Dessert.java` 로 컴파일 한다면 Main 에서 먼저 선언된 Utensil 참조를 만나 Utensil.java 에서 두 클래스를 찾을 것이고, 이 후 Dessert.java 에서 같은 클래스를 발견하며 에러가 발생할 것이다.\
하지만 `javac Main.java` or `javac Dessert.java Main.java` 와 같이 컴파일 할 경우 에러는 발생하지 않고 서로 다른 결과를 출력할 것이다.