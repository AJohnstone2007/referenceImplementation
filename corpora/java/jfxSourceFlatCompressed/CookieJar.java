package com.sun.webkit.network;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
final class CookieJar {
private CookieJar() {
}
private static void fwkPut(String url, String cookie) {
@SuppressWarnings("removal")
CookieHandler handler =
AccessController.doPrivileged((PrivilegedAction<CookieHandler>) CookieHandler::getDefault);
if (handler != null) {
URI uri = null;
try {
uri = new URI(url);
uri = rewriteToFilterOutHttpOnlyCookies(uri);
} catch (URISyntaxException e) {
return;
}
Map<String, List<String>> headers = new HashMap<String, List<String>>();
List<String> val = new ArrayList<String>();
val.add(cookie);
headers.put("Set-Cookie", val);
try {
handler.put(uri, headers);
} catch (IOException e) {
}
}
}
private static String fwkGet(String url, boolean includeHttpOnlyCookies) {
@SuppressWarnings("removal")
CookieHandler handler =
AccessController.doPrivileged((PrivilegedAction<CookieHandler>) CookieHandler::getDefault);
if (handler != null) {
URI uri = null;
try {
uri = new URI(url);
if (!includeHttpOnlyCookies) {
uri = rewriteToFilterOutHttpOnlyCookies(uri);
}
} catch (URISyntaxException e) {
return null;
}
Map<String, List<String>> headers = new HashMap<String, List<String>>();
Map<String, List<String>> val = null;
try {
val = handler.get(uri, headers);
} catch (IOException e) {
return null;
}
if (val != null) {
StringBuilder sb = new StringBuilder();
for (Map.Entry<String, List<String>> entry: val.entrySet()) {
String key = entry.getKey();
if ("Cookie".equalsIgnoreCase(key)) {
for (String s : entry.getValue()) {
if (sb.length() > 0) {
sb.append("; ");
}
sb.append(s);
}
}
}
return sb.toString();
}
}
return null;
}
private static URI rewriteToFilterOutHttpOnlyCookies(URI uri)
throws URISyntaxException
{
return new URI(
uri.getScheme().equalsIgnoreCase("https")
? "javascripts" : "javascript",
uri.getRawSchemeSpecificPart(),
uri.getRawFragment());
}
}
