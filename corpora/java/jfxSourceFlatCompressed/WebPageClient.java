package com.sun.webkit;
import com.sun.webkit.graphics.WCPageBackBuffer;
import com.sun.webkit.graphics.WCPoint;
import com.sun.webkit.graphics.WCRectangle;
public interface WebPageClient<T> {
public void setCursor(long cursorID);
public void setFocus(boolean focus);
public void transferFocus(boolean forward);
public void setTooltip(String tooltip);
public WCRectangle getScreenBounds(boolean available);
public int getScreenDepth();
public T getContainer();
public WCPoint screenToWindow(WCPoint ptScreen);
public WCPoint windowToScreen(WCPoint ptWindow);
public WCPageBackBuffer createBackBuffer();
public boolean isBackBufferSupported();
public void addMessageToConsole(String message, int lineNumber,
String sourceId);
public void didClearWindowObject(long context, long windowObject);
}
