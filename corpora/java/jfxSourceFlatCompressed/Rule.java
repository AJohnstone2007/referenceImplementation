package javafx.css;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import com.sun.javafx.collections.TrackableObservableList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
final public class Rule {
private List<Selector> selectors = null;
List<Selector> getUnobservedSelectorList() {
if (selectors == null) {
selectors = new ArrayList<Selector>();
}
return selectors;
}
private List<Declaration> declarations = null;
List<Declaration> getUnobservedDeclarationList() {
if (declarations == null && serializedDecls != null) {
try {
ByteArrayInputStream bis = new ByteArrayInputStream(serializedDecls);
DataInputStream dis = new DataInputStream(bis);
short nDeclarations = dis.readShort();
declarations = new ArrayList<Declaration>(nDeclarations);
for (int i = 0; i < nDeclarations; i++) {
Declaration decl = Declaration.readBinary(bssVersion, dis, stylesheet.getStringStore());
decl.rule = Rule.this;
if (stylesheet != null && stylesheet.getUrl() != null) {
String stylesheetUrl = stylesheet.getUrl();
decl.fixUrl(stylesheetUrl);
}
declarations.add(decl);
}
} catch (IOException ioe) {
declarations = new ArrayList<>();
assert false; ioe.getMessage();
} finally {
serializedDecls = null;
}
}
return declarations;
}
private Observables observables = null;
public final ObservableList<Declaration> getDeclarations() {
if (observables == null) {
observables = new Observables(this);
}
return observables.getDeclarations();
}
public final ObservableList<Selector> getSelectors() {
if (observables == null) {
observables = new Observables(this);
}
return observables.getSelectors();
}
private Stylesheet stylesheet;
public Stylesheet getStylesheet() {
return stylesheet;
}
void setStylesheet(Stylesheet stylesheet) {
this.stylesheet = stylesheet;
if (stylesheet != null && stylesheet.getUrl() != null) {
final String stylesheetUrl = stylesheet.getUrl();
int nDeclarations = declarations != null ? declarations.size() : 0;
for (int d=0; d<nDeclarations; d++) {
declarations.get(d).fixUrl(stylesheetUrl);
}
}
}
public StyleOrigin getOrigin() {
return stylesheet != null ? stylesheet.getOrigin() : null;
}
Rule(List<Selector> selectors, List<Declaration> declarations) {
this.selectors = selectors;
this.declarations = declarations;
serializedDecls = null;
this.bssVersion = Stylesheet.BINARY_CSS_VERSION;
int sMax = selectors != null ? selectors.size() : 0;
for(int i = 0; i < sMax; i++) {
Selector sel = selectors.get(i);
sel.setRule(Rule.this);
}
int dMax = declarations != null ? declarations.size() : 0;
for (int d=0; d<dMax; d++) {
Declaration decl = declarations.get(d);
decl.rule = this;
}
}
private byte[] serializedDecls;
private final int bssVersion;
private Rule(List<Selector> selectors, byte[] buf, int bssVersion) {
this.selectors = selectors;
this.declarations = null;
this.serializedDecls = buf;
this.bssVersion = bssVersion;
int sMax = selectors != null ? selectors.size() : 0;
for(int i = 0; i < sMax; i++) {
Selector sel = selectors.get(i);
sel.setRule(Rule.this);
}
}
long applies(Node node, Set<PseudoClass>[] triggerStates) {
long mask = 0;
for (int i = 0; i < selectors.size(); i++) {
Selector sel = selectors.get(i);
if (sel.applies(node, triggerStates, 0)) {
mask |= 1l << i;
}
}
return mask;
}
@Override public String toString() {
StringBuilder sb = new StringBuilder();
if (selectors.size()>0) {
sb.append(selectors.get(0));
}
for (int n=1; n<selectors.size(); n++) {
sb.append(',');
sb.append(selectors.get(n));
}
sb.append("{\n");
int nDeclarations = declarations != null ? declarations.size() : 0;
for (int n=0; n<nDeclarations; n++) {
sb.append("\t");
sb.append(declarations.get(n));
sb.append("\n");
}
sb .append("}");
return sb.toString();
}
private final static class Observables {
private Observables(Rule rule) {
this.rule = rule;
selectorObservableList = new TrackableObservableList<Selector>(rule.getUnobservedSelectorList()) {
@Override protected void onChanged(Change<Selector> c) {
while (c.next()) {
if (c.wasAdded()) {
List<Selector> added = c.getAddedSubList();
for(int i = 0, max = added.size(); i < max; i++) {
Selector sel = added.get(i);
sel.setRule(Observables.this.rule);
}
}
if (c.wasRemoved()) {
List<Selector> removed = c.getAddedSubList();
for(int i = 0, max = removed.size(); i < max; i++) {
Selector sel = removed.get(i);
if (sel.getRule() == Observables.this.rule) {
sel.setRule(null);
}
}
}
}
}
};
declarationObservableList = new TrackableObservableList<Declaration>(rule.getUnobservedDeclarationList()) {
@Override protected void onChanged(Change<Declaration> c) {
while (c.next()) {
if (c.wasAdded()) {
List<Declaration> added = c.getAddedSubList();
for(int i = 0, max = added.size(); i < max; i++) {
Declaration decl = added.get(i);
decl.rule = Observables.this.rule;
Stylesheet stylesheet = Observables.this.rule.stylesheet;
if (stylesheet != null && stylesheet.getUrl() != null) {
final String stylesheetUrl = stylesheet.getUrl();
decl.fixUrl(stylesheetUrl);
}
}
}
if (c.wasRemoved()) {
List<Declaration> removed = c.getRemoved();
for(int i = 0, max = removed.size(); i < max; i++) {
Declaration decl = removed.get(i);
if (decl.rule == Observables.this.rule) {
decl.rule = null;
}
}
}
}
}
};
}
private ObservableList<Selector> getSelectors() {
return selectorObservableList;
}
private ObservableList<Declaration> getDeclarations() {
return declarationObservableList;
}
private final Rule rule;
private final ObservableList<Selector> selectorObservableList;
private final ObservableList<Declaration> declarationObservableList;
}
final void writeBinary(DataOutputStream os, StyleConverter.StringStore stringStore)
throws IOException {
final int nSelectors = this.selectors != null ? this.selectors.size() : 0;
os.writeShort(nSelectors);
for (int i = 0; i < nSelectors; i++) {
Selector sel = this.selectors.get(i);
sel.writeBinary(os, stringStore);
}
List<Declaration> decls = getUnobservedDeclarationList();
if (decls != null) {
ByteArrayOutputStream bos = new ByteArrayOutputStream(5192);
DataOutputStream dos = new DataOutputStream(bos);
int nDeclarations = decls.size();
dos.writeShort(nDeclarations);
for (int i = 0; i < nDeclarations; i++) {
Declaration decl = declarations.get(i);
decl.writeBinary(dos, stringStore);
}
os.writeInt(bos.size());
os.write(bos.toByteArray());
} else {
os.writeShort(0);
}
}
static Rule readBinary(int bssVersion, DataInputStream is, String[] strings)
throws IOException
{
short nSelectors = is.readShort();
List<Selector> selectors = new ArrayList<Selector>(nSelectors);
for (int i = 0; i < nSelectors; i++) {
Selector s = Selector.readBinary(bssVersion, is, strings);
selectors.add(s);
}
if (bssVersion < 4) {
short nDeclarations = is.readShort();
List<Declaration> declarations = new ArrayList<Declaration>(nDeclarations);
for (int i = 0; i < nDeclarations; i++) {
Declaration d = Declaration.readBinary(bssVersion, is, strings);
declarations.add(d);
}
return new Rule(selectors, declarations);
}
int nBytes = is.readInt();
byte[] buf = new byte[nBytes];
if (nBytes > 0) {
is.readFully(buf);
}
return new Rule(selectors, buf, bssVersion);
}
}
