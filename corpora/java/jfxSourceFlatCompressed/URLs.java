package com.sun.webkit.network;
import java.net.MalformedURLException;
import java.net.NetPermission;
import java.net.URL;
import java.net.URLStreamHandler;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Map;
public final class URLs {
private static final Map<String,URLStreamHandler> HANDLER_MAP = Map.of(
"about", new com.sun.webkit.network.about.Handler(),
"data", new com.sun.webkit.network.data.Handler());
private static final Permission streamHandlerPermission =
new NetPermission("specifyStreamHandler");
private URLs() {
throw new AssertionError();
}
public static URL newURL(String spec) throws MalformedURLException {
return newURL(null, spec);
}
public static URL newURL(final URL context, final String spec)
throws MalformedURLException
{
try {
return new URL(context, spec);
} catch (MalformedURLException ex) {
int colonPosition = spec.indexOf(':');
final URLStreamHandler handler = (colonPosition != -1) ?
HANDLER_MAP.get(spec.substring(0, colonPosition).toLowerCase()) :
null;
if (handler == null) throw ex;
try {
@SuppressWarnings("removal")
URL result = AccessController.doPrivileged((PrivilegedAction<URL>) () -> {
try {
return new URL(context, spec, handler);
} catch (MalformedURLException muex) {
throw new RuntimeException(muex);
}
}, null, streamHandlerPermission);
return result;
} catch (RuntimeException re) {
if (re.getCause() instanceof MalformedURLException) {
throw (MalformedURLException)re.getCause();
}
throw re;
}
}
}
}
