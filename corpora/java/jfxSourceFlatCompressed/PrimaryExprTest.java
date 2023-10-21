package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.tree.Expr;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import com.sun.scenario.effect.compiler.tree.LiteralExpr;
import com.sun.scenario.effect.compiler.tree.VariableExpr;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Before;
import org.junit.Test;
import static com.sun.scenario.effect.compiler.parser.Expressions.SIMPLE_EXPRESSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class PrimaryExprTest extends ParserBase {
private String primary;
@Before
public void setUp() {
this.primary = primary();
}
@Test
public void variable() throws Exception {
Expr tree = parseTreeFor("foo");
assertTrue(tree instanceof VariableExpr);
assertEquals(((VariableExpr)tree).getVariable().getName(), "foo");
}
@Test
public void intLiteral() throws Exception {
Expr tree = parseTreeFor("123");
assertTrue(tree instanceof LiteralExpr);
assertEquals(((LiteralExpr)tree).getValue(), Integer.valueOf(123));
}
@Test
public void floatLiteral() throws Exception {
Expr tree = parseTreeFor("1.234");
assertTrue(tree instanceof LiteralExpr);
assertEquals(((LiteralExpr)tree).getValue(), Float.valueOf(1.234f));
}
@Test
public void boolLiteralT() throws Exception {
Expr tree = parseTreeFor("true");
assertTrue(tree instanceof LiteralExpr);
assertEquals(((LiteralExpr)tree).getValue(), Boolean.TRUE);
}
@Test
public void boolLiteralF() throws Exception {
Expr tree = parseTreeFor("false");
assertTrue(tree instanceof LiteralExpr);
assertEquals(((LiteralExpr)tree).getValue(), Boolean.FALSE);
}
@Test
public void bracketted() throws Exception {
Expr tree = parseTreeFor("(" + primary + ")");
}
@Test(expected = ParseCancellationException.class)
public void notAPrimaryExpression() throws Exception {
parseTreeFor("!(@&#");
}
private Expr parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
visitor.getSymbolTable().declareVariable("foo", Type.INT, null);
return visitor.visitPrimary_expression(parser.primary_expression());
}
protected String primary() {
return SIMPLE_EXPRESSION;
}
}
