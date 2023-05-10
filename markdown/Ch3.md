- [Ch 3. 모든 객체의 공통 메소드](#ch-3-모든-객체의-공통-메소드)
  - [Item 10. equals는 일반 규약을 지켜 재정의하라](#item-10-equals는-일반-규약을-지켜-재정의하라)
  - [Item 11. equals를 재정의하려거든 hashCode도 재정의하라](#item-11-equals를-재정의하려거든-hashcode도-재정의하라)
  - [Item 12. toString을 항상 재정의하라](#item-12-tostring을-항상-재정의하라)
  - [Item 13. clone 재정의는 주의해서 진행하라](#item-13-clone-재정의는-주의해서-진행하라)
  - [Item 14. Comparable을 구현할지 고려하라](#item-14-comparable을-구현할지-고려하라)

# Ch 3. 모든 객체의 공통 메소드

- Object는 상속해서 사용하도록 설계됐으므로 Object의 메소드 제정의시 지켜야 하는 일반규약 존재
- 일반규약을 지키지 않을경우 HashSet, HashMap 같은 클래스에서 오작동 가능성
- comapreTo는 성격이 비슷해서 같이 소개

## Item 10. equals는 일반 규약을 지켜 재정의하라

> 웬만하면 equals는 재정의 하지 말자.\
> 재정의 해야하면 핵심필드에 대해서 5가지 규약이 지켜지는지 테스트

equals를 재정의하지 않는게 최선인 경우(equals가 필요없거나 상위클래스에 정의된게 적합하다)

- 인스턴스의 논리적 동치성을 검사할 일이 없다
- 클래스가 private이거나, package-private이고 equals를 호출할 일이 없다
  - equals 재정의에서 `throw new AssertionError();` 해도 됨
- 각 인스턴스가 본질적으로 고유하다
  - 값이 아닌 동작하는 개체를 표현(ex. Thread)
- 상위 클래스에서 재정의한 equals가 하위 클래스에도 적합
  - `AbstractSet` -> `Set` 구현체들
  - `AbstractMap` -> `Map` 구현체들
- 인스턴스가 1개라면 재정의 안해도 됨(enum, 인스턴스 통제 클래스)

equals를 재정의 해야 하는 경우

- 객체 식별성(object identity: 물리적으로 같은가)이 아니라 논리적 동치성(logical equality)을 확인할 때 && 상위클래스 equals가 논리적 동치성을 비교하지 않을 때 -> 주로 값객체가 해당됨

Object 명세에서 발췌한 equals 관련 일반규약

- 반사성
  - `null`이 아닌 x에 대해 `x.eqauls(x) == true`다
  - 어기면? Collection에 x를 넣고, `contains(x) == false`
- 대칭성
  - `null`이 아닌 x,y에 대해 `x.equals(y) == true`면, `y.equals(x) == true`다
  - 어기면? Collecion<x>에 대해서 `contains(y) == true`지만, 반대로 Collection<y>에 대해서 `contains(x) == false`

  ``` java
  public class CaseInsensitiveString대칭성위배 {

      private final String s;

      // ...

      // 대칭성 위배
      @Override
      public boolean equals(Object o) {
          if (o instanceof CaseInsensitiveString대칭성위배) {
              return s.equalsIgnoreCase(((CaseInsensitiveString대칭성위배) o).s);
          }
          if (o instanceof String) {
              return s.equalsIgnoreCase((String) o);
          }
          return false;
      }
  }

  @Test
  public void 대칭성위배1() throws Exception {
      // given
      CaseInsensitiveString대칭성위반 cis = new CaseInsensitiveString대칭성위반("Test");
      String s = "test";

      // then
      assertNotEquals(s, cis);
      assertEquals(cis, s);
  }
  ```

  - string은 CaseInsensitiveString를 모르므로 CaseInsensitiveString의 equals를 string과 연동은 불가능
- 추이성
  - null이 아닌 x,y,z에 대해 x.equals(y)가 true고, y.equals(z)도 true면, x.equals(z)도 true다
  - 구체적 클래스에 확장은 추이성을 만족할 수 없다

  ``` java
  public class ColorPoint추이성위배 extends Point {
      private final Color color;

      // ...

      @Override
      public boolean equals(Object o) {
          if (!(o instanceof Point)) {
              return false;
          }
          if (!(o instanceof ColorPoint추이성위배)) {
              return o.equals(this);
          }
          return super.equals(o) && ((ColorPoint추이성위배) o).color == color;
      }
  }

  @Test
  public void 추이성위배() throws Exception {
      // given
      Point p = new Point(1, 2);
      ColorPoint추이성위배 cp = new ColorPoint추이성위배(1, 2, Color.RED.name());
      ColorPoint추이성위배 cp2 = new ColorPoint추이성위배(1, 2, Color.BLACK.name());

      // then
      assertEquals(cp, p);
      assertEquals(p, cp2);
      assertNotEquals(cp, cp2); // cp는 cp2와 같아야 함
  }
  ```

  - 무한 재귀에 빠질 위험도 있음

  ``` java
  public class ColorPoint추이성위배2 extends Point {
      private final Color color;

      public ColorPoint추이성위배2(int x, int y, Color color) {
          super(x, y);
          this.color = color;
      }

      @Override
      public boolean equals(Object o) {
          if (!(o instanceof Point)) {
              return false;
          }
          if (!(o instanceof ColorPoint추이성위배2)) {
              return o.equals(this);
          }
          return super.equals(o) && ((ColorPoint추이성위배2) o).color == color;
      }
  }

  @Test
  public void 추이성위배무한재귀() throws Exception {
      // given
      ColorPoint추이성위배 cp = new ColorPoint추이성위배(1, 2, Color.RED);
      ColorPoint추이성위배2 cp2 = new ColorPoint추이성위배2(1, 2, Color.BLACK);

      // then
      assertEquals(cp, cp2); // 무한재귀
  }
  ```

  - abstract를(클래스 계층구조. item23) 이용하거나 composition을(ex. Tiemstamp, Date) 이용하자

  ``` java
  public class ColorPoint추이성준수 {
      private final Color color; // composition
      private final Point point; // composition

      // ...

      @Override
      public boolean equals(Object o) {
          if (!(o instanceof ColorPoint추이성준수)) {
              return false;
          }
          ColorPoint추이성준수 cp = (ColorPoint추이성준수) o;
          return cp.point.equals(point) && cp.color.equals(color);
      }
  }

  @Test
  public void 추이성준수() throws Exception {
      // given
      Point p = new Point(1, 2);
      ColorPoint추이성준수 cp = new ColorPoint추이성준수(1, 2, Color.RED);
      ColorPoint추이성준수 cp2 = new ColorPoint추이성준수(1, 2, Color.BLACK);

      // then
      assertNotEquals(cp, p);
      assertNotEquals(p, cp2);
      assertNotEquals(cp2, cp);
  }
  ```

- 일관성
  - null이 아닌 x,y에 대해 x.equals(y)를 반복해서 호출한 결과는 동일하다
  - URL 예시

  ``` java
  @Test
  public void 일관성위배() throws Exception {
      // given
      URL url1 = new URL("http://example.com");
      URL url2 = new URL("http://example.com/");

      // then
      assertNotEquals(url1, url2);
      assertNotEquals(url1.hashCode(), url2.hashCode());
  }
  ```

  - 메모리상의 객체만을 사용한 deteministic 계산 수행
- null과 같지 않음
  - null이 아닌 x에 대해서 x.equals(null)은 false다
  - null체크보다 instanceof연산자 체크가 낫다(동치성 검사 -> 필드검사가 필요 -> 형변환 필요)

  ``` java
  public class Null아님NotGood {
      @Override
      public boolean equals(Object o) {
          if (o == null) { // 명시적 null 체크보다는
              return false;
          }
          Null아님NotGood n = (Null아님NotGood) o;
      }
  }

  public class Null아님Better {
      @Override
      public boolean equals(Object o) {
          if (!(o instanceof Null아님Better)) { // 필드 검사를 위해 형변환을 해야 하니 instanceof가 낫다
              return false;
          }
          Null아님Better n = (Null아님Better) o;
      }
  }
  ```

양질의 equals 구현법 정리(eqauls 가이드)

``` java
@Override
public boolean equals(Object o) {
    if (o == this) {
        return true;
    }
    if (!(o instanceof PointBest)) {
        return false;
    }
    PointBest p = (PointBest) o;
    return p.x == x && p.y == y;
}
```

- == 연산자를 이용해 입력이 자기자신 참조인지 확인(성능 최적화)
- instanceof 연산자로 입력이 올바른 타입인지 확인
- 입력을 올바른 타입으로 형변환
- 입력과 자신의 대응되는 핵심 필드가 일치하는지 확인
  - 2번에서 올바른 타입이 인터페이스라면, 인터페이스의 메소드를 이용한다
  - float, double 제외한 기본타입은 ==로 비교
    - float, double은 각각 Float.compare, Double.compare로 비교
    - Float.eqauls, Double.eqauls는 오토박싱 수반 가능하므로 지양
  - 참조는 각각의 eqauls로 비교
  - 배열도 앞서 지침에 따라 비교, 모든 원소를 봐야되면 Arrays.eqauls 메소드 중 하나 사용
  - null은 Objects.equals
  - 복잡한 필드를 가진 클래스(ex. CaseInsensitiveString)는 필드의 표준형(canonical form)을 저장해서 비교에 사용하면 경제적
    - 불변클래스에 특히 제격이고, 가변클래스라면 값이 바뀔때마다 표준형을 최신으로 갱신
- eqauls를 구현했다면 세가지 자문 및 테스트. AutoValue 사용했으면 할 필요 없음
  - 대칭적인가?
  - 추이성이 있는가?
  - 일관적인가?

  ``` java
  @AutoValue // equals, hashCode, toString, builder
  public abstract class PointAutoValue {
      public static Builder builder() {
          return new AutoValue_PointAutoValue.Builder();
      }

      public abstract int x();

      public abstract int y();


      @AutoValue.Builder
      public abstract static class Builder {

          public abstract Builder x(int x);

          public abstract Builder y(int y);

          public abstract PointAutoValue build();
      }
  }

  @Test
  public void autoValue() throws Exception {
      PointAutoValue autoValue = PointAutoValue.builder()
              .x(1)
              .y(2)
              .build();
      assertEquals(1, autoValue.x());
      assertEquals(2, autoValue.y());

      // You probably don't need to write assertions like these; just illustrating.
      PointAutoValue x1_y2 = PointAutoValue.builder()
              .x(1)
              .y(2)
              .build();
      assertTrue(x1_y2.equals(autoValue));

      PointAutoValue x2_y2 = PointAutoValue.builder()
              .x(2)
              .y(2)
              .build();
      assertFalse(x2_y2.equals(autoValue));

      PointAutoValue x1_y4 = PointAutoValue.builder()
              .x(1)
              .y(2)
              .build();
      assertFalse(x1_y4.equals(autoValue));
      assertEquals("PointAutoValue{x=1, y=2}", autoValue.toString());
  }
  ```

추가적인 점검사항

- equals를 재정의할 땐 hashCode로 반드시 재정의
- 너무 복잡하게 해결하지 말자
  - 필드들의 동치성만
  - 별칭은 비교하지 않는게 좋음(ex. Fiile 클래스라면, 심볼릭 링크를 비교해 같은 파일을 가리키는지 확인하려하면 안된다)
- Object 타입 외 매개변수로 받는 equals는 선언하지 말자
  - 재정의가 아닌 다중정의

  ``` java
  // Method does not override method from its superclass
  @Override
  public boolean equals(PointWrongEquals o) { // Object가 아닌 PointWrongEquals
      if (!(o instanceof PointWrongEquals)) {
          return false;
      }
      PointWrongEquals p = (PointWrongEquals) o;
      return p.x == x && p.y == y;
  }
  ```

찐 마지막 정리

- 꼭 필요한 경우가 아니면 equals를 재정의 하지말자
- 재정의할거면 핵심 필드를 다섯가지 규약을 지켜가며 비교

## Item 11. equals를 재정의하려거든 hashCode도 재정의하라

> Hash~~와 같은 collection을 사용할 경우, hashCode를 재정의 하지 않으면 문제가 생길 수 있음.

- hashCode 재정의 안했을때 HashMap 동작

``` java
public class Point {

    // hashCode 재정의 x

}

@Test
public void hashCode재정의안함() throws Exception {
    // given
    Map<Point, Integer> hm = new HashMap<>();

    // when
    hm.put(new Point(1, 2), 1);

    // then
    Integer get = hm.get(new Point(1, 2));
    assertEquals(null, get); // get의 반환값은 null
}
```

- hashCode 재정의 했을때 HashMap 동작

``` java
public class PointHashBad {

    // ...

    @Override
    public int hashCode() {
        return 1;
    }
}

public class PointHashGood {

    // ...

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}

@Test
public void hashCode재정의함() throws Exception {
    // given
    Map<PointHashGood, Integer> hmg = new HashMap<>();
    Map<PointHashBad, Integer> hmb = new HashMap<>();

    // when
    hmg.put(new PointHashGood(1, 2), 1);
    hmb.put(new PointHashBad(1, 2), 1);

    // then
    Integer get1 = hmg.get(new PointHashGood(1, 2));
    assertEquals(Integer.valueOf(1), get1); // get == 1, 다른 버킷
    Integer get2 = hmb.get(new PointHashBad(1, 2));
    assertEquals(Integer.valueOf(1), get2); // get == 1, 같은 버킷
}
```

Object 명세에서 발췌한 hashCode 관련 일반규약

- eqauls 비교에 사용되는 정보가 변경되지 않았다면, 어플리케이션이 실행되는 동안 그 객체의 hashCode는 동일해야 한다
- eqauls가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다
- eqauls가 두 객체를 다르다고 판단해도, 두 객체의 hashCode는 다른값을 반환할 필요는 없다. 단, 다른 객체에 대해서는 다른값을 반환해야 해시테이블 성능이 좋아진다.

  ``` java
  @Test
  public void hashCode에따른성능비교() throws Exception {
      // given
      Map<PointHashGood, Integer> hmg = new HashMap<>();
      ArrayList<PointHashGood> lg = new ArrayList<>();
      int loop = 10000;
      for (int i = 0; i < loop; i++) {
          lg.add(new PointHashGood(i, i));
      }

      // when
      for (int i = 0; i < lg.size(); i++) {
          hmg.put(lg.get(i), i);
      }

      // then
      TimeCheck.START();
      for (PointHashGood pointHashGood : lg) {
          hmg.get(pointHashGood); // get하는 성능 체크
      }
      TimeCheck.END();


      // given
      Map<PointHashBad, Integer> hmb = new HashMap<>();
      ArrayList<PointHashBad> lb = new ArrayList<>();
      for (int i = 0; i < loop; i++) {
          lb.add(new PointHashBad(i, i));
      }

      // when
      for (int i = 0; i < lb.size(); i++) {
          hmb.put(lb.get(i), i);
      }

      // then
      TimeCheck.START();
      for (PointHashBad pointHashBad : lb) {
          hmb.get(pointHashBad); // get하는 성능 체크
      }
      TimeCheck.END();
  }
  ```

hashCode 가이드

- 해시충돌을 줄여야 한다면 구아바의 `com.google.common.hash.Hashing`
- 간결하지만 성능은 조금 떨어지는 `Objects.hash`
- 클래스가 불변이고 해시코드 계산 비용이 크다면? 필드에 캐싱. 근데 주로 안쓰이면? lazy loading
- 해시코드 계산시 핵심필드는 무조건 들어가야 함
- 해시코드 생성 규칙을 api 사용자에게 자세히 공표하지 말 것, 그래야 사용자나 이 값에 의지하지 않고, 추후에 계산방식도 변경 가능

## Item 12. toString을 항상 재정의하라

> 잘 재정의하면 디버깅과 개발이 즐겁다\
> 스스로를 완벽히 설명하는 문자열

- toString 일반규약에 따르면, 간결하면서 사람이 읽기 쉬운 형태의 유익한 정보를 반환
- 사용되는 시점
  - println
  - printf
  - 문자열연결연산자(+)
  - assert 구문에 넘길때
  - 혹은 디버거가 객체를 출력 할 떄
- 스스로를 완벽히 설명하는 문자열
- 포맷 문서화는 선택이고, 의도는 확실히 밝혀야 함
- toString이 반환한 값에 포함된 정보를 얻어올 수 있는 api를 제공하자

## Item 13. clone 재정의는 주의해서 진행하라

> Cloneable 사용법, 대안책

Cloneable이란

- Cloneable은 protected Object.clone의 동작방식을 결정
- 인터페이스를 구현하는것은 일반적으로 특정 클래스에서 인터페이스의 기능을 제공한다는것.
- Cloneable은 상위 클래스의 protected 메소드 동작방식을 변경하는 것
- 구현하지 않은 클래스의 인스턴스에서 호출시 CloneNotSupportException

  ``` java
  @Test(expected = CloneNotSupportedException.class)
  public void clone재정의x() throws Exception {
      Point p = new Point(1, 2);
      /**
       * java: clone() has protected access in java.lang.Object
       * 예외가 아닌 컴파일 에러?
       */
      // Point p2 = p.clone(); //
  }
  ```

- clone을 재정의한 메소드에서 throws절을 없애는게 낫다(item-71)
- clone은 원본객체에 아무런 해를 끼치지 않는 동시에 복제된 객체의 불변식을 보장
- 가변객체(배열, elements)을 clone할 때, clone을 재귀적으로 호출

  ``` java
  @Override
  public StackClone clone() throws CloneNotSupportedException {
      // 공변 반환 타이핑(재정의한 메소드의 반환타입(StackClone)은, 상위클래스(Object)의 하위클래스(StackClone)가 될 수 있다)
      StackClone result = (StackClone) super.clone(); // 1) elements는 얕은복사
      result.elements = elements.clone(); // 2) elements 깊은복사

      result.elements2d = elements2d.clone(); // 3) 다차원 clone은 얕은복사
      for (int i = 0; i < elements2d.length; i++) { // 3) 재귀적으로 clone 호출
          result.elements2d[i] = elements2d[i].clone();
      }
      return result;
  }
  ```

- elements가 final이면 clone은 동작하지 않음

  ``` java
  public class StackClone implements Cloneable {
      private final Object[] elementsFinal;

      // ...

      @Override
      public StackClone clone() throws CloneNotSupportedException {
          // ...

          /**
           * Cloneable 아키텍처는 가변 객체를 참조하는 필드는 final로 선언하라는 일반 용법(불변성)과 충돌
           * java: cannot assign a value to final variable elementsFinal
           */
          result.elementsFinal = elementsFinal.clone();
          return result;
      }

      // ...

  }
  ```

- 가변객체의 또 다른 예시로 HashTable을 보면 clone를 구현하고, 해당 엔트리가 가르키는 연결리스트를 재귀적으로 clone 호출

  ``` java
  public class Hashtable<K,V>
    extends Dictionary<K,V>
    implements Map<K,V>, Cloneable, java.io.Serializable {
        public synchronized Object clone() {
            Hashtable<?,?> t = cloneHashtable(); // 1) super.clone
            t.table = new Entry<?,?>[table.length];
            for (int i = table.length ; i-- > 0 ; ) {
                t.table[i] = (table[i] != null)
                    ? (Entry<?,?>) table[i].clone() : null; // 2) deepcopy
            }
            // ...
            return t;
        }

          /** Calls super.clone() */
          final Hashtable<?,?> cloneHashtable() {
              try {
                  return (Hashtable<?,?>)super.clone();
              } catch (CloneNotSupportedException e) {
                  // this shouldn't happen, since we are Cloneable
                  throw new InternalError(e);
              }
          }

          /**
           * Hashtable bucket collision list entry
           */
          private static class Entry<K,V> implements Map.Entry<K,V> {

              // ...

              /* 책에서는 recursive 대신 iterator 쓰라고 했지만 실제 코드는 recursive임 */
              @SuppressWarnings("unchecked")
              protected Object clone() {
                  return new Entry<>(hash, key, value,
                                     (next==null ? null : (Entry<K,V>) next.clone()));
              }

            // ...

          }
    }
  ```

clone을 정리하면

- public이면서 클래스 자신을 반환하고,
- super.clone 호출 후 깊은구조에 숨어있는 모든 가변객체를 복사하고
- 복제본이 가진 객체 참조가 모두 복사된 객체들을 가리키게 해야 함
- 기본타입필드, 불변객체참조만 갖는 클래스라면, 아무 필드도 수정 x

하지만, Cloneable보다 나은 복사생성자, 복사팩터리 제공 가능

  ``` java
  public class PointCloneAlternative {
      // ...

      // 복사 생성자
      public PointCloneAlternative(PointCloneAlternative other) {
          this.x = other.x;
          this.y = other.y;
      }

      // 정적 복사 팩토리
      public static PointCloneAlternative valueOf(PointCloneAlternative other) {
          return new PointCloneAlternative(other.x, other.y);
      }

      // ...
  }

  @Test
  public void clone대안책() throws Exception {
      // given
      int x = 1;
      int y = 2;
      PointCloneAlternative p1 = new PointCloneAlternative(x, y);
      PointCloneAlternative cp1 = PointCloneAlternative.valueOf(p1);
      PointCloneAlternative cp2 = new PointCloneAlternative(p1);

      // then
      assertEquals(p1, cp1);
      assertEquals(cp1, cp2);
  }
  ```

clone과 다르게 복사생성자, 복사팩터리는 아래의 장점이 있다.

- 생성자를 쓰지않는방식의 객체 생성 매커니즘을 사용하지 않는다
- 엉성하게 문서화된 규약에 기대지 않고, 정상적인 final 필드 용법과도 충돌하지 않는다
- 불필요한 검사 예외를 던지지 않고, 형변환도 필요치 않는다
- 해당 클래스가 구현한 인터페이스 타입의 인스턴스를 인수로 받을 수 있다

## Item 14. Comparable을 구현할지 고려하라

> 정렬이 필요하다면, Comparable을 구현하라(혹은 Comparator)\
> 규약 위배시 정렬된 컬렉션(TreeSet, TreeMap) 혹은 검색과 정렬 알고리즘을 활용하는 유틸리티(Collections, Arrays)와 융화되지 못함\
> 값 비교시에는 <, > 는 지양하고, 박싱된기본클래스가제공하는 compare 혹은 Comparator 인터페이스가 제공하는 비교자 생성메소드를 사용

Comparable

- 단순하게 동치성(0)과 순서(1,-1)를 비교할 수 있는 Generic 인터페이스
- Comparable를 구현한 인스턴스에는 자연적인 순서가 있음을 의미하고 정렬은 Arrays.sort(Comparable)

compareTo 메소드 일반규약

- sgn(x.compareTo(y)) == -sgn(y.compareTo(x))
  - x.compareTo(y)가 1이면 반대는 -1
  - 즉, x가 y보다 작으면 y는 x보단 커야 함
- x.compareTo(y) > 0 && y.compareTo(z) > 0이면 x.compareTo(z) > 0
  - 추이성
- x.compareTo(y) == 0 이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z))
  - x,y가 같으면 z에 대해서 동일하다
- 권고사항. (x.compareTo(y) == 0 ) == (x.equals(y))
  - compareTo가 0이면 eqauls도 0이여야 한다

  ``` java
  @Test
  public void 규약4번위배() throws Exception {
      // given
      BigDecimal a = new BigDecimal("1.0");
      BigDecimal b = new BigDecimal("1.00");
      Set<BigDecimal> hs = new HashSet<>();
      Set<BigDecimal> ts = new TreeSet<>();

      // when
      hs.add(a);
      hs.add(b);
      ts.add(a);
      ts.add(b);
      int compare = a.compareTo(b);

      // then
      assertNotEquals(a, b); // 1.0과 1.00은 다름
      assertEquals(0, compare); // compare에서는 동일함
      assertEquals(2, hs.size()); // 2, HashSet에는 내부적으로 equals를
      assertEquals(1, ts.size()); // 1, TreeSet에서는 내부적으로 compareTo를
  }
  ```

비교 방식

- 관계연산자 <, >는 지양, 대신 자바7부터 기본타입의 compare 이용

  ``` java
  @Override
  public int compareTo(PointComparable o) {
      /* 직접적인 >, < 사용은 지양. 대신 박싱클래스의 정적 compare 메소드 */
      int res = Integer.compare(x, o.x); // 우선순위 1번째 필드
      if (res == 0) {
          /* 직접적인 >, < 사용은 지양. 대신 박싱클래스의 정적 compare 메소드 */
          res = Integer.compare(y, o.y); // 우선순위 2번째 필드
      }
      return res;
  }
  ```

자바 8부터는 Comparator

- 성능저하가 있지만, 자바 숫자용 기본 타입을 모두 커버할 수 있다
- 직관적이고 체이닝이되고 간결함

  ``` java
  @Test
  public void comparator비교자생성메소드() throws Exception {
      // given
      Comparator<Point> COMPARATOR = // 비교자 정의
              Comparator.comparingInt((Point p) -> p.x)
                      .thenComparingInt(p -> p.y);

      ArrayList<Point> l = new ArrayList<>();

      // ...

      // when
      l.sort(COMPARATOR); // 정렬
      System.out.println("--- after sort ---");

      // ...
  }
  ```

  ``` java
    @Test
    public void comparator정적compare메소드() throws Exception {
        // given
        /* Comparable의 compareTo 재정의와 동일 */
        Comparator<Point> COMPARATOR = new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                int res = Integer.compare(o1.x, o2.x); // 우선순위 1번째 필드
                if (res == 0) {
                    res = Integer.compare(o1.y, o2.y); // 우선순위 2번째 필드
                }
                return res;
            }
        };

        ArrayList<Point> l = new ArrayList<>();

        // ...

        // when
        l.sort(COMPARATOR); // 정렬
        System.out.println("--- after sort ---");

        // ...
    }
  ```

정리하면

- 정렬이 필요하다면 Comparable 인터페이스를 구현하고, 해당 인스턴스를 정렬,검색,비교할 수 있는 컬렉션과 어우러지도록 한다
- compareTo에서 비교시 <, >는 지양
- 대신에 박싱된 기본타입 클래스가 제공하는 정적 compare 메소드 혹은 Comparator 인터페이스가 제공하는 비교자 생성 메소드를 사용
