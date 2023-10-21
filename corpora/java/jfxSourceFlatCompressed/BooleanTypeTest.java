package test.com.sun.javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import static org.junit.Assert.assertEquals;
import javafx.css.ParsedValue;
import javafx.scene.text.Font;
import org.junit.Test;
import javafx.css.converter.BooleanConverter;
public class BooleanTypeTest {
public BooleanTypeTest() {
}
@Test
public void testConvert() {
Font font = null;
ParsedValue< String,Boolean> value = new ParsedValueImpl<String,Boolean>("true", BooleanConverter.getInstance());
Boolean expResult = Boolean.TRUE;
Boolean result = value.convert(font);
assertEquals(expResult, result);
value = value = new ParsedValueImpl<String,Boolean>("false", BooleanConverter.getInstance());
expResult = Boolean.FALSE;
result = value.convert(font);
assertEquals(expResult, result);
value = new ParsedValueImpl<String,Boolean>(null, BooleanConverter.getInstance());
expResult = Boolean.FALSE;
result = value.convert(font);
assertEquals(expResult, result);
}
}
