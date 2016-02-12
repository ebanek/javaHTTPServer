package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * Visitor that visits a node hierarchy.
 * 
 * @author Erik Banek
 */
public interface INodeVisitor {
    /**
     * Visits a document node.
     * 
     * @param node
     *            to be visited.
     * @throws VisitorException
     *             if something wrong occurs while visiting.
     */
    void visitDocumentNode(DocumentNode node) throws VisitorException;

    /**
     * Visits an echo node.
     * 
     * @param node
     *            to be visited.
     * @throws VisitorException
     *             if something wrong occurs while visiting.
     */
    void visitEchoNode(EchoNode node) throws VisitorException;

    /**
     * Visits a for loop node.
     * 
     * @param node
     *            to be visited.
     * @throws VisitorException
     *             if something wrong occurs while visiting.
     */
    void visitForLoopNode(ForLoopNode node) throws VisitorException;

    /**
     * Visits a text node.
     * 
     * @param node
     *            to be visited.
     * @throws VisitorException
     *             if something wrong occurs while visiting.
     */
    void visitTextNode(TextNode node) throws VisitorException;
}
