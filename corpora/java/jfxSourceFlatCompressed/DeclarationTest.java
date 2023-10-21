package test.javafx.css;
import javafx.scene.paint.Color;
import com.sun.javafx.css.ParsedValueImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.css.Declaration;
import javafx.css.DeclarationShim;
import javafx.css.Rule;
import javafx.css.RuleShim;
import javafx.css.SelectorShim;
import javafx.css.StyleOrigin;
import javafx.css.Stylesheet;
import javafx.css.StylesheetShim;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class DeclarationTest {
private static class Data {
private final Declaration d1, d2;
private final boolean expected;
Data(Declaration d1, Declaration d2, boolean expected){
this.d1 = d1;
this.d2 = d2;
this.expected = expected;
}
@Override public String toString() {
return "\"" + d1 + "\" " + (expected ? "==" : "!=") + " \"" + d2 + "\"";
}
}
public DeclarationTest(Data data) {
this.data = data;
}
private final Data data;
@Parameters
public static Collection data() {
int n = 0;
final int GI = n++;
final int YI = n++;
final int GA1 = n++;
final int YA1 = n++;
final int GA2 = n++;
final int YA2 = n++;
final Declaration[] DECLS = new Declaration[n];
Stylesheet inlineSS = new StylesheetShim() {
{
setOrigin(StyleOrigin.INLINE);
DECLS[GI] = DeclarationShim.getDeclaration("-fx-base", new ParsedValueImpl<Color,Color>(Color.GREEN, null), false);
DECLS[YI] = DeclarationShim.getDeclaration("-fx-color", new ParsedValueImpl<Color,Color>(Color.YELLOW, null), false);
Collections.addAll(getRules(),
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(DECLS[GI])),
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(DECLS[YI]))
);
}
};
Stylesheet authorSS_1 = new StylesheetShim() {
{
setOrigin(StyleOrigin.AUTHOR);
DECLS[GA1] = DeclarationShim.getDeclaration("-fx-base", new ParsedValueImpl<Color,Color>(Color.GREEN, null), false);
DECLS[YA1] = DeclarationShim.getDeclaration("-fx-color", new ParsedValueImpl<Color,Color>(Color.YELLOW, null), false);
Collections.addAll(getRules(),
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(DECLS[GA1])),
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(DECLS[YA1]))
);
}
};
Stylesheet authorSS_2 = new StylesheetShim() {
{
setOrigin(StyleOrigin.AUTHOR);
DECLS[GA2] = DeclarationShim.getDeclaration("-fx-base", new ParsedValueImpl<Color,Color>(Color.GREEN, null), false);
DECLS[YA2] = DeclarationShim.getDeclaration("-fx-color", new ParsedValueImpl<Color,Color>(Color.YELLOW, null), false);
Collections.addAll(getRules(),
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(DECLS[GA2])),
RuleShim.getRule(Arrays.asList(SelectorShim.getUniversalSelector()), Arrays.asList(DECLS[YA2]))
);
}
};
return Arrays.asList(new Object[] {
new Object[] { new Data(DECLS[GA1], DECLS[GA2], true) },
new Object[] { new Data(DECLS[GA1], DECLS[YA1], false) },
new Object[] { new Data(DECLS[GA1], DECLS[GI], false) }
});
}
@Test
public void testEquals() {
Declaration instance = data.d1;
Declaration obj = data.d2;
boolean expected = data.expected;
boolean actual = instance.equals(obj);
assertTrue(data.toString(), expected == actual);
}
}
