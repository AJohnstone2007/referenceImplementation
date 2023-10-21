package com.sun.javafx.embed;
import com.sun.javafx.cursor.CursorFrame;
public interface HostInterface {
public void setEmbeddedStage(EmbeddedStageInterface embeddedStage);
public void setEmbeddedScene(EmbeddedSceneInterface embeddedScene);
public boolean requestFocus();
public boolean traverseFocusOut(boolean forward);
public void repaint();
public void setPreferredSize(int width, int height);
public void setEnabled(boolean enabled);
public void setCursor(CursorFrame cursorFrame);
public boolean grabFocus();
public void ungrabFocus();
}
