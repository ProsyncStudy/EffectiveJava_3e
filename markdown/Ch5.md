- [Ch 5. 제네릭](#ch-5-제네릭)
  - [Item 26. raw 타입은 사용하지 마라](#item-26-raw-타입은-사용하지-마라)
  - [Item 27. 비검사 경고를 제거하라](#item-27-비검사-경고를-제거하라)
  - [Item 28. 배열보다는 리스트를 사용하라](#item-28-배열보다는-리스트를-사용하라)
  - [Item 29. 이왕이면 제네릭 타입으로 만들라](#item-29-이왕이면-제네릭-타입으로-만들라)
  - [Item 30. 이왕이면 제네릭 메서드로 만들라](#item-30-이왕이면-제네릭-메서드로-만들라)
  - [Item 31. 한정적 와일드카드를 사용해 API 유연성을 높이라](#item-31-한정적-와일드카드를-사용해-API-유연성을-높이라)
  - [Item 32. 제네릭과 가변인수를 함께 쓸 때는 신중하라](#item-32-제네릭과-가변인수를-함께-쓸-때는-신중하라)
  - [Item 33. 타입 안전 이종 컨테이너를 고려하라](#item-33-타입-안전-이종-컨테이너를-고려하라)

# Ch 5. 제네릭

- JAVA 5부터 지원하며 이전에는 collection에서 캐스팅 했어야 해서 잘못된 객체를 넣으면 런타임 에러가 발생했다.
- 런타임이 아닌 컴파일 시 오류를 검출할 수 있도록 코드를 짜는게 좋다.

## Item 26. raw 타입은 사용하지 마라

클래스나 인터페이스 선언에 `타입 매개변수(type parameter)`가 쓰이면 `제네릭 클래스`, `제네릭 인터페이스`(통틀어 제네릭 타입) 라고 부른다.
  - `List<E>`같은게 제너릭 인터페이스다.
    - `List`: 인터페이스
    - `<E>`: 매개변수화 타입((parameterized type)

`로 타입(raw type)`은 여기서 타입 매개변수 `<E>`가 없어진 모양으로, 제네릭 타입을 정의하면 그에 딸린 로 타입도 함께 정의된다.
  - `List<E>`의 로 타입은 List다.

로 타입은 들어갈 타입을 제한하지 않으므로 빌드 시 잘못된 타입이어도 컴파일하는데 문제가 없지만, `런타임중` 사용될 때 런타임 에러를 출력한다. 
``` java
// Raw collection type - don't do this!
// My stamp collection. Contains only Stamp instances.
private final Collection stamps = ... ;

// Erroneous insertion of coin into stamp collection
stamps.add(new Coin( ... )); // Emits "unchecked call" warning

// Raw iterator type - don't do this!
for (Iterator i = stamps.iterator(); i.hasNext(); )
Stamp stamp = (Stamp) i.next(); // Throws ClassCastException
stamp.cancel();
```
  - 오류는 빨리 발견하는게 좋으므로, 다음과 같이 제네릭을 사용하는게 좋다.
``` java
// Parameterized collection type - typesafe
private final Collection<Stamp> stamps = ... ;

stamps.add(new Coin( ... ));
```

  - 넣은 요소들을 사용할 때 컴파일러가 알아서 캐스팅 해 주기 때문에, 타입을 보장하며 문제가 있을 시 `컴파일 시` 다음 오류를 출력하며 코드에 이상이 있음을 알려준다.

``` java
Test.java:9: error: incompatible types: Coin cannot be converted
to Stamp
c.add(new Coin());
^
```

### 이처럼, 로 타입 사용은 지양하는게 좋다.

> 근데 왜 사용함?\
> -> 제네릭은 자바가 나온 지 20년 뒤에 나온거라 호환성때문에 없애기 쉽지 않았음

### 모든 타입을 받고싶으면, 로 타입이 아닌 `<Object>`를 사용하는게 좋다.
- 로 타입은 제네릭을 사용 안하겠다는 의미인 반면에, `<Object>`는 컴파일러에 명시적으로 모든 객체를 허용한다는 의미다.
``` java
private static void unsafeAdd(List list, Object o) {
list.add(o);
}
private static void unsafeAdd2(List<Object> list, Object o) { // this is ok
list.add(o);
}

// Fails at runtime - unsafeAdd method uses a raw type (List)!
public static void main(String[] args) {
  List<String> strings = new ArrayList<>();

  unsafeAdd(strings, Integer.valueOf(42)); // unsafeAdd2: Has compiler-generated cast

  String s = strings.get(0); // unsafeAdd: ClassCastException
}
  ```

파라미터로 어떤 타입이든 받고 싶고, 그 타입을 신경쓰고 싶지 않은 것이면 `비한정적 와일드카드` `<?>`를 사용하는게 좋다.
- 로 타입과 달리, 컬렉션의 불변식이 보장된다.
``` java 
  static void unsafeAdd(List l1, List l2){
    // runtime error
    l1.add(l2.get(0));
  }

  static void unsafeAdd(List<?> l1, List<?> l2){
    // 코드 블록에 들어온 순간 타입이 정해진다. List<String>이 들어왔다면 s1과 s2에는 String만 담을 수 있다.
    // 와일드 카드 Collection<?>은 null 외에는 어떠한 원소도 넣을 수 없다.
    // compile error
    l1.add(l2.get(0));
  }
  ```

로 타입을 사용해야 하는 경우는 다음과 같다
1. class 리터럴 사용할 때
  - 규칙이다. 배열과 기본 타입만 허용함.
``` java
// legal
List.class
String[].class
int.class

// illegal 
List<String>.class
List<Integer>.class
  ```
2. instanceof 사용할 때
  - 런타임에는 제네릭 타입 정보가 지워지므로, <?> 제외하고는 instanceof 사용 불가능.
``` java
// Legitimate use of raw type - instanceof operator
if (o instanceof Set) { // Raw type
Set<?> s = (Set<?>) o; // Wildcard type
...
}
  ```
  - 같은 동작이므로 더 깔끔하게 표현 가능한 로 타입 사용

## Item 27. 비검사 경고를 제거하라

> 아주 당연하다

### 비 검사(unckecked)는
  - 자바 컴파일러가 타입 안전을 보장하기 위한 타입 정보가 부족하다는 의미
  - 기본적으로 비활성화 되어 있으며, 컴파일러에 `-Xlint:uncheck` 추가해야 한다.

### 제네릭을 사용한다면, 아래의 컴파일러 warning을 제거할 수 있다.
- 비 검사 형 변환
``` java
@Test
void uncheckedCast() {
    Map<String, String> map = (Map<String, String>) getMap();
}

private Map getMap() {
    return new HashMap<String, String>();
}

/*
warning: [unchecked] unchecked cast
        Map<String, String> map = (Map<String, String>) getMap();
                                                               ^
  required: Map<String,String>
  found:    Map
*/
```
- 비 검사 메서드 호출
  - 타입이 안맞는 메서드 호출에 발생한다.
``` java
@Test
void uncheckedCall() {
    Set words = new HashSet();
    words.add("hello");
    words.add(1);

    assertThat(words.contains("hello")).isEqualTo(true);
    assertThat(words.contains(1)).isEqualTo(true);
}

/*
warning: [unchecked] unchecked call to add(E) as a member of the raw type Set
        words.add("1");
                 ^
  where E is a type-variable:
    E extends Object declared in interface Set
*/
```
- 비 검사 매개변수화 가변인수 타입
  - 가변인수를 제네릭과 함꼐 사용하는 경우 발생한다(Item 32)
``` java
@Test
void uncheckedCall() {
    Set words = new HashSet();
    words.add("hello");
    words.add(1);

    assertThat(words.contains("hello")).isEqualTo(true);
    assertThat(words.contains(1)).isEqualTo(true);
}

/*
warning: [unchecked] Possible heap pollution from parameterized vararg type List<T>
    private <T> List<List<T>> toList(List<T>... elements) {
                                                ^
  where T is a type-variable:
    T extends Object declared in method <T>toList(List<T>...)
*/
```
- 비 검사 변환
``` java
@Test
void uncheckedCast() {
    Map<String, String> map = getMap();
}

private Map getMap() {
    return new HashMap<String, String>();
}

/*
warning: [unchecked] unchecked conversion
        Map<String, String> map = getMap();
                                        ^
  required: Map<String,String>
  found:    Map
*/
```

``` java
// Wrong, Compiler Error
Set<Lark> exaltation = new HashSet();
/*
Venery.java:4: warning: [unchecked] unchecked conversion
Set<Lark> exaltation = new HashSet();
                       ^
required: Set<Lark>
found: HashSet
*/

// Good , <>: java7 support
Set<Lark> exaltation = new HashSet<>();
  ```
  - 타입 안전성을 위해, 모든 warning은 제거하는게 좋다.

### 자신 있으면 @SuppressWarnings("unchecked") 붙여서 warning 제거해도 된다.
  - 하지만, 중요한 문제를 넘길 수 있으므로 전체 클래스같은데는 적용하면 안되고, 한 줄이 넘는 메서드나 생성자에 달린 @SuppressWarnings 어노테이션 발견하면 지역 변수 선언 쪽으로 옮기는게 좋다.
  - 너무 안써도 문제인게, 의미없는 warning에 진짜 경고가 묻힐 수 있다.

``` java
public <T> T[] toArray(T[] a) {
if (a.length < size)
  return (T[]) Arrays.copyOf(elements, size, a.getClass());

System.arraycopy(elements, 0, a, 0, size);

if (a.length > size)
  a[size] = null;

return a;
}

/*
ArrayList.java:305: warning: [unchecked] unchecked cast
return (T[]) Arrays.copyOf(elementsf size, a.getClass());
                           ^
required: T[]
found: Object[]
*/
```

- 위 코드는 비 검사 형 변환의 한 예시이며, return은 선언이 아니여서 바로 위에 @SuppressWarnings 어노테이션을 붙일 수 없으므로(JLS, Java Language Specification) 메서드에 붙여야 한다 생각할 수 있지만 다음처럼 변경하면 그럴 필요가 없다.

``` java
// Adding local variable to reduce scope of @SuppressWarnings
public <T> T[] toArray(T[] a) {

if (a.length < size) {
  // This cast is correct because the array we're creating
  // is of the same type as the one passed in, which is T[].
  
  @SuppressWarnings("unchecked")
  T[] result = (T[]) Arrays.copyOf(elements, size, a.getClass());
  
  return result;
}

System.arraycopy(elements, 0, a, 0, size);

if (a.length > size)
  a[size] = null;

return a;
}
```
  - 이처럼 최대한 작은 범위로 어노테이션을 붙이는게 좋다.

> @SuppressWarnings 사용하면 주석으로 왜 안전한지 적어놓는게 다른 사람에게 좋다.

## Item 28. 배열보다는 리스트를 사용하라

### 뭐가 다른가?
1. 배열은 공변, 제네릭은 불공변이다.
  - 어떤 타입 S가 T의 하위 타입이라면, S를 T로 대체할 수 있는가의 문제로 공변의 경우 O, 불공변의 경우 X 이다.

``` java
// Fails at runtime!
Object[] objectArray = new Long[1];
objectArray[0] = "I don't fit in"; // Throws ArrayStoreException

// Won't compile!
List<Object> ol = new ArrayList<Long>(); // Incompatible types
ol.add("I don't fit in");
```
> 이번 챕터가 항상 강조하듯이, 컴파일 시 오류를 발견하는게 좋다.

2. 배열은 런타임 중 자신이 담을 원소의 타입을 알고있고(실체화됨(reified)), 제네릭은 런타임에 타입 정보가 소거된다(실체화 불가(non-reifiable)).
  - 이전 코드를 보면 알 수 있듯이, 배열은 담을 원소를 아니까 런타임에 ArrayStoreException 오류가 나온다.
  - 그러므로, 배열과 제네릭은 함께 못쓴다. 컴파일러가 자동 생성한 형변환 코드가 문제를 일으키기 때문
    - `new List<E>[]`, `new List<String>[]`, `new E[]`는 컴파일부터 안됨
``` java
// Why generic array creation is illegal - won't compile!
List<String>[] stringLists = new List<String>[1]; // (1)
List<Integer> intList = List.of(42); // (2)
Object[] objects = stringLists; // (3)
objects[0] = intList; // (4)
String s = stringLists[0].get(0); // (5)

/*
  (1)이 되면?
  (2)에서 원소 하나짜리 List<Integer>를 생성
  (3)에서 List<String>[]를 Object[]에 할당 -> 배열은 공변이니 가능
  (4)에서 Object[]의 첫 원소로 List<Integer> 저장 -> 제네릭은 타입 소거되므로(List<Integer> -> List) 문제 없음
  (5)에서 List<String>만 담겠다는 stringLists에는 지금 List<Integer>가 있네?

  이래서 컴파일러는 (1)부터 안되도록 막음
*/
```
  - 매개변수화 타입 중, 실체화 가능한 타입은 비 한정적 와일드 카드 타입인 `List<?>`, `Map<?,?>`이 있긴 하다.
    - 쓸일 없긴 함

### 이런 차이 때문에, 제네릭 컬렉션은 일반적으로 자신의 원소 타입을 담은 배열을 반환하지 못한다.
  - 또한, 가변 인수 메서드와 같이 사용하면 해석이 힘든 warning이 발생한다.
    - 이떄, @SafeVarargs를 사용해서 warning을 제거할 수 있긴 하다.
  - 좀 있다 일반적이지 않은 경우 설명함(Item 33)

## 배열로 형 변환 시, `E[]` 말고 `List<E>`를 쓰면 warning 제거가 가능하다.
  - 이 방법이 더 좋은게, 좀 더 복잡해지고 성능에 손해가 있을 수 있지만, 안전성과 상호 운용성이 좋기 떄문

``` java
// Chooser - a class badly in need of generics!
public class Chooser {

  private final Object[] choiceArray;

  public Chooser(Collection choices) {
    choiceArray = choices.toArray();
  }

  public Object choose() {
    Random rnd = ThreadLocalRandom.current();
    return choiceArray[rnd.nextInt(choiceArray.length)];
  }

}
```
  - 위 코드는 매번 형 변환이 필요하고, 런 타임 중 ClassCastException이 발생할 수 있다.
``` java
// A first cut at making Chooser generic - won't compile
public class Chooser<T> {

  private final T[] choiceArray;

  public Chooser(Collection<T> choices) {
    choiceArray =  (T[]) choices.toArray(); // <- is this safe?
  }
  
  // choose method unchanged
  /*
Chooser.java:9: warning: [unchecked] unchecked cast
choiceArray = (T[]) choices.toArray();
required: T[], found: Object[]
where T is a type-variable:
T extends Object declared in class Chooser
  */
}
```
  - 좀 낫지만, 런타임에는 타입 정보가 소거되어 안전한지 보장할 수 없으므로, 작성한 사람이 주석으로 이게 안전한 이유를 설명해야 하므로 안하는게 낫다.

``` java
// List-based Chooser - typesafe
public class Chooser<T> {

  private final List<T> choiceList;

  public Chooser(Collection<T> choices) {
    choiceList = new ArrayList<>(choices);
  }
  public T choose() {
    Random rnd = ThreadLocalRandom.current();
    return choiceList.get(rnd.nextInt(choiceList.size()));
  }
}
```
  - 좀 장황하고 성능이 떨어지지만, ClassCastException를 발생시키지 않으므로, 안전하다

## Item 29. 이왕이면 제네릭 타입으로 만들라

### Item 9의 예시를 제네릭 타입으로 변환해볼 것이다.

``` java
// Object-based collection - a prime candidate for generics
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

  public Object pop() {
    if (size == 0)
      throw new EmptyStackException();

    Object result = elements[--size];
    elements[size] = null; // Eliminate obsolete reference
    return result;
  }

  public boolean isEmpty() {
  return size == 0;
  }

  private void ensureCapacity() {
    if (elements.length == size)
    elements = Arrays.copyOf(elements, 2 * size + 1);
  }
}
```

  - 우선 Object를 적절한 타입으로 만들어야 한다

``` java
// Initial attempt to generify Stack - won't compile!
public class Stack<E> {
  private E[] elements;
  private int size = 0;
  private static final int DEFAULT_INITIAL_CAPACITY = 16;

  public Stack() {
    elements = new E[DEFAULT_INITIAL_CAPACITY];
  }
  
  public void push(E e) {
    ensureCapacity();
    elements[size++] = e;
  }

  public E pop() {
    if (size == 0)
      throw new EmptyStackException();
    
    E result = elements[--size];
    elements[size] = null; // Eliminate obsolete reference
    
    return result;
  }
  ... // no changes in isEmpty or ensureCapacity
}

/*
Stack.java:8: generic array creation
elements = new E[DEFAULT_INITIAL_CAPACITY];
^
*/
```

  - ### 두 방법이 있다
  1. 문제가 되는 배열은 private로 저장되고, 전달되거나 반환되는 일이 없으니 push로 저장되는건 항상 E라고 생각할 수 있다.
``` java
// The elements array will contain only E instances from push(E).
// This is sufficient to ensure type safety, but the runtime
// type of the array won't be E[]; it will always be Object[]!
@SuppressWarnings("unckecked")
public Stack() {
  elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
}
```
  2.  E는 non-reifiable type이므로 컴파일러가 런타임에서 일어나는 형변환이 안전한지 알 수 없으니, 배열의 타입 E[] 를 Object[]로 변경하고 안전함을 증명한다
``` java
public Stack() {
  elements = new Object[DEFAULT_INITIAL_CAPACITY];
}

// Appropriate suppression of unchecked warning
public E pop() {
  if (size == 0)
    throw new EmptyStackException();
  
  // push requires elements to be of type E, so cast is correct
  @SuppressWarnings("unchecked")
  E result = (E) elements[--size];

  elements[size] = null; // Eliminate obsolete reference
  
  return result;
}
```
  - 첫 번째 방식이 읽기 쉽고, 간결하고, 배열 선언에만 캐스팅하면 되어서 주로 사용하지만, heap pollution(런타임과 컴파일때의 타입이 달라 매개변수화 타입의 변수가 타입이 다른 객체를 참조) 문제가 발생 가능하다.

> 배열 말고 리스트 쓰라매
  - 제네릭 타입 안에서 무조건 사용할 수 있는게 아니다.
    - 자바 기본 타입이 아니라 그렇다, ArrayList도 구현 보면 배열로 쓴다.
    - HashMap같은 경우는 성능 향상 떄문에 사용하기도 한다.

### 제네릭은 매개변수의 제한이 없다.
  - `Stack<Object>`, `Stack<int[]>`, `Stack<List<String>>`나 다른 reference type의 객체도 가능하다
  - 참조타입만 가능하므로, primitive type은 안되긴 한데, 박싱하면 된다.
  - DelayQueue처럼 타입 매개변수를 제한하는 제네릭 타입도 있다.
``` java
class DelayQueue<E extends Delayed> implements BlockingQueue<E>
```
  - `<E extends Delayed>`로 java.util.concurrent.Delayed의 하위 타입만 받는다고 선언했다.
    - 이때, E는 한정적 타입 매개변수(bounded type parameter)라고 부른다
  - 이렇게 해서 DelayQueue 자신과 DelayQueue를 사용하는 클라이언트는 형변환 없이 바로 Delayed 클래스의 메서드를 사용할 수 있다.

## Item 30. 이왕이면 제네릭 메서드로 만들라

> 클래스가 되는데 안 될 이유가 없다.

### 매개변수화 타입을 받는 정적 유틸리티 메서드는 보통 제네릭이다.
  - ex) Collections 의 모든 알고리즘 메서드(binarysearch, sort)가 제네릭이다.

### 이번엔 Item 26의 예시를 제네릭 타입으로 변환해볼 것이다.
``` java
// Uses raw types - unacceptable! (Item 26)
public static Set union(Set s1, Set s2) {
  Set result = new HashSet(s1);
  
  result.addAll(s2);

  return result;
}
/*
Union.java:5: warning: [unchecked] unchecked call to
HashSet(Collection<? extends E>) as a member of raw type HashSet
Set result = new HashSet(s1);
^
Union.java:6: warning: [unchecked] unchecked call to
addAll(Collection<? extends E>) as a member of raw type Set
result.addAll(s2);
^
*/
```

  - 이제 우리는 위 코드를 아래처럼 안전하게 만들 수 있다.
    - 받는 입력과 반환을 타입 매개변수로 전환
``` java
// Generic method
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
  Set<E> result = new HashSet<>(s1);
  
  result.addAll(s2);
  
  return result;
}
*/
```
  - 타입 매개변수 목록은 `<E>`이고 반환 타입은 `Set<E>`다.
  - 안정적이지만, input과 output의 타입이 같아야 한다.
    - 여기서 한정적 와일드 카드를 사용하면 유연해 질 것이다.

### immutable하지만, 여러 타입에서 사용하고 싶을 수도 있다.
  - `제네릭 싱글턴 팩토리`라는 정적 팩토리 메서드를 사용하면 불변 객체를 여러 타입으로 활용할 수 있다.
    - 제네릭은 런타임에 타입 정보가 소거되기 때문에 요청한 타입 매개변수에 맞게 매번 그 객체의 타입을 변경하려고 필요

``` java
// Generic singleton factory pattern
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

@SuppressWarnings("unchecked")
public static <T> UnaryOperator<T> identityFunction() {
  return (UnaryOperator<T>) IDENTITY_FN;
}
```

  - 원래는 `return (UnaryOperator<T>) IDENTITY_FN;`에서 `(UnaryFunction<T>)`는 비검사 형변환 warning을 만들지만 항등함수(identity function)는 입력값을 수정 없이 그대로 리턴하므로 안전하다.
    - 그러니 `@SuppressWarnings("unchecked")`로 숨겨도 문제 없다.

``` java
// Sample program to exercise generic singleton
public static void main(String[] args) {
  String[] strings = { "jute", "hemp", "nylon" };
  UnaryOperator<String> sameString = identityFunction();
  
  for (String s : strings)
    System.out.println(sameString.apply(s));
  
  Number[] numbers = { 1, 2.0, 3L };
  UnaryOperator<Number> sameNumber = identityFunction();
  
  for (Number n : numbers)
    System.out.println(sameNumber.apply(n));
}
```

### 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 제한할 수 있으며, 이를 재귀적 타입 한정(recursive type bound)이라고 한다.
  - 보통, 순서를 정하는 Comparable 인터페이스와 같이 사용된다.
``` java
public interface Comparable<T> {
  int compareTo(T o);
}
```

  - 타입 파라미터 T는 Comparable를 구현한 타입이 비교할 수 있는 원소의 타입을 정의한다.
    - 대부분의 타입은 자신과 같은 타입의 원소만 비교 가능하다.
      - `String`은 `Comparable<String>`, `Integer`는 `Comparable<Integer>`

  - Comparable을 구현했다는 것은, 모든 요소가 다른 요소랑 비교 가능하다는 것이고 이 제약은 다음 코드처럼 나타낼 수 있다.

``` java
// Returns max value in a collection - uses recursive type bound
public static <E extends Comparable<E>> E max(Collection<E> c) {
  
  if (c.isEmpty())
    throw new IllegalArgumentException("Empty collection"); // Item 55
  
  E result = null;
  
  for (E e : c)
    if (result == null || e.compareTo(result) > 0)
      result = Objects.requireNonNull(e);
  
  return result;
}
```

  - `<E extends Comparable<E>>`는 모든 타입 E는 자신과 비교할 수 있다 라는 것 이므로, 상호 비교가 가능하다는 뜻이다.

### 이처럼, 제네릭 메서드를 사용하면
  1. 매개변수 및 반환 값에 캐스팅을 일일이 해 줄 필요도 없고
  2. 안전하고
  3. 사용이 쉽다

## Item 31. 한정적 와일드카드를 사용해 API 유연성을 높이라

> 중요) 서로 다른 `Type1`, `Type2`에 대해서 `List<Type1>`은 `List<Type2>`의 하위타입도, 상위타입도 아니다.(불공변, Item 28)

### 가끔 불공변 방식보다 유연한게 필요할 떄가 있다
  - 예시로, 좀 전에 사용한 Stack을 사용한다.

``` java
public class Stack<E> {
  public Stack();
  public void push(E e);
  public E pop();
  public boolean isEmpty();
}
```

- 여기에 `pushAll()`이라는 메서드를 추가한다 생각해보자

``` java
// pushAll method without wildcard type - deficient!
public void pushAll(Iterable<E> src) {
  for (E e : src)
    push(e);
}
/*
Stack<Number>에 Integer 넣으면?

StackTest.java:7: error: incompatible types: Iterable<Integer>
cannot be converted to Iterable<Number>
numberstack.pushAll(integers);
*/
```

  - 컴파일에는 문제가 없지만, src와 Stack의 타입이 같아야 한다.
    - `Stack<Number>`에 Number의 하위타입인 Integer를 못넣는다는 뜻
    - ### 이걸 해결하는게 `한정적 와일드카드 타입(bounded wildcard type)`이다.

``` java
// Wildcard type for a parameter that serves as an E producer
public void pushAll(Iterable<? extends E> src) {
  for (E e : src)
   push(e);
}
```

  - E만이 아니라, E의 하위타입까지도 가능하다.

``` java
// popAll method without wildcard type - deficient!
public void popAll(Collection<E> dst) {
  while (!isEmpty())
    dst.add(pop());
}
/*
Stack<Number> numberstack = new Stack<>();
Collection<Object> objects = ...;
numberstack.popAll(objects);
??
*/

// Wildcard type for parameter that serves as an E consumer
public void popAll(Collection<? super E> dst) {
  while (!isEmpty())
    dst.add(pop());
}
```

  - 위처럼, 상위 타입을 받을 수도 있다.

> 유연성을 위해, 생산자(producers)나 소비자(consumers)용 입력 매개변수에 와일드카드 타입을 사용하는게 좋다.\
> 생산자와 소비자를 동시에 하면, 타입을 명확히 해야 하므로 사용 안하는게 좋다.

### PECS:  producer-extends, consumer-super
  - 와일드카드를 사용하는 기본 원칙으로, 위 예제를 보면 이 원칙이 사용되는 것을 볼 수 있다.

### 다른 예제들을 보자.

``` java
// producer(Item 28)
public Chooser(Collection<T> choices)

// ->
// Wildcard type for parameter that serves as an T producer
public Chooser(Collection<? extends T> choices)

// producer
public static <E> Set<E> union(Set<E> s1, Set<E> s2)

// ->
public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2)

// twice(consumer, producer)
public static <E extends Comparable<E>> E max(List<E> list)

// ->
public static <E extends Comparable<? super E>> E max(List<? extends E> list)
```

### java 7까지는, 컴파일러가 좀 멍청해서, 올바른 타입을 추론하지 못하는 경우가 있는데, 이떄는 명시적 타입 인수(explicit type argument)를 사용해서 타입을 알려주면 된다.
``` java
// Explicit type parameter - required prior to Java 8
Set<Number> numbers = Union.<Number>union(integers, doubles);
```
> 8버전 이후를 사용하자

### Comparable, Comparator는 언제나 소비자다.
``` java
// consumer
public static <T extends Comparable<T>> T max(List<T> list)

// ->
public static <T extends Comparable<? super T>> T max(List<? extends T> list)
```

  - Comparable, Comparator를 직접 구현한 다른 타입을 확장한 타입을 지원하기 위해서는 와일드카드가 필요하다.
``` java
List<ScheduledFuture<?>> scheduledFutures = ... ;
```
  - 수정하지 않은 max() 메서드였다면, 위의 scheduledFutures를 처리하지 못했을 것이다.
    - Delayed의 하위 인터페이스이고, Delayed는 Comparable를 확장했기 떄문

### 컴파일러는 와일드카드의 타입을 추론하기도 한다.
  - 그래서, 메소드 정의 중에는 타입 매개변수와 와일드카드중 어느것을 사용해도 문제 없다.
``` java
// Two possible declarations for the swap method
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j);
```
  - 놀랍게도, 두 방식 모두 문제 없다.
    - 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체하는 것이 좋다.
      - 비한정적 타입 매개변수면 비한정적 와일드카드로, 한정적 타입 매개변수면 한정적 와일드카드로 바꾸면 된다.
    - 하지만, 두 번째 방법은 `List<?> list`에  null 외에는 어떤 값도 넣을 수 없으므로, 도우미 메서드를 만들어야 한다.
      - 이때, 도우미 메서드는 제네릭이어야 한다.
``` java
public static void swap(List<?> list, int i, int j) {
list.set(i, list.set(j, list.get(i)));
}
/*
Swap.java:5: error: incompatible types: Object cannot be
converted to CAP#1
list.set(i, list.set(j, list.get(i)));
^
where CAP#1 is a fresh type-variable:
CAP#1 extends Object from capture of ?
*/

// do this
public static void swap(List<?> list, int i, int j) {
swapHelper(list, i, j);
}
// Private helper method for wildcard capture
private static <E> void swapHelper(List<E> list, int i, int j) {
list.set(i, list.set(j, list.get(i)));
}

```
  - swapHelper는 list가 `List<E>`인 것을 알고 있으므로, 꺼낸 값은 E고 E타입이면 넣을수 있다는걸 안다.

## Item 32. 제네릭과 가변인수를 함께 쓸 때는 신중하라

### 제네릭과 가변인수(varargs)는 모두 Java5에서 추가된건데 같이쓰면 문제가 있다.
  - 가변인수는 클라이언트가 메서드에 넘기는 인수의 개수를 조절할 수 있도록 해주는데, 허점이 있다.
    - 가변인수 메서드 호출 시, 가변인수들을 담을 배열을 만드는데 이게 노출되어있다.
      - 메서드 선언 시, 가변인수 파라미터가 실체화 불가 타입(non-reifiable type)이면, warning을 출력한다.
        - = 가변인수 파라미터에 제네릭이나 매개변수화 타입이 있으면, 컴파일러에서 warning을 출력한다.
          - 대부분의 제네릭과 매개변수화 타입은 실체화 불가 타입(non-reifiable type)이기 때문(Item 28)
      - 메서드 호출 할 때에도, 실체화 불가 타입으로 추론하면 warning을 출력한다.

``` java
warning: [unchecked] Possible heap pollution from
parameterized vararg type List<String>
```
  - 위 경고처럼, 힙 오염(매개변수화 타입의 변수가 타입이 다른 객체를 참조) warning을 뱉는다.

``` java
// Mixing generics and varargs can violate type safety!
static void dangerous(List<String>... stringLists) {
  List<Integer> intList = List.of(42);
  Object[] objects = stringLists;
  
  objects[0] = intList; // Heap pollution
  
  String s = stringLists[0].get(0); // ClassCastException
}
```

  - 위 코드는 가시적인 형변환은 없지만, 컴파일러가 자동으로 형변환 하기 때문에, 마지막줄에서 ClassCastException 오류가 발생하게 된다.
    - 즉, 타입 안정성이 보장되지 않으므로, 제네릭 가변인수 배열 매개변수에 값을 저장하는건 위험하다.
> 왜 이걸 문법적으로 허용하는거임? 제네릭 배열을 직접 작성하는 건 안되는데
  - 실무에서 매우 유용하기 떄문에 안고 가는걸로 함
  - Arrays.asList(T... a), Collections.addAll(Collection<? super T> c, T... elements), EnumSet.of(E first, E... rest) 같은 것들이 자바에서 제공하는 메서드이며 type safe하다.

### Java7 이전에는 제네릭 가변인수 메서드가 호출자 쪽에서 발생하는 warning을 제거하려면 매번 `@SuppressWarnings("unchecked")` 를 넣어야 했는데, 이제는 `@SafeVarargs`으로 작성자가 메서드의 타입 안전함을 보장할 수 있다.
  - 메서드를 호출 시, 가변인수 매개변수를 담는 제네릭 배열이 만들어질 때 메서드가 이 배열에 아무것도 저장하지 않고 그 배열의 참조가 밖으로 노출되지 않는다면, 타입 안전함을 보장해도 된다.
    - 즉, 가변인수 매개변수를 담는 제네릭 배열이 호출자로부터 그 메서드로 순수하게 인수들을 전달하는 일만 한다면 그 메서드는 안전하다.

### 매개변수 배열에 아무것도 저장 안해도 타입 안정성이 위험할 수 있다.

``` java
// UNSAFE - Exposes a reference to its generic parameter array!
static <T> T[] toArray(T... args) {
  return args;
}
```
  - 다음 문제가 있다.
    - 이 메서드가 반환하는 배열의 타입은 이 메서드에 인수를 넘기는 컴파일타임에 결정되는데, 그 시점에는 컴파일러에게 충분한 정보가 주어지지 않아 타입을 잘못 판단할 수 있다.
      - 즉, 자신의 가변인수 매개변수 배열을 그대로 반환하면, 힙 오염을 호출한 콜스택까지 갖고갈 수 있다.

``` java
// UNSAFE - Exposes a reference to its generic parameter array!
static <T> T[] toArray(T... args) {
  return args;
}

static <T> T[] pickTwo(T a, T b, T c) {
  switch(ThreadLocalRandom.current().nextInt(3)) {
    case 0: return toArray(a, b);
    case 1: return toArray(a, c);
    case 2: return toArray(b, c);
  }
  throw new AssertionError(); // Can't get here
}

public static void main(String[] args) {
  String[] attributes = pickTwo("Good", "Fast", "Cheap");
}
```

  - 다음 과정으로 문제가 발생한다
    1. 컴파일러가 pickTwo에서 받을 T를 T[]로 변환한다.
    2. 타입 소거 후 Object[]로 변환한다. -> 반환은 무조건 Object 타입 배열이다.
    3. 반환값을 String[] 에 저장하려고하면 형변환이 실패해서 ClassCastException 오류가 발생한다.

    - 자신의 제네릭 매개변수 배열의 참조를 노출하여, 힙오염이 발생한 것이며
    - 제네릭 가변인수 매개변수 배열에 다른 메서드가 접근하도록 허용하는 것은 안전하지 않다는 것을 알 수 있다.
      - 두가지 예외가 있긴 하다
        1. @SafeVarargs로 제대로 어노테이트된 또 다른 가변인수 메서드에 넘기는 것은 안전하다.
        2. 이 배열 내용의 일부 함수를 호출만 하는(가변인수를 받지 않는) 일반 메서드에 넘기는 것은 안전하다.

``` java
// 예외 1
// Safe method with a generic varargs parameter
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
  List<T> result = new ArrayList<>();
  
  for (List<? extends T> list : lists)
    result.addAll(list);
  
  return result;
}
```

### 제네릭이나 매개변수화 타입의 가변인수 매개변수를 받는 모든 메서드에 @SafeVarargs 를 사용할 수 있으므로, 주의해서 사용해야 한다.
  - 가변인수 매개변수 배열에(위 경우에는 list) 아무것도 저장하지 않고 그 배열(또는 clone)을 신뢰할 수 없는 코드에 노출하지 않아야 한다.
  - > 물론, 재정의할 수 없는 메서드에만 달아야 한다
  - > 그래서, Java 8에서 @SafeVarargs는 오직 정적 메서드와 final 인스턴스 메서드에만 붙일 수 있고 Java 9는 private 인스턴스 메서드에도 허용

### 다른 방식으로도 안전하게 만들 수 있다(Item 28 참고)
``` java
// List as a typesafe alternative to a generic varargs parameter
static <T> List<T> flatten(List<List<? extends T>> lists) {
  
  List<T> result = new ArrayList<>();
  
  for (List<? extends T> list : lists)
    result.addAll(list);
  
  return result;
}

audience = flatten(List.of(friends, romans, countrymen)); // no problem
```
  - 정적 팩터리 메서드인 List.of를 활용해 메서드에 임의의 인수를 넘길 수 있다.
    - List.of에 @SafeVarargs가 사용되었음을 알 수 있다.
    - 컴파일러가 메서드의 타입 안전성을 검증할 수 있으므로, 실수했을까봐 걱정하지 않아도 된다.
    - 코드가 좀 지저분하고, 약간 느려지긴 한다.

``` java
static <T> List<T> pickTwo(T a, T b, T c) {
  switch(rnd.nextInt(3)) {
    case 0: return List.of(a, b);
    case 1: return List.of(a, c);
    case 2: return List.of(b, c);
  }

  throw new AssertionError();
}

public static void main(String[] args) {
List<String> attributes = pickTwo("Good", "Fast", "Cheap");
}
```
  - 제네릭만 쓰므로 문제도 없고, 보기도 좋다!

## Item 33. 타입 안전 이종 컨테이너를 고려하라

### 보통 제네릭은 컬렉션이나 단일원소 컨테이너에서 많이 사용한다.
  - `Set<E>`나 `Map<K,V>`, 단일원소 컨테이너로는 `ThreadLocal<T>`나 `AtomicReference<T>`가 있다.
  - 이것들이 매개변수화되는 대상은 원소가 아닌 컨테이너 자신이다
    - 그러므로, 컨테이너마다 타입 매개변수의 수가 제한된다.
      - Set은 하나, Map은 두개 이런식으로

### 하지만, 좀 더 유연하게 사용하고 싶을 수가 있다.
  - 예를 들어, db의 row같이 임의로 많은 컬럼을 가지고 싶을 수 있다.
    - 이럴 때, 컨테이너 대신 키를 매개변수화 하고 매개변수화된 키를 값을 넣거나 뺄 때 같이 주면 된다.
      - 이거를 `타입 안전 이종 컨테이너 패턴`이라 한다.

``` java
// Typesafe heterogeneous container pattern - implementation
public class Favorites {
  private Map<Class<?>, Object> favorites = new HashMap<>();

  public <T> void putFavorite(Class<T> type, T instance) {
    favorites.put(Objects.requireNonNull(type), instance);
  }

  public <T> T getFavorite(Class<T> type) {
    return type.cast(favorites.get(type));
  }
}

// Typesafe heterogeneous container pattern - client
public static void main(String[] args) {
  Favorites f = new Favorites();
  
  f.putFavorite(String.class, "Java");
  f.putFavorite(Integer.class, 0xcafebabe);
  f.putFavorite(Class.class, Favorites.class);
  
  String favoriteString = f.getFavorite(String.class);
  int favoriteInteger = f.getFavorite(Integer.class);
  Class<?> favoriteClass = f.getFavorite(Class.class);
  
  System.out.printf("%s %x %s%n", favoriteString,
  
  favoriteInteger, favoriteClass.getName());
}
```

- 위 코드는 `타입 안전 이종 컨테이너 패턴`을 사용한 예시이다.
  - 각 타입 T의 클래스 타입인 `Class<T>`를 사용하는데, class의 클래스가 제네릭이기 때문에 문제가 없다.
    - class의 리터럴 타입은 `Class`가 아닌 `Class<T>`다.
      - `String.class`의 타입은 `Class<String>`라는 뜻
      - 여기서 컴파일타임 타입 정보와 런타임 타입정보를 알아내기 위해 메서드들이 주고 받는 class리터럴을 `타입 토큰(type token)`이라 한다.
  - 타입안전(typesafe)하고 이종(heterogeneous)하다.
    - typesafe: String 달라했는데 Integer 줄 일이 없다.
    - heterogeneous: key가 모든 타입으로 올 수 있다

- 구현한 메서드들을 보자
  - `Map<Class<?>, Object> favorites`에서 볼 수 있듯이 비 한정적 와일드카드를 중첩되게 사용해서 Map이 아닌 key에 와일드 카드를 사용했다.
    - 이를 통해, 모든 key가 다른 매개변수화 타입일 수 있게 만들어 다양한 타입을 지원할 수 있게 되었다.
      - `Class<String>`, `Class<Integer>` 이렇게 넣을 수 있다는 뜻.
      - 비한정적 와일드카드만 보고 아무것도 못 넣는다고 착각하면 안된다.
  - Map의 value는 Object이므로 key와 value 사이의 타입관계를 보증하지 않는다.
    - 자바에서는 명확하게 표현할 수는 없지만, 작성자는 관계를 알 수 있고 이를 이용할 수 있다.
  - `putFavorite()`는 Class와 instance를 받아 매핑하며, 이때 key와 value 사이의 타입 링크(type linkage)가 버려진다.
    - `getFavorite()`에서 다시 설정하니 괜찮다.
  - `getFavorite()`는 맵에서 Class객체에 해당하는 값을 꺼내고 Object를 T로 바꿔서 반환한다.
    - Class의 `cast`메서드를 사용해서 동적으로 캐스팅한다.

> cast는 어떻게 동작하는거지?

``` java
// 주어진 인수가 Class 객체가 알려주는 타입의 인스턴스인지 검사한 다음, 맞다면 그대로 반환하고 아니면 ClassCastException
public class Class<T> {
  T cast(Object obj);
}
```

- `Favorites` 클래스에서는 이정도만 쓰인다.
  - 주어진 인수가 Class 객체가 알려주는 타입의 인스턴스인지를 검사하고, 맞으면 인수를 아니면 ClassCastException을 뱉는다.
    - 굳이 메서드로 사용하는 이유는 cast 메서드의 시그니처가 Class 클래스가제네릭이라는 걸 최대한 이용하기 떄문
      - cast의 반환 타입은 Class 객체의 타입 매개변수와 같아 Favorites 를 T로 비검사 형변환하지 않고도 타입 안전하게 만들 수 있다.
  - 2가지 문제가 있다
  1. client가 Class 객체를 로 타입으로 전달하면 안전성이 깨진다.
``` java
favorites.put((Class) Integer.class, "Invalid Type");
int value = favorites.getFavorite(Integer.class); //ClassCastException
```
  - 컴파일은 되지만 warning 출력한다.
  - `type.cast(instance)`로 수정하면 instance와 type이 명시한 타입이 같은지 확인할 수 있다.
    - `Collections.checkedList`, `Collections.checkedSet`, `Collections.checkedMap`가 이런 식으로 구현되어있다.
    2. 실체화 불가 타입에는 사용할 수 없다.
      - `String` 이나 `String[]`는 저장하는데 `List<String>`는 안된다.
        - `List<String>`이나 `List<Integer>`가 모두 List.class 라는 객체를 공유하기 때문에, 컴파일러에서 `List<String>`의 Class객체를 알 수 없기 때문
      - 이건 완전히 해결할 수 없다.

### 한정적 타입 토큰(bounded type token)은 한정적 타입 매개변수나 한정적 와일드카드를 사용해 표현 가능한 타입을 제한하는 타입 토큰이다.
  - 위 예시인, Favorites 가 사용하는 타입 토큰은 비한정적이다.
    - `getFavorite()`와 `putFavorite()`가 어떤 Class 객체든 받을 수 있다는 뜻
      - 이때, 한정적 타입 토큰을 사용해서 타입을 제한할 수 있다.
  - Annotation API는 한정적 타입 토큰을 잘 활용한다.
``` java
public <T extends Annotation> T getAnnotation(Class<T> annotationType);
```
  - annotationType은 한정적 타입 토큰으로, 토큰으로 명시한 타입의 어노테이션이 있으면 반환, 없으면 null을 반환한다.
    - 어노테이션 된 요소는 키가 어노테이션 타입인 타입 안전 이종 컨테이너라는 뜻
  - Class<?> 와 같이 비한정 와일드카드 타입을 한정적 타입 토큰을 받는 메서드에 전달할 때 객체를 Class<? extends Annotation>으로 형변환 할 수는 있지만 비검사 경고 문구가 나올 수 있으므로 형변환을 안전하고 동적으로 수행해주는 Class 제공 메서드인 `asSubclass()`로 해결한다.
``` java
// Use of asSubclass to safely cast to a bounded type token
static Annotation getAnnotation(AnnotatedElement element, String annotationTypeName) {
  Class<?> annotationType = null; // Unbounded type token
  
  try {
    annotationType = Class.forName(annotationTypeName);
  } catch (Exception ex) {
    throw new IllegalArgumentException(ex);
  }

  return element.getAnnotation(annotationType.asSubclass(Annotation.class));
}

public <U> Class<? extends U> asSubclass(Class<U> clazz) {
        if (clazz.isAssignableFrom(this))
            return (Class<? extends U>) this;
        else
            throw new ClassCastException(this.toString());
}
```
  - `asSubclass()`는 자신을 받은 clazz의 타입으로 형변환 시도하고 안되면 ClassCastException을 뱉는다. 
  - 이를 사용해, 컴파일 시점에는 타입을 알 수 없는 어노테이션을 런타임에 읽을 수 있다.