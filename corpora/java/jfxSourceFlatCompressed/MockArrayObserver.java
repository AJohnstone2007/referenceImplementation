package test.javafx.collections;
import javafx.collections.ArrayChangeListener;
import javafx.collections.ObservableArray;
import static org.junit.Assert.*;
public class MockArrayObserver<T extends ObservableArray<T>> implements ArrayChangeListener<T> {
private boolean tooManyCalls;
static class Call<T> {
T array;
boolean sizeChanged;
int from;
int to;
@Override
public String toString() {
return "sizeChanged: " + sizeChanged + ", from: " + from + ", to: " + to;
}
}
Call call;
@Override
public void onChanged(T observableArray, boolean sizeChanged, int from, int to) {
if (call == null) {
call = new Call();
call.array = observableArray;
call.sizeChanged = sizeChanged;
call.from = from;
call.to = to;
assertFalse("Negative from index", from < 0);
assertFalse("Negative to index", to < 0);
assertFalse("from index is greater then to index", from > to);
assertFalse("No change in both elements and size", from == to && sizeChanged == false);
assertFalse("from index is greater than array size", from < to && from >= observableArray.size());
assertFalse("to index is greater than array size", from < to && to > observableArray.size());
} else {
tooManyCalls = true;
}
}
public void check0() {
assertNull(call);
}
public void checkOnlySizeChanged(T array) {
assertFalse("Too many array change events", tooManyCalls);
assertSame(array, call.array);
assertEquals(true, call.sizeChanged);
}
public void checkOnlyElementsChanged(T array,
int from,
int to) {
assertFalse("Too many array change events", tooManyCalls);
assertSame(array, call.array);
assertEquals(false, call.sizeChanged);
assertEquals(from, call.from);
assertEquals(to, call.to);
}
public void check(T array,
boolean sizeChanged,
int from,
int to) {
assertFalse("Too many array change events", tooManyCalls);
assertSame(array, call.array);
assertEquals(sizeChanged, call.sizeChanged);
assertEquals(from, call.from);
assertEquals(to, call.to);
}
public void check1() {
assertFalse("Too many array change events", tooManyCalls);
assertNotNull(call);
}
public void reset() {
call = null;
tooManyCalls = false;
}
}
