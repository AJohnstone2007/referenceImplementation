package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import org.junit.Test;
public class WhitespaceTest extends LexerBase {
@Test
public void tab() throws Exception {
assertRecognized('\t');
}
@Test
public void space() throws Exception {
assertRecognized(' ');
}
@Test
public void newLine() throws Exception {
assertRecognized('\n');
}
@Test
public void carriageReturn() throws Exception {
assertRecognized('\r');
}
@Test
public void nonSpace() throws Exception {
assertNotRecognized('4');
}
@Override
protected int expectedTokenType() {
return JSLLexer.WS;
}
}
