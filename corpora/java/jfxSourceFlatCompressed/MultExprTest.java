package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.BinaryOpType;
import com.sun.scenario.effect.compiler.tree.BinaryExpr;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import org.antlr.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class MultExprTest extends UnaryExprTest {
private String unary;
@Before
@Override
public void setUp() {
super.setUp();
this.unary = unary();
}
@Test
public void oneMultiplication() throws Exception {
BinaryExpr tree = parseTreeFor(unary + " * " + unary);
assertEquals(tree.getOp(), BinaryOpType.MUL);
}
@Test
public void oneDivision() throws Exception {
BinaryExpr tree = parseTreeFor(unary + "   / " + unary);
assertEquals(tree.getOp(), BinaryOpType.DIV);
}
@Test
public void expressionCombination() throws Exception {
BinaryExpr tree = parseTreeFor(unary + " * " + unary + '/' + unary + '/' + unary + "   *" + unary);
assertEquals(tree.getOp(), BinaryOpType.MUL);
}
@Test(expected = ClassCastException.class)
public void notAMultiplicativeExpression() throws Exception {
parseTreeFor("3 + 3");
}
private BinaryExpr parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
return (BinaryExpr) visitor.visit(parser.multiplicative_expression());
}
protected String multiplicative() {
return "(" + unary() + " * " + unary() + ")";
}
}
