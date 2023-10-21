package com.sun.javafx.tk;
import javafx.scene.input.TransferMode;
public interface TKDropTargetListener {
public TransferMode dragEnter(double x, double y, double screenX, double screenY,
TransferMode transferMode, TKClipboard dragboard);
public TransferMode dragOver(double x, double y, double screenX, double screenY,
TransferMode transferMode);
public void dragExit(double x, double y, double screenX, double screenY);
public TransferMode drop(double x, double y, double screenX, double screenY,
TransferMode transferMode);
}
