package test.com.sun.webkit.text;
import com.sun.webkit.text.TextBreakIteratorShim;
import java.text.BreakIterator;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class TextBreakIteratorTest {
private static final int[] ITERATOR_TYPES = {
TextBreakIteratorShim.CHARACTER_ITERATOR,
TextBreakIteratorShim.WORD_ITERATOR,
TextBreakIteratorShim.LINE_ITERATOR,
TextBreakIteratorShim.SENTENCE_ITERATOR,
};
@Test
public void testBreakPrecedingFromGreaterThanStringLengthPosition() {
int method = TextBreakIteratorShim.TEXT_BREAK_PRECEDING;
for (int type : ITERATOR_TYPES) {
String[] strings = new String[] {
"", "a", "aa", "a a", "a a. a a."
};
for (String string : strings) {
int length = string.length();
BreakIterator it =
TextBreakIteratorShim.getIterator(type, "en-US", string, false);
int[] positions = new int[] {
length + 1, length + 2, length + 10
};
for (int position : positions) {
int result = TextBreakIteratorShim.invokeMethod(
it, method, position);
assertEquals("Unexpected result, type: " + type
+ ", string: " + string + ", position: " + position,
length, result);
}
}
}
}
}
