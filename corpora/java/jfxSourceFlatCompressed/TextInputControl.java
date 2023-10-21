package javafx.scene.control;
import com.sun.javafx.scene.control.FormatterAccessor;
import javafx.beans.DefaultProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Font;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.util.Utils;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.scene.NodeHelper;
import javafx.util.StringConverter;
@DefaultProperty("text")
public abstract class TextInputControl extends Control {
protected interface Content extends ObservableStringValue {
public String get(int start, int end);
public void insert(int index, String text, boolean notifyListeners);
public void delete(int start, int end, boolean notifyListeners);
public int length();
}
private boolean blockSelectedTextUpdate;
protected TextInputControl(final Content content) {
this.content = content;
content.addListener(observable -> {
if (content.length() > 0) {
text.textIsNull = false;
}
text.controlContentHasChanged();
});
length.bind(new IntegerBinding() {
{ bind(text); }
@Override protected int computeValue() {
String txt = text.get();
return txt == null ? 0 : txt.length();
}
});
selection.addListener((ob, o, n) -> updateSelectedText());
text.addListener((ob, o, n) -> updateSelectedText());
focusedProperty().addListener((ob, o, n) -> {
if (n) {
if (getTextFormatter() != null) {
updateText(getTextFormatter());
}
} else {
commitValue();
}
});
getStyleClass().add("text-input");
}
private void updateSelectedText() {
if (!blockSelectedTextUpdate) {
String txt = text.get();
IndexRange sel = selection.get();
if (txt == null || sel == null) {
selectedText.set("");
} else {
int start = sel.getStart();
int end = sel.getEnd();
selectedText.set(txt.substring(start, end));
}
}
}
public final ObjectProperty<Font> fontProperty() {
if (font == null) {
font = new StyleableObjectProperty<Font>(Font.getDefault()) {
private boolean fontSetByCss = false;
@Override
public void applyStyle(StyleOrigin newOrigin, Font value) {
try {
fontSetByCss = true;
super.applyStyle(newOrigin, value);
} catch(Exception e) {
throw e;
} finally {
fontSetByCss = false;
}
}
@Override
public void set(Font value) {
final Font oldValue = get();
if (value == null ? oldValue == null : value.equals(oldValue)) {
return;
}
super.set(value);
}
@Override
protected void invalidated() {
if(fontSetByCss == false) {
NodeHelper.reapplyCSS(TextInputControl.this);
}
}
@Override
public CssMetaData<TextInputControl,Font> getCssMetaData() {
return StyleableProperties.FONT;
}
@Override
public Object getBean() {
return TextInputControl.this;
}
@Override
public String getName() {
return "font";
}
};
}
return font;
}
private ObjectProperty<Font> font;
public final void setFont(Font value) { fontProperty().setValue(value); }
public final Font getFont() { return font == null ? Font.getDefault() : font.getValue(); }
private StringProperty promptText = new SimpleStringProperty(this, "promptText", "") {
@Override protected void invalidated() {
String txt = get();
if (txt != null && txt.contains("\n")) {
txt = txt.replace("\n", "");
set(txt);
}
}
};
public final StringProperty promptTextProperty() { return promptText; }
public final String getPromptText() { return promptText.get(); }
public final void setPromptText(String value) { promptText.set(value); }
private final ObjectProperty<TextFormatter<?>> textFormatter = new ObjectPropertyBase<TextFormatter<?>>() {
private TextFormatter<?> oldFormatter = null;
@Override
public Object getBean() {
return TextInputControl.this;
}
@Override
public String getName() {
return "textFormatter";
}
@Override
protected void invalidated() {
final TextFormatter<?> formatter = get();
try {
if (formatter != null) {
try {
formatter.bindToControl(f -> updateText(f));
} catch (IllegalStateException e) {
if (isBound()) {
unbind();
}
set(null);
throw e;
}
if (!isFocused()) {
updateText(get());
}
}
if (oldFormatter != null) {
oldFormatter.unbindFromControl();
}
} finally {
oldFormatter = formatter;
}
}
};
public final ObjectProperty<TextFormatter<?>> textFormatterProperty() { return textFormatter; }
public final TextFormatter<?> getTextFormatter() { return textFormatter.get(); }
public final void setTextFormatter(TextFormatter<?> value) { textFormatter.set(value); }
private final Content content;
protected final Content getContent() {
return content;
}
private TextProperty text = new TextProperty();
public final String getText() { return text.get(); }
public final void setText(String value) { text.set(value); }
public final StringProperty textProperty() { return text; }
private ReadOnlyIntegerWrapper length = new ReadOnlyIntegerWrapper(this, "length");
public final int getLength() { return length.get(); }
public final ReadOnlyIntegerProperty lengthProperty() { return length.getReadOnlyProperty(); }
private BooleanProperty editable = new SimpleBooleanProperty(this, "editable", true) {
@Override protected void invalidated() {
pseudoClassStateChanged(PSEUDO_CLASS_READONLY, ! get());
}
};
public final boolean isEditable() { return editable.getValue(); }
public final void setEditable(boolean value) { editable.setValue(value); }
public final BooleanProperty editableProperty() { return editable; }
private ReadOnlyObjectWrapper<IndexRange> selection = new ReadOnlyObjectWrapper<IndexRange>(this, "selection", new IndexRange(0, 0));
public final IndexRange getSelection() { return selection.getValue(); }
public final ReadOnlyObjectProperty<IndexRange> selectionProperty() { return selection.getReadOnlyProperty(); }
private ReadOnlyStringWrapper selectedText = new ReadOnlyStringWrapper(this, "selectedText");
public final String getSelectedText() { return selectedText.get(); }
public final ReadOnlyStringProperty selectedTextProperty() { return selectedText.getReadOnlyProperty(); }
private ReadOnlyIntegerWrapper anchor = new ReadOnlyIntegerWrapper(this, "anchor", 0);
public final int getAnchor() { return anchor.get(); }
public final ReadOnlyIntegerProperty anchorProperty() { return anchor.getReadOnlyProperty(); }
private ReadOnlyIntegerWrapper caretPosition = new ReadOnlyIntegerWrapper(this, "caretPosition", 0);
public final int getCaretPosition() { return caretPosition.get(); }
public final ReadOnlyIntegerProperty caretPositionProperty() { return caretPosition.getReadOnlyProperty(); }
private UndoRedoChange undoChangeHead = new UndoRedoChange();
private UndoRedoChange undoChange = undoChangeHead;
private boolean createNewUndoRecord = false;
private final ReadOnlyBooleanWrapper undoable = new ReadOnlyBooleanWrapper(this, "undoable", false);
public final boolean isUndoable() { return undoable.get(); }
public final ReadOnlyBooleanProperty undoableProperty() { return undoable.getReadOnlyProperty(); }
private final ReadOnlyBooleanWrapper redoable = new ReadOnlyBooleanWrapper(this, "redoable", false);
public final boolean isRedoable() { return redoable.get(); }
public final ReadOnlyBooleanProperty redoableProperty() { return redoable.getReadOnlyProperty(); }
public String getText(int start, int end) {
if (start > end) {
throw new IllegalArgumentException("The start must be <= the end");
}
if (start < 0
|| end > getLength()) {
throw new IndexOutOfBoundsException();
}
return getContent().get(start, end);
}
public void appendText(String text) {
insertText(getLength(), text);
}
public void insertText(int index, String text) {
replaceText(index, index, text);
}
public void deleteText(IndexRange range) {
replaceText(range, "");
}
public void deleteText(int start, int end) {
replaceText(start, end, "");
}
public void replaceText(IndexRange range, String text) {
final int start = range.getStart();
final int end = start + range.getLength();
replaceText(start, end, text);
}
public void replaceText(final int start, final int end, final String text) {
if (start > end) {
throw new IllegalArgumentException();
}
if (text == null) {
throw new NullPointerException();
}
if (start < 0
|| end > getLength()) {
throw new IndexOutOfBoundsException();
}
if (!this.text.isBound()) {
final int oldLength = getLength();
TextFormatter<?> formatter = getTextFormatter();
TextFormatter.Change change;
if (formatter != null && formatter.getFilter() != null) {
change = new TextFormatter.Change(this, getFormatterAccessor(), start, end, text);
change = formatter.getFilter().apply(change);
if (change == null) {
return;
}
} else {
change = new TextFormatter.Change(this, getFormatterAccessor(), start, end, filterInput(text));
}
updateContent(change, oldLength == 0);
}
}
private void updateContent(TextFormatter.Change change, boolean forceNewUndoRecord) {
final boolean nonEmptySelection = getSelection().getLength() > 0;
String oldText = getText(change.start, change.end);
int adjustmentAmount = replaceText(change.start, change.end, change.text, change.getAnchor(), change.getCaretPosition());
String newText = getText(change.start, change.start + change.text.length() - adjustmentAmount);
if (newText.equals(oldText)) {
return;
}
int endOfUndoChange = undoChange == undoChangeHead ? -1 : undoChange.start + undoChange.newText.length();
boolean isNewSpaceChar = false;
if (newText.equals(" ")) {
if (!UndoRedoChange.isSpaceCharSequence()) {
isNewSpaceChar = true;
UndoRedoChange.setSpaceCharSequence(true);
}
} else {
UndoRedoChange.setSpaceCharSequence(false);
}
if (createNewUndoRecord || nonEmptySelection || endOfUndoChange == -1 || forceNewUndoRecord ||
isNewSpaceChar || UndoRedoChange.hasChangeDurationElapsed() ||
(endOfUndoChange != change.start && endOfUndoChange != change.end) || change.end - change.start > 0) {
undoChange = undoChange.add(change.start, oldText, newText);
} else if (change.start != change.end && change.text.isEmpty()) {
if (undoChange.newText.length() > 0) {
undoChange.newText = undoChange.newText.substring(0, change.start - undoChange.start);
if (undoChange.newText.isEmpty()) {
undoChange = undoChange.discard();
}
} else {
if (change.start == endOfUndoChange) {
undoChange.oldText += oldText;
} else {
undoChange.oldText = oldText + undoChange.oldText;
undoChange.start--;
}
}
} else {
undoChange.newText += newText;
}
updateUndoRedoState();
}
public void cut() {
copy();
IndexRange selection = getSelection();
deleteText(selection.getStart(), selection.getEnd());
}
public void copy() {
final String selectedText = getSelectedText();
if (selectedText.length() > 0) {
final ClipboardContent content = new ClipboardContent();
content.putString(selectedText);
Clipboard.getSystemClipboard().setContent(content);
}
}
public void paste() {
final Clipboard clipboard = Clipboard.getSystemClipboard();
if (clipboard.hasString()) {
final String text = clipboard.getString();
if (text != null) {
createNewUndoRecord = true;
try {
replaceSelection(text);
} finally {
createNewUndoRecord = false;
}
}
}
}
public void selectBackward() {
if (getCaretPosition() > 0 && getLength() > 0) {
if (charIterator == null) {
charIterator = BreakIterator.getCharacterInstance();
}
charIterator.setText(getText());
selectRange(getAnchor(), charIterator.preceding(getCaretPosition()));
}
}
public void selectForward() {
final int textLength = getLength();
if (textLength > 0 && getCaretPosition() < textLength) {
if (charIterator == null) {
charIterator = BreakIterator.getCharacterInstance();
}
charIterator.setText(getText());
selectRange(getAnchor(), charIterator.following(getCaretPosition()));
}
}
private BreakIterator charIterator;
private BreakIterator wordIterator;
public void previousWord() {
previousWord(false);
}
public void nextWord() {
nextWord(false);
}
public void endOfNextWord() {
endOfNextWord(false);
}
public void selectPreviousWord() {
previousWord(true);
}
public void selectNextWord() {
nextWord(true);
}
public void selectEndOfNextWord() {
endOfNextWord(true);
}
private void previousWord(boolean select) {
final int textLength = getLength();
final String text = getText();
if (textLength <= 0) {
return;
}
if (wordIterator == null) {
wordIterator = BreakIterator.getWordInstance();
}
wordIterator.setText(text);
int pos = wordIterator.preceding(Utils.clamp(0, getCaretPosition(), textLength));
while (pos != BreakIterator.DONE &&
!Character.isLetterOrDigit(text.charAt(Utils.clamp(0, pos, textLength-1)))) {
pos = wordIterator.preceding(Utils.clamp(0, pos, textLength));
}
selectRange(select ? getAnchor() : pos, pos);
}
private void nextWord(boolean select) {
final int textLength = getLength();
final String text = getText();
if (textLength <= 0) {
return;
}
if (wordIterator == null) {
wordIterator = BreakIterator.getWordInstance();
}
wordIterator.setText(text);
int last = wordIterator.following(Utils.clamp(0, getCaretPosition(), textLength-1));
int current = wordIterator.next();
while (current != BreakIterator.DONE) {
for (int p=last; p<=current; p++) {
char ch = text.charAt(Utils.clamp(0, p, textLength-1));
if (ch != ' ' && ch != '\t') {
if (select) {
selectRange(getAnchor(), p);
} else {
selectRange(p, p);
}
return;
}
}
last = current;
current = wordIterator.next();
}
if (select) {
selectRange(getAnchor(), textLength);
} else {
end();
}
}
private void endOfNextWord(boolean select) {
final int textLength = getLength();
final String text = getText();
if (textLength <= 0) {
return;
}
if (wordIterator == null) {
wordIterator = BreakIterator.getWordInstance();
}
wordIterator.setText(text);
int last = wordIterator.following(Utils.clamp(0, getCaretPosition(), textLength));
int current = wordIterator.next();
while (current != BreakIterator.DONE) {
for (int p=last; p<=current; p++) {
if (!Character.isLetterOrDigit(text.charAt(Utils.clamp(0, p, textLength-1)))) {
if (select) {
selectRange(getAnchor(), p);
} else {
selectRange(p, p);
}
return;
}
}
last = current;
current = wordIterator.next();
}
if (select) {
selectRange(getAnchor(), textLength);
} else {
end();
}
}
public void selectAll() {
selectRange(0, getLength());
}
public void home() {
selectRange(0, 0);
}
public void end() {
final int textLength = getLength();
if (textLength > 0) {
selectRange(textLength, textLength);
}
}
public void selectHome() {
selectRange(getAnchor(), 0);
}
public void selectEnd() {
final int textLength = getLength();
if (textLength > 0) selectRange(getAnchor(), textLength);
}
public boolean deletePreviousChar() {
boolean failed = true;
if (isEditable() && !isDisabled()) {
final String text = getText();
final int dot = getCaretPosition();
final int mark = getAnchor();
if (dot != mark) {
replaceSelection("");
failed = false;
} else if (dot > 0) {
int p = Character.offsetByCodePoints(text, dot, -1);
deleteText(p, dot);
failed = false;
}
}
return !failed;
}
public boolean deleteNextChar() {
boolean failed = true;
if (isEditable() && !isDisabled()) {
final int textLength = getLength();
final String text = getText();
final int dot = getCaretPosition();
final int mark = getAnchor();
if (dot != mark) {
replaceSelection("");
failed = false;
} else if (textLength > 0 && dot < textLength) {
if (charIterator == null) {
charIterator = BreakIterator.getCharacterInstance();
}
charIterator.setText(text);
int p = charIterator.following(dot);
deleteText(dot, p);
failed = false;
}
}
return !failed;
}
public void forward() {
final int textLength = getLength();
final int dot = getCaretPosition();
final int mark = getAnchor();
if (dot != mark) {
int pos = Math.max(dot, mark);
selectRange(pos, pos);
} else if (dot < textLength && textLength > 0) {
if (charIterator == null) {
charIterator = BreakIterator.getCharacterInstance();
}
charIterator.setText(getText());
int pos = charIterator.following(dot);
selectRange(pos, pos);
}
deselect();
}
public void backward() {
final int textLength = getLength();
final int dot = getCaretPosition();
final int mark = getAnchor();
if (dot != mark) {
int pos = Math.min(dot, mark);
selectRange(pos, pos);
} else if (dot > 0 && textLength > 0) {
if (charIterator == null) {
charIterator = BreakIterator.getCharacterInstance();
}
charIterator.setText(getText());
int pos = charIterator.preceding(dot);
selectRange(pos, pos);
}
deselect();
}
public void positionCaret(int pos) {
final int p = Utils.clamp(0, pos, getLength());
selectRange(p, p);
}
public void selectPositionCaret(int pos) {
selectRange(getAnchor(), Utils.clamp(0, pos, getLength()));
}
public void selectRange(int anchor, int caretPosition) {
caretPosition = Utils.clamp(0, caretPosition, getLength());
anchor = Utils.clamp(0, anchor, getLength());
TextFormatter.Change change = new TextFormatter.Change(this, getFormatterAccessor(), anchor, caretPosition);
TextFormatter<?> formatter = getTextFormatter();
if (formatter != null && formatter.getFilter() != null) {
change = formatter.getFilter().apply(change);
if (change == null) {
return;
}
}
updateContent(change, false);
}
private void doSelectRange(int anchor, int caretPosition) {
this.caretPosition.set(Utils.clamp(0, caretPosition, getLength()));
this.anchor.set(Utils.clamp(0, anchor, getLength()));
this.selection.set(IndexRange.normalize(getAnchor(), getCaretPosition()));
notifyAccessibleAttributeChanged(AccessibleAttribute.SELECTION_START);
}
public void extendSelection(int pos) {
final int p = Utils.clamp(0, pos, getLength());
final int dot = getCaretPosition();
final int mark = getAnchor();
int start = Math.min(dot, mark);
int end = Math.max(dot, mark);
if (p < start) {
selectRange(end, p);
} else {
selectRange(start, p);
}
}
public void clear() {
deselect();
if (!text.isBound()) {
setText("");
}
}
public void deselect() {
selectRange(getCaretPosition(), getCaretPosition());
}
public void replaceSelection(String replacement) {
replaceText(getSelection(), replacement);
}
public final void undo() {
if (isUndoable()) {
final int start = undoChange.start;
final String newText = undoChange.newText;
final String oldText = undoChange.oldText;
blockSelectedTextUpdate = true;
try {
if (newText != null) {
getContent().delete(start, start + newText.length(), oldText.isEmpty());
}
if (oldText != null) {
getContent().insert(start, oldText, true);
doSelectRange(start, start + oldText.length());
} else {
doSelectRange(start, start + newText.length());
}
undoChange = undoChange.prev;
} finally {
blockSelectedTextUpdate = false;
updateSelectedText();
}
}
updateUndoRedoState();
}
public final void redo() {
if (isRedoable()) {
undoChange = undoChange.next;
final int start = undoChange.start;
final String newText = undoChange.newText;
final String oldText = undoChange.oldText;
blockSelectedTextUpdate = true;
try {
if (oldText != null) {
getContent().delete(start, start + oldText.length(), newText.isEmpty());
}
if (newText != null) {
getContent().insert(start, newText, true);
doSelectRange(start + newText.length(), start + newText.length());
} else {
doSelectRange(start, start);
}
} finally {
blockSelectedTextUpdate = false;
updateSelectedText();
}
}
updateUndoRedoState();
}
void textUpdated() { }
private void resetUndoRedoState() {
undoChange = undoChangeHead;
undoChange.next = null;
updateUndoRedoState();
}
private void updateUndoRedoState() {
undoable.set(undoChange != undoChangeHead);
redoable.set(undoChange.next != null);
}
private boolean filterAndSet(String value) {
TextFormatter<?> formatter = getTextFormatter();
int length = content.length();
if (formatter != null && formatter.getFilter() != null && !text.isBound()) {
TextFormatter.Change change = new TextFormatter.Change(
TextInputControl.this, getFormatterAccessor(), 0, length, value, 0, 0);
change = formatter.getFilter().apply(change);
if (change == null) {
return false;
}
replaceText(change.start, change.end, change.text, change.getAnchor(), change.getCaretPosition());
} else {
replaceText(0, length, value, 0, 0);
}
return true;
}
private int replaceText(int start, int end, String value, int anchor, int caretPosition) {
blockSelectedTextUpdate = true;
try {
int length = getLength();
int adjustmentAmount = 0;
if (end != start) {
getContent().delete(start, end, value.isEmpty());
length -= (end - start);
}
if (value != null) {
getContent().insert(start, value, true);
adjustmentAmount = value.length() - (getLength() - length);
anchor -= adjustmentAmount;
caretPosition -= adjustmentAmount;
}
doSelectRange(anchor, caretPosition);
return adjustmentAmount;
} finally {
blockSelectedTextUpdate = false;
updateSelectedText();
}
}
private <T> void updateText(TextFormatter<T> formatter) {
T value = formatter.getValue();
StringConverter<T> converter = formatter.getValueConverter();
if (converter != null) {
String text = converter.toString(value);
if (text == null) text = "";
replaceText(0, getLength(), text, text.length(), text.length());
}
}
public final void commitValue() {
if (getTextFormatter() != null) {
getTextFormatter().updateValue(getText());
}
}
public final void cancelEdit() {
if (getTextFormatter() != null) {
updateText(getTextFormatter());
}
}
private FormatterAccessor accessor;
private FormatterAccessor getFormatterAccessor() {
if (accessor == null) {
accessor = new TextInputControlFromatterAccessor();
}
return accessor;
}
String filterInput(String text) {
return text;
}
static String filterInput(String txt, boolean stripNewlines, boolean stripTabs) {
if (containsInvalidCharacters(txt, stripNewlines, stripTabs)) {
StringBuilder s = new StringBuilder(txt.length());
for (int i=0; i<txt.length(); i++) {
final char c = txt.charAt(i);
if (!isInvalidCharacter(c, stripNewlines, stripTabs)) {
s.append(c);
}
}
txt = s.toString();
}
return txt;
}
static boolean containsInvalidCharacters(String txt, boolean newlineIllegal, boolean tabIllegal) {
for (int i=0; i<txt.length(); i++) {
final char c = txt.charAt(i);
if (isInvalidCharacter(c, newlineIllegal, tabIllegal)) return true;
}
return false;
}
private static boolean isInvalidCharacter(char c, boolean newlineIllegal, boolean tabIllegal) {
if (c == 0x7F) return true;
if (c == 0xA) return newlineIllegal;
if (c == 0x9) return tabIllegal;
if (c < 0x20) return true;
return false;
}
private class TextProperty extends StringProperty {
private ObservableValue<? extends String> observable = null;
private InvalidationListener listener = null;
private ExpressionHelper<String> helper = null;
private boolean textIsNull = false;
@Override public String get() {
return textIsNull ? null : content.get();
}
@Override public void set(String value) {
if (isBound()) {
throw new java.lang.RuntimeException("A bound value cannot be set.");
}
doSet(value);
markInvalid();
}
private void controlContentHasChanged() {
markInvalid();
notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
}
@Override public void bind(ObservableValue<? extends String> observable) {
if (observable == null) {
throw new NullPointerException("Cannot bind to null");
}
if (!observable.equals(this.observable)) {
unbind();
this.observable = observable;
if (listener == null) {
listener = new Listener();
}
this.observable.addListener(listener);
markInvalid();
doSet(observable.getValue());
}
}
@Override public void unbind() {
if (observable != null) {
doSet(observable.getValue());
observable.removeListener(listener);
observable = null;
}
}
@Override public boolean isBound() {
return observable != null;
}
@Override public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override public void addListener(ChangeListener<? super String> listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override public void removeListener(ChangeListener<? super String> listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
@Override public Object getBean() {
return TextInputControl.this;
}
@Override public String getName() {
return "text";
}
private void fireValueChangedEvent() {
ExpressionHelper.fireValueChangedEvent(helper);
}
private void markInvalid() {
fireValueChangedEvent();
}
private void doSet(String value) {
textIsNull = value == null;
if (value == null) value = "";
if (!filterAndSet(value)) return;
if (getTextFormatter() != null) {
getTextFormatter().updateValue(getText());
}
textUpdated();
resetUndoRedoState();
}
private class Listener implements InvalidationListener {
@Override
public void invalidated(Observable valueModel) {
doSet(observable.getValue());
}
}
}
static class UndoRedoChange {
static long prevRecordTime;
static final long CHANGE_DURATION = 2500;
static boolean spaceCharSequence = false;
int start;
String oldText;
String newText;
UndoRedoChange prev;
UndoRedoChange next;
UndoRedoChange() { }
public UndoRedoChange add(int start, String oldText, String newText) {
UndoRedoChange c = new UndoRedoChange();
c.start = start;
c.oldText = oldText;
c.newText = newText;
c.prev = this;
next = c;
prevRecordTime = System.currentTimeMillis();
return c;
}
static boolean hasChangeDurationElapsed() {
return (System.currentTimeMillis() - prevRecordTime > CHANGE_DURATION) ;
}
static void setSpaceCharSequence(boolean value) {
spaceCharSequence = value;
}
static boolean isSpaceCharSequence() {
return spaceCharSequence;
}
public UndoRedoChange discard() {
prev.next = next;
return prev;
}
void debugPrint() {
UndoRedoChange c = this;
System.out.print("[");
while (c != null) {
System.out.print(c.toString());
if (c.next != null) System.out.print(", ");
c = c.next;
}
System.out.println("]");
}
@Override public String toString() {
if (oldText == null && newText == null) {
return "head";
}
if (oldText.isEmpty() && !newText.isEmpty()) {
return "added '" + newText + "' at index " + start;
} else if (!oldText.isEmpty() && !newText.isEmpty()) {
return "replaced '" + oldText + "' with '" + newText + "' at index " + start;
} else {
return "deleted '" + oldText + "' at index " + start;
}
}
}
private static final PseudoClass PSEUDO_CLASS_READONLY
= PseudoClass.getPseudoClass("readonly");
private static class StyleableProperties {
private static final FontCssMetaData<TextInputControl> FONT =
new FontCssMetaData<TextInputControl>("-fx-font", Font.getDefault()) {
@Override
public boolean isSettable(TextInputControl n) {
return n.font == null || !n.font.isBound();
}
@Override
public StyleableProperty<Font> getStyleableProperty(TextInputControl n) {
return (StyleableProperty<Font>)(WritableValue<Font>)n.fontProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(FONT);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: {
String accText = getAccessibleText();
if (accText != null && !accText.isEmpty()) return accText;
String text = getText();
if (text == null || text.isEmpty()) {
text = getPromptText();
}
return text;
}
case EDITABLE: return isEditable();
case SELECTION_START: return getSelection().getStart();
case SELECTION_END: return getSelection().getEnd();
case CARET_OFFSET: return getCaretPosition();
case FONT: return getFont();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case SET_TEXT: {
String value = (String) parameters[0];
if (value != null) setText(value);
break;
}
case SET_TEXT_SELECTION: {
Integer start = (Integer) parameters[0];
Integer end = (Integer) parameters[1];
if (start != null && end != null) {
selectRange(start, end);
}
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
private class TextInputControlFromatterAccessor implements FormatterAccessor {
@Override
public int getTextLength() {
return TextInputControl.this.getLength();
}
@Override
public String getText(int begin, int end) {
return TextInputControl.this.getText(begin, end);
}
@Override
public int getCaret() {
return TextInputControl.this.getCaretPosition();
}
@Override
public int getAnchor() {
return TextInputControl.this.getAnchor();
}
}
}
