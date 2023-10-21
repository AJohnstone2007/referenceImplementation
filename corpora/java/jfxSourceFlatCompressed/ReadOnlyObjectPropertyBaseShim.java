package javafx.beans.property;
public class ReadOnlyObjectPropertyBaseShim {
public static void fireValueChangedEvent(ReadOnlyObjectPropertyBase base) {
base.fireValueChangedEvent();
}
}
