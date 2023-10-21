package com.sun.javafx.webkit;
import javafx.scene.web.WebView;
public interface WebConsoleListener {
public static void setDefaultListener(WebConsoleListener l) {
WebPageClientImpl.setConsoleListener(l);
}
void messageAdded(WebView webView, String message, int lineNumber, String sourceId);
}
