package hr.fer.zemris.java.custom.collections;

/**
 * A general, generic stack that contains object references of any type, adapted
 * from the ArrayBackedIndexedCollection. Null elements are not allowed on the
 * stack.
 * 
 * <p>
 * All operations are O(1), except when the underlying array has to be resized.
 * 
 * @author Erik Banek
 */
public class ObjectStack {
    /**
     * Has all the functionalities that are needed for a stack.
     */
    private ArrayBackedIndexedCollection Adaptee;

    /**
     * Basic constructor of empty stack.
     */
    public ObjectStack() {
        this.Adaptee = new ArrayBackedIndexedCollection();
    }

    /**
     * Empties the stack.
     */
    public void clear() {
        this.Adaptee.clear();
    }

    /**
     * Checks if stack is empty.
     * 
     * @return true if stack is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.Adaptee.isEmpty();
    }

    /**
     * Gets the top object of the stack. Throws an exception if the stack is
     * empty.
     * 
     * @return the first object on the stack.
     * @throws EmptyStackException
     *             if stack is empty on call.
     */
    public Object peek() throws EmptyStackException {
        if (this.isEmpty())
            throw new EmptyStackException();
        // Gets the last element of Adaptee.
        return this.Adaptee.get(this.Adaptee.size() - 1);
    }

    /**
     * Gets the top object of the stack and removes it from the stack. Throws an
     * exception if the stack is empty.
     * 
     * @return the first object on the stack.
     * @throws EmptyStackException
     *             if stack is empty on call.
     */
    public Object pop() {
        if (this.isEmpty())
            throw new EmptyStackException();
        // Removes and gets the last element of Adaptee.
        Object returnValue = this.Adaptee.get(this.Adaptee.size() - 1);
        this.Adaptee.remove(this.Adaptee.size() - 1);
        return returnValue;
    }

    /**
     * Pushes the object on top of the stack.
     * 
     * @param value
     *            object to be pushed on the stack.
     * @throws IllegalArgumentException
     *             if user wants to push null object.
     */
    public void push(Object value) throws IllegalArgumentException {
        if (value == null)
            throw new IllegalArgumentException();
        this.Adaptee.add(value);
    }

    /**
     * Gets the size of the stack.
     * 
     * @return current number of objects on the stack.
     */
    public int size() {
        return this.Adaptee.size();
    }
}
