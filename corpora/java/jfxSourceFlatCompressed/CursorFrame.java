package com.sun.javafx.cursor;
import java.util.HashMap;
import java.util.Map;
public abstract class CursorFrame {
public abstract CursorType getCursorType();
private Class<?> firstPlatformCursorClass;
private Object firstPlatformCursor;
private Map<Class<?>, Object> otherPlatformCursors;
public <T> T getPlatformCursor(final Class<T> platformCursorClass) {
if (firstPlatformCursorClass == platformCursorClass) {
return (T) firstPlatformCursor;
}
if (otherPlatformCursors != null) {
return (T) otherPlatformCursors.get(platformCursorClass);
}
return null;
}
public <T> void setPlatforCursor(final Class<T> platformCursorClass,
final T platformCursor) {
if ((firstPlatformCursorClass == null)
|| (firstPlatformCursorClass == platformCursorClass)) {
firstPlatformCursorClass = platformCursorClass;
firstPlatformCursor = platformCursor;
return;
}
if (otherPlatformCursors == null) {
otherPlatformCursors = new HashMap<Class<?>, Object>();
}
otherPlatformCursors.put(platformCursorClass, platformCursor);
}
}
