package javafx.application;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import javafx.application.Preloader.PreloaderNotification;
import javafx.css.Stylesheet;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.sun.javafx.application.LauncherImpl;
import com.sun.javafx.application.ParametersImpl;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.css.StyleManager;
public abstract class Application {
public static final String STYLESHEET_CASPIAN = "CASPIAN";
public static final String STYLESHEET_MODENA = "MODENA";
public static void launch(Class<? extends Application> appClass, String... args) {
LauncherImpl.launchApplication(appClass, args);
}
public static void launch(String... args) {
StackTraceElement[] cause = Thread.currentThread().getStackTrace();
boolean foundThisMethod = false;
String callingClassName = null;
for (StackTraceElement se : cause) {
String className = se.getClassName();
String methodName = se.getMethodName();
if (foundThisMethod) {
callingClassName = className;
break;
} else if (Application.class.getName().equals(className)
&& "launch".equals(methodName)) {
foundThisMethod = true;
}
}
if (callingClassName == null) {
throw new RuntimeException("Error: unable to determine Application class");
}
try {
Class theClass = Class.forName(callingClassName, false,
Thread.currentThread().getContextClassLoader());
if (Application.class.isAssignableFrom(theClass)) {
Class<? extends Application> appClass = theClass;
LauncherImpl.launchApplication(appClass, args);
} else {
throw new RuntimeException("Error: " + theClass
+ " is not a subclass of javafx.application.Application");
}
} catch (RuntimeException ex) {
throw ex;
} catch (Exception ex) {
throw new RuntimeException(ex);
}
}
public Application() {
}
public void init() throws Exception {
}
public abstract void start(Stage primaryStage) throws Exception;
public void stop() throws Exception {
}
private HostServices hostServices = null;
public final HostServices getHostServices() {
synchronized (this) {
if (hostServices == null) {
hostServices = new HostServices(this);
}
return hostServices;
}
}
public final Parameters getParameters() {
return ParametersImpl.getParameters(this);
}
public final void notifyPreloader(PreloaderNotification info) {
LauncherImpl.notifyPreloader(this, info);
}
public static abstract class Parameters {
public Parameters() {
}
public abstract List<String> getRaw();
public abstract List<String> getUnnamed();
public abstract Map<String, String> getNamed();
}
private static String userAgentStylesheet = null;
public static String getUserAgentStylesheet() {
return userAgentStylesheet;
}
public static void setUserAgentStylesheet(String url) {
userAgentStylesheet = url;
if (url == null) {
PlatformImpl.setDefaultPlatformUserAgentStylesheet();
} else {
PlatformImpl.setPlatformUserAgentStylesheet(url);
}
}
}
