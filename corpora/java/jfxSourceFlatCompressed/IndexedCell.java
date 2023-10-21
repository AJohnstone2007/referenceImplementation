package javafx.scene.control;
import javafx.css.PseudoClass;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
public class IndexedCell<T> extends Cell<T> {
public IndexedCell() {
getStyleClass().addAll(DEFAULT_STYLE_CLASS);
}
private int oldIndex = -1;
private ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper(this, "index", -1) {
@Override protected void invalidated() {
int newIndex = get();
boolean active = ((newIndex % 2) == 0);
pseudoClassStateChanged(PSEUDO_CLASS_EVEN, active);
pseudoClassStateChanged(PSEUDO_CLASS_ODD, !active);
indexChanged(oldIndex, newIndex);
}
};
public final int getIndex() { return index.get(); }
public final ReadOnlyIntegerProperty indexProperty() { return index.getReadOnlyProperty(); }
public void updateIndex(int newIndex) {
oldIndex = index.get();
if (oldIndex == newIndex) {
indexChanged(oldIndex, newIndex);
} else {
index.set(newIndex);
}
}
void indexChanged(int oldIndex, int newIndex) {
}
private static final String DEFAULT_STYLE_CLASS = "indexed-cell";
private static final PseudoClass PSEUDO_CLASS_ODD = PseudoClass.getPseudoClass("odd");
private static final PseudoClass PSEUDO_CLASS_EVEN = PseudoClass.getPseudoClass("even");
}
