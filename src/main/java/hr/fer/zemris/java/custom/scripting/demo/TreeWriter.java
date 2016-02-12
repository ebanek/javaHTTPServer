package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.INodeVisitor;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Class that serves mainly as a demo program. Gets a file and interprets it as
 * a SmartScript. Then the SmartScriptParses parses that file and creates a main
 * {@code DocumentNode}. After that, the Visitor defined here visits that
 * hierarchy of nodes and outputs script based on the hierarchy of nodes that is
 * semantically the same as the one given.
 * 
 * @author Erik Banek
 */
public class TreeWriter {
    /**
     * A visitor of node hierarchy created by the {@code SmartScriptParser}.
     * Outputs the text that is semantically identical to the node hierarchy if
     * interpreted by the parser.
     * 
     * @author Erik Banek
     */
    private static class WriterVisitor implements INodeVisitor {

        /**
         * Appends to given StringBuilder tokens that are in the FOR tag.
         * 
         * @param sb
         *            StringBuilder to append to.
         * @param node
         *            the node whose tokens will be appended.
         */
        private static void appendForLoopVariables(StringBuilder sb,
                ForLoopNode node) {
            sb.append(node.getVariable().asText() + " ");
            sb.append(node.getStartExpression().asText() + " ");
            if (node.getStepExpression() != null) {
                sb.append(node.getStepExpression().asText() + " ");
            }
            sb.append(node.getEndExpression().asText() + " ");
        }

        /**
         * Visits all children of a {@code Node}.
         * 
         * @param node
         *            whose children is to be visited.
         */
        private void visitChildren(Node node) {
            int size = node.numberOfChildren();
            for (int i = 0; i < size; i++) {
                node.getChild(i).accept(this);
            }
        }

        @Override
        public void visitDocumentNode(DocumentNode node) {
            visitChildren(node);
        }

        @Override
        public void visitEchoNode(EchoNode node) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{$= ");

            for (int i = 0; i < node.getTokens().length; i++) {
                stringBuilder.append(node.getTokens()[i].asText());
                stringBuilder.append(" ");
            }

            stringBuilder.append("$}");
            System.out.print(stringBuilder.toString());
        }

        @Override
        public void visitForLoopNode(ForLoopNode node) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{$FOR ");
            appendForLoopVariables(stringBuilder, node);
            stringBuilder.append("$}");
            System.out.print(stringBuilder.toString());

            visitChildren(node);
            System.out.print("{$END$}");
        }

        @Override
        public void visitTextNode(TextNode node) {
            String text = node.getText();

            StringBuilder stringBuilder = new StringBuilder();
            char[] array = text.toCharArray();
            for (int i = 0; i < array.length; i++) {
                if (array[i] == '\\') {
                    stringBuilder.append("\\\\");// beautiful
                } else if (array[i] == '{') {
                    stringBuilder.append("\\{");
                } else {
                    stringBuilder.append(array[i]);
                }
            }
            System.out.print(stringBuilder.toString());
        }

    }

    /**
     * Gets, parses, and calls the visitor on the {@code DocumentNode}.
     * 
     * @param args
     *            one path to file conaining a SmartScript.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Wrong number of command line arguments!"
                    + "\nThere should be exactly one path to document.");
            return;
        }
        String docBody = null;
        try {
            docBody = new String(Files.readAllBytes(Paths.get(args[0])),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Wrong filepath!");
            return;
        }

        SmartScriptParser p = new SmartScriptParser(docBody);
        WriterVisitor visitor = new WriterVisitor();
        p.getDocumentNode().accept(visitor);
    }
}
