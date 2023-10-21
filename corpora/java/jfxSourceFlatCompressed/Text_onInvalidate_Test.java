package test.javafx.scene.text;
import java.util.Arrays;
import java.util.Collection;
import javafx.geometry.VPos;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Text_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Text_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(Text.class, "text", "cool", new DirtyBits[] {DirtyBits.NODE_CONTENTS, DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Text.class, "x", 123.0, new DirtyBits[] {DirtyBits.NODE_GEOMETRY})},
{new Configuration(Text.class, "y", 123.0, new DirtyBits[] {DirtyBits.NODE_GEOMETRY})},
{new Configuration(Text.class, "font", new Font(10) , new DirtyBits[] {DirtyBits.TEXT_FONT, DirtyBits.NODE_CONTENTS, DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Text.class, "wrappingWidth", 5 , new DirtyBits[] {DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Text.class, "underline", true , new DirtyBits[] {DirtyBits.TEXT_ATTRS})},
{new Configuration(Text.class, "strikethrough", true , new DirtyBits[] {DirtyBits.TEXT_ATTRS})},
{new Configuration(Text.class, "textAlignment", TextAlignment.RIGHT , new DirtyBits[] {DirtyBits.NODE_CONTENTS, DirtyBits.NODE_BOUNDS, DirtyBits.NODE_GEOMETRY})},
{new Configuration(Text.class, "textOrigin", VPos.BOTTOM , new DirtyBits[] {DirtyBits.NODE_BOUNDS , DirtyBits.NODE_GEOMETRY})},
{new Configuration(Text.class, "boundsType", TextBoundsType.VISUAL , new DirtyBits[] {DirtyBits.NODE_BOUNDS , DirtyBits.NODE_GEOMETRY})}
};
return Arrays.asList(data);
}
}
