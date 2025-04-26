package org.example.classes;

public class IntList {
    public int[] elements;
    public int size;
    public int capacity;

    public IntList(int initialCapacity) {
        this.capacity = initialCapacity;
        this.elements = new int[initialCapacity];
        this.size = 0;
    }

    public IntList() {
        this(16); // Default capacity
    }

    public void add(int element) {
        if (size >= capacity) {
            // Double the capacity when needed
            capacity *= 2;
            int[] newElements = new int[capacity];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
        elements[size++] = element;
    }

    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return elements[index];
    }

    public int size() {
        return size;
    }
}
