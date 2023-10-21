package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.ThrowingErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.*;
public abstract class LexerBase {
protected void assertRecognized(char ch) throws Exception {
assertRecognized(String.valueOf(ch));
}
protected void assertRecognized(String text) throws Exception {
Token token = recognize(text);
assertEquals(text, token.getText());
if (expectedTokenType() != Integer.MIN_VALUE) {
assertEquals(expectedTokenType(), token.getType());
}
}
protected void assertNotRecognized(char ch) throws Exception {
assertNotRecognized(String.valueOf(ch));
}
protected void assertNotRecognized(String text) throws Exception {
assertNotRecognized(text, text);
}
protected void assertNotRecognized(String text, String shouldLex) throws Exception {
Token token = recognize(text);
assertEquals(shouldLex, token.getText());
if (expectedTokenType() != Integer.MIN_VALUE) {
assertFalse(expectedTokenType() == token.getType());
}
}
protected Token recognize(String text) throws Exception {
JSLLexer lexer = lexerOver(text);
return lexer.nextToken();
}
private JSLLexer lexerOver(String text) throws IOException {
InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
CharStream charStream = CharStreams.fromStream(stream, StandardCharsets.UTF_8);
JSLLexer lexer = new JSLLexer(charStream);
lexer.removeErrorListeners();
lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
return lexer;
}
protected int expectedTokenType() {
return Integer.MIN_VALUE;
}
}
