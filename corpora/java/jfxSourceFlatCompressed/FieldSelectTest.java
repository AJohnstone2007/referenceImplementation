package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import static org.junit.Assert.*;
public class FieldSelectTest extends ParserBase {
@Test
public void rgba() throws Exception {
String tree = parseTreeFor(".rgba");
assertEquals(tree, ".rgba");
}
@Test
public void rgb() throws Exception {
String tree = parseTreeFor(".rgb");
assertEquals(tree, ".rgb");
}
@Test
public void rg() throws Exception {
String tree = parseTreeFor(".rg");
assertEquals(tree, ".rg");
}
@Test
public void r() throws Exception {
String tree = parseTreeFor(".r");
assertEquals(tree, ".r");
}
@Test
public void aaaa() throws Exception {
String tree = parseTreeFor(".aaaa");
assertEquals(tree, ".aaaa");
}
@Test
public void abgr() throws Exception {
String tree = parseTreeFor(".abgr");
assertEquals(tree, ".abgr");
}
@Test
public void xyzw() throws Exception {
String tree = parseTreeFor(".xyzw");
assertEquals(tree, ".xyzw");
}
@Test
public void xyz() throws Exception {
String tree = parseTreeFor(".xyz");
assertEquals(tree, ".xyz");
}
@Test
public void xy() throws Exception {
String tree = parseTreeFor(".xy");
assertEquals(tree, ".xy");
}
@Test
public void x() throws Exception {
String tree = parseTreeFor(".x");
assertEquals(tree, ".x");
}
@Test
public void zzz() throws Exception {
String tree = parseTreeFor(".zzz");
assertEquals(tree, ".zzz");
}
@Test
public void wzyz() throws Exception {
String tree = parseTreeFor(".wzyx");
assertEquals(tree, ".wzyx");
}
@Test(expected = ParseCancellationException.class)
public void notAFieldSelection1() throws Exception {
parseTreeFor("qpz");
}
@Test(expected = AssertionFailedError.class)
public void notAFieldSelection2() throws Exception {
parseTreeFor(".xqpz", true);
}
@Test(expected = AssertionFailedError.class)
public void tooManyVals() throws Exception {
parseTreeFor(".xyzwx", true);
}
@Test(expected = AssertionFailedError.class)
public void mixedVals() throws Exception {
parseTreeFor(".xyba", true);
}
private String parseTreeFor(String text) throws Exception {
return parseTreeFor(text, false);
}
private String parseTreeFor(String text, boolean expectEx) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
String ret = visitor.visitField_selection(parser.field_selection()).getString();
boolean sawException = false;
try {
visitor.visitField_selection(parser.field_selection());
} catch (Exception e) {
sawException = true;
}
if (sawException == expectEx) {
Assert.fail(expectEx ? "Expecting EOF" : "Not expecting EOF");
}
return ret;
}
}
