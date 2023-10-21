package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.BinaryOpType;
import com.sun.scenario.effect.compiler.tree.BinaryExpr;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class AddExprTest extends MultExprTest {
private String mult;
@Before
@Override
public void setUp() {
super.setUp();
this.mult = multiplicative();
}
@Test
public void oneAddition() throws Exception {
BinaryExpr tree = parseTreeFor(mult + " + " + mult);
assertEquals(BinaryOpType.ADD, tree.getOp());
}
@Test
public void oneSubtraction() throws Exception {
BinaryExpr tree = parseTreeFor(mult + "   - " + mult);
assertEquals(BinaryOpType.SUB, tree.getOp());
}
@Test
public void additiveCombination() throws Exception {
BinaryExpr tree = parseTreeFor(mult + " + " + mult + '-' + mult + '-' + mult + "   +" + mult);
assertEquals(BinaryOpType.ADD, tree.getOp());
}
@Test(expected = ClassCastException.class)
public void notAnAdditiveExpression() throws Exception {
parseTreeFor(mult + "!" + mult);
}
private BinaryExpr parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
return (BinaryExpr) visitor.visit(parser.additive_expression());
}
protected String additive() {
return "(" + multiplicative() + " + " + multiplicative() + ")";
}
}
