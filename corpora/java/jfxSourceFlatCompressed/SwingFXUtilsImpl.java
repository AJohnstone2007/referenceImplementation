package com.sun.javafx.embed.swing;
import java.awt.EventQueue;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.javafx.embed.swing.newimpl.SwingFXUtilsImplInteropN;
public class SwingFXUtilsImpl {
private static SwingFXUtilsImplInteropN swFXUtilIOP;
static {
swFXUtilIOP = new SwingFXUtilsImplInteropN();
}
@SuppressWarnings("removal")
private static EventQueue getEventQueue() {
return AccessController.doPrivileged(
(PrivilegedAction<EventQueue>) () -> java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue());
}
public static void installFwEventQueue() {
swFXUtilIOP.setFwDispatcher(getEventQueue());
}
public static void removeFwEventQueue() {
swFXUtilIOP.setFwDispatcher(getEventQueue());
}
}
