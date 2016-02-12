package hr.fer.zemris.java.custom.scripting.exec;

import hr.fer.zemris.java.custom.collections.EmptyStackException;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.exec.functions.ISmartScriptFunction;
import hr.fer.zemris.java.custom.scripting.exec.functions.SmartScriptFunctions;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.nodes.VisitorException;
import hr.fer.zemris.java.custom.scripting.tokens.Token;
import hr.fer.zemris.java.custom.scripting.tokens.TokenFunction;
import hr.fer.zemris.java.custom.scripting.tokens.TokenOperator;
import hr.fer.zemris.java.custom.scripting.tokens.TokenVariable;
import hr.fer.zemris.java.webserver.RequestContext;

import java.io.IOException;
import java.util.Map;

/**
 * Engine which by the means of a {@code INodeVisitor} visits and actually
 * executes a custom SmartScript.
 * 
 * @author Erik Banek
 */
public class SmartScriptEngine {
    /** Map of all functions that are supported in scripts to their keyed names. */
    private static final Map<String, ISmartScriptFunction> functions =
            SmartScriptFunctions.getFunctions();
    /** Top node from whom the visit starts, and which holds all script data. */
    private DocumentNode documentNode;
    /** Context through which everything is output to the client. */
    private RequestContext requestContext;
    /** Multistack which is used for storing variables. */
    private ObjectMultistack multistack;
    /**
     * Visitor of each node, actually a script executor. Works with the request
     * context and multistack from this class. Executes the script by
     * interpreting it, uses the {@code ValueWrapper} for help with execution.
     */
    private INodeVisitor visitor = new INodeVisitor() {
        /**
         * Visits all children.
         */
        @Override
        public void visitDocumentNode(DocumentNode node)
                throws VisitorException {
            int size = node.numberOfChildren();
            for (int i = 0; i < size; i++) {
                node.getChild(i).accept(this);
            }
        }

        /**
         * Goes through all tokens in node, makes calculations/functions, and
         * outputs all values that are left in the stack in the FIFO order.
         */
        @Override
        public void visitEchoNode(EchoNode node) throws VisitorException {
            ObjectStack stack = new ObjectStack();

            // go through all tokens in node and do the calculations
            for (Token t : node.getTokens()) {
                if (t instanceof TokenFunction || t instanceof TokenOperator) {

                    ISmartScriptFunction f = functions.get(t.toString());
                    if (f == null) {
                        throw new VisitorException(
                                "Not an existing function is present!");
                    }
                    try {
                        f.apply(stack, requestContext);
                    } catch (IllegalArgumentException | ClassCastException e) {
                        throw new VisitorException(
                                "Problem with executing script!");
                    }

                } else if (t instanceof TokenVariable) {
                    Object o = null;
                    try {
                        o = multistack.peek(((TokenVariable) t).getName());
                    } catch (EmptyStackException e) {
                        throw new VisitorException("No such variable on stack!");
                    }
                    stack.push(o.toString());
                } else {
                    stack.push(t.toString());
                }
            }

            StringBuilder sb = new StringBuilder();
            while (stack.size() > 0) {
                sb.insert(0, stack.pop().toString());
            }
            try {
                requestContext.write(sb.toString());
            } catch (IOException e) {
                throw new VisitorException(
                        "Problem writing to context!");
            }
        }

        /**
         * Loops through the for loop node in the standard way, and visits all
         * of its children.
         */
        @Override
        public void visitForLoopNode(ForLoopNode node) throws VisitorException {
            ValueWrapper step = new ValueWrapper(1);

            if (node.getStepExpression() != null) {
                step = new ValueWrapper(node.getStepExpression().toString());
            }
            ValueWrapper start = new ValueWrapper(
                    node.getStartExpression().toString());
            ValueWrapper end = new ValueWrapper(
                    node.getEndExpression().toString());

            TokenVariable variable = node.getVariable();
            String varName = variable.getName();

            multistack.push(varName, start);

            while (multistack.peek(varName).numCompare(end.toString()) < 1) {
                int size = node.numberOfChildren();
                for (int i = 0; i < size; i++) {
                    node.getChild(i).accept(this);
                }
                multistack.peek(varName).increment(step.toString());
            }

            multistack.pop(varName);
        }

        /**
         * Outputs the text node to the context.
         */
        @Override
        public void visitTextNode(TextNode node) throws VisitorException {
            try {
                requestContext.write(node.getText());
            } catch (IOException e) {
                throw new VisitorException(
                        "Problem with writing text node to context!");
            }
        }
    };

    /**
     * Creates an engine with given document node and context.
     * 
     * @param documentNode
     *            whose content is executed.
     * @param requestContext
     *            context through whom output is handled.
     * @throws IllegalArgumentException
     *             iff some given argument is null.
     */
    public SmartScriptEngine(DocumentNode documentNode,
            RequestContext requestContext) throws IllegalArgumentException {
        if (documentNode == null || requestContext == null) {
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
        this.documentNode = documentNode;
        this.requestContext = requestContext;
        multistack = new ObjectMultistack();
    }

    /**
     * Executes the script contained in the document node.
     * 
     * @throws IOException
     *             if a problem occurs with writing to context.
     */
    public void execute() throws IOException {
        try {
            documentNode.accept(visitor);
        } catch (VisitorException e) {
            requestContext
                    .write(e.getMessage()
                            +
                            "\nError occured with executing script. Check URL parameters.");
        }
    }
}
