package javafx.css.converter;
import javafx.application.Application;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.util.DataURI;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
public final class URLConverter extends StyleConverter<ParsedValue[], String> {
private static class Holder {
static final URLConverter INSTANCE = new URLConverter();
static final SequenceConverter SEQUENCE_INSTANCE = new SequenceConverter();
}
public static StyleConverter<ParsedValue[], String> getInstance() {
return Holder.INSTANCE;
}
private URLConverter() {
super();
}
@Override
public String convert(ParsedValue<ParsedValue[], String> value, Font font) {
String url = null;
ParsedValue[] values = value.getValue();
String resource = values.length > 0 ? StringConverter.getInstance().convert(values[0], font) : null;
resource = resource != null ? resource.trim() : null;
if (resource != null && !resource.isEmpty()) {
if (resource.startsWith("url(")) {
resource = com.sun.javafx.util.Utils.stripQuotes(resource.substring(4, resource.length() - 1));
} else {
resource = com.sun.javafx.util.Utils.stripQuotes(resource);
}
if (DataURI.matchScheme(resource)) {
url = resource;
} else if (!resource.isEmpty()) {
String stylesheetURL = values.length > 1 && values[1] != null ? (String) values[1].getValue() : null;
URL resolvedURL = resolve(stylesheetURL, resource);
if (resolvedURL != null) url = resolvedURL.toExternalForm();
}
}
return url;
}
private URL resolve(String stylesheetUrl, String resource) {
try {
URI resourceUri = new URI(resource);
if (resourceUri.isAbsolute()) {
return resourceUri.toURL();
}
URL rtJarUrl = resolveRuntimeImport(resourceUri);
if (rtJarUrl != null) {
return rtJarUrl;
}
final String path = resourceUri.getPath();
if (path.startsWith("/")) {
final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
return contextClassLoader.getResource(path.substring(1));
}
final String stylesheetPath = (stylesheetUrl != null) ? stylesheetUrl.trim() : null;
if (stylesheetPath != null && stylesheetPath.isEmpty() == false) {
URI stylesheetUri = new URI(stylesheetPath);
if (stylesheetUri.isOpaque() == false) {
URI resolved = stylesheetUri.resolve(resourceUri);
return resolved.toURL();
} else {
URL url = stylesheetUri.toURL();
return new URL(url, resourceUri.getPath());
}
}
final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
return contextClassLoader.getResource(path);
} catch (final MalformedURLException|URISyntaxException e) {
PlatformLogger cssLogger = com.sun.javafx.util.Logging.getCSSLogger();
if (cssLogger.isLoggable(PlatformLogger.Level.WARNING)) {
cssLogger.warning(e.getLocalizedMessage());
}
return null;
}
}
private URL resolveRuntimeImport(final URI resourceUri) {
final String path = resourceUri.getPath();
final String resourcePath = path.startsWith("/") ? path.substring(1) : path;
if ((resourcePath.startsWith("com/sun/javafx/scene/control/skin/modena/") ||
resourcePath.startsWith("com/sun/javafx/scene/control/skin/caspian/")) &&
(resourcePath.endsWith(".css") || resourcePath.endsWith(".bss"))) {
System.err.println("WARNING: resolveRuntimeImport cannot resolve: " + resourcePath);
@SuppressWarnings("removal")
final SecurityManager sm = System.getSecurityManager();
if (sm == null) {
final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
final URL resolved = contextClassLoader.getResource(resourcePath);
return resolved;
}
try {
@SuppressWarnings("removal")
final URL rtJarURL = AccessController.doPrivileged((PrivilegedExceptionAction<URL>) () -> {
final ProtectionDomain protectionDomain = Application.class.getProtectionDomain();
final CodeSource codeSource = protectionDomain.getCodeSource();
return codeSource.getLocation();
});
final URI rtJarURI = rtJarURL.toURI();
String scheme = rtJarURI.getScheme();
String rtJarPath = rtJarURI.getPath();
if ("file".equals(scheme) && rtJarPath.endsWith(".jar")) {
if ("file".equals(scheme)) {
scheme = "jar:file";
rtJarPath = rtJarPath.concat("!/");
}
}
rtJarPath = rtJarPath.concat(resourcePath);
final String rtJarUserInfo = rtJarURI.getUserInfo();
final String rtJarHost = rtJarURI.getHost();
final int rtJarPort = rtJarURI.getPort();
URI resolved = new URI(scheme, rtJarUserInfo, rtJarHost, rtJarPort, rtJarPath, null, null);
return resolved.toURL();
} catch (URISyntaxException | MalformedURLException | PrivilegedActionException ignored) {
}
}
return null;
}
@Override
public String toString() {
return "URLType";
}
public static final class SequenceConverter extends StyleConverter<ParsedValue<ParsedValue[], String>[], String[]> {
public static SequenceConverter getInstance() {
return Holder.SEQUENCE_INSTANCE;
}
private SequenceConverter() {
super();
}
@Override
public String[] convert(ParsedValue<ParsedValue<ParsedValue[], String>[], String[]> value, Font font) {
ParsedValue<ParsedValue[], String>[] layers = value.getValue();
String[] urls = new String[layers.length];
for (int layer = 0; layer < layers.length; layer++) {
urls[layer] = URLConverter.getInstance().convert(layers[layer], font);
}
return urls;
}
@Override
public String toString() {
return "URLSeqType";
}
}
}
