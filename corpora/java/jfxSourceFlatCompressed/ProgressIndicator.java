package javafx.scene.control;
import javafx.css.PseudoClass;
import javafx.scene.control.skin.ProgressIndicatorSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
public class ProgressIndicator extends Control {
public static final double INDETERMINATE_PROGRESS = -1;
public ProgressIndicator() {
this(INDETERMINATE_PROGRESS);
}
public ProgressIndicator(double progress) {
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
setProgress(progress);
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.PROGRESS_INDICATOR);
final int c = Double.compare(INDETERMINATE_PROGRESS, progress);
pseudoClassStateChanged(PSEUDO_CLASS_INDETERMINATE, c == 0);
pseudoClassStateChanged(PSEUDO_CLASS_DETERMINATE, c != 0);
}
private ReadOnlyBooleanWrapper indeterminate;
private void setIndeterminate(boolean value) {
indeterminatePropertyImpl().set(value);
}
public final boolean isIndeterminate() {
return indeterminate == null ? true : indeterminate.get();
}
public final ReadOnlyBooleanProperty indeterminateProperty() {
return indeterminatePropertyImpl().getReadOnlyProperty();
}
private ReadOnlyBooleanWrapper indeterminatePropertyImpl() {
if (indeterminate == null) {
indeterminate = new ReadOnlyBooleanWrapper(true) {
@Override protected void invalidated() {
final boolean active = get();
pseudoClassStateChanged(PSEUDO_CLASS_INDETERMINATE, active);
pseudoClassStateChanged(PSEUDO_CLASS_DETERMINATE, !active);
}
@Override
public Object getBean() {
return ProgressIndicator.this;
}
@Override
public String getName() {
return "indeterminate";
}
};
}
return indeterminate;
}
private DoubleProperty progress;
public final void setProgress(double value) {
progressProperty().set(value);
}
public final double getProgress() {
return progress == null ? INDETERMINATE_PROGRESS : progress.get();
}
public final DoubleProperty progressProperty() {
if (progress == null) {
progress = new DoublePropertyBase(-1.0) {
@Override protected void invalidated() {
setIndeterminate(getProgress() < 0.0);
}
@Override
public Object getBean() {
return ProgressIndicator.this;
}
@Override
public String getName() {
return "progress";
}
};
}
return progress;
}
@Override protected Skin<?> createDefaultSkin() {
return new ProgressIndicatorSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "progress-indicator";
private static final PseudoClass PSEUDO_CLASS_DETERMINATE =
PseudoClass.getPseudoClass("determinate");
private static final PseudoClass PSEUDO_CLASS_INDETERMINATE =
PseudoClass.getPseudoClass("indeterminate");
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case VALUE: return getProgress();
case MAX_VALUE: return 1.0;
case MIN_VALUE: return 0.0;
case INDETERMINATE: return isIndeterminate();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
