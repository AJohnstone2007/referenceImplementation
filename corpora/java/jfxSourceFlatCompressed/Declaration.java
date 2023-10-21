package javafx.css;
import com.sun.javafx.css.ParsedValueImpl;
import javafx.css.converter.URLConverter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
final public class Declaration {
final String property;
final ParsedValue parsedValue;
final boolean important;
Rule rule;
Declaration(final String propertyName, final ParsedValue parsedValue,
final boolean important) {
this.property = propertyName;
this.parsedValue = parsedValue;
this.important = important;
if (propertyName == null) {
throw new IllegalArgumentException("propertyName cannot be null");
}
if (parsedValue == null) {
throw new IllegalArgumentException("parsedValue cannot be null");
}
}
public ParsedValue getParsedValue() {
return parsedValue;
}
public String getProperty() {
return property;
}
public Rule getRule() {
return rule;
}
public final boolean isImportant() {
return important;
}
private StyleOrigin getOrigin() {
Rule rule = getRule();
if (rule != null) {
return rule.getOrigin();
}
return null;
}
@Override public boolean equals(Object obj) {
if (this == obj) {
return true;
}
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final Declaration other = (Declaration) obj;
if (this.important != other.important) {
return false;
}
if (this.getOrigin() != other.getOrigin()) {
return false;
}
if ((this.property == null) ? (other.property != null) : !this.property.equals(other.property)) {
return false;
}
if (this.parsedValue != other.parsedValue && (this.parsedValue == null || !this.parsedValue.equals(other.parsedValue))) {
return false;
}
return true;
}
@Override public int hashCode() {
int hash = 5;
hash = 89 * hash + (this.property != null ? this.property.hashCode() : 0);
hash = 89 * hash + (this.parsedValue != null ? this.parsedValue.hashCode() : 0);
hash = 89 * hash + (this.important ? 1 : 0);
return hash;
}
@Override public String toString() {
StringBuilder sbuf = new StringBuilder(property);
sbuf.append(": ");
sbuf.append(parsedValue);
if (important) sbuf.append(" !important");
return sbuf.toString();
}
void fixUrl(String stylesheetUrl) {
if (stylesheetUrl == null) return;
final StyleConverter converter = parsedValue.getConverter();
if (converter == URLConverter.getInstance()) {
final ParsedValue[] values = (ParsedValue[])parsedValue.getValue();
values[1] = new ParsedValueImpl<String,String>(stylesheetUrl, null);
} else if (converter == URLConverter.SequenceConverter.getInstance()) {
final ParsedValue<ParsedValue[], String>[] layers =
(ParsedValue<ParsedValue[], String>[])parsedValue.getValue();
for (int layer = 0; layer < layers.length; layer++) {
final ParsedValue[] values = layers[layer].getValue();
values[1] = new ParsedValueImpl<String,String>(stylesheetUrl, null);
}
}
}
final void writeBinary(final DataOutputStream os, final StyleConverter.StringStore stringStore)
throws IOException
{
if (parsedValue instanceof ParsedValueImpl) {
os.writeShort(stringStore.addString(getProperty()));
((ParsedValueImpl)parsedValue).writeBinary(os,stringStore);
os.writeBoolean(isImportant());
}
}
static Declaration readBinary(int bssVersion, DataInputStream is, String[] strings)
throws IOException
{
final String propertyName = strings[is.readShort()];
final ParsedValueImpl parsedValue = ParsedValueImpl.readBinary(bssVersion,is,strings);
final boolean important = is.readBoolean();
return new Declaration(propertyName, parsedValue, important);
}
}
