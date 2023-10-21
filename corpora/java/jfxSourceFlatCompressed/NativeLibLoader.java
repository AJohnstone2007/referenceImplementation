package com.sun.glass.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
public class NativeLibLoader {
private static final HashSet<String> loaded = new HashSet<String>();
public static synchronized void loadLibrary(String libname) {
if (!loaded.contains(libname)) {
@SuppressWarnings("removal")
StackWalker walker = AccessController.doPrivileged((PrivilegedAction<StackWalker>) () ->
StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE));
Class caller = walker.getCallerClass();
loadLibraryInternal(libname, null, caller);
loaded.add(libname);
}
}
public static synchronized void loadLibrary(String libname, List<String> dependencies) {
if (!loaded.contains(libname)) {
@SuppressWarnings("removal")
StackWalker walker = AccessController.doPrivileged((PrivilegedAction<StackWalker>) () ->
StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE));
Class caller = walker.getCallerClass();
loadLibraryInternal(libname, dependencies, caller);
loaded.add(libname);
}
}
private static boolean verbose = false;
private static File libDir = null;
private static String libPrefix = "";
private static String libSuffix = "";
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
verbose = Boolean.getBoolean("javafx.verbose");
return null;
});
}
private static String[] initializePath(String propname) {
String ldpath = System.getProperty(propname, "");
String ps = File.pathSeparator;
int ldlen = ldpath.length();
int i, j, n;
i = ldpath.indexOf(ps);
n = 0;
while (i >= 0) {
n++;
i = ldpath.indexOf(ps, i + 1);
}
String[] paths = new String[n + 1];
n = i = 0;
j = ldpath.indexOf(ps);
while (j >= 0) {
if (j - i > 0) {
paths[n++] = ldpath.substring(i, j);
} else if (j - i == 0) {
paths[n++] = ".";
}
i = j + 1;
j = ldpath.indexOf(ps, i);
}
paths[n] = ldpath.substring(i, ldlen);
return paths;
}
private static void loadLibraryInternal(String libraryName, List<String> dependencies, Class caller) {
try {
loadLibraryFullPath(libraryName);
} catch (UnsatisfiedLinkError ex) {
if (verbose) {
System.err.println("WARNING: " + ex);
}
if (loadLibraryFromResource(libraryName, dependencies, caller)) {
return;
}
String [] libPath = initializePath("java.library.path");
for (int i=0; i<libPath.length; i++) {
try {
String path = libPath[i];
if (!path.endsWith(File.separator)) path += File.separator;
String fileName = System.mapLibraryName(libraryName);
File libFile = new File(path + fileName);
System.load(libFile.getAbsolutePath());
if (verbose) {
System.err.println("Loaded " + libFile.getAbsolutePath()
+ " from java.library.path");
}
return;
} catch (UnsatisfiedLinkError ex3) {
}
}
try {
System.loadLibrary(libraryName);
if (verbose) {
System.err.println("System.loadLibrary("
+ libraryName + ") succeeded");
}
} catch (UnsatisfiedLinkError ex2) {
if ("ios".equals(System.getProperty("os.name").toLowerCase(Locale.ROOT))
&& libraryName.contains("-")) {
libraryName = libraryName.replace("-", "_");
try {
System.loadLibrary(libraryName);
return;
} catch (UnsatisfiedLinkError ex3) {
throw ex3;
}
}
throw ex2;
}
}
}
private static boolean loadLibraryFromResource(String libraryName, List<String> dependencies, Class caller) {
return installLibraryFromResource(libraryName, dependencies, caller, true);
}
private static boolean installLibraryFromResource(String libraryName, List<String> dependencies, Class caller, boolean load) {
try {
if (dependencies != null) {
for (String dep: dependencies) {
boolean hasdep = installLibraryFromResource(dep, null, caller, false);
}
}
String reallib = "/"+System.mapLibraryName(libraryName);
InputStream is = caller.getResourceAsStream(reallib);
if (is != null) {
String fp = cacheLibrary(is, reallib, caller);
if (load) {
System.load(fp);
if (verbose) {
System.err.println("Loaded library " + reallib + " from resource");
}
} else if (verbose) {
System.err.println("Unpacked library " + reallib + " from resource");
}
return true;
}
} catch (Throwable t) {
System.err.println("Loading library " + libraryName + " from resource failed: " + t);
t.printStackTrace();
}
return false;
}
private static String cacheLibrary(InputStream is, String name, Class caller) throws IOException {
String jfxVersion = System.getProperty("javafx.runtime.version", "versionless");
String userCache = System.getProperty("javafx.cachedir", "");
String arch = System.getProperty("os.arch");
if (userCache.isEmpty()) {
userCache = System.getProperty("user.home") + "/.openjfx/cache/" + jfxVersion + "/" + arch;
}
File cacheDir = new File(userCache);
boolean cacheDirOk = true;
if (cacheDir.exists()) {
if (!cacheDir.isDirectory()) {
System.err.println("Cache exists but is not a directory: "+cacheDir);
cacheDirOk = false;
}
} else {
if (!cacheDir.mkdirs()) {
System.err.println("Can not create cache at "+cacheDir);
cacheDirOk = false;
}
}
if (!cacheDir.canRead()) {
cacheDirOk = false;
}
if (!cacheDirOk) {
String username = System.getProperty("user.name", "anonymous");
String tmpCache = System.getProperty("java.io.tmpdir") + "/.openjfx_" + username
+ "/cache/" + jfxVersion + "/" + arch;
cacheDir = new File(tmpCache);
if (cacheDir.exists()) {
if (!cacheDir.isDirectory()) {
throw new IOException("Cache exists but is not a directory: "+cacheDir);
}
} else {
if (!cacheDir.mkdirs()) {
throw new IOException("Can not create cache at "+cacheDir);
}
}
}
File f = new File(cacheDir, name);
boolean write = true;
if (f.exists()) {
byte[] isHash;
byte[] fileHash;
try {
DigestInputStream dis = new DigestInputStream(is, MessageDigest.getInstance("MD5"));
dis.getMessageDigest().reset();
byte[] buffer = new byte[4096];
while (dis.read(buffer) != -1) { }
isHash = dis.getMessageDigest().digest();
is.close();
is = caller.getResourceAsStream(name);
}
catch (NoSuchAlgorithmException nsa) {
isHash = new byte[1];
}
fileHash = calculateCheckSum(f);
if (!Arrays.equals(isHash, fileHash)) {
Files.delete(f.toPath());
} else {
write = false;
}
}
if (write) {
Path path = f.toPath();
Files.copy(is, path);
}
String fp = f.getAbsolutePath();
return fp;
}
static byte[] calculateCheckSum(File file) {
try {
try (final InputStream stream = new FileInputStream(file);
final DigestInputStream dis = new DigestInputStream(stream, MessageDigest.getInstance("MD5")); ) {
dis.getMessageDigest().reset();
byte[] buffer = new byte[4096];
while (dis.read(buffer) != -1) { }
return dis.getMessageDigest().digest();
}
} catch (IllegalArgumentException | NoSuchAlgorithmException | IOException | SecurityException e) {
}
return new byte[0];
}
private static File libDirForJRT() {
String javaHome = System.getProperty("java.home");
if (javaHome == null || javaHome.isEmpty()) {
throw new UnsatisfiedLinkError("Cannot find java.home");
}
String osName = System.getProperty("os.name");
String relativeDir = null;
if (osName.startsWith("Windows")) {
relativeDir = "bin/javafx";
} else if (osName.startsWith("Mac")) {
relativeDir = "lib";
} else if (osName.startsWith("Linux")) {
relativeDir = "lib";
}
return new File(javaHome + "/" + relativeDir);
}
private static File libDirForJarFile(String classUrlString) throws Exception {
String tmpStr = classUrlString.substring(4, classUrlString.lastIndexOf('!'));
int lastIndexOfSlash = Math.max(tmpStr.lastIndexOf('/'), tmpStr.lastIndexOf('\\'));
String osName = System.getProperty("os.name");
String relativeDir = null;
if (osName.startsWith("Windows")) {
relativeDir = "../bin";
} else if (osName.startsWith("Mac")) {
relativeDir = ".";
} else if (osName.startsWith("Linux")) {
relativeDir = ".";
}
String libDirUrlString = tmpStr.substring(0, lastIndexOfSlash)
+ "/" + relativeDir;
return new File(new URI(libDirUrlString).getPath());
}
private static void loadLibraryFullPath(String libraryName) {
try {
if (libDir == null) {
String theClassFile = "NativeLibLoader.class";
Class theClass = NativeLibLoader.class;
String classUrlString = theClass.getResource(theClassFile).toString();
if (classUrlString.startsWith("jrt:")) {
libDir = libDirForJRT();
} else if (classUrlString.startsWith("jar:file:") && classUrlString.indexOf('!') > 0) {
libDir = libDirForJarFile(classUrlString);
} else {
throw new UnsatisfiedLinkError("Invalid URL for class: " + classUrlString);
}
String osName = System.getProperty("os.name");
if (osName.startsWith("Windows")) {
libPrefix = "";
libSuffix = ".dll";
} else if (osName.startsWith("Mac")) {
libPrefix = "lib";
libSuffix = ".dylib";
} else if (osName.startsWith("Linux")) {
libPrefix = "lib";
libSuffix = ".so";
}
}
File libFile = new File(libDir, libPrefix + libraryName + libSuffix);
String libFileName = libFile.getCanonicalPath();
try {
System.load(libFileName);
if (verbose) {
System.err.println("Loaded " + libFile.getAbsolutePath()
+ " from relative path");
}
} catch(UnsatisfiedLinkError ex) {
throw ex;
}
} catch (Exception e) {
throw (UnsatisfiedLinkError) new UnsatisfiedLinkError().initCause(e);
}
}
}
