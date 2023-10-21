package javafx.beans.property;
import javafx.collections.MapChangeListener;
public class ReadOnlyMapPropertyBaseShim {
public static void fireValueChangedEvent(ReadOnlyMapPropertyBase base) {
base.fireValueChangedEvent();
}
public static void fireValueChangedEvent(ReadOnlyMapPropertyBase base,
MapChangeListener.Change change) {
base.fireValueChangedEvent(change);
}
}
