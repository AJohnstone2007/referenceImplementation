package test.javafx.css;
import test.javafx.css.TypeTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.io.IOException;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import org.junit.Before;
import org.junit.Test;
import javafx.css.CssParser;
import javafx.css.CssParser;
import javafx.css.ParsedValue;
import javafx.css.Stylesheet;
public class InsetsTypeTest {
String[] css;
Insets[][] expResults;
final float[][][] fvals = new float[][][] {
{ {1.0f} },
{ {-1.0f, 0.0f, 1.0f, 2.0f} },
{ {1.0f}, {2.0f}, {3.0f}, {4.0f} },
{ {1.0f, 0.0f, 1.0f, 2.0f},
{2.0f, 0.0f, 1.0f, 2.0f},
{3.0f, 0.0f, 1.0f, 2.0f},
{4.0f, 0.0f, 1.0f, 2.0f} },
{ {1.0f},
{2.0f, -1.0f},
{3.0f, 0.0f, 1.0f, 2.0f},
{4.0f, 0.0f, 1.0f} }
};
@Before
public void setup() {
css = new String[fvals.length];
expResults = new Insets[fvals.length][];
for (int i=0; i<fvals.length; i++) {
StringBuilder sbuf = new StringBuilder();
expResults[i] = new Insets[fvals[i].length];
for (int j=0; j<fvals[i].length; j++) {
expResults[i][j] = makeInsets(fvals[i][j]);
for (int k=0; k<fvals[i][j].length; k++) {
sbuf.append(Float.toString(fvals[i][j][k]));
sbuf.append("px");
if (k+1 < fvals[i][j].length) sbuf.append(' ');
}
if (j+1 < fvals[i].length) sbuf.append(", ");
}
css[i] = sbuf.toString();
}
}
public InsetsTypeTest() {
}
final Insets makeInsets(float[] vals) {
float top = (vals.length > 0) ? vals[0] : 0.0f;
float right = (vals.length > 1) ? vals[1] : top;
float bottom = (vals.length > 2) ? vals[2] : top;
float left = (vals.length > 3) ? vals[3] : right;
return new Insets(top, right, bottom, left);
}
void checkInsets(String msg, Insets expResult, Insets result) {
assertEquals(msg + "top", expResult.getTop(), result.getTop(), 0.01);
assertEquals(msg + "right", expResult.getRight(), result.getRight(), 0.01);
assertEquals(msg + "bottom", expResult.getBottom(), result.getBottom(), 0.01);
assertEquals(msg + "left", expResult.getLeft(), result.getLeft(), 0.01);
}
@Test
public void testConvert() {
for (int i=0; i<css.length; i++) {
Stylesheet stylesheet =
new CssParser().parse("* { -fx-border-insets: " + css[i] + "; }");
ParsedValue value =
TypeTest.getValueFor(stylesheet, "-fx-border-insets");
Insets[] insets = (Insets[]) value.convert(Font.getDefault());
for(int j=0; j<insets.length; j++) {
String msg = Integer.toString(i) + "." + Integer.toString(j);
checkInsets(msg, expResults[i][j], insets[j]);
}
}
}
}
