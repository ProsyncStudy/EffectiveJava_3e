package study.prosync.ch3;

import java.util.Arrays;

public class StackClone implements Cloneable {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private final Object[] elementsFinal;
    private Object[] elements;
    private Object[][] elements2d;


    private int size = 0;

    public StackClone() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
        elementsFinal = new Object[DEFAULT_INITIAL_CAPACITY];
        elements2d = new Object[DEFAULT_INITIAL_CAPACITY][DEFAULT_INITIAL_CAPACITY];
    }

    @Override
    public StackClone clone() throws CloneNotSupportedException {
        // 공변 반환 타이핑(재정의한 메소드의 반환타입(StackClone)은, 상위클래스(Object)의 하위클래스(StackClone)가 될 수 있다)
        StackClone result = (StackClone) super.clone(); // 1) elements는 얕은복사
        result.elements = elements.clone(); // 2) elements 깊은복사

        result.elements2d = elements2d.clone(); // 3) 다차원 clone은 얕은복사
        for (int i = 0; i < elements2d.length; i++) { // 3) 재귀적으로 clone 호출
            result.elements2d[i] = elements2d[i].clone();
        }

        /**
         * Cloneable 아키텍처는 가변 객체를 참조하는 필드는 final로 선언하라는 일반 용법(불변성)과 충돌
         * java: cannot assign a value to final variable elementsFinal
         */
        // result.elementsFinal = elementsFinal.clone();
        return result;
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() throws Exception {
        if (size == 0) {
            throw new Exception("Empty");
        }
        return elements[--size];
    }

    public Object[] getElements() {
        return elements;
    }

    /**
     * Ensure space for at least one more element, roughly
     * doubling the capacity each time the array needs to grow.
     */
    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
