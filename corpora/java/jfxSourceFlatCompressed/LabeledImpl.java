package com.sun.javafx.scene.control;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.css.CssMetaData;
import javafx.css.StyleOrigin;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.scene.layout.Region;
public class LabeledImpl extends Label {
public LabeledImpl(final Labeled labeled) {
shuttler = new Shuttler(this, labeled);
}
private final Shuttler shuttler;
private static void initialize(Shuttler shuttler, LabeledImpl labeledImpl, Labeled labeled) {
labeledImpl.setText(labeled.getText());
labeled.textProperty().addListener(shuttler);
labeledImpl.setGraphic(labeled.getGraphic());
labeled.graphicProperty().addListener(shuttler);
final List<CssMetaData<? extends Styleable, ?>> styleables = StyleableProperties.STYLEABLES_TO_MIRROR;
for(int n=0, nMax=styleables.size(); n<nMax; n++) {
@SuppressWarnings("unchecked")
final CssMetaData<Styleable,Object> styleable = (CssMetaData<Styleable,Object>)styleables.get(n);
if ("-fx-skin".equals(styleable.getProperty())) continue;
final StyleableProperty<?> fromVal = styleable.getStyleableProperty(labeled);
if (fromVal instanceof Observable) {
((Observable)fromVal).addListener(shuttler);
final StyleOrigin origin = fromVal.getStyleOrigin();
if (origin == null) continue;
final StyleableProperty<Object> styleableProperty = styleable.getStyleableProperty(labeledImpl);
styleableProperty.applyStyle(origin, fromVal.getValue());
}
}
}
private static class Shuttler implements InvalidationListener {
private final LabeledImpl labeledImpl;
private final Labeled labeled;
Shuttler(LabeledImpl labeledImpl, Labeled labeled) {
this.labeledImpl = labeledImpl;
this.labeled = labeled;
initialize(this, labeledImpl, labeled);
}
@Override public void invalidated(Observable valueModel) {
if (valueModel == labeled.textProperty()) {
labeledImpl.setText(labeled.getText());
} else if (valueModel == labeled.graphicProperty()) {
StyleOrigin origin = ((StyleableProperty<?>)labeled.graphicProperty()).getStyleOrigin();
if (origin == null || origin == StyleOrigin.USER) {
labeledImpl.setGraphic(labeled.getGraphic());
}
} else if (valueModel instanceof StyleableProperty) {
StyleableProperty<?> styleableProperty = (StyleableProperty<?>)valueModel;
@SuppressWarnings("unchecked")
CssMetaData<Styleable,Object> cssMetaData = (CssMetaData<Styleable,Object>)styleableProperty.getCssMetaData();
if (cssMetaData != null) {
StyleOrigin origin = styleableProperty.getStyleOrigin();
StyleableProperty<Object> targetProperty = cssMetaData.getStyleableProperty(labeledImpl);
targetProperty.applyStyle(origin, styleableProperty.getValue());
}
}
}
}
static final class StyleableProperties {
static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES_TO_MIRROR;
static {
final List<CssMetaData<? extends Styleable, ?>> labeledStyleables = Labeled.getClassCssMetaData();
final List<CssMetaData<? extends Styleable, ?>> parentStyleables = Region.getClassCssMetaData();
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(labeledStyleables);
styleables.removeAll(parentStyleables);
STYLEABLES_TO_MIRROR = Collections.unmodifiableList(styleables);
}
}
}
