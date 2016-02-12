package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.tokens.Token;

/**
 * Creates a node from given array of tokens.
 * 
 * @author Erik Banek
 */
public interface INodeCreator {
    /**
     * Returns a node with given tokens.
     * 
     * @param tokens
     *            contained inside node to be created.
     * @return node.
     */
    Node create(Token[] tokens);
}
