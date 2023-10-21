package javafx.beans.property;
public class ReadOnlySetPropertyBaseShim {
public static void fireValueChangedEvent(ReadOnlySetPropertyBase base) {
base.fireValueChangedEvent();
}
}
