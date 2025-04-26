package org.example.model;

public class IntSet {
    public boolean[] elements;
    public int size;
    public int capacity;

    public IntSet(int capacity) {
        this.capacity = capacity;
        this.elements = new boolean[capacity + 1]; // +1 since we're using 1-based indices
        this.size = 0;
    }

    public IntSet() {
        this(16); // Default capacity
    }

    // Copy constructor
    public IntSet(IntSet other) {
        this.capacity = other.capacity;
        this.elements = new boolean[capacity + 1];
        this.size = other.size;
        
        for (int i = 1; i <= capacity; i++) {
            this.elements[i] = other.elements[i];
        }
    }

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

    public boolean remove(int value) {
        if (value < 1 || value > capacity || !elements[value]) {
            return false;
        }
        
        elements[value] = false;
        size--;
        return true;
    }

    public boolean contains(int value) {
        if (value < 1 || value > capacity) {
            return false;
        }
        return elements[value];
    }

    public void clear() {
        for (int i = 1; i <= capacity; i++) {
            elements[i] = false;
        }
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
