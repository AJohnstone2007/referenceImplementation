package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
public class IdentifierTest extends LexerBase {
@Test
public void basic() throws Exception {
assertRecognized("foo");
}
@Test
public void mixedCase() throws Exception {
assertRecognized("aAbBCCdd");
}
@Test
public void lettersAndDigits() throws Exception {
assertRecognized("aA29");
}
@Test
public void lettersAndDigitsAndSymbols() throws Exception {
assertRecognized("$aA___29");
}
@Test
public void notAnId1() throws Exception {
assertNotRecognized("6foo", "6");
}
@Test(expected = ParseCancellationException.class)
public void notAnId2() throws Exception {
assertRecognized("###");
}
@Override
protected int expectedTokenType() {
return JSLLexer.IDENTIFIER;
}
}
