package test.javafx.animation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.junit.Test;
public class KeyFrameTest {
private static final Duration TIME = Duration.ONE;
private static final KeyValue[] NO_KEYVALUES = new KeyValue[0];
private static final List<KeyValue> NO_KEYVALUES_COL = Arrays.asList(NO_KEYVALUES);
private static final String NAME = "KeyFrameName";
private static final EventHandler<ActionEvent> ACTION = event -> {};
private final IntegerProperty TARGET = new SimpleIntegerProperty();
private final KeyValue[] ONE_KEYVALUE = new KeyValue[] {
new KeyValue(TARGET, 1)
};
private final KeyValue[] TWO_KEYVALUES = new KeyValue[] {
new KeyValue(TARGET, 0),
new KeyValue(TARGET, 1)
};
private final List<KeyValue> ONE_KEYVALUE_COL = Arrays.asList(ONE_KEYVALUE);
private final List<KeyValue> TWO_KEYVALUES_COL = Arrays.asList(TWO_KEYVALUES);
private <T> void assertSetEquals(T[] expected, Set<T> result) {
assertEquals(expected.length, result.size());
for (final T element : expected) {
assertTrue(result.contains(element));
}
}
@Test public void testConstructor_ObservableList() {
final KeyFrame kf0 = new KeyFrame(TIME, NAME, ACTION, NO_KEYVALUES_COL);
assertEquals(TIME, kf0.getTime());
assertEquals(NAME, kf0.getName());
assertEquals(ACTION, kf0.getOnFinished());
assertSetEquals(NO_KEYVALUES, kf0.getValues());
final KeyFrame kf1 = new KeyFrame(TIME, NAME, ACTION, ONE_KEYVALUE_COL);
assertEquals(TIME, kf1.getTime());
assertEquals(NAME, kf1.getName());
assertEquals(ACTION, kf1.getOnFinished());
assertSetEquals(ONE_KEYVALUE, kf1.getValues());
final KeyFrame kf2 = new KeyFrame(TIME, NAME, ACTION, TWO_KEYVALUES_COL);
assertEquals(TIME, kf2.getTime());
assertEquals(NAME, kf2.getName());
assertEquals(ACTION, kf2.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf2.getValues());
}
@Test public void testConstructor_ObservableList_SpecialCases() {
final KeyFrame kf1 = new KeyFrame(TIME, null, ACTION, TWO_KEYVALUES_COL);
assertEquals(TIME, kf1.getTime());
assertNull(kf1.getName());
assertEquals(ACTION, kf1.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf1.getValues());
final KeyFrame kf2 = new KeyFrame(TIME, NAME, null, TWO_KEYVALUES_COL);
assertEquals(TIME, kf2.getTime());
assertEquals(NAME, kf2.getName());
assertNull(kf2.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf2.getValues());
final KeyFrame kf3 = new KeyFrame(TIME, NAME, ACTION, (Collection<KeyValue>)null);
assertEquals(TIME, kf3.getTime());
assertEquals(NAME, kf3.getName());
assertEquals(ACTION, kf3.getOnFinished());
assertSetEquals(NO_KEYVALUES, kf3.getValues());
final KeyFrame kf4 = new KeyFrame(TIME, NAME, ACTION, (Collection<KeyValue>)null);
assertEquals(TIME, kf4.getTime());
assertEquals(NAME, kf4.getName());
assertEquals(ACTION, kf4.getOnFinished());
assertSetEquals(NO_KEYVALUES, kf4.getValues());
final KeyFrame kf5 = new KeyFrame(TIME, NAME, ACTION, Arrays.asList(TWO_KEYVALUES[0], null, TWO_KEYVALUES[1]));
assertEquals(TIME, kf5.getTime());
assertEquals(NAME, kf5.getName());
assertEquals(ACTION, kf5.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf2.getValues());
};
@Test(expected=NullPointerException.class)
public void testConstructor_ObservableList_DurationIsNull(){
new KeyFrame(null, NAME, ACTION, TWO_KEYVALUES_COL);
};
@Test(expected=IllegalArgumentException.class)
public void testConstructor_ObservableList_DurationIsNegative(){
new KeyFrame(Duration.millis(-1), NAME, ACTION, TWO_KEYVALUES_COL);
};
@Test(expected=IllegalArgumentException.class)
public void testConstructor_ObservableList_DuratinIsUnknown(){
new KeyFrame(Duration.UNKNOWN, NAME, ACTION, TWO_KEYVALUES_COL);
};
@Test public void testConstructor_Time_Name_Action_Valus() {
final KeyFrame kf0 = new KeyFrame(TIME, NAME, ACTION, NO_KEYVALUES);
assertEquals(TIME, kf0.getTime());
assertEquals(NAME, kf0.getName());
assertEquals(ACTION, kf0.getOnFinished());
assertSetEquals(NO_KEYVALUES, kf0.getValues());
final KeyFrame kf1 = new KeyFrame(TIME, NAME, ACTION, ONE_KEYVALUE);
assertEquals(TIME, kf1.getTime());
assertEquals(NAME, kf1.getName());
assertEquals(ACTION, kf1.getOnFinished());
assertSetEquals(ONE_KEYVALUE, kf1.getValues());
final KeyFrame kf2 = new KeyFrame(TIME, NAME, ACTION, TWO_KEYVALUES);
assertEquals(TIME, kf2.getTime());
assertEquals(NAME, kf2.getName());
assertEquals(ACTION, kf2.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf2.getValues());
}
@Test public void testConstructor_Time_Name_Action_Valus_SpecialCases() {
final KeyFrame kf1 = new KeyFrame(TIME, null, ACTION, TWO_KEYVALUES);
assertEquals(TIME, kf1.getTime());
assertNull(kf1.getName());
assertEquals(ACTION, kf1.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf1.getValues());
final KeyFrame kf2 = new KeyFrame(TIME, NAME, null, TWO_KEYVALUES);
assertEquals(TIME, kf2.getTime());
assertEquals(NAME, kf2.getName());
assertNull(kf2.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf2.getValues());
final KeyFrame kf3 = new KeyFrame(TIME, NAME, ACTION, new KeyValue[0]);
assertEquals(TIME, kf3.getTime());
assertEquals(NAME, kf3.getName());
assertEquals(ACTION, kf3.getOnFinished());
assertSetEquals(NO_KEYVALUES, kf3.getValues());
final KeyFrame kf4 = new KeyFrame(TIME, NAME, ACTION, (KeyValue[])null);
assertEquals(TIME, kf4.getTime());
assertEquals(NAME, kf4.getName());
assertEquals(ACTION, kf4.getOnFinished());
assertSetEquals(NO_KEYVALUES, kf4.getValues());
final KeyFrame kf5 = new KeyFrame(TIME, NAME, ACTION, TWO_KEYVALUES[0], null, TWO_KEYVALUES[1]);
assertEquals(TIME, kf5.getTime());
assertEquals(NAME, kf5.getName());
assertEquals(ACTION, kf5.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf5.getValues());
final KeyFrame kf6 = new KeyFrame(TIME, NAME, ACTION, (KeyValue)null);
assertEquals(TIME, kf6.getTime());
assertEquals(NAME, kf6.getName());
assertEquals(ACTION, kf6.getOnFinished());
assertSetEquals(NO_KEYVALUES, kf6.getValues());
}
@Test(expected=NullPointerException.class)
public void testConstructor_KeyValues_DurationIsNull(){
new KeyFrame(null, NAME, ACTION, TWO_KEYVALUES);
};
@Test(expected=IllegalArgumentException.class)
public void testConstructor_KeyValues_DurationIsNegative(){
new KeyFrame(Duration.millis(-1), NAME, ACTION, TWO_KEYVALUES);
};
@Test(expected=IllegalArgumentException.class)
public void testConstructor_KeyValues_DurationIsUnknown(){
new KeyFrame(Duration.UNKNOWN, NAME, ACTION, TWO_KEYVALUES);
};
@Test public void testConstructor_Time_Action_Valus() {
final KeyFrame kf0 = new KeyFrame(TIME, ACTION, TWO_KEYVALUES);
assertEquals(TIME, kf0.getTime());
assertNull(kf0.getName());
assertEquals(ACTION, kf0.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf0.getValues());
}
@Test public void testConstructor_Time_Name_Valus() {
final KeyFrame kf0 = new KeyFrame(TIME, NAME, TWO_KEYVALUES);
assertEquals(TIME, kf0.getTime());
assertEquals(NAME, kf0.getName());
assertNull(kf0.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf0.getValues());
}
@Test public void testConstructor_Time_Valus() {
final KeyFrame kf0 = new KeyFrame(TIME, TWO_KEYVALUES);
assertEquals(TIME, kf0.getTime());
assertNull(kf0.getName());
assertNull(kf0.getOnFinished());
assertSetEquals(TWO_KEYVALUES, kf0.getValues());
}
}
