package org.example.models;

/**
 * A set implementation for integers using a boolean array.
 * This implementation is optimized for small integer values (1 to capacity).
 */
public class IntSet {
    /** Boolean array representing elements in the set (true if present) */
    public boolean[] elements;
    /** Current number of elements in the set */
    public int size;
    /** Maximum value that can be stored in this set */
    public int capacity;

    /**
     * Creates a new IntSet with the specified capacity.
     * 
     * @param capacity The maximum integer value that can be stored
     */
    public IntSet(int capacity) {
        this.capacity = capacity;
        this.elements = new boolean[capacity + 1]; // +1 since we're using 1-based indices
        this.size = 0;
    }

    /**
     * Creates a new IntSet with default capacity of 16.
     */
    public IntSet() {
        this(16); // Default capacity
    }

    /**
     * Creates a new IntSet that is a copy of another IntSet.
     * 
     * @param other The IntSet to copy
     */
    public IntSet(IntSet other) {
        this.capacity = other.capacity;
        this.elements = new boolean[capacity + 1];
        this.size = other.size;
        
        for (int i = 1; i <= capacity; i++) {
            this.elements[i] = other.elements[i];
        }
    }

    /**
     * Adds a value to the set if it's not already present.
     * 
     * @param value The integer to add (must be between 1 and capacity)
     * @return true if the value was added, false if it was already in the set or out of range
     */
    public boolean add(int value) {
        if (value < 1 || value > capacity) {
            return false;
        }
        
        if (!elements[value]) {
            elements[value] = true;
            size++;
            return true;
        }
        return false; // Value was already in the set
    }

    /**
     * Removes a value from the set if it's present.
     * 
     * @param value The integer to remove
     * @return true if the value was removed, false if it wasn't in the set or out of range
     */
    public boolean remove(int value) {
        if (value < 1 || value > capacity || !elements[value]) {
            return false;
        }
        
        elements[value] = false;
        size--;
        return true;
    }

    /**
     * Checks if a value is in the set.
     * 
     * @param value The integer to check
     * @return true if the value is in the set, false otherwise
     */
    public boolean contains(int value) {
        if (value < 1 || value > capacity) {
            return false;
        }
        return elements[value];
    }

    /**
     * Removes all elements from the set.
     */
    public void clear() {
        for (int i = 1; i <= capacity; i++) {
            elements[i] = false;
        }
        size = 0;
    }

    /**
     * Returns the number of elements in the set.
     * 
     * @return The number of elements
     */
    public int size() {
        return size;
    }

    /**
     * Checks if the set has no elements.
     * 
     * @return true if the set is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
}
