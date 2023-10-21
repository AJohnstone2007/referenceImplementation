package javafx.css;
import java.util.Collections;
import java.util.List;
import javafx.scene.Node;
public abstract class CssMetaData<S extends Styleable, V> {
@Deprecated(since="8")
public void set(S styleable, V value, StyleOrigin origin) {
final StyleableProperty<V> styleableProperty = getStyleableProperty(styleable);
final StyleOrigin currentOrigin = styleableProperty.getStyleOrigin();
final V currentValue = styleableProperty.getValue();
if ((currentOrigin != origin)
|| (currentValue != null
? currentValue.equals(value) == false
: value != null)) {
styleableProperty.applyStyle(origin, value);
}
}
public abstract boolean isSettable(S styleable);
public abstract StyleableProperty<V> getStyleableProperty(S styleable);
private final String property;
public final String getProperty() {
return property;
}
private final StyleConverter<?,V> converter;
public final StyleConverter<?,V> getConverter() {
return converter;
}
private final V initialValue;
public V getInitialValue(S styleable) {
return initialValue;
}
private final List<CssMetaData<? extends Styleable, ?>> subProperties;
public final List<CssMetaData<? extends Styleable, ?>> getSubProperties() {
return subProperties;
}
private final boolean inherits;
public final boolean isInherits() {
return inherits;
}
protected CssMetaData(
final String property,
final StyleConverter<?,V> converter,
final V initialValue,
boolean inherits,
final List<CssMetaData<? extends Styleable, ?>> subProperties) {
this.property = property;
this.converter = converter;
this.initialValue = initialValue;
this.inherits = inherits;
this.subProperties = subProperties != null ? Collections.unmodifiableList(subProperties) : null;
if (this.property == null || this.converter == null) {
throw new IllegalArgumentException("neither property nor converter can be null");
}
}
protected CssMetaData(
final String property,
final StyleConverter<?,V> converter,
final V initialValue,
boolean inherits) {
this(property, converter, initialValue, inherits, null);
}
protected CssMetaData(
final String property,
final StyleConverter<?,V> converter,
final V initialValue) {
this(property, converter, initialValue, false, null);
}
protected CssMetaData(
final String property,
final StyleConverter<?,V> converter) {
this(property, converter, null, false, null);
}
@Override
public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final CssMetaData<? extends Styleable, ?> other = (CssMetaData<? extends Styleable, ?>) obj;
if ((this.property == null) ? (other.property != null) : !this.property.equals(other.property)) {
return false;
}
return true;
}
@Override
public int hashCode() {
int hash = 3;
hash = 19 * hash + (this.property != null ? this.property.hashCode() : 0);
return hash;
}
@Override public String toString() {
return new StringBuilder("CSSProperty {")
.append("property: ").append(property)
.append(", converter: ").append(converter.toString())
.append(", initalValue: ").append(String.valueOf(initialValue))
.append(", inherits: ").append(inherits)
.append(", subProperties: ").append(
(subProperties != null) ? subProperties.toString() : "[]")
.append("}").toString();
}
}
