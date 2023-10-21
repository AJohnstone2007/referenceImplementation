package com.sun.webkit.plugin;
public interface PluginListener {
void fwkRedraw(
final int x, final int y,
final int width, final int height,
final boolean eraseBackground);
String fwkEvent(
final int eventId,
final String name, final String params);
}
