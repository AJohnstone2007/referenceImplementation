package test.javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import javafx.css.StylesheetShim;
import javafx.css.StyleConverter.StringStore;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.css.Declaration;
import javafx.css.DeclarationShim;
import javafx.css.Rule;
import javafx.css.RuleShim;
import javafx.css.Selector;
import javafx.css.StyleOrigin;
import javafx.css.Stylesheet;
import javafx.css.StylesheetShim;
import javafx.scene.Node;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
public class RuleTest {
public RuleTest() {
}
@BeforeClass
public static void setUpClass() throws Exception {
}
@AfterClass
public static void tearDownClass() throws Exception {
}
@Test
public void testGetUnobservedSelectorList() {
List<Selector> expResult = new ArrayList<Selector>();
expResult.add(Selector.createSelector("One.two#three"));
expResult.add(Selector.createSelector("Four.five#six"));
Rule instance = RuleShim.getRule(expResult, Collections.EMPTY_LIST);
List result = RuleShim.getUnobservedSelectorList(instance);
assertEquals(expResult, result);
}
@Test
public void testGetUnobservedDeclarationList() {
List<Declaration> expResult = new ArrayList<Declaration>();
expResult.add(DeclarationShim.getDeclaration("one", new ParsedValueImpl<String,String>("one", null), false));
expResult.add(DeclarationShim.getDeclaration("two", new ParsedValueImpl<String,String>("two", null), false));
expResult.add(DeclarationShim.getDeclaration("three", new ParsedValueImpl<String,String>("three", null), false));
Rule instance = RuleShim.getRule(Collections.EMPTY_LIST, expResult);
List result = RuleShim.getUnobservedDeclarationList(instance);
assertEquals(expResult, result);
}
@Test
public void testGetSelectors() {
List<Selector> expResult = new ArrayList<Selector>();
expResult.add(Selector.createSelector("One.two#three"));
expResult.add(Selector.createSelector("Four.five#six"));
Rule instance = RuleShim.getRule(expResult, Collections.EMPTY_LIST);
List result = instance.getSelectors();
assertEquals(expResult, result);
}
@Test
public void testGetDeclarations() {
List<Declaration> expResult = new ArrayList<Declaration>();
expResult.add(DeclarationShim.getDeclaration("one", new ParsedValueImpl<String,String>("one", null), false));
expResult.add(DeclarationShim.getDeclaration("two", new ParsedValueImpl<String,String>("two", null), false));
expResult.add(DeclarationShim.getDeclaration("three", new ParsedValueImpl<String,String>("three", null), false));
Rule instance = RuleShim.getRule(Collections.EMPTY_LIST, expResult);
List result = instance.getDeclarations();
assertEquals(expResult, result);
}
@Test
public void testGetStylesheet() {
Rule instance = RuleShim.getRule(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
Stylesheet expResult = new StylesheetShim();
expResult.getRules().add(instance);
Stylesheet result = instance.getStylesheet();
assertEquals(expResult, result);
}
@Test
public void testGetOriginAfterSettingOriginAfterAddingRuleToStylesheet() {
Rule instance = RuleShim.getRule(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
Stylesheet stylesheet = new StylesheetShim();
stylesheet.getRules().add(instance);
stylesheet.setOrigin(StyleOrigin.INLINE);
StyleOrigin expResult = StyleOrigin.INLINE;
StyleOrigin result = instance.getOrigin();
assertEquals(expResult, result);
}
@Test
public void testGetOriginAfterSettingOriginBeforeAddingRuleToStylesheet() {
Rule instance = RuleShim.getRule(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
Stylesheet stylesheet = new StylesheetShim();
stylesheet.setOrigin(StyleOrigin.INLINE);
stylesheet.getRules().add(instance);
StyleOrigin expResult = StyleOrigin.INLINE;
StyleOrigin result = instance.getOrigin();
assertEquals(expResult, result);
}
@Test
public void testGetOriginWithoutAddingRuleToStylesheet() {
Rule instance = RuleShim.getRule(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
StyleOrigin result = instance.getOrigin();
assertNull(result);
}
@Test
public void testGetOriginAfterRemovingRuleFromStylesheet() {
Rule instance = RuleShim.getRule(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
Stylesheet stylesheet = new StylesheetShim();
stylesheet.getRules().add(instance);
stylesheet.setOrigin(StyleOrigin.INLINE);
stylesheet.getRules().remove(instance);
StyleOrigin result = instance.getOrigin();
assertNull(result);
}
@Ignore("JDK-8234154")
@Test
public void testApplies() {
System.out.println("applies");
Node node = null;
Rule instance = null;
long expResult = 0l;
long result = RuleShim.applies(instance, node, null);
assertEquals(expResult, result);
fail("The test case is a prototype.");
}
@Ignore("JDK-8234154")
@Test
public void testToString() {
System.out.println("toString");
Rule instance = null;
String expResult = "";
String result = instance.toString();
assertEquals(expResult, result);
fail("The test case is a prototype.");
}
@Ignore("JDK-8234154")
@Test
public void testWriteBinary() throws Exception {
System.out.println("writeBinary");
DataOutputStream os = null;
StringStore stringStore = null;
Rule instance = null;
RuleShim.writeBinary(instance, os, stringStore);
fail("The test case is a prototype.");
}
@Ignore("JDK-8234154")
@Test
public void testReadBinary() throws Exception {
System.out.println("readBinary");
DataInputStream is = null;
String[] strings = null;
Rule expResult = null;
Rule result = RuleShim.readBinary(StylesheetShim.BINARY_CSS_VERSION, is, strings);
assertEquals(expResult, result);
fail("The test case is a prototype.");
}
}
