package com.sun.javafx.embed;
import javafx.scene.input.TransferMode;
public interface EmbeddedSceneDTInterface {
public TransferMode handleDragEnter(int x, int y, int xAbs, int yAbs,
TransferMode recommendedDropAction,
EmbeddedSceneDSInterface dragSource);
public void handleDragLeave();
public TransferMode handleDragDrop(int x, int y, int xAbs, int yAbs,
TransferMode recommendedDropAction);
public TransferMode handleDragOver(int x, int y, int xAbs, int yAbs,
TransferMode recommendedDropAction);
}
