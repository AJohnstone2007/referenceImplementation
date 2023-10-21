package test.javafx.scene.web;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.io.File;
import java.net.MalformedURLException;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebHistory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import java.util.Date;
import java.util.concurrent.Callable;
import javafx.scene.web.WebHistory;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
public class HistoryTest extends TestBase {
WebHistory history = getEngine().getHistory();
AtomicBoolean entriesChanged = new AtomicBoolean(false);
AtomicBoolean titleChanged = new AtomicBoolean(false);
AtomicBoolean dateChanged = new AtomicBoolean(false);
AtomicBoolean indexChanged = new AtomicBoolean(false);
@Test public void test() {
submit(() -> {
try {
history.go(-1);
fail("go: IndexOutOfBoundsException is not thrown");
} catch (IndexOutOfBoundsException ex) {}
try {
history.go(1);
fail("go: IndexOutOfBoundsException is not thrown");
} catch (IndexOutOfBoundsException ex) {}
history.setMaxSize(99);
assertEquals("max size is wrong", history.getMaxSize(), 99);
});
checkLoad(new File("src/test/resources/test/html/h1.html"), 1, 0, "1");
checkLoad(new File("src/test/resources/test/html/h2.html"), 2, 1, "2");
history.getEntries().addListener(new ListChangeListener<WebHistory.Entry>() {
public void onChanged(ListChangeListener.Change<? extends WebHistory.Entry> c) {
c.next();
assertTrue("entries: change is wrong", c.wasAdded());
assertTrue("entries: size is wrong", c.getAddedSubList().size() == 1);
history.getEntries().removeListener(this);
entriesChanged.set(true);
}
});
checkLoad(new File("src/test/resources/test/html/h3.html"), 3, 2, "3");
ensureValueChanged(entriesChanged, "entries not changed after load");
history.getEntries().get(history.getCurrentIndex() - 1).lastVisitedDateProperty().addListener(newDateListener());
try { Thread.sleep(150); } catch (Exception e) {}
history.currentIndexProperty().addListener(new ChangeListener<Number>() {
public void changed(ObservableValue<? extends java.lang.Number> observable, Number oldValue, Number newValue) {
assertEquals("currentIndexProperty: old index is wrong", 2, oldValue);
assertEquals("currentIndexProperty: new index is wrong", 1, newValue);
observable.removeListener(this);
indexChanged.set(true);
}
});
submit(() -> {
history.go(-1);
});
waitLoadFinished();
check(new File("src/test/resources/test/html/h2.html"), 3, 1, "2");
ensureValueChanged(dateChanged, "date not changed after go(-1)");
ensureValueChanged(indexChanged, "index not changed after go(-1)");
submit(() -> {
history.go(1);
});
waitLoadFinished();
check(new File("src/test/resources/test/html/h3.html"), 3, 2, "3");
submit(() -> {
history.go(-2);
});
waitLoadFinished();
check(new File("src/test/resources/test/html/h1.html"), 3, 0, "1");
submit(() -> {
history.go(0);
});
submit(() -> {
try {
history.go(-1);
fail("go: IndexOutOfBoundsException is not thrown");
} catch (IndexOutOfBoundsException ex) {}
});
submit(() -> {
history.go(2);
});
waitLoadFinished();
check(new File("src/test/resources/test/html/h3.html"), 3, 2, "3");
submit(() -> {
try {
history.go(1);
fail("go: IndexOutOfBoundsException is not thrown");
} catch (IndexOutOfBoundsException ex) {}
});
submit(() -> {
history.setMaxSize(3);
});
checkLoad(new File("src/test/resources/test/html/h1.html"), 3, 2, "1");
submit(() -> {
history.setMaxSize(2);
assertEquals("entries: size is wrong", 2, history.getEntries().size());
assertEquals("entries: title is wrong", "2", history.getEntries().get(0).getTitle());
});
submit(() -> {
history.setMaxSize(3);
history.go(-1);
});
waitLoadFinished();
checkLoad(new File("src/test/resources/test/html/h1.html"), 2, 1, "1");
checkLoad(new File("src/test/resources/test/html/h3.html"), 3, 2, "3");
submit(() -> {
history.go(-2);
});
waitLoadFinished();
checkLoad(new File("src/test/resources/test/html/h3.html"), 2, 1, "3");
load(new File("src/test/resources/test/html/h4.html"));
history.getEntries().get(history.getCurrentIndex()).lastVisitedDateProperty().addListener(newDateListener());
try { Thread.sleep(150); } catch (Exception e) {}
reload();
ensureValueChanged(dateChanged, "date not changed after reload");
submit(() -> {
history.setMaxSize(0);
assertEquals("maxSizeProperty: wrong value", 0, history.getEntries().size());
try {
history.maxSizeProperty().set(-1);
fail("maxSizeProperty: IllegalArgumentException is not thrown");
} catch (IllegalArgumentException ex) {}
});
}
void checkLoad(File file, int size, int index, String title) {
load(file);
check(file, size, index, title);
}
void check(File file, int size, int index, String title) {
assertEquals("entries: size is wrong", size, history.getEntries().size());
assertEquals("currentIndex: index is wrong", index, history.getCurrentIndex());
assertEquals("entries: url is wrong", file.toURI().toString(), history.getEntries().get(index).getUrl());
}
void ensureValueChanged(AtomicBoolean value, String errMsg) {
if (!value.compareAndSet(true, false)) {
fail(errMsg);
}
}
ChangeListener newDateListener() {
return new ChangeListener<Date>() {
long startTime = System.currentTimeMillis();
public void changed(ObservableValue<? extends Date> observable, Date oldValue, Date newValue) {
long curTime = System.currentTimeMillis();
if (newValue.before(oldValue) ||
newValue.getTime() < startTime ||
newValue.getTime() > curTime)
{
System.out.println("oldValue=" + oldValue.getTime() +
", newValue=" + newValue.getTime() +
", startTime=" + startTime +
", curTime=" + curTime);
fail("entries: date is wrong");
}
observable.removeListener(this);
dateChanged.set(true);
}
};
}
}
