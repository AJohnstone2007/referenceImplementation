package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import org.junit.Test;
public class CommentTest extends LexerBase {
@Test
public void comment() throws Exception {
assertRecognized("/* ignored */");
}
@Test
public void multilineComment() throws Exception {
assertRecognized("/* ignored \n * line 2 */");
}
@Test
public void notAComment() throws Exception {
assertNotRecognized("ignored");
}
@Override
protected int expectedTokenType() {
return JSLLexer.COMMENT;
}
}
