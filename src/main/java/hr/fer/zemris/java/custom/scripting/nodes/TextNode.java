package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Node representing a piece of textual data. Inherits from node class.
 * 
 * @author Erik Banek
 */
public class TextNode extends Node {
    /**
     * Textual data of node
     */
    private String text;

    /**
     * Constructs a read-only TextNode with string text.
     * 
     * @param text
     *            data of TextNode to be constructed.
     */
    public TextNode(String text) {
        this.text = text;
    }

    @Override
    public void accept(INodeVisitor visitor) {
        visitor.visitTextNode(this);
    }

    /**
     * Gets textual data of TextNode.
     * 
     * @return text this node represents.
     */
    public String getText() {
        return this.text;
    }
}
