package com.sun.javafx.scene.control;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.css.*;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
public class LabeledText extends Text {
private final Labeled labeled;
public LabeledText(Labeled labeled) {
super();
if (labeled == null) {
throw new IllegalArgumentException("labeled cannot be null");
}
this.labeled = labeled;
this.setFill(this.labeled.getTextFill());
this.setFont(this.labeled.getFont());
this.setTextAlignment(this.labeled.getTextAlignment());
this.setUnderline(this.labeled.isUnderline());
this.setLineSpacing(this.labeled.getLineSpacing());
this.fillProperty().bind(this.labeled.textFillProperty());
this.fontProperty().bind(this.labeled.fontProperty());
this.textAlignmentProperty().bind(this.labeled.textAlignmentProperty());
this.underlineProperty().bind(this.labeled.underlineProperty());
this.lineSpacingProperty().bind(this.labeled.lineSpacingProperty());
getStyleClass().addAll("text");
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
private StyleablePropertyMirror<Font> fontMirror = null;
private StyleableProperty<Font> fontMirror() {
if (fontMirror == null) {
fontMirror = new StyleablePropertyMirror<Font>(FONT, "fontMirror", Font.getDefault(), (StyleableProperty<Font>)(WritableValue<Font>)labeled.fontProperty());
fontProperty().addListener(fontMirror);
}
return fontMirror;
}
private static final CssMetaData<LabeledText,Font> FONT =
new FontCssMetaData<LabeledText>("-fx-font", Font.getDefault()) {
@Override
public boolean isSettable(LabeledText node) {
return node.labeled != null ? node.labeled.fontProperty().isBound() == false : true;
}
@Override
public StyleableProperty<Font> getStyleableProperty(LabeledText node) {
return node.fontMirror();
}
};
private StyleablePropertyMirror<Paint> fillMirror;
private StyleableProperty<Paint> fillMirror() {
if (fillMirror == null) {
fillMirror = new StyleablePropertyMirror<Paint>(FILL, "fillMirror", Color.BLACK, (StyleableProperty<Paint>)(WritableValue<Paint>)labeled.textFillProperty());
fillProperty().addListener(fillMirror);
}
return fillMirror;
}
private static final CssMetaData<LabeledText,Paint> FILL =
new CssMetaData<LabeledText,Paint>("-fx-fill",
PaintConverter.getInstance(), Color.BLACK) {
@Override
public boolean isSettable(LabeledText node) {
return node.labeled.textFillProperty().isBound() == false;
}
@Override
public StyleableProperty<Paint> getStyleableProperty(LabeledText node) {
return node.fillMirror();
}
};
private StyleablePropertyMirror<TextAlignment> textAlignmentMirror;
private StyleableProperty<TextAlignment> textAlignmentMirror() {
if (textAlignmentMirror == null) {
textAlignmentMirror = new StyleablePropertyMirror<TextAlignment>(TEXT_ALIGNMENT, "textAlignmentMirror", TextAlignment.LEFT, (StyleableProperty<TextAlignment>)(WritableValue<TextAlignment>)labeled.textAlignmentProperty());
textAlignmentProperty().addListener(textAlignmentMirror);
}
return textAlignmentMirror;
}
private static final CssMetaData<LabeledText,TextAlignment> TEXT_ALIGNMENT =
new CssMetaData<LabeledText,TextAlignment>("-fx-text-alignment",
new EnumConverter<TextAlignment>(TextAlignment.class),
TextAlignment.LEFT) {
@Override
public boolean isSettable(LabeledText node) {
return node.labeled.textAlignmentProperty().isBound() == false;
}
@Override
public StyleableProperty<TextAlignment> getStyleableProperty(LabeledText node) {
return node.textAlignmentMirror();
}
};
private StyleablePropertyMirror<Boolean> underlineMirror;
private StyleableProperty<Boolean> underlineMirror() {
if (underlineMirror == null) {
underlineMirror = new StyleablePropertyMirror<Boolean>(UNDERLINE, "underLineMirror", Boolean.FALSE, (StyleableProperty<Boolean>)(WritableValue<Boolean>)labeled.underlineProperty());
underlineProperty().addListener(underlineMirror);
}
return underlineMirror;
}
private static final CssMetaData<LabeledText,Boolean> UNDERLINE =
new CssMetaData<LabeledText,Boolean>("-fx-underline",
BooleanConverter.getInstance(),
Boolean.FALSE) {
@Override
public boolean isSettable(LabeledText node) {
return node.labeled.underlineProperty().isBound() == false;
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(LabeledText node) {
return node.underlineMirror();
}
};
private StyleablePropertyMirror<Number> lineSpacingMirror;
private StyleableProperty<Number> lineSpacingMirror() {
if (lineSpacingMirror == null) {
lineSpacingMirror = new StyleablePropertyMirror<Number>(LINE_SPACING, "lineSpacingMirror", 0d, (StyleableProperty<Number>)(WritableValue<Number>)labeled.lineSpacingProperty());
lineSpacingProperty().addListener(lineSpacingMirror);
}
return lineSpacingMirror;
}
private static final CssMetaData<LabeledText,Number> LINE_SPACING =
new CssMetaData<LabeledText,Number>("-fx-line-spacing",
SizeConverter.getInstance(),
0) {
@Override
public boolean isSettable(LabeledText node) {
return node.labeled.lineSpacingProperty().isBound() == false;
}
@Override
public StyleableProperty<Number> getStyleableProperty(LabeledText node) {
return node.lineSpacingMirror();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Text.getClassCssMetaData());
for (int n=0,nMax=styleables.size(); n<nMax; n++) {
final String prop = styleables.get(n).getProperty();
if ("-fx-fill".equals(prop)) {
styleables.set(n, FILL);
} else if ("-fx-font".equals(prop)) {
styleables.set(n, FONT);
} else if ("-fx-text-alignment".equals(prop)) {
styleables.set(n, TEXT_ALIGNMENT);
} else if ("-fx-underline".equals(prop)) {
styleables.set(n, UNDERLINE);
} else if ("-fx-line-spacing".equals(prop)) {
styleables.set(n, LINE_SPACING);
}
}
STYLEABLES = Collections.unmodifiableList(styleables);
}
private class StyleablePropertyMirror<T> extends SimpleStyleableObjectProperty<T> implements InvalidationListener {
private StyleablePropertyMirror(CssMetaData<LabeledText, T> cssMetaData, String name, T initialValue, StyleableProperty<T> property) {
super(cssMetaData, LabeledText.this, name, initialValue);
this.property = property;
this.applying = false;
}
@Override
public void invalidated(Observable observable) {
if (applying == false) {
super.applyStyle(null, ((ObservableValue<T>)observable).getValue());
}
}
@Override
public void applyStyle(StyleOrigin newOrigin, T value) {
applying = true;
StyleOrigin propOrigin = property.getStyleOrigin();
if (propOrigin == null ||
(newOrigin != null
? propOrigin.compareTo(newOrigin) <= 0
: propOrigin != StyleOrigin.USER)) {
super.applyStyle(newOrigin, value);
property.applyStyle(newOrigin, value);
}
applying = false;
}
@Override public StyleOrigin getStyleOrigin() {
return property.getStyleOrigin();
}
boolean applying;
private final StyleableProperty<T> property;
}
}
