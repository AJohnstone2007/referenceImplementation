package javafx.scene.control;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableProperty;
import com.sun.javafx.binding.ExpressionHelper;
import com.sun.javafx.collections.ListListenerHelper;
import com.sun.javafx.collections.NonIterableChange;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.css.Styleable;
import javafx.scene.AccessibleRole;
public class TextArea extends TextInputControl {
private static final class TextAreaContent implements Content {
private ExpressionHelper<String> helper = null;
private ArrayList<StringBuilder> paragraphs = new ArrayList<StringBuilder>();
private int contentLength = 0;
private ParagraphList paragraphList = new ParagraphList();
private ListListenerHelper<CharSequence> listenerHelper;
private TextAreaContent() {
paragraphs.add(new StringBuilder(DEFAULT_PARAGRAPH_CAPACITY));
paragraphList.content = this;
}
@Override public String get(int start, int end) {
int length = end - start;
StringBuilder textBuilder = new StringBuilder(length);
int paragraphCount = paragraphs.size();
int paragraphIndex = 0;
int offset = start;
while (paragraphIndex < paragraphCount) {
StringBuilder paragraph = paragraphs.get(paragraphIndex);
int count = paragraph.length() + 1;
if (offset < count) {
break;
}
offset -= count;
paragraphIndex++;
}
StringBuilder paragraph = paragraphs.get(paragraphIndex);
int i = 0;
while (i < length) {
if (offset == paragraph.length()
&& i < contentLength) {
textBuilder.append('\n');
paragraph = paragraphs.get(++paragraphIndex);
offset = 0;
} else {
textBuilder.append(paragraph.charAt(offset++));
}
i++;
}
return textBuilder.toString();
}
@Override
@SuppressWarnings("unchecked")
public void insert(int index, String text, boolean notifyListeners) {
if (index < 0
|| index > contentLength) {
throw new IndexOutOfBoundsException();
}
if (text == null) {
throw new IllegalArgumentException();
}
text = TextInputControl.filterInput(text, false, false);
int length = text.length();
if (length > 0) {
ArrayList<StringBuilder> lines = new ArrayList<StringBuilder>();
StringBuilder line = new StringBuilder(DEFAULT_PARAGRAPH_CAPACITY);
for (int i = 0; i < length; i++) {
char c = text.charAt(i);
if (c == '\n') {
lines.add(line);
line = new StringBuilder(DEFAULT_PARAGRAPH_CAPACITY);
} else {
line.append(c);
}
}
lines.add(line);
int paragraphIndex = paragraphs.size();
int offset = contentLength + 1;
StringBuilder paragraph = null;
do {
paragraph = paragraphs.get(--paragraphIndex);
offset -= paragraph.length() + 1;
} while (index < offset);
int start = index - offset;
int n = lines.size();
if (n == 1) {
paragraph.insert(start, line);
fireParagraphListChangeEvent(paragraphIndex, paragraphIndex + 1,
Collections.singletonList((CharSequence)paragraph));
} else {
int end = paragraph.length();
CharSequence trailingText = paragraph.subSequence(start, end);
paragraph.delete(start, end);
StringBuilder first = lines.get(0);
paragraph.insert(start, first);
line.append(trailingText);
fireParagraphListChangeEvent(paragraphIndex, paragraphIndex + 1,
Collections.singletonList((CharSequence)paragraph));
paragraphs.addAll(paragraphIndex + 1, lines.subList(1, n));
fireParagraphListChangeEvent(paragraphIndex + 1, paragraphIndex + n,
Collections.EMPTY_LIST);
}
contentLength += length;
if (notifyListeners) {
ExpressionHelper.fireValueChangedEvent(helper);
}
}
}
@Override public void delete(int start, int end, boolean notifyListeners) {
if (start > end) {
throw new IllegalArgumentException();
}
if (start < 0
|| end > contentLength) {
throw new IndexOutOfBoundsException();
}
int length = end - start;
if (length > 0) {
int paragraphIndex = paragraphs.size();
int offset = contentLength + 1;
StringBuilder paragraph = null;
do {
paragraph = paragraphs.get(--paragraphIndex);
offset -= paragraph.length() + 1;
} while (end < offset);
int trailingParagraphIndex = paragraphIndex;
int trailingOffset = offset;
StringBuilder trailingParagraph = paragraph;
paragraphIndex++;
offset += paragraph.length() + 1;
do {
paragraph = paragraphs.get(--paragraphIndex);
offset -= paragraph.length() + 1;
} while (start < offset);
int leadingParagraphIndex = paragraphIndex;
int leadingOffset = offset;
StringBuilder leadingParagraph = paragraph;
if (leadingParagraphIndex == trailingParagraphIndex) {
leadingParagraph.delete(start - leadingOffset,
end - leadingOffset);
fireParagraphListChangeEvent(leadingParagraphIndex, leadingParagraphIndex + 1,
Collections.singletonList((CharSequence)leadingParagraph));
} else {
CharSequence leadingSegment = leadingParagraph.subSequence(0,
start - leadingOffset);
int trailingSegmentLength = (start + length) - trailingOffset;
trailingParagraph.delete(0, trailingSegmentLength);
fireParagraphListChangeEvent(trailingParagraphIndex, trailingParagraphIndex + 1,
Collections.singletonList((CharSequence)trailingParagraph));
if (trailingParagraphIndex - leadingParagraphIndex > 0) {
List<CharSequence> removed = new ArrayList<CharSequence>(paragraphs.subList(leadingParagraphIndex,
trailingParagraphIndex));
paragraphs.subList(leadingParagraphIndex,
trailingParagraphIndex).clear();
fireParagraphListChangeEvent(leadingParagraphIndex, leadingParagraphIndex,
removed);
}
trailingParagraph.insert(0, leadingSegment);
fireParagraphListChangeEvent(leadingParagraphIndex, leadingParagraphIndex + 1,
Collections.singletonList((CharSequence)leadingParagraph));
}
contentLength -= length;
if (notifyListeners) {
ExpressionHelper.fireValueChangedEvent(helper);
}
}
}
@Override public int length() {
return contentLength;
}
@Override public String get() {
return get(0, length());
}
@Override public void addListener(ChangeListener<? super String> changeListener) {
helper = ExpressionHelper.addListener(helper, this, changeListener);
}
@Override public void removeListener(ChangeListener<? super String> changeListener) {
helper = ExpressionHelper.removeListener(helper, changeListener);
}
@Override public String getValue() {
return get();
}
@Override public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
private void fireParagraphListChangeEvent(int from, int to, List<CharSequence> removed) {
ParagraphListChange change = new ParagraphListChange(paragraphList, from, to, removed);
ListListenerHelper.fireValueChangedEvent(listenerHelper, change);
}
}
private static final class ParagraphList extends AbstractList<CharSequence>
implements ObservableList<CharSequence> {
private TextAreaContent content;
@Override
public CharSequence get(int index) {
return content.paragraphs.get(index);
}
@Override
public boolean addAll(Collection<? extends CharSequence> paragraphs) {
throw new UnsupportedOperationException();
}
@Override
public boolean addAll(CharSequence... paragraphs) {
throw new UnsupportedOperationException();
}
@Override
public boolean setAll(Collection<? extends CharSequence> paragraphs) {
throw new UnsupportedOperationException();
}
@Override
public boolean setAll(CharSequence... paragraphs) {
throw new UnsupportedOperationException();
}
@Override
public int size() {
return content.paragraphs.size();
}
@Override
public void addListener(ListChangeListener<? super CharSequence> listener) {
content.listenerHelper = ListListenerHelper.addListener(content.listenerHelper, listener);
}
@Override
public void removeListener(ListChangeListener<? super CharSequence> listener) {
content.listenerHelper = ListListenerHelper.removeListener(content.listenerHelper, listener);
}
@Override
public boolean removeAll(CharSequence... elements) {
throw new UnsupportedOperationException();
}
@Override
public boolean retainAll(CharSequence... elements) {
throw new UnsupportedOperationException();
}
@Override
public void remove(int from, int to) {
throw new UnsupportedOperationException();
}
@Override
public void addListener(InvalidationListener listener) {
content.listenerHelper = ListListenerHelper.addListener(content.listenerHelper, listener);
}
@Override
public void removeListener(InvalidationListener listener) {
content.listenerHelper = ListListenerHelper.removeListener(content.listenerHelper, listener);
}
}
private static final class ParagraphListChange extends NonIterableChange<CharSequence> {
private List<CharSequence> removed;
protected ParagraphListChange(ObservableList<CharSequence> list, int from, int to,
List<CharSequence> removed) {
super(from, to, list);
this.removed = removed;
}
@Override
public List<CharSequence> getRemoved() {
return removed;
}
@Override
protected int[] getPermutation() {
return new int[0];
}
};
public static final int DEFAULT_PREF_COLUMN_COUNT = 40;
public static final int DEFAULT_PREF_ROW_COUNT = 10;
private static final int DEFAULT_PARAGRAPH_CAPACITY = 32;
public TextArea() {
this("");
}
public TextArea(String text) {
super(new TextAreaContent());
getStyleClass().add("text-area");
setAccessibleRole(AccessibleRole.TEXT_AREA);
setText(text);
}
@Override final void textUpdated() {
setScrollTop(0);
setScrollLeft(0);
}
public ObservableList<CharSequence> getParagraphs() {
return ((TextAreaContent)getContent()).paragraphList;
}
private BooleanProperty wrapText = new StyleableBooleanProperty(false) {
@Override public Object getBean() {
return TextArea.this;
}
@Override public String getName() {
return "wrapText";
}
@Override public CssMetaData<TextArea,Boolean> getCssMetaData() {
return StyleableProperties.WRAP_TEXT;
}
};
public final BooleanProperty wrapTextProperty() { return wrapText; }
public final boolean isWrapText() { return wrapText.getValue(); }
public final void setWrapText(boolean value) { wrapText.setValue(value); }
private IntegerProperty prefColumnCount = new StyleableIntegerProperty(DEFAULT_PREF_COLUMN_COUNT) {
private int oldValue = get();
@Override
protected void invalidated() {
int value = get();
if (value < 0) {
if (isBound()) {
unbind();
}
set(oldValue);
throw new IllegalArgumentException("value cannot be negative.");
}
oldValue = value;
}
@Override public CssMetaData<TextArea,Number> getCssMetaData() {
return StyleableProperties.PREF_COLUMN_COUNT;
}
@Override
public Object getBean() {
return TextArea.this;
}
@Override
public String getName() {
return "prefColumnCount";
}
};
public final IntegerProperty prefColumnCountProperty() { return prefColumnCount; }
public final int getPrefColumnCount() { return prefColumnCount.getValue(); }
public final void setPrefColumnCount(int value) { prefColumnCount.setValue(value); }
private IntegerProperty prefRowCount = new StyleableIntegerProperty(DEFAULT_PREF_ROW_COUNT) {
private int oldValue = get();
@Override
protected void invalidated() {
int value = get();
if (value < 0) {
if (isBound()) {
unbind();
}
set(oldValue);
throw new IllegalArgumentException("value cannot be negative.");
}
oldValue = value;
}
@Override public CssMetaData<TextArea,Number> getCssMetaData() {
return StyleableProperties.PREF_ROW_COUNT;
}
@Override
public Object getBean() {
return TextArea.this;
}
@Override
public String getName() {
return "prefRowCount";
}
};
public final IntegerProperty prefRowCountProperty() { return prefRowCount; }
public final int getPrefRowCount() { return prefRowCount.getValue(); }
public final void setPrefRowCount(int value) { prefRowCount.setValue(value); }
private DoubleProperty scrollTop = new SimpleDoubleProperty(this, "scrollTop", 0);
public final DoubleProperty scrollTopProperty() { return scrollTop; }
public final double getScrollTop() { return scrollTop.getValue(); }
public final void setScrollTop(double value) { scrollTop.setValue(value); }
private DoubleProperty scrollLeft = new SimpleDoubleProperty(this, "scrollLeft", 0);
public final DoubleProperty scrollLeftProperty() { return scrollLeft; }
public final double getScrollLeft() { return scrollLeft.getValue(); }
public final void setScrollLeft(double value) { scrollLeft.setValue(value); }
@Override protected Skin<?> createDefaultSkin() {
return new TextAreaSkin(this);
}
@Override
String filterInput(String text) {
return TextInputControl.filterInput(text, false, false);
}
private static class StyleableProperties {
private static final CssMetaData<TextArea,Number> PREF_COLUMN_COUNT =
new CssMetaData<TextArea,Number>("-fx-pref-column-count",
SizeConverter.getInstance(), DEFAULT_PREF_COLUMN_COUNT) {
@Override
public boolean isSettable(TextArea n) {
return !n.prefColumnCount.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TextArea n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.prefColumnCountProperty();
}
};
private static final CssMetaData<TextArea,Number> PREF_ROW_COUNT =
new CssMetaData<TextArea,Number>("-fx-pref-row-count",
SizeConverter.getInstance(), DEFAULT_PREF_ROW_COUNT) {
@Override
public boolean isSettable(TextArea n) {
return !n.prefRowCount.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TextArea n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.prefRowCountProperty();
}
};
private static final CssMetaData<TextArea,Boolean> WRAP_TEXT =
new CssMetaData<TextArea,Boolean>("-fx-wrap-text",
StyleConverter.getBooleanConverter(), false) {
@Override
public boolean isSettable(TextArea n) {
return !n.wrapText.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(TextArea n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.wrapTextProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(TextInputControl.getClassCssMetaData());
styleables.add(PREF_COLUMN_COUNT);
styleables.add(PREF_ROW_COUNT);
styleables.add(WRAP_TEXT);
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
}
