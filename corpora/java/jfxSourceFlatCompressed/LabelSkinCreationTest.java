package test.javafx.scene.control.skin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import javafx.scene.control.SkinBaseShim;
import javafx.scene.control.skin.LabelSkin;
import javafx.scene.control.skin.LabeledSkinBaseShim;
@RunWith(Parameterized.class)
public class LabelSkinCreationTest {
private Paint fill;
private Font font;
private TextAlignment align;
private boolean underline;
private boolean wrapText;
private ContentDisplay contentDisplay;
private Node graphic;
private Label label;
private LabelSkin skin;
private Text text;
@SuppressWarnings("rawtypes")
@Parameters public static Collection implementations() {
Rectangle rect = new Rectangle();
rect.setWidth(25);
rect.setHeight(25);
return Arrays.asList(new Object[][] {
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.LEFT, null },
{ Color.RED, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.LEFT, null },
{ Color.BLACK, Font.font("Arial", 64), TextAlignment.LEFT, false, false, ContentDisplay.LEFT, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.JUSTIFY, false, false, ContentDisplay.LEFT, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, true, false, ContentDisplay.LEFT, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, true, ContentDisplay.LEFT, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.BOTTOM, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.LEFT, rect },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.BOTTOM, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.CENTER, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.GRAPHIC_ONLY, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.LEFT, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.RIGHT, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.TEXT_ONLY, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.TOP, null },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.BOTTOM, rect },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.CENTER, rect },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.GRAPHIC_ONLY, rect },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.LEFT, rect },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.RIGHT, rect },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.TEXT_ONLY, rect },
{ Color.BLACK, Font.getDefault(), TextAlignment.LEFT, false, false, ContentDisplay.TOP, rect },
});
}
public LabelSkinCreationTest(
Paint fill,
Font font,
TextAlignment align,
boolean underline,
boolean wrapText,
ContentDisplay contentDisplay,
Node graphic)
{
this.fill = fill;
this.font = font;
this.align = align;
this.underline = underline;
this.wrapText = wrapText;
this.contentDisplay = contentDisplay;
this.graphic = graphic;
}
@Before public void setup() {
label = new Label();
label.setTextFill(fill);
label.setFont(font);
label.setTextAlignment(align);
label.setUnderline(underline);
label.setWrapText(wrapText);
label.setContentDisplay(contentDisplay);
label.setGraphic(graphic);
label.setText("*");
label.resize(30, 30);
skin = new LabelSkin(label);
label.setSkin(skin);
text = LabeledSkinBaseShim.get_text(skin);
}
@Test public void labelWasInitializedCorrectly() {
assertSame(label.getTextFill(), text.getFill());
assertSame(label.getFont(), text.getFont());
assertSame(label.getTextAlignment(), text.getTextAlignment());
assertEquals(label.isUnderline(), text.isUnderline());
if (label.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY) {
if (label.getGraphic() == null) {
assertEquals(0, SkinBaseShim.getChildren(skin).size());
} else {
assertEquals(1, SkinBaseShim.getChildren(skin).size());
assertEquals(label.getGraphic(), SkinBaseShim.getChildren(skin).get(0));
}
} else if (label.getContentDisplay() == ContentDisplay.TEXT_ONLY) {
assertEquals(1, SkinBaseShim.getChildren(skin).size());
assertEquals(text, SkinBaseShim.getChildren(skin).get(0));
} else {
if (label.getGraphic() == null) {
assertEquals(1, SkinBaseShim.getChildren(skin).size());
assertEquals(text, SkinBaseShim.getChildren(skin).get(0));
} else {
assertEquals(2, SkinBaseShim.getChildren(skin).size());
assertEquals(label.getGraphic(), SkinBaseShim.getChildren(skin).get(0));
assertEquals(text, SkinBaseShim.getChildren(skin).get(1));
}
}
}
}
