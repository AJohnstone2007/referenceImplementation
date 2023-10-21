package com.sun.javafx.css;
import javafx.css.PseudoClass;
import javafx.scene.text.Font;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
public final class StyleCacheEntry {
public StyleCacheEntry() {
}
public CalculatedValue get(String property) {
CalculatedValue cv = null;
if (calculatedValues != null && ! calculatedValues.isEmpty()) {
cv = calculatedValues.get(property);
}
return cv;
}
public void put(String property, CalculatedValue calculatedValue) {
if (calculatedValues == null) {
this.calculatedValues = new HashMap<>(5);
}
calculatedValues.put(property, calculatedValue);
}
public final static class Key {
private final Set<PseudoClass>[] pseudoClassStates;
private final double fontSize;
private int hash = Integer.MIN_VALUE;
public Key(Set<PseudoClass>[] pseudoClassStates, Font font) {
this.pseudoClassStates = new Set[pseudoClassStates.length];
for (int n=0; n<pseudoClassStates.length; n++) {
this.pseudoClassStates[n] = new PseudoClassState();
this.pseudoClassStates[n].addAll(pseudoClassStates[n]);
}
this.fontSize = font != null ? font.getSize() : Font.getDefault().getSize();
}
@Override public String toString() {
return Arrays.toString(pseudoClassStates) + ", " + fontSize;
}
public static int hashCode(double value) {
long bits = Double.doubleToLongBits(value);
return (int)(bits ^ (bits >>> 32));
}
@Override
public int hashCode() {
if (hash == Integer.MIN_VALUE) {
hash = hashCode(fontSize);
final int iMax = pseudoClassStates != null ? pseudoClassStates.length : 0;
for (int i=0; i<iMax; i++) {
final Set<PseudoClass> states = pseudoClassStates[i];
if (states != null) {
hash = 67 * (hash + states.hashCode());
}
}
}
return hash;
}
@Override
public boolean equals(Object obj) {
if (obj == this) return true;
if (obj == null || obj.getClass() != this.getClass()) return false;
final Key other = (Key) obj;
if (this.hash != other.hash) return false;
final double diff = fontSize - other.fontSize;
if (diff < -0.000001 || 0.000001 < diff) {
return false;
}
if ((pseudoClassStates == null) ^ (other.pseudoClassStates == null)) {
return false;
}
if (pseudoClassStates == null) {
return true;
}
if (pseudoClassStates.length != other.pseudoClassStates.length) {
return false;
}
for (int i=0; i<pseudoClassStates.length; i++) {
final Set<PseudoClass> this_pcs = pseudoClassStates[i];
final Set<PseudoClass> other_pcs = other.pseudoClassStates[i];
if (this_pcs == null ? other_pcs != null : !this_pcs.equals(other_pcs)) {
return false;
}
}
return true;
}
}
private Map<String,CalculatedValue> calculatedValues;
}
