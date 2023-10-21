package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.SymbolTable;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.model.Variable;
import com.sun.scenario.effect.compiler.tree.BinaryExpr;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import com.sun.scenario.effect.compiler.tree.LiteralExpr;
import com.sun.scenario.effect.compiler.tree.VariableExpr;
import com.sun.scenario.effect.compiler.tree.VectorCtorExpr;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;
public class AssignmentExprTest extends ParserBase {
@Test
public void userVar() throws Exception {
BinaryExpr tree = parseTreeFor("foo = 32.0");
assertEquals(Type.FLOAT, tree.getResultType());
assertEquals(BinaryOpType.EQ, tree.getOp());
assertEquals(VariableExpr.class, tree.getLeft().getClass());
Variable var = ((VariableExpr) tree.getLeft()).getVariable();
assertEquals("foo", var.getName());
assertEquals(Type.FLOAT, tree.getRight().getResultType());
assertEquals(LiteralExpr.class, tree.getRight().getClass());
Object val = ((LiteralExpr) tree.getRight()).getValue();
assertEquals(32.0f, val);
}
@Test(expected = RuntimeException.class)
public void userROVar() throws Exception {
BinaryExpr tree = parseTreeFor("readonly = 32.0");
}
@Test
public void coreVar() throws Exception {
BinaryExpr tree = parseTreeFor("color = float4(1.0)");
assertEquals(Type.FLOAT4, tree.getResultType());
assertEquals(BinaryOpType.EQ, tree.getOp());
assertEquals(VariableExpr.class, tree.getLeft().getClass());
Variable var = ((VariableExpr) tree.getLeft()).getVariable();
assertEquals("color", var.getName());
assertEquals(Type.FLOAT4, tree.getRight().getResultType());
assertEquals(VectorCtorExpr.class, tree.getRight().getClass());
List<Expr> params = ((VectorCtorExpr) tree.getRight()).getParams();
assertEquals(4, params.size());
for (int i = 0; i < 4; i++) {
Object val = ((LiteralExpr) params.get(i)).getValue();
assertEquals(Type.FLOAT, params.get(i).getResultType());
assertEquals(1.0f, val);
}
}
@Test
public void coreVarField() throws Exception {
BinaryExpr tree = parseTreeFor("color.r = 3.0");
assertEquals(Type.FLOAT, tree.getResultType());
assertEquals(BinaryOpType.EQ, tree.getOp());
assertEquals(FieldSelectExpr.class, tree.getLeft().getClass());
FieldSelectExpr fsExpr = (FieldSelectExpr) tree.getLeft();
VariableExpr expr = (VariableExpr) fsExpr.getExpr();
assertEquals(Type.FLOAT4, expr.getResultType());
assertEquals("r", fsExpr.getFields());
assertEquals("color", expr.getVariable().getName());
assertEquals(LiteralExpr.class, tree.getRight().getClass());
Object val = ((LiteralExpr) tree.getRight()).getValue();
assertEquals(3.0f, val);
}
@Test(expected = RuntimeException.class)
public void coreROVar() throws Exception {
parseTreeFor("pos0 = float2(1.0)");
}
@Test(expected = RuntimeException.class)
public void coreROVarField() throws Exception {
parseTreeFor("pos0.x = 1.0");
}
@Test(expected = ParseCancellationException.class)
public void notAnAssignment() throws Exception {
parseTreeFor("const foo");
}
private BinaryExpr parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
SymbolTable st = visitor.getSymbolTable();
st.declareVariable("foo", Type.FLOAT, null);
st.declareVariable("readonly", Type.FLOAT, Qualifier.CONST);
st.enterFrame();
st.declareFunction("main", Type.VOID, null);
return (BinaryExpr) visitor.visit(parser.assignment_expression());
}
}
