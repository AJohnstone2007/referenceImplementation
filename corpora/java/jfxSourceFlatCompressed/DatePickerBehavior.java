package com.sun.javafx.scene.control.behavior;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PopupControl;
import java.time.LocalDate;
public class DatePickerBehavior extends ComboBoxBaseBehavior<LocalDate> {
public DatePickerBehavior(final DatePicker datePicker) {
super(datePicker);
}
@Override public void onAutoHide(PopupControl popup) {
if (!popup.isShowing() && getNode().isShowing()) {
getNode().hide();
}
if (!getNode().isShowing()) {
super.onAutoHide(popup);
}
}
}
