package javafx.beans.property;
import javafx.collections.ListChangeListener;
public class ReadOnlyListPropertyBaseShim {
public static void fireValueChangedEvent(ReadOnlyListPropertyBase base) {
base.fireValueChangedEvent();
}
public static void fireValueChangedEvent(ReadOnlyListPropertyBase base,
ListChangeListener.Change change) {
base.fireValueChangedEvent(change);
}
}
