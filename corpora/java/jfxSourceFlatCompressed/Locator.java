package com.sun.media.jfxmedia.locator;
import com.sun.media.jfxmedia.MediaException;
import com.sun.media.jfxmedia.MediaManager;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmediaimpl.HostUtils;
import com.sun.media.jfxmediaimpl.MediaUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
public class Locator {
public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
private static final int MAX_CONNECTION_ATTEMPTS = 5;
private static final long CONNECTION_RETRY_INTERVAL = 1000L;
private static final int CONNECTION_TIMEOUT = 300000;
protected String contentType = DEFAULT_CONTENT_TYPE;
protected long contentLength = -1;
protected URI uri;
private Map<String, Object> connectionProperties;
private final Object propertyLock = new Object();
private String uriString = null;
private String scheme = null;
private String protocol = null;
private LocatorCache.CacheReference cacheEntry = null;
private boolean canBlock = false;
private CountDownLatch readySignal = new CountDownLatch(1);
private boolean isIpod;
private static class LocatorConnection {
public HttpURLConnection connection = null;
public int responseCode = HttpURLConnection.HTTP_OK;
}
private LocatorConnection getConnection(URI uri, String requestMethod)
throws MalformedURLException, IOException {
LocatorConnection locatorConnection = new LocatorConnection();
HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
connection.setRequestMethod(requestMethod);
connection.setConnectTimeout(CONNECTION_TIMEOUT);
connection.setReadTimeout(CONNECTION_TIMEOUT);
synchronized (propertyLock) {
if (connectionProperties != null) {
for (String key : connectionProperties.keySet()) {
Object value = connectionProperties.get(key);
if (value instanceof String) {
connection.setRequestProperty(key, (String) value);
}
}
}
}
locatorConnection.responseCode = connection.getResponseCode();
if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
locatorConnection.connection = connection;
} else {
closeConnection(connection);
locatorConnection.connection = null;
}
return locatorConnection;
}
private static long getContentLengthLong(URLConnection connection) {
@SuppressWarnings("removal")
Method method = AccessController.doPrivileged((PrivilegedAction<Method>) () -> {
try {
return URLConnection.class.getMethod("getContentLengthLong");
} catch (NoSuchMethodException ex) {
return null;
}
});
try {
if (method != null) {
return (long) method.invoke(connection);
} else {
return connection.getContentLength();
}
} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
return -1;
}
}
public Locator(URI uri) throws URISyntaxException {
if (uri == null) {
throw new NullPointerException("uri == null!");
}
uriString = uri.toASCIIString();
scheme = uri.getScheme();
if (scheme == null) {
throw new IllegalArgumentException("uri.getScheme() == null! uri == '" + uri + "'");
}
scheme = scheme.toLowerCase();
if (scheme.equals("jar")) {
URI subURI = new URI(uriString.substring(4));
protocol = subURI.getScheme();
if (protocol == null) {
throw new IllegalArgumentException("uri.getScheme() == null! subURI == '" + subURI + "'");
}
protocol = protocol.toLowerCase();
} else {
protocol = scheme;
}
if (HostUtils.isIOS() && protocol.equals("ipod-library")) {
isIpod = true;
}
if (!isIpod && !MediaManager.canPlayProtocol(protocol)) {
throw new UnsupportedOperationException("Unsupported protocol \"" + protocol + "\"");
}
if (protocol.equals("http") || protocol.equals("https")) {
canBlock = true;
}
this.uri = uri;
}
private InputStream getInputStream(URI uri)
throws MalformedURLException, IOException {
URL url = uri.toURL();
URLConnection connection = url.openConnection();
synchronized (propertyLock) {
if (connectionProperties != null) {
for (String key : connectionProperties.keySet()) {
Object value = connectionProperties.get(key);
if (value instanceof String) {
connection.setRequestProperty(key, (String) value);
}
}
}
}
InputStream inputStream = url.openStream();
contentLength = getContentLengthLong(connection);
return inputStream;
}
public void cacheMedia() {
LocatorCache.CacheReference ref = LocatorCache.locatorCache().fetchURICache(uri);
if (null == ref) {
ByteBuffer cacheBuffer;
InputStream is;
try {
is = getInputStream(uri);
} catch (Throwable t) {
return;
}
cacheBuffer = ByteBuffer.allocateDirect((int) contentLength);
byte[] readBuf = new byte[8192];
int total = 0;
int count;
while (total < contentLength) {
try {
count = is.read(readBuf);
} catch (IOException ioe) {
try {
is.close();
} catch (Throwable t) {
}
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "IOException trying to preload media: " + ioe);
}
return;
}
if (count == -1) {
break;
}
cacheBuffer.put(readBuf, 0, count);
}
try {
is.close();
} catch (Throwable t) {
}
cacheEntry = LocatorCache.locatorCache().registerURICache(uri, cacheBuffer, contentType);
canBlock = false;
}
}
public boolean canBlock() {
return canBlock;
}
public void init() throws URISyntaxException, IOException, FileNotFoundException {
try {
int firstSlash = uriString.indexOf("/");
if (firstSlash != -1 && uriString.charAt(firstSlash + 1) != '/') {
if (protocol.equals("file")) {
uriString = uriString.replaceFirst("/", "///");
} else if (protocol.equals("http") || protocol.equals("https")) {
uriString = uriString.replaceFirst("/", "//");
}
}
if (System.getProperty("os.name").toLowerCase().indexOf("win") == -1
&& protocol.equals("file")) {
int index = uriString.indexOf("/~/");
if (index != -1) {
uriString = uriString.substring(0, index)
+ System.getProperty("user.home")
+ uriString.substring(index + 2);
}
}
uri = new URI(uriString);
cacheEntry = LocatorCache.locatorCache().fetchURICache(uri);
if (null != cacheEntry) {
contentType = cacheEntry.getMIMEType();
contentLength = cacheEntry.getBuffer().capacity();
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "Locator init cache hit:"
+ "\n    uri " + uri
+ "\n    type " + contentType
+ "\n    length " + contentLength);
}
return;
}
boolean isConnected = false;
boolean isMediaUnAvailable = false;
boolean isMediaSupported = true;
if (!isIpod) {
for (int numConnectionAttempts = 0; numConnectionAttempts < MAX_CONNECTION_ATTEMPTS; numConnectionAttempts++) {
try {
if (scheme.equals("http") || scheme.equals("https")) {
LocatorConnection locatorConnection = getConnection(uri, "HEAD");
if (locatorConnection == null || locatorConnection.connection == null) {
locatorConnection = getConnection(uri, "GET");
}
if (locatorConnection != null && locatorConnection.connection != null) {
isConnected = true;
contentType = locatorConnection.connection.getContentType();
contentLength = getContentLengthLong(locatorConnection.connection);
closeConnection(locatorConnection.connection);
locatorConnection.connection = null;
} else if (locatorConnection != null) {
if (locatorConnection.responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
isMediaUnAvailable = true;
}
}
} else if (scheme.equals("file") || scheme.equals("jar") || scheme.equals("jrt") || (scheme.equals("resource")) ) {
InputStream stream = getInputStream(uri);
stream.close();
isConnected = true;
contentType = MediaUtils.filenameToContentType(uri.getPath());
}
if (isConnected) {
if (MediaUtils.CONTENT_TYPE_WAV.equals(contentType)) {
contentType = getContentTypeFromFileSignature(uri);
if (!MediaManager.canPlayContentType(contentType)) {
isMediaSupported = false;
}
} else {
if (contentType == null || !MediaManager.canPlayContentType(contentType)) {
contentType = MediaUtils.filenameToContentType(uri.getPath());
if (Locator.DEFAULT_CONTENT_TYPE.equals(contentType)) {
contentType = getContentTypeFromFileSignature(uri);
}
if (!MediaManager.canPlayContentType(contentType)) {
isMediaSupported = false;
}
}
}
break;
}
} catch (IOException ioe) {
if (numConnectionAttempts + 1 >= MAX_CONNECTION_ATTEMPTS) {
throw ioe;
}
}
try {
Thread.sleep(CONNECTION_RETRY_INTERVAL);
} catch (InterruptedException ie) {
}
}
}
else {
contentType = MediaUtils.filenameToContentType(uri.getPath());
}
if (Logger.canLog(Logger.WARNING)) {
if (contentType.equals(MediaUtils.CONTENT_TYPE_FLV)) {
Logger.logMsg(Logger.WARNING, "Support for FLV container and VP6 video is removed.");
throw new MediaException("media type not supported (" + uri.toString() + ")");
} else if (contentType.equals(MediaUtils.CONTENT_TYPE_JFX)) {
Logger.logMsg(Logger.WARNING, "Support for FXM container and VP6 video is removed.");
throw new MediaException("media type not supported (" + uri.toString() + ")");
}
}
if (!isIpod && !isConnected) {
if (isMediaUnAvailable) {
throw new FileNotFoundException("media is unavailable (" + uri.toString() + ")");
} else {
throw new IOException("could not connect to media (" + uri.toString() + ")");
}
} else if (!isMediaSupported) {
throw new MediaException("media type not supported (" + uri.toString() + ")");
}
} catch (FileNotFoundException e) {
throw e;
} catch (IOException e) {
throw e;
} catch (MediaException e) {
throw e;
} finally {
readySignal.countDown();
}
}
public String getContentType() {
try {
readySignal.await();
} catch (Exception e) {
}
return contentType;
}
public String getProtocol() {
return protocol;
}
public long getContentLength() {
try {
readySignal.await();
} catch (Exception e) {
}
return contentLength;
}
public void waitForReadySignal() {
try {
readySignal.await();
} catch (Exception e) {
}
}
public URI getURI() {
return this.uri;
}
@Override
public String toString() {
if (LocatorCache.locatorCache().isCached(uri)) {
return "{LocatorURI uri: " + uri.toString() + " (media cached)}";
}
return "{LocatorURI uri: " + uri.toString() + "}";
}
public String getStringLocation() {
return uri.toString();
}
public void setConnectionProperty(String property, Object value) {
synchronized (propertyLock) {
if (connectionProperties == null) {
connectionProperties = new TreeMap<String, Object>();
}
connectionProperties.put(property, value);
}
}
public ConnectionHolder createConnectionHolder() throws IOException {
if (null != cacheEntry) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "Locator.createConnectionHolder: media cached, creating memory connection holder");
}
return ConnectionHolder.createMemoryConnectionHolder(cacheEntry.getBuffer());
}
if ("file".equals(scheme)) {
return ConnectionHolder.createFileConnectionHolder(uri);
}
String uriPath = uri.getPath();
if (uriPath != null && (uriPath.endsWith(".m3u8") ||
uriPath.endsWith(".m3u"))) {
return ConnectionHolder.createHLSConnectionHolder(uri);
}
String type = getContentType();
if (type != null && (type.equals(MediaUtils.CONTENT_TYPE_M3U8) ||
type.equals(MediaUtils.CONTENT_TYPE_M3U))) {
return ConnectionHolder.createHLSConnectionHolder(uri);
}
synchronized (propertyLock) {
return ConnectionHolder.createURIConnectionHolder(uri, connectionProperties);
}
}
private String getContentTypeFromFileSignature(URI uri) throws MalformedURLException, IOException {
InputStream stream = getInputStream(uri);
byte[] signature = new byte[MediaUtils.MAX_FILE_SIGNATURE_LENGTH];
int size = stream.read(signature);
stream.close();
return MediaUtils.fileSignatureToContentType(signature, size);
}
static void closeConnection(URLConnection connection) {
if (connection instanceof HttpURLConnection) {
HttpURLConnection httpConnection = (HttpURLConnection)connection;
try {
if (httpConnection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST &&
httpConnection.getInputStream() != null) {
httpConnection.getInputStream().close();
}
} catch (IOException ex) {
try {
if (httpConnection.getErrorStream() != null) {
httpConnection.getErrorStream().close();
}
} catch (IOException e) {}
}
}
}
}
