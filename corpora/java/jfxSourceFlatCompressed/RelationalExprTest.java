package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.BinaryOpType;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.tree.BinaryExpr;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import static org.junit.Assert.*;
public class RelationalExprTest extends ParserBase {
@Test
public void oneLtEq() throws Exception {
BinaryExpr tree = parseTreeFor("foo <= 3");
assertEquals(tree.getOp(), BinaryOpType.LTEQ);
}
@Test
public void oneGtEq() throws Exception {
BinaryExpr tree = parseTreeFor("foo >= 3");
assertEquals(tree.getOp(), BinaryOpType.GTEQ);
}
@Test
public void oneLt() throws Exception {
BinaryExpr tree = parseTreeFor("foo < 3");
assertEquals(tree.getOp(), BinaryOpType.LT);
}
@Test
public void oneGt() throws Exception {
BinaryExpr tree = parseTreeFor("foo > 3");
assertEquals(tree.getOp(), BinaryOpType.GT);
}
@Test(expected = ParseCancellationException.class)
public void notARelationalExpression() throws Exception {
parseTreeFor("foo @ 3");
}
private BinaryExpr parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
visitor.getSymbolTable().declareVariable("foo", Type.INT, null);
return (BinaryExpr) visitor.visit(parser.relational_expression());
}
}
