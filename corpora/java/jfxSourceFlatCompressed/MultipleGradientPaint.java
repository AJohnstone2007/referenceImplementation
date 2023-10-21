package com.sun.prism.j2d.paint;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.lang.ref.SoftReference;
public abstract class MultipleGradientPaint implements Paint {
public static enum CycleMethod {
NO_CYCLE,
REFLECT,
REPEAT
}
public static enum ColorSpaceType {
SRGB,
LINEAR_RGB
}
final int transparency;
final float[] fractions;
final Color[] colors;
final AffineTransform gradientTransform;
final CycleMethod cycleMethod;
final ColorSpaceType colorSpace;
ColorModel model;
float[] normalizedIntervals;
boolean isSimpleLookup;
SoftReference<int[][]> gradients;
SoftReference<int[]> gradient;
int fastGradientArraySize;
MultipleGradientPaint(float[] fractions,
Color[] colors,
CycleMethod cycleMethod,
ColorSpaceType colorSpace,
AffineTransform gradientTransform)
{
if (fractions == null) {
throw new NullPointerException("Fractions array cannot be null");
}
if (colors == null) {
throw new NullPointerException("Colors array cannot be null");
}
if (cycleMethod == null) {
throw new NullPointerException("Cycle method cannot be null");
}
if (colorSpace == null) {
throw new NullPointerException("Color space cannot be null");
}
if (gradientTransform == null) {
throw new NullPointerException("Gradient transform cannot be "+
"null");
}
if (fractions.length != colors.length) {
throw new IllegalArgumentException("Colors and fractions must " +
"have equal size");
}
if (colors.length < 2) {
throw new IllegalArgumentException("User must specify at least " +
"2 colors");
}
float previousFraction = -1.0f;
for (float currentFraction : fractions) {
if (currentFraction < 0f || currentFraction > 1f) {
throw new IllegalArgumentException("Fraction values must " +
"be in the range 0 to 1: " +
currentFraction);
}
if (currentFraction <= previousFraction) {
throw new IllegalArgumentException("Keyframe fractions " +
"must be increasing: " +
currentFraction);
}
previousFraction = currentFraction;
}
boolean fixFirst = false;
boolean fixLast = false;
int len = fractions.length;
int off = 0;
if (fractions[0] != 0f) {
fixFirst = true;
len++;
off++;
}
if (fractions[fractions.length-1] != 1f) {
fixLast = true;
len++;
}
this.fractions = new float[len];
System.arraycopy(fractions, 0, this.fractions, off, fractions.length);
this.colors = new Color[len];
System.arraycopy(colors, 0, this.colors, off, colors.length);
if (fixFirst) {
this.fractions[0] = 0f;
this.colors[0] = colors[0];
}
if (fixLast) {
this.fractions[len-1] = 1f;
this.colors[len-1] = colors[colors.length - 1];
}
this.colorSpace = colorSpace;
this.cycleMethod = cycleMethod;
this.gradientTransform = new AffineTransform(gradientTransform);
boolean opaque = true;
for (int i = 0; i < colors.length; i++){
opaque = opaque && (colors[i].getAlpha() == 0xff);
}
this.transparency = opaque ? OPAQUE : TRANSLUCENT;
}
public final float[] getFractions() {
float[] copy = new float[fractions.length];
System.arraycopy(fractions, 0, copy, 0, fractions.length);
return copy;
}
public final Color[] getColors() {
Color[] copy = new Color[fractions.length];
System.arraycopy(fractions, 0, copy, 0, fractions.length);
return copy;
}
public final CycleMethod getCycleMethod() {
return cycleMethod;
}
public final ColorSpaceType getColorSpace() {
return colorSpace;
}
public final AffineTransform getTransform() {
return new AffineTransform(gradientTransform);
}
public final int getTransparency() {
return transparency;
}
}
