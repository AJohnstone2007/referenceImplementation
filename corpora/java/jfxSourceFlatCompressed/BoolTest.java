package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import org.junit.Test;
public class BoolTest extends LexerBase {
@Test
public void trueLit() throws Exception {
assertRecognized("true");
}
@Test
public void falseLit() throws Exception {
assertRecognized("false");
}
public void notABool() throws Exception {
assertNotRecognized("629");
}
@Override
protected int expectedTokenType() {
return JSLLexer.BOOLCONSTANT;
}
}
