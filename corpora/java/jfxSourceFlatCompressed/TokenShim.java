package com.sun.javafx.css.parser;
import static com.sun.javafx.css.parser.Token.EOF;
public class TokenShim {
public static final int EOF = Token.EOF;
public static final int INVALID = Token.INVALID;
public static final int SKIP = Token.SKIP;
public final static TokenShim EOF_TOKEN = new TokenShim(Token.EOF_TOKEN);
public final static TokenShim INVALID_TOKEN = new TokenShim(Token.INVALID_TOKEN);
public final static TokenShim SKIP_TOKEN = new TokenShim(Token.SKIP_TOKEN);
private final Token token;
public TokenShim(int type, String text, int line, int offset) {
token = new Token(type, text, line, offset);
}
public TokenShim(int type, String text) {
token = new Token(type, text);
}
public TokenShim(Token t) {
token = t;
}
public int getType() {
return token.getType();
}
public int getLine() {
return token.getLine();
}
public int getOffset() {
return token.getOffset();
}
public String getText() {
return token.getText();
}
}
