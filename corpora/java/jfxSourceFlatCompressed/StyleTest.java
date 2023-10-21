package test.javafx.css;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javafx.css.CssParser;
import javafx.css.Declaration;
import javafx.css.Rule;
import javafx.css.RuleShim;
import javafx.css.Selector;
import javafx.css.Style;
import javafx.css.Stylesheet;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class StyleTest {
private static class Data {
private final String s1, s2;
private final boolean expected;
Data(String s1, String s2, boolean expected){
this.s1 = s1;
this.s2 = s2;
this.expected = expected;
}
@Override public String toString() {
return "\"" + s1 + "\" " + (expected ? "==" : "!=") + " \"" + s2 + "\"";
}
}
public StyleTest(Data data) {
this.data = data;
}
private final Data data;
private static Style createStyle(String stylesheetText) {
Stylesheet stylesheet = new CssParser().parse(stylesheetText);
Rule rule = stylesheet.getRules().get(0);
Selector sel = RuleShim.getUnobservedSelectorList(rule).get(0);
Declaration decl = RuleShim.getUnobservedDeclarationList(rule).get(0);
return new Style(sel, decl);
}
@Parameters
public static Collection data() {
return Arrays.asList(new Object[] {
new Object[] { new Data("*.style { -fx-fill: red; }",
"*.style { -fx-fill: red; }", true) },
new Object[] { new Data("*.style { -fx-fill: red; }",
"*.bad   { -fx-fill: red; }", false) },
new Object[] { new Data("*.style:p { -fx-fill: red; }",
"*.style:p { -fx-fill: red; }", true) },
new Object[] { new Data("*.style:p { -fx-fill: red; }",
"*.style:q { -fx-fill: red; }", false) },
new Object[] { new Data("*.style:p { -fx-fill: red; }",
"*.bad:p   { -fx-fill: red; }", false) },
new Object[] { new Data("*.style#c { -fx-fill: red; }",
"*.style#c { -fx-fill: red; }", true) },
new Object[] { new Data("*.style#c { -fx-fill: red; }",
"*.style#d { -fx-fill: red; }", false) },
new Object[] { new Data("*.style#c:p { -fx-fill: red; }",
"*.style#c:p { -fx-fill: red; }", true) },
new Object[] { new Data("*.style#c:p { -fx-fill: red; }",
"*.style#c:q { -fx-fill: red; }", false) },
new Object[] { new Data("*.style { -fx-fill: red; }",
"*.style { -fx-fill: green; }", false) },
new Object[] { new Data("*.style { -fx-border-color: red; }",
"*.style { -fx-fill: red; }", false) },
});
}
@Test
public void testEquals() {
Style instance = createStyle(data.s1);
Style obj = createStyle(data.s2);
boolean expected = data.expected;
boolean actual = instance.equals(obj);
assertTrue(data.toString(), expected == actual);
}
}
