package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.collections.EmptyStackException;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.tokens.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a custom type script, parsing results are located in main Node. Throws
 * SmartScriptParserException if there is any problem anywhere.
 * 
 * @author Erik Banek
 */
public class SmartScriptParser {
    /**
     * Node that closes the FOR tag from the custom scripting language, used
     * only for parsing.
     * 
     * @author Erik Banek
     */
    public static class EndNode extends Node {
        @Override
        public void accept(INodeVisitor visitor) {
        }
    }

    /**
     * Head node that contains all document information after parsing, actually
     * the node hierarchy of the document.
     */
    private DocumentNode mainNode;
    /** String of document to parse. */
    private String docString;

    /**
     * Array of characters of document to parse.
     */
    private char[] docArray;

    /** Makes generating graph of nodes easier. */
    private ObjectStack stack;

    /**
     * Constructs a read-only parser, that will contain all the parsed
     * information in the mainNode field.
     * 
     * @param docBody
     *            String to be parsed.
     */
    public SmartScriptParser(String docBody) {
        docString = docBody;
        docArray = docBody.toCharArray();
        stack = new ObjectStack();
        mainNode = new DocumentNode();
        stack.push(mainNode);
        parseIt();
    }

    /**
     * Adds the given node to the current top node on the stack of nodes.
     * 
     * @param node
     *            to be added to the top node in the hierarchy.
     */
    private void addNodeToTopNode(Node node) {
        Node topNode = null;
        try {
            topNode = (Node) stack.peek();
        } catch (EmptyStackException e) {
            throw new SmartScriptParserException(
                    "Wrong number of closing tags!");
        }
        topNode.addChildNode(node);
    }

    /**
     * Finds the index of tag end starting from index in the argument.
     * 
     * @param currentIndex
     *            index from which the tag end is searched for.
     * @return index of tag end, that is after currentIndex.
     */
    private int findNextTagEnd(int currentIndex) {
        while (currentIndex < docArray.length) {
            int possibleEnd = docString.indexOf("$}", currentIndex);

            int nextQuoteIndex = currentIndex;
            boolean openedQuote = false;
            boolean isContained = false;

            while (nextQuoteIndex < possibleEnd) {
                int nextQuote = docString.indexOf("\"", nextQuoteIndex);

                if (nextQuote == -1 && openedQuote) {
                    throw new SmartScriptParserException("Unclosed quote!");
                } else if (nextQuote == -1) {
                    break;
                }

                if (isEscaped(nextQuote)) {
                    nextQuoteIndex = nextQuote + 1;
                    continue;
                }

                if (openedQuote && nextQuote > possibleEnd) {
                    currentIndex = nextQuote + 1;
                    isContained = true;
                    break;
                } else if (openedQuote) {
                    openedQuote = false;
                    nextQuoteIndex = nextQuote + 1;
                } else {
                    openedQuote = true;
                    nextQuoteIndex = nextQuote + 1;
                }
            }
            if (!isContained) {
                return possibleEnd;
            } else {
                currentIndex = Math.max(currentIndex, possibleEnd + 1);
            }
        }

        return docArray.length;
    }

    /**
     * Finds the next start of opened tag.
     * 
     * @param currentIndex
     *            from which the start of opened tag is searched for.
     * @return next index of start of opened tag, or length of document if no
     *         start was found.
     */
    private int findNextTagStart(int currentIndex) {
        while (currentIndex < docArray.length) {
            int possibleStart = docString.indexOf("{$", currentIndex);
            if (possibleStart == -1) {
                return docArray.length;
            }
            if (possibleStart == 0 || docArray[possibleStart - 1] != '\\') {
                return possibleStart;
            }
            currentIndex = possibleStart + 2;
        }
        return docArray.length;
    }

    /**
     * Gets the mainNode that contains parsed string information that was passed
     * as an argument to the constructor.
     * 
     * @return mainNode containing the parsed document information.
     * @throws NullPointerException
     *             if parsing wasn't successful, but the mainNode is wanted.
     */
    public DocumentNode getDocumentNode() {
        return mainNode;
    }

    /**
     * Checks if current char at given position is escaped.
     * 
     * @param currentIndex
     *            position of char whose escaping is checked.
     * @return true iff the char is escaped.
     */
    private boolean isEscaped(int currentIndex) {
        int numberOfEscapes = 0;
        while (currentIndex > 0 && docArray[currentIndex - 1] == '\\') {
            currentIndex--;
            numberOfEscapes++;
        }
        return numberOfEscapes % 2 == 1;
    }

    /**
     * Main body of parser. Goes through sequence of chars in string and
     * separates TextNodes and tags.
     * 
     * @throws SmartScriptParserException
     *             if a problem occurs with parsing
     */
    private void parseIt() {
        int currentIndex = 0;

        while (currentIndex < docArray.length) {
            int tagStart = findNextTagStart(currentIndex);
            int tagEnd = findNextTagEnd(tagStart + 2); // inclusive

            if (tagStart != docArray.length && tagEnd == docArray.length) {
                throw new SmartScriptParserException("Unclosed tag!");
            }

            if (tagStart != currentIndex) {
                TextNode text = stringToTextNode(docString.substring(
                        currentIndex, tagStart));
                addNodeToTopNode(text);
                if (tagStart == docArray.length) {
                    break;
                }
            }

            Node tagNode = parseTag(tagStart, tagEnd);

            if (tagNode instanceof EndNode) {
                try {
                    stack.pop();
                } catch (EmptyStackException e) {
                    throw new SmartScriptParserException(
                            "More end tags than for tags!");
                }
            } else {
                addNodeToTopNode(tagNode);
            }

            if (tagNode instanceof ForLoopNode) {
                stack.push(tagNode);
            }
            currentIndex = tagEnd + 2;
        }

        if (stack.size() != 1) {
            throw new SmartScriptParserException(
                    "Wrong number of closing tags!");
        }
    }

    /**
     * Parses a tag into a node.
     * 
     * @param tagStart
     *            index of tag start in {@code docString}.
     * @param tagEnd
     *            index of tag end in {@code docString}.
     * @return Node representing the tag.
     */
    private Node parseTag(int tagStart, int tagEnd) {
        tagStart += 2;
        // String tagString = docString.substring(tagStart, tagEnd);
        List<Token> tokenList = new ArrayList<>();
        int currentIndex = tagStart;
        while (currentIndex < tagEnd) {
            char c = docArray[currentIndex];
            if (c == ' ' || c == '\n' || c == '\t') {
                currentIndex++;
                continue;
            }
            ATokenCreator creator = null;
            try {
                creator = TokenCreators.getCreator(docString,
                        currentIndex);
            } catch (IllegalArgumentException e) {
                throw new SmartScriptParserException(e.getMessage());
            }

            currentIndex = creator.getEnd();
            tokenList.add(creator.getToken());
        }

        Node node = null;
        try {
            node = NodeCreators.create(tokenList.toArray(new Token[1]));
        } catch (IllegalArgumentException e) {
            throw new SmartScriptParserException(e.getMessage());
        }
        return node;
    }

    /**
     * Converts text for TextNode to form that acknowledges escaping.
     * 
     * @param toConvert
     *            String with escaping, should be parsed.
     * @return TextNode that contains the text from String without escaping.
     */
    private TextNode stringToTextNode(String toConvert) {
        char[] arrayToConvert = toConvert.toCharArray();
        StringBuilder convertBuilder = new StringBuilder();

        for (int i = 0; i < arrayToConvert.length; i++) {
            if (arrayToConvert[i] == '\\'
                    && i != (arrayToConvert.length - 1)
                    && (arrayToConvert[i + 1] == '\\' || arrayToConvert[i + 1] == '{')) {
                // append that which is escaped, and skip
                convertBuilder.append(arrayToConvert[i + 1]);
                i++;
                continue;
            }
            // if there is no escape, standard append
            convertBuilder.append(arrayToConvert[i]);
        }

        return new TextNode(convertBuilder.toString());
    }
}
