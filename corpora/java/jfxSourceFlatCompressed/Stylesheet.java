package javafx.css;
import javafx.css.StyleConverter.StringStore;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.css.FontFaceImpl;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
public class Stylesheet {
final static int BINARY_CSS_VERSION = 6;
private final String url;
public String getUrl() {
return url;
}
private StyleOrigin origin = StyleOrigin.AUTHOR;
public StyleOrigin getOrigin() {
return origin;
}
public void setOrigin(StyleOrigin origin) {
this.origin = origin;
}
private final ObservableList<Rule> rules = new TrackableObservableList<Rule>() {
@Override protected void onChanged(Change<Rule> c) {
c.reset();
while (c.next()) {
if (c.wasAdded()) {
for(Rule rule : c.getAddedSubList()) {
rule.setStylesheet(Stylesheet.this);
}
} else if (c.wasRemoved()) {
for (Rule rule : c.getRemoved()) {
if (rule.getStylesheet() == Stylesheet.this) rule.setStylesheet(null);
}
}
}
}
};
private final List<FontFace> fontFaces = new ArrayList<FontFace>();
Stylesheet() {
this(null);
}
Stylesheet(String url) {
this.url = url;
}
public List<Rule> getRules() {
return rules;
}
public List<FontFace> getFontFaces() {
return fontFaces;
}
@Override
public boolean equals(Object obj) {
if (this == obj) return true;
if (obj instanceof Stylesheet) {
Stylesheet other = (Stylesheet)obj;
if (this.url == null && other.url == null) {
return true;
} else if (this.url == null || other.url == null) {
return false;
} else {
return this.url.equals(other.url);
}
}
return false;
}
@Override public int hashCode() {
int hash = 7;
hash = 13 * hash + (this.url != null ? this.url.hashCode() : 0);
return hash;
}
@Override public String toString() {
StringBuilder sbuf = new StringBuilder();
sbuf.append("/* ");
if (url != null) sbuf.append(url);
if (rules.isEmpty()) {
sbuf.append(" */");
} else {
sbuf.append(" */\n");
for(int r=0; r<rules.size(); r++) {
sbuf.append(rules.get(r));
sbuf.append('\n');
}
}
return sbuf.toString();
}
final void writeBinary(final DataOutputStream os, final StringStore stringStore)
throws IOException
{
int index = stringStore.addString(origin.name());
os.writeShort(index);
os.writeShort(rules.size());
for (Rule r : rules) r.writeBinary(os,stringStore);
List<FontFace> fontFaceList = getFontFaces();
int nFontFaces = fontFaceList != null ? fontFaceList.size() : 0;
os.writeShort(nFontFaces);
for(int n=0; n<nFontFaces; n++) {
FontFace fontFace = fontFaceList.get(n);
if (fontFace instanceof FontFaceImpl) {
((FontFaceImpl)fontFace).writeBinary(os, stringStore);
}
}
}
final void readBinary(int bssVersion, DataInputStream is, String[] strings)
throws IOException
{
this.stringStore = strings;
final int index = is.readShort();
this.setOrigin(StyleOrigin.valueOf(strings[index]));
final int nRules = is.readShort();
List<Rule> persistedRules = new ArrayList<Rule>(nRules);
for (int n=0; n<nRules; n++) {
persistedRules.add(Rule.readBinary(bssVersion,is,strings));
}
this.rules.addAll(persistedRules);
if (bssVersion >= 5) {
List<FontFace> fontFaceList = this.getFontFaces();
int nFontFaces = is.readShort();
for (int n=0; n<nFontFaces; n++) {
FontFace fontFace = FontFaceImpl.readBinary(bssVersion, is, strings);
fontFaceList.add(fontFace);
}
}
}
private String[] stringStore;
final String[] getStringStore() { return stringStore; }
public static Stylesheet loadBinary(URL url) throws IOException {
if (url == null) {
return null;
}
try (InputStream stream = url.openStream()) {
return loadBinary(stream, url.toExternalForm());
} catch (FileNotFoundException ex) {
return null;
}
}
public static Stylesheet loadBinary(InputStream stream) throws IOException {
return loadBinary(stream, null);
}
private static Stylesheet loadBinary(InputStream stream, String uri) throws IOException {
Stylesheet stylesheet = null;
try (DataInputStream dataInputStream =
new DataInputStream(new BufferedInputStream(stream, 40 * 1024))) {
final int bssVersion = dataInputStream.readShort();
if (bssVersion > Stylesheet.BINARY_CSS_VERSION) {
throw new IOException(
String.format("Wrong binary CSS version %s, expected version less than or equal to %s",
uri != null ? bssVersion + " in stylesheet \"" + uri + "\"" : bssVersion,
Stylesheet.BINARY_CSS_VERSION));
}
final String[] strings = StringStore.readBinary(dataInputStream);
stylesheet = new Stylesheet(uri);
try {
dataInputStream.mark(Integer.MAX_VALUE);
stylesheet.readBinary(bssVersion, dataInputStream, strings);
} catch (Exception e) {
stylesheet = new Stylesheet(uri);
dataInputStream.reset();
if (bssVersion == 2) {
stylesheet.readBinary(3, dataInputStream, strings);
} else {
stylesheet.readBinary(Stylesheet.BINARY_CSS_VERSION, dataInputStream, strings);
}
}
}
return stylesheet;
}
public static void convertToBinary(File source, File destination) throws IOException {
if (source == null || destination == null) {
throw new IllegalArgumentException("parameters may not be null");
}
if (source.getAbsolutePath().equals(destination.getAbsolutePath())) {
throw new IllegalArgumentException("source and destination may not be the same");
}
if (source.canRead() == false) {
throw new IllegalArgumentException("cannot read source file");
}
if (destination.exists() ? (destination.canWrite() == false) : (destination.createNewFile() == false)) {
throw new IllegalArgumentException("cannot write destination file");
}
URI sourceURI = source.toURI();
Stylesheet stylesheet = new CssParser().parse(sourceURI.toURL());
ByteArrayOutputStream baos = new ByteArrayOutputStream();
DataOutputStream dos = new DataOutputStream(baos);
StringStore stringStore = new StringStore();
stylesheet.writeBinary(dos, stringStore);
dos.flush();
dos.close();
FileOutputStream fos = new FileOutputStream(destination);
DataOutputStream os = new DataOutputStream(fos);
os.writeShort(BINARY_CSS_VERSION);
stringStore.writeBinary(os);
os.write(baos.toByteArray());
os.flush();
os.close();
}
void importStylesheet(Stylesheet importedStylesheet) {
if (importedStylesheet == null) return;
List<Rule> rulesToImport = importedStylesheet.getRules();
if (rulesToImport == null || rulesToImport.isEmpty()) return;
List<Rule> importedRules = new ArrayList<>(rulesToImport.size());
for (Rule rule : rulesToImport) {
List<Selector> selectors = rule.getSelectors();
List<Declaration> declarations = rule.getUnobservedDeclarationList();
importedRules.add(new Rule(selectors, declarations));
}
rules.addAll(importedRules);
}
}
