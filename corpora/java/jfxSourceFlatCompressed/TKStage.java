package com.sun.javafx.tk;
import java.security.AccessControlContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination.ModifierValue;
public interface TKStage {
public void setTKStageListener(TKStageListener listener);
public TKScene createTKScene(boolean depthBuffer, boolean msaa, @SuppressWarnings("removal") AccessControlContext acc);
public void setScene(TKScene scene);
public void setBounds(float x, float y, boolean xSet, boolean ySet,
float w, float h, float cw, float ch,
float xGravity, float yGravity,
float renderScaleX, float renderScaleY);
public float getPlatformScaleX();
public float getPlatformScaleY();
public float getOutputScaleX();
public float getOutputScaleY();
public void setIcons(java.util.List icons);
public void setTitle(String title);
public void setVisible(boolean visible);
public void setOpacity(float opacity);
public void setIconified(boolean iconified);
public void setMaximized(boolean maximized);
public void setAlwaysOnTop(boolean alwaysOnTop);
public void setResizable(boolean resizable);
public void setImportant(boolean important);
public void setMinimumSize(int minWidth, int minHeight);
public void setMaximumSize(int maxWidth, int maxHeight);
public void setFullScreen(boolean fullScreen);
public void requestFocus();
public void toBack();
public void toFront();
public void close();
public default void postponeClose() {}
public default void closePostponed() {}
public void requestFocus(FocusCause cause);
public boolean grabFocus();
public void ungrabFocus();
void requestInput(String text, int type, double width, double height,
double Mxx, double Mxy, double Mxz, double Mxt,
double Myx, double Myy, double Myz, double Myt,
double Mzx, double Mzy, double Mzz, double Mzt);
void releaseInput();
public void setRTL(boolean b);
public void setEnabled(boolean enabled);
public long getRawHandle();
public static final KeyCodeCombination defaultFullScreenExitKeycombo =
new KeyCodeCombination(KeyCode.ESCAPE,
ModifierValue.UP,
ModifierValue.UP,
ModifierValue.UP,
ModifierValue.UP,
ModifierValue.UP);
}
