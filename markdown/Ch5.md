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

클래스나 인터페이스 선언에 타입 매개변수가 쓰이면 제네릭 클래스, 제네릭 인터페이스(통틀어 제네릭 타입) 라고 부른다.
List<E>같은게 제너릭 인터페이스다.

로 타입(raw type)은 여기서 <E>가 없어진 모양으로, 제네릭 타입을 정의하면 그에 딸린 로 타입도 함께 정의된다.
List<E>의 로 타입은 List다.

``` java
// Raw collection type - don't do this!
// My stamp collection. Contains only Stamp instances.
private final Collection stamps = ... ;
```

로 타입은 들어갈 타입을 제한하지 않으므로 빌드 시 잘못된 타입이어도 컴파일하는데 문제가 없고 런타임중 사용될 때 런타임 에러를 출력한다. 

``` java
// Erroneous insertion of coin into stamp collection
stamps.add(new Coin( ... )); // Emits "unchecked call" warning

// Raw iterator type - don't do this!
for (Iterator i = stamps.iterator(); i.hasNext(); )
Stamp stamp = (Stamp) i.next(); // Throws ClassCastException
stamp.cancel();
```

오류는 빨리 발견하는게 좋으므로, 다음과 같이 제네릭 사용하는게 좋다.

``` java
// Parameterized collection type - typesafe
private final Collection<Stamp> stamps = ... ;

stamps.add(new Coin( ... ));
```

넣은 요소들을 사용할 때 컴파일러가 알아서 캐스팅 해 주기 때문에, 타입을 보장하며 문제가 있을 시 컴파일 시 다음 오류를 출력하며 코드에 이상이 있음을 알려준다.

``` java
Test.java:9: error: incompatible types: Coin cannot be converted
to Stamp
c.add(new Coin());
^
```

이처럼, 로 타입 사용은 지양하는게 좋다.

> 근데 왜 사용함?\
> -> 제네릭은 자바가 나온 지 20년 뒤에 나온거라 호환성때문에 없애기 쉽지 않았음

모든 타입을 받고싶으면, 로 타입이 아닌 `<Object>`를 사용하는게 좋다.
- 로 타입은 제네릭을 사용 안하겠다는 의미인 반면에, `<Object>`는 컴파일러에 명시적으로 모든 객체를 허용한다는 의미다.
``` java
// Fails at runtime - unsafeAdd method uses a raw type (List)!
public static void main(String[] args) {
List<String> strings = new ArrayList<>();
unsafeAdd(strings, Integer.valueOf(42));
String s = strings.get(0); // Has compiler-generated cast
}
private static void unsafeAdd(List list, Object o) {
list.add(o);
}
private static void safeAdd(List<Object> list, Object o) { // this is ok
list.add(o);
}
  ```

파라미터로 어떤 타입이든 받고 싶고, 그 타입을 신경쓰고 싶지 않은 것이면 비한정적 와일드카드 <?>를 사용하는게 좋다.
- 로 타입과 달리, 컬렉션의 불변식이 보장된다.
``` java 
/////////////////////////////////////////////////////////////////// 예시 손볼 것
static void unsafeAdd(List<?> l1, List<?> l2){
  // 코드 블록에 들어온 순간 타입이 정해진다. Set<String>이 들어왔다면 s1과 s2에는 String만 담을 수 있다.
  // 아래 코드는 컴파일 오류가 발생한다.
	l1.add(l2.get(0));
}

static void unsafeAdd(List l1, List l2){
  // 아래 코드는 성공한다. 런타임 오류 가능성을 내포한다.
	l1.add(l2.get(0));
}
  ```

로 타입을 사용해야 하는 경우는 다음과 같다
- class 리터럴 사용할 때
``` java
// legal
List.class
String[].class
int.class

// illegal 
List<String>.class
List<Integer>.class
  ```
- instanceof 사용할 때
``` java
// Legitimate use of raw type - instanceof operator
if (o instanceof Set) { // Raw type
Set<?> s = (Set<?>) o; // Wildcard type
...
}
  ```
> 런타임에는 제네릭 타입 정보가 지워지므로, <?> 제외하고는 instanceof 사용 불가능.\
> 같은 동작이므로 더 깔끔하게 표현 가능한 로 타입 사용

## Item 27. 비검사 경고를 제거하라

> 아주 당연하다

제네릭을 사용한다면, 아래의 컴파일러 warning을 제거할 수 있다.
- 비 검사 형 변환
- 비 검사 메서드 호출
- 비 검사 파라미터화된 가변 인수 타입
- 비 검사 변환
``` java
// Wrong, Compiler Error
Set<Lark> exaltation = new HashSet();

// Good
Set<Lark> exaltation = new HashSet<>();
  ```

자신 있으면 @SuppressWarnings("unchecked") 붙여서 warning 제거해도 된다.
하지만, 중요한 문제를 넘길 수 있으므로 전체 클래스같은데는 적용하면 안되고, 한 줄이 넘는 메서드나 생성자에 달린 @SuppressWarnings 어노테이션 발견하면 지역 변수 선언 쪽으로 옮기는게 좋다.

``` java
public <T> T[] toArray(T[] a) {
if (a.length < size)
return (T[]) Arrays.copyOf(elements, size, a.getClass());
System.arraycopy(elements, 0, a, 0, size);
if (a.length > size)
a[size] = null;
return a;
}
```

위 코드는 비 검사 형 변환의 한 예시이며, return은 선언이 아니여서 바로 위에  @SuppressWarnings 어노테이션을 붙일 수 없으므로(JLS, Java Language Specification) 메소드에 붙여야 한다 생각할 수 있지만
다음처럼 변경하면 그럴 필요가 없다.

``` java
// Adding local variable to reduce scope of @SuppressWarnings
public <T> T[] toArray(T[] a) {
if (a.length < size) {
// This cast is correct because the array we're creating
// is of the same type as the one passed in, which is T[].
@SuppressWarnings("unchecked") T[] result =
(T[]) Arrays.copyOf(elements, size, a.getClass());
return result;
}
System.arraycopy(elements, 0, a, 0, size);
if (a.length > size)
a[size] = null;
return a;
}
```
이처럼 최대한 작은 범위로 어노테이션을 붙이는게 좋다.

> @SuppressWarnings 사용하면 주석으로 왜 안전한지 적어놓는게 다른 사람에게 좋다.\
> 그냥 쓰지 말자

## Item 28. 배열보다는 리스트를 사용하라

뭐가 다른가?
1. 배열은 공변, 제네릭은 불공변이다
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

2. 배열은 런타임 중 자신이 담을 원소의 타입을 알고있고(reified), 제네릭은 런타임에 타입 정보가 소거된다.
  - 그러므로, 배열과 제네릭은 함께 못쓴다. 컴파일러가 자동 생성한 형변환 코드가 문제를 일으키기 때문
    - `new List<E>[]`, `new List<String>[]`, `new E[]`는 컴파일부터 안됨
``` java
// Why generic array creation is illegal - won't compile!
List<String>[] stringLists = new List<String>[1]; // (1)
List<Integer> intList = List.of(42); // (2)
Object[] objects = stringLists; // (3)
objects[0] = intList; // (4)
String s = stringLists[0].get(0); // (5)
```
> 정 쓰려면 비 한정적 와일드 카드 타입을 사용한 `List<?>`, `Map<?,?>`이 있긴 하다.

이런 차이 때문에, 제네릭 컬렉션은 일반적으로 자신의 원소 타입을 담은 배열을 반환하지 못한다.
또한, 가변 인수 메서드와 같이 사용하면 해석이 힘든 warning이 발생한다.
- 이떄, @SafeVarargs를 사용해서 warning을 제거할 수 있긴 하다.

이때, `E[]` 말고 `List<E>`를 쓰는게 된다.
좀 더 복잡해지고, 성능에 손해가 있을 수 있지만, 안전성과 상호 운용성이 좋기 떄문

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
위 코드는 매번 형 변환이 필요하고, 런 타임 중 ClassCastException이 발생할 수 있다.
``` java
// A first cut at making Chooser generic - won't compile
public class Chooser<T> {
private final T[] choiceArray;
public Chooser(Collection<T> choices) {
choiceArray =  (T[]) choices.toArray(); // <- is this safe?
}
// choose method unchanged
}
```
좀 낫지만, 작성한 사람이 주석으로 이게 안전한 이유를 설명해야 하므로 안하는게 낫다.

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
좀 장황하고 성능이 떨어지지만, ClassCastException를 발생시키지 않으므로, 안전하다
