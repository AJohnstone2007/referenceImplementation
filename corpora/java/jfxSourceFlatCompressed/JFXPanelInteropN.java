package com.sun.javafx.embed.swing.newimpl;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.Window;
import javafx.embed.swing.JFXPanel;
import jdk.swing.interop.SwingInterOpUtils;
public class JFXPanelInteropN {
public void postEvent(JFXPanel panel, AWTEvent e) {
SwingInterOpUtils.postEvent(panel, e);
}
public boolean isUngrabEvent(AWTEvent event) {
return SwingInterOpUtils.isUngrabEvent(event);
}
public long getMask() {
return SwingInterOpUtils.GRAB_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK;
}
public void grab(Toolkit toolkit, Window w) {
SwingInterOpUtils.grab(toolkit, w);
}
public void ungrab(Toolkit toolkit, Window w) {
SwingInterOpUtils.ungrab(toolkit, w);
}
}
