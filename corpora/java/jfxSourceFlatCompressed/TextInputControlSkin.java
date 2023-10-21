package javafx.scene.control.skin;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.TextInputControlBehavior;
import com.sun.javafx.scene.control.skin.FXVK;
import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;
import static com.sun.javafx.PlatformUtil.*;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.PaintConverter;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.AccessibleAction;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.IndexRange;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import javafx.stage.Window;
import javafx.util.Duration;
public abstract class TextInputControlSkin<T extends TextInputControl> extends SkinBase<T> {
public static enum TextUnit {
CHARACTER,
WORD,
LINE,
PARAGRAPH,
PAGE
};
public static enum Direction {
LEFT,
RIGHT,
UP,
DOWN,
BEGINNING,
END
};
static boolean preload = false;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
String s = System.getProperty("com.sun.javafx.virtualKeyboard.preload");
if (s != null) {
if (s.equalsIgnoreCase("PRERENDER")) {
preload = true;
}
}
return null;
});
}
static final boolean SHOW_HANDLES = Properties.IS_TOUCH_SUPPORTED;
private final static boolean IS_FXVK_SUPPORTED = Platform.isSupported(ConditionalFeature.VIRTUAL_KEYBOARD);
final ObservableObjectValue<FontMetrics> fontMetrics;
private ObservableBooleanValue caretVisible;
private CaretBlinking caretBlinking = new CaretBlinking(blinkProperty());
final Path caretPath = new Path();
StackPane caretHandle = null;
StackPane selectionHandle1 = null;
StackPane selectionHandle2 = null;
private int imstart;
private int imlength;
private List<Shape> imattrs = new java.util.ArrayList<Shape>();
private EventHandler<InputMethodEvent> inputMethodTextChangedHandler;
public TextInputControlSkin(final T control) {
super(control);
fontMetrics = new ObjectBinding<FontMetrics>() {
{ bind(control.fontProperty()); }
@Override protected FontMetrics computeValue() {
invalidateMetrics();
return Toolkit.getToolkit().getFontLoader().getFontMetrics(control.getFont());
}
};
caretVisible = new BooleanBinding() {
{ bind(control.focusedProperty(), control.anchorProperty(), control.caretPositionProperty(),
control.disabledProperty(), control.editableProperty(), displayCaret, blinkProperty());}
@Override protected boolean computeValue() {
return !blinkProperty().get() && displayCaret.get() && control.isFocused() &&
(isWindows() || (control.getCaretPosition() == control.getAnchor())) &&
!control.isDisabled() &&
control.isEditable();
}
};
if (SHOW_HANDLES) {
caretHandle = new StackPane();
selectionHandle1 = new StackPane();
selectionHandle2 = new StackPane();
caretHandle.setManaged(false);
selectionHandle1.setManaged(false);
selectionHandle2.setManaged(false);
if (PlatformUtil.isIOS()) {
caretHandle.setVisible(false);
} else {
caretHandle.visibleProperty().bind(new BooleanBinding() {
{
bind(control.focusedProperty(), control.anchorProperty(),
control.caretPositionProperty(), control.disabledProperty(),
control.editableProperty(), control.lengthProperty(), displayCaret);
}
@Override
protected boolean computeValue() {
return (displayCaret.get() && control.isFocused() &&
control.getCaretPosition() == control.getAnchor() &&
!control.isDisabled() && control.isEditable() &&
control.getLength() > 0);
}
});
}
selectionHandle1.visibleProperty().bind(new BooleanBinding() {
{ bind(control.focusedProperty(), control.anchorProperty(), control.caretPositionProperty(),
control.disabledProperty(), displayCaret);}
@Override protected boolean computeValue() {
return (displayCaret.get() && control.isFocused() &&
control.getCaretPosition() != control.getAnchor() &&
!control.isDisabled());
}
});
selectionHandle2.visibleProperty().bind(new BooleanBinding() {
{ bind(control.focusedProperty(), control.anchorProperty(), control.caretPositionProperty(),
control.disabledProperty(), displayCaret);}
@Override protected boolean computeValue() {
return (displayCaret.get() && control.isFocused() &&
control.getCaretPosition() != control.getAnchor() &&
!control.isDisabled());
}
});
caretHandle.getStyleClass().setAll("caret-handle");
selectionHandle1.getStyleClass().setAll("selection-handle");
selectionHandle2.getStyleClass().setAll("selection-handle");
selectionHandle1.setId("selection-handle-1");
selectionHandle2.setId("selection-handle-2");
}
if (IS_FXVK_SUPPORTED) {
if (preload) {
Scene scene = control.getScene();
if (scene != null) {
Window window = scene.getWindow();
if (window != null) {
FXVK.init(control);
}
}
}
registerInvalidationListener(control.focusedProperty(), observable -> {
if (FXVK.useFXVK()) {
Scene scene = getSkinnable().getScene();
if (control.isEditable() && control.isFocused()) {
FXVK.attach(control);
} else if (scene == null ||
scene.getWindow() == null ||
!scene.getWindow().isFocused() ||
!(scene.getFocusOwner() instanceof TextInputControl &&
((TextInputControl)scene.getFocusOwner()).isEditable())) {
FXVK.detach();
}
}
});
}
if (control.getOnInputMethodTextChanged() == null) {
inputMethodTextChangedHandler = this::handleInputMethodEvent;
control.setOnInputMethodTextChanged(inputMethodTextChangedHandler);
}
control.setInputMethodRequests(new ExtendedInputMethodRequests() {
@Override public Point2D getTextLocation(int offset) {
Scene scene = getSkinnable().getScene();
Window window = scene != null ? scene.getWindow() : null;
if (window == null) {
return new Point2D(0, 0);
}
Rectangle2D characterBounds = getCharacterBounds(control.getSelection().getStart() + offset);
Point2D p = getSkinnable().localToScene(characterBounds.getMinX(), characterBounds.getMaxY());
Point2D location = new Point2D(window.getX() + scene.getX() + p.getX(),
window.getY() + scene.getY() + p.getY());
return location;
}
@Override public int getLocationOffset(int x, int y) {
return getInsertionPoint(x, y);
}
@Override public void cancelLatestCommittedText() {
}
@Override public String getSelectedText() {
TextInputControl control = getSkinnable();
IndexRange selection = control.getSelection();
return control.getText(selection.getStart(), selection.getEnd());
}
@Override public int getInsertPositionOffset() {
int caretPosition = getSkinnable().getCaretPosition();
if (caretPosition < imstart) {
return caretPosition;
} else if (caretPosition < imstart + imlength) {
return imstart;
} else {
return caretPosition - imlength;
}
}
@Override public String getCommittedText(int begin, int end) {
TextInputControl control = getSkinnable();
if (begin < imstart) {
if (end <= imstart) {
return control.getText(begin, end);
} else {
return control.getText(begin, imstart) + control.getText(imstart + imlength, end + imlength);
}
} else {
return control.getText(begin + imlength, end + imlength);
}
}
@Override public int getCommittedTextLength() {
return getSkinnable().getText().length() - imlength;
}
});
}
@Override
public void dispose() {
if (getSkinnable() == null) return;
if (getSkinnable().getOnInputMethodTextChanged() == inputMethodTextChangedHandler) {
getSkinnable().setOnInputMethodTextChanged(null);
}
getSkinnable().setInputMethodRequests(null);
super.dispose();
}
private BooleanProperty blink;
private final void setBlink(boolean value) {
blinkProperty().set(value);
}
private final boolean isBlink() {
return blinkProperty().get();
}
private final BooleanProperty blinkProperty() {
if (blink == null) {
blink = new SimpleBooleanProperty(this, "blink", true);
}
return blink;
}
private final ObjectProperty<Paint> textFill = new StyleableObjectProperty<Paint>(Color.BLACK) {
@Override protected void invalidated() {
updateTextFill();
}
@Override public Object getBean() {
return TextInputControlSkin.this;
}
@Override public String getName() {
return "textFill";
}
@Override public CssMetaData<TextInputControl,Paint> getCssMetaData() {
return StyleableProperties.TEXT_FILL;
}
};
protected final void setTextFill(Paint value) {
textFill.set(value);
}
protected final Paint getTextFill() {
return textFill.get();
}
protected final ObjectProperty<Paint> textFillProperty() {
return textFill;
}
private final ObjectProperty<Paint> promptTextFill = new StyleableObjectProperty<Paint>(Color.GRAY) {
@Override public Object getBean() {
return TextInputControlSkin.this;
}
@Override public String getName() {
return "promptTextFill";
}
@Override public CssMetaData<TextInputControl,Paint> getCssMetaData() {
return StyleableProperties.PROMPT_TEXT_FILL;
}
};
protected final void setPromptTextFill(Paint value) {
promptTextFill.set(value);
}
protected final Paint getPromptTextFill() {
return promptTextFill.get();
}
protected final ObjectProperty<Paint> promptTextFillProperty() {
return promptTextFill;
}
private final ObjectProperty<Paint> highlightFill = new StyleableObjectProperty<Paint>(Color.DODGERBLUE) {
@Override protected void invalidated() {
updateHighlightFill();
}
@Override public Object getBean() {
return TextInputControlSkin.this;
}
@Override public String getName() {
return "highlightFill";
}
@Override public CssMetaData<TextInputControl,Paint> getCssMetaData() {
return StyleableProperties.HIGHLIGHT_FILL;
}
};
protected final void setHighlightFill(Paint value) {
highlightFill.set(value);
}
protected final Paint getHighlightFill() {
return highlightFill.get();
}
protected final ObjectProperty<Paint> highlightFillProperty() {
return highlightFill;
}
private final ObjectProperty<Paint> highlightTextFill = new StyleableObjectProperty<Paint>(Color.WHITE) {
@Override protected void invalidated() {
updateHighlightTextFill();
}
@Override public Object getBean() {
return TextInputControlSkin.this;
}
@Override public String getName() {
return "highlightTextFill";
}
@Override public CssMetaData<TextInputControl,Paint> getCssMetaData() {
return StyleableProperties.HIGHLIGHT_TEXT_FILL;
}
};
protected final void setHighlightTextFill(Paint value) {
highlightTextFill.set(value);
}
protected final Paint getHighlightTextFill() {
return highlightTextFill.get();
}
protected final ObjectProperty<Paint> highlightTextFillProperty() {
return highlightTextFill;
}
private final BooleanProperty displayCaret = new StyleableBooleanProperty(true) {
@Override public Object getBean() {
return TextInputControlSkin.this;
}
@Override public String getName() {
return "displayCaret";
}
@Override public CssMetaData<TextInputControl,Boolean> getCssMetaData() {
return StyleableProperties.DISPLAY_CARET;
}
};
private final void setDisplayCaret(boolean value) {
displayCaret.set(value);
}
private final boolean isDisplayCaret() {
return displayCaret.get();
}
private final BooleanProperty displayCaretProperty() {
return displayCaret;
}
private BooleanProperty forwardBias = new SimpleBooleanProperty(this, "forwardBias", true);
protected final BooleanProperty forwardBiasProperty() {
return forwardBias;
}
public final void setForwardBias(boolean isLeading) {
forwardBias.set(isLeading);
}
protected final boolean isForwardBias() {
return forwardBias.get();
}
protected abstract PathElement[] getUnderlineShape(int start, int end);
protected abstract PathElement[] getRangeShape(int start, int end);
protected abstract void addHighlight(List<? extends Node> nodes, int start);
protected abstract void removeHighlight(List<? extends Node> nodes);
public abstract void moveCaret(TextUnit unit, Direction dir, boolean select);
public Point2D getMenuPosition() {
if (SHOW_HANDLES) {
if (caretHandle.isVisible()) {
return new Point2D(caretHandle.getLayoutX() + caretHandle.getWidth() / 2,
caretHandle.getLayoutY());
} else if (selectionHandle1.isVisible() && selectionHandle2.isVisible()) {
return new Point2D((selectionHandle1.getLayoutX() + selectionHandle1.getWidth() / 2 +
selectionHandle2.getLayoutX() + selectionHandle2.getWidth() / 2) / 2,
selectionHandle2.getLayoutY() + selectionHandle2.getHeight() / 2);
} else {
return null;
}
} else {
throw new UnsupportedOperationException();
}
}
protected String maskText(String txt) {
return txt;
}
protected int getInsertionPoint(double x, double y) { return 0; }
public Rectangle2D getCharacterBounds(int index) { return null; }
protected void scrollCharacterToVisible(int index) {}
protected void invalidateMetrics() {
}
protected void updateTextFill() {};
protected void updateHighlightFill() {};
protected void updateHighlightTextFill() {};
protected void handleInputMethodEvent(InputMethodEvent event) {
final TextInputControl textInput = getSkinnable();
if (textInput.isEditable() && !textInput.textProperty().isBound() && !textInput.isDisabled()) {
if (imlength != 0) {
removeHighlight(imattrs);
imattrs.clear();
textInput.selectRange(imstart, imstart + imlength);
}
if (event.getCommitted().length() != 0) {
String committed = event.getCommitted();
textInput.replaceText(textInput.getSelection(), committed);
}
imstart = textInput.getSelection().getStart();
StringBuilder composed = new StringBuilder();
for (InputMethodTextRun run : event.getComposed()) {
composed.append(run.getText());
}
textInput.replaceText(textInput.getSelection(), composed.toString());
imlength = composed.length();
if (imlength != 0) {
int pos = imstart;
for (InputMethodTextRun run : event.getComposed()) {
int endPos = pos + run.getText().length();
createInputMethodAttributes(run.getHighlight(), pos, endPos);
pos = endPos;
}
addHighlight(imattrs, imstart);
int caretPos = event.getCaretPosition();
if (caretPos >= 0 && caretPos < imlength) {
textInput.selectRange(imstart + caretPos, imstart + caretPos);
}
}
}
}
public void setCaretAnimating(boolean value) {
if (value) {
caretBlinking.start();
} else {
caretBlinking.stop();
blinkProperty().set(true);
}
}
TextInputControlBehavior getBehavior() {
return null;
}
ObservableBooleanValue caretVisibleProperty() {
return caretVisible;
}
boolean isCaretBlinking() {
return caretBlinking.caretTimeline.getStatus() == Status.RUNNING;
}
boolean isRTL() {
return (getSkinnable().getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT);
};
private void createInputMethodAttributes(InputMethodHighlight highlight, int start, int end) {
double minX = 0f;
double maxX = 0f;
double minY = 0f;
double maxY = 0f;
PathElement elements[] = getUnderlineShape(start, end);
for (int i = 0; i < elements.length; i++) {
PathElement pe = elements[i];
if (pe instanceof MoveTo) {
minX = maxX = ((MoveTo)pe).getX();
minY = maxY = ((MoveTo)pe).getY();
} else if (pe instanceof LineTo) {
minX = (minX < ((LineTo)pe).getX() ? minX : ((LineTo)pe).getX());
maxX = (maxX > ((LineTo)pe).getX() ? maxX : ((LineTo)pe).getX());
minY = (minY < ((LineTo)pe).getY() ? minY : ((LineTo)pe).getY());
maxY = (maxY > ((LineTo)pe).getY() ? maxY : ((LineTo)pe).getY());
} else if (pe instanceof HLineTo) {
minX = (minX < ((HLineTo)pe).getX() ? minX : ((HLineTo)pe).getX());
maxX = (maxX > ((HLineTo)pe).getX() ? maxX : ((HLineTo)pe).getX());
} else if (pe instanceof VLineTo) {
minY = (minY < ((VLineTo)pe).getY() ? minY : ((VLineTo)pe).getY());
maxY = (maxY > ((VLineTo)pe).getY() ? maxY : ((VLineTo)pe).getY());
}
if (pe instanceof ClosePath ||
i == elements.length - 1 ||
(i < elements.length - 1 && elements[i+1] instanceof MoveTo)) {
Shape attr = null;
if (highlight == InputMethodHighlight.SELECTED_RAW) {
attr = new Path();
((Path)attr).getElements().addAll(getRangeShape(start, end));
attr.setFill(Color.BLUE);
attr.setOpacity(0.3f);
} else if (highlight == InputMethodHighlight.UNSELECTED_RAW) {
attr = new Line(minX + 2, maxY + 1, maxX - 2, maxY + 1);
attr.setStroke(textFill.get());
attr.setStrokeWidth(maxY - minY);
ObservableList<Double> dashArray = attr.getStrokeDashArray();
dashArray.add(Double.valueOf(2f));
dashArray.add(Double.valueOf(2f));
} else if (highlight == InputMethodHighlight.SELECTED_CONVERTED) {
attr = new Line(minX + 2, maxY + 1, maxX - 2, maxY + 1);
attr.setStroke(textFill.get());
attr.setStrokeWidth((maxY - minY) * 3);
} else if (highlight == InputMethodHighlight.UNSELECTED_CONVERTED) {
attr = new Line(minX + 2, maxY + 1, maxX - 2, maxY + 1);
attr.setStroke(textFill.get());
attr.setStrokeWidth(maxY - minY);
}
if (attr != null) {
attr.setManaged(false);
imattrs.add(attr);
}
}
}
}
private static final class CaretBlinking {
private final Timeline caretTimeline;
private final WeakReference<BooleanProperty> blinkPropertyRef;
public CaretBlinking(final BooleanProperty blinkProperty) {
blinkPropertyRef = new WeakReference<>(blinkProperty);
caretTimeline = new Timeline();
caretTimeline.setCycleCount(Timeline.INDEFINITE);
caretTimeline.getKeyFrames().addAll(
new KeyFrame(Duration.ZERO, e -> setBlink(false)),
new KeyFrame(Duration.seconds(.5), e -> setBlink(true)),
new KeyFrame(Duration.seconds(1)));
}
public void start() {
caretTimeline.play();
}
public void stop() {
caretTimeline.stop();
}
private void setBlink(final boolean value) {
final BooleanProperty blinkProperty = blinkPropertyRef.get();
if (blinkProperty == null) {
caretTimeline.stop();
return;
}
blinkProperty.set(value);
}
}
private static class StyleableProperties {
private static final CssMetaData<TextInputControl,Paint> TEXT_FILL =
new CssMetaData<TextInputControl,Paint>("-fx-text-fill",
PaintConverter.getInstance(), Color.BLACK) {
@Override public boolean isSettable(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return skin.textFill == null || !skin.textFill.isBound();
}
@Override @SuppressWarnings("unchecked")
public StyleableProperty<Paint> getStyleableProperty(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return (StyleableProperty<Paint>)skin.textFill;
}
};
private static final CssMetaData<TextInputControl,Paint> PROMPT_TEXT_FILL =
new CssMetaData<TextInputControl,Paint>("-fx-prompt-text-fill",
PaintConverter.getInstance(), Color.GRAY) {
@Override public boolean isSettable(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return skin.promptTextFill == null || !skin.promptTextFill.isBound();
}
@Override @SuppressWarnings("unchecked")
public StyleableProperty<Paint> getStyleableProperty(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return (StyleableProperty<Paint>)skin.promptTextFill;
}
};
private static final CssMetaData<TextInputControl,Paint> HIGHLIGHT_FILL =
new CssMetaData<TextInputControl,Paint>("-fx-highlight-fill",
PaintConverter.getInstance(), Color.DODGERBLUE) {
@Override public boolean isSettable(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return skin.highlightFill == null || !skin.highlightFill.isBound();
}
@Override @SuppressWarnings("unchecked")
public StyleableProperty<Paint> getStyleableProperty(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return (StyleableProperty<Paint>)skin.highlightFill;
}
};
private static final CssMetaData<TextInputControl,Paint> HIGHLIGHT_TEXT_FILL =
new CssMetaData<TextInputControl,Paint>("-fx-highlight-text-fill",
PaintConverter.getInstance(), Color.WHITE) {
@Override public boolean isSettable(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return skin.highlightTextFill == null || !skin.highlightTextFill.isBound();
}
@Override @SuppressWarnings("unchecked")
public StyleableProperty<Paint> getStyleableProperty(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return (StyleableProperty<Paint>)skin.highlightTextFill;
}
};
private static final CssMetaData<TextInputControl,Boolean> DISPLAY_CARET =
new CssMetaData<TextInputControl,Boolean>("-fx-display-caret",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override public boolean isSettable(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return skin.displayCaret == null || !skin.displayCaret.isBound();
}
@Override @SuppressWarnings("unchecked")
public StyleableProperty<Boolean> getStyleableProperty(TextInputControl n) {
final TextInputControlSkin<?> skin = (TextInputControlSkin<?>) n.getSkin();
return (StyleableProperty<Boolean>)skin.displayCaret;
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
styleables.add(TEXT_FILL);
styleables.add(PROMPT_TEXT_FILL);
styleables.add(HIGHLIGHT_FILL);
styleables.add(HIGHLIGHT_TEXT_FILL);
styleables.add(DISPLAY_CARET);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
@Override protected void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case SHOW_TEXT_RANGE: {
Integer start = (Integer)parameters[0];
Integer end = (Integer)parameters[1];
if (start != null && end != null) {
scrollCharacterToVisible(end);
scrollCharacterToVisible(start);
scrollCharacterToVisible(end);
}
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
}
