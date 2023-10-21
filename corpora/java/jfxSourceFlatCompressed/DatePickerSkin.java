package javafx.scene.control.skin;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.HijrahChronology;
import com.sun.javafx.scene.control.DatePickerContent;
import com.sun.javafx.scene.control.DatePickerHijrahContent;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ComboBoxBaseBehavior;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import com.sun.javafx.scene.control.behavior.DatePickerBehavior;
public class DatePickerSkin extends ComboBoxPopupControl<LocalDate> {
private final DatePicker datePicker;
private TextField displayNode;
private DatePickerContent datePickerContent;
private final DatePickerBehavior behavior;
public DatePickerSkin(final DatePicker control) {
super(control);
this.datePicker = control;
this.behavior = new DatePickerBehavior(control);
arrow.paddingProperty().addListener(new InvalidationListener() {
private boolean rounding = false;
@Override public void invalidated(Observable observable) {
if (!rounding) {
Insets padding = arrow.getPadding();
Insets rounded = new Insets(Math.round(padding.getTop()), Math.round(padding.getRight()),
Math.round(padding.getBottom()), Math.round(padding.getLeft()));
if (!rounded.equals(padding)) {
rounding = true;
arrow.setPadding(rounded);
rounding = false;
}
}
}
});
registerChangeListener(control.chronologyProperty(), e -> {
updateDisplayNode();
datePickerContent = null;
popup = null;
});
registerChangeListener(control.converterProperty(), e -> updateDisplayNode());
registerChangeListener(control.dayCellFactoryProperty(), e -> {
updateDisplayNode();
datePickerContent = null;
popup = null;
});
registerChangeListener(control.showWeekNumbersProperty(), e -> {
if (datePickerContent != null) {
datePickerContent.updateGrid();
datePickerContent.updateWeeknumberDateCells();
}
});
registerChangeListener(control.valueProperty(), e -> {
updateDisplayNode();
if (datePickerContent != null) {
LocalDate date = control.getValue();
datePickerContent.displayedYearMonthProperty().set((date != null) ? YearMonth.from(date) : YearMonth.now());
datePickerContent.updateValues();
}
control.fireEvent(new ActionEvent());
});
registerChangeListener(control.showingProperty(), e -> {
if (control.isShowing()) {
if (datePickerContent != null) {
LocalDate date = control.getValue();
datePickerContent.displayedYearMonthProperty().set((date != null) ? YearMonth.from(date) : YearMonth.now());
datePickerContent.updateValues();
}
show();
} else {
hide();
}
});
if (control.isShowing()) {
show();
}
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override public Node getPopupContent() {
if (datePickerContent == null) {
if (datePicker.getChronology() instanceof HijrahChronology) {
datePickerContent = new DatePickerHijrahContent(datePicker);
} else {
datePickerContent = new DatePickerContent(datePicker);
}
}
return datePickerContent;
}
@Override protected double computeMinWidth(double height,
double topInset, double rightInset,
double bottomInset, double leftInset) {
return 50;
}
@Override public void show() {
super.show();
datePickerContent.clearFocus();
}
@Override protected TextField getEditor() {
return ((DatePicker)getSkinnable()).getEditor();
}
@Override protected StringConverter<LocalDate> getConverter() {
return ((DatePicker)getSkinnable()).getConverter();
}
@Override public Node getDisplayNode() {
if (displayNode == null) {
displayNode = getEditableInputNode();
displayNode.getStyleClass().add("date-picker-display-node");
updateDisplayNode();
}
displayNode.setEditable(datePicker.isEditable());
return displayNode;
}
@Override void focusLost() {
}
@Override ComboBoxBaseBehavior getBehavior() {
return behavior;
}
}
