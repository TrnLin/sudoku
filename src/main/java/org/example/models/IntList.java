package org.example.models;

/**
 * A dynamic array implementation for integers with automatic resizing.
 * This class provides methods to store and access integers in a list-like structure.
 */
public class IntList {
    /** The array holding the elements of this list */
    public int[] elements;
    /** The number of elements in this list */
    public int size;
    /** The current capacity of the internal array */
    public int capacity;

    /**
     * Constructs an IntList with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     */
    public IntList(int initialCapacity) {
        this.capacity = initialCapacity;
        this.elements = new int[initialCapacity];
        this.size = 0;
    }

    /**
     * Constructs an IntList with a default capacity of 16.
     */
    public IntList() {
        this(16); // Default capacity
    }

    /**
     * Adds an element to the end of this list.
     * Automatically resizes the internal array if needed.
     *
     * @param element the element to be added
     */
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

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index the index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return elements[index];
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list
     */
    public int size() {
        return size;
    }
}
