package test.com.sun.javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import javafx.css.ParsedValue;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
import org.junit.Test;
import javafx.css.converter.EnumConverter;
public class EnumTypeTest {
public EnumTypeTest() {
}
@Test
public void testConvert() {
StyleConverter sizeUnitsType = new EnumConverter(SizeUnits.class);
ParsedValue<String,Enum> value =
new ParsedValueImpl<String,Enum>("percent", sizeUnitsType);
Font font = null;
Enum expResult = SizeUnits.PERCENT;
Enum result = value.convert(font);
assertEquals(expResult, result);
value = new ParsedValueImpl<String,Enum>("SizeUnits.PERCENT", sizeUnitsType);
result = value.convert(font);
assertEquals(expResult, result);
try {
value = new ParsedValueImpl<String,Enum>("fubar", sizeUnitsType);
result = value.convert(font);
fail("expected IllegalArgumentException");
} catch (IllegalArgumentException iae) {
}
}
}
