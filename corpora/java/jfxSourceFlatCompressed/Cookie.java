package com.sun.webkit.network;
import com.sun.javafx.logging.PlatformLogger;
import java.net.URI;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
final class Cookie {
private static final PlatformLogger logger =
PlatformLogger.getLogger(Cookie.class.getName());
private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile(
"(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
private final String name;
private final String value;
private final long expiryTime;
private String domain;
private String path;
private ExtendedTime creationTime;
private long lastAccessTime;
private final boolean persistent;
private boolean hostOnly;
private final boolean secureOnly;
private final boolean httpOnly;
private Cookie(String name, String value, long expiryTime, String domain,
String path, ExtendedTime creationTime, long lastAccessTime,
boolean persistent, boolean hostOnly, boolean secureOnly,
boolean httpOnly)
{
this.name = name;
this.value = value;
this.expiryTime = expiryTime;
this.domain = domain;
this.path = path;
this.creationTime = creationTime;
this.lastAccessTime = lastAccessTime;
this.persistent = persistent;
this.hostOnly = hostOnly;
this.secureOnly = secureOnly;
this.httpOnly = httpOnly;
}
static Cookie parse(String setCookieString, ExtendedTime currentTime) {
logger.finest("setCookieString: [{0}]", setCookieString);
String[] items = setCookieString.split(";", -1);
String[] nameValuePair = items[0].split("=", 2);
if (nameValuePair.length != 2) {
logger.finest("Name-value pair string lacks '=', "
+ "ignoring cookie");
return null;
}
String name = nameValuePair[0].trim();
String value = nameValuePair[1].trim();
if (name.length() == 0) {
logger.finest("Name string is empty, ignoring cookie");
return null;
}
Long expires = null;
Long maxAge = null;
String domain = null;
String path = null;
boolean secure = false;
boolean httpOnly = false;
for (int i = 1; i < items.length; i++) {
String[] terms = items[i].split("=", 2);
String attrName = terms[0].trim();
String attrValue = (terms.length > 1 ? terms[1] : "").trim();
try {
if ("Expires".equalsIgnoreCase(attrName)) {
expires = parseExpires(attrValue);
} else if ("Max-Age".equalsIgnoreCase(attrName)) {
maxAge = parseMaxAge(attrValue, currentTime.baseTime());
} else if ("Domain".equalsIgnoreCase(attrName)) {
domain = parseDomain(attrValue);
} else if ("Path".equalsIgnoreCase(attrName)) {
path = parsePath(attrValue);
} else if ("Secure".equalsIgnoreCase(attrName)) {
secure = true;
} else if ("HttpOnly".equalsIgnoreCase(attrName)) {
httpOnly = true;
} else {
logger.finest("Unknown attribute: [{0}], "
+ "ignoring", attrName);
}
} catch (ParseException ex) {
logger.finest("{0}, ignoring", ex.getMessage());
}
}
long expiryTime;
boolean persistent;
if (maxAge != null) {
persistent = true;
expiryTime = maxAge;
} else if (expires != null) {
persistent = true;
expiryTime = expires;
} else {
persistent = false;
expiryTime = Long.MAX_VALUE;
}
if (domain == null) {
domain = "";
}
Cookie result = new Cookie(name, value, expiryTime, domain, path,
currentTime, currentTime.baseTime(), persistent, false,
secure, httpOnly);
logger.finest("result: {0}", result);
return result;
}
private static long parseExpires(String attributeValue)
throws ParseException
{
try {
return Math.max(DateParser.parse(attributeValue), 0);
} catch (ParseException ex) {
throw new ParseException("Error parsing Expires attribute", 0);
}
}
private static long parseMaxAge(String attributeValue, long currentTime)
throws ParseException
{
try {
long maxAge = Long.parseLong(attributeValue);
if (maxAge <= 0) {
return 0;
} else {
return maxAge > (Long.MAX_VALUE - currentTime) / 1000
? Long.MAX_VALUE : currentTime + maxAge * 1000;
}
} catch (NumberFormatException ex) {
throw new ParseException("Error parsing Max-Age attribute", 0);
}
}
private static String parseDomain(String attributeValue)
throws ParseException
{
if (attributeValue.length() == 0) {
throw new ParseException("Domain attribute is empty", 0);
}
if (attributeValue.startsWith(".")) {
attributeValue = attributeValue.substring(1);
}
return attributeValue.toLowerCase();
}
private static String parsePath(String attributeValue) {
return attributeValue.startsWith("/") ? attributeValue : null;
}
String getName() {
return name;
}
String getValue() {
return value;
}
long getExpiryTime() {
return expiryTime;
}
String getDomain() {
return domain;
}
void setDomain(String domain) {
this.domain = domain;
}
String getPath() {
return path;
}
void setPath(String path) {
this.path = path;
}
ExtendedTime getCreationTime() {
return creationTime;
}
void setCreationTime(ExtendedTime creationTime) {
this.creationTime = creationTime;
}
long getLastAccessTime() {
return lastAccessTime;
}
void setLastAccessTime(long lastAccessTime) {
this.lastAccessTime = lastAccessTime;
}
boolean getPersistent() {
return persistent;
}
boolean getHostOnly() {
return hostOnly;
}
void setHostOnly(boolean hostOnly) {
this.hostOnly = hostOnly;
}
boolean getSecureOnly() {
return secureOnly;
}
boolean getHttpOnly() {
return httpOnly;
}
boolean hasExpired() {
return System.currentTimeMillis() > expiryTime;
}
@Override
public boolean equals(Object obj) {
if (obj instanceof Cookie) {
Cookie cookie = (Cookie) obj;
return equal(name, cookie.name)
&& equal(domain, cookie.domain)
&& equal(path, cookie.path);
} else {
return false;
}
}
private static boolean equal(Object obj1, Object obj2) {
return (obj1 == null && obj2 == null)
|| (obj1 != null && obj1.equals(obj2));
}
@Override
public int hashCode() {
int hashCode = 7;
hashCode = 53 * hashCode + hashCode(name);
hashCode = 53 * hashCode + hashCode(domain);
hashCode = 53 * hashCode + hashCode(path);
return hashCode;
}
private static int hashCode(Object obj) {
return obj != null ? obj.hashCode() : 0;
}
@Override
public String toString() {
return "[name=" + name + ", value=" + value + ", "
+ "expiryTime=" + expiryTime + ", domain=" + domain + ", "
+ "path=" + path + ", creationTime=" + creationTime + ", "
+ "lastAccessTime=" + lastAccessTime + ", "
+ "persistent=" + persistent + ", hostOnly=" + hostOnly + ", "
+ "secureOnly=" + secureOnly + ", httpOnly=" + httpOnly + "]";
}
static boolean domainMatches(String domain, String cookieDomain) {
return domain.endsWith(cookieDomain) && (
domain.length() == cookieDomain.length()
|| domain.charAt(domain.length()
- cookieDomain.length() - 1) == '.'
&& !isIpAddress(domain));
}
private static boolean isIpAddress(String hostname) {
Matcher matcher = IP_ADDRESS_PATTERN.matcher(hostname);
if (!matcher.matches()) {
return false;
}
for (int i = 1; i <= matcher.groupCount(); i++) {
if (Integer.parseInt(matcher.group(i)) > 255) {
return false;
}
}
return true;
}
static String defaultPath(URI uri) {
String path = uri.getPath();
if (path == null || !path.startsWith("/")) {
return "/";
}
path = path.substring(0, path.lastIndexOf("/"));
if (path.length() == 0) {
return "/";
}
return path;
}
static boolean pathMatches(String path, String cookiePath) {
return path != null && path.startsWith(cookiePath) && (
path.length() == cookiePath.length()
|| cookiePath.endsWith("/")
|| path.charAt(cookiePath.length()) == '/');
}
}
