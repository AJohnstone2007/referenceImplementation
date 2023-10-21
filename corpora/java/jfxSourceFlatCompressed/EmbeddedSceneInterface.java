package com.sun.javafx.embed;
import java.nio.IntBuffer;
import com.sun.javafx.scene.traversal.Direction;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.InputMethodTextRun;
public interface EmbeddedSceneInterface {
public void setSize(int width, int height);
public void setPixelScaleFactors(float scalex, float scaley);
public boolean getPixels(IntBuffer dest, int width, int height);
public PixelFormat<?> getPixelFormat();
public void mouseEvent(int type, int button,
boolean primaryBtnDown, boolean middleBtnDown, boolean secondaryBtnDown,
boolean backBtnDown, boolean forwardBtnDown,
int x, int y, int xAbs, int yAbs,
boolean shift, boolean ctrl, boolean alt, boolean meta,
boolean popupTrigger);
public void scrollEvent(int type, double scrollX, double scrollY,
double totalScrollX, double totalScrollY,
double xMultiplier, double yMultiplier,
double x, double y, double screenX, double screenY,
boolean shift, boolean ctrl,
boolean alt, boolean meta, boolean inertia);
public void keyEvent(int type, int key, char[] chars, int modifiers);
public void zoomEvent(final int type, final double zoomFactor, final double totalZoomFactor,
final double x, final double y, final double screenX, final double screenY,
boolean shift, boolean ctrl, boolean alt, boolean meta, boolean inertia);
public void rotateEvent(final int type, final double angle, final double totalAngle,
final double x, final double y, final double screenX, final double screenY,
boolean shift, boolean ctrl, boolean alt, boolean meta, boolean inertia);
public void swipeEvent(final int type, final double x, final double y, final double screenX, final double screenY,
boolean shift, boolean ctrl, boolean alt, boolean meta);
public void menuEvent(int x, int y, int xAbs, int yAbs, boolean isKeyboardTrigger);
public boolean traverseOut(Direction dir);
public void setDragStartListener(HostDragStartListener l);
public EmbeddedSceneDTInterface createDropTarget();
public void inputMethodEvent(EventType<InputMethodEvent> type,
ObservableList<InputMethodTextRun> composed, String committed,
int caretPosition);
public InputMethodRequests getInputMethodRequests();
}
