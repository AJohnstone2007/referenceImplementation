package com.sun.javafx.text;
import com.sun.javafx.scene.text.TextLayoutFactory;
public class PrismTextLayoutFactory implements TextLayoutFactory {
private static final PrismTextLayout reusableTL = new PrismTextLayout();
private static boolean inUse;
private PrismTextLayoutFactory() {
}
public com.sun.javafx.scene.text.TextLayout createLayout() {
return new PrismTextLayout();
}
public com.sun.javafx.scene.text.TextLayout getLayout() {
if (inUse) {
return new PrismTextLayout();
} else {
synchronized(PrismTextLayoutFactory.class) {
if (inUse) {
return new PrismTextLayout();
} else {
inUse = true;
reusableTL.setAlignment(0);
reusableTL.setWrapWidth(0);
reusableTL.setDirection(0);
reusableTL.setContent(null);
return reusableTL;
}
}
}
}
public void disposeLayout(com.sun.javafx.scene.text.TextLayout layout) {
if (layout == reusableTL) {
inUse = false;
}
}
private static final PrismTextLayoutFactory factory = new PrismTextLayoutFactory();
public static PrismTextLayoutFactory getFactory() {
return factory;
}
}
