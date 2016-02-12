package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.tokens.Token;
import hr.fer.zemris.java.custom.scripting.tokens.TokenVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines all node creator.
 * 
 * @author Erik Banek
 */
public class NodeCreators {
    /**
     * Creator of an echo node.
     * 
     * @author Erik Banek
     */
    public static class EchoCreator implements INodeCreator {
        @Override
        public Node create(Token[] tokens) {
            return new EchoNode(tokens);
        }

    }

    /**
     * Creator of an end node.
     * 
     * @author Erik Banek
     */
    public static class EndCreator implements INodeCreator {
        @Override
        public Node create(Token[] tokens) {
            if (tokens.length != 0) {
                throw new IllegalArgumentException(
                        "End tag should contain nothing inside!");
            }
            return new SmartScriptParser.EndNode();
        }

    }

    /**
     * Creator of a for loop node.
     * 
     * @author Erik Banek
     */
    public static class ForCreator implements INodeCreator {

        @Override
        public Node create(Token[] tokens) {
            if (tokens.length != 3 && tokens.length != 4) {
                throw new IllegalArgumentException(
                        "Wrong number of tokens in for tag!");
            }
            TokenVariable variable = null;
            try {
                variable = (TokenVariable) tokens[0];
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(
                        "First token in for node should be a variable!");
            }

            if (tokens.length == 3) {
                return new ForLoopNode(variable, tokens[1], tokens[2]);
            } else {
                return new ForLoopNode(variable, tokens[1], tokens[2],
                        tokens[3]);
            }
        }

    }

    /** Map of all node creators with their lower case names mapped to them. */
    private static Map<String, INodeCreator> creators;

    static {
        Map<String, INodeCreator> map = new HashMap<>();
        map.put("for", new ForCreator());
        map.put("=", new EchoCreator());
        map.put("end", new EndCreator());
        creators = map;
    }

    /**
     * Creates a node based on the tokens array. The first token in array is the
     * name of the node to be create, actually the name of the tag.
     * 
     * @param tokens
     *            which the node contains.
     * @return node creator.
     */
    public static Node create(Token[] tokens) {
        if (tokens.length < 1) {
            throw new IllegalArgumentException("Tag should contain a name!");
        }

        INodeCreator creator = creators.get(tokens[0].asText().toLowerCase());
        if (creator == null) {
            throw new IllegalArgumentException(
                    "Can not create tag with that name!");
        }
        Token[] forCreation = new Token[tokens.length - 1];
        for (int i = 0; i < forCreation.length; i++) {
            forCreation[i] = tokens[i + 1];
        }

        for (Token t : forCreation) {
            if (t.asText().equals("=")) {
                throw new IllegalArgumentException("= cannot be a token!");
            }
        }

        return creator.create(forCreation);
    }
}
