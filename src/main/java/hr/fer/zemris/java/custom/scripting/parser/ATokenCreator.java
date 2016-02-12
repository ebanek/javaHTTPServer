package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.scripting.tokens.Token;

/**
 * Class specifying a creator of token, which returns the actual token and the
 * index of the end of token inside the document String.
 * 
 * @author Erik Banek
 */
public abstract class ATokenCreator {
    /** Start index of token in String. */
    private int fromIndex;
    /** String containing all document text. */
    private String docString;
    /** Token which is created. */
    protected Token token;
    /** End of created token. */
    protected int end;

    /**
     * Constructor.
     * 
     * @param docString
     *            containing all document text.
     * @param fromIndex
     *            start of token which is parsed.
     * @throws IllegalArgumentException
     *             if a problem occurs with parsing the token.
     */
    public ATokenCreator(String docString, int fromIndex)
            throws IllegalArgumentException {
        this.fromIndex = fromIndex;
        this.docString = docString;
    }

    /**
     * Exclusive end of token, the place after which the String of token has
     * ended.
     * 
     * @return index of place after which the token ended.
     */
    public int getEnd() {
        return end;
    }

    /***
     * String of token from the document text.
     * 
     * @return String of token.
     */
    public String getString() {
        return docString.substring(fromIndex, getEnd());
    }

    /**
     * Token which was created.
     * 
     * @return created token.
     */
    public Token getToken() {
        return token;
    }
}
