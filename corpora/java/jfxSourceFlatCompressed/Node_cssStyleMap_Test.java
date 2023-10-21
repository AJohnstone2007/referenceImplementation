package test.javafx.css;
import com.sun.javafx.css.CascadingStyle;
import com.sun.javafx.css.ParsedValueImpl;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.NodeHelper;
import javafx.css.converter.FontConverter;
import javafx.css.converter.SizeConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javafx.beans.property.Property;
import javafx.css.CssMetaData;
import javafx.css.Declaration;
import javafx.css.DeclarationShim;
import javafx.css.ParsedValue;
import javafx.css.Rule;
import javafx.css.RuleShim;
import javafx.css.Selector;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.Style;
import javafx.css.StyleOrigin;
import javafx.css.StyleableProperty;
import javafx.css.Stylesheet;
import javafx.css.StylesheetShim;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
public class Node_cssStyleMap_Test {
public Node_cssStyleMap_Test() {
}
boolean disabled = false;
private void checkFoundStyle(Property<?> property, Map<StyleableProperty<?>, List<Style>> map, List<Declaration> decls) {
List<Style> styles = map.get(property);
assert (styles != null && !styles.isEmpty());
String pname = ((StyleableProperty<?>)property).getCssMetaData().getProperty();
Declaration declaration = null;
for(Declaration decl : decls) {
if (pname.equals(decl.getProperty())) {
declaration = decl;
break;
}
}
assertNotNull(pname, declaration);
Style style = null;
for(Style s : styles) {
if (pname.equals(s.getDeclaration().getProperty())) {
style = s;
break;
}
}
assertNotNull(pname, style);
assert(style.getDeclaration() == declaration);
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
@Ignore("JDK-8234241")
@Test
public void testStyleMap() {
final List<Declaration> declsNoState = new ArrayList<Declaration>();
Collections.addAll(declsNoState,
DeclarationShim.getDeclaration("-fx-fill", new ParsedValueImpl<Color,Color>(Color.RED, null), false),
DeclarationShim.getDeclaration("-fx-stroke", new ParsedValueImpl<Color,Color>(Color.YELLOW, null), false),
DeclarationShim.getDeclaration("-fx-stroke-width", new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(3d, SizeUnits.PX), null),
SizeConverter.getInstance()), false)
);
final List<Selector> selsNoState = new ArrayList<Selector>();
Collections.addAll(selsNoState,
Selector.createSelector(".rect")
);
Rule rule = RuleShim.getRule(selsNoState, declsNoState);
Stylesheet stylesheet = new StylesheetShim("testStyleMap");
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
stylesheet.getRules().add(rule);
final List<Declaration> declsDisabledState = new ArrayList<Declaration>();
Collections.addAll(declsDisabledState,
DeclarationShim.getDeclaration("-fx-fill", new ParsedValueImpl<Color,Color>(Color.GRAY, null), false),
DeclarationShim.getDeclaration("-fx-stroke", new ParsedValueImpl<Color,Color>(Color.DARKGRAY, null), false)
);
final List<Selector> selsDisabledState = new ArrayList<Selector>();
Collections.addAll(selsDisabledState,
Selector.createSelector(".rect:disabled")
);
rule = RuleShim.getRule(selsDisabledState, declsDisabledState);
stylesheet.getRules().add(rule);
Rectangle rect = new Rectangle(50,50);
rect.getStyleClass().add("rect");
Group root = new Group();
root.getChildren().add(rect);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Scene scene = new Scene(root);
rect.applyCss();
Map<StyleableProperty<?>, List<Style>> map = NodeHelper.findStyles(rect, null);
assert (map != null && !map.isEmpty());
checkFoundStyle(rect.fillProperty(), map, declsNoState);
checkFoundStyle(rect.strokeProperty(), map, declsNoState);
checkFoundStyle(rect.strokeWidthProperty(), map, declsNoState);
rect.setDisable(true);
rect.applyCss();
map = NodeHelper.findStyles(rect, null);
assert (map != null && !map.isEmpty());
checkFoundStyle(rect.fillProperty(), map, declsDisabledState);
checkFoundStyle(rect.strokeProperty(), map, declsDisabledState);
checkFoundStyle(rect.strokeWidthProperty(), map, declsNoState);
}
@Test
public void testStyleMapChildren() {
final List<Declaration> declsNoState = new ArrayList<Declaration>();
Collections.addAll(declsNoState,
DeclarationShim.getDeclaration("-fx-fill", new ParsedValueImpl<Color,Color>(Color.RED, null), false)
);
final List<Selector> selsNoState = new ArrayList<Selector>();
Collections.addAll(selsNoState,
Selector.createSelector(".rect")
);
Rule rule = RuleShim.getRule(selsNoState, declsNoState);
Stylesheet stylesheet = new StylesheetShim("testStyleMapChildren");
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
stylesheet.getRules().add(rule);
Rectangle rect = new Rectangle(50,50);
rect.getStyleClass().add("rect");
Group root = new Group();
Group group = new Group();
root.getChildren().add(group);
group.getChildren().add(rect);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Scene scene = new Scene(root);
root.applyCss();
Map<StyleableProperty<?>, List<Style>> map = NodeHelper.findStyles(root, null);
assert (map != null && !map.isEmpty());
checkFoundStyle(rect.fillProperty(), map, declsNoState);
}
@Test
public void testRT_21212() {
final List<Declaration> rootDecls = new ArrayList<Declaration>();
Collections.addAll(rootDecls,
DeclarationShim.getDeclaration("-fx-font-size", new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(12, SizeUnits.PX), null),
SizeConverter.getInstance()), false)
);
final List<Selector> rootSels = new ArrayList<Selector>();
Collections.addAll(rootSels,
Selector.createSelector(".root")
);
Rule rootRule = RuleShim.getRule(rootSels, rootDecls);
Stylesheet stylesheet = new StylesheetShim("testRT_21212");
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
stylesheet.getRules().add(rootRule);
Group group = new Group();
group.getStyleClass().add("root");
final ParsedValue[] fontValues = new ParsedValue[] {
new ParsedValueImpl<String,String>("system", null),
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(1.5, SizeUnits.EM), null),
SizeConverter.getInstance()
),
null,
null
};
final List<Declaration> textDecls = new ArrayList<Declaration>();
Collections.addAll(textDecls,
DeclarationShim.getDeclaration("-fx-font", new ParsedValueImpl<ParsedValue[], Font>(
fontValues, FontConverter.getInstance()), false)
);
final List<Selector> textSels = new ArrayList<Selector>();
Collections.addAll(textSels,
Selector.createSelector(".text")
);
Rule textRule = RuleShim.getRule(textSels, textDecls);
stylesheet.getRules().add(textRule);
Text text = new Text("HelloWorld");
text.getStyleClass().add("text");
group.getChildren().add(text);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Scene scene = new Scene(group);
text.applyCss();
Map<StyleableProperty<?>, List<Style>> map = NodeHelper.findStyles(text, null);
assert (map != null && !map.isEmpty());
checkFoundStyle(text.fontProperty(), map, textDecls);
}
boolean containsProperty(CssMetaData key, Map<String,List<CascadingStyle>> map) {
if (map.containsKey(key)) return true;
List<CssMetaData> subProperties = key.getSubProperties();
if (subProperties != null && !subProperties.isEmpty()) {
for (CssMetaData subKey: subProperties) {
if (map.containsKey(subKey)) return true;
}
}
return false;
}
@Test
public void testRT_34799() {
Stylesheet stylesheet = new StylesheetShim("testRT_34799");
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
final List<Declaration> txtDecls = new ArrayList<Declaration>();
Collections.addAll(txtDecls,
DeclarationShim.getDeclaration("-fx-fill", new ParsedValueImpl<Color,Color>(Color.RED, null), false)
);
final List<Selector> textSels = new ArrayList<Selector>();
Collections.addAll(textSels,
Selector.createSelector(".rt-34799")
);
Rule txtRules = RuleShim.getRule(textSels, txtDecls);
stylesheet.getRules().add(txtRules);
final List<Style> expectedStyles = new ArrayList<>();
for (Rule rule : stylesheet.getRules()) {
for (Selector selector : rule.getSelectors()) {
for (Declaration declaration : RuleShim.getUnobservedDeclarationList(rule)) {
expectedStyles.add(
new Style(selector, declaration)
);
}
}
}
Text text = new Text("HelloWorld");
text.getStyleClass().add("rt-34799");
Group group = new Group();
group.getStyleClass().add("root");
group.getChildren().add(text);
StyleManager.getInstance().setDefaultUserAgentStylesheet(stylesheet);
Scene scene = new Scene(group);
group.applyCss();
int nExpected = expectedStyles.size();
assert(nExpected > 0);
for(CssMetaData cssMetaData : text.getCssMetaData()) {
List<Style> styles = NodeHelper.getMatchingStyles(cssMetaData, text);
if (styles != null && !styles.isEmpty()) {
assertTrue(expectedStyles.containsAll(styles));
assertTrue(styles.containsAll(expectedStyles));
nExpected -= 1;
}
}
assertEquals(nExpected, 0);
}
}
