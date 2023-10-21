package test.javafx.scene.control;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.IndexRange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class IndexRangeValueOfTest {
@SuppressWarnings("rawtypes")
@Parameterized.Parameters public static Collection implementations() {
int[] numbers = new int[] { 10, 20, 1, -10, -20, -1, 0};
int[] rules = new int[] {0, 1, 2, 3};
List params = new LinkedList();
for (int i=0; i<numbers.length; i++) {
for (int j=0; j<numbers.length; j++) {
for (int k=0; k<rules.length; k++) {
final int start = numbers[i];
final int end = numbers[j];
final int rule = rules[k];
TestParameters param = new TestParameters();
switch(rule) {
case 0:
param.string = start + "," + end;
param.expected = IndexRange.normalize(start, end);
break;
case 1:
param.string = " " + start + " , " + end;
param.expected = IndexRange.normalize(start, end);
break;
case 2:
param.string = "a" + start + "," + end + "a";
break;
case 3:
param.string = start + ",," + end;
break;
}
params.add(new Object[] {param});
}
}
}
return params;
}
private TestParameters params;
public IndexRangeValueOfTest(TestParameters params) {
this.params = params;
}
@Test public void testValueOf() {
if (params.expected == null) {
try {
IndexRange.valueOf(params.string);
assertTrue(false);
} catch (IllegalArgumentException e) {
assertTrue(true);
}
} else {
IndexRange range = IndexRange.valueOf(params.string);
assertEquals(params.expected, range);
}
}
private static final class TestParameters {
private String string;
private IndexRange expected;
}
}
