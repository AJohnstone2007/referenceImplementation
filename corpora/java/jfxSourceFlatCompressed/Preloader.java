package javafx.application;
import java.security.AccessController;
import java.security.PrivilegedAction;
public abstract class Preloader extends Application {
private static final String lineSeparator;
static {
@SuppressWarnings("removal")
String prop = AccessController.doPrivileged((PrivilegedAction<String>) () -> System.getProperty("line.separator"));
lineSeparator = prop != null ? prop : "\n";
}
public Preloader() {
}
public void handleProgressNotification(ProgressNotification info) {
}
public void handleStateChangeNotification(StateChangeNotification info) {
}
public void handleApplicationNotification(PreloaderNotification info) {
}
public boolean handleErrorNotification(ErrorNotification info) {
return false;
}
public static interface PreloaderNotification {
}
public static class ErrorNotification implements PreloaderNotification {
private String location;
private String details = "";
private Throwable cause;
public ErrorNotification(String location, String details, Throwable cause) {
if (details == null) throw new NullPointerException();
this.location = location;
this.details = details;
this.cause = cause;
}
public String getLocation() {
return location;
}
public String getDetails() {
return details;
}
public Throwable getCause() {
return cause;
}
@Override public String toString() {
StringBuilder str = new StringBuilder("Preloader.ErrorNotification: ");
str.append(details);
if (cause != null) {
str.append(lineSeparator).append("Caused by: ").append(cause.toString());
}
if (location != null) {
str.append(lineSeparator).append("Location: ").append(location);
}
return str.toString();
}
}
public static class ProgressNotification implements PreloaderNotification {
private final double progress;
private final String details;
public ProgressNotification(double progress) {
this(progress, "");
}
private ProgressNotification(double progress, String details) {
this.progress = progress;
this.details = details;
}
public double getProgress() {
return progress;
}
private String getDetails() {
return details;
}
}
public static class StateChangeNotification implements PreloaderNotification {
public enum Type {
BEFORE_LOAD,
BEFORE_INIT,
BEFORE_START
}
private final Type notificationType;
private final Application application;
public StateChangeNotification(Type notificationType){
this.notificationType = notificationType;
this.application = null;
}
public StateChangeNotification(Type notificationType, Application application) {
this.notificationType = notificationType;
this.application = application;
}
public Type getType() {
return notificationType;
}
public Application getApplication() {
return application;
}
}
}
