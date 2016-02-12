package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.tokens.Token;
import hr.fer.zemris.java.custom.scripting.tokens.TokenVariable;

/**
 * Node representing a single for-loop construct. Inherits from node class. All
 * fields except stepExpression cannot be null.
 * 
 * @author Erik Banek
 */
public class ForLoopNode extends Node {
    /** Holds the name of variable in the loop. */
    private TokenVariable variable;
    /** Start expression in loop, usually a number. */
    private Token startExpression;
    /** End expression in loop, usually a number. */
    private Token endExpression;
    /** Step expression for exotic for-loops. Can be null. */
    private Token stepExpression;

    /**
     * Constructs a read-only for-loop node with three arguments.
     * 
     * @param variable
     *            with which for loop iterates.
     * @param startExpression
     *            start value of variable.
     * @param endExpression
     *            end value from which the variable is less than or equal.
     */
    public ForLoopNode(TokenVariable variable, Token startExpression,
            Token endExpression) {
        this(variable, startExpression, endExpression, null);
    }

    /**
     * Constructs a read-only for-loop with four constructor arguments.
     * 
     * @param variable
     *            with which for loop iterates.
     * @param startExpression
     *            start value of variable.
     * @param endExpression
     *            end value from which the variable is less than or equal.
     * @param stepExpression
     *            step of variable.
     * 
     * @throws IllegalArgumentException
     *             if any of first three values are null.
     */
    public ForLoopNode(TokenVariable variable, Token startExpression,
            Token endExpression, Token stepExpression) {
        if (variable == null || startExpression == null
                || endExpression == null) {
            throw new IllegalArgumentException();
        }
        this.variable = variable;
        this.startExpression = startExpression;
        this.endExpression = endExpression;
        this.stepExpression = stepExpression;
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitForLoopNode(this);
    }

    /**
     * Getter of read-only token.
     * 
     * @return wanted value;
     */
    public Token getEndExpression() {
        return this.endExpression;
    }

    /**
     * Getter of read-only token.
     * 
     * @return wanted value;
     */
    public Token getStartExpression() {
        return this.startExpression;
    }

    /**
     * Getter of read-only token.
     * 
     * @return wanted value;
     */
    public Token getStepExpression() {
        return this.stepExpression;
    }

    /**
     * Getter of read-only token.
     * 
     * @return wanted value;
     */
    public TokenVariable getVariable() {
        return this.variable;
    }
}
