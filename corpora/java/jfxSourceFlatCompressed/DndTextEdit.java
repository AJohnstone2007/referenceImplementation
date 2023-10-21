package dragdrop;
import javafx.event.EventHandler;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
public class DndTextEdit extends SimpleTextEdit {
public DndTextEdit() {
skin.setOnMousePressed(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
requestFocus();
int pos = getPos(event.getX());
if (!isInSelection(pos)) {
if (pos >= 0) {
setCaretPos(pos);
}
clearSelection();
}
}
});
skin.setOnDragDetected(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent event) {
int pos = getPos(event.getX());
if (isInSelection(pos)) {
Dragboard db = skin.startDragAndDrop(TransferMode.ANY);
ClipboardContent data = new ClipboardContent();
data.putString(getSelection());
db.setContent(data);
}
}
});
skin.setOnDragEntered(new EventHandler<DragEvent>() {
@Override public void handle(DragEvent event) {
if (!isFocused()) {
showCaret();
}
}
});
skin.setOnDragExited(new EventHandler<DragEvent>() {
@Override public void handle(DragEvent event) {
if (!isFocused()) {
removeCaret();
}
}
});
skin.setOnDragOver(new EventHandler<DragEvent>() {
@Override public void handle(DragEvent event) {
int pos = getPos(event.getX());
setCaretPos(pos);
if (event.getDragboard().hasString()) {
event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
}
}
});
skin.setOnDragDropped(new EventHandler<DragEvent>() {
@Override public void handle(DragEvent event) {
int pos = getPos(event.getX());
if (event.getGestureSource() == skin && isInSelection(pos)) {
event.setDropCompleted(false);
return;
}
setCaretPos(pos);
Dragboard db = event.getDragboard();
if (db.hasString()) {
String s = db.getString();
if (event.getGestureSource() == skin) {
removeSelection();
}
insert(s);
event.setDropCompleted(true);
requestFocus();
return;
}
event.setDropCompleted(false);
}
});
skin.setOnDragDone(new EventHandler<DragEvent>() {
@Override public void handle(DragEvent event) {
if (event.getTransferMode() == TransferMode.MOVE) {
removeSelection();
}
if (event.getTransferMode() != null) {
clearSelection();
}
}
});
}
}
