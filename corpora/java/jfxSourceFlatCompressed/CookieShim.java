package com.sun.webkit.network;
import java.net.URI;
public class CookieShim {
Cookie cookie;
private CookieShim(Cookie cookie) {
this.cookie = cookie;
}
public static CookieShim parse(String setCookieString, ExtendedTimeShim currentTime) {
Cookie c = Cookie.parse(setCookieString, currentTime.getExtendedTime());
if (c == null) {
return null;
}
return new CookieShim(c);
}
public String getName() {
return cookie.getName();
}
public String getValue() {
return cookie.getValue();
}
public long getExpiryTime() {
return cookie.getExpiryTime();
}
public String getDomain() {
return cookie.getDomain();
}
public String getPath() {
return cookie.getPath();
}
public ExtendedTimeShim getCreationTime() {
return new ExtendedTimeShim(cookie.getCreationTime());
}
public long getLastAccessTime() {
return cookie.getLastAccessTime();
}
public boolean getPersistent() {
return cookie.getPersistent();
}
public boolean getHostOnly() {
return cookie.getHostOnly();
}
public boolean getSecureOnly() {
return cookie.getSecureOnly();
}
public boolean getHttpOnly() {
return cookie.getHttpOnly();
}
public boolean hasExpired() {
return cookie.hasExpired();
}
public boolean equals(CookieShim cs) {
return cookie.equals(cs != null ? cs.cookie: null);
}
public int hashCode() {
return cookie.hashCode();
}
public static boolean domainMatches(String domain, String cookieDomain) {
return Cookie.domainMatches(domain, cookieDomain);
}
public static String defaultPath(URI uri) {
return Cookie.defaultPath(uri);
}
public static boolean pathMatches(String path, String cookiePath) {
return Cookie.pathMatches(path, cookiePath);
}
}
