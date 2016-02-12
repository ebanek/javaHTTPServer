package hr.fer.zemris.java.custom.scripting.exec.functions;

import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.exec.functions.context.ContextDeleter;
import hr.fer.zemris.java.custom.scripting.exec.functions.context.ContextGetter;
import hr.fer.zemris.java.custom.scripting.exec.functions.context.ContextSetter;
import hr.fer.zemris.java.webserver.RequestContext;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Defines all functions that are supported by the {@code SmartScriptEngine}.
 * 
 * @author Erik Banek
 */
public class SmartScriptFunctions {
    /**
     * + function.
     */
    private static ISmartScriptFunction add = new BinarySmartFunction(
            (a, b) -> {
                return a.add(b);
            });

    /**
     * - function.
     */
    private static ISmartScriptFunction sub = new BinarySmartFunction(
            (a, b) -> {
                return a.sub(b);
            });
    /**
     * * function.
     */
    private static ISmartScriptFunction mul = new BinarySmartFunction(
            (a, b) -> {
                return a.mul(b);
            });
    /**
     * divides function.
     */
    private static ISmartScriptFunction div = new BinarySmartFunction(
            (a, b) -> {
                return a.div(b);
            });
    /**
     * sin(x) function.
     */
    private static ISmartScriptFunction sin = new AbstractSmartScriptFunction(1) {
        @Override
        public void apply(ObjectStack stack, RequestContext rc)
                throws IllegalArgumentException, ClassCastException {
            check(stack);
            Double value = Double.parseDouble((String) stack.pop());
            stack.push(Double.valueOf(Math.sin(value)).toString());
        }
    };
    /**
     * Pushes the next to last number in the format of the last number on stack.
     */
    private static ISmartScriptFunction decfmt = new AbstractSmartScriptFunction(
            2) {
        @Override
        public void apply(ObjectStack stack, RequestContext rc)
                throws IllegalArgumentException, ClassCastException {
            check(stack);
            DecimalFormat df = new DecimalFormat((String) stack.pop());
            String num = (String) stack.pop();
            stack.push(df.format(Double.parseDouble(num)));
        }
    };
    /**
     * Duplicates the top number on the stack.
     */
    private static ISmartScriptFunction dup = new AbstractSmartScriptFunction(1) {
        @Override
        public void apply(ObjectStack stack, RequestContext rc)
                throws IllegalArgumentException, ClassCastException {
            check(stack);
            Object x = stack.peek();
            stack.push(x);
        }
    };
    /**
     * Swaps the top two numbers on the stack.
     */
    private static ISmartScriptFunction swap = new AbstractSmartScriptFunction(
            2) {
        @Override
        public void apply(ObjectStack stack, RequestContext rc)
                throws IllegalArgumentException, ClassCastException {
            check(stack);
            Object a = stack.pop();
            Object b = stack.pop();
            stack.push(a);
            stack.push(b);
        }
    };
    /**
     * Sets the mime type of the context.
     */
    private static ISmartScriptFunction setMimeType = new AbstractSmartScriptFunction(
            1) {
        @Override
        public void apply(ObjectStack stack, RequestContext rc)
                throws IllegalArgumentException, ClassCastException {
            check(stack);
            String x = (String) stack.pop();
            rc.setMimeType(x);
        }
    };
    /**
     * Gets some parameter of context.
     */
    private static ISmartScriptFunction paramGet = new ContextGetter(
            (key, rc) -> {
                return rc.getParameter(key);
            });
    /**
     * Gets some persistent parameter of context.
     */
    private static ISmartScriptFunction pparamGet = new ContextGetter(
            (key, rc) -> {
                return rc.getPersistentParameter(key);
            });
    /**
     * Gets some temporary parameter of context.
     */
    private static ISmartScriptFunction tparamGet = new ContextGetter(
            (key, rc) -> {
                return rc.getTemporaryParameter(key);
            });
    /**
     * Sets some persistent parameter of context.
     */
    private static ISmartScriptFunction pparamSet = new ContextSetter((key,
            value, rc) -> {
        rc.setPersistentParameter(key, value);
    });
    /**
     * Sets some temporary parameter of context.
     */
    private static ISmartScriptFunction tparamSet = new ContextSetter((key,
            value, rc) -> {
        rc.setTemporaryParameter(key, value);
    });
    /**
     * Deletes some persistent parameter of context.
     */
    private static ISmartScriptFunction pparamDel = new ContextDeleter(
            (key, rc) -> {
                rc.removePersistentParameter(key);
            });
    /**
     * Deletes some temporary parameter of context.
     */
    private static ISmartScriptFunction tparamDel = new ContextDeleter(
            (key, rc) -> {
                rc.removeTemporaryParameter(key);
            });
    /**
     * Returns the map of all functions mapped to their names.
     * 
     * @return map of function.
     */
    public static Map<String, ISmartScriptFunction> getFunctions() {
        Map<String, ISmartScriptFunction> map = new HashMap<>();
        map.put("sin", sin);
        map.put("decfmt", decfmt);
        map.put("dup", dup);
        map.put("swap", swap);
        map.put("setMimeType", setMimeType);
        map.put("paramGet", paramGet);
        map.put("pparamGet", pparamGet);
        map.put("tparamGet", tparamGet);
        map.put("pparamSet", pparamSet);
        map.put("tparamSet", tparamSet);
        map.put("pparamDel", pparamDel);
        map.put("tparamDel", tparamDel);
        map.put("+", add);
        map.put("-", sub);
        map.put("*", mul);
        map.put("/", div);
        return map;
    }
}
