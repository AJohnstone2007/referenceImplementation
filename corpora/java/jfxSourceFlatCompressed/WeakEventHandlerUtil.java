package javafx.event;
public final class WeakEventHandlerUtil {
private WeakEventHandlerUtil() {
}
public static void clear(final WeakEventHandler weh) {
weh.clear();
}
}
