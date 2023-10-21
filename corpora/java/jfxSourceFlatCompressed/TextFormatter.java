package javafx.scene.control;
import com.sun.javafx.scene.control.FormatterAccessor;
import javafx.beans.NamedArg;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.util.StringConverter;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
public class TextFormatter<V> {
private final StringConverter<V> valueConverter;
private final UnaryOperator<Change> filter;
private Consumer<TextFormatter<?>> textUpdater;
public static final StringConverter<String> IDENTITY_STRING_CONVERTER = new StringConverter<String>() {
@Override
public String toString(String object) {
return object == null ? "" : object;
}
@Override
public String fromString(String string) {
return string;
}
};
public TextFormatter(@NamedArg("filter") UnaryOperator<Change> filter) {
this(null, null, filter);
}
public TextFormatter(@NamedArg("valueConverter") StringConverter<V> valueConverter,
@NamedArg("defaultValue") V defaultValue, @NamedArg("filter") UnaryOperator<Change> filter) {
this.filter = filter;
this.valueConverter = valueConverter;
setValue(defaultValue);
}
public TextFormatter(@NamedArg("valueConverter") StringConverter<V> valueConverter, @NamedArg("defaultValue") V defaultValue) {
this(valueConverter, defaultValue, null);
}
public TextFormatter(@NamedArg("valueConverter") StringConverter<V> valueConverter) {
this(valueConverter, null, null);
}
public final StringConverter<V> getValueConverter() {
return valueConverter;
}
public final UnaryOperator<Change> getFilter() {
return filter;
}
private final ObjectProperty<V> value = new ObjectPropertyBase<V>() {
@Override
public Object getBean() {
return TextFormatter.this;
}
@Override
public String getName() {
return "value";
}
@Override
protected void invalidated() {
if (valueConverter == null && get() != null) {
if (isBound()) {
unbind();
}
throw new IllegalStateException("Value changes are not supported when valueConverter is not set");
}
updateText();
}
};
public final ObjectProperty<V> valueProperty() {
return value;
}
public final void setValue(V value) {
if (valueConverter == null && value != null) {
throw new IllegalStateException("Value changes are not supported when valueConverter is not set");
}
this.value.set(value);
}
public final V getValue() {
return value.get();
}
private void updateText() {
if (textUpdater != null) {
textUpdater.accept(this);
}
}
void bindToControl(Consumer<TextFormatter<?>> updater) {
if (textUpdater != null) {
throw new IllegalStateException("Formatter is already used in other control");
}
this.textUpdater = updater;
}
void unbindFromControl() {
this.textUpdater = null;
}
void updateValue(String text) {
if (valueConverter != null && !value.isBound()) {
try {
V v = valueConverter.fromString(text);
setValue(v);
} catch (Exception e) {
updateText();
}
}
}
public static final class Change implements Cloneable {
private final FormatterAccessor accessor;
private Control control;
int start;
int end;
String text;
int anchor;
int caret;
Change(Control control, FormatterAccessor accessor, int anchor, int caret) {
this(control, accessor, caret, caret, "", anchor, caret);
}
Change(Control control, FormatterAccessor accessor, int start, int end, String text) {
this(control, accessor, start, end, text, start + text.length(), start + text.length());
}
Change(Control control, FormatterAccessor accessor, int start, int end, String text, int anchor, int caret) {
this.control = control;
this.accessor = accessor;
this.start = start;
this.end = end;
this.text = text;
this.anchor = anchor;
this.caret = caret;
}
public final Control getControl() { return control; }
public final int getRangeStart() { return start; }
public final int getRangeEnd() { return end; }
public final void setRange(int start, int end) {
int length = accessor.getTextLength();
if (start < 0 || start > length || end < 0 || end > length) {
throw new IndexOutOfBoundsException();
}
this.start = start;
this.end = end;
}
public final int getCaretPosition() { return caret; }
public final int getAnchor() { return anchor; }
public final int getControlCaretPosition() { return accessor.getCaret();}
public final int getControlAnchor() { return accessor.getAnchor(); }
public final void selectRange(int newAnchor, int newCaretPosition) {
if (newAnchor < 0 || newAnchor > accessor.getTextLength() - (end - start) + text.length()
|| newCaretPosition < 0 || newCaretPosition > accessor.getTextLength() - (end - start) + text.length()) {
throw new IndexOutOfBoundsException();
}
anchor = newAnchor;
caret = newCaretPosition;
}
public final IndexRange getSelection() {
return IndexRange.normalize(anchor, caret);
}
public final void setAnchor(int newAnchor) {
if (newAnchor < 0 || newAnchor > accessor.getTextLength() - (end - start) + text.length()) {
throw new IndexOutOfBoundsException();
}
anchor = newAnchor;
}
public final void setCaretPosition(int newCaretPosition) {
if (newCaretPosition < 0 || newCaretPosition > accessor.getTextLength() - (end - start) + text.length()) {
throw new IndexOutOfBoundsException();
}
caret = newCaretPosition;
}
public final String getText() { return text; }
public final void setText(String value) {
if (value == null) throw new NullPointerException();
text = value;
}
public final String getControlText() {
return accessor.getText(0, accessor.getTextLength());
}
public final String getControlNewText() {
return accessor.getText(0, start) + text + accessor.getText(end, accessor.getTextLength());
}
public final boolean isAdded() { return !text.isEmpty(); }
public final boolean isDeleted() { return start != end; }
public final boolean isReplaced() {
return isAdded() && isDeleted();
}
public final boolean isContentChange() {
return isAdded() || isDeleted();
}
@Override
public String toString() {
StringBuilder builder = new StringBuilder("TextInputControl.Change [");
if (isReplaced()) {
builder.append(" replaced \"").append(accessor.getText(start, end)).append("\" with \"").append(text).
append("\" at (").append(start).append(", ").append(end).append(")");
} else if (isDeleted()) {
builder.append(" deleted \"").append(accessor.getText(start, end)).
append("\" at (").append(start).append(", ").append(end).append(")");
} else if (isAdded()) {
builder.append(" added \"").append(text).append("\" at ").append(start);
}
if (isAdded() || isDeleted()) {
builder.append("; ");
} else {
builder.append(" ");
}
builder.append("new selection (anchor, caret): [").append(anchor).append(", ").append(caret).append("]");
builder.append(" ]");
return builder.toString();
}
@Override
public Change clone() {
try {
return (Change) super.clone();
} catch (CloneNotSupportedException e) {
throw new RuntimeException(e);
}
}
}
}
