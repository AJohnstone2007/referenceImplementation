package test.com.sun.javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import javafx.css.converter.SizeConverter;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.SizeUnitsShim;
import javafx.scene.text.Font;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class FontSizeTypeTest {
public FontSizeTypeTest() {
}
@Test
public void testConvertToPixels() {
ParsedValue<Size,Size> size = new ParsedValueImpl<Size,Size>(new Size(2.0f, SizeUnits.EM), null);
ParsedValue<ParsedValue<?,Size>,Number> value = new ParsedValueImpl<ParsedValue<?,Size>,Number>(size, SizeConverter.getInstance());
Font font = Font.getDefault();
double expResult = SizeUnitsShim.pixels(SizeUnits.EM, 2, 1, font);
double result = SizeConverter.getInstance().convert(value, font).doubleValue();
assertEquals(expResult, result, 0.01);
size = new ParsedValueImpl<Size,Size>(new Size(120.0f, SizeUnits.PERCENT), null);
value = new ParsedValueImpl<ParsedValue<?,Size>,Number>(size, SizeConverter.getInstance());
expResult = SizeUnitsShim.pixels(SizeUnits.PERCENT, 120, 1, font);
result = SizeConverter.getInstance().convert(value, font).doubleValue();
assertEquals(expResult, result, 0.01);
size = new ParsedValueImpl<Size,Size>(new Size(12.0f, SizeUnits.PT), null);
value = new ParsedValueImpl<ParsedValue<?,Size>,Number>(size, SizeConverter.getInstance());
expResult = SizeUnitsShim.pixels(SizeUnits.PT, 12, 1, font);
result = SizeConverter.getInstance().convert(value, font).doubleValue();
assertEquals(expResult, result, 0.01);
size = new ParsedValueImpl<Size,Size>(new Size(12.0f, SizeUnits.PX), null);
value = new ParsedValueImpl<ParsedValue<?,Size>,Number>(size, SizeConverter.getInstance());
expResult = SizeUnitsShim.pixels(SizeUnits.PX, 12, 1, font);
result = SizeConverter.getInstance().convert(value, font).doubleValue();
assertEquals(expResult, result, 0.01);
}
}
