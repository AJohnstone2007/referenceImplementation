package com.sun.scenario.effect.compiler.parser;
import java.util.List;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.model.Function;
import com.sun.scenario.effect.compiler.model.Param;
import com.sun.scenario.effect.compiler.model.Qualifier;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.model.Variable;
import com.sun.scenario.effect.compiler.tree.ExtDecl;
import com.sun.scenario.effect.compiler.tree.FuncDef;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import com.sun.scenario.effect.compiler.tree.VarDecl;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import static org.junit.Assert.*;
public class ExternalDeclarationTest extends ParserBase {
@Test
public void declaration() throws Exception {
ExtDecl tree = parseTreeFor("param float4 foo;").get(0);
assertTrue(tree instanceof VarDecl);
VarDecl d = (VarDecl)tree;
Variable var = d.getVariable();
assertNotNull(var);
assertEquals(var.getQualifier(), Qualifier.PARAM);
assertEquals(var.getType(), Type.FLOAT4);
assertEquals(var.getName(), "foo");
assertNull(d.getInit());
}
@Test
public void multiDeclaration() throws Exception {
List<ExtDecl> decls = parseTreeFor("param float4 foo, bar;");
assertEquals(decls.size(), 2);
ExtDecl tree;
tree = decls.get(0);
assertTrue(tree instanceof VarDecl);
VarDecl d = (VarDecl)tree;
Variable var = d.getVariable();
assertNotNull(var);
assertEquals(var.getQualifier(), Qualifier.PARAM);
assertEquals(var.getType(), Type.FLOAT4);
assertEquals(var.getName(), "foo");
assertNull(d.getInit());
tree = decls.get(1);
assertTrue(tree instanceof VarDecl);
d = (VarDecl)tree;
var = d.getVariable();
assertNotNull(var);
assertEquals(var.getQualifier(), Qualifier.PARAM);
assertEquals(var.getType(), Type.FLOAT4);
assertEquals(var.getName(), "bar");
assertNull(d.getInit());
}
@Test
public void funcDefNoParam() throws Exception {
ExtDecl tree = parseTreeFor("void test() { int i = 3; }").get(0);
assertTrue(tree instanceof FuncDef);
FuncDef d = (FuncDef)tree;
Function func = d.getFunction();
assertNotNull(func);
assertEquals(func.getReturnType(), Type.VOID);
assertEquals(func.getName(), "test");
List<Param> params = func.getParams();
assertNotNull(params);
assertEquals(params.size(), 0);
assertNotNull(d.getStmt());
}
@Test
public void funcDefOneParam() throws Exception {
ExtDecl tree = parseTreeFor("void test(float3 foo) { int i = 3; }").get(0);
assertTrue(tree instanceof FuncDef);
FuncDef d = (FuncDef)tree;
Function func = d.getFunction();
assertNotNull(func);
assertEquals(func.getReturnType(), Type.VOID);
assertEquals(func.getName(), "test");
List<Param> params = func.getParams();
assertNotNull(params);
assertEquals(params.size(), 1);
assertNotNull(d.getStmt());
}
@Test
public void funcDefTwoParam() throws Exception {
ExtDecl tree = parseTreeFor("void test(float3 foo, float3 bar) { int i = 3; }").get(0);
assertTrue(tree instanceof FuncDef);
FuncDef d = (FuncDef)tree;
Function func = d.getFunction();
assertNotNull(func);
assertEquals(func.getReturnType(), Type.VOID);
assertEquals(func.getName(), "test");
List<Param> params = func.getParams();
assertNotNull(params);
assertEquals(params.size(), 2);
assertNotNull(d.getStmt());
}
@Test(expected = ParseCancellationException.class)
public void notAnExtDecl() throws Exception {
parseTreeFor("foo = 4");
}
private List<ExtDecl> parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
return visitor.visitExternal_declaration(parser.external_declaration()).getDecls();
}
}
