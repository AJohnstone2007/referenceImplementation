package test.com.sun.javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import static org.junit.Assert.assertEquals;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
import org.junit.Test;
public class SizeTypeTest {
public SizeTypeTest() {
}
@Test
public void testConvert() {
Size size = new Size(2.0f, SizeUnits.EM);
ParsedValue<Size, Size> value = new ParsedValueImpl<Size,Size>(size,null);
Font font = null;
Size result = value.convert(font);
assertEquals(size, result);
ParsedValue<ParsedValue<?,Size>,Double> pxSize = new ParsedValueImpl(value,StyleConverter.getSizeConverter());
Float expResult = (float) size.pixels(font);
Float pixels = (float) ((double) pxSize.convert(font));
assertEquals(expResult, pixels);
}
}
