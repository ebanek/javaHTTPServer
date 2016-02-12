package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.collections.EmptyStackException;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple multimap-like structure that enables association of multiple values
 * with one key. Associates Strings as keys with custom stacks, so when the key
 * of some stack is known, multiple values associated with that key make a
 * well-known stack structure.
 * 
 * @author Erik Banek
 */
public class ObjectMultistack {
    /**
     * Entry representing a node in a linked list, serves for implementing
     * stack-like behavior. Encapsulates the ValueWrapper class for use with
     * stacks.
     * 
     * @author Erik Banek
     */
    private static class MultistackEntry {
        /** Value contained in entry. */
        private ValueWrapper value;
        /** Entry that will be popped after this one in the stack. */
        private MultistackEntry next;

        /**
         * Basic constructor.
         * 
         * @param value
         *            which is used for constructing an entry.
         * @param next
         *            the next node in the list mapped to a single String key.
         */
        private MultistackEntry(ValueWrapper value, MultistackEntry next) {
            this.value = value;
            this.next = next;
        }
    }

    /** Map associating Strings with stacks. */
    Map<String, MultistackEntry> multistack;

    /** Constructor of an ObjectMultistack. */
    public ObjectMultistack() {
        this.multistack = new HashMap<String, MultistackEntry>();
    }

    /**
     * Checks if the stack associated with some String is empty.
     * 
     * @param name
     *            key of stack for which the emptiness is tested.
     * @return true iff the stack associated with name is empty (i.e. doesn't
     *         exist).
     */
    public boolean isEmpty(String name) {
        return !this.multistack.containsKey(name);
    }

    /**
     * Returns the top value from the stack associated with given key.
     * 
     * @param name
     *            key associated with the stack from which the top value will be
     *            returned.
     * @return top value.
     * @throws EmptyStackException
     *             if the stack associated with given key is empty, so that the
     *             nothing can be returned.
     */
    public ValueWrapper peek(String name) {
        if (this.isEmpty(name)) {
            throw new EmptyStackException();
        }
        return this.multistack.get(name).value;
    }

    /**
     * Pops the top value from the stack associated with given key.
     * 
     * @param name
     *            key associated with the stack from which the value will be
     *            popped.
     * @return popped value.
     * @throws EmptyStackException
     *             if the stack associated with given key is empty, so that the
     *             nothing can be popped.
     */
    public ValueWrapper pop(String name) {
        if (this.isEmpty(name)) {
            throw new EmptyStackException();
        }
        ValueWrapper returnValueWrapper = this.peek(name);
        this.remove(name);
        return returnValueWrapper;
    }

    /**
     * Pushes the value onto the stack associated with the given key.
     * 
     * @param name
     *            key associated with stack on which the value will be pushed.
     * @param valueWrapper
     *            value which will be pushed onto the stack.
     */
    public void push(String name, ValueWrapper valueWrapper) {
        if (this.isEmpty(name)) {
            MultistackEntry entry = new MultistackEntry(valueWrapper, null);
            this.multistack.put(name, entry);
        } else {
            MultistackEntry previousTop = this.multistack.get(name);
            MultistackEntry newTop = new MultistackEntry(valueWrapper,
                    previousTop);
            this.multistack.put(name, newTop);
        }
    }

    /**
     * Removes the top value from the stack associated with key. Assumes that
     * stack associated with key is non-empty.
     * 
     * @param name
     *            key of stack from which the top value will be removed.
     */
    private void remove(String name) {
        MultistackEntry entry = this.multistack.get(name);
        this.multistack.remove(name);
        if (!(entry.next == null)) {
            this.multistack.put(name, entry.next);
        }
    }
}
