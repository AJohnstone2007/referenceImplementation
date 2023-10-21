package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import org.junit.Test;
public class FloatTest extends LexerBase {
@Test
public void uptoOneMillion() throws Exception {
for (float i = 0f; i < 1e6; i += 1.723f) {
assertRecognized(String.valueOf(i));
}
}
@Test
public void noLeadingZero() throws Exception {
assertRecognized(".1234567890");
}
@Test
public void badDigits() throws Exception {
assertNotRecognized("0110", "0");
}
@Override
protected int expectedTokenType() {
return JSLLexer.FLOATCONSTANT;
}
}
