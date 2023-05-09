- [Ch 3. 모든 객체의 공통 메서드](#ch-3-모든-객체의-공통-메서드)
  - [Item 10. equals는 일반 규약을 지켜 재정의하라](#item-10-equals는-일반-규약을-지켜-재정의하라)
  - [Item 11. equals를 재정의하려거든 hashCode도 재정의하라](#item-11-equals를-재정의하려거든-hashcode도-재정의하라)
  - [Item 12. toString을 항상 재정의하라](#item-12-tostring을-항상-재정의하라)
  - [Item 13. clone 재정의는 주의해서 진행하라](#item-13-clone-재정의는-주의해서-진행하라)
  - [Item 14. Comparable을 구현할지 고려하라](#item-14-comparable을-구현할지-고려하라)

# Ch 3. 모든 객체의 공통 메서드

- Object는 상속해서 사용하도록 설계됐으므로 Object의 메소드 제정의시 지켜야 하는 일반규약 존재
- 일반규약을 지키지 않을경우 HashSet, HashMap 같은 클래스에서 오작동 가능성
- comapreTo는 성격이 비슷해서 같이 소개

## Item 10. equals는 일반 규약을 지켜 재정의하라

equals를 재정의하지 않는게 최선인 경우

- 각 인스턴스가 본질적으로 고유하다
  - 값이 아닌 동작하는 개체를 표현(ex. Thread)

  ``` java
  TODO - 코드로 설명
  ```

- 인스턴스의 논리적 동치성을 검사할 일이 없다

  ``` java
  TODO - Pattern?
  ```

- 상의 클래스에서 재정의한 equals가 하위 클래스에도 적합

  ``` java
  TODO - 코드로 설명
  ```

  - AbstractSet -> Set 구현체들
  - AbstractMap -> Map 구현체들
- 클래스가 private이거나, package-private이고 equals를 호출할 일이 없다
  - equals 재정의에서 `throw new AssertionError();` 해도 됨

    ``` java
    TODO - 코드로 설명
    ```

equals를 재정의 해야 하는 경우

- 객체 식별성(object identity: 물리적으로 같은가)이 아니라 논리적 동치성(logical equality)을 확인할 때 && 상위클래스 equals가 논리적 동치성을 비교하지 않을 때 -> 주로 값객체가 해당됨
- 인스턴스가 1개라면 재정의 안해도 됨(enum, 인스턴스 통제 클래스)
- Object 명세에서 발췌한 equals 관련 일반규약
  - 반사성
    - null이 아닌 x에 대해 x.eqauls(x)는 true다
    - 어기면? Collection에 x를 넣고, contain(x)하면 false
  - 대칭성

    ``` java
    TODO - 위배되는 것만
    ```

    - null이 아닌 x,y에 대해 x.equals(y)가 true면, y.equals(x)도 true다
    - 어기면? Collecion<x>에 대해서 contain(y)는 true지만, 반대로 Collection<y>에 대해서 contain(x)는 false
    - CaseInsensitiveString의 equals를 string과 연동은 불가능
  - 추이성

    ``` java
    TODO - 위배되는 것만
    ```

    - null이 아닌 x,y,z에 대해 x.equals(y)가 true고, y.equals(z)도 true면, x.equals(z)도 true다
    - 구체적 클래스에 확장은 추이성을 만족할 수 없다
    - abstract를(클래스 계층구조. item23) 이용하거나 composition을(ex. Tiemstamp, Date) 이용하자
  - 일관성

    ``` java
    TODO - 위배되는 것만
    ```

    - null이 아닌 x,y에 대해 x.equals(y)를 반복해서 호출한 결과는 동일하다
    - 메모리상의 객체만을 사용한 deteministic 계산 수행
    - URL
  - non-null

    ``` java
    TODO - 위배되는 것만
    ```

    - null이 아닌 x에 대해서 x.equals(null)은 false다
    - 동치성 검사를 위해선 필드검사가 필요하고 이는 형변환을 의미 -> null체크보다 instanceof연산자 체크가 낫다
- 양질의 equals 구현법 정리(eqauls 가이드)

  ``` java
  TODO - psm entity 예시1
  ```

  - == 연산자를 이용해 입력이 자기자신 참조인지 확인(성능 최적화)
  - instanceof 연산자로 입력이 올바른 타입인지 확인

    ``` java
    TODO - 올바른 타입은 보통은 eqauls가 정의된 클래스이지만, 가끔은 클래스가 구현한 인터페이스가 될 수 있다
    ```

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

  ``` java
  TODO - psm entity 예시2 with AutoValue // intellij의 다른 여러 방식들과 성능 비교
  ```

  - 대칭적인가?
  - 추이성이 있는가?
  - 일관적인가?
- 추가적인 점검사항
  - equals를 재정의할 땐 hashCode로 만드시 재정의
  - 너무 복잡하게 해결하지 말자
    - 필드들의 동치성만
    - 별칭은 비교하지 않는게 좋음(ex. Fiile 클래스라면, 심볼릭 링크를 비교해 같은 파일을 가리키는지 확인하려하면 안된다)
  - Object 타입 외 매개변수로 받는 equals는 선언하지 말자
    - 재정의가 아닌 다중정의
    - 다중정의더라도 타입을 구체적 타입 명시는 1)하위클래스에서 @Override에서 거짓 양성(false positive), 2)보안 측면에서 잘못된 정보

    ``` java
    TODO - 컴파일 되지않고 뭐가 문제인지 알려주는 메시지
    ```

- 찐 마지막 정리
  - 꼭 필요한 경우가 아니면 equals를 재정의 하지말자
  - 재정의할거면 핵심 필드를 다섯가지 규약을 지켜가며 비교ß

## Item 11. equals를 재정의하려거든 hashCode도 재정의하라

- Object 명세에서 발췌한 hashCode 관련 일반규약
  - eqauls 비교에 사용되는 정보가 변경되지 않았다면, 어플리케이션이 실행되는 동안 그 객체의 hashCode는 동일해야 한다
  - eqauls가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다 // 논리적으로 같은 객체는 hashCode도 같다
  - eqauls가 두 객체를 다르다고 판단해도, 두 객체의 hashCode는 다른값을 반환할 필요는 없다. 단, 다른 객체에 대해서는 다른값을 반환해야 해시테이블 성능이 좋아진다.
- hashCode 가이드

  ``` java
  TODO  - 실제 코드 짜볼것 // AutoValue, 핵심 필드
  ```

- 해시충돌을 줄여야 한다면 구아바의 com.google.common.hash.Hashing
- 간결하지만 성능은 조금 떨어지는 Objects.hash
- 클래스가 불변이고 해시코드 계산 비용이 크다면? 필드에 캐싱. 근데 주로 안쓰이면? lazy loading
- 해시코드 계산시 핵심필드는 무조건 들어가야 함
- 해시코드 생성 규칙을 api 사용자에게 자세히 공표하지 말 것, 그래야 사용자나 이 값에 의지하지 않고, 추후에 계산방식도 변경 가능
  - 의존하지 말라 이거임

``` java
TODO - psm entity 예시 intellij의 다른 여러 방식들과 성능 비교
```

## Item 12. toString을 항상 재정의하라

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
  - TODO - 뭔소리지? 이걸 위반하면 추가적으로 반환값을 파싱해야된다는데?

## Item 13. clone 재정의는 주의해서 진행하라

- Clonable은 protected Object.clone의 동작방식을 결정
  - 인터페이스를 구현하는것은 일반적으로 특정 클래스에서 인터페이스의 기능을 제공한다는것.
  - Clonable은 상위 클래스의 protected 메소드 동작방식을 변경하느 것

## Item 14. Comparable을 구현할지 고려하라
