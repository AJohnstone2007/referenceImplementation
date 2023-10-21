package javafx.css;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.sun.javafx.css.PseudoClassState;
import com.sun.javafx.css.StyleClassSet;
import static javafx.geometry.NodeOrientation.INHERIT;
import static javafx.geometry.NodeOrientation.LEFT_TO_RIGHT;
import static javafx.geometry.NodeOrientation.RIGHT_TO_LEFT;
final public class SimpleSelector extends Selector {
final private String name;
public String getName() {
return name;
}
public List<String> getStyleClasses() {
final List<String> names = new ArrayList<String>();
Iterator<StyleClass> iter = styleClassSet.iterator();
while (iter.hasNext()) {
names.add(iter.next().getStyleClassName());
}
return Collections.unmodifiableList(names);
}
public Set<StyleClass> getStyleClassSet() {
return styleClassSet;
}
final private StyleClassSet styleClassSet;
final private String id;
public String getId() {
return id;
}
final private PseudoClassState pseudoClassState;
Set<PseudoClass> getPseudoClassStates() {
return pseudoClassState;
}
List<String> getPseudoclasses() {
final List<String> names = new ArrayList<String>();
Iterator<PseudoClass> iter = pseudoClassState.iterator();
while (iter.hasNext()) {
names.add(iter.next().getPseudoClassName());
}
if (nodeOrientation == RIGHT_TO_LEFT) {
names.add("dir(rtl)");
} else if (nodeOrientation == LEFT_TO_RIGHT) {
names.add("dir(ltr)");
}
return Collections.unmodifiableList(names);
}
final private boolean matchOnName;
final private boolean matchOnId;
final private boolean matchOnStyleClass;
final private NodeOrientation nodeOrientation;
public NodeOrientation getNodeOrientation() {
return nodeOrientation;
}
SimpleSelector(final String name, final List<String> styleClasses,
final List<String> pseudoClasses, final String id)
{
this.name = name == null ? "*" : name;
this.matchOnName = (name != null && !("".equals(name)) && !("*".equals(name)));
this.styleClassSet = new StyleClassSet();
int nMax = styleClasses != null ? styleClasses.size() : 0;
for(int n=0; n<nMax; n++) {
final String styleClassName = styleClasses.get(n);
if (styleClassName == null || styleClassName.isEmpty()) continue;
final StyleClass styleClass = StyleClassSet.getStyleClass(styleClassName);
this.styleClassSet.add(styleClass);
}
this.matchOnStyleClass = (this.styleClassSet.size() > 0);
this.pseudoClassState = new PseudoClassState();
nMax = pseudoClasses != null ? pseudoClasses.size() : 0;
NodeOrientation dir = NodeOrientation.INHERIT;
for(int n=0; n<nMax; n++) {
final String pclass = pseudoClasses.get(n);
if (pclass == null || pclass.isEmpty()) continue;
if ("dir(".regionMatches(true, 0, pclass, 0, 4)) {
final boolean rtl = "dir(rtl)".equalsIgnoreCase(pclass);
dir = rtl ? RIGHT_TO_LEFT : LEFT_TO_RIGHT;
continue;
}
final PseudoClass pseudoClass = PseudoClassState.getPseudoClass(pclass);
this.pseudoClassState.add(pseudoClass);
}
this.nodeOrientation = dir;
this.id = id == null ? "" : id;
this.matchOnId = (id != null && !("".equals(id)));
}
@Override public Match createMatch() {
final int idCount = (matchOnId) ? 1 : 0;
int styleClassCount = styleClassSet.size();
return new Match(this, pseudoClassState, idCount, styleClassCount);
}
@Override public boolean applies(Styleable styleable) {
if (nodeOrientation != INHERIT && styleable instanceof Node) {
final Node node = (Node)styleable;
final NodeOrientation orientation = node.getNodeOrientation();
if (orientation == INHERIT
? node.getEffectiveNodeOrientation() != nodeOrientation
: orientation != nodeOrientation)
{
return false;
}
}
if (matchOnId) {
final String otherId = styleable.getId();
final boolean idMatch = id.equals(otherId);
if (!idMatch) return false;
}
if (matchOnName) {
final String otherName = styleable.getTypeSelector();
final boolean classMatch = this.name.equals(otherName);
if (!classMatch) return false;
}
if (matchOnStyleClass) {
final StyleClassSet otherStyleClassSet = new StyleClassSet();
final List<String> styleClasses = styleable.getStyleClass();
for(int n=0, nMax = styleClasses.size(); n<nMax; n++) {
final String styleClassName = styleClasses.get(n);
if (styleClassName == null || styleClassName.isEmpty()) continue;
final StyleClass styleClass = StyleClassSet.getStyleClass(styleClassName);
otherStyleClassSet.add(styleClass);
}
boolean styleClassMatch = matchStyleClasses(otherStyleClassSet);
if (!styleClassMatch) return false;
}
return true;
}
@Override public boolean applies(Styleable styleable, Set<PseudoClass>[] pseudoClasses, int depth) {
final boolean applies = applies(styleable);
if (applies && pseudoClasses != null && depth < pseudoClasses.length) {
if (pseudoClasses[depth] == null) {
pseudoClasses[depth] = new PseudoClassState();
}
pseudoClasses[depth].addAll(pseudoClassState);
}
return applies;
}
@Override public boolean stateMatches(final Styleable styleable, Set<PseudoClass> states) {
return states != null ? states.containsAll(pseudoClassState) : false;
}
private boolean matchStyleClasses(StyleClassSet otherStyleClasses) {
return otherStyleClasses.containsAll(styleClassSet);
}
@Override public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final SimpleSelector other = (SimpleSelector) obj;
if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
return false;
}
if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
return false;
}
if (this.styleClassSet.equals(other.styleClassSet) == false) {
return false;
}
if (this.pseudoClassState.equals(other.pseudoClassState) == false) {
return false;
}
return true;
}
@Override public int hashCode() {
int hash = 7;
hash = 31 * (hash + name.hashCode());
hash = 31 * (hash + styleClassSet.hashCode());
hash = 31 * (hash + styleClassSet.hashCode());
hash = (id != null) ? 31 * (hash + id.hashCode()) : 0;
hash = 31 * (hash + pseudoClassState.hashCode());
return hash;
}
@Override public String toString() {
StringBuilder sbuf = new StringBuilder();
if (name != null && name.isEmpty() == false) sbuf.append(name);
else sbuf.append("*");
Iterator<StyleClass> iter1 = styleClassSet.iterator();
while(iter1.hasNext()) {
final StyleClass styleClass = iter1.next();
sbuf.append('.').append(styleClass.getStyleClassName());
}
if (id != null && id.isEmpty() == false) {
sbuf.append('#');
sbuf.append(id);
}
Iterator<PseudoClass> iter2 = pseudoClassState.iterator();
while(iter2.hasNext()) {
final PseudoClass pseudoClass = iter2.next();
sbuf.append(':').append(pseudoClass.getPseudoClassName());
}
return sbuf.toString();
}
@Override protected final void writeBinary(final DataOutputStream os, final StyleConverter.StringStore stringStore)
throws IOException
{
super.writeBinary(os, stringStore);
os.writeShort(stringStore.addString(name));
os.writeShort(styleClassSet.size());
Iterator<StyleClass> iter1 = styleClassSet.iterator();
while(iter1.hasNext()) {
final StyleClass sc = iter1.next();
os.writeShort(stringStore.addString(sc.getStyleClassName()));
}
os.writeShort(stringStore.addString(id));
int pclassSize = pseudoClassState.size()
+ (nodeOrientation == RIGHT_TO_LEFT || nodeOrientation == LEFT_TO_RIGHT ? 1 : 0);
os.writeShort(pclassSize);
Iterator<PseudoClass> iter2 = pseudoClassState.iterator();
while(iter2.hasNext()) {
final PseudoClass pc = iter2.next();
os.writeShort(stringStore.addString(pc.getPseudoClassName()));
}
if (nodeOrientation == RIGHT_TO_LEFT) {
os.writeShort(stringStore.addString("dir(rtl)"));
} else if (nodeOrientation == LEFT_TO_RIGHT) {
os.writeShort(stringStore.addString("dir(ltr)"));
}
}
static SimpleSelector readBinary(int bssVersion, final DataInputStream is, final String[] strings)
throws IOException
{
final String name = strings[is.readShort()];
final int nStyleClasses = is.readShort();
final List<String> styleClasses = new ArrayList<String>();
for (int n=0; n < nStyleClasses; n++) {
styleClasses.add(strings[is.readShort()]);
}
final String id = strings[is.readShort()];
final int nPseudoclasses = is.readShort();
final List<String> pseudoclasses = new ArrayList<String>();
for(int n=0; n < nPseudoclasses; n++) {
pseudoclasses.add(strings[is.readShort()]);
}
return new SimpleSelector(name, styleClasses, pseudoclasses, id);
}
}
