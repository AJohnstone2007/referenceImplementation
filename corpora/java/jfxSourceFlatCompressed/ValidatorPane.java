package ensemble.samples.controls.text.textvalidator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.layout.Region;
public abstract class ValidatorPane<C extends Control> extends Region {
private ObjectProperty<C> content =
new SimpleObjectProperty<C>(this, "content", null);
public final C getContent() {
return content.get();
}
public final void setContent(C value) {
content.set(value);
}
public final ObjectProperty<C> contentProperty() {
return content;
}
private ObjectProperty<Validator<C>> validator =
new SimpleObjectProperty<Validator<C>>(this, "validator");
public final Validator<C> getValidator() {
return validator.get();
}
public final void setValidator(Validator<C> value) {
validator.set(value);
}
public final ObjectProperty<Validator<C>> validatorProperty() {
return validator;
}
private ReadOnlyObjectWrapper<ValidationResult> validationResult =
new ReadOnlyObjectWrapper<ValidationResult>(this, "validationResult");
public final ValidationResult getValidationResult() {
return validationResult.get();
}
public final ReadOnlyObjectProperty<ValidationResult> validationResultProperty() {
return validationResult.getReadOnlyProperty();
}
private ObjectProperty<EventHandler<ValidationEvent>> onValidation =
new SimpleObjectProperty<EventHandler<ValidationEvent>>(this,
"onValidation");
public final EventHandler<ValidationEvent> getOnValidation() {
return onValidation.get();
}
public final void setOnValidation(EventHandler<ValidationEvent> value) {
onValidation.set(value);
}
public final ObjectProperty<EventHandler<ValidationEvent>> onValidationProperty() {
return onValidation;
}
public ValidatorPane() {
content.addListener((ObservableValue<? extends Control> ov,
Control oldValue, Control newValue) -> {
if (oldValue != null) {
getChildren().remove(oldValue);
}
if (newValue != null) {
getChildren().add(0, newValue);
}
});
}
protected void handleValidationResult(ValidationResult result) {
getStyleClass().removeAll("validation-error", "validation-warning");
if (result != null) {
if (result.getType() == ValidationResult.Type.ERROR) {
getStyleClass().add("validation-error");
} else if (result.getType() == ValidationResult.Type.WARNING) {
getStyleClass().add("validation-warning");
}
}
validationResult.set(result);
fireEvent(new ValidationEvent(result));
}
@Override
protected void layoutChildren() {
Control c = content.get();
if (c != null) {
c.resizeRelocate(0, 0, getWidth(), getHeight());
}
}
@Override
protected double computeMaxHeight(double d) {
Control c = content.get();
return c == null ? super.computeMaxHeight(d) : c.maxHeight(d);
}
@Override
protected double computeMinHeight(double d) {
Control c = content.get();
return c == null ? super.computeMinHeight(d) : c.minHeight(d);
}
@Override
protected double computePrefHeight(double d) {
Control c = content.get();
return c == null ? super.computePrefHeight(d) : c.prefHeight(d);
}
@Override
protected double computePrefWidth(double d) {
Control c = content.get();
return c == null ? super.computePrefWidth(d) : c.prefWidth(d);
}
@Override
protected double computeMaxWidth(double d) {
Control c = content.get();
return c == null ? super.computeMaxWidth(d) : c.maxWidth(d);
}
@Override
protected double computeMinWidth(double d) {
Control c = content.get();
return c == null ? super.computeMinWidth(d) : c.minWidth(d);
}
}
