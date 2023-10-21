package com.sun.javafx.application;
import java.io.File;
import java.net.URI;
import javafx.application.Application;
public abstract class HostServicesDelegate {
public static HostServicesDelegate getInstance(final Application app) {
return StandaloneHostService.getInstance(app);
}
protected HostServicesDelegate() {
}
public abstract String getCodeBase();
public abstract String getDocumentBase();
public abstract void showDocument(String uri);
private static class StandaloneHostService extends HostServicesDelegate {
private static HostServicesDelegate instance = null;
private Class appClass = null;
public static HostServicesDelegate getInstance(Application app) {
synchronized (StandaloneHostService.class) {
if (instance == null) {
instance = new StandaloneHostService(app);
}
return instance;
}
}
private StandaloneHostService(Application app) {
appClass = app.getClass();
}
@Override
public String getCodeBase() {
String theClassFile = appClass.getName();
int idx = theClassFile.lastIndexOf(".");
if (idx >= 0) {
theClassFile = theClassFile.substring(idx + 1);
}
theClassFile = theClassFile + ".class";
String classUrlString = appClass.getResource(theClassFile).toString();
if (!classUrlString.startsWith("jar:file:") ||
classUrlString.indexOf("!") == -1) {
return "";
}
String urlString = classUrlString.substring(4,
classUrlString.lastIndexOf("!"));
File jarFile = null;
try {
jarFile = new File(new URI(urlString).getPath());
} catch (Exception e) {
}
if (jarFile != null) {
String codebase = jarFile.getParent();
if (codebase != null) {
return toURIString(codebase);
}
}
return "";
}
private String toURIString(String filePath) {
try {
return new File(filePath).toURI().toString();
} catch (Exception e) {
e.printStackTrace();
}
return "";
}
@Override public String getDocumentBase() {
return toURIString(System.getProperty("user.dir"));
}
static final String[] browsers = {
"xdg-open",
"google-chrome",
"firefox",
"opera",
"konqueror",
"mozilla"
};
@Override
public void showDocument(final String uri) {
String osName = System.getProperty("os.name");
try {
if (osName.startsWith("Mac OS")) {
Runtime.getRuntime().exec(
"open " + uri);
} else if (osName.startsWith("Windows")) {
Runtime.getRuntime().exec(
"rundll32 url.dll,FileProtocolHandler " + uri);
} else {
String browser = null;
for (String b : browsers) {
if (browser == null && Runtime.getRuntime().exec(
new String[]{"which", b}).getInputStream().read() != -1) {
Runtime.getRuntime().exec(new String[]{browser = b, uri});
}
}
if (browser == null) {
throw new Exception("No web browser found");
}
}
} catch (Exception e) {
e.printStackTrace();
}
}
}
}
