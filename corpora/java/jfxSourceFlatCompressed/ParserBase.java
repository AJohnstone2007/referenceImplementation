package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLLexer;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.ThrowingErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
public abstract class ParserBase {
protected JSLParser parserOver(String text) throws IOException {
InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
CharStream charStream = CharStreams.fromStream(stream, StandardCharsets.UTF_8);
JSLLexer lexer = new JSLLexer(charStream);
lexer.removeErrorListeners();
lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
CommonTokenStream tokenStream = new CommonTokenStream(lexer);
JSLParser parser = new JSLParser(tokenStream);
parser.removeErrorListeners();
parser.addErrorListener(ThrowingErrorListener.INSTANCE);
return parser;
}
}
