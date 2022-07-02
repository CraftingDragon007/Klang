//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.kotw.klang;

import java.util.HashMap;
import java.util.Stack;

/**
 * Klang Parser
 * A class to parse klang files.
 * @author KekOnTheWorld
 * @since 1.0.0
 */
public class KlangParser {
    private String klang;
    private int klangLen;
    private int index;
    private int line;
    private int lineIndex;
    private Stack<String> key;

    /**
     * parseKlang - Parses a klang file.
     * @return A HashMap containing the parsed klang file.
     * @throws KlangException If an error occurs.
     */
    public HashMap<String, Object> parseKlang() throws KlangException {
        HashMap<String, Object> result = new HashMap();

        while(!this.isEnd()) {
            char c = this.next();
            if (c == 0) {
                break;
            }

            if (c == ')') {
                this.key.pop();
            } else {
                String key = this.isStringChar(c) ? this.stringConclude() : this.wordConclude();
                if (key == "") {
                    throw new KlangException("key required!", this);
                }

                c = this.next();
                this.key.push(key);
                if (c != '(') {
                    if (this.isStringChar(c)) {
                        String val = this.stringConclude();
                        result.put(String.join(".", this.key), val);
                        this.key.pop();
                    } else {
                        if (!this.isNumChar(c)) {
                            throw new KlangException("type STRING, NUM or KEY_COLLECTION required but found '" + c + "'!", this);
                        }

                        int val = this.numConclude();
                        result.put(String.join(".", this.key), val);
                        this.key.pop();
                    }
                }
            }
        }

        if (this.key.size() != 0) {
            throw new KlangException("EOF but " + this.key.size() + " unclosed key collection(s) found!", this);
        } else {
            return result;
        }
    }

    /**
     * KlangParser - Constructor.
     * @param klang The klang file to parse.
     */
    public KlangParser(String klang) {
        this.klang = klang;
        this.index = -1;
        this.line = 1;
        this.lineIndex = 0;
        this.klangLen = klang.length();
        this.key = new Stack();
    }

    /**
     * stringConclude - Concludes a string.
     * @return The string.
     * @throws KlangException If an error occurs.
     */
    public String stringConclude() throws KlangException {
        StringBuilder builder = new StringBuilder();
        char strc = this.getChar();
        boolean stre = false;
        boolean i = false;

        for(char c = this.nextChar(); !this.isEnd(); c = this.nextChar()) {
            if (this.charIsEscaped(c)) {
                throw new KlangException("unescaped special char '\\" + this.charUnescape(c) + "' found while concluding string!", this);
            }

            if (stre) {
                builder.append(this.charEscape(c));
                stre = false;
            } else if (c == '\\') {
                stre = true;
            } else {
                if (c == strc) {
                    return builder.toString();
                }

                builder.append(c);
            }
        }

        throw new KlangException("EOF while concluding string!", this);
    }

    /**
     * numConclude - Concludes a number.
     * @return The number.
     */
    public int numConclude() {
        StringBuilder builder = new StringBuilder();

        for(char c = this.getChar(); this.isNumChar(c) && !this.isEnd(); c = this.nextChar()) {
            builder.append(c);
        }

        this.previousChar();
        return Integer.parseInt(builder.toString());
    }

    /**
     * wordConclude - Concludes a word.
     * @return The word.
     */
    public String wordConclude() {
        StringBuilder builder = new StringBuilder();

        for(char c = this.getChar(); this.isWordChar(c) && !this.isEnd(); c = this.nextChar()) {
            builder.append(c);
        }

        this.previousChar();
        return builder.toString();
    }

    /**
     * getChar - Gets the current char.
     * @return The current char.
     */
    public char getChar() {
        return !this.isEnd() && !this.isStart() ? this.klang.charAt(this.index) : '\u0000';
    }

    /**
     * nextChar - Gets the next char.
     * @return The next char.
     */
    public char nextChar() {
        ++this.index;
        char c = this.getChar();
        ++this.lineIndex;
        if (c == '\n') {
            ++this.line;
            this.lineIndex = 0;
        }

        return c;
    }

    /**
     * previousChar - Gets the previous char.
     * @return The previous char.
     */
    public char previousChar() {
        --this.index;
        char c = this.getChar();
        --this.lineIndex;
        if (c == '\n') {
            --this.line;
            this.lineIndex = 0;
        }

        return c;
    }

    /**
     * next - Gets the next char.
     * @return The next char.
     */
    public char next() {
        while(!this.isEnd() && this.isEmptyChar(this.nextChar())) {
        }

        return this.getChar();
    }

    /**
     * previous - Gets the previous char.
     * @return The previous char.
     */
    public char previous() {
        while(!this.isEnd() && this.isEmptyChar(this.previousChar())) {
        }

        return this.getChar();
    }

    /**
     * isEmptyChar - Checks if the char is empty.
     * @param c The char to check.
     * @return True if the char is empty, false otherwise.
     */
    public boolean isEmptyChar(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    /**
     * Checks if the current char is a string char.
     * @param c The char to check.
     * @return True if the char is a string char.
     */
    public boolean isStringChar(char c) {
        return c == '"' || c == '\'';
    }

    /**
     * Checks if the given char is a number.
     * @param c The char to check.
     * @return True if the char is a number, false otherwise.
     */
    public boolean isNumChar(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * isEnd - checks if the end of the klang file has been reached.
     * @return true if the end of the klang file has been reached, false otherwise.
     */
    public boolean isEnd() {
        return this.index >= this.klangLen;
    }

    /**
     * isStart - checks if the start of the klang file has been reached.
     * @return true if the start of the klang file has been reached, false otherwise.
     */
    public boolean isStart() {
        return this.index < 0;
    }

    /**
     * charEscape - escape special chars in string
     * @param c char to escape
     * @return escaped char
     */
    public char charEscape(char c) {
        return c == 'n' ? '\n' : (c == 'r' ? '\r' : (c == 't' ? '\t' : c));
    }

    /**
     * charIsEscaped - checks if a char is escaped
     * @param c the char to check
     * @return true if the char is escaped, false otherwise
     */
    public boolean charIsEscaped(char c) {
        return c == '\n' || c == '\r' || c == '\t';
    }

    /**
     * Unescape special chars in string.
     * @param c char to unescape
     * @return unescaped char
     */
    public char charUnescape(char c) {
        return c == '\n' ? 'n' : (c == '\t' ? 't' : (c == '\r' ? 'r' : c));
    }

    /**
     * isWordChar
     * @param c char to check
     * @return boolean
     */
    public boolean isWordChar(char c) {
        return c != '(' && c != ')' && c != '"' && c != '\'' && c != ' ' && c != '\n' && c != '\r' && c != '\t';
    }

    /**
     * An exception thrown by the KlangParser.
     */
    public static class KlangException extends Exception {
        /**
         * The message of the exception.
         */
        private final String message;
        /**
         * The parser that threw the exception.
         */
        private final KlangParser parser;

        /**
         * Constructor.
         * @param msg The message.
         * @param parser The parser.
         */
        public KlangException(String msg, KlangParser parser) {
            super(msg);
            this.message = msg;
            this.parser = parser;
        }

        /**
         * getMessage - Gets the message.
         * @return The message.
         */
        public String getMessage() {
            StringBuilder builder = new StringBuilder();
            builder.append("\n\nKlang Error at ").append(this.parser.line).append("::").append(this.parser.lineIndex).append(":\n");
            int partStart = Math.max(0, this.parser.index - this.parser.lineIndex + 1);
            builder.append(this.parser.line).append(" | ").append(this.parser.klang, partStart, this.parser.index).append(" <- HERE: ").append(this.message).append("\n\n");
            return builder.toString();
        }
    }
}
