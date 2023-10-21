package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.model.UnaryOpType;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import com.sun.scenario.effect.compiler.tree.LiteralExpr;
import com.sun.scenario.effect.compiler.tree.UnaryExpr;
import com.sun.scenario.effect.compiler.tree.VariableExpr;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class UnaryExprTest extends PrimaryExprTest {
private String primary;
@Before
@Override
public void setUp() {
super.setUp();
this.primary = primary();
}
@Test
public void negated() throws Exception {
UnaryExpr tree = parseTreeFor("!true");
assertEquals(tree.getOp(), UnaryOpType.NOT);
assertEquals(((LiteralExpr)tree.getExpr()).getValue(), Boolean.TRUE);
}
@Test
public void positive() throws Exception {
UnaryExpr tree = parseTreeFor("+72.4");
assertEquals(tree.getOp(), UnaryOpType.PLUS);
assertEquals(((LiteralExpr)tree.getExpr()).getValue(), Float.valueOf(72.4f));
}
@Test
public void negative() throws Exception {
UnaryExpr tree = parseTreeFor("-72.4");
assertEquals(tree.getOp(), UnaryOpType.MINUS);
assertEquals(((LiteralExpr)tree.getExpr()).getValue(), Float.valueOf(72.4f));
}
@Test
public void preIncrement() throws Exception {
UnaryExpr tree = parseTreeFor("++foo");
assertEquals(tree.getOp(), UnaryOpType.INC);
assertEquals(((VariableExpr)tree.getExpr()).getVariable().getName(), "foo");
}
@Test
public void preDecrement() throws Exception {
UnaryExpr tree = parseTreeFor("--foo");
assertEquals(tree.getOp(), UnaryOpType.DEC);
assertEquals(((VariableExpr)tree.getExpr()).getVariable().getName(), "foo");
}
@Test
public void postIncrement() throws Exception {
UnaryExpr tree = parseTreeFor("foo++");
assertEquals(tree.getOp(), UnaryOpType.INC);
assertEquals(((VariableExpr)tree.getExpr()).getVariable().getName(), "foo");
}
@Test
public void postDecrement() throws Exception {
UnaryExpr tree = parseTreeFor("foo--");
assertEquals(tree.getOp(), UnaryOpType.DEC);
assertEquals(((VariableExpr)tree.getExpr()).getVariable().getName(), "foo");
}
@Test(expected = ParseCancellationException.class)
public void notAUnaryExpression() throws Exception {
parseTreeFor("^" + primary);
}
private UnaryExpr parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
visitor.getSymbolTable().declareVariable("foo", Type.INT, null);
visitor.getSymbolTable().declareVariable("vec", Type.INT3, null);
return (UnaryExpr) visitor.visit(parser.unary_expression());
}
protected String unary() {
return "(-" + primary() + ")";
}
}
