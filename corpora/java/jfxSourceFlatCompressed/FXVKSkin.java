package com.sun.javafx.scene.control.skin;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.Scene;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.TouchEvent.TOUCH_PRESSED;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import java.security.AccessController;
import java.security.PrivilegedAction;
public class FXVKSkin extends SkinBase<FXVK> {
private static final int GAP = 6;
private List<List<Key>> currentBoard;
private static HashMap<String, List<List<Key>>> boardMap = new HashMap<String, List<List<Key>>>();
private int numCols;
private boolean capsDown = false;
private boolean shiftDown = false;
private boolean isSymbol = false;
long lastTime = -1L;
void clearShift() {
if (shiftDown && !capsDown) {
shiftDown = false;
updateKeys();
}
lastTime = -1L;
}
void pressShift() {
long time = System.currentTimeMillis();
if (shiftDown && !capsDown) {
if (lastTime > 0L && time - lastTime < 400L) {
shiftDown = false;
capsDown = true;
} else {
shiftDown = false;
capsDown = false;
}
} else if (!shiftDown && !capsDown) {
shiftDown=true;
} else {
shiftDown = false;
capsDown = false;
}
updateKeys();
lastTime = time;
}
void clearSymbolABC() {
isSymbol = false;
updateKeys();
}
void pressSymbolABC() {
isSymbol = !isSymbol;
updateKeys();
}
void clearStateKeys() {
capsDown = false;
shiftDown = false;
isSymbol = false;
lastTime = -1L;
updateKeys();
}
private void updateKeys() {
for (List<Key> row : currentBoard) {
for (Key key : row) {
key.update(capsDown, shiftDown, isSymbol);
}
}
}
private static Popup vkPopup;
private static Popup secondaryPopup;
private static FXVK primaryVK;
private static Timeline slideInTimeline = new Timeline();
private static Timeline slideOutTimeline = new Timeline();
private static boolean hideAfterSlideOut = false;
private static FXVK secondaryVK;
private static Timeline secondaryVKDelay;
private static CharKey secondaryVKKey;
private static TextInputKey repeatKey;
private static Timeline repeatInitialDelay;
private static Timeline repeatSubsequentDelay;
private static double KEY_REPEAT_DELAY = 400;
private static double KEY_REPEAT_DELAY_MIN = 100;
private static double KEY_REPEAT_DELAY_MAX = 1000;
private static double KEY_REPEAT_RATE = 25;
private static double KEY_REPEAT_RATE_MIN = 2;
private static double KEY_REPEAT_RATE_MAX = 50;
private Node attachedNode;
private String vkType = null;
FXVK fxvk;
static final double VK_HEIGHT = 243;
static final double VK_SLIDE_MILLIS = 250;
static final double PREF_PORTRAIT_KEY_WIDTH = 40;
static final double PREF_KEY_HEIGHT = 56;
static boolean vkAdjustWindow = false;
static boolean vkLookup = false;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
String s = System.getProperty("com.sun.javafx.vk.adjustwindow");
if (s != null) {
vkAdjustWindow = Boolean.valueOf(s);
}
s = System.getProperty("com.sun.javafx.sqe.vk.lookup");
if (s != null) {
vkLookup = Boolean.valueOf(s);
}
s = System.getProperty("com.sun.javafx.virtualKeyboard.backspaceRepeatDelay");
if (s != null) {
Double delay = Double.valueOf(s);
KEY_REPEAT_DELAY = Math.min(Math.max(delay, KEY_REPEAT_DELAY_MIN), KEY_REPEAT_DELAY_MAX);
}
s = System.getProperty("com.sun.javafx.virtualKeyboard.backspaceRepeatRate");
if (s != null) {
Double rate = Double.valueOf(s);
if (rate <= 0) {
KEY_REPEAT_RATE = 0;
} else {
KEY_REPEAT_RATE = Math.min(Math.max(rate, KEY_REPEAT_RATE_MIN), KEY_REPEAT_RATE_MAX);
}
}
return null;
});
}
private static DoubleProperty winY = new SimpleDoubleProperty();
static {
winY.addListener(valueModel -> {
if (vkPopup != null) {
vkPopup.setY(winY.get());
}
});
}
private static void startSlideIn() {
slideOutTimeline.stop();
slideInTimeline.playFromStart();
}
private static void startSlideOut(boolean doHide) {
hideAfterSlideOut = doHide;
slideInTimeline.stop();
slideOutTimeline.playFromStart();
}
private void adjustWindowPosition(final Node node) {
if ( !(node instanceof TextInputControl) ) {
return;
}
double inputControlMinY = node.localToScene(0.0, 0.0).getY() + node.getScene().getY();
double inputControlHeight = ((TextInputControl) node).getHeight();
double inputControlMaxY = inputControlMinY + inputControlHeight;
double screenHeight =
com.sun.javafx.util.Utils.getScreen(node).getBounds().getHeight();
double visibleAreaMaxY = screenHeight - VK_HEIGHT;
double inputLineCenterY = 0.0;
double inputLineBottomY = 0.0;
double newWindowYPos = 0.0;
double screenTopOffset = 10.0;
if (node instanceof TextField) {
inputLineCenterY = inputControlMinY + inputControlHeight / 2;
inputLineBottomY = inputControlMaxY;
Parent parent = attachedNode.getParent();
if (parent instanceof ComboBoxBase) {
newWindowYPos = Math.min(screenTopOffset - inputControlMinY, 0);
} else {
newWindowYPos = Math.min(visibleAreaMaxY / 2 - inputLineCenterY, 0);
}
} else if (node instanceof TextArea) {
TextAreaSkin textAreaSkin = (TextAreaSkin)((TextArea)node).getSkin();
Bounds caretBounds = textAreaSkin.getCaretBounds();
double caretMinY = caretBounds.getMinY();
double caretMaxY = caretBounds.getMaxY();
inputLineCenterY = inputControlMinY + ( caretMinY + caretMaxY ) / 2;
inputLineBottomY = inputControlMinY + caretMaxY;
if (inputControlHeight < visibleAreaMaxY) {
newWindowYPos = visibleAreaMaxY / 2 - (inputControlMinY + inputControlHeight / 2);
} else {
newWindowYPos = visibleAreaMaxY / 2 - inputLineCenterY;
}
newWindowYPos = Math.min(newWindowYPos, 0);
} else {
inputLineCenterY = inputControlMinY + inputControlHeight / 2;
inputLineBottomY = inputControlMaxY;
newWindowYPos = Math.min(visibleAreaMaxY / 2 - inputLineCenterY, 0);
}
Window w = node.getScene().getWindow();
if (origWindowYPos + inputLineBottomY > visibleAreaMaxY) {
w.setY(newWindowYPos);
} else {
w.setY(origWindowYPos);
}
}
private void saveWindowPosition(final Node node) {
Window w = node.getScene().getWindow();
origWindowYPos = w.getY();
}
private void restoreWindowPosition(final Node node) {
if (node != null) {
Scene scene = node.getScene();
if (scene != null) {
Window window = scene.getWindow();
if (window != null) {
window.setY(origWindowYPos);
}
}
}
}
EventHandler<InputEvent> unHideEventHandler;
private boolean isVKHidden = false;
private Double origWindowYPos = null;
private void registerUnhideHandler(final Node node) {
if (unHideEventHandler == null) {
unHideEventHandler = event -> {
if (attachedNode != null && isVKHidden) {
double screenHeight = com.sun.javafx.util.Utils.getScreen(attachedNode).getBounds().getHeight();
if (fxvk.getHeight() > 0 && (vkPopup.getY() > screenHeight - fxvk.getHeight())) {
if (slideInTimeline.getStatus() != Animation.Status.RUNNING) {
startSlideIn();
if (vkAdjustWindow) {
adjustWindowPosition(attachedNode);
}
}
}
}
isVKHidden = false;
};
}
node.addEventHandler(TOUCH_PRESSED, unHideEventHandler);
node.addEventHandler(MOUSE_PRESSED, unHideEventHandler);
}
private void unRegisterUnhideHandler(Node node) {
if (unHideEventHandler != null) {
node.removeEventHandler(TOUCH_PRESSED, unHideEventHandler);
node.removeEventHandler(MOUSE_PRESSED, unHideEventHandler);
}
}
private String getNodeVKType(Node node) {
Integer vkType = (Integer)node.getProperties().get(FXVK.VK_TYPE_PROP_KEY);
String typeStr = null;
if (vkType != null) {
Object typeValue = FXVK.VK_TYPE_NAMES[vkType];
if (typeValue instanceof String) {
typeStr = ((String)typeValue).toLowerCase(Locale.ROOT);
}
}
return (typeStr != null ? typeStr : "text");
}
private void updateKeyboardType(Node node) {
String oldType = vkType;
vkType = getNodeVKType(node);
if ( oldType == null || !vkType.equals(oldType) ) {
rebuildPrimaryVK(vkType);
}
}
private void closeSecondaryVK() {
if (secondaryVK != null) {
secondaryVK.setAttachedNode(null);
secondaryPopup.hide();
}
}
private void setupPrimaryVK() {
fxvk.setFocusTraversable(false);
fxvk.setVisible(true);
if (vkPopup == null) {
vkPopup = new Popup();
vkPopup.setAutoFix(false);
}
vkPopup.getContent().setAll(fxvk);
double screenHeight =
com.sun.javafx.util.Utils.getScreen(fxvk).getBounds().getHeight();
double width = com.sun.javafx.util.Utils.getScreen(fxvk).getBounds().getWidth();
slideInTimeline.getKeyFrames().setAll(
new KeyFrame(Duration.millis(VK_SLIDE_MILLIS),
new KeyValue(winY, screenHeight - VK_HEIGHT,
Interpolator.EASE_BOTH)));
slideOutTimeline.getKeyFrames().setAll(
new KeyFrame(Duration.millis(VK_SLIDE_MILLIS),
event -> {
if (hideAfterSlideOut && vkPopup.isShowing()) {
vkPopup.hide();
}
},
new KeyValue(winY, screenHeight, Interpolator.EASE_BOTH)));
fxvk.setPrefWidth(width);
fxvk.setMinWidth(USE_PREF_SIZE);
fxvk.setMaxWidth(USE_PREF_SIZE);
fxvk.setPrefHeight(VK_HEIGHT);
fxvk.setMinHeight(USE_PREF_SIZE);
if (secondaryVKDelay == null) {
secondaryVKDelay = new Timeline();
}
KeyFrame kf = new KeyFrame(Duration.millis(500), event -> {
if (secondaryVKKey != null) {
showSecondaryVK(secondaryVKKey);
}
});
secondaryVKDelay.getKeyFrames().setAll(kf);
if (KEY_REPEAT_RATE > 0) {
repeatInitialDelay = new Timeline(new KeyFrame(
Duration.millis(KEY_REPEAT_DELAY),
event -> {
repeatKey.sendKeyEvents();
repeatSubsequentDelay.playFromStart();
}
));
repeatSubsequentDelay = new Timeline(new KeyFrame(
Duration.millis(1000.0 / KEY_REPEAT_RATE),
event -> {
repeatKey.sendKeyEvents();
}
));
repeatSubsequentDelay.setCycleCount(Animation.INDEFINITE);
}
}
void prerender(Node node) {
if (fxvk != primaryVK) {
return;
}
loadBoard("text");
loadBoard("numeric");
loadBoard("url");
loadBoard("email");
updateKeyboardType(node);
fxvk.setVisible(true);
if (!vkPopup.isShowing()) {
Rectangle2D screenBounds =
com.sun.javafx.util.Utils.getScreen(node).getBounds();
vkPopup.setX((screenBounds.getWidth() - fxvk.prefWidth(-1)) / 2);
winY.set(screenBounds.getHeight());
vkPopup.show(node.getScene().getWindow());
}
}
public FXVKSkin(final FXVK fxvk) {
super(fxvk);
this.fxvk = fxvk;
if (fxvk == FXVK.vk) {
primaryVK = fxvk;
}
if (fxvk == primaryVK) {
setupPrimaryVK();
}
fxvk.attachedNodeProperty().addListener(new InvalidationListener() {
@Override public void invalidated(Observable valueModel) {
Node oldNode = attachedNode;
attachedNode = fxvk.getAttachedNode();
if (fxvk != primaryVK) {
return;
}
closeSecondaryVK();
if (attachedNode != null) {
if (oldNode != null) {
unRegisterUnhideHandler(oldNode);
}
registerUnhideHandler(attachedNode);
updateKeyboardType(attachedNode);
if (oldNode == null || oldNode.getScene() == null || oldNode.getScene().getWindow() != attachedNode.getScene().getWindow()) {
if (vkPopup.isShowing()) {
vkPopup.hide();
} else {
}
}
if (!vkPopup.isShowing()) {
Rectangle2D screenBounds =
com.sun.javafx.util.Utils.getScreen(attachedNode).getBounds();
vkPopup.setX((screenBounds.getWidth() - fxvk.prefWidth(-1)) / 2);
if (oldNode == null || isVKHidden) {
winY.set(screenBounds.getHeight());
} else {
winY.set(screenBounds.getHeight() - VK_HEIGHT);
}
vkPopup.show(attachedNode.getScene().getWindow());
}
if (oldNode == null || isVKHidden) {
startSlideIn();
}
if (vkAdjustWindow) {
if (oldNode == null || oldNode.getScene() == null
|| oldNode.getScene().getWindow() != attachedNode.getScene().getWindow()) {
saveWindowPosition(attachedNode);
}
adjustWindowPosition(attachedNode);
}
} else {
if (oldNode != null) {
unRegisterUnhideHandler(oldNode);
}
startSlideOut(true);
if (vkAdjustWindow) {
restoreWindowPosition(oldNode);
}
}
isVKHidden = false;
}
});
}
private void rebuildSecondaryVK() {
if (secondaryVK.chars == null) {
} else {
int nKeys = secondaryVK.chars.length;
int nRows = (int)Math.floor(Math.sqrt(Math.max(1, nKeys - 2)));
int nKeysPerRow = (int)Math.ceil(nKeys / (double)nRows);
Key tmpKey;
List<List<Key>> rows = new ArrayList<List<Key>>(2);
for (int i = 0; i < nRows; i++) {
int start = i * nKeysPerRow;
int end = Math.min(start + nKeysPerRow, nKeys);
if (start >= end)
break;
List<Key> keys = new ArrayList<Key>(nKeysPerRow);
for (int j = start; j < end; j++) {
tmpKey = new CharKey(secondaryVK.chars[j], null, null);
tmpKey.col= (j - start) * 2;
tmpKey.colSpan = 2;
for (String sc : tmpKey.getStyleClass()) {
tmpKey.text.getStyleClass().add(sc + "-text");
tmpKey.altText.getStyleClass().add(sc + "-alttext");
tmpKey.icon.getStyleClass().add(sc + "-icon");
}
if (secondaryVK.chars[j] != null && secondaryVK.chars[j].length() > 1) {
tmpKey.text.getStyleClass().add("multi-char-text");
}
keys.add(tmpKey);
}
rows.add(keys);
}
currentBoard = rows;
getChildren().clear();
numCols = 0;
for (List<Key> row : currentBoard) {
for (Key key : row) {
numCols = Math.max(numCols, key.col + key.colSpan);
}
getChildren().addAll(row);
}
}
}
private void rebuildPrimaryVK(String type) {
currentBoard = loadBoard(type);
clearStateKeys();
getChildren().clear();
numCols = 0;
for (List<Key> row : currentBoard) {
for (Key key : row) {
numCols = Math.max(numCols, key.col + key.colSpan);
}
getChildren().addAll(row);
}
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return leftInset + (56 * numCols) + rightInset;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return topInset + (80 * 5) + bottomInset;
}
@Override
protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
int numRows = currentBoard.size();
final double colWidth = ((contentWidth - ((numCols - 1) * GAP)) / numCols);
double rowHeight = ((contentHeight - ((numRows - 1) * GAP)) / numRows);
double rowY = contentY;
for (List<Key> row : currentBoard) {
for (Key key : row) {
double startX = contentX + (key.col * (colWidth + GAP));
double width = (key.colSpan * (colWidth + GAP)) - GAP;
key.resizeRelocate((int)(startX + .5), (int)(rowY + .5),
width, rowHeight);
}
rowY += rowHeight + GAP;
}
}
private class Key extends Region {
int col = 0;
int colSpan = 1;
protected final Text text;
protected final Text altText;
protected final Region icon;
protected Key() {
icon = new Region();
text = new Text();
text.setTextOrigin(VPos.TOP);
altText = new Text();
altText.setTextOrigin(VPos.TOP);
getChildren().setAll(text, altText, icon);
getStyleClass().setAll("key");
addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
if (event.getButton() == MouseButton.PRIMARY)
press();
});
addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
if (event.getButton() == MouseButton.PRIMARY)
release();
});
}
protected void press() { }
protected void release() {
clearShift();
}
public void update(boolean capsDown, boolean shiftDown, boolean isSymbol) { }
@Override protected void layoutChildren() {
final double left = snappedLeftInset();
final double top = snappedTopInset();
final double width = getWidth() - left - snappedRightInset();
final double height = getHeight() - top - snappedBottomInset();
text.setVisible(icon.getBackground() == null);
double contentPrefWidth = text.prefWidth(-1);
double contentPrefHeight = text.prefHeight(-1);
text.resizeRelocate(
(int) (left + ((width - contentPrefWidth) / 2) + .5),
(int) (top + ((height - contentPrefHeight) / 2) + .5),
(int) contentPrefWidth,
(int) contentPrefHeight);
altText.setVisible(icon.getBackground() == null && altText.getText().length() > 0);
contentPrefWidth = altText.prefWidth(-1);
contentPrefHeight = altText.prefHeight(-1);
altText.resizeRelocate(
(int) left + (width - contentPrefWidth) + .5,
(int) (top + ((height - contentPrefHeight) / 2) + .5 - height/2),
(int) contentPrefWidth,
(int) contentPrefHeight);
icon.resizeRelocate(left-8, top-8, width+16, height+16);
}
}
private class TextInputKey extends Key {
String chars = "";
protected void press() {
}
protected void release() {
if (fxvk != secondaryVK && secondaryPopup != null && secondaryPopup.isShowing()) {
return;
}
sendKeyEvents();
if (fxvk == secondaryVK) {
showSecondaryVK(null);
}
super.release();
}
protected void sendKeyEvents() {
Node target = fxvk.getAttachedNode();
if (target instanceof EventTarget) {
if (chars != null) {
target.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, chars, "", KeyCode.UNDEFINED, shiftDown, false, false, false));
}
}
}
}
private class CharKey extends TextInputKey {
private final String letterChars;
private final String altChars;
private final String[] moreChars;
private CharKey(String letter, String alt, String[] moreChars, String id) {
this.letterChars = letter;
this.altChars = alt;
this.moreChars = moreChars;
this.chars = this.letterChars;
text.setText(this.chars);
altText.setText(this.altChars);
if (vkLookup) {
setId((id != null ? id : chars).replaceAll("\\.", ""));
}
}
private CharKey(String letter, String alt, String[] moreChars) {
this(letter, alt, moreChars, null);
}
protected void press() {
super.press();
if (letterChars.equals(altChars) && moreChars == null) {
return;
}
if (fxvk == primaryVK) {
showSecondaryVK(null);
secondaryVKKey = CharKey.this;
secondaryVKDelay.playFromStart();
}
}
protected void release() {
super.release();
if (letterChars.equals(altChars) && moreChars == null) {
return;
}
if (fxvk == primaryVK) {
secondaryVKDelay.stop();
}
}
@Override public void update(boolean capsDown, boolean shiftDown, boolean isSymbol) {
if (isSymbol) {
chars = altChars;
text.setText(chars);
if (moreChars != null && moreChars.length > 0 && !Character.isLetter(moreChars[0].charAt(0))) {
altText.setText(moreChars[0]);
} else {
altText.setText(null);
}
} else {
chars = (capsDown || shiftDown) ? letterChars.toUpperCase() : letterChars.toLowerCase();
text.setText(chars);
altText.setText(altChars);
}
}
}
private class SuperKey extends TextInputKey {
private SuperKey(String letter, String code) {
this.chars = code;
text.setText(letter);
getStyleClass().add("special");
if (vkLookup) {
setId(letter);
}
}
}
private class KeyCodeKey extends SuperKey {
private KeyCode code;
private KeyCodeKey(String letter, String c, KeyCode code) {
super(letter, c);
this.code = code;
if (vkLookup) {
setId(letter);
}
}
protected void sendKeyEvents() {
Node target = fxvk.getAttachedNode();
if (target instanceof EventTarget) {
target.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, KeyEvent.CHAR_UNDEFINED, chars, code, shiftDown, false, false, false));
target.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, chars, "", KeyCode.UNDEFINED, shiftDown, false, false, false));
target.fireEvent(new KeyEvent(KeyEvent.KEY_RELEASED, KeyEvent.CHAR_UNDEFINED, chars, code, shiftDown, false, false, false));
}
}
}
private class KeyboardStateKey extends Key {
private final String defaultText;
private final String toggledText;
private KeyboardStateKey(String defaultText, String toggledText, String id) {
this.defaultText = defaultText;
this.toggledText = toggledText;
text.setText(this.defaultText);
if (vkLookup && id != null) {
setId(id);
}
getStyleClass().add("special");
}
@Override public void update(boolean capsDown, boolean shiftDown, boolean isSymbol) {
if (isSymbol) {
text.setText(this.toggledText);
} else {
text.setText(this.defaultText);
}
}
}
private void showSecondaryVK(final CharKey key) {
if (key != null) {
final Node textInput = primaryVK.getAttachedNode();
if (secondaryVK == null) {
secondaryVK = new FXVK();
secondaryVK.setSkin(new FXVKSkin(secondaryVK));
secondaryVK.getStyleClass().setAll("fxvk-secondary");
secondaryPopup = new Popup();
secondaryPopup.setAutoHide(true);
secondaryPopup.getContent().add(secondaryVK);
}
secondaryVK.chars=null;
ArrayList<String> secondaryList = new ArrayList<String>();
if (!isSymbol) {
if (key.letterChars != null && key.letterChars.length() > 0) {
if (shiftDown || capsDown) {
secondaryList.add(key.letterChars.toUpperCase());
} else {
secondaryList.add(key.letterChars);
}
}
}
if (key.altChars != null && key.altChars.length() > 0) {
if (shiftDown || capsDown) {
secondaryList.add(key.altChars.toUpperCase());
} else {
secondaryList.add(key.altChars);
}
}
if (key.moreChars != null && key.moreChars.length > 0) {
if (isSymbol) {
for (String ch : key.moreChars) {
if (!Character.isLetter(ch.charAt(0))) {
secondaryList.add(ch);
}
}
} else {
for (String ch : key.moreChars) {
if (Character.isLetter(ch.charAt(0))) {
if (shiftDown || capsDown) {
secondaryList.add(ch.toUpperCase());
} else {
secondaryList.add(ch);
}
}
}
}
}
boolean isMultiChar = false;
for (String s : secondaryList) {
if (s.length() > 1 ) {
isMultiChar = true;
}
}
secondaryVK.chars = secondaryList.toArray(new String[secondaryList.size()]);
if (secondaryVK.chars.length > 1) {
if (secondaryVK.getSkin() != null) {
((FXVKSkin)secondaryVK.getSkin()).rebuildSecondaryVK();
}
secondaryVK.setAttachedNode(textInput);
FXVKSkin primarySkin = (FXVKSkin)primaryVK.getSkin();
FXVKSkin secondarySkin = (FXVKSkin)secondaryVK.getSkin();
int nKeys = secondaryVK.chars.length;
int nRows = (int)Math.floor(Math.sqrt(Math.max(1, nKeys - 2)));
int nKeysPerRow = (int)Math.ceil(nKeys / (double)nRows);
final double w = snappedLeftInset() + snappedRightInset() +
nKeysPerRow * PREF_PORTRAIT_KEY_WIDTH * (isMultiChar ? 2 : 1) + (nKeysPerRow - 1) * GAP;
final double h = snappedTopInset() + snappedBottomInset() +
nRows * PREF_KEY_HEIGHT + (nRows-1) * GAP;
secondaryVK.setPrefWidth(w);
secondaryVK.setMinWidth(USE_PREF_SIZE);
secondaryVK.setPrefHeight(h);
secondaryVK.setMinHeight(USE_PREF_SIZE);
Platform.runLater(() -> {
Point2D nodePoint =
com.sun.javafx.util.Utils.pointRelativeTo(key, w, h, HPos.CENTER, VPos.TOP,
5, -3, true);
double x = nodePoint.getX();
double y = nodePoint.getY();
Scene scene = key.getScene();
x = Math.min(x, scene.getWindow().getX() + scene.getWidth() - w);
secondaryPopup.show(key.getScene().getWindow(), x, y);
});
}
} else {
closeSecondaryVK();
}
}
private List<List<Key>> loadBoard(String type) {
List<List<Key>> tmpBoard = boardMap.get(type);
if (tmpBoard != null) {
return tmpBoard;
}
String boardFileName = type.substring(0,1).toUpperCase() + type.substring(1).toLowerCase() + "Board.txt";
try {
tmpBoard = new ArrayList<List<Key>>(5);
List<Key> keys = new ArrayList<Key>(20);
InputStream boardFile = FXVKSkin.class.getResourceAsStream(boardFileName);
BufferedReader reader = new BufferedReader(new InputStreamReader(boardFile, "UTF-8"));
String line;
int c = 0;
int col = 0;
int colSpan = 1;
boolean identifier = false;
List<String> charsList = new ArrayList<String>(10);
while ((line = reader.readLine()) != null) {
if (line.length() == 0 || line.charAt(0) == '#') {
continue;
}
for (int i=0; i<line.length(); i++) {
char ch = line.charAt(i);
if (ch == ' ') {
c++;
} else if (ch == '[') {
col = c;
charsList = new ArrayList<String>(10);
identifier = false;
} else if (ch == ']') {
String chars = "";
String alt = null;
String[] moreChars = null;
for (int idx = 0; idx < charsList.size(); idx++) {
charsList.set(idx, FXVKCharEntities.get(charsList.get(idx)));
}
int listSize = charsList.size();
if (listSize > 0) {
chars = charsList.get(0);
if (listSize > 1) {
alt = charsList.get(1);
if (listSize > 2) {
moreChars = charsList.subList(2, listSize).toArray(new String[listSize - 2]);
}
}
}
colSpan = c - col;
Key key;
if (identifier) {
if ("$shift".equals(chars)) {
key = new KeyboardStateKey("", null, "shift") {
@Override protected void release() {
pressShift();
}
@Override public void update(boolean capsDown, boolean shiftDown, boolean isSymbol) {
if (isSymbol) {
this.setDisable(true);
this.setVisible(false);
} else {
if (capsDown) {
icon.getStyleClass().remove("shift-icon");
icon.getStyleClass().add("capslock-icon");
} else {
icon.getStyleClass().remove("capslock-icon");
icon.getStyleClass().add("shift-icon");
}
this.setDisable(false);
this.setVisible(true);
}
}
};
key.getStyleClass().add("shift");
} else if ("$SymbolABC".equals(chars)) {
key = new KeyboardStateKey("!#123", "ABC", "symbol") {
@Override protected void release() {
pressSymbolABC();
}
};
} else if ("$backspace".equals(chars)) {
key = new KeyCodeKey("backspace", "\b", KeyCode.BACK_SPACE) {
@Override protected void press() {
if (KEY_REPEAT_RATE > 0) {
clearShift();
sendKeyEvents();
repeatKey = this;
repeatInitialDelay.playFromStart();
} else {
super.press();
}
}
@Override protected void release() {
if (KEY_REPEAT_RATE > 0) {
repeatInitialDelay.stop();
repeatSubsequentDelay.stop();
} else {
super.release();
}
}
};
key.getStyleClass().add("backspace");
} else if ("$enter".equals(chars)) {
key = new KeyCodeKey("enter", "\n", KeyCode.ENTER);
key.getStyleClass().add("enter");
} else if ("$tab".equals(chars)) {
key = new KeyCodeKey("tab", "\t", KeyCode.TAB);
} else if ("$space".equals(chars)) {
key = new CharKey(" ", " ", null, "space");
} else if ("$clear".equals(chars)) {
key = new SuperKey("clear", "");
} else if ("$.org".equals(chars)) {
key = new SuperKey(".org", ".org");
} else if ("$.com".equals(chars)) {
key = new SuperKey(".com", ".com");
} else if ("$.net".equals(chars)) {
key = new SuperKey(".net", ".net");
} else if ("$oracle.com".equals(chars)) {
key = new SuperKey("oracle.com", "oracle.com");
} else if ("$gmail.com".equals(chars)) {
key = new SuperKey("gmail.com", "gmail.com");
} else if ("$hide".equals(chars)) {
key = new KeyboardStateKey("hide", null, "hide") {
@Override protected void release() {
isVKHidden = true;
startSlideOut(false);
if (vkAdjustWindow) {
restoreWindowPosition(attachedNode);
}
}
};
key.getStyleClass().add("hide");
} else if ("$undo".equals(chars)) {
key = new SuperKey("undo", "");
} else if ("$redo".equals(chars)) {
key = new SuperKey("redo", "");
} else {
key = null;
}
} else {
key = new CharKey(chars, alt, moreChars);
}
if (key != null) {
key.col = col;
key.colSpan = colSpan;
for (String sc : key.getStyleClass()) {
key.text.getStyleClass().add(sc + "-text");
key.altText.getStyleClass().add(sc + "-alttext");
key.icon.getStyleClass().add(sc + "-icon");
}
if (chars != null && chars.length() > 1) {
key.text.getStyleClass().add("multi-char-text");
}
if (alt != null && alt.length() > 1) {
key.altText.getStyleClass().add("multi-char-text");
}
keys.add(key);
}
} else {
for (int j=i; j<line.length(); j++) {
char c2 = line.charAt(j);
boolean e = false;
if (c2 == '\\') {
j++;
i++;
e = true;
c2 = line.charAt(j);
}
if (c2 == '$' && !e) {
identifier = true;
}
if (c2 == '|' && !e) {
charsList.add(line.substring(i, j));
i = j + 1;
} else if ((c2 == ']' || c2 == ' ') && !e) {
charsList.add(line.substring(i, j));
i = j-1;
break;
}
}
c++;
}
}
c = 0;
col = 0;
tmpBoard.add(keys);
keys = new ArrayList<Key>(20);
}
reader.close();
boardMap.put(type, tmpBoard);
return tmpBoard;
} catch (Exception e) {
e.printStackTrace();
return Collections.emptyList();
}
}
}
