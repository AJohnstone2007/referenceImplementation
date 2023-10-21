package com.sun.scenario.effect.compiler.lexer;
import com.sun.scenario.effect.compiler.JSLLexer;
import org.junit.Test;
public class LineCommentTest extends LexerBase {
@Test
public void comment() throws Exception {
assertRecognized("// ignored\n");
}
@Test
public void notAComment() throws Exception {
assertNotRecognized("ignored");
}
@Override
protected int expectedTokenType() {
return JSLLexer.LINE_COMMENT;
}
}
