package com.sun.javafx.embed.swing;
import com.sun.javafx.embed.EmbeddedSceneDSInterface;
import com.sun.javafx.tk.Toolkit;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Set;
import javafx.scene.input.TransferMode;
final class SwingDragSource extends CachingTransferable implements EmbeddedSceneDSInterface {
private int sourceActions;
SwingDragSource() {
}
void updateContents(final DropTargetDragEvent e, boolean fetchData) {
sourceActions = e.getSourceActions();
updateData(e.getTransferable(), fetchData);
}
void updateContents(final DropTargetDropEvent e, boolean fetchData) {
sourceActions = e.getSourceActions();
updateData(e.getTransferable(), fetchData);
}
@Override
public Set<TransferMode> getSupportedActions() {
assert Toolkit.getToolkit().isFxUserThread();
return SwingDnD.dropActionsToTransferModes(sourceActions);
}
@Override
public void dragDropEnd(TransferMode performedAction) {
throw new UnsupportedOperationException();
}
}
