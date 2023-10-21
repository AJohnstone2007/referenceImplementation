package javafx.scene.web;
import com.sun.webkit.BackForwardList;
import com.sun.webkit.WebPage;
import com.sun.webkit.event.WCChangeEvent;
import com.sun.webkit.event.WCChangeListener;
import java.net.URL;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public final class WebHistory {
public final class Entry {
private final URL url;
private final ReadOnlyObjectWrapper<String> title = new ReadOnlyObjectWrapper(this, "title");
private final ReadOnlyObjectWrapper<Date> lastVisitedDate = new ReadOnlyObjectWrapper(this, "lastVisitedDate");
private final BackForwardList.Entry peer;
private Entry(final BackForwardList.Entry entry) {
this.url = entry.getURL();
this.title.set(entry.getTitle());
this.lastVisitedDate.set(entry.getLastVisitedDate());
this.peer = entry;
entry.addChangeListener(e -> {
String _title = entry.getTitle();
if (_title == null || !_title.equals(getTitle())) {
title.set(_title);
}
Date _date = entry.getLastVisitedDate();
if (_date != null && !_date.equals(getLastVisitedDate())) {
lastVisitedDate.set(_date);
}
});
}
public String getUrl() {
assert url != null;
return url.toString();
}
public ReadOnlyObjectProperty<String> titleProperty() {
return title.getReadOnlyProperty();
}
public String getTitle() {
return title.get();
}
public ReadOnlyObjectProperty<Date> lastVisitedDateProperty() {
return lastVisitedDate.getReadOnlyProperty();
}
public Date getLastVisitedDate() {
return lastVisitedDate.get();
}
boolean isPeer(BackForwardList.Entry entry) {
return peer == entry;
}
@Override
public String toString() {
return "[url: " + getUrl()
+ ", title: " + getTitle()
+ ", date: " + getLastVisitedDate()
+ "]";
}
}
private final BackForwardList bfl;
private final ObservableList<Entry> list;
private final ObservableList<Entry> ulist;
WebHistory(WebPage page) {
this.list = FXCollections.<Entry>observableArrayList();
this.ulist = FXCollections.unmodifiableObservableList(list);
this.bfl = page.createBackForwardList();
setMaxSize(getMaxSize());
this.bfl.addChangeListener(e -> {
if (bfl.size() > list.size()) {
assert (bfl.size() == list.size() + 1);
list.add(new Entry(bfl.getCurrentEntry()));
WebHistory.this.setCurrentIndex(list.size() - 1);
return;
}
if (bfl.size() == list.size()) {
if (list.size() == 0) {
return;
}
assert (list.size() > 0);
BackForwardList.Entry last = bfl.get(list.size() - 1);
BackForwardList.Entry first = bfl.get(0);
if (list.get(list.size() - 1).isPeer(last)) {
WebHistory.this.setCurrentIndex(bfl.getCurrentIndex());
return;
} else if (!list.get(0).isPeer(first)) {
list.remove(0);
list.add(new Entry(last));
WebHistory.this.setCurrentIndex(bfl.getCurrentIndex());
return;
}
}
assert (bfl.size() <= list.size());
list.remove(bfl.size(), list.size());
int lastIndex = list.size() - 1;
if (lastIndex >= 0 && !list.get(lastIndex).isPeer(bfl.get(lastIndex))) {
list.remove(lastIndex);
list.add(new Entry(bfl.get(lastIndex)));
}
WebHistory.this.setCurrentIndex(bfl.getCurrentIndex());
});
}
private final ReadOnlyIntegerWrapper currentIndex =
new ReadOnlyIntegerWrapper(this, "currentIndex");
public ReadOnlyIntegerProperty currentIndexProperty() {
return currentIndex.getReadOnlyProperty();
}
public int getCurrentIndex() {
return currentIndexProperty().get();
}
private void setCurrentIndex(int value) {
currentIndex.set(value);
}
private IntegerProperty maxSize;
public IntegerProperty maxSizeProperty() {
if (maxSize == null) {
maxSize = new SimpleIntegerProperty(this, "maxSize", 100) {
@Override
public void set(int value) {
if (value < 0) {
throw new IllegalArgumentException("value cannot be negative.");
}
super.set(value);
}
};
}
return maxSize;
}
public void setMaxSize(int value) {
maxSizeProperty().set(value);
bfl.setMaximumSize(value);
}
public int getMaxSize() {
return maxSizeProperty().get();
}
public ObservableList<Entry> getEntries() {
return ulist;
}
public void go(int offset) throws IndexOutOfBoundsException {
if (offset == 0)
return;
int index = getCurrentIndex() + offset;
if (index < 0 || index >= list.size()) {
throw new IndexOutOfBoundsException("the effective index " + index
+ " is out of the range [0.."
+ (list.size() - 1) + "]");
}
bfl.setCurrentIndex(index);
}
}
