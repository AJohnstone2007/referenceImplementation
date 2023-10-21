package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import org.junit.Test;
public class IntegerTest extends LexerBase {
@Test
public void uptoOneMillion() throws Exception {
for (int i = 0; i < 1e6; ++i) {
assertRecognized(String.valueOf(i));
}
}
@Test
public void badDigits() throws Exception {
recognize("H128376");
}
@Override
protected int expectedTokenType() {
return JSLLexer.INTCONSTANT;
}
}
