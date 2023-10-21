package com.sun.webkit.plugin;
import java.io.IOException;
import com.sun.webkit.graphics.WCGraphicsContext;
public interface Plugin {
public final static int EVENT_BEFOREACTIVATE = -4;
public final static int EVENT_FOCUSCHANGE = -1;
public void requestFocus();
public void setNativeContainerBounds(int x, int y, int width, int height);
void activate(Object nativeContainer, PluginListener pl);
void destroy();
void setVisible(boolean isVisible);
public void setEnabled(boolean enabled);
void setBounds(int x, int y, int width, int height);
Object invoke(
String subObjectId,
String methodName,
Object[] args) throws IOException;
public void paint(WCGraphicsContext g, int intX, int intY, int intWidth, int intHeight);
public boolean handleMouseEvent(
String type,
int offsetX,
int offsetY,
int screenX,
int screenY,
int button,
boolean buttonDown,
boolean altKey,
boolean metaKey,
boolean ctrlKey,
boolean shiftKey,
long timeStamp);
}
