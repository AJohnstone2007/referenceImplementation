package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.tree.BreakStmt;
import com.sun.scenario.effect.compiler.tree.ContinueStmt;
import com.sun.scenario.effect.compiler.tree.DiscardStmt;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import com.sun.scenario.effect.compiler.tree.LiteralExpr;
import com.sun.scenario.effect.compiler.tree.ReturnStmt;
import com.sun.scenario.effect.compiler.tree.Stmt;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
public class JumpStatementTest extends ParserBase {
@Test
public void cont() throws Exception {
Stmt tree = parseTreeFor("continue;");
assertTrue(tree instanceof ContinueStmt);
}
@Test
public void brk() throws Exception {
Stmt tree = parseTreeFor(" break ; ");
assertTrue(tree instanceof BreakStmt);
}
@Test
public void discard() throws Exception {
Stmt tree = parseTreeFor("discard;");
assertTrue(tree instanceof DiscardStmt);
}
@Test
public void returnEmpty() throws Exception {
Stmt tree = parseTreeFor("return;");
assertTrue(tree instanceof ReturnStmt);
assertNull(((ReturnStmt)tree).getExpr());
}
@Test
public void returnExpr() throws Exception {
Stmt tree = parseTreeFor("return 3;");
assertTrue(tree instanceof ReturnStmt);
ReturnStmt ret = (ReturnStmt)tree;
assertTrue(ret.getExpr() instanceof LiteralExpr);
LiteralExpr lit = (LiteralExpr)ret.getExpr();
assertEquals(lit.getValue(), Integer.valueOf(3));
}
@Test(expected = ParseCancellationException.class)
public void notAJump() throws Exception {
parseTreeFor("float;");
}
private Stmt parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
return visitor.visitJump_statement(parser.jump_statement());
}
}
