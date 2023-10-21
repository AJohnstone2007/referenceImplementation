package com.sun.javafx.scene;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.Mnemonic;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.collections.ObservableMapWrapper;
import com.sun.javafx.event.BasicEventDispatcher;
import com.sun.javafx.scene.traversal.Direction;
public final class KeyboardShortcutsHandler extends BasicEventDispatcher {
private ObservableMap<KeyCombination, Runnable> accelerators;
private CopyOnWriteMap<KeyCombination, Runnable> acceleratorsBackingMap;
private ObservableMap<KeyCombination, ObservableList<Mnemonic>> mnemonics;
public void addMnemonic(Mnemonic m) {
ObservableList<Mnemonic> mnemonicsList = getMnemonics().get(m.getKeyCombination());
if (mnemonicsList == null) {
mnemonicsList = new ObservableListWrapper<>(new ArrayList<>());
getMnemonics().put(m.getKeyCombination(), mnemonicsList);
}
boolean foundMnemonic = false;
for (Mnemonic mnemonic : mnemonicsList) {
if (mnemonic == m) {
foundMnemonic = true;
break;
}
}
if (!foundMnemonic) {
mnemonicsList.add(m);
}
}
public void removeMnemonic(Mnemonic m) {
ObservableList<Mnemonic> mnemonicsList = getMnemonics().get(m.getKeyCombination());
if (mnemonicsList != null) {
for (int i = 0 ; i < mnemonicsList.size() ; i++) {
if (mnemonicsList.get(i).getNode() == m.getNode()) {
mnemonicsList.remove(i);
}
}
}
}
public ObservableMap<KeyCombination, ObservableList<Mnemonic>> getMnemonics() {
if (mnemonics == null) {
mnemonics = new ObservableMapWrapper<>(new HashMap<>());
}
return mnemonics;
}
public ObservableMap<KeyCombination, Runnable> getAccelerators() {
if (accelerators == null) {
acceleratorsBackingMap = new CopyOnWriteMap<>();
accelerators = new ObservableMapWrapper<>(acceleratorsBackingMap);
}
return accelerators;
}
private void traverse(Event event, Node node, Direction dir) {
if (NodeHelper.traverse(node, dir)) {
event.consume();
}
}
public void processTraversal(Event event) {
if (event.getEventType() != KeyEvent.KEY_PRESSED) return;
if (!(event instanceof KeyEvent)) return;
KeyEvent keyEvent = (KeyEvent)event;
if (!keyEvent.isMetaDown() && !keyEvent.isControlDown() && !keyEvent.isAltDown()) {
Object obj = event.getTarget();
if (!(obj instanceof Node)) return;
Node node = (Node)obj;
switch (keyEvent.getCode()) {
case TAB :
if (keyEvent.isShiftDown()) {
traverse(event, node, Direction.PREVIOUS);
}
else {
traverse(event, node, Direction.NEXT);
}
break;
case UP :
traverse(event, node, Direction.UP);
break;
case DOWN :
traverse(event, node, Direction.DOWN);
break;
case LEFT :
traverse(event, node, Direction.LEFT);
break;
case RIGHT :
traverse(event, node, Direction.RIGHT);
break;
default :
break;
}
}
}
@Override
public Event dispatchBubblingEvent(Event event) {
if (!(event instanceof KeyEvent)) return event;
final boolean keyPressedEvent = event.getEventType() == KeyEvent.KEY_PRESSED;
final KeyEvent keyEvent = (KeyEvent)event;
if (keyPressedEvent) {
if (!event.isConsumed()) {
processAccelerators(keyEvent);
}
if (!event.isConsumed()) {
processTraversal(event);
}
}
return event;
}
@Override
public Event dispatchCapturingEvent(Event event) {
if (!(event instanceof KeyEvent)) return event;
final boolean keyPressedEvent = event.getEventType() == KeyEvent.KEY_PRESSED;
final boolean keyTypedEvent = event.getEventType() == KeyEvent.KEY_TYPED;
final boolean keyReleasedEvent = event.getEventType() == KeyEvent.KEY_RELEASED;
final KeyEvent keyEvent = (KeyEvent)event;
if (keyPressedEvent || keyTypedEvent) {
if (PlatformUtil.isMac()) {
if (keyEvent.isMetaDown()) {
processMnemonics(keyEvent);
}
} else if (keyEvent.isAltDown() || isMnemonicsDisplayEnabled()) {
processMnemonics(keyEvent);
}
}
if (!PlatformUtil.isMac() && !event.isConsumed()) {
if (keyPressedEvent) {
if (keyEvent.isAltDown()) {
if (!isMnemonicsDisplayEnabled()) {
setMnemonicsDisplayEnabled(true);
} else {
if (PlatformUtil.isWindows()) {
setMnemonicsDisplayEnabled(!isMnemonicsDisplayEnabled());
}
}
} else if (keyEvent.getCode() == KeyCode.ESCAPE) {
setMnemonicsDisplayEnabled(false);
}
}
if (keyReleasedEvent && !keyEvent.isAltDown() && !PlatformUtil.isWindows()) {
setMnemonicsDisplayEnabled(false);
}
}
return event;
}
private void processMnemonics(final KeyEvent event) {
if (mnemonics == null) return;
KeyEvent lookupEvent = event;
if (event.getEventType() == KeyEvent.KEY_TYPED) {
lookupEvent = new KeyEvent(null, event.getTarget(), KeyEvent.KEY_PRESSED,
" ",
event.getCharacter(),
KeyCode.getKeyCode(event.getCharacter()),
event.isShiftDown(),
event.isControlDown(),
isMnemonicsDisplayEnabled(),
event.isMetaDown());
} else if (isMnemonicsDisplayEnabled()) {
lookupEvent = new KeyEvent(null, event.getTarget(), KeyEvent.KEY_PRESSED,
event.getCharacter(),
event.getText(),
event.getCode(),
event.isShiftDown(),
event.isControlDown(),
isMnemonicsDisplayEnabled(),
event.isMetaDown());
}
ObservableList<Mnemonic> mnemonicsList = null;
for (Map.Entry<KeyCombination, ObservableList<Mnemonic>> mnemonic: mnemonics.entrySet()) {
if (mnemonic.getKey().match(lookupEvent)) {
mnemonicsList = mnemonic.getValue();
break;
}
}
if (mnemonicsList == null) return;
boolean multipleNodes = false;
Node firstNode = null;
Mnemonic firstMnemonics = null;
int focusedIndex = -1;
int nextFocusable = -1;
for (int i = 0 ; i < mnemonicsList.size() ; i++) {
Mnemonic mnemonic = mnemonicsList.get(i);
Node currentNode = mnemonic.getNode();
if (firstMnemonics == null && (NodeHelper.isTreeVisible(currentNode) && !currentNode.isDisabled())) {
firstMnemonics = mnemonic;
}
if (NodeHelper.isTreeVisible(currentNode) && (currentNode.isFocusTraversable() && !currentNode.isDisabled())) {
if (firstNode == null) {
firstNode = currentNode;
} else {
multipleNodes = true;
if (focusedIndex != -1) {
if (nextFocusable == -1) {
nextFocusable = i;
}
}
}
}
if (currentNode.isFocused()) {
focusedIndex = i;
}
}
if (firstNode != null) {
if (!multipleNodes == true) {
firstNode.requestFocus();
event.consume();
}
else {
if (focusedIndex == -1) {
firstNode.requestFocus();
event.consume();
}
else {
if (focusedIndex >= mnemonicsList.size()) {
firstNode.requestFocus();
event.consume();
}
else {
if (nextFocusable != -1) {
mnemonicsList.get(nextFocusable).getNode().requestFocus();
}
else {
firstNode.requestFocus();
}
event.consume();
}
}
}
}
if (!multipleNodes && firstMnemonics != null) {
if (event.getEventType() == KeyEvent.KEY_TYPED) {
event.consume();
} else {
firstMnemonics.fire();
event.consume();
}
}
}
private void processAccelerators(KeyEvent event) {
if (acceleratorsBackingMap != null) {
acceleratorsBackingMap.lock();
try {
for (Map.Entry<KeyCombination, Runnable>
accelerator : acceleratorsBackingMap.backingMap.entrySet()) {
if (accelerator.getKey().match(event)) {
Runnable acceleratorRunnable = accelerator.getValue();
if (acceleratorRunnable != null) {
acceleratorRunnable.run();
event.consume();
}
}
}
} finally {
acceleratorsBackingMap.unlock();
}
}
}
private void processMnemonicsKeyDisplay() {
ObservableList<Mnemonic> mnemonicsList = null;
if (mnemonics != null) {
for (Map.Entry<KeyCombination, ObservableList<Mnemonic>> mnemonic: mnemonics.entrySet()) {
mnemonicsList = (ObservableList) mnemonic.getValue();
if (mnemonicsList != null) {
for (int i = 0 ; i < mnemonicsList.size() ; i++) {
Node currentNode = (Node)mnemonicsList.get(i).getNode();
NodeHelper.setShowMnemonics(currentNode, mnemonicsDisplayEnabled);
}
}
}
}
}
private boolean mnemonicsDisplayEnabled = false;
public boolean isMnemonicsDisplayEnabled() {
return mnemonicsDisplayEnabled;
}
public void setMnemonicsDisplayEnabled(boolean b) {
if (b != mnemonicsDisplayEnabled) {
mnemonicsDisplayEnabled = b;
processMnemonicsKeyDisplay();
}
}
public void clearNodeMnemonics(Node node) {
if (mnemonics != null) {
for (ObservableList<Mnemonic> list : mnemonics.values()) {
for (Iterator<Mnemonic> it = list.iterator(); it.hasNext(); ) {
Mnemonic m = it.next();
if (m.getNode() == node) {
it.remove();
}
}
}
}
}
private static class CopyOnWriteMap<K, V> extends AbstractMap<K, V> {
private Map<K, V> backingMap = new HashMap<>();
private boolean lock;
public void lock() {
lock = true;
}
public void unlock() {
lock = false;
}
@Override
public V put(K key, V value) {
if (lock) {
backingMap = new HashMap<>(backingMap);
lock = false;
}
return backingMap.put(key, value);
}
@Override
public Set<Entry<K, V>> entrySet() {
return new AbstractSet<Entry<K, V>>() {
@Override
public Iterator<Entry<K, V>> iterator() {
return new Iterator<Entry<K, V>>() {
private Iterator<Entry<K, V>> backingIt = backingMap.entrySet().iterator();
private Map<K, V> backingMapAtCreation = backingMap;
private Entry<K, V> lastNext = null;
@Override
public boolean hasNext() {
checkCoMod();
return backingIt.hasNext();
}
private void checkCoMod() {
if (backingMap != backingMapAtCreation) {
throw new ConcurrentModificationException();
}
}
@Override
public Entry<K, V> next() {
checkCoMod();
return lastNext = backingIt.next();
}
@Override
public void remove() {
checkCoMod();
if (lastNext == null) {
throw new IllegalStateException();
}
if (lock) {
backingMap = new HashMap<>(backingMap);
backingIt = backingMap.entrySet().iterator();
while (!lastNext.equals(backingIt.next()));
lock = false;
}
backingIt.remove();
lastNext = null;
}
};
}
@Override
public int size() {
return backingMap.size();
}
};
}
}
}
