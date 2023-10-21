package javafx.scene;
import java.security.AccessController;
class PropertyHelper {
static boolean getBooleanProperty(final String propName) {
try {
@SuppressWarnings("removal")
boolean answer =
AccessController.doPrivileged((java.security.PrivilegedAction<Boolean>) () -> {
String propVal = System.getProperty(propName);
return "true".equals(propVal.toLowerCase());
});
return answer;
} catch (Exception any) {
}
return false;
}
}
