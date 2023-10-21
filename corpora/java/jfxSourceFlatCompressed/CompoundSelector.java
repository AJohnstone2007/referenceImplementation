package javafx.css;
import com.sun.javafx.css.Combinator;
import com.sun.javafx.css.PseudoClassState;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
final public class CompoundSelector extends Selector {
private final List<SimpleSelector> selectors;
public List<SimpleSelector> getSelectors() {
return selectors;
}
private final List<Combinator> relationships;
CompoundSelector(List<SimpleSelector> selectors, List<Combinator> relationships) {
this.selectors =
(selectors != null)
? Collections.unmodifiableList(selectors)
: Collections.EMPTY_LIST;
this.relationships =
(relationships != null)
? Collections.unmodifiableList(relationships)
: Collections.EMPTY_LIST;
}
private CompoundSelector() {
this(null, null);
}
@Override public Match createMatch() {
final PseudoClassState allPseudoClasses = new PseudoClassState();
int idCount = 0;
int styleClassCount = 0;
for(int n=0, nMax=selectors.size(); n<nMax; n++) {
Selector sel = selectors.get(n);
Match match = sel.createMatch();
allPseudoClasses.addAll(match.pseudoClasses);
idCount += match.idCount;
styleClassCount += match.styleClassCount;
}
return new Match(this, allPseudoClasses, idCount, styleClassCount);
}
@Override public boolean applies(final Styleable styleable) {
return applies(styleable, selectors.size()-1, null, 0);
}
@Override public boolean applies(final Styleable styleable, Set<PseudoClass>[] triggerStates, int depth) {
assert (triggerStates == null || depth < triggerStates.length);
if (triggerStates != null && triggerStates.length <= depth) {
return false;
}
final Set<PseudoClass>[] tempStates = triggerStates != null
? new PseudoClassState[triggerStates.length] : null;
final boolean applies = applies(styleable, selectors.size()-1, tempStates, depth);
if (applies && tempStates != null) {
for(int n=0; n<triggerStates.length; n++) {
final Set<PseudoClass> pseudoClassOut = triggerStates[n];
final Set<PseudoClass> pseudoClassIn = tempStates[n];
if (pseudoClassOut != null) {
pseudoClassOut.addAll(pseudoClassIn);
} else {
triggerStates[n] = pseudoClassIn;
}
}
}
return applies;
}
private boolean applies(final Styleable styleable, final int index, Set<PseudoClass>[] triggerStates, int depth) {
if (index < 0) return false;
if (! selectors.get(index).applies(styleable, triggerStates, depth)) return false;
if (index == 0) return true;
final Combinator relationship = relationships.get(index-1);
if (relationship == Combinator.CHILD) {
final Styleable parent = styleable.getStyleableParent();
if (parent == null) return false;
return applies(parent, index - 1, triggerStates, ++depth);
} else {
Styleable parent = styleable.getStyleableParent();
while (parent != null) {
boolean answer = applies(parent, index - 1, triggerStates, ++depth);
if (answer) return true;
parent = parent.getStyleableParent();
}
}
return false;
}
@Override public boolean stateMatches(final Styleable styleable, Set<PseudoClass> states) {
return stateMatches(styleable, states, selectors.size()-1);
}
private boolean stateMatches(Styleable styleable, Set<PseudoClass> states, int index) {
if (index < 0) return false;
if (! selectors.get(index).stateMatches(styleable, states)) return false;
if (index == 0) return true;
final Combinator relationship = relationships.get(index - 1);
if (relationship == Combinator.CHILD) {
final Styleable parent = styleable.getStyleableParent();
if (parent == null) return false;
if (selectors.get(index-1).applies(parent)) {
Set<PseudoClass> parentStates = parent.getPseudoClassStates();
return stateMatches(parent, parentStates, index - 1);
}
} else {
Styleable parent = styleable.getStyleableParent();
while (parent != null) {
if (selectors.get(index-1).applies(parent)) {
Set<PseudoClass> parentStates = parent.getPseudoClassStates();
return stateMatches(parent, parentStates, index - 1);
}
parent = parent.getStyleableParent();
}
}
return false;
}
private int hash = -1;
@Override public int hashCode() {
if (hash == -1) {
for (int i = 0, max=selectors.size(); i<max; i++)
hash = 31 * (hash + selectors.get(i).hashCode());
for (int i = 0, max=relationships.size(); i<max; i++)
hash = 31 * (hash + relationships.get(i).hashCode());
}
return hash;
}
@Override public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final CompoundSelector other = (CompoundSelector) obj;
if (other.selectors.size() != selectors.size()) return false;
for (int i = 0, max=selectors.size(); i<max; i++) {
if (!other.selectors.get(i).equals(selectors.get(i))) return false;
}
if (other.relationships.size() != relationships.size()) return false;
for (int i = 0, max=relationships.size(); i<max; i++) {
if (!other.relationships.get(i).equals(relationships.get(i))) return false;
}
return true;
}
@Override public String toString() {
StringBuilder sbuf = new StringBuilder();
sbuf.append(selectors.get(0));
for(int n=1; n<selectors.size(); n++) {
sbuf.append(relationships.get(n-1));
sbuf.append(selectors.get(n));
}
return sbuf.toString();
}
@Override protected final void writeBinary(final DataOutputStream os, final StyleConverter.StringStore stringStore)
throws IOException
{
super.writeBinary(os, stringStore);
os.writeShort(selectors.size());
for (int n=0; n< selectors.size(); n++) selectors.get(n).writeBinary(os,stringStore);
os.writeShort(relationships.size());
for (int n=0; n< relationships.size(); n++) os.writeByte(relationships.get(n).ordinal());
}
static CompoundSelector readBinary(int bssVersion, final DataInputStream is, final String[] strings)
throws IOException
{
final int nSelectors = is.readShort();
final List<SimpleSelector> selectors = new ArrayList<SimpleSelector>();
for (int n=0; n<nSelectors; n++) {
selectors.add((SimpleSelector)Selector.readBinary(bssVersion, is,strings));
}
final int nRelationships = is.readShort();
final List<Combinator> relationships = new ArrayList<Combinator>();
for (int n=0; n<nRelationships; n++) {
final int ordinal = is.readByte();
if (ordinal == Combinator.CHILD.ordinal())
relationships.add(Combinator.CHILD);
else if (ordinal == Combinator.DESCENDANT.ordinal())
relationships.add(Combinator.DESCENDANT);
else {
assert false : "error deserializing CompoundSelector: Combinator = " + ordinal;
relationships.add(Combinator.DESCENDANT);
}
}
return new CompoundSelector(selectors, relationships);
}
}
