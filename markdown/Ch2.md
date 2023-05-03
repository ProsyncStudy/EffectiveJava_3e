- [Ch 2. 객체의 생성과 소멸](#ch-2-객체의-생성과-소멸)
- [Item 1. 생성자 대신 정적 팩터리 메서드를 고려하라](#item-1-생성자-대신-정적-팩터리-메서드를-고려하라)
  - [1-1. 생성자와 다르게 정적 팩토리 메소드는 이름을 가질 수 있다.](#1-1-생성자와-다르게-정적-팩토리-메소드는-이름을-가질-수-있다)
  - [1-2. 생성자와 다르게 메소드가 호출될 때마다 객체를 생성하지 않아도 된다.](#1-2-생성자와-다르게-메소드가-호출될-때마다-객체를-생성하지-않아도-된다)
    - [참고: 플라이웨이트 패턴](#참고-플라이웨이트-패턴)
  - [1-3. 생성자와 다르게, 반환타입에 객체의 하위타입들을 리턴할 수 있다.](#1-3-생성자와-다르게-반환타입에-객체의-하위타입들을-리턴할-수-있다)
  - [1-4. 파라미터에따라 다른 객체를 리턴시킬 수도 있다.](#1-4-파라미터에따라-다른-객체를-리턴시킬-수도-있다)
  - [1-5. 정적 팩토리 메소드를 통해 리턴받는 객체의 타입(클래스)은 반드시 존재할 필요가 없다.](#1-5-정적-팩토리-메소드를-통해-리턴받는-객체의-타입클래스은-반드시-존재할-필요가-없다)
  - [그렇다면 단점은 없나?](#그렇다면-단점은-없나)
- [Item 2. 생성자에 매개변수가 많다면 빌더를 고려하라](#item-2-생성자에-매개변수가-많다면-빌더를-고려하라)
- [Item 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라](#item-3-private-생성자나-열거-타입으로-싱글턴임을-보증하라)
- [Item 4. 인스턴스화를 막으려거든 private 생성자를 사용하라](#item-4-인스턴스화를-막으려거든-private-생성자를-사용하라)
- [Item 5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라](#item-5-자원을-직접-명시하지-말고-의존-객체-주입을-사용하라)
- [Item 6. 불필요한 객체 생성을 피하라](#item-6-불필요한-객체-생성을-피하라)
- [Item 7. 다 쓴 객체 참조를 해제하라](#item-7-다-쓴-객체-참조를-해제하라)
- [Item 8. finalizer와 cleaner 사용을 피하라](#item-8-finalizer와-cleaner-사용을-피하라)
  - [promptness 문제 (신속성?)](#promptness-문제-신속성)
  - [자원 해제 문제](#자원-해제-문제)
  - [실행 속도 문제](#실행-속도-문제)
  - [보안 이슈](#보안-이슈)
  - [AutoCloseable](#autocloseable)
  - [cleaner/finalizer가 사용이되는 경우?](#cleanerfinalizer가-사용이되는-경우)
  - [cleaner 예](#cleaner-예)
- [Item 9. try-finally보다는 try-with-resources를 사용하라](#item-9-try-finally보다는-try-with-resources를-사용하라)


# Ch 2. 객체의 생성과 소멸
# Item 1. 생성자 대신 정적 팩터리 메서드를 고려하라
클래스 내에 정적 인스턴스를 정의해서 해당 값을 리턴하는 메소드를 구현하면 좋다.
정적 인스턴스를 쓰면 객체를 생성하거나 소멸시키는 오버헤드를 없앨 수 있고, 다른 여러 장점들이 존재한다.

간단한 예로 Boolean과 같은 객체에 대해서 TRUE/FALSE를 만드는 것 보다 valueOf 같은 정적 팩토리 메소드로 값을 가져오는게 좋다.
예를 들면 아래와 같은 코드는
```java
Boolean b1 = new Boolean(true);
Boolean b2 = Boolean.valueOf(true);
```

정적 팩토리 메소드가 좋은 상세한 예는 아래 다섯가지 사례를 들어서 보도록 하자.

## 1-1. 생성자와 다르게 정적 팩토리 메소드는 이름을 가질 수 있다.
```java
// 만약 우리가 정적 팩토리 메소드를 쓰지 않고 생성자를 썼을 때.

//이거보단.
BigInteger bi = new BigInteger(int , int, Random);

//이게 더 가독성이 좋다. 뭘 하는지가 명확하다..?
BigInteger bi = BigInteger.probablePrime(int, Random);
```

객체에는 수없이 많은 방식으로 생성자를 오버로딩할 수 있는데,
파라미터 갯수와 타입에 따라 이를 구분하게되면 대체 무엇을 하고싶은 지 알 수가 없게된다.
(위 BigInteger 예에서, java 11 기준 생성자는 총 8개로 오버로딩 되어있다.)

따라서 이 때 이름을 제공해줌으로써 훨씬 명확하게 동작을 기술할 수 있게된다.

## 1-2. 생성자와 다르게 메소드가 호출될 때마다 객체를 생성하지 않아도 된다.
앞의 Boolean 예시와 동일한데, 아래의 예를 보도록 하자.

```java
log.info(Boolean.TRUE.hashCode());
log.info(Boolean.valueOf(true).hashCode());

log.info(Boolean.FALSE.hashCode());
log.info(Boolean.valueOf(false).hashCode());
```

이 코드는 아래와 같은 결과를 낸다. ( 객체 생성을 하지 않고 매번 같은 hashcode를 꺼내옴을 알 수 있다.)
```bash
ch2.Item1[BooleanTest] (   Item1.java:19) - 1231
ch2.Item1[BooleanTest] (   Item1.java:20) - 1231
ch2.Item1[BooleanTest] (   Item1.java:22) - 1237
ch2.Item1[BooleanTest] (   Item1.java:23) - 1237
```

이런 기법을 **instance-controlled** 라고 한다.
**instance-controlled** 는 싱글톤을 보장해주고, 인스턴스화되지 않음을 보장해주며,
equal과 ==이 필요충분조건이 될 수 있도록 해준다. Enum이 가장 대표적인 예시라고 볼 수 있다.

### 참고: [플라이웨이트 패턴](https://refactoring.guru/ko/design-patterns/flyweight)
각 객체에 모든 상태를 유지시키고 서로 간의 공통된 부분을 공유하는 디자인패턴

## 1-3. 생성자와 다르게, 반환타입에 객체의 하위타입들을 리턴할 수 있다.

Collection은 자바 상속의 대표적인 성공사례이다. Collection 인터페이스 메소드들로 모두 통일시켜서
객체 사용 시 개발자가 무언갈 외워야 하는 부담을 훨씬 줄여줬다.

그런데 이를 유연하게 사용하기 위해서 하위 타입들에 대해 Collection으로 받아주려면 아래의 코드대로 만들어야 한다.
```java
Collection c1 = new ArrayList<Integer>();
Collection c2 = new LinkedList<Integer>();
```

하지만 이는 보기에 조금 불편하다. 따라서 이를 정적 팩토리 메소드를 사용하여 아래와 같이 바꿀 수 있다.
```java
public class test1CollectionFactory {
  public static Collection makeLinkedList() {
      return new LinkedList<Integer>();
  }
  public static Collection makeArrayList() {
      return new ArrayList<Integer>();
  }
}

Collection c1 = test1CollectionFactory.makeLinkedList();
Collection c2 = test1CollectionFactory.makeArrayList();
```
이렇게 원하는 타입으로 리턴을 하여 사용하는게 가능하기 때문에 훨씬 유연하게 쓸 수 있다.

추가로 원래 Java8 이전엔 인터페이스가 정적 메소드를 가질 수 없었다고 한다.
하지만 8버전 이후부턴 이를 허용해 주었고, 이로인해 더더욱 유연성이 생겼다.
9 이후부터는 private static에 대해서도 허용해준다고 한다.

## 1-4. 파라미터에따라 다른 객체를 리턴시킬 수도 있다.
EnumSet은 public 생성자가 없고, static factory만 존재한다.
이 때, enum타입의 갯수에 따라 다른 객체를 리턴하게 되는데
64개 이하일 경우 ```RegularEnumSet``` 을 리턴하고
64개를 초과할 경우 ```JumboEnumSet``` 을 리턴한다고 한다.

하지만 이런 정보는 개발자에겐 보여지지 않는다.
따라서 Enum이 중간중간 늘어난다 해도 큰 상관이 없다.

## 1-5. 정적 팩토리 메소드를 통해 리턴받는 객체의 타입(클래스)은 반드시 존재할 필요가 없다.
서비스 제공자 프레임워크가 대표적인 예시라고 책에서는 소개한다. 가장 유명한 프레임워크로는 JDBC가 가 있겠다.

우리가 보통 쓰는 JDBC는 인터페이스를 가지고 사용하게 된다.
```Connection```이나 ```ResultSet``` 등등이 그 예시이다.

서비스 제공자 프레임워크에는 필수적인 구성요소들이 존재하는데,
1. 서비스 인터페이스 = ```java.sql.Connection```
2. 제공자 등록 API = ```DriverManager.registerDriver```
3. 서비스 접근 API = ```DriverManager.getConnection```

이 되겠다. 추가 구성요소로 ```Driver```를 서비스 제공자 인터페이스로써 네 번째 구성요소로 볼 수 있다.

여기서 유의깊게 볼 것은 **서비스 접근 API** 인데, getConnection 수행 시 우리는 우리가 구현하지 않은 Connection 인터페이스에 대한 객체를 얻어온다. 그리고 이를 편하게 사용한다.

티베로를 예로 들면, getConnection 수행 시 TbDriver 내에서 Connection 객체의 구현체인 TbConnection이 리턴되며, 우리는 표준에 맞는 함수들만 사용하면 내부 구조를 전혀 몰라도 사용에 지장이 없다.

## 그렇다면 단점은 없나?
우선 하위 클래스를 만들 수 없다.
정적 팩토리 메소드는 보통 private으로 관리되는 싱글톤 객체를 사용할텐데, 이는 위아래로 보이지가 않아 상속에 불리한 구조이다.
하지만 이는 경우에 따라 컴포지션을 유도할 수 있으므로 오히려 특정 코딩컨벤션에서는 장점이 될 수 있다.

그리고 협업할 때 서로서로 메소드를 공유해놓지 않으면 다른 개발자가 메소드를 찾기가 어렵다.
따라서 아래와 같이 보통 사용되는 메소드명을 쓰면 좋다.
```java
Object.from();
Object.to();
Object.valueOf();
Object.instance();
Object.getInstance();
Object.create();
Object.newInstance(Object input);

Object.getObject();
Object.newObject();
BufferedReader br = Files.newBufferedReader();
Object.Object();
```

# Item 2. 생성자에 매개변수가 많다면 빌더를 고려하라

아래와 같은 구조에서 자바 생성자를 사용하는 방법은 무엇이 있을까?
```java
public class NutritionFacts {
  private int servingSize = -1; // required
  private int servings = -1; // required
  private int calories = 0; // optional
  private int fat = 0; // optional
  private int sodium = 0; // optional
  private int carbohydrate = 0; // optional

  public NutritionFacts(int servingSize, int servings) {
    this(servingSize, servings, 0);
  };
  public NutritionFacts(int servingSize, int servings, int calories) {
    this(servingSize, servings, calories, 0);
  };
  public NutritionFacts(int servingSize, int servings, int calories, int fat) {
    this(servingSize, servings, calories, fat, 0);
  };
  public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium) {
    this(servingSize, servings, calories, fat, sodium, 0);
  };
  public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate) {
    this.servingSize = servingSize;
    this.servings = servings;
    this.calories = calories;
    this.fat = fat;
    this.sodium = sodium;
    this.carbohydrate = carbohydrate;
  };
}
```
보통 필요한 파라미터, 변경이 필요한 파라미터에 대해서 생성자를 만들어준다.
required 파라미터 + optional 파라미터의 조합으로 생성자를 만드는 것이다.
이를 **점층적 생성자 패턴 (Telescoping Constructor Pattern)** 이라고 한다.
하지만 이는 너무나 많은 생성자 메소드를 요구하고, 순서바뀌면 망한다.

두 번째 방법은 무엇이 있을까?
바로 자바 빈즈 패턴이다. 흔히 우리가 아는 getter/setter로 만든 녀석이 되겠다.

```java
// 롬복을 쓰면 아아~주 편하다.
@Data
public class NutritionFacts {
  private int servingSize = -1;
  private int servings = -1;
  private int calories = 0;
  private int fat = 0;
  private int sodium = 0;
  private int carbohydrate = 0;
}
```

하지만 이녀석도 완벽한 해결책이 될 수 없다.

예를들어 우리가 다른 setter는 다 썼지만 ```setFat```을 빼먹었다고 치자.
그런데 여기에는 default값이 있고 ,객체 생성은 아무런 이상이 없이 동작할 것이다.
만약 이로인해 치명적인 버그가 일어난 경우에 우리는 우리가 어떤 파라미터를 빼먹었는 지 알기가 어렵고, 이는 디버깅 난이도를 대폭 상승시키는 요인이 된다.

또한 자바빈즈는 불변 객체를 만들기가 애매하고, Thread Safety를 지켜주기 위해 우리가 추가적인 공수를 들여야 한다.

불변 객체의 경우 한번 set된 값에 대해선 setter 사용이 불가능하기 때문에, 이름과 다르게 메소드 사용이 유연하지 못하게되고 다중 Thread가 접근 가능한 객체라면 setter 내 혹은 synchronized(this)를 다 걸어주어야 할 것이다.

* 불변 객체란?
흔히 우리가 final로 태그 걸어서 사용하는 멤버객체들을 생각하면 좋다. 정적이 아닌 객체들은 힙영역에 객체를 생성될 것인데, 어떤 객체의 final이 달린 멤버 객체는 해당 힙 영역을 포인팅하게되면 그 포인팅 자체가 절대 변할 수가 없다.

이를 위한 마지막 패턴이 존재하는데, 이게 바로 빌더패턴이다.

사용자는 빌더라는 객체 내부의 또다른 객체를 호출하여 setter 비스무리한 녀석들로 변수들을 세팅해준다.
그리고 최종적으로 ``build`` 라는 메소드를 통해 객체를 생성하게 된다.

이렇게 생성된 객체는 대체로 불변 객체로 생성이 되며, 대게 static 멤버 메소드로 구현이 된다.
따라서 자바빈즈에서 문제가 되었던 불변 객체 문제 및 멀티스레드 문제가 해결이 된다.

```java
// 롬복을 쓰면 아아~주 편하다.
// 다만 일부 optional에 대해선 불변 객체 유지를 위해 점층적 생성자 패턴을 살짝 섞어줘야 한다.
// 다른 좋은 방법이 있을 수도?

@Builder
public class NutritionFacts3 {
  private final int servingSize;
  private final int servings;
  private final int calories;
  private final int fat;
  private final int sodium;
  private final int carbohydrate;

  private static NutritionFacts3Builder builder() {
    return new NutritionFacts3Builder();
  }

  public static NutritionFacts3Builder builder(
    int calories, int fat, int sodium, int carbohydrate) {
    return builder().calories(calories)
                    .fat(fat)
                    .sodium(sodium)
                    .carbohydrate(carbohydrate);
  }
}
```

또한 빌더패턴은 상속구조에서도 용이하다.

```java
public abstract class Pizza {
  public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }
  final Set<Topping> toppings;

  abstract static class Builder<T extends Builder<T>> {
    EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

    public T addTopping(Topping topping) {
      toppings.add(topping);
      return self();
    }

    abstract Pizza build();
    // Subclasses must override this method to return "this"
    protected abstract T self();
  }

  Pizza(Builder<?> builder) {
    toppings = builder.toppings.clone(); // See Item 50
  }
}

public class NyPizza extends Pizza {
  public enum Size { SMALL, MEDIUM, LARGE }
  private final Size size;

  public static class Builder extends Pizza.Builder<Builder> {
    private final Size size;

    public Builder(Size size) {
      this.size = size;
    }
    @Override public NyPizza build() {
      return new NyPizza(this);
    }
    @Override protected Builder self() { return this; }
  }

  private NyPizza(Builder builder) {
    super(builder);
    size = builder.size;
  }
}

NyPizza pizza = new NyPizza.Builder(SMALL)
.addTopping(SAUSAGE)
.addTopping(ONION)
.build();
```
Pizza를 상속받는 NyPizza는 빌더를 커스텀한 뒤 사용이 가능하다.

이 예시에선 Builder를 통해 사이즈를 정해주고, Pizza.Builder의 함수인 addTopping을 통해
빌더로 지정할 변수들을 추가한다. 그리고 마지막으로 빌드를 사용하여 NyPizza 생성자를 호출하고,
생성자 내에서 super를 통해 상위객체 생성까지 모두 완료해준다.


마지막으로, 클래스 디자인을 할 때 파라미터가 많은 객체라면 빌더를 그냥 사용하는게 좋다. 나중에 변경하려고 하면 굉장히 공수가 많이들어가고 개발자들끼리도 읽기에 무리가 없어서 좋다.

# Item 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라
싱글턴은 인스턴스가 정확히 한번만 되는 클래스를 의미한다. 싱글턴은 대게 stateless한 객체에 사용된다.

싱글턴은 테스트 할 때 mock 실행으로 대체가 불가능해서 테스트가 어렵다(?)
( 이 내용은 이해가 잘 안된다. )

싱글톤을 만드는 방법은 크게 두 가지가 있다.

아래는 첫 번째 방법이다.
```java
public class Elvis {
  public static final Elvis INSTANCE = new Elvis();
  private Elvis() {
  }
}
```
클래스 초기화 시점에 static final로 인해 오직 한 개의 인스턴스가 만들어짐이 보장된다.
하지만 권한이 있는 사용자에 의해
AccessibleObject.setAccessible 메소드를 사용하여 접근할 수도 있다고 한다.
이럴 땐 생성자에서 INSTANCE 객체가 만들어 졌는 지를 체크하여 throw처리를 해주면 된다.

두 번째 방법은 아래와 같다.
```java
public class Elvis2 {
  private static final Elvis2 INSTANCE = new Elvis2();
  private Elvis2() {
  }
  public static Elvis2 getInstance() {
    return INSTANCE;
  }
}
```
이 방법의 장점은 getInstance라는 이름을 사용하기 때문에 싱글톤임이 조금 더 명확하고, 만약 singletone이 아니라 INSTANCE가 추가가 된다거나 모종의 이유로 싱글톤을 벗어나고 싶을 때, getInstance 내부의 함수만 조금 고쳐주면 된다.

또한 메소드이기 때문에 제네릭을 사용해서 제네릭 싱글톤 팩토리로 만들 수도 있다는 점이다.

마지막 장점은 ```Supplier``` 로 사용이 가능하다는 점이다.


만약 Serializable을 implements하는 경우엔 readResolve를 반드시 추가하라.
readResolve를 추가해놓지 않으면 다른 사람이 상속받아 쓸 때 새 객체 생성 코드를 삽입할 가능성이 있다.

마지막 방법으로는 열거형 타입이 있다.
이 부분은 생략한다.

# Item 4. 인스턴스화를 막으려거든 private 생성자를 사용하라
java.lang.Math나 java.util.Arrays와 같은 클래스들은 인스턴스화 하려고 설계된 놈들이 아니다.
그저 내부에 있는 static method를 사용하기 위해 설계된 클래스이다.
만약 이런 클래스를 설계하고 있다면 인스턴스화를 막아야 한다.

인스턴스화를 막는 방법은 호출 자체도 막아야 하고 상속도 불가능하게 해야 한다.
이를 위해 private 생성자를 사용하면 된다.

```java
// Noninstantiable utility class
public class UtilityClass {
  // Suppress default constructor for noninstantiability
  private UtilityClass() {
  throw new AssertionError();
}
... // Remainder omitted
}
```

위와같이 선언을 해놓으면 상속도 불가능하고 외부에서의 인스턴스화도 불가능하다.
이를 이용해  static method들을 만들어 유틸리티 클래스를 사용하면 된다.

# Item 5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

스펠링을 체크하는 객체에 대한 두 예시를 보자.

예제1)
```java
// Inappropriate use of static utility - inflexible & untestable!
public class SpellChecker {
  private static final Lexicon dictionary = ...;
  private SpellChecker() {} // Noninstantiable
  public static boolean isValid(String word) { ... }
  public static List<String> suggestions(String typo) { ... }
}
```

예제2)
```java
// Inappropriate use of singleton - inflexible & untestable!
public class SpellChecker {
  private final Lexicon dictionary = ...;
  private SpellChecker(...) {}
  public static INSTANCE = new SpellChecker(...);
  public boolean isValid(String word) { ... }
  public List<String> suggestions(String typo) { ... }
}
```

스펠링 체크는 한 언어만 가지고 사용하지 않는다. (파파고가 영어,일본어 등의 맞춤법을 지원하듯)
첫 번째는 유틸리티 클래스를 통해 만들었지만 dictionary가 정적 불변이라 유연하게 사용하기 어렵다.
두 번째는 싱글톤인데, 싱글톤 생성 시에 dictionary가 정해져서 정적이 아님에도 유연하게 사용하기 어렵다.

따라서 싱글톤이나 유틸리티 클래스를 통해서 스펠링 체크 객체를 만드는건 쉽지 않다.
기능 특성 상dictionary에 SpellChecker가 의존하는 구조이기 때문이다.

이 때 사용하기 좋은게 의존성 주입(DI)이다. SpellChecker를 생성할 때 dictionary를 같이 주어 생성과 동시에 불변객체로 dictionary를 만들어준다.
```java
// Dependency injection provides flexibility and testability
public class SpellChecker {
private final Lexicon dictionary;
public SpellChecker(Lexicon dictionary) {
this.dictionary = Objects.requireNonNull(dictionary);
}
public boolean isValid(String word) { ... }
public List<String> suggestions(String typo) { ... }
}
```
이렇게 만들면 SpellChecker 객체도 여러벌 만들 수 있고, 불변이기 때문에 멀티 스레딩 환경에서도 모두 참조할 수 있다.

이는 앞에배운걸 응용하여 정적 팩토리로 만든다거나, 빌더로 만들 수도 있다.
앞에서 잠깐 나온 Supplier는 이런 DI에 유용하게 사용될 수 있다.

그런데 이와 같은 방법은 큰 프로젝트에서 DI로 인해 코드를 굉장히 지저분하게 만든다.
실제 현업에서는 수없이 많은 의존성이 생기기 때문이다.

따라서 DI는 DI관련 프레임워크를 쓰는게 좋다. (Dagger, Guice, **Spring**)

요약하면
1. 의존성이 있는 리소스에 대해서 싱글톤이나 유틸리티 클래스를 사용하는걸 지양하도록 하고,
2. 객체 생성시 의존성이 있는 리소스를 해당 객체가 다이렉트하게 만들지 말고 생성자에 전달하라.

# Item 6. 불필요한 객체 생성을 피하라
객체를 만들 때 재사용하는건 기깔나다.
불변 객체로 만들면 앞에서 언급했듯이 재사용도 쉽고 멀티스레드 환경에서도 유용하다.

다음과 같은 행위는 하지 않는게 좋다.
```java
String s = new String("bikini");
```
이 행위는 매번 완전히 동일한 똑같은 객체를 만드는게 확실한데도 매번 객체를 재생성해서 쓴다.
이런게 for문에 들어가있다면 아주 끔찍하다.

조금 더 나은 버전은 아래와 같다.
```java
String s = "bikini";
```
이 경우 String을 매번 생성하지 않고 재사용하는게 보장이된다. 또한 같은 라인이 아니더라도, JVM 상에서 동일한 문자열 ("bikini") 을 사용하는 String들에 대해서 모두 생성하지 않고 재사용하는게 보장된다.

이와 비슷하게 Boolean에서도
```java
Boolean bl1 = Boolean.valueOf(String) // Better
Boolean bl2 = Boolean(String) // Worse. deprecated in java9
```
정적 팩토리 메소드를 사용한 버전이 선호된다. (재사용하지 않음이 보장)

다른 예시를 들어보자. RomanNumeral을 체크하는 함수가 있다고 가정하자.
```java
// Performance can be greatly improved!
static boolean isRomanNumeral(String s) {
return s.matches("^(?=.)M*(C[MD]|D?C{0,3})"
+ "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
}
```
이 코드의 단점은 무엇일까? 그건 바로 matches 메소드 내부에 있다.
matches 내부로 들어가면 Patern 객체 자체를 생성한다. Patern객체는 사실상 FSM을 하나 만드는 것과 같기때문에 그 비용은 상당하다.

따라서 이를 한 번만 만들어주고 재사용하는게 좋다.

```java
public class RomalNumeral2 {
  private static final Pattern ROMAN = Pattern.compile(
    "^(?=.)M*(C[MD]|D?C{0,3})"
    + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");
    static boolean isRomanNumeral(String s) {
    return ROMAN.matcher(s).matches();
    }
}
```
위와 같은 코드 사용 시, Pattern을 정적 불변으로 만들어주었기 때문에 매번 생성할 필요가 없다.
따라서 이렇게 쓰는게 성능에 훨씬 좋다.

코드가 아예 사용되지 않을 때를 대비해 **지연 생성**을 할 수도 있는데 이는 그닥 권장하지 않는다.
딱히 속도면에서 이점도 없을 뿐더러 코드만 복잡해진다.

객체가 불변이면 재사용되는건 명확해보인다. 다만 어떤 상황에선 명확하지 않아보이기도 한다.
예를 들면 Map 인터페이스의 keySet 메소드를 생각해보자.
```java
Map mp = new HashMap<Integer,String>();
for (int i = 0; i < 1000; i++) {
   mp.put(i, "" + i);
}

log.info(mp.keySet().hashCode());
```
위 코드에서 keySet은 실제 mp의 key들을 Set에 모아 리턴을 할까? 아니면 mp의 키들 그 자체를 리턴할까?
만약 mp에서 참조하는 녀석들을 리턴한다면 그 Set이 변경되었을 때 어떤 일이 일어날까?

정답은 mp의 keySet에 대한 실제 Set을 리턴하고, Set에 변화가 일어나면 mp 전반에 영향을 미친다.
이런 경우는 우리의 직관적인 이해와 살짝 다른 동작을 하게된다. 마치 새로운 Set을 얻어온 것 같지만 실제론 그렇지 않다. 이런 경우는 아무런 장점도 없고 버그 유발이 가능하니 유의하라.

마지막 예시로는 오토박싱이 존재한다. 아래 예를 보자.
```JAVA
private static long sum() {
  Long sum = 0L;
  for (long i = 0; i <= Integer.MAX_VALUE; i++)
    sum += i;
  return sum;
}
```
이 코드의 단점은 무엇일까?

정답은 ```sum += i```에 있다. sum은 Long타입이고 i는 long이다. primitive타입은 object타입으로 오토박싱된다. 즉, Long 생성자가 매 반복마다 호출되게 된다. 이런 경우도 반드시 피해야 하는 케이스이다.
따라서 박싱된 기본타입을 적당히 피하는게 좋다. 사용을 한다면 의도치않게 숨어드는 경우를 항상 조심하자.

객체의 풀에 관해서 웬만하면 객체풀을 사용하지 말아라. 아~주 무거운 객체가 아니면 만들지 말아라.
DB Connection같은건 오래걸려서 재사용할 가치가 있지만, 그렇지 않은 경우 메모리 사용량을 상당히 늘리게 되고 성능에 악영향을 준다.

최신의 JVM들은 매우 최적화된 가비지 콜렉터가 있어서 당신이 만드는 객체풀보다 대부분 잘 동작할 거다.
( 아마 명시적으로 해제만 잘해주면 될듯? )

추가로 Item 50은 새 객체를 만들 때 기존 객체 재사용을 하지 말라는 거고, 이번 장은 기존 객체를 재사용 할 때, 불필요하게 객체생성되는걸 방지하라는 장이다. 기존 객체 재사용을 defensive copying이라고 하는 것 같은데, 여기서 실패하면 매우 큰 버그가 유발될 수 있으니 사용하지 말라는 것 같다.


# Item 7. 다 쓴 객체 참조를 해제하라
c / c++쓰다가 자바로 넘어와서 가비지 콜렉터 맛좀 보면 아주 달다. 아주 똑똑한 친구가 알아서 모든걸 잘 관리할 것 같은데 실상은 그렇지는 않다.

다음 코드를 보자.
```java
public class Stack {
  private Object[] elements;
  private int size = 0;
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  public Stack() {
    elements = new Object[DEFAULT_INITIAL_CAPACITY];
  }

  public void push(Object e) {
    ensureCapacity();
    elements[size++] = e;
  }

  public Object pop() throws Exception {
    if (size == 0)
      throw new Exception("size 0");
    return elements[--size];
  }
}
```
걷보기에는 크게 문제가 없어보인다. 하지만 elements에 해당하는 객체의 참조가 사라지질 않아 GC에 반환이 되질 않는다.

누군가 push를 통해 객체를 넣고 곧이어 pop을 통해 객체를 더이상 참조하지 않으려 해도, elements 배열 안에 이미 해당 객체를 참조 중이고 Stack의 인스턴스가 해당 배열을 계속해서 들고있으므로 size가 줄었다 한들 참조 자체가 제거되지가 않는다.

이를 방지하기 위해 pop코드를 아래와 같이 변경해야 한다.
```java
  public Object pop_new() throws Exception {
    if (size == 0)
      throw new Exception("asdf");
    Object result = elements[--size];
    elements[size] = null;
    return result;
  }
```
이렇게 할 경우 GC에게 명시적으로 해당 객체를 참조하지 않겠다고 말하게 된다.
혹시라도 외부에서 해당 객체를 보고있다고 하더라도 NPE가 발생한다.

할당 해제를 안하면 NPE가 발생하지 않으니 더 좋은게 아니냐고 생각할 수도 있지만, 예기치 못한 에러가 누적되어 프로그램이 비정상 종료되는 것 보다 NPE자체를 핸들해서 빠르게 리포트를 받는게 훨씬 좋은 구조다.

사실 모든 객체에 대해 참조가 끝났다고 해서 nulled out시킬 필요는 없다. 보통의 경우 변수가 스코프를 벗어나면 알아서 nulled out 될 것이고 이런 변수들은 관리할 필요가 없다.

다만 위의 Stack 처럼 자기가 메모리를 관리하는 객체들이 문제이다. 이 객체는 스스로가 스토리지에 대한 풀을 관리하도록 짜여져 있어서 GC에게 명시적으로 nulled out되었음을 알려야만 한다.


다른 메모리 누수의 주범으로는 **캐싱**이 있다.
KeyValue 해시맵을 캐시처럼 사용할 경우, 사용되지 않는 수많은 객체들이 해제되지 않은 채로 메모리에 달려있을 수 있다.

이럴 때 사용하기 좋은게 WeakHashMap이다. Weak가 붙은건 컨테이너에 저장한 객체가 할당해제될 경우 컨테이너에서도 사라지는 놈들이다.


마지막으론 리스너와 콜백이다.
소켓 통신을 예로 들면, 소켓에 대해 이벤트 리스너를 달았는데 그에 대한 처리를 제때 하지 않으면 메모리가 언젠간 터진다. 실제 예시로 java.nio.pipe에 데이터를 미친듯이 밀어넣고 꺼내가지 않으면 터진다.

# Item 8. finalizer와 cleaner 사용을 피하라
finalizer는 예상 불가능하고, 종종 위험하기도 하며 일반적으론 불필요하다.
일단 성능이 좋지 않고 에러를 유발시키는 원인이 되기도 하다. 유효한 경우도 있지만 이건 후술하도록 하겠다.

이렇듯 finalizer가 좋지 않기 때문에 deprecated 되고, Java 9부턴 cleaner 라는 이름으로 새로 등장했다.
하지만 여전히 성능이 좋지 않고, 예상불가능하며 일반적인 경우엔 불필요하다.

C++의 생성/소멸자는 서로 대응되며, 객체 파괴가 필요할 땐 스코프를 벗어나면 알아서 해제되는 경우도 많다.
하지만 java의 finalize는 대응되지 않고, 명시적으로 finalize를 추가하여도 GC가 수거할 때 까지 기다려야 한다.
C++에서 소멸자는 또한 명시적으로 nonmemory 영역에 대한 리소스도 반환받아오는데, try-with-resources나 try-finally 블락들이 그 역할을 한다.

## promptness 문제 (신속성?)
계속해서 단점들을 나열해보면 finalizer와 cleaner는 우선 빨리 끝난다는 보장이 없다. 실제 참조가 끝났다 하더라도 메모리 자원이 반환되는 데 까지 꽤 긴 시간이 걸릴 수도 있다. 따라서 개발을 할 때 시간이 많이걸릴 것 같은 작업은 finalizer나 cleaner에 넣어선 안된다.

예를 들어, file close를 finalizer에게 맡기는건 위험하다. 왜냐면 실제로 언제 자원을 반환하고 close할 지 모르는데, fd 자체는 보통 프로그램에 제한이 걸려있기 때문이다. (보통 1024개) 이런 경우에 자원 반환이 늦어지면 fd가 꽉차서 더이상 파일을 열 수 없게된다.

또한 GC 알고리즘에 따라 finalizer의 실행 시간이나 주기가 다르기 때문에 고객사에 나갔을 때 큰 결함이 생길 수도 있다.

늦은 Finalization은 이론적은 데에서만 그치지 않는다. GUI를 포함하고있는 어떤 툴에서 OutOfMemoryError 이슈가 발생하게 되면 원인을 알 수 없이 GUI가 죽어버린다.
분석 결과 수없이 많은 그래픽 object들이 Finalizer에서 해제되지 못한 채로 큐잉되어 기다리고 있다 하더라도, 이걸 우리가 강제할 방법이 없다.
유일한 방법은 finalizer를 피하는 것이다.

cleaner가 이보다 좀 더 낫다곤 해도 여전히 통제가 불가능하기 때문에 이것도 피하는게 좋다.

## 자원 해제 문제
즉시 동작하지 않는다는 단점 외에도 전혀 동작하지 않을 가능성도 가지고있다. 따라서 JDBC Connection 같이 공유하기 좋은 자원에 대한 락 해제를 finalizer에 넣을 경우, 분산시스템에서 프로그램이 멈추기 딱 좋다.

gc를 호출하는 함수들에도 유혹되어선 안된다. System.gc()나 System.runFinalization 같은 메소드를 써도 GC가능성을 올릴 뿐, GC가 즉시 돈다는걸 보장하지 않는다.

또한 finalization 도중 발생한 에러는 무시당하며, 해당 객체에 대한 finalize는 제거된다. 따라서 해제되지 않고 참조도 되지 않는 객체들이 다른 곳에서 사용하려고 하면 예기치 못한 버그가 발생한다.
그리고 보통은 에러발생 시 에러스택을 까서 확인이 가능한데, finalizer가 멈추거나 안에서 Exception이 발생할 경우 캐치가 안된다.

## 실행 속도 문제
그리고 finalizer나 cleaner 없이 try with resources를 사용하면 훨씬 빠르게 작업이 진행된다. 같은 GC를 호출하는데에도 차이가 나는 이유는 finalizer같은 경우, GC의 효율적인 동작 자체를 억제한다. 따라서 훨씬 느리다.

## 보안 이슈
finalizer 어택이라는게 있다. 생성자나 직렬화 과정에서 throw가 발생하면, 하위 클래스의 finalizer가 비정상적으로 호출될 수 있다.
즉, 나는 생성하다가 exception을 맞은 것인데 비정상동작을 하게된다. 이 과정에서 static 영역에 되다만 객체에 대한 참조값이 들어가 수 있고,
이는 GC가 수거해 가는 것을 방해한다.

따라서 이딴거 쓰지말고 throw나 적절히 날려주면 그만이다. finalize는 제어하기가 너무 어렵다.
만약 반드시 쓰고싶다면 finalizer에 final 태그를 붙여서 하위 클래스에서 사용할 수 없도록 하라.

## AutoCloseable
try with resources 테크닉이랑 같이쓰이는 녀석이다. 바로 다음 장에 나온다.
AutoCloseable을 implements한 객체에 대해 close를 구현하면, try 블록을 탈출 시 자동으로 close가 불린다.
close에서는 대게 해당 객체가 더이상 유효하지 않음을 기록한다. (null로 변경하거나 JDBC의 경우 Connection close 등을 통해 자원을 반환)

객체가 유효하지 않음을 필드에 기록하고, 다른 메소드에서 이를 체크하면 좋다. 만약 이미 유효하지 않음이 체크가 되었다면 IlligalStateException 을
내보내면 된다.

## cleaner/finalizer가 사용이되는 경우?
크게 두 가지가 있다.
1. 사용자가 close를 사용하지 않을 경우. (못할 경우?)
이런 경우엔 소멸자(cleaner, finalize)를 정의해주는게 좋다.
강제로 만들 지 않는 이상 이런 케이스가 있는 진 모르겠다.

2. native peer를 사용할 경우
자바 객체가 네이티브 메서드를 통해 기능을 위임시킨 객체를 말한다.
이런 객체는 GC가 관리하는 자바객체가 아니어서 존재를 알지 못한다. 따라서 이럴 땐 cleaner로 제거해주어야 한다.

## cleaner 예
```java
// An autocloseable class using a cleaner as a safety net
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // Resource that requires cleaning. Must not refer to Room!
    private static class State implements Runnable {
        int numJunkPiles; // Number of junk piles in this room

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        // Invoked by close method or cleaner
        @Override public void run() {
        System.out.println("Cleaning room");
        numJunkPiles = 0;
        }
    }

    // The state of this room, shared with our cleanable
    private final State state;

    // Our cleanable. Cleans the room when it’s eligible for gc
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    @Override public void close() {
        cleanable.clean();
    }
}
```
close를 명시적으로 호출하거나 try with resource를 통해 네이티브 피어에 해당하는 State를 제거할 수 있다.
Room은 State의 주소에 해당하는 Long값을 불변값으로 들고있을 것이고, 이는 GC가 알 수 없기 때문에 우리가 제거해야만 한다.

네이티브 피어 State가 Room에 해당하는 정보를 가지고 있지 않음을 유의하자. 만약 그랬다면 circular한 참조가 생겨 GC가 제거할 수 없었을 것이다.
따라서 State는 **static nested** 여야만 한다. 왜냐면 nonstatic한 녀석들은 그들 인스턴스에 대한 참조값을 포함하고 있기 때문이다.

# Item 9. try-finally보다는 try-with-resources를 사용하라

try - finally로 자원해제를 하면 아래와 같을 것이다.
```java
// try-finally - No longer the best way to close resources!
static String firstLineOfFile(String path) throws IOException {
  BufferedReader br = new BufferedReader(new FileReader(path));
  try {
    return br.readLine();
  } finally {
    br.close();
  }
}
```

그런데, 자원이 하나 더 늘어나서 해제할 자원이 2개라면 아래와 같이 동작한다.
구조가 매우 지저분하다.
```java
// try-finally is ugly when used with more than one resource!
static void copy(String src, String dst) throws IOException {
InputStream in = new FileInputStream(src);
  try {
    OutputStream out = new FileOutputStream(dst);
    try {
      byte[] buf = new byte[BUFFER_SIZE];
      int n;
      while ((n = in.read(buf)) >= 0)
        out.write(buf, 0, n);
    } finally {
      out.close();
    }
  } finally {
  in.close();
  }
}
```

try - finally의 단점을 firstLineOfFile 코드를 예로 들어 보면, br.readline() 시에 파일에 문제가 있어서 throw를 날릴 경우,
br.close()에서도 똑같이 throw가 발생할 것이다. 그런데 이에 대한 에러처리는 되어있지 않다.

try-with-resources을 사용하면 이에 대해 손쉽게 처리가 가능하다.
각각 첫 / 두 번째 예제에 대해서 변경한 코드이다.
```java
// try-with-resources on multiple resources - short and sweet
static void copy(String src, String dst) throws IOException {
  try (InputStream in = new FileInputStream(src);
      OutputStream out = new FileOutputStream(dst)) {
    byte[] buf = new byte[BUFFER_SIZE];
    int n;
    while ((n = in.read(buf)) >= 0)
      out.write(buf, 0, n);
  }
}
```

```java
// try-with-resources - the the best way to close resources!
static String firstLineOfFile(String path) throws IOException {
  try (BufferedReader br = new BufferedReader(
                                new FileReader(path))) {
    return br.readLine();
  }
}
```

위의 firstLineOfFile를 변형해 try - catch 또는 try - finally도 가능하다. 물론 close가 호출됨은 알아서 보장이 된다.
```java
// try-with-resources with a catch clause
static String firstLineOfFile(String path, String defaultVal) {
  try (BufferedReader br = new BufferedReader(new FileReader(path))) {
    return br.readLine();
  } catch (IOException e) {
    return defaultVal;
  }
}
```