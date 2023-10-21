package com.sun.webkit.network;
import static com.sun.webkit.network.URLs.newURL;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.webkit.WebPage;
import java.security.Permission;
final class NetworkContext {
private static final PlatformLogger logger =
PlatformLogger.getLogger(NetworkContext.class.getName());
private static final int THREAD_POOL_SIZE = 20;
private static final long THREAD_POOL_KEEP_ALIVE_TIME = 10000L;
private static final int DEFAULT_HTTP_MAX_CONNECTIONS = 5;
private static final int DEFAULT_HTTP2_MAX_CONNECTIONS = 20;
private static final int BYTE_BUFFER_SIZE = 1024 * 40;
private static final ThreadPoolExecutor threadPool;
private static final boolean useHTTP2Loader;
static {
threadPool = new ThreadPoolExecutor(
THREAD_POOL_SIZE,
THREAD_POOL_SIZE,
THREAD_POOL_KEEP_ALIVE_TIME,
TimeUnit.MILLISECONDS,
new LinkedBlockingQueue<Runnable>(),
new URLLoaderThreadFactory());
threadPool.allowCoreThreadTimeOut(true);
@SuppressWarnings("removal")
boolean tmp = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
final var version = Runtime.Version.parse(System.getProperty("java.version"));
final String defaultUseHTTP2 = version.feature() >= 12 ? "true" : "false";
return Boolean.valueOf(System.getProperty("com.sun.webkit.useHTTP2Loader", defaultUseHTTP2));
});
useHTTP2Loader = tmp;
}
private static final ByteBufferPool byteBufferPool =
ByteBufferPool.newInstance(BYTE_BUFFER_SIZE);
private NetworkContext() {
throw new AssertionError();
}
private static boolean canHandleURL(String url) {
java.net.URL u = null;
try {
u = newURL(url);
} catch (MalformedURLException malformedURLException) {
}
return u != null;
}
private static URLLoaderBase fwkLoad(WebPage webPage,
boolean asynchronous,
String url,
String method,
String headers,
FormDataElement[] formDataElements,
long data)
{
if (logger.isLoggable(Level.FINEST)) {
logger.finest(String.format(
"webPage: [%s], " +
"asynchronous: [%s], " +
"url: [%s], " +
"method: [%s], " +
"formDataElements: %s, " +
"data: [0x%016X], " +
"headers:%n%s",
webPage,
asynchronous,
url,
method,
formDataElements != null
? Arrays.asList(formDataElements) : "[null]",
data,
Util.formatHeaders(headers)));
}
if (useHTTP2Loader) {
final URLLoaderBase loader = HTTP2Loader.create(
webPage,
byteBufferPool,
asynchronous,
url,
method,
headers,
formDataElements,
data);
if (loader != null) {
return loader;
}
}
URLLoader loader = new URLLoader(
webPage,
byteBufferPool,
asynchronous,
url,
method,
headers,
formDataElements,
data);
if (asynchronous) {
threadPool.submit(loader);
if (logger.isLoggable(Level.FINEST)) {
logger.finest(
"active count: [{0}], " +
"pool size: [{1}], " +
"max pool size: [{2}], " +
"task count: [{3}], " +
"completed task count: [{4}]",
new Object[] {
threadPool.getActiveCount(),
threadPool.getPoolSize(),
threadPool.getMaximumPoolSize(),
threadPool.getTaskCount(),
threadPool.getCompletedTaskCount()});
}
return loader;
} else {
loader.run();
return null;
}
}
private static int fwkGetMaximumHTTPConnectionCountPerHost() {
@SuppressWarnings("removal")
int propValue = AccessController.doPrivileged(
(PrivilegedAction<Integer>) () -> Integer.getInteger("http.maxConnections", -1));
if (useHTTP2Loader) {
return propValue >= 0 ? propValue : DEFAULT_HTTP2_MAX_CONNECTIONS;
}
return propValue >= 0 ? propValue : DEFAULT_HTTP_MAX_CONNECTIONS;
}
private static final class URLLoaderThreadFactory implements ThreadFactory {
private final ThreadGroup group;
private final AtomicInteger index = new AtomicInteger(1);
private static final Permission modifyThreadGroupPerm = new RuntimePermission("modifyThreadGroup");
private static final Permission modifyThreadPerm = new RuntimePermission("modifyThread");
private URLLoaderThreadFactory() {
@SuppressWarnings("removal")
SecurityManager sm = System.getSecurityManager();
group = (sm != null) ? sm.getThreadGroup()
: Thread.currentThread().getThreadGroup();
}
@SuppressWarnings("removal")
@Override
public Thread newThread(Runnable r) {
return
AccessController.doPrivileged((PrivilegedAction<Thread>) () -> {
Thread t = new Thread(group, r,
"URL-Loader-" + index.getAndIncrement());
t.setDaemon(true);
if (t.getPriority() != Thread.NORM_PRIORITY) {
t.setPriority(Thread.NORM_PRIORITY);
}
return t;
},
null,
modifyThreadGroupPerm, modifyThreadPerm);
}
}
}
