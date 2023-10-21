package com.sun.scenario.effect.compiler.parser;
import com.sun.scenario.effect.compiler.JSLParser;
import com.sun.scenario.effect.compiler.JSLParser.Fully_specified_typeContext;
import com.sun.scenario.effect.compiler.model.Qualifier;
import com.sun.scenario.effect.compiler.model.Type;
import com.sun.scenario.effect.compiler.tree.JSLVisitor;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
public class FullySpecifiedTypeTest extends ParserBase {
@Test
public void unqualified() throws Exception {
JSLVisitor.FullySpecifiedTypeExpr ret = parseTreeFor("float");
assertNull(ret.getQual());
assertEquals(Type.FLOAT, ret.getType());
}
@Test
public void qualified() throws Exception {
JSLVisitor.FullySpecifiedTypeExpr ret = parseTreeFor("param bool3");
assertEquals(Qualifier.PARAM, ret.getQual());
assertEquals(Type.BOOL3, ret.getType());
}
@Test(expected = ParseCancellationException.class)
public void notAFullySpecifiedType() throws Exception {
parseTreeFor("double");
}
private fully_specified_type_return parseTreeFor(String text) throws Exception {
JSLParser parser = parserOver(text);
JSLVisitor visitor = new JSLVisitor();
return (JSLVisitor.FullySpecifiedTypeExpr) visitor.visit(parser.fully_specified_type());
}
}
