package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.tokens.Token;

/**
 * Node representing a command which generates output dynamically. Inherits from
 * the node class.
 * 
 * @author Erik Banek
 */
public class EchoNode extends Node {
    /**
     * Tokens that will be output.
     */
    private Token[] tokens;

    /**
     * Constructs a read-only EchoNode.
     * 
     * @param tokens
     *            array of Token that belong to this echo node.
     */
    public EchoNode(Token[] tokens) {
        this.tokens = tokens;
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitEchoNode(this);
    }

    /**
     * Gets all the tokens of echo node in an array.
     * 
     * @return token array of node.
     * */
    public Token[] getTokens() {
        return this.tokens;
    }
}
