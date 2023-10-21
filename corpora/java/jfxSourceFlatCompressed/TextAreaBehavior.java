package com.sun.javafx.scene.control.behavior;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.scene.control.Properties;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.input.ContextMenuEvent;
import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.inputmap.KeyBinding;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.HitInfo;
import javafx.stage.Screen;
import javafx.stage.Window;
import java.util.function.Predicate;
import static com.sun.javafx.PlatformUtil.isMac;
import static com.sun.javafx.PlatformUtil.isWindows;
import com.sun.javafx.stage.WindowHelper;
import static javafx.scene.control.skin.TextInputControlSkin.TextUnit;
import static javafx.scene.control.skin.TextInputControlSkin.Direction;
import static javafx.scene.input.KeyCode.*;
public class TextAreaBehavior extends TextInputControlBehavior<TextArea> {
private TextAreaSkin skin;
private TwoLevelFocusBehavior tlFocus;
private ChangeListener<Boolean> focusListener;
public TextAreaBehavior(final TextArea c) {
super(c);
if (Properties.IS_TOUCH_SUPPORTED) {
contextMenu.getStyleClass().add("text-input-context-menu");
}
final Predicate<KeyEvent> validWhenEditable = e -> !c.isEditable();
InputMap<TextArea> textAreaInputMap = new InputMap<>(c);
textAreaInputMap.getMappings().addAll(
keyMapping(HOME, e -> lineStart(false)),
keyMapping(END, e -> lineEnd(false)),
keyMapping(UP, e -> skin.moveCaret(TextUnit.LINE, Direction.UP, false)),
keyMapping(DOWN, e -> skin.moveCaret(TextUnit.LINE, Direction.DOWN, false)),
keyMapping(PAGE_UP, e -> skin.moveCaret(TextUnit.PAGE, Direction.UP, false)),
keyMapping(PAGE_DOWN, e -> skin.moveCaret(TextUnit.PAGE, Direction.DOWN, false)),
keyMapping(new KeyBinding(HOME).shift(), e -> lineStart(true)),
keyMapping(new KeyBinding(END).shift(), e -> lineEnd(true)),
keyMapping(new KeyBinding(UP).shift(), e -> skin.moveCaret(TextUnit.LINE, Direction.UP, true)),
keyMapping(new KeyBinding(DOWN).shift(), e -> skin.moveCaret(TextUnit.LINE, Direction.DOWN, true)),
keyMapping(new KeyBinding(PAGE_UP).shift(), e -> skin.moveCaret(TextUnit.PAGE, Direction.UP, true)),
keyMapping(new KeyBinding(PAGE_DOWN).shift(), e -> skin.moveCaret(TextUnit.PAGE, Direction.DOWN, true)),
keyMapping(new KeyBinding(ENTER), e -> insertNewLine(), validWhenEditable),
keyMapping(new KeyBinding(TAB), e -> insertTab(), validWhenEditable)
);
addDefaultChildMap(getInputMap(), textAreaInputMap);
InputMap<TextArea> macOsInputMap = new InputMap<>(c);
macOsInputMap.setInterceptor(e -> !PlatformUtil.isMac());
macOsInputMap.getMappings().addAll(
keyMapping(new KeyBinding(LEFT).shortcut(), e -> lineStart(false)),
keyMapping(new KeyBinding(RIGHT).shortcut(), e -> lineEnd(false)),
keyMapping(new KeyBinding(UP).shortcut(), e -> c.home()),
keyMapping(new KeyBinding(DOWN).shortcut(), e -> c.end()),
keyMapping(new KeyBinding(LEFT).shortcut().shift(), e -> lineStart(true)),
keyMapping(new KeyBinding(RIGHT).shortcut().shift(), e -> lineEnd(true)),
keyMapping(new KeyBinding(UP).shortcut().shift(), e -> selectHomeExtend()),
keyMapping(new KeyBinding(DOWN).shortcut().shift(), e -> selectEndExtend()),
keyMapping(new KeyBinding(UP).alt(), e -> skin.moveCaret(TextUnit.PARAGRAPH, Direction.UP, false)),
keyMapping(new KeyBinding(DOWN).alt(), e -> skin.moveCaret(TextUnit.PARAGRAPH, Direction.DOWN, false)),
keyMapping(new KeyBinding(UP).alt().shift(), e -> skin.moveCaret(TextUnit.PARAGRAPH, Direction.UP, true)),
keyMapping(new KeyBinding(DOWN).alt().shift(), e -> skin.moveCaret(TextUnit.PARAGRAPH, Direction.DOWN, true))
);
addDefaultChildMap(textAreaInputMap, macOsInputMap);
InputMap<TextArea> nonMacOsInputMap = new InputMap<>(c);
nonMacOsInputMap.setInterceptor(e -> PlatformUtil.isMac());
nonMacOsInputMap.getMappings().addAll(
keyMapping(new KeyBinding(UP).ctrl(), e -> skin.moveCaret(TextUnit.PARAGRAPH, Direction.UP, false)),
keyMapping(new KeyBinding(DOWN).ctrl(), e -> skin.moveCaret(TextUnit.PARAGRAPH, Direction.DOWN, false)),
keyMapping(new KeyBinding(UP).ctrl().shift(), e -> skin.moveCaret(TextUnit.PARAGRAPH, Direction.UP, true)),
keyMapping(new KeyBinding(DOWN).ctrl().shift(), e -> skin.moveCaret(TextUnit.PARAGRAPH, Direction.DOWN, true))
);
addDefaultChildMap(textAreaInputMap, nonMacOsInputMap);
addKeyPadMappings(textAreaInputMap);
focusListener = (src, ov, nv) -> handleFocusChange();
c.focusedProperty().addListener(focusListener);
if (Utils.isTwoLevelFocus()) {
tlFocus = new TwoLevelFocusBehavior(c);
}
}
@Override public void dispose() {
getNode().focusedProperty().removeListener(focusListener);
if (tlFocus != null) tlFocus.dispose();
super.dispose();
}
private void handleFocusChange() {
final TextArea textArea = getNode();
if (textArea.isFocused()) {
if (!focusGainedByMouseClick) {
setCaretAnimating(true);
}
} else {
focusGainedByMouseClick = false;
setCaretAnimating(false);
}
}
public void setTextAreaSkin(TextAreaSkin skin) {
this.skin = skin;
}
private void insertNewLine() {
setEditing(true);
getNode().replaceSelection("\n");
setEditing(false);
}
private void insertTab() {
setEditing(true);
getNode().replaceSelection("\t");
setEditing(false);
}
@Override protected void deleteChar(boolean previous) {
if (previous) {
getNode().deletePreviousChar();
} else {
getNode().deleteNextChar();
}
}
@Override protected void deleteFromLineStart() {
TextArea textArea = getNode();
int end = textArea.getCaretPosition();
if (end > 0) {
lineStart(false);
int start = textArea.getCaretPosition();
if (end > start) {
replaceText(start, end, "");
}
}
}
private void lineStart(boolean select) {
skin.moveCaret(TextUnit.LINE, Direction.BEGINNING, select);
}
private void lineEnd(boolean select) {
skin.moveCaret(TextUnit.LINE, Direction.END, select);
}
@Override protected void replaceText(int start, int end, String txt) {
getNode().replaceText(start, end, txt);
}
private boolean focusGainedByMouseClick = false;
private boolean shiftDown = false;
private boolean deferClick = false;
@Override public void mousePressed(MouseEvent e) {
TextArea textArea = getNode();
if (!textArea.isDisabled()) {
if (!textArea.isFocused()) {
focusGainedByMouseClick = true;
textArea.requestFocus();
}
setCaretAnimating(false);
if (e.getButton() == MouseButton.PRIMARY && !(e.isMiddleButtonDown() || e.isSecondaryButtonDown())) {
HitInfo hit = skin.getIndex(e.getX(), e.getY());
int i = hit.getInsertionIndex();
final int anchor = textArea.getAnchor();
final int caretPosition = textArea.getCaretPosition();
if (e.getClickCount() < 2 &&
(e.isSynthesized() ||
(anchor != caretPosition &&
((i > anchor && i < caretPosition) || (i < anchor && i > caretPosition))))) {
deferClick = true;
} else if (!(e.isControlDown() || e.isAltDown() || e.isShiftDown() || e.isMetaDown() || e.isShortcutDown())) {
switch (e.getClickCount()) {
case 1: skin.positionCaret(hit, false); break;
case 2: mouseDoubleClick(hit); break;
case 3: mouseTripleClick(hit); break;
default:
}
} else if (e.isShiftDown() && !(e.isControlDown() || e.isAltDown() || e.isMetaDown() || e.isShortcutDown()) && e.getClickCount() == 1) {
shiftDown = true;
if (isMac()) {
textArea.extendSelection(i);
} else {
skin.positionCaret(hit, true);
}
}
}
if (contextMenu.isShowing()) {
contextMenu.hide();
}
}
}
@Override public void mouseDragged(MouseEvent e) {
final TextArea textArea = getNode();
if (!textArea.isDisabled() && !e.isSynthesized()) {
if (e.getButton() == MouseButton.PRIMARY &&
!(e.isMiddleButtonDown() || e.isSecondaryButtonDown() ||
e.isControlDown() || e.isAltDown() || e.isShiftDown() || e.isMetaDown())) {
skin.positionCaret(skin.getIndex(e.getX(), e.getY()), true);
}
}
deferClick = false;
}
@Override public void mouseReleased(final MouseEvent e) {
final TextArea textArea = getNode();
if (!textArea.isDisabled()) {
setCaretAnimating(false);
if (deferClick) {
deferClick = false;
skin.positionCaret(skin.getIndex(e.getX(), e.getY()), shiftDown);
shiftDown = false;
}
setCaretAnimating(true);
}
}
@Override public void contextMenuRequested(ContextMenuEvent e) {
final TextArea textArea = getNode();
if (contextMenu.isShowing()) {
contextMenu.hide();
} else if (textArea.getContextMenu() == null &&
textArea.getOnContextMenuRequested() == null) {
double screenX = e.getScreenX();
double screenY = e.getScreenY();
double sceneX = e.getSceneX();
if (Properties.IS_TOUCH_SUPPORTED) {
Point2D menuPos;
if (textArea.getSelection().getLength() == 0) {
skin.positionCaret(skin.getIndex(e.getX(), e.getY()), false);
menuPos = skin.getMenuPosition();
} else {
menuPos = skin.getMenuPosition();
if (menuPos != null && (menuPos.getX() <= 0 || menuPos.getY() <= 0)) {
skin.positionCaret(skin.getIndex(e.getX(), e.getY()), false);
menuPos = skin.getMenuPosition();
}
}
if (menuPos != null) {
Point2D p = getNode().localToScene(menuPos);
Scene scene = getNode().getScene();
Window window = scene.getWindow();
Point2D location = new Point2D(window.getX() + scene.getX() + p.getX(),
window.getY() + scene.getY() + p.getY());
screenX = location.getX();
sceneX = p.getX();
screenY = location.getY();
}
}
populateContextMenu();
double menuWidth = contextMenu.prefWidth(-1);
double menuX = screenX - (Properties.IS_TOUCH_SUPPORTED ? (menuWidth / 2) : 0);
Screen currentScreen = com.sun.javafx.util.Utils.getScreenForPoint(screenX, 0);
Rectangle2D bounds = currentScreen.getBounds();
if (menuX < bounds.getMinX()) {
getNode().getProperties().put("CONTEXT_MENU_SCREEN_X", screenX);
getNode().getProperties().put("CONTEXT_MENU_SCENE_X", sceneX);
contextMenu.show(getNode(), bounds.getMinX(), screenY);
} else if (screenX + menuWidth > bounds.getMaxX()) {
double leftOver = menuWidth - ( bounds.getMaxX() - screenX);
getNode().getProperties().put("CONTEXT_MENU_SCREEN_X", screenX);
getNode().getProperties().put("CONTEXT_MENU_SCENE_X", sceneX);
contextMenu.show(getNode(), screenX - leftOver, screenY);
} else {
getNode().getProperties().put("CONTEXT_MENU_SCREEN_X", 0);
getNode().getProperties().put("CONTEXT_MENU_SCENE_X", 0);
contextMenu.show(getNode(), menuX, screenY);
}
}
e.consume();
}
@Override protected void setCaretAnimating(boolean play) {
skin.setCaretAnimating(play);
}
protected void mouseDoubleClick(HitInfo hit) {
final TextArea textArea = getNode();
textArea.previousWord();
if (isWindows()) {
textArea.selectNextWord();
} else {
textArea.selectEndOfNextWord();
}
}
protected void mouseTripleClick(HitInfo hit) {
skin.moveCaret(TextUnit.PARAGRAPH, Direction.BEGINNING, false);
skin.moveCaret(TextUnit.PARAGRAPH, Direction.END, true);
}
}
