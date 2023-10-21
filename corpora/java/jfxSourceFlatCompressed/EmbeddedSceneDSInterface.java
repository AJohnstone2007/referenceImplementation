package com.sun.javafx.embed;
import java.util.Set;
import javafx.scene.input.TransferMode;
public interface EmbeddedSceneDSInterface {
public Set<TransferMode> getSupportedActions();
public Object getData(String mimeType);
public String[] getMimeTypes();
public boolean isMimeTypeAvailable(String mimeType);
public void dragDropEnd(TransferMode performedAction);
}
