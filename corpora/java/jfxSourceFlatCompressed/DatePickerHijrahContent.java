package com.sun.javafx.scene.control;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DecimalStyle;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.IsoChronology;
import java.util.Locale;
import static java.time.temporal.ChronoField.*;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
public class DatePickerHijrahContent extends DatePickerContent {
private Label hijrahMonthYearLabel;
public DatePickerHijrahContent(final DatePicker datePicker) {
super(datePicker);
}
@Override protected Chronology getPrimaryChronology() {
return IsoChronology.INSTANCE;
}
@Override protected BorderPane createMonthYearPane() {
BorderPane monthYearPane = super.createMonthYearPane();
hijrahMonthYearLabel = new Label();
hijrahMonthYearLabel.getStyleClass().add("secondary-label");
monthYearPane.setBottom(hijrahMonthYearLabel);
BorderPane.setAlignment(hijrahMonthYearLabel, Pos.CENTER);
return monthYearPane;
}
@Override protected void updateMonthYearPane() {
super.updateMonthYearPane();
Locale locale = getLocale();
HijrahChronology chrono = HijrahChronology.INSTANCE;
long firstMonth = -1;
long firstYear = -1;
String firstMonthStr = null;
String firstYearStr = null;
String hijrahStr = null;
YearMonth displayedYearMonth = displayedYearMonthProperty().get();
for (DateCell dayCell : dayCells) {
LocalDate date = dayCellDate(dayCell);
if (!displayedYearMonth.equals(YearMonth.from(date))) {
continue;
}
try {
HijrahDate cDate = chrono.date(date);
long month = cDate.getLong(MONTH_OF_YEAR);
long year = cDate.getLong(YEAR);
if (hijrahStr == null || month != firstMonth) {
String monthStr = monthFormatter.withLocale(locale)
.withChronology(chrono)
.withDecimalStyle(DecimalStyle.of(locale))
.format(cDate);
String yearStr = yearFormatter.withLocale(locale)
.withChronology(chrono)
.withDecimalStyle(DecimalStyle.of(locale))
.format(cDate);
if (hijrahStr == null) {
firstMonth = month;
firstYear = year;
firstMonthStr = monthStr;
firstYearStr = yearStr;
hijrahStr = firstMonthStr + " " + firstYearStr;
} else {
if (year > firstYear) {
hijrahStr = firstMonthStr + " " + firstYearStr + " - " + monthStr + " " + yearStr;
} else {
hijrahStr = firstMonthStr + " - " + monthStr + " " + firstYearStr;
}
break;
}
}
} catch (DateTimeException ex) {
}
}
hijrahMonthYearLabel.setText(hijrahStr);
}
@Override protected void createDayCells() {
super.createDayCells();
for (DateCell dayCell : dayCells) {
Text secondaryText = new Text();
dayCell.getProperties().put("DateCell.secondaryText", secondaryText);
}
}
@Override public void updateDayCells() {
super.updateDayCells();
Locale locale = getLocale();
HijrahChronology chrono = HijrahChronology.INSTANCE;
int majorityMonth = -1;
int visibleDaysInMajorityMonth = -1;
int curMonth = -1;
int visibleDaysInCurMonth = 0;
for (DateCell dayCell : dayCells) {
Text secondaryText = (Text)dayCell.getProperties().get("DateCell.secondaryText");
dayCell.getStyleClass().add("hijrah-day-cell");
secondaryText.getStyleClass().setAll("text", "secondary-text");
try {
HijrahDate cDate = chrono.date(dayCellDate(dayCell));
String hijrahStr =
dayCellFormatter.withLocale(locale)
.withChronology(chrono)
.withDecimalStyle(DecimalStyle.of(locale))
.format(cDate);
secondaryText.setText(hijrahStr);
dayCell.requestLayout();
} catch (DateTimeException ex) {
secondaryText.setText(" ");
dayCell.setDisable(true);
}
}
}
}
