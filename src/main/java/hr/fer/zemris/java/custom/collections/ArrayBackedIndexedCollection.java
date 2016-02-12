package hr.fer.zemris.java.custom.collections;

/**
 * Provides a custom implementation of a general purpose array. The array cannot
 * contain null references. No other restrictions are imposed.
 * 
 * @author Erik Banek
 */
public class ArrayBackedIndexedCollection {
    /**
     * Starting size of initialized array if no size is provided in constructor.
     */
    private static final int INITIAL_CAPACITY = 16;

    /**
     * How many elements are currently in the elements array. Those elements
     * have indexes from 0 to size-1 in that array. Size is always less than or
     * equal to capacity.
     */
    private int size;

    /** Current size of the elements array. */
    private int capacity;

    /** The underlying array of objects. */
    private Object[] elements;

    /** Uses predefined starting size for allocating the elements array. */
    public ArrayBackedIndexedCollection() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Allocates the underlying array with the given capacity, sets values for
     * set and capacity. The array always starts empty.
     * 
     * @param initialCapacity
     *            initial capacity of the array.
     * @throws IllegalArgumentException
     *             if initialCapacity is less than 1, so that array of that size
     *             cannot be allocated.
     */
    public ArrayBackedIndexedCollection(int initialCapacity)
            throws IllegalArgumentException {

        if (initialCapacity < 1) {
            throw new IllegalArgumentException();
        }
        this.elements = new Object[initialCapacity];
        this.capacity = initialCapacity;
        this.size = 0;
    }

    /**
     * Adds the object at the end of the array. Doubles array size if more
     * memory is needed. Throws exception if given reference is null.
     * 
     * @param value
     *            of the object to be added. Should not be null.
     * @throws IllegalArgumentException
     *             if a null is passed to the method as the object to be added.
     */
    public void add(Object value)
            throws IllegalArgumentException {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        if (this.size == this.capacity) {
            this.doubleCapacity();
        }

        this.elements[this.size++] = value;
    }

    /**
     * Sets all references in underlying array to null, and sets size to 0.
     * Doesn't resize the array.
     */
    public void clear() {
        for (int i = 0; i < this.size; i++) {
            this.elements[i] = null;
        }
        this.size = 0;
    }

    /**
     * Uses indexOf to check if an object is in the array.
     * 
     * @param value
     *            of the object that is checked.
     * @return true if the value is in the array, false otherwise.
     */
    public boolean contains(Object value) {
        if (indexOf(value) == -1) {
            return false;
        }
        return true;
    }

    /** Doubles the current capacity of the underlying (elements) array. */
    private void doubleCapacity() {
        Object[] newElements = new Object[2 * this.capacity];
        for (int i = 0; i < this.size; i++) {
            newElements[i] = this.elements[i];
        }
        this.elements = newElements;

        this.capacity = 2 * this.capacity;
    }

    /**
     * Accesses the underlying array with index. If index is out of bounds
     * throws an exception. Index should be inclusively between zero and
     * size()-1.
     * 
     * @param index
     *            of the wanted object from the array.
     * @return wanted object.
     * @throws IndexOutOfBoundsException
     *             if out of bounds.
     */
    public Object get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }

        return elements[index];
    }

    /**
     * Iterates through all objects in the array and checks for equality using
     * the equals method.
     * 
     * @param value
     *            of the object that is looked for in the array.
     * @return index of the wanted object in the underlying array, if it doesn't
     *         exist returns -1.
     */
    public int indexOf(Object value) {
        if (value == null)
            return -1;
        for (int i = 0; i < this.size; ++i) {
            if (this.elements[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Inserts an object at given position in array. Moves all objects that are
     * later than position in the array one place further in the array.
     * 
     * @param value
     *            of the object to be added. Should not be null.
     * @param position
     *            where the object should be added. Should not be greater than
     *            size() or negative.
     * 
     * @throws IndexOutOfBoundsException
     *             if index out of bounds.
     * @throws IllegalArgumentException
     *             if a null is passed to the method as the object to be
     *             inserted.
     */
    public void insert(Object value, int position)
            throws IndexOutOfBoundsException, IllegalArgumentException {
        if (position < 0 || position > this.size) {
            throw new IndexOutOfBoundsException();
        }
        if (value == null) {
            throw new IllegalArgumentException();
        }

        if (this.size == this.capacity) {
            this.doubleCapacity();
        }

        for (int i = this.size; i > position; i--) {
            this.elements[i] = this.elements[i - 1];
        }
        this.size++;

        this.elements[position] = value;
    }

    /**
     * Checks if underlying elements array is empty.
     * 
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Removes an object from the array. Moves objects to the left in the array
     * to satisfy the property that all objects are sequentially placed in
     * memory. If index is not correct, throws an exception.
     * 
     * @param index
     *            of the object to be removed.
     * @throws IndexOutOfBoundsException
     *             if index out of bounds.
     */
    public void remove(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }

        for (int i = index; i < (this.size - 1); i++) {
            this.elements[i] = this.elements[i + 1];
        }
        this.elements[this.size - 1] = null;
        this.size--;
    }

    /**
     * Returns current number of objects that the array holds.
     * 
     * @return current number of objects in the underlying array.
     */
    public int size() {
        return this.size;
    }
}
