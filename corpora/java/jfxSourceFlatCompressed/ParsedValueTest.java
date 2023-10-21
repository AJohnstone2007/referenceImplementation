package test.javafx.css;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.sun.javafx.css.ParsedValueImpl;
import javafx.css.StylesheetShim;
import javafx.css.StyleConverter.StringStore;
import javafx.css.converter.SizeConverter;
import javafx.scene.text.Font;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StylesheetShim;
public class ParsedValueTest {
public ParsedValueTest() {
}
@Test
public void testGetValue() {
ParsedValue<Size,Size> instance =
new ParsedValueImpl<Size,Size>(new Size(100.0, SizeUnits.PERCENT), null);
Size expResult = new Size(100.0, SizeUnits.PERCENT);;
Size result = instance.getValue();
assertEquals(expResult, result);
}
@Test
public void testConvert() {
Font font = Font.getDefault();
Size size = new Size(1.0, SizeUnits.EM);
ParsedValue<ParsedValue<?,Size>,Number> value =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
SizeConverter.getInstance());
double expResult = font.getSize();
double result = value.convert(font).doubleValue();
assertEquals(expResult, result, 0.01);
}
@Test
public void testEquals() {
Font font = Font.getDefault();
Size size = new Size(1.0, SizeUnits.EM);
ParsedValue<ParsedValue<?,Size>,Number> value1 =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
SizeConverter.getInstance());
ParsedValue<ParsedValue<?,Size>,Number> value2 =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
null);
assertTrue(value1.equals(value2));
value1 =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
SizeConverter.getInstance());
value2 =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null),
SizeConverter.getInstance());
assertFalse(value1.equals(value2));
value2 =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.PX), null),
SizeConverter.getInstance());
assertFalse(value1.equals(value2));
value2 =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(null, null);
assertFalse(value1.equals(value2));
ParsedValue<ParsedValue[],Number[]> value3 =
new ParsedValueImpl<ParsedValue[],Number[]>(
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
}, SizeConverter.SequenceConverter.getInstance()
);
assertFalse(value1.equals(value3));
assertFalse(value3.equals(value1));
ParsedValue<ParsedValue[],Number[]> value4 =
new ParsedValueImpl<ParsedValue[],Number[]>(
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
}, SizeConverter.SequenceConverter.getInstance()
);
assertTrue(value3.equals(value4));
value4 =
new ParsedValueImpl<ParsedValue[],Number[]>(
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null),
null
}, SizeConverter.SequenceConverter.getInstance()
);
assertFalse(value3.equals(value4));
value4 =
new ParsedValueImpl<ParsedValue[],Number[]>(
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null)
}, SizeConverter.SequenceConverter.getInstance()
);
assertFalse(value3.equals(value4));
value4 =
new ParsedValueImpl<ParsedValue[],Number[]>(
new ParsedValueImpl[] {
null
}, SizeConverter.SequenceConverter.getInstance()
);
assertFalse(value3.equals(value4));
value4 =
new ParsedValueImpl<ParsedValue[],Number[]>(
null,
SizeConverter.SequenceConverter.getInstance()
);
assertFalse(value3.equals(value4));
ParsedValue<ParsedValue[][],Number[][]> value5 =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(4.0, SizeUnits.EM), null)
}
}, null
);
assertFalse(value1.equals(value5));
assertFalse(value3.equals(value5));
assertFalse(value5.equals(value1));
assertFalse(value5.equals(value3));
ParsedValue<ParsedValue[][],Number[][]> value6 =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(4.0, SizeUnits.EM), null)
}
}, null
);
assertTrue(value5.equals(value6));
value6 =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(5.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(4.0, SizeUnits.EM), null)
}
}, null
);
assertFalse(value5.equals(value6));
value6 =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null),
null
}
}, null
);
assertFalse(value5.equals(value6));
value6 =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
null
}, null
);
assertFalse(value5.equals(value6));
}
@Test
public void test_RT_24614() {
ParsedValue<String,String> value1 =
new ParsedValueImpl<>("FOO", null);
ParsedValue<String,String> value2 =
new ParsedValueImpl<>("FOO", null);
assertTrue(value1.equals(value2));
value1 =
new ParsedValueImpl<>("FOO", null);
value2 =
new ParsedValueImpl<>("foo", null);
assertTrue(value1.equals(value2));
ParsedValueImpl<ParsedValue<?,Size>,Number> value3 =
new ParsedValueImpl<>(
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.PX), null),
SizeConverter.getInstance());
value1 =
new ParsedValueImpl<>("FOO", null);
assertFalse(value1.equals(value3));
assertFalse(value3.equals(value1));
ParsedValue<ParsedValue[],String[]> value4 =
new ParsedValueImpl<>(
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("FOO", null),
new ParsedValueImpl<String,String>("BAR", null)
}, null
);
ParsedValue<ParsedValue[],String[]> value5 =
new ParsedValueImpl<>(
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("foo", null),
new ParsedValueImpl<String,String>("bar", null)
}, null
);
assertTrue(value4.equals(value5));
assertTrue(value5.equals(value4));
value4 =
new ParsedValueImpl<>(
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("FOO", null),
new ParsedValueImpl<String,String>("BAR", null)
}, null
);
value5 =
new ParsedValueImpl<>(
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("foo", null),
new ParsedValueImpl<String,String>("foo", null)
}, null
);
assertFalse(value4.equals(value5));
assertFalse(value5.equals(value4));
ParsedValue<ParsedValue[][],String[][]> value6 =
new ParsedValueImpl<>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("foo", null),
new ParsedValueImpl<String,String>("bar", null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("FOO", null),
new ParsedValueImpl<String,String>("BAR", null)
}
}, null
);
ParsedValue<ParsedValue[][],String[][]> value7 =
new ParsedValueImpl<>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("FOO", null),
new ParsedValueImpl<String,String>("BAR", null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("foo", null),
new ParsedValueImpl<String,String>("bar", null)
}
}, null
);
assertTrue(value6.equals(value7));
assertTrue(value7.equals(value6));
value6 =
new ParsedValueImpl<>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("foo", null),
new ParsedValueImpl<String,String>("bar", null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("FOO", null),
new ParsedValueImpl<String,String>("BAR", null)
}
}, null
);
value7 =
new ParsedValueImpl<>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("FOO", null),
new ParsedValueImpl<String,String>("BAR", null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<String,String>("foo", null),
new ParsedValueImpl<String,String>("foo", null)
}
}, null
);
assertFalse(value6.equals(value7));
assertFalse(value7.equals(value6));
}
private void writeBinary(ParsedValueImpl parsedValue) {
try {
StringStore stringStore = new StringStore();
ByteArrayOutputStream baos = new ByteArrayOutputStream();
DataOutputStream dos = new DataOutputStream(baos);
parsedValue.writeBinary(dos, stringStore);
dos.close();
} catch (IOException ioe) {
org.junit.Assert.fail(parsedValue.toString());
}
}
@Test
public void testWriteReadBinary() throws Exception {
Font font = Font.getDefault();
Size size = new Size(1.0, SizeUnits.EM);
ParsedValueImpl parsedValue =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
SizeConverter.getInstance());
writeBinary(parsedValue);
parsedValue =
new ParsedValueImpl<ParsedValue[],Number[]>(
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
}, SizeConverter.SequenceConverter.getInstance()
);
writeBinary(parsedValue);
parsedValue =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(4.0, SizeUnits.EM), null)
}
}, null
);
writeBinary(parsedValue);
parsedValue =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null),
null
}
}, null
);
writeBinary(parsedValue);
parsedValue =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
null
}, null
);
writeBinary(parsedValue);
}
private void writeAndReadBinary(ParsedValueImpl<?,?> parsedValue) {
try {
StringStore stringStore = new StringStore();
ByteArrayOutputStream baos = new ByteArrayOutputStream();
DataOutputStream dos = new DataOutputStream(baos);
parsedValue.writeBinary(dos, stringStore);
dos.close();
String[] strings = stringStore.strings.toArray(new String[]{});
ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
DataInputStream dis = new DataInputStream(bais);
ParsedValue<?,?> pv = ParsedValueImpl.readBinary(StylesheetShim.BINARY_CSS_VERSION, dis, strings);
org.junit.Assert.assertEquals(parsedValue, pv);
} catch (IOException ioe) {
System.err.println(ioe);
org.junit.Assert.fail(parsedValue.toString());
}
}
@Test
public void testReadBinary() throws Exception {
Font font = Font.getDefault();
Size size = new Size(1.0, SizeUnits.EM);
ParsedValueImpl parsedValue =
new ParsedValueImpl<ParsedValue<?,Size>,Number>(
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
SizeConverter.getInstance());
writeAndReadBinary(parsedValue);
parsedValue =
new ParsedValueImpl<ParsedValue[],Number[]>(
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
}, SizeConverter.SequenceConverter.getInstance()
);
writeAndReadBinary(parsedValue);
parsedValue =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(4.0, SizeUnits.EM), null)
}
}, null
);
writeAndReadBinary(parsedValue);
parsedValue =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(3.0, SizeUnits.EM), null),
null
}
}, null
);
writeAndReadBinary(parsedValue);
parsedValue =
new ParsedValueImpl<ParsedValue[][],Number[][]>(
new ParsedValueImpl[][] {
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(1.0, SizeUnits.EM), null),
new ParsedValueImpl<Size,Size>(new Size(2.0, SizeUnits.EM), null)
},
null
}, null
);
writeAndReadBinary(parsedValue);
}
}
