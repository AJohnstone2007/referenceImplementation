package javafx.print;
import java.util.Arrays;
import javafx.beans.NamedArg;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
public final class PageRange {
private ReadOnlyIntegerWrapper startPage, endPage;
public PageRange(@NamedArg("startPage") int startPage, @NamedArg("endPage") int endPage) {
if (startPage <= 0 || startPage > endPage) {
throw new IllegalArgumentException("Invalid range : " +
startPage + " -> " + endPage);
}
startPageImplProperty().set(startPage);
endPageImplProperty().set(endPage);
}
private ReadOnlyIntegerWrapper startPageImplProperty() {
if (startPage == null) {
startPage =
new ReadOnlyIntegerWrapper(PageRange.this, "startPage", 1) {
@Override
public void set(int value) {
if ((value <= 0) ||
(endPage != null && value < endPage.get())) {
return;
}
super.set(value);
}
};
}
return startPage;
}
public final ReadOnlyIntegerProperty startPageProperty() {
return startPageImplProperty().getReadOnlyProperty();
}
public final int getStartPage() {
return startPageProperty().get();
}
private ReadOnlyIntegerWrapper endPageImplProperty() {
if (endPage == null) {
endPage =
new ReadOnlyIntegerWrapper(PageRange.this, "endPage", 9999) {
@Override
public void set(int value) {
if ((value <= 0) ||
(startPage != null && value < startPage.get())) {
return;
}
super.set(value);
}
};
}
return endPage;
}
public final ReadOnlyIntegerProperty endPageProperty() {
return endPageImplProperty().getReadOnlyProperty();
}
public final int getEndPage() {
return endPageProperty().get();
}
@Override
public String toString() {
return "Pages " + getStartPage() + " to " + getEndPage();
}
}
