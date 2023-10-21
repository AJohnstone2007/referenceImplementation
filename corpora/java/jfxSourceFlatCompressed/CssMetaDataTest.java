package test.javafx.css;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.sun.javafx.css.CascadingStyle;
import com.sun.javafx.css.ParsedValueImpl;
import com.sun.javafx.css.PseudoClassState;
import com.sun.javafx.css.StyleManager;
import test.com.sun.javafx.css.TestNode;
import test.com.sun.javafx.css.TestNodeBase;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.value.WritableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.css.converter.BooleanConverter;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import javafx.css.CompoundSelector;
import javafx.css.CssMetaData;
import javafx.css.CssParser;
import javafx.css.CssParserShim;
import javafx.css.Declaration;
import javafx.css.DeclarationShim;
import javafx.css.FontCssMetaData;
import javafx.css.ParsedValue;
import javafx.css.PseudoClass;
import javafx.css.Rule;
import javafx.css.RuleShim;
import javafx.css.Selector;
import javafx.css.SelectorShim;
import javafx.css.SimpleSelector;
import javafx.css.SimpleSelectorShim;
import javafx.css.Style;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.Stylesheet;
import javafx.css.StylesheetShim;
import org.junit.After;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;
enum TestEnum {
LEFT,
CENTER,
RIGHT,
JUSTIFY
}
public class CssMetaDataTest {
public CssMetaDataTest() {
}
private static CssMetaData get(List<CssMetaData<? extends Styleable, ?>> list, String prop) {
for (CssMetaData styleable : list) {
if (prop.equals(styleable.getProperty())) return styleable;
}
return null;
}
private static void resetStyleManager() {
StyleManager sm = StyleManager.getInstance();
sm.userAgentStylesheetContainers.clear();
sm.platformUserAgentStylesheetContainers.clear();
sm.stylesheetContainerMap.clear();
sm.cacheContainerMap.clear();
sm.hasDefaultUserAgentStylesheet = false;
}
@After
public void cleanup() {
resetStyleManager();
}
@Test
public void testGetCssMetaData_Styleable() {
Styleable styleable = new TestNode();
List<CssMetaData<? extends Styleable, ?>> expResult = TestNode.getClassCssMetaData();
List result = styleable.getCssMetaData();
assertEquals(expResult, result);
}
@Test
public void testGetJavafxBeansProperty() {
TestNode testNode = new TestNode();
WritableValue prop = TestNodeBase.StyleableProperties.TEST.getStyleableProperty(testNode);
assert(prop != null);
CssMetaData result = ((StyleableProperty)prop).getCssMetaData();
assert(result == TestNodeBase.StyleableProperties.TEST);
}
@Test
public void testGetProperty() {
String expResult = "-fx-test";
String result = TestNodeBase.StyleableProperties.TEST.getProperty();
assertEquals(expResult, result);
}
@Test
public void testGetConverter() {
StyleConverter expResult = BooleanConverter.getInstance();
StyleConverter result = TestNodeBase.StyleableProperties.TEST.getConverter();
assertEquals(expResult, result);
}
@Test
public void testGetInitialValue() {
TestNode testNode = new TestNode();
Double expResult = testNode.getXyzzy();
Double result = (Double)TestNode.StyleableProperties.XYZZY.getInitialValue(testNode);
assertEquals(expResult, result);
}
@Test
public void testGetSubProperties() {
CssMetaData<TestNode,Font> fontProp =
new FontCssMetaData<TestNode>("-fx-font", Font.getDefault()) {
@Override
public boolean isSettable(TestNode n) {
return true;
}
@Override
public StyleableProperty<Font> getStyleableProperty(TestNode n) {
return null;
}
};
List<CssMetaData<? extends Styleable, ?>> list = fontProp.getSubProperties();
assertNotNull(list);
}
@Test
public void testIsInherits() {
boolean expResult = false;
boolean result = TestNode.StyleableProperties.XYZZY.isInherits();
assertEquals(expResult, result);
}
@Test
public void testToString() {
CssMetaData<TestNode,Font> fontProp =
new FontCssMetaData<TestNode>("-fx-font", Font.getDefault()) {
@Override
public boolean isSettable(TestNode n) {
return true;
}
@Override
public StyleableProperty<Font> getStyleableProperty(TestNode n) {
return null;
}
};
String string = fontProp.toString();
assertNotNull(string);
}
@Test
public void testEquals() {
TestNode testNode = new TestNode();
Node node = new Node() {
};
CssMetaData testNodeOpacity = get(TestNode.getClassCssMetaData(), "-fx-opacity");
CssMetaData nodeOpacity = get(Node.getClassCssMetaData(), "-fx-opacity");
assertTrue(testNodeOpacity.equals(nodeOpacity));
}
static int ord = 0;
static CascadingStyle createCascadingStyle(Selector selector, Declaration declaration) {
Set<PseudoClass> pseudoClasses = null;
if (selector instanceof SimpleSelector) {
pseudoClasses =
SimpleSelectorShim.getPseudoClassStates((SimpleSelector)selector);
} else {
pseudoClasses = new PseudoClassState();
for (SimpleSelector sel : ((CompoundSelector)selector).getSelectors()) {
Set<PseudoClass> selectorPseudoClasses = SimpleSelectorShim.getPseudoClassStates(sel);
pseudoClasses.addAll(selectorPseudoClasses);
}
}
return new CascadingStyle(
new Style(selector, declaration),
pseudoClasses,
0,
ord++
);
}
@Ignore("JDK-8234142")
@Test
public void testGetMatchingStyles() {
final Stylesheet stylesheet = StylesheetShim.getStylesheet();
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final List<Rule> rules = stylesheet.getRules();
List<String> rootStyleClass = new ArrayList<String>();
rootStyleClass.add("root");
Selector root = SimpleSelectorShim.getSimpleSelector("*", rootStyleClass, null, null);
ParsedValue fxBaseValue = new CssParserShim().parseExpr("-fx-base", "red");
Declaration fxBase = DeclarationShim.getDeclaration("-fx-base", fxBaseValue, false);
ParsedValueImpl<String,String> fxColorValue = new ParsedValueImpl<String,String>(fxBase.getProperty(), null, true);
Declaration fxColor = DeclarationShim.getDeclaration("-fx-color", fxColorValue, false);
List<Selector> selectors = new ArrayList<Selector>();
Collections.addAll(selectors, root);
List<Declaration> declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxBase, fxColor);
Rule baseRule = RuleShim.getRule(selectors, declarations);
rules.add(baseRule);
List<String> rectStyleClass = new ArrayList<String>();
rectStyleClass.add("rect");
Selector rect = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, null, null);
ParsedValue fxFillValue = new CssParserShim().parseExpr("-fx-fill", "-fx-color");
Declaration fxFill = DeclarationShim.getDeclaration("-fx-fill", fxFillValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rect);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFill);
Rule rectRule = RuleShim.getRule(selectors, declarations);
rules.add(rectRule);
List<String> pseudoclasses = new ArrayList<String>();
pseudoclasses.add("hover");
Selector rectHover = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, pseudoclasses, null);
ParsedValueImpl<Color,Color> fxFillHoverValue = new ParsedValueImpl<Color,Color>(Color.YELLOW, null);
Declaration fxFillHover = DeclarationShim.getDeclaration("-fx-fill", fxFillHoverValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rectHover);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFillHover);
Rule rectHoverRule = RuleShim.getRule(selectors, declarations);
rules.add(rectHoverRule);
List<Style> expecteds = new ArrayList<Style>();
Collections.addAll(expecteds,
new Style(root, fxBase),
new Style(root, fxColor),
new Style(rect, fxFill),
new Style(rectHover, fxFillHover)
);
final Rectangle rectangle = new Rectangle();
rectangle.getStyleClass().add("rect");
final Group group = new Group();
group.getChildren().add(rectangle);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
final CssMetaData FILL = get(rectangle.getCssMetaData(), "-fx-fill");
final List<Style> actuals = NodeHelper.getMatchingStyles(FILL, rectangle);
assertEquals(expecteds.size(), actuals.size(), 0);
for (Style style : expecteds) {
if (!actuals.remove(style)) fail();
}
assertTrue(actuals.isEmpty());
}
@Ignore("JDK-8234142")
@Test
public void testGetMatchingStylesWithInlineStyleOnParent() {
final Stylesheet stylesheet = StylesheetShim.getStylesheet();
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final List<Rule> rules = stylesheet.getRules();
List<String> rootStyleClass = new ArrayList<String>();
rootStyleClass.add("root");
Selector root = SimpleSelectorShim.getSimpleSelector("*", rootStyleClass, null, null);
ParsedValue fxBaseValue = new CssParserShim().parseExpr("-fx-base", "red");
Declaration fxBase = DeclarationShim.getDeclaration("-fx-base", fxBaseValue, false);
ParsedValueImpl<String,String> fxColorValue = new ParsedValueImpl<String,String>(fxBase.getProperty(), null, true);
Declaration fxColor = DeclarationShim.getDeclaration("-fx-color", fxColorValue, false);
List<Selector> selectors = new ArrayList<Selector>();
Collections.addAll(selectors, root);
List<Declaration> declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxBase, fxColor);
Rule baseRule = RuleShim.getRule(selectors, declarations);
rules.add(baseRule);
List<String> rectStyleClass = new ArrayList<String>();
rectStyleClass.add("rect");
Selector rect = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, null, null);
ParsedValue fxFillValue = new CssParserShim().parseExpr("-fx-fill", "-fx-color");
Declaration fxFill = DeclarationShim.getDeclaration("-fx-fill", fxFillValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rect);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFill);
Rule rectRule = RuleShim.getRule(selectors, declarations);
rules.add(rectRule);
List<String> pseudoclasses = new ArrayList<String>();
pseudoclasses.add("hover");
Selector rectHover = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, pseudoclasses, null);
ParsedValueImpl<Color,Color> fxFillHoverValue = new ParsedValueImpl<Color,Color>(Color.YELLOW, null);
Declaration fxFillHover = DeclarationShim.getDeclaration("-fx-fill", fxFillHoverValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rectHover);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFillHover);
Rule rectHoverRule = RuleShim.getRule(selectors, declarations);
rules.add(rectHoverRule);
final Declaration decl = DeclarationShim.getDeclaration("-fx-base", new ParsedValueImpl<Color,Color>(Color.GREEN, null), false);
Stylesheet ss = new StylesheetShim(null) {
{
setOrigin(StyleOrigin.INLINE);
getRules().add(
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(decl))
);
}
};
List<Style> expecteds = new ArrayList<Style>();
Collections.addAll(expecteds,
new Style(SelectorShim.getUniversalSelector(), decl),
new Style(root, fxBase),
new Style(root, fxColor),
new Style(rect, fxFill),
new Style(rectHover, fxFillHover)
);
final Rectangle rectangle = new Rectangle();
rectangle.getStyleClass().add("rect");
final Group group = new Group();
group.setStyle("-fx-base: green;");
group.getChildren().add(rectangle);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
final CssMetaData FILL = get(rectangle.getCssMetaData(), "-fx-fill");
final List<Style> actuals = NodeHelper.getMatchingStyles(FILL, rectangle);
assertEquals(expecteds.size(), actuals.size(), 0);
assertEquals(expecteds.get(0), actuals.get(0));
for (Style style : expecteds) {
if (!actuals.remove(style)) fail();
}
assertTrue(actuals.isEmpty());
}
@Ignore("JDK-8234142")
@Test
public void testGetMatchingStylesWithInlineStyleOnLeaf() {
final Stylesheet stylesheet = StylesheetShim.getStylesheet();
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final List<Rule> rules = stylesheet.getRules();
List<String> rootStyleClass = new ArrayList<String>();
rootStyleClass.add("root");
Selector root = SimpleSelectorShim.getSimpleSelector("*", rootStyleClass, null, null);
ParsedValue fxBaseValue = new CssParserShim().parseExpr("-fx-base", "red");
Declaration fxBase = DeclarationShim.getDeclaration("-fx-base", fxBaseValue, false);
ParsedValueImpl<String,String> fxColorValue = new ParsedValueImpl<String,String>(fxBase.getProperty(), null, true);
Declaration fxColor = DeclarationShim.getDeclaration("-fx-color", fxColorValue, false);
List<Selector> selectors = new ArrayList<Selector>();
Collections.addAll(selectors, root);
List<Declaration> declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxBase, fxColor);
Rule baseRule = RuleShim.getRule(selectors, declarations);
rules.add(baseRule);
List<String> rectStyleClass = new ArrayList<String>();
rectStyleClass.add("rect");
Selector rect = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, null, null);
ParsedValue fxFillValue = new CssParserShim().parseExpr("-fx-fill", "-fx-color");
Declaration fxFill = DeclarationShim.getDeclaration("-fx-fill", fxFillValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rect);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFill);
Rule rectRule = RuleShim.getRule(selectors, declarations);
rules.add(rectRule);
List<String> pseudoclasses = new ArrayList<String>();
pseudoclasses.add("hover");
Selector rectHover = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, pseudoclasses, null);
ParsedValueImpl<Color,Color> fxFillHoverValue = new ParsedValueImpl<Color,Color>(Color.YELLOW, null);
Declaration fxFillHover = DeclarationShim.getDeclaration("-fx-fill", fxFillHoverValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rectHover);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFillHover);
Rule rectHoverRule = RuleShim.getRule(selectors, declarations);
rules.add(rectHoverRule);
final Declaration decl = DeclarationShim.getDeclaration("-fx-base", new ParsedValueImpl<Color,Color>(Color.GREEN, null), false);
Stylesheet ss = new StylesheetShim(null) {
{
setOrigin(StyleOrigin.INLINE);
getRules().add(
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(decl))
);
}
};
List<Style> expecteds = new ArrayList<Style>();
Collections.addAll(expecteds,
new Style(SelectorShim.getUniversalSelector(), decl),
new Style(root, fxBase),
new Style(root, fxColor),
new Style(rect, fxFill),
new Style(rectHover, fxFillHover)
);
final Rectangle rectangle = new Rectangle();
rectangle.getStyleClass().add("rect");
rectangle.setStyle("-fx-base: green;");
final Group group = new Group();
group.getChildren().add(rectangle);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
final CssMetaData FILL = get(rectangle.getCssMetaData(), "-fx-fill");
final List<Style> actuals = NodeHelper.getMatchingStyles(FILL, rectangle);
assertEquals(expecteds.size(), actuals.size(), 0);
assertEquals(expecteds.get(0), actuals.get(0));
for (Style style : expecteds) {
if (!actuals.remove(style)) fail();
}
assertTrue(actuals.isEmpty());
}
@Ignore("JDK-8234142")
@Test
public void testGetMatchingStylesWithInlineStyleOnRootAndLeaf() {
final Stylesheet stylesheet = StylesheetShim.getStylesheet();
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final List<Rule> rules = stylesheet.getRules();
List<String> rootStyleClass = new ArrayList<String>();
rootStyleClass.add("root");
Selector root = SimpleSelectorShim.getSimpleSelector("*", rootStyleClass, null, null);
ParsedValue fxBaseValue = new CssParserShim().parseExpr("-fx-base", "red");
Declaration fxBase = DeclarationShim.getDeclaration("-fx-base", fxBaseValue, false);
ParsedValueImpl<String,String> fxColorValue = new ParsedValueImpl<String,String>(fxBase.getProperty(), null, true);
Declaration fxColor = DeclarationShim.getDeclaration("-fx-color", fxColorValue, false);
List<Selector> selectors = new ArrayList<Selector>();
Collections.addAll(selectors, root);
List<Declaration> declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxBase, fxColor);
Rule baseRule = RuleShim.getRule(selectors, declarations);
rules.add(baseRule);
List<String> rectStyleClass = new ArrayList<String>();
rectStyleClass.add("rect");
Selector rect = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, null, null);
ParsedValue fxFillValue = new CssParserShim().parseExpr("-fx-fill", "-fx-color");
Declaration fxFill = DeclarationShim.getDeclaration("-fx-fill", fxFillValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rect);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFill);
Rule rectRule = RuleShim.getRule(selectors, declarations);
rules.add(rectRule);
List<String> pseudoclasses = new ArrayList<String>();
pseudoclasses.add("hover");
Selector rectHover = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, pseudoclasses, null);
ParsedValueImpl<Color,Color> fxFillHoverValue = new ParsedValueImpl<Color,Color>(Color.YELLOW, null);
Declaration fxFillHover = DeclarationShim.getDeclaration("-fx-fill", fxFillHoverValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rectHover);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFillHover);
Rule rectHoverRule = RuleShim.getRule(selectors, declarations);
rules.add(rectHoverRule);
final Declaration gdecl = DeclarationShim.getDeclaration("-fx-base", new ParsedValueImpl<Color,Color>(Color.GREEN, null), false);
final Declaration ydecl = DeclarationShim.getDeclaration("-fx-color", new ParsedValueImpl<Color,Color>(Color.YELLOW, null), false);
Stylesheet ss = new StylesheetShim(null) {
{
setOrigin(StyleOrigin.INLINE);
Collections.addAll(getRules(),
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(gdecl)),
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(ydecl))
);
}
};
List<Style> expecteds = new ArrayList<Style>();
Collections.addAll(expecteds,
new Style(SelectorShim.getUniversalSelector(), ydecl),
new Style(SelectorShim.getUniversalSelector(), gdecl),
new Style(root, fxBase),
new Style(root, fxColor),
new Style(rect, fxFill),
new Style(rectHover, fxFillHover)
);
final Rectangle rectangle = new Rectangle();
rectangle.getStyleClass().add("rect");
rectangle.setStyle("-fx-base: green;");
final Group group = new Group();
group.setStyle("-fx-color: yellow;");
group.getChildren().add(rectangle);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
final CssMetaData FILL = get(rectangle.getCssMetaData(), "-fx-fill");
final List<Style> actuals = NodeHelper.getMatchingStyles(FILL, rectangle);
assertEquals(expecteds.size(), actuals.size(), 0);
assertEquals(expecteds.get(0), actuals.get(0));
for (Style style : expecteds) {
if (!actuals.remove(style)) fail(style.toString());
}
assertTrue(actuals.isEmpty());
}
@Ignore("JDK-8234142")
@Test
public void testGetMatchingStylesShouldNotReturnAncestorPropertyIfNotInherited() {
final Stylesheet stylesheet = StylesheetShim.getStylesheet();
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final List<Rule> rules = stylesheet.getRules();
List<String> rootStyleClass = new ArrayList<String>();
rootStyleClass.add("root");
Selector root = SimpleSelectorShim.getSimpleSelector("*", rootStyleClass, null, null);
ParsedValue fxBaseValue = new CssParserShim().parseExpr("-fx-base", "red");
Declaration fxBase = DeclarationShim.getDeclaration("-fx-base", fxBaseValue, false);
ParsedValueImpl<String,String> fxColorValue = new ParsedValueImpl<String,String>(fxBase.getProperty(), null, true);
Declaration fxColor = DeclarationShim.getDeclaration("-fx-color", fxColorValue, false);
ParsedValueImpl<Color,Color> fxFillShouldNotMatchValue = new ParsedValueImpl<Color,Color>(Color.RED, null);
Declaration fxFillShouldNotMatch = DeclarationShim.getDeclaration("-fx-fill", fxFillShouldNotMatchValue, false);
List<Selector> selectors = new ArrayList<Selector>();
Collections.addAll(selectors, root);
List<Declaration> declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxBase, fxColor, fxFillShouldNotMatch);
Rule baseRule = RuleShim.getRule(selectors, declarations);
rules.add(baseRule);
List<String> rectStyleClass = new ArrayList<String>();
rectStyleClass.add("rect");
Selector rect = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, null, null);
ParsedValue fxFillValue = new CssParserShim().parseExpr("-fx-fill", "-fx-color");
Declaration fxFill = DeclarationShim.getDeclaration("-fx-fill", fxFillValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rect);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFill);
Rule rectRule = RuleShim.getRule(selectors, declarations);
rules.add(rectRule);
List<String> pseudoclasses = new ArrayList<String>();
pseudoclasses.add("hover");
Selector rectHover = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, pseudoclasses, null);
ParsedValueImpl<Color,Color> fxFillHoverValue = new ParsedValueImpl<Color,Color>(Color.YELLOW, null);
Declaration fxFillHover = DeclarationShim.getDeclaration("-fx-fill", fxFillHoverValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rectHover);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFillHover);
Rule rectHoverRule = RuleShim.getRule(selectors, declarations);
rules.add(rectHoverRule);
List<Style> expecteds = new ArrayList<Style>();
Collections.addAll(expecteds,
new Style(root, fxBase),
new Style(root, fxColor),
new Style(rect, fxFill),
new Style(rectHover, fxFillHover)
);
final Rectangle rectangle = new Rectangle();
rectangle.getStyleClass().add("rect");
final Group group = new Group();
group.getChildren().add(rectangle);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
final CssMetaData FILL = get(rectangle.getCssMetaData(), "-fx-fill");
final List<Style> actuals = NodeHelper.getMatchingStyles(FILL, rectangle);
assertEquals(expecteds.size(), actuals.size(), 0);
for (Style style : expecteds) {
if (!actuals.remove(style)) fail();
}
assertTrue(actuals.isEmpty());
}
@Ignore("JDK-8234142")
@Test
public void testGetMatchingStylesShouldNotReturnInlineAncestorPropertyIfNotInherited() {
final Stylesheet stylesheet = StylesheetShim.getStylesheet();
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final List<Rule> rules = stylesheet.getRules();
List<String> rootStyleClass = new ArrayList<String>();
rootStyleClass.add("root");
List<String> rectStyleClass = new ArrayList<String>();
rectStyleClass.add("rect");
Selector root = SimpleSelectorShim.getSimpleSelector("*", rootStyleClass, null, null);
ParsedValue fxBaseValue = new CssParserShim().parseExpr("-fx-base", "red");
Declaration fxBase = DeclarationShim.getDeclaration("-fx-base", fxBaseValue, false);
ParsedValueImpl<String,String> fxColorValue = new ParsedValueImpl<String,String>(fxBase.getProperty(), null, true);
Declaration fxColor = DeclarationShim.getDeclaration("-fx-color", fxColorValue, false);
ParsedValueImpl<Color,Color> fxFillShouldNotMatchValue = new ParsedValueImpl<Color,Color>(Color.RED, null);
Declaration fxFillShouldNotMatch = DeclarationShim.getDeclaration("-fx-fill", fxFillShouldNotMatchValue, false);
List<Selector> selectors = new ArrayList<Selector>();
Collections.addAll(selectors, root);
List<Declaration> declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxBase, fxColor, fxFillShouldNotMatch);
Rule baseRule = RuleShim.getRule(selectors, declarations);
rules.add(baseRule);
Selector rect = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, null, null);
ParsedValue fxFillValue = new CssParserShim().parseExpr("-fx-fill", "-fx-color");
Declaration fxFill = DeclarationShim.getDeclaration("-fx-fill", fxFillValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rect);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFill);
Rule rectRule = RuleShim.getRule(selectors, declarations);
rules.add(rectRule);
List<String> pseudoclasses = new ArrayList<String>();
pseudoclasses.add("hover");
Selector rectHover = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, pseudoclasses, null);
ParsedValueImpl<Color,Color> fxFillHoverValue = new ParsedValueImpl<Color,Color>(Color.YELLOW, null);
Declaration fxFillHover = DeclarationShim.getDeclaration("-fx-fill", fxFillHoverValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, rectHover);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFillHover);
Rule rectHoverRule = RuleShim.getRule(selectors, declarations);
rules.add(rectHoverRule);
List<Style> expecteds = new ArrayList<Style>();
Collections.addAll(expecteds,
new Style(root, fxBase),
new Style(root, fxColor),
new Style(rect, fxFill),
new Style(rectHover, fxFillHover)
);
final Rectangle rectangle = new Rectangle();
rectangle.getStyleClass().add("rect");
final Group group = new Group();
group.getChildren().add(rectangle);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
final CssMetaData FILL = get(rectangle.getCssMetaData(), "-fx-fill");
final List<Style> actuals = NodeHelper.getMatchingStyles(FILL, rectangle);
for (Style style : expecteds) {
actuals.remove(style);
}
assertTrue(actuals.toString(), actuals.isEmpty());
}
@Ignore("JDK-8234142")
@Test
public void testGetMatchingStylesReturnsInheritedProperty() {
final Stylesheet stylesheet = StylesheetShim.getStylesheet();
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final List<Rule> rules = stylesheet.getRules();
List<String> rootStyleClass = new ArrayList<String>();
rootStyleClass.add("root");
Selector root = SimpleSelectorShim.getSimpleSelector("*", rootStyleClass, null, null);
ParsedValue<Color,Color> fxFontShouldInheritValue = new CssParserShim().parseExpr("-fx-font", "12px system");
Declaration fxFontShouldInherit = DeclarationShim.getDeclaration("-fx-font", fxFontShouldInheritValue, false);
List<Selector> selectors = new ArrayList<Selector>();
Collections.addAll(selectors, root);
List<Declaration> declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFontShouldInherit);
Rule baseRule = RuleShim.getRule(selectors, declarations);
rules.add(baseRule);
List<String> textStyleClass = new ArrayList<String>();
textStyleClass.add("text");
Selector textSelector = SimpleSelectorShim.getSimpleSelector("*", textStyleClass, null, null);
ParsedValue fxFillValue = new CssParserShim().parseExpr("-fx-fill", "red");
Declaration fxFill = DeclarationShim.getDeclaration("-fx-fill", fxFillValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, textSelector);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFill);
Rule rectRule = RuleShim.getRule(selectors, declarations);
rules.add(rectRule);
List<Style> expecteds = new ArrayList<Style>();
Collections.addAll(expecteds,
new Style(root, fxFontShouldInherit)
);
final Text text = new Text("text");
text.getStyleClass().add("text");
final Group group = new Group();
group.getChildren().add(text);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
final CssMetaData FONT = get(text.getCssMetaData(), "-fx-font");
final List<Style> actuals = NodeHelper.getMatchingStyles(FONT, text);
assertEquals(expecteds.size(), actuals.size(), 0);
for (Style style : expecteds) {
if (!actuals.remove(style)) fail();
}
assertTrue(actuals.isEmpty());
}
@Ignore("JDK-8234142")
@Test
public void testGetMatchingStylesReturnsSubProperty() {
final Stylesheet stylesheet = StylesheetShim.getStylesheet();
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final List<Rule> rules = stylesheet.getRules();
List<String> rootStyleClass = new ArrayList<String>();
rootStyleClass.add("root");
Selector root = SimpleSelectorShim.getSimpleSelector("*", rootStyleClass, null, null);
ParsedValue<Color,Color> fxFontShouldInheritValue = new CssParserShim().parseExpr("-fx-font", "12px system");
Declaration fxFontShouldInherit = DeclarationShim.getDeclaration("-fx-font", fxFontShouldInheritValue, false);
List<Selector> selectors = new ArrayList<Selector>();
Collections.addAll(selectors, root);
List<Declaration> declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFontShouldInherit);
Rule baseRule = RuleShim.getRule(selectors, declarations);
rules.add(baseRule);
List<String> rectStyleClass = new ArrayList<String>();
rectStyleClass.add("text");
Selector textSelector = SimpleSelectorShim.getSimpleSelector("*", rectStyleClass, null, null);
ParsedValue fxFillValue = new CssParserShim().parseExpr("-fx-fill", "red");
Declaration fxFill = DeclarationShim.getDeclaration("-fx-fill", fxFillValue, false);
ParsedValue fxFontFamilyValue = new CssParserShim().parseExpr("-fx-font-family", "arial");
Declaration fxFontFamily = DeclarationShim.getDeclaration("-fx-font-family", fxFontFamilyValue, false);
selectors = new ArrayList<Selector>();
Collections.addAll(selectors, textSelector);
declarations = new ArrayList<Declaration>();
Collections.addAll(declarations, fxFill, fxFontFamily);
Rule rectRule = RuleShim.getRule(selectors, declarations);
rules.add(rectRule);
List<Style> expecteds = new ArrayList<Style>();
Collections.addAll(expecteds,
new Style(textSelector, fxFontFamily),
new Style(root, fxFontShouldInherit)
);
final Text text = new Text();
text.getStyleClass().add("text");
final Group group = new Group();
group.getChildren().add(text);
Scene scene = new Scene(group);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
final CssMetaData FONT = get(text.getCssMetaData(), "-fx-font");
final List<Style> actuals = NodeHelper.getMatchingStyles(FONT, text);
assertEquals(expecteds.size(), actuals.size(), 0);
for (Style style : expecteds) {
if (!actuals.remove(style)) fail();
}
assertTrue(actuals.isEmpty());
}
@Test
public void testRT18097() {
try {
File f = System.getProperties().containsKey("CSS_META_DATA_TEST_DIR") ?
new File(System.getProperties().get("CSS_META_DATA_TEST_DIR").toString()) :
null;
if (f == null) {
ClassLoader cl = Thread.currentThread().getContextClassLoader();
URL base = cl.getResource("javafx/../javafx");
f = new File(base.toURI());
}
assertTrue("" + f.getCanonicalPath() + " is not a directory", f.isDirectory());
recursiveCheck(f, f.getPath().length() - 7);
} catch (Exception ex) {
ex.printStackTrace(System.err);
fail(ex.getMessage());
}
}
private static void checkClass(Class someClass) {
if (javafx.scene.Node.class.isAssignableFrom(someClass) &&
Modifier.isAbstract(someClass.getModifiers()) == false) {
String what = someClass.getName();
try {
Method m = someClass.getMethod("getClassCssMetaData", (Class[]) null);
Node node = (Node)someClass.getDeclaredConstructor().newInstance();
List<CssMetaData<? extends Styleable, ?>> list = (List<CssMetaData<? extends Styleable, ?>>)m.invoke(null);
if(list == null || list.isEmpty()) return;
for (CssMetaData styleable : list) {
what = someClass.getName() + " " + styleable.getProperty();
WritableValue writable = styleable.getStyleableProperty(node);
assertNotNull(what, writable);
Object defaultValue = writable.getValue();
Object initialValue = styleable.getInitialValue((Node) someClass.getDeclaredConstructor().newInstance());
if (defaultValue instanceof Number) {
assert(initialValue instanceof Number);
double d1 = ((Number)defaultValue).doubleValue();
double d2 = ((Number)initialValue).doubleValue();
assertEquals(what, d1, d2, .001);
} else if (defaultValue != null && defaultValue.getClass().isArray()) {
assertTrue(what, Arrays.equals((Object[])defaultValue, (Object[])initialValue));
} else {
assertEquals(what, defaultValue, initialValue);
}
}
} catch (NoSuchMethodException ex) {
System.err.println("NoSuchMethodException: " + what);
} catch (IllegalAccessException ex) {
System.err.println("IllegalAccessException: " + what);
} catch (IllegalArgumentException ex) {
System.err.println("IllegalArgumentException: " + what);
} catch (InvocationTargetException ex) {
System.err.println("InvocationTargetException: " + what);
} catch (InstantiationException ex) {
System.err.println("InstantiationException: " + what);
}
}
}
private static void checkDirectory(File directory, final int pathLength) {
if (directory.isDirectory()) {
for (File file : directory.listFiles()) {
if (file.isFile() && file.getName().endsWith(".class")) {
final int len = file.getPath().length() - ".class".length();
final String clName =
file.getPath().substring(pathLength+1, len).replace(File.separatorChar,'.');
try {
final Class cl = Class.forName(clName);
if (cl != null) checkClass(cl);
} catch(ClassNotFoundException ex) {
System.err.println(ex.toString() + " " + clName);
}
}
}
}
}
private static void recursiveCheck(File directory, int pathLength) {
if (directory.isDirectory()) {
checkDirectory(directory, pathLength);
for (File subFile : directory.listFiles()) {
recursiveCheck(subFile, pathLength);
}
}
}
@Ignore("JDK-8234143")
@Test
public void testRT_21185() {
Color c1 = new Color(.1,.2,.3,1.0);
Color c2 = new Color(.1,.2,.3,1.0);
Rectangle rect = new Rectangle();
rect.setFill(c1);
StyleableProperty fill = (StyleableProperty)rect.fillProperty();
StyleOrigin origin = ((StyleableProperty)rect.fillProperty()).getStyleOrigin();
assertEquals(c1, c2);
fill.applyStyle(origin, c2);
assertSame(c1,rect.getFill());
c2 = new Color(.3,.2,.1,1.0);
fill.applyStyle(origin, c2);
assertSame(c2,rect.getFill());
fill.applyStyle(StyleOrigin.INLINE, c2);
origin = ((StyleableProperty)rect.fillProperty()).getStyleOrigin();
assertSame(StyleOrigin.INLINE, origin);
rect.setFill(null);
fill.applyStyle(origin, c2);
assertSame(c2, rect.getFill());
fill.applyStyle(origin, null);
assertNull(rect.getFill());
}
@Ignore("JDK-8234142")
@Test
public void testRT_24606() {
final Stylesheet stylesheet = new CssParser().parse(
".root { -fx-base: red; }" +
".group { -fx-color: -fx-base; }" +
".text { -fx-fill: -fx-color; }"
);
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
final Text text = new Text("HelloWorld");
text.getStyleClass().add("text");
text.setFill(Color.BLUE);
final Group group = new Group();
group.getStyleClass().add("group");
group.getChildren().add(text);
final Group root = new Group();
root.getChildren().add(group);
Scene scene = new Scene(root);
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
CssMetaData prop = ((StyleableProperty)text.fillProperty()).getCssMetaData();
List list = NodeHelper.getMatchingStyles(prop, text);
assertEquals(3, list.size(), 0);
}
@Test
public void testStyleConverterReturnType() {
final CssMetaData<Pane, TestEnum> TEST_ENUM =
new CssMetaData<Pane, TestEnum>("-test-enum", StyleConverter.getEnumConverter(TestEnum.class), TestEnum.LEFT, false) {
@Override
public boolean isSettable(Pane styleable) {
return false;
}
@Override
public StyleableProperty<TestEnum> getStyleableProperty(Pane styleable) {
return null;
}
};
}
}
