package test.com.sun.javafx.scene.layout.region;
import javafx.scene.layout.BackgroundRepeat;
import org.junit.Test;
import javafx.css.ParsedValue;
import com.sun.javafx.css.ParsedValueImpl;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import com.sun.javafx.scene.layout.region.RepeatStructConverter;
public class BackgroundRepeatConverterTest {
@Test public void scenario1() {
ParsedValueImpl<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]> value =
new ParsedValueImpl<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]>(
new ParsedValueImpl[0][0], null
);
RepeatStruct[] results = RepeatStructConverter.getInstance().convert(value, null);
assertEquals(0, results.length, 0);
}
@Test
public void scenario2() {
ParsedValue<String,BackgroundRepeat>[][] values = new ParsedValueImpl[][] {
{null}
};
ParsedValueImpl<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]> value =
new ParsedValueImpl<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]>(
values, null
);
try {
RepeatStruct[] results = RepeatStructConverter.getInstance().convert(value, null);
fail("Expected NullPointerException");
} catch (NullPointerException expected) {
}
}
@Test public void scenario3() {
ParsedValue<String,BackgroundRepeat>[][] values = new ParsedValueImpl[][] {
{ new ParsedValueImpl("repeat", null), new ParsedValueImpl("round", null) }
};
ParsedValue<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]> value =
new ParsedValueImpl<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]>(
values, null
);
RepeatStruct[] results = RepeatStructConverter.getInstance().convert(value, null);
assertEquals(1, results.length, 0);
assertEquals(BackgroundRepeat.REPEAT, results[0].repeatX);
assertEquals(BackgroundRepeat.ROUND, results[0].repeatY);
}
@Test public void scenario4() {
ParsedValue<String,BackgroundRepeat>[][] values = new ParsedValueImpl[][] {
{ new ParsedValueImpl("space", null), new ParsedValueImpl("no-repeat", null) }
};
ParsedValue<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]> value =
new ParsedValueImpl<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]>(
values, null
);
RepeatStruct[] results = RepeatStructConverter.getInstance().convert(value, null);
assertEquals(1, results.length, 0);
assertEquals(BackgroundRepeat.SPACE, results[0].repeatX);
assertEquals(BackgroundRepeat.NO_REPEAT, results[0].repeatY);
}
@Test public void scenario5() {
ParsedValue<String,BackgroundRepeat>[][] values = new ParsedValueImpl[][] {
{ new ParsedValueImpl("no-repeat", null), new ParsedValueImpl("repeat", null) },
{ new ParsedValueImpl("space", null), new ParsedValueImpl("round", null) }
};
ParsedValue<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]> value =
new ParsedValueImpl<ParsedValue<String,BackgroundRepeat>[][], RepeatStruct[]>(
values, null
);
RepeatStruct[] results = RepeatStructConverter.getInstance().convert(value, null);
assertEquals(2, results.length, 0);
assertEquals(BackgroundRepeat.NO_REPEAT, results[0].repeatX);
assertEquals(BackgroundRepeat.REPEAT, results[0].repeatY);
assertEquals(BackgroundRepeat.SPACE, results[1].repeatX);
assertEquals(BackgroundRepeat.ROUND, results[1].repeatY);
}
}
