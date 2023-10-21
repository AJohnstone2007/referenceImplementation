package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.BinaryOpType;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.model.Variable;
import com.sun.scenario.effect.compiler.tree.BinaryExpr;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import com.sun.scenario.effect.compiler.tree.LiteralExpr;
import com.sun.scenario.effect.compiler.tree.VariableExpr;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import static org.junit.Assert.*;
public class EqualityExprTest extends ParserBase {
@Test
public void oneEq() throws Exception {
BinaryExpr tree = parseTreeFor("foo == 3");
assertEquals(tree.getOp(), BinaryOpType.EQEQ);
assertEquals(Type.INT, tree.getLeft().getResultType());
assertEquals(VariableExpr.class, tree.getLeft().getClass());
Variable var = ((VariableExpr) tree.getLeft()).getVariable();
assertEquals("foo", var.getName());
assertEquals(Type.INT, var.getType());
assertEquals(LiteralExpr.class, tree.getRight().getClass());
Object val = ((LiteralExpr) tree.getRight()).getValue();
assertEquals(3, val);
}
@Test
public void oneNotEq() throws Exception {
BinaryExpr tree = parseTreeFor("foo != 3");
assertEquals(tree.getOp(), BinaryOpType.NEQ);
assertEquals(Type.INT, tree.getLeft().getResultType());
assertEquals(VariableExpr.class, tree.getLeft().getClass());
Variable var = ((VariableExpr) tree.getLeft()).getVariable();
assertEquals("foo", var.getName());
assertEquals(Type.INT, var.getType());
assertEquals(LiteralExpr.class, tree.getRight().getClass());
Object val = ((LiteralExpr) tree.getRight()).getValue();
assertEquals(3, val);
}
@Test(expected = ParseCancellationException.class)
public void notAnEqualityExpression() throws Exception {
parseTreeFor("foo @ 3");
}
private BinaryExpr parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
visitor.getSymbolTable().declareVariable("foo", Type.INT, null);
return (BinaryExpr) visitor.visit(parser.equality_expression());
}
}
