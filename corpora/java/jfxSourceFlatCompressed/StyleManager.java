package com.sun.javafx.css;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.util.DataURI;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.CssParser;
import javafx.css.FontFace;
import javafx.css.PseudoClass;
import javafx.css.Rule;
import javafx.css.Selector;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleConverter;
import javafx.css.Stylesheet;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Window;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
final public class StyleManager {
private static final Object styleLock = new Object();
private static PlatformLogger LOGGER;
private static PlatformLogger getLogger() {
if (LOGGER == null) {
LOGGER = com.sun.javafx.util.Logging.getCSSLogger();
}
return LOGGER;
}
private static class InstanceHolder {
final static StyleManager INSTANCE = new StyleManager();
}
public static StyleManager getInstance() {
return InstanceHolder.INSTANCE;
}
private StyleManager() {
}
public static final Map<Parent, CacheContainer> cacheContainerMap = new WeakHashMap<>();
CacheContainer getCacheContainer(Styleable styleable, SubScene subScene) {
if (styleable == null && subScene == null) return null;
Parent root = null;
if (subScene != null) {
root = subScene.getRoot();
} else if (styleable instanceof Node) {
Node node = (Node)styleable;
Scene scene = node.getScene();
if (scene != null) root = scene.getRoot();
} else if (styleable instanceof Window) {
Scene scene = ((Window)styleable).getScene();
if (scene != null) root = scene.getRoot();
}
if (root == null) return null;
synchronized (styleLock) {
CacheContainer container = cacheContainerMap.get(root);
if (container == null) {
container = new CacheContainer();
cacheContainerMap.put(root, container);
}
return container;
}
}
public StyleCache getSharedCache(Styleable styleable, SubScene subScene, StyleCache.Key key) {
CacheContainer container = getCacheContainer(styleable, subScene);
if (container == null) return null;
Map<StyleCache.Key,StyleCache> styleCache = container.getStyleCache();
if (styleCache == null) return null;
StyleCache sharedCache = styleCache.get(key);
if (sharedCache == null) {
sharedCache = new StyleCache();
styleCache.put(new StyleCache.Key(key), sharedCache);
}
return sharedCache;
}
public StyleMap getStyleMap(Styleable styleable, SubScene subScene, int smapId) {
if (smapId == -1) return StyleMap.EMPTY_MAP;
CacheContainer container = getCacheContainer(styleable, subScene);
if (container == null) return StyleMap.EMPTY_MAP;
return container.getStyleMap(smapId);
}
public final List<StylesheetContainer> userAgentStylesheetContainers = new ArrayList<>();
public final List<StylesheetContainer> platformUserAgentStylesheetContainers = new ArrayList<>();
public boolean hasDefaultUserAgentStylesheet = false;
static class StylesheetContainer {
final String fname;
final Stylesheet stylesheet;
final SelectorPartitioning selectorPartitioning;
final RefList<Parent> parentUsers;
final int hash;
final byte[] checksum;
boolean checksumInvalid = false;
StylesheetContainer(String fname, Stylesheet stylesheet) {
this(fname, stylesheet, stylesheet != null ? calculateCheckSum(stylesheet.getUrl()) : new byte[0]);
}
StylesheetContainer(String fname, Stylesheet stylesheet, byte[] checksum) {
this.fname = fname;
hash = (fname != null) ? fname.hashCode() : 127;
this.stylesheet = stylesheet;
if (stylesheet != null) {
selectorPartitioning = new SelectorPartitioning();
final List<Rule> rules = stylesheet.getRules();
final int rMax = rules == null || rules.isEmpty() ? 0 : rules.size();
for (int r=0; r<rMax; r++) {
final Rule rule = rules.get(r);
final List<Selector> selectors = rule.getSelectors();
final int sMax = selectors == null || selectors.isEmpty() ? 0 : selectors.size();
for (int s=0; s < sMax; s++) {
final Selector selector = selectors.get(s);
selectorPartitioning.partition(selector);
}
}
} else {
selectorPartitioning = null;
}
this.parentUsers = new RefList<Parent>();
this.checksum = checksum;
}
void invalidateChecksum() {
checksumInvalid = checksum.length > 0;
}
@Override
public int hashCode() {
return hash;
}
@Override
public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final StylesheetContainer other = (StylesheetContainer) obj;
if ((this.fname == null) ? (other.fname != null) : !this.fname.equals(other.fname)) {
return false;
}
return true;
}
@Override public String toString() {
return fname;
}
}
static class RefList<K> {
final List<Reference<K>> list = new ArrayList<Reference<K>>();
void add(K key) {
for (int n=list.size()-1; 0<=n; --n) {
final Reference<K> ref = list.get(n);
final K k = ref.get();
if (k == null) {
list.remove(n);
} else {
if (k == key) {
return;
}
}
}
list.add(new WeakReference<K>(key));
}
void remove(K key) {
for (int n=list.size()-1; 0<=n; --n) {
final Reference<K> ref = list.get(n);
final K k = ref.get();
if (k == null) {
list.remove(n);
} else {
if (k == key) {
list.remove(n);
return;
}
}
}
}
boolean contains(K key) {
for (int n=list.size()-1; 0<=n; --n) {
final Reference<K> ref = list.get(n);
final K k = ref.get();
if (k == key) {
return true;
}
}
return false;
}
}
public final Map<String,StylesheetContainer> stylesheetContainerMap = new HashMap<>();
public void forget(final Scene scene) {
if (scene == null) return;
forget(scene.getRoot());
synchronized (styleLock) {
String sceneUserAgentStylesheet = null;
if ((scene.getUserAgentStylesheet() != null) &&
(!(sceneUserAgentStylesheet = scene.getUserAgentStylesheet().trim()).isEmpty())) {
for(int n=userAgentStylesheetContainers.size()-1; 0<=n; --n) {
StylesheetContainer container = userAgentStylesheetContainers.get(n);
if (sceneUserAgentStylesheet.equals(container.fname)) {
container.parentUsers.remove(scene.getRoot());
if (container.parentUsers.list.size() == 0) {
userAgentStylesheetContainers.remove(n);
}
}
}
}
Set<Entry<String,StylesheetContainer>> stylesheetContainers = stylesheetContainerMap.entrySet();
Iterator<Entry<String,StylesheetContainer>> iter = stylesheetContainers.iterator();
while(iter.hasNext()) {
Entry<String,StylesheetContainer> entry = iter.next();
StylesheetContainer container = entry.getValue();
Iterator<Reference<Parent>> parentIter = container.parentUsers.list.iterator();
while (parentIter.hasNext()) {
Reference<Parent> ref = parentIter.next();
Parent _parent = ref.get();
if (_parent == null || _parent.getScene() == scene || _parent.getScene() == null) {
ref.clear();
parentIter.remove();
}
}
if (container.parentUsers.list.isEmpty()) {
iter.remove();
}
}
}
}
public void stylesheetsChanged(Scene scene, Change<String> c) {
synchronized (styleLock) {
Set<Entry<Parent,CacheContainer>> entrySet = cacheContainerMap.entrySet();
for(Entry<Parent,CacheContainer> entry : entrySet) {
Parent parent = entry.getKey();
CacheContainer container = entry.getValue();
if (parent.getScene() == scene) {
container.clearCache();
}
}
c.reset();
while(c.next()) {
if (c.wasRemoved()) {
for (String fname : c.getRemoved()) {
stylesheetRemoved(scene, fname);
StylesheetContainer stylesheetContainer = stylesheetContainerMap.get(fname);
if (stylesheetContainer != null) {
stylesheetContainer.invalidateChecksum();
}
}
}
}
}
}
private void stylesheetRemoved(Scene scene, String fname) {
stylesheetRemoved(scene.getRoot(), fname);
}
public void forget(Parent parent) {
if (parent == null) return;
synchronized (styleLock) {
CacheContainer removedContainer = cacheContainerMap.remove(parent);
if (removedContainer != null) {
removedContainer.clearCache();
}
final List<String> stylesheets = parent.getStylesheets();
if (stylesheets != null && !stylesheets.isEmpty()) {
for (String fname : stylesheets) {
stylesheetRemoved(parent, fname);
}
}
Iterator<Entry<String,StylesheetContainer>> containerIterator = stylesheetContainerMap.entrySet().iterator();
while (containerIterator.hasNext()) {
Entry<String,StylesheetContainer> entry = containerIterator.next();
StylesheetContainer container = entry.getValue();
container.parentUsers.remove(parent);
if (container.parentUsers.list.isEmpty()) {
containerIterator.remove();
if (container.selectorPartitioning != null) {
container.selectorPartitioning.reset();
}
final String fname = container.fname;
imageCache.cleanUpImageCache(fname);
}
}
}
}
public void stylesheetsChanged(Parent parent, Change<String> c) {
synchronized (styleLock) {
c.reset();
while(c.next()) {
if (c.wasRemoved()) {
for (String fname : c.getRemoved()) {
stylesheetRemoved(parent, fname);
StylesheetContainer stylesheetContainer = stylesheetContainerMap.get(fname);
if (stylesheetContainer != null) {
stylesheetContainer.invalidateChecksum();
}
}
}
}
}
}
private void stylesheetRemoved(Parent parent, String fname) {
synchronized (styleLock) {
StylesheetContainer stylesheetContainer = stylesheetContainerMap.get(fname);
if (stylesheetContainer == null) return;
stylesheetContainer.parentUsers.remove(parent);
if (stylesheetContainer.parentUsers.list.isEmpty()) {
removeStylesheetContainer(stylesheetContainer);
}
}
}
public void forget(final SubScene subScene) {
if (subScene == null) return;
final Parent subSceneRoot = subScene.getRoot();
if (subSceneRoot == null) return;
forget(subSceneRoot);
synchronized (styleLock) {
String sceneUserAgentStylesheet = null;
if ((subScene.getUserAgentStylesheet() != null) &&
(!(sceneUserAgentStylesheet = subScene.getUserAgentStylesheet().trim()).isEmpty())) {
Iterator<StylesheetContainer> iterator = userAgentStylesheetContainers.iterator();
while(iterator.hasNext()) {
StylesheetContainer container = iterator.next();
if (sceneUserAgentStylesheet.equals(container.fname)) {
container.parentUsers.remove(subScene.getRoot());
if (container.parentUsers.list.size() == 0) {
iterator.remove();
}
}
}
}
List<StylesheetContainer> stylesheetContainers = new ArrayList<>(stylesheetContainerMap.values());
Iterator<StylesheetContainer> iter = stylesheetContainers.iterator();
while(iter.hasNext()) {
StylesheetContainer container = iter.next();
Iterator<Reference<Parent>> parentIter = container.parentUsers.list.iterator();
while (parentIter.hasNext()) {
final Reference<Parent> ref = parentIter.next();
final Parent _parent = ref.get();
if (_parent != null) {
Parent p = _parent;
while (p != null) {
if (subSceneRoot == p.getParent()) {
ref.clear();
parentIter.remove();
forget(_parent);
break;
}
p = p.getParent();
}
}
}
}
}
}
private void removeStylesheetContainer(StylesheetContainer stylesheetContainer) {
if (stylesheetContainer == null) return;
synchronized (styleLock) {
final String fname = stylesheetContainer.fname;
stylesheetContainerMap.remove(fname);
if (stylesheetContainer.selectorPartitioning != null) {
stylesheetContainer.selectorPartitioning.reset();
}
for(Entry<Parent,CacheContainer> entry : cacheContainerMap.entrySet()) {
CacheContainer container = entry.getValue();
if (container == null || container.cacheMap == null || container.cacheMap.isEmpty()) {
continue;
}
List<List<String>> entriesToRemove = new ArrayList<>();
for (Entry<List<String>, Map<Key,Cache>> cacheMapEntry : container.cacheMap.entrySet()) {
List<String> cacheMapKey = cacheMapEntry.getKey();
if (cacheMapKey != null ? cacheMapKey.contains(fname) : fname == null) {
entriesToRemove.add(cacheMapKey);
}
}
if (!entriesToRemove.isEmpty()) {
for (List<String> cacheMapKey : entriesToRemove) {
Map<Key,Cache> cacheEntry = container.cacheMap.remove(cacheMapKey);
if (cacheEntry != null) {
cacheEntry.clear();
}
}
}
}
imageCache.cleanUpImageCache(fname);
final List<Reference<Parent>> parentList = stylesheetContainer.parentUsers.list;
for (int n=parentList.size()-1; 0<=n; --n) {
final Reference<Parent> ref = parentList.remove(n);
final Parent parent = ref.get();
ref.clear();
if (parent == null || parent.getScene() == null) {
continue;
}
NodeHelper.reapplyCSS(parent);
}
}
}
private final static class ImageCache {
private Map<String, SoftReference<Image>> imageCache = new HashMap<>();
Image getCachedImage(String url) {
synchronized (styleLock) {
Image image = null;
if (imageCache.containsKey(url)) {
image = imageCache.get(url).get();
}
if (image == null) {
try {
image = new Image(url);
if (image.isError()) {
final PlatformLogger logger = getLogger();
if (logger != null && logger.isLoggable(Level.WARNING)) {
DataURI dataUri = DataURI.tryParse(url);
if (dataUri != null) {
logger.warning("Error loading image: " + dataUri);
} else {
logger.warning("Error loading image: " + url);
}
}
image = null;
}
imageCache.put(url, new SoftReference<>(image));
} catch (IllegalArgumentException | NullPointerException ex) {
final PlatformLogger logger = getLogger();
if (logger != null && logger.isLoggable(Level.WARNING)) {
logger.warning(ex.getLocalizedMessage());
}
}
}
return image;
}
}
void cleanUpImageCache(String imgFname) {
synchronized (styleLock) {
if (imgFname == null || imageCache.isEmpty()) return;
final String fname = imgFname.trim();
if (fname.isEmpty()) return;
int len = fname.lastIndexOf('/');
final String path = (len > 0) ? fname.substring(0,len) : fname;
final int plen = path.length();
final String[] entriesToRemove = new String[imageCache.size()];
int count = 0;
final Set<Entry<String, SoftReference<Image>>> entrySet = imageCache.entrySet();
for (Entry<String, SoftReference<Image>> entry : entrySet) {
final String key = entry.getKey();
if (entry.getValue().get() == null) {
entriesToRemove[count++] = key;
continue;
}
len = key.lastIndexOf('/');
final String kpath = (len > 0) ? key.substring(0, len) : key;
final int klen = kpath.length();
boolean match = (klen > plen) ? kpath.startsWith(path) : path.startsWith(kpath);
if (match) {
entriesToRemove[count++] = key;
}
}
for (int n = 0; n < count; n++) {
imageCache.remove(entriesToRemove[n]);
}
}
}
}
private final ImageCache imageCache = new ImageCache();
public Image getCachedImage(String url) {
return imageCache.getCachedImage(url);
}
private static final String skinPrefix = "com/sun/javafx/scene/control/skin/";
private static final String skinUtilsClassName = "com.sun.javafx.scene.control.skin.Utils";
private static URL getURL(final String str) {
if (str == null || str.trim().isEmpty()) return null;
try {
URI uri = new URI(str.trim());
if (uri.isAbsolute() == false) {
if (str.startsWith(skinPrefix) &&
(str.endsWith(".css") || str.endsWith(".bss"))) {
try {
ClassLoader cl = StyleManager.class.getClassLoader();
Class<?> clz = Class.forName(skinUtilsClassName, true, cl);
Method m_getResource = clz.getMethod("getResource", String.class);
return (URL)m_getResource.invoke(null, str.substring(skinPrefix.length()));
} catch (ClassNotFoundException
| NoSuchMethodException
| IllegalAccessException
| InvocationTargetException ex) {
ex.printStackTrace();
return null;
}
}
final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
final String path = uri.getPath();
URL resource = null;
if (path.startsWith("/")) {
resource = contextClassLoader.getResource(path.substring(1));
} else {
resource = contextClassLoader.getResource(path);
}
return resource;
}
return uri.toURL();
} catch (MalformedURLException malf) {
return null;
} catch (URISyntaxException urise) {
return null;
}
}
static byte[] calculateCheckSum(String fname) {
if (fname == null || fname.isEmpty()) return new byte[0];
try {
final URL url = getURL(fname);
if (url != null && "file".equals(url.getProtocol())) {
try (final InputStream stream = url.openStream();
final DigestInputStream dis = new DigestInputStream(stream, MessageDigest.getInstance("MD5")); ) {
dis.getMessageDigest().reset();
byte[] buffer = new byte[4096];
while (dis.read(buffer) != -1) { }
return dis.getMessageDigest().digest();
}
}
} catch (IllegalArgumentException | NoSuchAlgorithmException | IOException | SecurityException e) {
}
return new byte[0];
}
@SuppressWarnings("removal")
public static Stylesheet loadStylesheet(final String fname) {
try {
return loadStylesheetUnPrivileged(fname);
} catch (java.security.AccessControlException ace) {
System.err.println("WARNING: security exception trying to load: " + fname);
if ((fname.length() < 7) && (fname.indexOf("!/") < fname.length()-7)) {
return null;
}
try {
URI requestedFileUrI = new URI(fname);
if ("jar".equals(requestedFileUrI.getScheme())) {
URI styleManagerJarURI = AccessController.doPrivileged((PrivilegedExceptionAction<URI>) () -> StyleManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
final String styleManagerJarPath = styleManagerJarURI.getSchemeSpecificPart();
String requestedFilePath = requestedFileUrI.getSchemeSpecificPart();
String requestedFileJarPart = requestedFilePath.substring(requestedFilePath.indexOf('/'), requestedFilePath.indexOf("!/"));
if (styleManagerJarPath.equals(requestedFileJarPart)) {
String requestedFileJarPathNoLeadingSlash = fname.substring(fname.indexOf("!/")+2);
if (fname.endsWith(".css") || fname.endsWith(".bss")) {
FilePermission perm = new FilePermission(styleManagerJarPath, "read");
PermissionCollection perms = perm.newPermissionCollection();
perms.add(perm);
AccessControlContext permsAcc = new AccessControlContext(
new ProtectionDomain[] {
new ProtectionDomain(null, perms)
});
JarFile jar = null;
try {
jar = AccessController.doPrivileged((PrivilegedExceptionAction<JarFile>) () -> new JarFile(styleManagerJarPath), permsAcc);
} catch (PrivilegedActionException pae) {
return null;
}
if (jar != null) {
JarEntry entry = jar.getJarEntry(requestedFileJarPathNoLeadingSlash);
if (entry != null) {
return AccessController.doPrivileged(
(PrivilegedAction<Stylesheet>) () -> loadStylesheetUnPrivileged(fname), permsAcc);
}
}
}
}
}
return null;
}
catch (java.net.URISyntaxException e) {
return null;
}
catch (java.security.PrivilegedActionException e) {
return null;
}
}
}
private static Stylesheet loadStylesheetUnPrivileged(final String fname) {
synchronized (styleLock) {
@SuppressWarnings("removal")
Boolean parse = AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> {
final String bss = System.getProperty("binary.css");
return (!fname.endsWith(".bss") && bss != null) ?
!Boolean.valueOf(bss) : Boolean.FALSE;
});
try {
final String ext = (parse) ? (".css") : (".bss");
java.net.URL url = null;
Stylesheet stylesheet = null;
if (!(fname.endsWith(".css") || fname.endsWith(".bss"))) {
url = getURL(fname);
} else {
final String name = fname.substring(0, fname.length() - 4);
url = getURL(name+ext);
if (url == null && (parse = !parse)) {
url = getURL(name+".css");
}
if ((url != null) && !parse) {
try {
stylesheet = Stylesheet.loadBinary(url);
} catch (IOException ignored) {
}
if (stylesheet == null) {
url = getURL(fname);
}
}
}
if (stylesheet == null) {
DataURI dataUri = null;
if (url != null) {
stylesheet = new CssParser().parse(url);
} else {
dataUri = DataURI.tryParse(fname);
}
if (dataUri != null) {
boolean isText =
"text".equalsIgnoreCase(dataUri.getMimeType())
&& ("css".equalsIgnoreCase(dataUri.getMimeSubtype())
|| "plain".equalsIgnoreCase(dataUri.getMimeSubtype()));
boolean isBinary =
"application".equalsIgnoreCase(dataUri.getMimeType())
&& "octet-stream".equalsIgnoreCase(dataUri.getMimeSubtype());
if (isText) {
String charsetName = dataUri.getParameters().get("charset");
Charset charset;
try {
charset = charsetName != null ? Charset.forName(charsetName) : Charset.defaultCharset();
} catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
String message = String.format(
"Unsupported charset \"%s\" in stylesheet URI \"%s\"", charsetName, dataUri);
if (errors != null) {
errors.add(new CssParser.ParseError(message));
}
if (getLogger().isLoggable(Level.WARNING)) {
getLogger().warning(message);
}
return null;
}
var stylesheetText = new String(dataUri.getData(), charset);
stylesheet = new CssParser().parse(stylesheetText);
} else if (isBinary) {
try (InputStream stream = new ByteArrayInputStream(dataUri.getData())) {
stylesheet = Stylesheet.loadBinary(stream);
}
} else {
String message = String.format("Unexpected MIME type \"%s/%s\" in stylesheet URI \"%s\"",
dataUri.getMimeType(), dataUri.getMimeSubtype(), dataUri);
if (errors != null) {
errors.add(new CssParser.ParseError(message));
}
if (getLogger().isLoggable(Level.WARNING)) {
getLogger().warning(message);
}
return null;
}
}
}
if (stylesheet == null) {
if (errors != null) {
CssParser.ParseError error =
new CssParser.ParseError(
"Resource \""+fname+"\" not found."
);
errors.add(error);
}
if (getLogger().isLoggable(Level.WARNING)) {
getLogger().warning(
String.format("Resource \"%s\" not found.", fname)
);
}
}
if (stylesheet != null) {
faceLoop: for(FontFace fontFace: stylesheet.getFontFaces()) {
if (fontFace instanceof FontFaceImpl) {
for(FontFaceImpl.FontFaceSrc src: ((FontFaceImpl)fontFace).getSources()) {
if (src.getType() == FontFaceImpl.FontFaceSrcType.URL) {
Font loadedFont = Font.loadFont(src.getSrc(),10);
if (loadedFont == null) {
getLogger().info("Could not load @font-face font [" + src.getSrc() + "]");
}
continue faceLoop;
}
}
}
}
}
return stylesheet;
} catch (FileNotFoundException fnfe) {
if (errors != null) {
CssParser.ParseError error =
new CssParser.ParseError(
"Stylesheet \""+fname+"\" not found."
);
errors.add(error);
}
if (getLogger().isLoggable(Level.INFO)) {
getLogger().info("Could not find stylesheet: " + fname);
}
} catch (IOException ioe) {
var dataUri = DataURI.tryParse(fname);
String stylesheetName = dataUri != null ? dataUri.toString() : fname;
if (errors != null) {
errors.add(new CssParser.ParseError("Could not load stylesheet: " + stylesheetName));
}
if (getLogger().isLoggable(Level.INFO)) {
getLogger().info("Could not load stylesheet: " + stylesheetName);
}
}
return null;
}
}
public void setUserAgentStylesheets(List<String> urls) {
if (urls == null || urls.size() == 0) return;
synchronized (styleLock) {
if (urls.size() == platformUserAgentStylesheetContainers.size()) {
boolean isSame = true;
for (int n=0, nMax=urls.size(); n < nMax && isSame; n++) {
final String url = urls.get(n);
final String fname = (url != null) ? url.trim() : null;
if (fname == null || fname.isEmpty()) break;
StylesheetContainer container = platformUserAgentStylesheetContainers.get(n);
if(isSame = fname.equals(container.fname)) {
String stylesheetUrl = container.stylesheet.getUrl();
byte[] checksum = calculateCheckSum(stylesheetUrl);
isSame = Arrays.equals(checksum, container.checksum);
}
}
if (isSame) return;
}
boolean modified = false;
for (int n=0, nMax=urls.size(); n < nMax; n++) {
final String url = urls.get(n);
final String fname = (url != null) ? url.trim() : null;
if (fname == null || fname.isEmpty()) continue;
if (!modified) {
platformUserAgentStylesheetContainers.clear();
modified = true;
}
if (n==0) {
_setDefaultUserAgentStylesheet(fname);
} else {
_addUserAgentStylesheet(fname);
}
}
if (modified) {
userAgentStylesheetsChanged();
}
}
}
public void addUserAgentStylesheet(String fname) {
addUserAgentStylesheet(null, fname);
}
public void addUserAgentStylesheet(Scene scene, String url) {
final String fname = (url != null) ? url.trim() : null;
if (fname == null || fname.isEmpty()) {
return;
}
synchronized (styleLock) {
if (_addUserAgentStylesheet(fname)) {
userAgentStylesheetsChanged();
}
}
}
private boolean _addUserAgentStylesheet(String fname) {
synchronized (styleLock) {
for (int n=0, nMax= platformUserAgentStylesheetContainers.size(); n < nMax; n++) {
StylesheetContainer container = platformUserAgentStylesheetContainers.get(n);
if (fname.equals(container.fname)) {
return false;
}
}
final Stylesheet ua_stylesheet = loadStylesheet(fname);
if (ua_stylesheet == null) return false;
ua_stylesheet.setOrigin(StyleOrigin.USER_AGENT);
platformUserAgentStylesheetContainers.add(new StylesheetContainer(fname, ua_stylesheet));
return true;
}
}
public void addUserAgentStylesheet(Scene scene, Stylesheet ua_stylesheet) {
if (ua_stylesheet == null ) {
throw new IllegalArgumentException("null arg ua_stylesheet");
}
String url = ua_stylesheet.getUrl();
final String fname = url != null ? url.trim() : "";
synchronized (styleLock) {
for (int n=0, nMax= platformUserAgentStylesheetContainers.size(); n < nMax; n++) {
StylesheetContainer container = platformUserAgentStylesheetContainers.get(n);
if (fname.equals(container.fname)) {
return;
}
}
platformUserAgentStylesheetContainers.add(new StylesheetContainer(fname, ua_stylesheet));
if (ua_stylesheet != null) {
ua_stylesheet.setOrigin(StyleOrigin.USER_AGENT);
}
userAgentStylesheetsChanged();
}
}
public void setDefaultUserAgentStylesheet(String fname) {
setDefaultUserAgentStylesheet(null, fname);
}
public void setDefaultUserAgentStylesheet(Scene scene, String url) {
final String fname = (url != null) ? url.trim() : null;
if (fname == null || fname.isEmpty()) {
return;
}
synchronized (styleLock) {
if(_setDefaultUserAgentStylesheet(fname)) {
userAgentStylesheetsChanged();
}
}
}
private boolean _setDefaultUserAgentStylesheet(String fname) {
synchronized (styleLock) {
for (int n=0, nMax= platformUserAgentStylesheetContainers.size(); n < nMax; n++) {
StylesheetContainer container = platformUserAgentStylesheetContainers.get(n);
if (fname.equals(container.fname)) {
if (n > 0) {
platformUserAgentStylesheetContainers.remove(n);
if (hasDefaultUserAgentStylesheet) {
platformUserAgentStylesheetContainers.set(0, container);
} else {
platformUserAgentStylesheetContainers.add(0, container);
}
}
return n > 0;
}
}
final Stylesheet ua_stylesheet = loadStylesheet(fname);
if (ua_stylesheet == null) return false;
ua_stylesheet.setOrigin(StyleOrigin.USER_AGENT);
final StylesheetContainer sc = new StylesheetContainer(fname, ua_stylesheet);
if (platformUserAgentStylesheetContainers.size() == 0) {
platformUserAgentStylesheetContainers.add(sc);
}
else if (hasDefaultUserAgentStylesheet) {
platformUserAgentStylesheetContainers.set(0,sc);
}
else {
platformUserAgentStylesheetContainers.add(0,sc);
}
hasDefaultUserAgentStylesheet = true;
return true;
}
}
public void removeUserAgentStylesheet(String url) {
final String fname = (url != null) ? url.trim() : null;
if (fname == null || fname.isEmpty()) {
return;
}
synchronized (styleLock) {
boolean removed = false;
for (int n = platformUserAgentStylesheetContainers.size() - 1; n >= 0; n--) {
if (fname.equals(Application.getUserAgentStylesheet())) {
continue;
}
StylesheetContainer container = platformUserAgentStylesheetContainers.get(n);
if (fname.equals(container.fname)) {
platformUserAgentStylesheetContainers.remove(n);
removed = true;
}
}
if (removed) {
userAgentStylesheetsChanged();
}
}
}
public void setDefaultUserAgentStylesheet(Stylesheet ua_stylesheet) {
if (ua_stylesheet == null ) {
return;
}
String url = ua_stylesheet.getUrl();
final String fname = url != null ? url.trim() : "";
synchronized (styleLock) {
for (int n=0, nMax= platformUserAgentStylesheetContainers.size(); n < nMax; n++) {
StylesheetContainer container = platformUserAgentStylesheetContainers.get(n);
if (fname.equals(container.fname)) {
if (n > 0) {
platformUserAgentStylesheetContainers.remove(n);
if (hasDefaultUserAgentStylesheet) {
platformUserAgentStylesheetContainers.set(0, container);
} else {
platformUserAgentStylesheetContainers.add(0, container);
}
}
return;
}
}
StylesheetContainer sc = new StylesheetContainer(fname, ua_stylesheet);
if (platformUserAgentStylesheetContainers.size() == 0) {
platformUserAgentStylesheetContainers.add(sc);
} else if (hasDefaultUserAgentStylesheet) {
platformUserAgentStylesheetContainers.set(0,sc);
} else {
platformUserAgentStylesheetContainers.add(0,sc);
}
hasDefaultUserAgentStylesheet = true;
ua_stylesheet.setOrigin(StyleOrigin.USER_AGENT);
userAgentStylesheetsChanged();
}
}
private void userAgentStylesheetsChanged() {
List<Parent> parents = new ArrayList<>();
synchronized (styleLock) {
for (CacheContainer container : cacheContainerMap.values()) {
container.clearCache();
}
StyleConverter.clearCache();
for (Parent root : cacheContainerMap.keySet()) {
if (root == null) {
continue;
}
parents.add(root);
}
}
for (Parent root : parents) NodeHelper.reapplyCSS(root);
}
private List<StylesheetContainer> processStylesheets(List<String> stylesheets, Parent parent) {
synchronized (styleLock) {
final List<StylesheetContainer> list = new ArrayList<StylesheetContainer>();
for (int n = 0, nMax = stylesheets.size(); n < nMax; n++) {
final String fname = stylesheets.get(n);
StylesheetContainer container = null;
if (stylesheetContainerMap.containsKey(fname)) {
container = stylesheetContainerMap.get(fname);
if (!list.contains(container)) {
if (container.checksumInvalid) {
final byte[] checksum = calculateCheckSum(fname);
if (!Arrays.equals(checksum, container.checksum)) {
removeStylesheetContainer(container);
Stylesheet stylesheet = loadStylesheet(fname);
container = new StylesheetContainer(fname, stylesheet, checksum);
stylesheetContainerMap.put(fname, container);
} else {
container.checksumInvalid = false;
}
}
list.add(container);
}
container.parentUsers.add(parent);
} else {
final Stylesheet stylesheet = loadStylesheet(fname);
container = new StylesheetContainer(fname, stylesheet);
container.parentUsers.add(parent);
stylesheetContainerMap.put(fname, container);
list.add(container);
}
}
return list;
}
}
private List<StylesheetContainer> gatherParentStylesheets(final Parent parent) {
if (parent == null) {
return Collections.<StylesheetContainer>emptyList();
}
final List<String> parentStylesheets = ParentHelper.getAllParentStylesheets(parent);
if (parentStylesheets == null || parentStylesheets.isEmpty()) {
return Collections.<StylesheetContainer>emptyList();
}
synchronized (styleLock) {
return processStylesheets(parentStylesheets, parent);
}
}
private List<StylesheetContainer> gatherSceneStylesheets(final Scene scene) {
if (scene == null) {
return Collections.<StylesheetContainer>emptyList();
}
final List<String> sceneStylesheets = scene.getStylesheets();
if (sceneStylesheets == null || sceneStylesheets.isEmpty()) {
return Collections.<StylesheetContainer>emptyList();
}
synchronized (styleLock) {
return processStylesheets(sceneStylesheets, scene.getRoot());
}
}
private Key key = null;
private final WeakHashMap<Region, String> weakRegionUserAgentStylesheetMap = new WeakHashMap<>();
public StyleMap findMatchingStyles(Node node, SubScene subScene, Set<PseudoClass>[] triggerStates) {
final Scene scene = node.getScene();
if (scene == null) {
return StyleMap.EMPTY_MAP;
}
CacheContainer cacheContainer = getCacheContainer(node, subScene);
if (cacheContainer == null) {
assert false : node.toString();
return StyleMap.EMPTY_MAP;
}
synchronized (styleLock) {
final Parent parent =
(node instanceof Parent)
? (Parent) node : node.getParent();
final List<StylesheetContainer> parentStylesheets =
gatherParentStylesheets(parent);
final boolean hasParentStylesheets = parentStylesheets.isEmpty() == false;
final List<StylesheetContainer> sceneStylesheets = gatherSceneStylesheets(scene);
final boolean hasSceneStylesheets = sceneStylesheets.isEmpty() == false;
final String inlineStyle = node.getStyle();
final boolean hasInlineStyles = inlineStyle != null && inlineStyle.trim().isEmpty() == false;
final String sceneUserAgentStylesheet = scene.getUserAgentStylesheet();
final boolean hasSceneUserAgentStylesheet =
sceneUserAgentStylesheet != null && sceneUserAgentStylesheet.trim().isEmpty() == false;
final String subSceneUserAgentStylesheet =
(subScene != null) ? subScene.getUserAgentStylesheet() : null;
final boolean hasSubSceneUserAgentStylesheet =
subSceneUserAgentStylesheet != null && subSceneUserAgentStylesheet.trim().isEmpty() == false;
String regionUserAgentStylesheet = null;
Node region = node;
while (region != null) {
if (region instanceof Region) {
regionUserAgentStylesheet = weakRegionUserAgentStylesheetMap.computeIfAbsent(
(Region)region, Region::getUserAgentStylesheet);
if (regionUserAgentStylesheet != null) {
break;
}
}
region = region.getParent();
}
final boolean hasRegionUserAgentStylesheet =
regionUserAgentStylesheet != null && regionUserAgentStylesheet.trim().isEmpty() == false;
if (hasInlineStyles == false
&& hasParentStylesheets == false
&& hasSceneStylesheets == false
&& hasSceneUserAgentStylesheet == false
&& hasSubSceneUserAgentStylesheet == false
&& hasRegionUserAgentStylesheet == false
&& platformUserAgentStylesheetContainers.isEmpty()) {
return StyleMap.EMPTY_MAP;
}
final String cname = node.getTypeSelector();
final String id = node.getId();
final List<String> styleClasses = node.getStyleClass();
if (key == null) {
key = new Key();
}
key.className = cname;
key.id = id;
for(int n=0, nMax=styleClasses.size(); n<nMax; n++) {
final String styleClass = styleClasses.get(n);
if (styleClass == null || styleClass.isEmpty()) continue;
key.styleClasses.add(StyleClassSet.getStyleClass(styleClass));
}
Map<Key, Cache> cacheMap = cacheContainer.getCacheMap(parentStylesheets,regionUserAgentStylesheet);
Cache cache = cacheMap.get(key);
if (cache != null) {
key.styleClasses.clear();
} else {
final List<Selector> selectorData = new ArrayList<>();
if (hasSubSceneUserAgentStylesheet || hasSceneUserAgentStylesheet) {
final String uaFileName = hasSubSceneUserAgentStylesheet ?
subScene.getUserAgentStylesheet().trim() :
scene.getUserAgentStylesheet().trim();
StylesheetContainer container = null;
for (int n=0, nMax=userAgentStylesheetContainers.size(); n<nMax; n++) {
container = userAgentStylesheetContainers.get(n);
if (uaFileName.equals(container.fname)) {
break;
}
container = null;
}
if (container == null) {
Stylesheet stylesheet = loadStylesheet(uaFileName);
if (stylesheet != null) {
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
}
container = new StylesheetContainer(uaFileName, stylesheet);
userAgentStylesheetContainers.add(container);
}
if (container.selectorPartitioning != null) {
final Parent root = hasSubSceneUserAgentStylesheet ? subScene.getRoot() : scene.getRoot();
container.parentUsers.add(root);
final List<Selector> matchingRules =
container.selectorPartitioning.match(id, cname, key.styleClasses);
selectorData.addAll(matchingRules);
}
} else if (platformUserAgentStylesheetContainers.isEmpty() == false) {
for(int n=0, nMax= platformUserAgentStylesheetContainers.size(); n<nMax; n++) {
final StylesheetContainer container = platformUserAgentStylesheetContainers.get(n);
if (container != null && container.selectorPartitioning != null) {
final List<Selector> matchingRules =
container.selectorPartitioning.match(id, cname, key.styleClasses);
selectorData.addAll(matchingRules);
}
}
}
if (hasRegionUserAgentStylesheet) {
StylesheetContainer container = null;
for (int n=0, nMax=userAgentStylesheetContainers.size(); n<nMax; n++) {
container = userAgentStylesheetContainers.get(n);
if (regionUserAgentStylesheet.equals(container.fname)) {
break;
}
container = null;
}
if (container == null) {
Stylesheet stylesheet = loadStylesheet(regionUserAgentStylesheet);
if (stylesheet != null) {
stylesheet.setOrigin(StyleOrigin.USER_AGENT);
}
container = new StylesheetContainer(regionUserAgentStylesheet, stylesheet);
userAgentStylesheetContainers.add(container);
}
if (container.selectorPartitioning != null) {
container.parentUsers.add((Parent)region);
final List<Selector> matchingRules =
container.selectorPartitioning.match(id, cname, key.styleClasses);
selectorData.addAll(matchingRules);
}
}
if (sceneStylesheets.isEmpty() == false) {
for(int n=0, nMax=sceneStylesheets.size(); n<nMax; n++) {
final StylesheetContainer container = sceneStylesheets.get(n);
if (container != null && container.selectorPartitioning != null) {
final List<Selector> matchingRules =
container.selectorPartitioning.match(id, cname, key.styleClasses);
selectorData.addAll(matchingRules);
}
}
}
if (hasParentStylesheets) {
final int nMax = parentStylesheets == null ? 0 : parentStylesheets.size();
for(int n=0; n<nMax; n++) {
final StylesheetContainer container = parentStylesheets.get(n);
if (container.selectorPartitioning != null) {
final List<Selector> matchingRules =
container.selectorPartitioning.match(id, cname, key.styleClasses);
selectorData.addAll(matchingRules);
}
}
}
cache = new Cache(selectorData);
cacheMap.put(key, cache);
key = null;
}
StyleMap smap = cache.getStyleMap(cacheContainer, node, triggerStates, hasInlineStyles);
return smap;
}
}
private static ObservableList<CssParser.ParseError> errors = null;
public static ObservableList<CssParser.ParseError> errorsProperty() {
if (errors == null) {
errors = FXCollections.observableArrayList();
}
return errors;
}
public static ObservableList<CssParser.ParseError> getErrors() {
return errors;
}
private static List<String> cacheMapKey;
static class CacheContainer {
private Map<StyleCache.Key,StyleCache> getStyleCache() {
if (styleCache == null) styleCache = new HashMap<StyleCache.Key, StyleCache>();
return styleCache;
}
private Map<Key,Cache> getCacheMap(List<StylesheetContainer> parentStylesheets, String regionUserAgentStylesheet) {
if (cacheMap == null) {
cacheMap = new HashMap<List<String>,Map<Key,Cache>>();
}
synchronized (styleLock) {
if ((parentStylesheets == null || parentStylesheets.isEmpty()) &&
(regionUserAgentStylesheet == null || regionUserAgentStylesheet.isEmpty())) {
Map<Key,Cache> cmap = cacheMap.get(null);
if (cmap == null) {
cmap = new HashMap<Key,Cache>();
cacheMap.put(null, cmap);
}
return cmap;
} else {
final int nMax = parentStylesheets.size();
if (cacheMapKey == null) {
cacheMapKey = new ArrayList<String>(nMax);
}
for (int n=0; n<nMax; n++) {
StylesheetContainer sc = parentStylesheets.get(n);
if (sc == null || sc.fname == null || sc.fname.isEmpty()) continue;
cacheMapKey.add(sc.fname);
}
if (regionUserAgentStylesheet != null) {
cacheMapKey.add(regionUserAgentStylesheet);
}
Map<Key,Cache> cmap = cacheMap.get(cacheMapKey);
if (cmap == null) {
cmap = new HashMap<Key,Cache>();
cacheMap.put(cacheMapKey, cmap);
cacheMapKey = null;
} else {
cacheMapKey.clear();
}
return cmap;
}
}
}
private List<StyleMap> getStyleMapList() {
if (styleMapList == null) styleMapList = new ArrayList<StyleMap>();
return styleMapList;
}
private int nextSmapId() {
styleMapId = baseStyleMapId + getStyleMapList().size();
return styleMapId;
}
private void addStyleMap(StyleMap smap) {
getStyleMapList().add(smap);
}
public StyleMap getStyleMap(int smapId) {
final int correctedId = smapId - baseStyleMapId;
if (0 <= correctedId && correctedId < getStyleMapList().size()) {
return getStyleMapList().get(correctedId);
}
return StyleMap.EMPTY_MAP;
}
private void clearCache() {
if (cacheMap != null) cacheMap.clear();
if (styleCache != null) styleCache.clear();
if (styleMapList != null) styleMapList.clear();
baseStyleMapId = styleMapId;
if (baseStyleMapId > Integer.MAX_VALUE/8*7) {
baseStyleMapId = styleMapId = 0;
}
}
private Selector getInlineStyleSelector(String inlineStyle) {
if ((inlineStyle == null) || inlineStyle.trim().isEmpty()) return null;
if (inlineStylesCache != null && inlineStylesCache.containsKey(inlineStyle)) {
return inlineStylesCache.get(inlineStyle);
}
if (inlineStylesCache == null) {
inlineStylesCache = new HashMap<>();
}
final Stylesheet inlineStylesheet =
new CssParser().parse("*{"+inlineStyle+"}");
if (inlineStylesheet != null) {
inlineStylesheet.setOrigin(StyleOrigin.INLINE);
List<Rule> rules = inlineStylesheet.getRules();
Rule rule = rules != null && !rules.isEmpty() ? rules.get(0) : null;
List<Selector> selectors = rule != null ? rule.getSelectors() : null;
Selector selector = selectors != null && !selectors.isEmpty() ? selectors.get(0) : null;
if (selector != null) {
selector.setOrdinal(-1);
inlineStylesCache.put(inlineStyle, selector);
return selector;
}
}
inlineStylesCache.put(inlineStyle, null);
return null;
}
private Map<StyleCache.Key,StyleCache> styleCache;
private Map<List<String>, Map<Key,Cache>> cacheMap;
private List<StyleMap> styleMapList;
private Map<String,Selector> inlineStylesCache;
private int styleMapId = 0;
private int baseStyleMapId = 0;
}
private static class Cache {
private static class Key {
final long[] key;
final String inlineStyle;
Key(long[] key, String inlineStyle) {
this.key = key;
this.inlineStyle = (inlineStyle != null && inlineStyle.trim().isEmpty() ? null : inlineStyle);
}
@Override public String toString() {
return Arrays.toString(key) + (inlineStyle != null ? "* {" + inlineStyle + "}" : "");
}
@Override
public int hashCode() {
int hash = 3;
hash = 17 * hash + Arrays.hashCode(this.key);
if (inlineStyle != null) hash = 17 * hash + inlineStyle.hashCode();
return hash;
}
@Override
public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final Key other = (Key) obj;
if (inlineStyle == null ? other.inlineStyle != null : !inlineStyle.equals(other.inlineStyle)) {
return false;
}
if (!Arrays.equals(this.key, other.key)) {
return false;
}
return true;
}
}
private final List<Selector> selectors;
private final Map<Key, Integer> cache;
Cache(List<Selector> selectors) {
this.selectors = selectors;
this.cache = new HashMap<Key, Integer>();
}
private StyleMap getStyleMap(CacheContainer cacheContainer, Node node, Set<PseudoClass>[] triggerStates, boolean hasInlineStyle) {
if ((selectors == null || selectors.isEmpty()) && !hasInlineStyle) {
return StyleMap.EMPTY_MAP;
}
final int selectorDataSize = selectors.size();
long key[] = new long[selectorDataSize/Long.SIZE + 1];
boolean nothingMatched = true;
for (int s = 0; s < selectorDataSize; s++) {
final Selector sel = selectors.get(s);
if (sel.applies(node, triggerStates, 0)) {
final int index = s / Long.SIZE;
final long mask = key[index] | 1l << s;
key[index] = mask;
nothingMatched = false;
}
}
if (nothingMatched && hasInlineStyle == false) {
return StyleMap.EMPTY_MAP;
}
final String inlineStyle = node.getStyle();
final Key keyObj = new Key(key, inlineStyle);
if (cache.containsKey(keyObj)) {
Integer styleMapId = cache.get(keyObj);
final StyleMap styleMap = styleMapId != null
? cacheContainer.getStyleMap(styleMapId.intValue())
: StyleMap.EMPTY_MAP;
return styleMap;
}
final List<Selector> selectors = new ArrayList<>();
if (hasInlineStyle) {
Selector selector = cacheContainer.getInlineStyleSelector(inlineStyle);
if (selector != null) selectors.add(selector);
}
for (int k = 0; k<key.length; k++) {
if (key[k] == 0) continue;
final int offset = k * Long.SIZE;
for (int b = 0; b<Long.SIZE; b++) {
final long mask = 1l << b;
if ((mask & key[k]) != mask) continue;
final Selector pair = this.selectors.get(offset + b);
selectors.add(pair);
}
}
int id = cacheContainer.nextSmapId();
cache.put(keyObj, Integer.valueOf(id));
final StyleMap styleMap = new StyleMap(id, selectors);
cacheContainer.addStyleMap(styleMap);
return styleMap;
}
}
private static class Key {
String className;
String id;
final StyleClassSet styleClasses;
private Key() {
styleClasses = new StyleClassSet();
}
@Override
public boolean equals(Object o) {
if (this == o) {
return true;
}
if (o instanceof Key) {
Key other = (Key)o;
if (className == null ? other.className != null : (className.equals(other.className) == false)) {
return false;
}
if (id == null ? other.id != null : (id.equals(other.id) == false)) {
return false;
}
return this.styleClasses.equals(other.styleClasses);
}
return true;
}
@Override
public int hashCode() {
int hash = 7;
hash = 29 * hash + (this.className != null ? this.className.hashCode() : 0);
hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
hash = 29 * hash + this.styleClasses.hashCode();
return hash;
}
}
}
