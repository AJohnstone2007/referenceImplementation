package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.tree.BinaryExpr;
import com.sun.scenario.effect.compiler.tree.ExprStmt;
import com.sun.scenario.effect.compiler.tree.LiteralExpr;
import com.sun.scenario.effect.compiler.tree.SelectStmt;
import com.sun.scenario.effect.compiler.tree.Stmt;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import static org.junit.Assert.*;
public class SelectionStatementTest extends ParserBase {
@Test
public void ifOnly() throws Exception {
Stmt tree = parseTreeFor("if (foo >= 3) foo += 12;");
assertTrue(tree instanceof SelectStmt);
SelectStmt s = (SelectStmt)tree;
assertTrue(s.getIfExpr() instanceof BinaryExpr);
assertTrue(s.getThenStmt() instanceof ExprStmt);
assertNull(s.getElseStmt());
}
@Test
public void ifAndElse() throws Exception {
Stmt tree = parseTreeFor("if (true) foo+=5; else --foo;");
assertTrue(tree instanceof SelectStmt);
SelectStmt s = (SelectStmt)tree;
assertTrue(s.getIfExpr() instanceof LiteralExpr);
assertTrue(s.getThenStmt() instanceof ExprStmt);
assertTrue(s.getElseStmt() instanceof ExprStmt);
}
@Test(expected = ParseCancellationException.class)
public void notASelect() throws Exception {
parseTreeFor("then (so) { bobs yer uncle }");
}
private Stmt parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
visitor.getSymbolTable().declareVariable("foo", Type.INT, null);
return visitor.visitSelection_statement(parser.selection_statement());
}
}
