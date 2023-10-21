package com.sun.javafx.tk;
import java.security.AccessControlContext;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.sg.prism.NGNode;
public interface TKScene {
public void dispose();
public void waitForRenderingToComplete();
public void waitForSynchronization();
public void releaseSynchronization(boolean updateState);
public void setTKSceneListener(TKSceneListener listener);
public void setTKScenePaintListener(final TKScenePaintListener listener);
public void setRoot(NGNode root);
public void markDirty();
public void setCamera(NGCamera camera);
NGLightBase[] getLights();
public void setLights(NGLightBase[] lights);
public void setFillPaint(Object fillPaint);
public void setCursor(Object cursor);
public void enableInputMethodEvents(boolean enable);
public void finishInputMethodComposition();
public void entireSceneNeedsRepaint();
public TKClipboard createDragboard(boolean isDragSource);
@SuppressWarnings("removal")
public AccessControlContext getAccessControlContext();
}
