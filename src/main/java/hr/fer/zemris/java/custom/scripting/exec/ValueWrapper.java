package hr.fer.zemris.java.custom.scripting.exec;

/**
 * Wraps some value and provides basic numerical operations around that value if
 * that value is a number in Integer, Double or String form.
 * 
 * @author Erik Banek
 */
public class ValueWrapper {
    /**
     * Handles converting an Object into a number, and remembering if that
     * number is a double or an int. Can convert Strings, Integers or Doubles.
     * Also handles numerical operations.
     * 
     * @author Erik Banek
     */
    private static class intOrDoubleWrap {
        /** Double value contained inside class. */
        private Double doubleValue;
        /** Integer value contained inside class. */
        private Integer intValue;
        /** Decides whether the contained value is an Integer od Double. */
        private boolean isInteger;

        /** Parsing helper. */

        /**
         * Constructs an intOrDoubleWrap instance. Null is interpreted as zero.
         * 
         * @param value
         *            to be held. Must be a number in String, Integer, or Double
         *            form.
         * @throws IllegalArgumentException
         *             if value is not a number in correct form.
         */
        private intOrDoubleWrap(Object value) {
            if (value == null) {
                this.doubleValue = 0.0;
                this.intValue = 0;
                this.isInteger = true;
            } else if (value instanceof Integer) {
                // if Integer we want double form
                this.intValue = (Integer) value;
                this.doubleValue = (double) this.intValue;
                this.isInteger = true;
            } else if (value instanceof String) {
                createFromString((String) value);
            } else if (value instanceof Double) {
                // if double, that form is the only one we care about
                this.doubleValue = (Double) value;
                this.intValue = -1;
                this.isInteger = false;
            } else {
                throw new IllegalArgumentException(
                        "Problems with interpreting the argument as a number!");
            }
        }

        /**
         * Checks if both wraps contain Integer values.
         * 
         * @param wrap1
         *            first argument to check
         * @param wrap2
         *            second argument to check
         * @return true if both parameters are int
         */
        private boolean bothInt(intOrDoubleWrap wrap1, intOrDoubleWrap wrap2) {
            return wrap1.isInteger && wrap2.isInteger;
        }

        /**
         * Handles creating the class from a String literal and deciding whether
         * it is an int or double.
         * 
         * @param value
         *            String literal which holds the number.
         */
        private void createFromString(String value) {
            if (value.indexOf("E") != -1 || value.indexOf("e") != -1
                    || value.indexOf(".") != -1) {
                this.isInteger = false;
            } else {
                this.isInteger = true;
            }
            parse(value);
        }

        /**
         * Subtracts two wraps and returns result.
         * 
         * @param wrap2
         *            value for which this wrap is decremented and result
         *            returned.
         * @return result of subtraction.
         */
        private Object decrement(intOrDoubleWrap wrap2) {
            if (bothInt(this, wrap2)) {
                return this.intValue - wrap2.intValue;
            } else {
                return this.doubleValue - wrap2.doubleValue;
            }
        }

        /**
         * Divides two wraps integrally or as floating point decimals. Integral
         * division is only if both wraps contain integers.
         * 
         * @param wrap2
         *            divisor.
         * @return result of division.
         */
        private Object divide(intOrDoubleWrap wrap2) {
            if (bothInt(this, wrap2)) {
                return this.intValue / wrap2.intValue;
            } else {
                return this.doubleValue / wrap2.doubleValue;
            }
        }

        /**
         * Adds two wraps and returns result.
         * 
         * @param wrap2
         *            value for which this wrap is incremented and result
         *            returned.
         * @return result of addition.
         */
        private Object increment(intOrDoubleWrap wrap2) {
            if (bothInt(this, wrap2)) {
                return this.intValue + wrap2.intValue;
            } else {
                return this.doubleValue + wrap2.doubleValue;
            }
        }

        /**
         * Multiplies two wraps and returns result.
         * 
         * @param wrap2
         *            multiplier.
         * @return result of multiplication.
         */
        private Object multiply(intOrDoubleWrap wrap2) {
            if (bothInt(this, wrap2)) {
                return this.intValue * wrap2.intValue;
            } else {
                return this.doubleValue * wrap2.doubleValue;
            }
        }

        /**
         * Compares two intOrDoubleWrap-s. Compares them as one would normally
         * expect, as numbers.
         * 
         * @param toCompare
         *            value with which this value will be compared.
         * @return 1 if this i greater than arg, -1 if arg is greater than this,
         *         equal otherwise.
         */
        private int numCompare(intOrDoubleWrap toCompare) {
            if (bothInt(this, toCompare)) {
                int val1 = this.intValue;
                int val2 = toCompare.intValue;
                if (val1 < val2) {
                    return -1;
                } else if (val1 > val2) {
                    return 1;
                } else {
                    return 0;
                }
            } else {
                if (this.doubleValue < toCompare.doubleValue) {
                    return -1;
                } else if (this.doubleValue > toCompare.doubleValue) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        /**
         * Parses the value contained in the String and stores it. Formats that
         * can be parsed are formats that the java {@code Double.parseDouble}
         * knows.
         * 
         * @param value
         *            which will be parsed.
         * @throws if
         *             value cannot be parsed.
         */
        private void parse(String value) {
            try {
                if (this.isInteger) {
                    this.intValue = Integer.parseInt(value);
                    this.doubleValue = (double) this.intValue;
                } else {
                    this.intValue = -1;
                    this.doubleValue = Double.parseDouble(value);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Problem with parsing number!");
            }
        }
    }

    /** Value which the wrapper holds. */
    private Object value;

    /**
     * Constructor.
     * 
     * @param value
     *            which the wrapper will hold.
     */
    public ValueWrapper(Object value) {
        this.value = value;
    }

    /**
     * Adds two ValueWrapper objects.
     * 
     * @param b
     *            to be added to this object.
     * @return this object after addition.
     */
    public ValueWrapper add(ValueWrapper b) {
        this.increment(b.value);
        return new ValueWrapper(this.value);
    }

    /**
     * Subtracts the value of this object with the passed value. Delegates
     * subtraction to the intOrDoubleWrap class.
     * 
     * @param decValue
     *            for which the value will be decreased.
     * @throws IllegalArgumentException
     *             if wrapped value or passed argument are not numbers in
     *             String, Integer or Double form.
     */
    public void decrement(Object decValue) {
        intOrDoubleWrap wrap1 = new intOrDoubleWrap(this.value);
        intOrDoubleWrap wrap2 = new intOrDoubleWrap(decValue);
        this.value = wrap1.decrement(wrap2);
    }

    /**
     * Divides two ValueWrapper objects.
     * 
     * @param b
     *            that divides this object.
     * @return this object after division.
     */
    public ValueWrapper div(ValueWrapper b) {
        this.divide(b.value);
        return new ValueWrapper(this.value);
    }

    /**
     * Divides the value of this object with the passed value. Delegates
     * division to the intOrDoubleWrap class.
     * 
     * @param divValue
     *            with which this.value will be divided.
     * @throws IllegalArgumentException
     *             if wrapped value or passed argument are not numbers in
     *             String, Integer or Double form.
     */
    public void divide(Object divValue) {
        intOrDoubleWrap wrap1 = new intOrDoubleWrap(this.value);
        intOrDoubleWrap wrap2 = new intOrDoubleWrap(divValue);
        this.value = wrap1.divide(wrap2);
    }

    /**
     * Value getter.
     * 
     * @return value that is wrapped around.
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Adds the passed value to the value of this object. Delegates addition to
     * the intOrDoubleWrap class.
     * 
     * @param incValue
     *            for which the value will be increased.
     * @throws IllegalArgumentException
     *             if wrapped value or passed argument are not numbers in
     *             String, Integer or Double form.
     */
    public void increment(Object incValue) {
        intOrDoubleWrap wrap1 = new intOrDoubleWrap(this.value);
        intOrDoubleWrap wrap2 = new intOrDoubleWrap(incValue);
        this.value = wrap1.increment(wrap2);
    }

    /**
     * Multiplies two ValueWrapper objects.
     * 
     * @param b
     *            to be multiplied with this object.
     * @return this object after multiplication.
     */
    public ValueWrapper mul(ValueWrapper b) {
        this.multiply(b.value);
        return new ValueWrapper(this.value);
    }

    /**
     * Multiplies the value of this object with the passed value. Delegates
     * multiplication to the intOrDoubleWrap class.
     * 
     * @param mulValue
     *            with which this.value will be multiplied.
     * @throws IllegalArgumentException
     *             if wrapped value or passed argument are not numbers in
     *             String, Integer or Double form.
     */
    public void multiply(Object mulValue) {
        intOrDoubleWrap wrap1 = new intOrDoubleWrap(this.value);
        intOrDoubleWrap wrap2 = new intOrDoubleWrap(mulValue);
        this.value = wrap1.multiply(wrap2);
    }

    /**
     * Compares numerically this.value with passed value. Delegates comparing to
     * the intOrDoubleWrap class. Does not work with non-numbers.
     * 
     * @param withValue
     *            with which the current value will be numerically compared.
     * @return 1 if this.value is greater than passed arg, -1 if passed arg is
     *         greater, 0 if equal.
     * @throws IllegalArgumentException
     *             if wrapped value or passed argument are not numbers in
     *             String, Integer or Double form.
     */
    public int numCompare(Object withValue) {
        intOrDoubleWrap wrap1 = new intOrDoubleWrap(this.value);
        intOrDoubleWrap wrap2 = new intOrDoubleWrap(withValue);
        return wrap1.numCompare(wrap2);
    }

    /**
     * Value setter.
     * 
     * @param value
     *            new wrapped value.
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Subtracts two ValueWrapper objects.
     * 
     * @param b
     *            to be subtracted from this object.
     * @return this object after subtraction.
     */
    public ValueWrapper sub(ValueWrapper b) {
        this.decrement(b.value);
        return new ValueWrapper(this.value);
    }

    @Override
    public String toString() {
        if (this.value instanceof String) {
            return (String) this.value;
        } else if (this.value instanceof Integer) {
            return Integer.valueOf((Integer) this.value).toString();
        } else {
            return Double.valueOf((Double) this.value).toString();
        }
    }
}
