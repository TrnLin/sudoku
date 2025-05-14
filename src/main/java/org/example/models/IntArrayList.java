package org.example.models;

/**
 * A specialized ArrayList implementation that stores arrays of integers.
 * This class provides dynamic array functionality specifically for int[] arrays.
 */
public class IntArrayList {
    /** The array of int arrays that stores the elements */
    public int[][] elements;
    /** The number of elements in this list */
    public int size;
    /** The capacity of the internal array */
    public int capacity;

    /**
     * Constructs an IntArrayList with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     */
    public IntArrayList(int initialCapacity) {
        this.capacity = initialCapacity;
        this.elements = new int[initialCapacity][];
        this.size = 0;
    }

    /**
     * Constructs an IntArrayList with a default capacity of 16.
     */
    public IntArrayList() {
        this(16); // Default capacity
    }

    /**
     * Adds the specified int array to the end of this list.
     * Expands the capacity if necessary.
     *
     * @param element the int array to be added to this list
     */
    public void add(int[] element) {
        if (size >= capacity) {
            // Double the capacity when needed
            capacity *= 2;
            int[][] newElements = new int[capacity][];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
        elements[size++] = element;
    }

    /**
     * Returns the int array at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the int array at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public int[] get(int index) {
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
