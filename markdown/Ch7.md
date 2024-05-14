- [Item 42. 익명 클래스보다는 람다를 사용해라](#item-42-익명-클래스보다는-람다를-사용해라)
- [Item 43. 람다보다는 메서드 참조를 사용해라](#item-43-람다보다는-메서드-참조를-사용해라)
- [Item 44. 표준 함수형 인터페이스를 사용해라](#item-44-표준-함수형-인터페이스를-사용해라)
- [Item 45. 스트림은 주의해서 사용해라](#item-45-스트림은-주의해서-사용해라)
- [Item 46. 스트림에서 부작용 없는 함수를 사용해라](#item-46-스트림에서-부작용-없는-함수를-사용해라)
- [Item 47. 반환 타입으로는 스트림보다 컬렉션이 낫다.](#item-47-반환-타입으로는-스트림보다-컬렉션이-낫다)
- [Item 48. 스트림 병렬화는 주의해서 사용해라](#item-48-스트림-병렬화는-주의해서-사용해라)

> 명확성, 간결성, 가독성
>
> 익명클래스 -> 람다 -> 메소드 참조 -> 표준형 함수 인터페이스
>
> 스트림에서도

# Item 42. 익명 클래스보다는 람다를 사용해라

> 함수객체 -> 익명함수 -> 람다
>
> 함수형 객체를 구현할 때 1)익명 클래스 2)람다를 이용해서 구현이 가능하고
> 익명클래스를 이용하면 함수 재정의 및 인스턴스 생성의 과정이 필요
> 람다를 이용하는게 가독성, 간결성에서 이득이 있다. 그러므로 람다를 쓰자


자바에서 함수 타입을 표현할때 **추상메소드 하나만 담은 인터페이스**(함수형 인터페이스)를 사용하는데, 이런 인터페이스의 인스턴스를 함수객체라고 하여 특정 함수나 동작을 나타나는데 사용했고, jdk1.1에서의 함수객체는 주로 익명클래스를 통해 만들었다. 아래는 익명클래스로 함수객체를 만드는 예제이다.

```java
@Test
public void 익명클래스로_함수객체_만들기() throws Exception {
        List<String> words = new ArrayList<>();
        Collections.sort(words, new Comparator<String>() {
@Override
public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
        }
        });
        }
```

위 방식은 코드가 너무 길기 때문에 자바는 함수형 프로그래밍에 적합하지 않았지만, 자바8부터 **함수형 인터페이스**가 특별한 의미를 인정받고, 람다로 함수형 인터페이스의 인스턴스를 만들 수 있게 됐다. 아래는 람다로 함수객체를 만드는 예제이다

```java
@Test
public void 람다로_함수객체_만들기() throws Exception {
        List<String> words = new ArrayList<>();
        Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
        }
```

익명클래스보다 간결하고 명확하다.

람다의 인자와 반환값의 타입은 각각 String, String, Comparator<String>이지만 코드에는 언급이 없는데, 이는 컴파일러가 타입을 추론해준 것이다.

타입 추론 관련 내용은 너무 복잡하고 알필요도 없으니, **1)타입을 명시해야 더 명확한 경우**나 **2)컴파일러가 타입을 알수없다고 하는 경우**를 제외하고는 생략하자.

cf) 타입추론에 필요한 타입 정보를 대부분 제네릭에서 얻으므로, ch5에서 얘기한것처럼 로타입이 아닌 제네릭, 제네릭 메소드를 사용하자.

람다 대신 비교자 생성 메소드를 사용하면 더 간결하다.

```java
@Test
public void 정적비교자생성메소드로_함수객체_만들기() throws Exception {
        List<String> words = new ArrayList<>();
        Collections.sort(words, comparingInt(String::length));
        }
```

list에 추가된 sort를 이용하면 더욱 간결하다.

```java
@Test
public void 리스트의_sort_이용() throws Exception {
        List<String> words = new ArrayList<>();
        words.sort(comparingInt(String::length));
        }
```

람다를 언어레벨에서 지원하면서 함수객체를 실용적으로 사용할 수 있게 됐다.

apply의 동작이 상수마다 달라야하는 Enum 구현시 1)클래스 바디에서 재정의 2)람다를 비교해보자

```java
// 1)클래스 body
public enum OP_클래스body {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    },
    TIMES("*") {
        public double apply(double x, double y) { return x * y; }
    },
    DIVIDE("/") {
        public double apply(double x, double y) { return x / y; }
    };
    private final String symbol;

    OP_클래스body(String s) { this.symbol = s; }
    public abstract double apply(double x, double y);
}
```

```java
// 2)람다
public enum OP_람다 {
    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final DoubleBinaryOperator op;

    OP_람다(String s, DoubleBinaryOperator op) {
        this.symbol = s;
        this.op = op;
    }

    public double apply(double x, double y) {
        return op.applyAsDouble(x, y);
    }
}
```

**보통은 람다가 더 간결하지만, 함수형 인터페이스의 인스턴스를 만들때만 쓰고 람다를 쓰면 안되는 경우에는 익명클래스를 사용하자.**

람다를 피해야하는 경우는 아래와 같다.

1. 이름도 없고 문서화도 못하므로 코드 자체로 명확히 설명이 안되거나 코드 줄 수가 많아지면(길어야 세줄) 람다를 쓰면 안된다
2. enum의 생성자에 넘겨지는 람다의 타입도 컴파일타임에 추론되므로 람다에서 enum의 인스턴스 멤버로의 접근은 불가능하다(인스턴스는 런타임에 만들어지므로)
3. 람다를 쓸 수 없는 경우(추상클래스의 인스턴스 생성, 추상메소드가 여러개인 인터페이스의 인스턴스 생성, 자기자신(this)을 참조해야하는 경우)는 익명클래스를 써야한다.

# Item 43. 람다보다는 메서드 참조를 사용해라

> 람다보다 더 간결하게 구현할 수 잇는 메서드 참조
> 메서드 참조의 유형

임의의 key와 그 key에 맵핑되는 integer를 관리하는 코드를 짜보자

```java
@Test
public void 람다_이용() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        String key = null;
        map.merge(key, 1, (count, incr) -> count + incr);
        }
```

꽤 깔끔하지만 `count`, `incr`는 크게 하는일 없이 공간만 차지한다. 메소드 참조를 이용해보자

```java
@Test
public void 메소드참조_이용() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        String key = null;
        map.merge(key, 1, Integer::sum); // (정적메소드 참조)
        }
```

매개변수가 늘어날수록 메소드 참조로 제거되는 코드양도 늘어난다. 그러나 매개변수 이름 자체가 프로그래머에게 좋은 가이드가 되기도 한다.

그래도 보통 메소드 참조를 사용하는 편이 더 짧고 간결하므로, 람다를 메소드로 빼고, 문서화를 남겨놓고, 메소드 참조를 이용하자.

때로는 람다가 메소드 참조보다 간결한 경우가 있는데 주로 같은 클래스 안에 메소드와 람다가 있는 경우다.

```java
@Test
public void 메소드참조보다_람다가_간결하다() throws Exception {
        // GoshThisClassNameIsHumongous 라는 클래스가 있고
        // 그 안에 action이라는 메소드가 있을때

        // 1) 메소드 참조
        service.execute(GoshThisClassNameIsHumongous::action);
        // 2) 람다
        service.execute(() -> action());
        }
```

메소드 참조의 유형은 다섯가지가 있다.

1. 정적 메소드를 가르키는 메소드 참조
2. 수신객체를 특정하는 한정적 인스턴스 메소드 참조
3. 수신객체를 특정하지 않는 비한정적 인스턴스 메소드 참조
4. 클래스 생성자를 가르키는 메소드 참조
5. 배열 생성자를 가르키는 메소드 참조

| 메소드 참조 유형  | 예                      | 같은 기능을 하는 람다                                            |
|------------|------------------------|---------------------------------------------------------|
| 정적         | Integer::parseInt      | str -> Integer.parseInt(str);                           |
| 한정적(인스턴스)  | Instant.now()::isAfter | Instant then = Instant.now();<br/>t -> then.isAfter(t); |
| 비한정적(인스턴스) | String::toLowerCase    | str -> str.toLowerCase();                               |
| 클래스 생성자    | TreeMap<K,V>::new      | () -> new TreeMap<K,V>();                               |
| 배열 생성자     | int[]::new             | len -> new int[len];                                    |


**보통은 메소드 참조를 쓰되, 람다가 더 간결,명확하다면 람다를 쓸 것**

# Item 44. 표준 함수형 인터페이스를 사용해라

> API 제공자의 입장.. 우리와는 거리가 먼듯한..

람다를 지원하면서 API의 모범 사례도 변경됐는데, 예를 들어 템플릿 메소드 패턴 대신 함수객체를 받는 정적 팩토리나 생성자를 제공하는 것

일반화 해서 말하면 함수 객체를 매개변수로 받는 생성자,메소드를 더 많이 만들어야 하고 함수형 매개변수 타입을 올바르게 선택해야 한다.

LinkedHashMap을 예시로 보자

```java
// LinkeHashMap은 내부적으로 put 이후 removeEldestEntry를 호출해서 true면 eldest를 삭제한다
public class ExtendedLinkedHashMap1<K, V> extends LinkedHashMap<K, V> {
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > 5;
    }
}

    @Test
    public void 개수제한map_removeEldestEntry_재정의() throws Exception {
        LinkedHashMap<Integer, Integer> map = new ExtendedLinkedHashMap1<>();

        for (int i = 0; i < 10; i++) {
            map.put(i, i);

            System.out.println("-----------size: " + map.size());
            map.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }
    }
```

removeEldestEntry를 재정의 함으로써 map의 size가 5개로 유지하도록 했다.

함수형 인터페이스를 정의하고, 생성자로 함수형 인터페이스를 인자로 받고, 인자를 넘길때 람다로 넘기는것도 가능하다.

```java
@FunctionalInterface
interface EldestEntryRemovalFunction<K, V> {
    boolean remove(Map<K, V> map, Map.Entry<K, V> eldest);
}

public class ExtendedLinkedHashMap2<K, V> extends LinkedHashMap<K, V> {
    private final EldestEntryRemovalFunction<K, V> predicate;

    public ExtendedLinkedHashMap2(EldestEntryRemovalFunction<K, V> predicate) {
        this.predicate = predicate;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return predicate.remove(this, eldest);
    }
}

    @Test
    public void 개수제한map_함수형인터페이스_정의() throws Exception {출
        // eldest는 LinkedHashMap에서 head
        // 1) size 5로 유지
        LinkedHashMap<Integer, Integer> map1 = new ExtendedLinkedHashMap2<>((m, eldest) -> m.size() > 5);

        System.out.println("-----map1");
        for (int i = 0; i < 10; i++) {
            map1.put(i, i);

            System.out.println("-----------size: " + map1.size());
            map1.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }

        // 2) size 1로 유지
        LinkedHashMap<Integer, Integer> map2 = new ExtendedLinkedHashMap2<>((m, eldest) -> m.size() > 1);

        System.out.println("-----map2");
        for (int i = 0; i < 10; i++) {
            map2.put(i, i);

            System.out.println("-----------size: " + map2.size());
            map2.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }
    }
```

하지만 위에서 정의한 `EldestEntryRemovalFunction`와 같은 모양인 `BiPredicate`가 이미 표준함수형인터페이스로 정의되어있고, 이를 활용하면 아래와 같다.

```java
public class ExtendedLinkedHashMap3<K, V> extends LinkedHashMap<K, V> {
    private final BiPredicate<Map<K, V>, Map.Entry<K, V>> predicate;

    public ExtendedLinkedHashMap3(BiPredicate<Map<K, V>, Map.Entry<K, V>> predicate) {
        this.predicate = predicate;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return predicate.test(this, eldest);
    }
}

    @Test
    public void 개수제한map_표준함수형인터페이스_정의() throws Exception {
        // eldest는 LinkedHashMap에서 head
        // 1) size 5로 유지
        LinkedHashMap<Integer, Integer> map1 = new ExtendedLinkedHashMap3<>((m, eldest) -> m.size() > 5);

        System.out.println("-----map1");
        for (int i = 0; i < 10; i++) {
            map1.put(i, i);

            System.out.println("-----------size: " + map1.size());
            map1.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }

        LinkedHashMap<Integer, Integer> map2 = new ExtendedLinkedHashMap3<>((m, eldest) -> m.size() > 1);

        System.out.println("-----map2");
        for (int i = 0; i < 10; i++) {
            map2.put(i, i);

            System.out.println("-----------size: " + map2.size());
            map2.forEach((k, v) -> {
                System.out.println("key: " + k + ", value: " + v);
            });
        }
    }
```

직접 구현하기 보다는 표준함수형인터페이스를 사용하는것이 좋으며 아래의 장점이 있다.
1. 다뤄야 하는 API의 수가 줄어든다
2. 유용한 default 메소드를 제공한다
3. 다른 코드와 상호운용성(호환성?)도 좋아진다.

`java.util.function` 패키지에는 많은 인터페이스가 있지만 6개만 기억해두면 나머지는 유추할 수 있다.

아래는 6개의 기본 함수형 인터페이스를 정리한 표이다.

| 인터페이스              | 함수 시그니처             | 예                   | desc                        |
|--------------------|---------------------|---------------------|-----------------------------|
| UnaryOperator\<T>  | T apply(T t)        | String::toLowerCase | 인수가 1개이며 반환값과 인수의 타입이 같은 함수 |
| BinaryOperator\<T> | T apply(T t1, T t2) | BigInteger::add     | 인수가 2개이며 반환값과 인수의 타입이 같은 함수 |
| Predicate\<T>      | boolean test<T t>   | Collection:isEmpty  | 인수 하나를 받아 boolean을 반환하는 함수  |
| Function\<T,R>     | R apply(T t)        | Arrays:asList       | 인수와 반환타입이 다른 함수             |
| Supplier\<T>       | T get()             | Instant::now        | 인수를 받지 않고 반환값을 제공하는 함수      |
| Consumer\<T>       | void accept(T t)    | System.out::println | 인수를 받고 반환값을 제공하지 않는 함수      |

인수와 반환타입을 조합해서 다른 기본 함수형 인터페이스들의 네이밍을 설명하는데 이는 굳이 알아야 하나 싶어서 넘어가겠습니다.

기본함수형인터페이스에 박싱된 기본타입은 사용하지 말 것(성능상 문제가 있을 수 있으므로)

`Comparator<T>`를 생각해보면 `BiFunction<T,U>`와 형태가 동일한데 그럼에도 `Comparator<T>`가 살아남는 이유는 아래와 같다
1. API에서 자주 사용되는데 이름이 용도를 훌륭히 설명한다
2. 구현하는쪽에서 반드시 지켜야 할 규약을 담고있다
3. 비교자들은 반환하고 조합해주는 유용한 디폴트 메소드들을 담고있다

즉, 위의 3가지 특성 중 하나 이상을 만족한다면 전용 함수형 인터페이스의 구현을 고려해볼하만다.

전용 함수형 인터페이스를 구현한다면 `@FunctionalInterface`를 달아줘야 하며 이는 3가지 이유가 있다.
1. 사용자에게 해당 인터페이스가 람다용으로 설계됨을 알려준다
2. 추상메소드가 오직 하나만 있어야 컴파일 되게 해준다
3. 이는 유지보수에 있어 실수로 메소드를 추가하지 못하게 막는다.

마지막으로 함수형 인터페이스를 API에서 사용할때의 주의점으로 같은 위치에 서로 다른 함수형 인터페이스를 받는 메소드들을 다중 정의해서는 안된다. 이는 API를 사용하는 사용자 입장에서 모호함이 생기며, 실제로 `ExecutorService.submit`은 `Callable<T>`을 받는것과 `Runnable`를 받는것을 다중정의를 했으며, 이는 사용자 입장에서 형변환으 해야되는 경우가 왕왕 생긴다.

# Item 45. 스트림은 주의해서 사용해라
> 스트림(데이터 시퀀스)과 스트림 파이프라인(각 원소에 대해 수행되는 연산 단계)의 정의
> 연산의 종류는 중간연산, 최종연산
> 최종연산이 호출될 때 eval되므로 lazy eval이고 최종연산이 없으면 no-op다
> 람다는 타입을 자주 생략하므로 매개변수 이름을 잘 지을 것
> char용 stream은 자바에서 지원하지 않음. chars()로 가능하지만 굳이?
> 코드블록에서만 수행가능한 걸 수행해야 한다면, 이는 스트림과 맞지않다
> > 범위 안에 지역변수 선언 및 수정
> > return, break, continue를 사용해야하는 경우
>
> 스트림 사용하기 좋은 경우
> > 일관되게 변환
> > 필터링
> > 하나의 연산 사용
> > 컬렉션으로 모으기
> > 특정 조건 만족하는 원소 찾기
>
> 스트림 사용하기 어려운 경우
> > 한 데이터가 파이프라인의 여러 단계를 통과할 떄, 각 단계에서 값들에 동시에 접근하기 어려운 경우? // 뭔말이래
> > 연산의 결과가 필요한 단계가 여러곳인 경우
>
> 즉, 잘써야하고, 스트림으로 리팩토링하고 더 나아보인다면 반영하는걸 권장
>
> 추가 내용
> > 가독성과 명확성을 위해 적절히
> > 컬렉션을 스트림으로 변환하지 마세요
> > 부작용(side-effect)가 있는 연산은 피할 것
> > 병렬 스트림은 신중히

스트림의 핵심은 두가지이다
1. 스트림은 데이터 원소의 유한 혹은 무한 시퀀스를 뜻하고
2. 스트림 파이프라인은 이 원소들로 수행하는 연산 단계를 표현한다

스트림 파이프라인은 소스 스트림으로 시작하며, 중간 연산이 있을수도 있고, 종단연산으로 끝난다

중간 연산은 스트림을 어떤 방식으로 변환한다. 필터링을 하거나, 특정 함수를 적용해 변환하거나 기타 등등

종단연산은 원소를 정렬해 컬렉션에 담거나, 특정원소를 하나를 선택하거나 마지막으로 수행되는 연산이며, 종단연산에서 evaluation이 이뤄지므로 lazy evaluation이 되며, 종단연산이 없으면 아무일도 하지않는 명령어(no-op)와 같으니 종단연산을 빼먹지 말자

스트림 API는 메소드 체이닝을 지원하는 fluenct API이므로 단 하나의 표현식으로 완성할 수 있다.

아나그램은 구성하는 알파벳은 같지만 순서가 다른 단어이다.(ex. "abc", "acb", "bac", "bca", "cab", "cba")

아래의 예제를 보자

```java
private String alphabetize(String s) {
    char[] a = s.toCharArray();
    Arrays.sort(a);
    return new String(a);
}

@Test
public void 아나그램() throws Exception {
    int minGroupSize = 5;
    String pathname = null;
    
    File dictionary = new File(pathname);
    Map<String, Set<String>> groups = new HashMap<>();
    try (Scanner s = new Scanner(dictionary)) {
        while (s.hasNext()) {
            String word = s.next();
            groups.computeIfAbsent(alphabetize(word),
                    (unused) -> new TreeSet<>()).add(word);
        }
    }
    for (Set<String> group : groups.values()) {
        if (group.size() >= minGroupSize) {
            System.out.println(group.size() + ": " + group);
        }
    }
}
```

사전에서 단어를 읽고, 사용자가 지정한 문턱값보다 원소수가 많은 아나그램을 출력한다.

동일한 기능을 하는 아래의 예제를 봐보자

```java
@Test
public void 아나그램_가독성떨어짐() throws Exception {
    int minGroupSize = 5;
    String pathname = null;
    
    Path dictionary = Paths.get(pathname);
    try (Stream<String> words = Files.lines(dictionary)) {
        words.collect(
                        groupingBy(word -> word.chars().sorted()
                                .collect(StringBuilder::new,
                                        (sb, c) -> sb.append((char) c),
                                        StringBuilder::append).toString()))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .map(group -> group.size() + ": " + group)
                .forEach(System.out::println);
    }
}
```

스트림을 너무 과도하게 사용하여 알아볼수가 없다. 다시 아래의 예제를 봐보자.

```java
@Test
public void 아나그램_가독성굳() throws Exception {
    int minGroupSize = 5;
    String pathname = null;
    
    Path dictionary = Paths.get(pathname);
    try (Stream<String> words = Files.lines(dictionary)) {
        words.collect(groupingBy(word -> alphabetize(word)))
                .values().stream()
                .filter(group -> group.size() >= minGroupSize)
                .forEach(group -> System.out.println(group.size() + ": " + group));
    }
}
```

스트림 변수의 이름을 words로 지어 스트림의 각 원소가 word임을 명확히 했으며, 중간연산 없이 바로 종단연산으로 맵으로 collect한다. 그리고 맵에서 스트림을 생성해 필터를 거치고 종단연산인 forEach를 통해 원소들을 출력한다.

```java
@Test
public void char는_스트림을_쓰지말자() throws Exception {
    // 잘못된 출력: 721011081081113211911111410810033 출력
    "Hello world!".chars().forEach(System.out::println);

    // 정상 출력
    "Hello world!".chars().forEach(x -> System.out.println((char) x));
}
```

alphabetize도 스트림을 통해 구현할 수 있지만, java에서 char 전용 스트림도 지원 안할뿐더러 chars는 int스트림을 반환하므로 헷갈리기도하므로 **굳이 char는 스트림으로 처리하지 말자**

스트림이 항상 좋은것이 아니므로(아나그램_가독성굳 예제 참조) **기존 코드는 스트림을 사용하도록 리팩토링 하되, 더 나아보이는 경우에만 반영하자**

스트림은 주로 함수객체로 표현하고, 반복문은 코드블록으로 표현된다. 즉, 코드블록에서만 할수있는게 필요하다면 이 경우는 스트림과 맞지않는다. 예시를 봐보자.

1. 코드블록에서는 지역변수를 읽고 쓸 수 있지만, 람다는 final만 읽을 수 있다.
2. 코드블록은 return, break, continue를 쓸 수 있고, 메소드 선언에 명시된 checked exception을 던질 수 있지만 람다는 불가능하다.

스트림은 아래의 경우에 알맞다.

1. 시퀀스르 일관되게 변환하고
2. 시퀀스를 필터링하고
3. 시퀀스를 하나의 연산을 사용해 결합하고
4. 시퀀스를 컬렉션에 모으고
5. 시퀀스에서 특정 조건을 만족하는 원소를 찾고

스트림으로 처리하기 어려운 일도 있다. 한 데이터가 파이프라인의 여러 stage를 통과 할 때 이 데이터의 각 단계에서의 값들에 동시에 접근하기 어려운 경우이다. 이는 스트림 파이프라인은 일단 한 값을 다른 값에 매핑하면 원래의 값은 잃는 구조이기 때문이다. 아래 예제를 봐보자

```java
@Test
public void 메르센소수() throws Exception {
    BigInteger TWO = BigInteger.valueOf(2);
    Supplier<Stream<BigInteger>> priems = () -> Stream.iterate(TWO, BigInteger::nextProbablePrime);

    priems.get().map(p -> TWO.pow(p.intValueExact()).subtract(ONE))
            .filter(mersenne -> mersenne.isProbablePrime(50))
            .limit(20)
            .forEach(System.out::println);
}
```

# Item 46. 스트림에서 부작용 없는 함수를 사용해라

> 부작용이 없는 함수를 통해 명확한 동작 보장
> 최종연산에서 부작용 있는 함수 활용을 제한
> collect 메소드를 활용(toList, toSet, toMap)하여 부작용 없이 container에 저장하라
>
> 부작용이 없는 함수.. 가 뭔데
> 스트림 패러다임의 핵심은 '계산'을 '일련의 변환'으로 재구성
> 각 단계는 이전의 입력을 받아 처리하는 **순수함수**
> > 순수함수는 오직 입력만이 결과에 영향을 주며
> > 다른 가변상태를 참조하지 않고
> > 스스로 상태를 변경하지 않는다
>
> forEach
> > 계산결과 보여줄때만 사용.
> > 계산할때는 사용 x(TODO: 리팩토링 할거 많겠누.. // forEach 내부에서 외부변수에 막 넣고 이런거...)
> > ```java
> > Map<String, Long> freq = new HashMap<>();
> > try(Stream<String> words = new Scanner(file).tokens()) {
> >     words.forEach(word -> {
> >         freq.merge(word.toLowerCase(), 1L, Long::sum);
> >     });
> > }
> > ```
> > ```java
> > Map<String, Long> freq;
> > try(Stream<String> words = new Scanner(file).tokens()) {
> >     freq = words.collect(groupingBy(String::toLowerCase, counting()));
> > }
> > ```
>
> Collectors라는 자주 쓰는 stream API 제공(TODO: 리팩토링할거 찾아서..)
> static import해서 사용 권장
> > toList
> > toMap
> > groupingBy
> > partitionBy
> > minBy, maxBy
> > joining

# Item 47. 반환 타입으로는 스트림보다 컬렉션이 낫다.

> 걍 스트림 쓰지말고 컬렉션 쓰고
> 데이터가 크면 스트림을 써야겠지만 그때도 가능하면 컬렉션을 써라.

> 스트림 반환의 단점
> > 스트림 파이프라인을 구성해야하며 종단연산이 필수다
>
> 컬렉션 반환의 장점
> > 추가적인 처리 없이도 원소를 바로 얻을 수 있다
>
> 스트림 반환이 적절한 경우
> > 데이터가 매우 크거나, 지연평가가 필요하거나, 데이터가 무한이거나
>
> 컬렉션은 Iterable의 하위타입(반복) && stream 메소드도 제공(스트림) 동시 제공
>
> 스트림 <-> Iterable은 각각 어댑터를 통해서 변환이 가능하지만, 난잡하고 직관성이 떨어지니 쓰지 않는것을 권장
> `iterable = iterableOf(stream)` 혹은 `stream = streamOf(iterable)
>
> 반환할 데이터가 크더라도 표현을 간결하게 할 수 있다면, 전용 컬렉션 구현을 고려하라

# Item 48. 스트림 병렬화는 주의해서 사용해라

TODO: 병렬화가 뭔지 구조(그림)부터 이해해야될듯

> 자바의 동시성 프로그래밍
> > 5: concurrent, Executor
> > 7: parallel decom-position 프레임워크 fork-join
> > 8: stream의 parallel
>
> 동시성 프로그래밍의 주요 포인트
> > 안정성과 응답 가능 상태
>
> 스트림에서의 동시성 프로그래밍(parallel) - 참조 지역성이 뛰어난 경우
> > 참조지역성이란 이웃한 원소가 메모리에 저장될 떄, 메모리에 연속으로 저장되는 성질
> > ArraList, HashMap, HashSet, ConcurrentHashMap, 배열, int범위, long범위
> > 이 자료구조는 데이터를 원하는 크기로 나누기 쉬워서 스레드한테 분배하기 좋고, 메모리에 올라오길 기다린느 대기시간이 작다
>
> 스트림에서의 동시성 프로그래밍(parallel) - 종단 연산 - 축소
> > 종단연산의 비중이 전체 파이프라인에서 크거나, 순차적 연산이라면 parallel 효과는 적다
> > reduce 메소드
> > min,max,count,sum처럼 완성된 형태로 제공되는 메소드
> > anyMatch,allMatch,noneMatch처럼 조건이 참이면 바로 반환하는 메소드
>
> 스트림에서의 동시성 프로그래밍(parallel) - spliterator 메소드 재정의
> > 병렬화의 이점을 높이려면 spliterator를 반드시 재정의 하는게 맞다.
> 