package test.javafx.util.converter;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.BigIntegerStringConverter;
import javafx.util.converter.BooleanStringConverter;
import javafx.util.converter.ByteStringConverter;
import javafx.util.converter.CharacterStringConverter;
import javafx.util.converter.CurrencyStringConverter;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.DateTimeStringConverter;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LongStringConverter;
import javafx.util.converter.NumberStringConverter;
import javafx.util.converter.PercentageStringConverter;
import javafx.util.converter.ShortStringConverter;
import javafx.util.converter.TimeStringConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class ParameterizedConverterTest {
private final Class<? extends StringConverter> converterClass;
private StringConverter converter;
@Parameterized.Parameters public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ BigDecimalStringConverter.class },
{ BigIntegerStringConverter.class },
{ BooleanStringConverter.class },
{ ByteStringConverter.class },
{ CharacterStringConverter.class },
{ CurrencyStringConverter.class },
{ DateStringConverter.class },
{ DateTimeStringConverter.class },
{ DefaultStringConverter.class },
{ DoubleStringConverter.class },
{ FloatStringConverter.class },
{ IntegerStringConverter.class },
{ LongStringConverter.class },
{ NumberStringConverter.class },
{ PercentageStringConverter.class },
{ ShortStringConverter.class },
{ TimeStringConverter.class },
});
}
public ParameterizedConverterTest(Class<? extends StringConverter> converterClass) {
this.converterClass = converterClass;
}
@Before public void setup() {
try {
converter = converterClass.getDeclaredConstructor().newInstance();
} catch (Exception ex) {
ex.printStackTrace();
}
}
@Test public void toString_testNull() {
assertEquals("", converter.toString(null));
}
@Test public void fromString_testEmptyStringWithWhiteSpace() {
if (converterClass == DefaultStringConverter.class) {
assertEquals("      ", converter.fromString("      "));
} else {
assertNull(converter.fromString("      "));
}
}
@Test public void fromString_testNull() {
assertNull(converter.fromString(null));
}
@Test public void fromString_testEmptyString() {
if (converterClass == DefaultStringConverter.class) {
assertEquals("", converter.fromString(""));
} else {
assertNull(converter.fromString(""));
}
}
}
