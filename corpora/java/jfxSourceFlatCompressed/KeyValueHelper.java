package com.sun.javafx.animation;
import com.sun.javafx.util.Utils;
import javafx.animation.KeyValue;
public class KeyValueHelper {
private static KeyValueAccessor keyValueAccessor;
static {
Utils.forceInit(KeyValue.class);
}
private KeyValueHelper() {
}
public static KeyValueType getType(KeyValue keyValue) {
return keyValueAccessor.getType(keyValue);
}
public static void setKeyValueAccessor(final KeyValueAccessor newAccessor) {
if (keyValueAccessor != null) {
throw new IllegalStateException();
}
keyValueAccessor = newAccessor;
}
public interface KeyValueAccessor {
KeyValueType getType(KeyValue keyValue);
}
}
