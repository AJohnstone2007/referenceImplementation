package com.sun.javafx.tk;
import javafx.scene.input.TransferMode;
public interface TKDragSourceListener {
void dragDropEnd(double x, double y, double screenX, double screenY, TransferMode transferMode);
}
