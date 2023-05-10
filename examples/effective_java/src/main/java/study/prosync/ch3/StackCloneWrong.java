package study.prosync.ch3;

import java.util.Arrays;

public class StackCloneWrong implements Cloneable {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private Object[] elements;
    private int size = 0;

    public StackCloneWrong() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    @Override
    public StackCloneWrong clone() throws CloneNotSupportedException {
        StackCloneWrong result = (StackCloneWrong) super.clone();
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
