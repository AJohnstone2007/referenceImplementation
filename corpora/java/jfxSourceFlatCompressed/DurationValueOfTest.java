package test.javafx.util;
import java.util.Arrays;
import java.util.Collection;
import javafx.util.Duration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;
@RunWith(Parameterized.class)
public class DurationValueOfTest {
@SuppressWarnings("rawtypes")
@Parameterized.Parameters public static Collection implementations() {
return Arrays.asList(new Object[][]{
{"5ms", Duration.millis(5)},
{"0ms", Duration.ZERO},
{"25.5ms", Duration.millis(25.5)},
{"-10ms", Duration.millis(-10)},
{"5s", Duration.seconds(5)},
{"0s", Duration.ZERO},
{"25.5s", Duration.seconds(25.5)},
{"-10s", Duration.seconds(-10)},
{"5m", Duration.minutes(5)},
{"0m", Duration.ZERO},
{"25.5m", Duration.minutes(25.5)},
{"-10m", Duration.minutes(-10)},
{"5h", Duration.hours(5)},
{"0h", Duration.ZERO},
{"25.5h", Duration.hours(25.5)},
{"-10h", Duration.hours(-10)}
});
}
private String asString;
private Duration expected;
public DurationValueOfTest(String asString, Duration expected) {
this.asString = asString;
this.expected = expected;
}
@Test public void testValueOf() {
Duration actual = Duration.valueOf(asString);
assertEquals(expected, actual);
}
@Test(expected = IllegalArgumentException.class)
public void leadingSpaceResultsInException() {
Duration.valueOf(" " + asString);
}
@Test(expected = IllegalArgumentException.class)
public void trailingSpaceResultsInException() {
Duration.valueOf(asString + " ");
}
@Test(expected = IllegalArgumentException.class)
public void wrongCaseResultsInException() {
String mangled = asString.substring(0, asString.length()-1) + Character.toUpperCase(asString.charAt(asString.length()-1));
Duration.valueOf(mangled);
}
}
