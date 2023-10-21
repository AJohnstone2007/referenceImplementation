package com.sun.javafx.font;
public interface CompositeFontResource extends FontResource {
public FontResource getSlotResource(int slot);
public int getNumSlots();
public int getSlotForFont(String fontName);
}
