package hello;
import java.time.LocalDate;
import java.time.MonthDay;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
public class HelloDatePicker extends Application {
static ObservableList<String> locales = FXCollections.observableArrayList();
static {
locales.addAll(new String[] {
"en_US",
"ar_SA",
"en_GB",
"cs_CZ",
"el_GR",
"he_IL",
"hi_IN",
"ja_JP",
"ja_JP-u-ca-japanese",
"ru_RU",
"sv_SE",
"th_TH",
"th-TH-u-ca-buddhist",
"th-TH-u-ca-buddhist-nu-thai",
"tr-TR",
"zh_CN",
"en-US-u-ca-islamic-umalqura",
"ar-SA-u-ca-islamic-umalqura",
"en-u-ca-japanese",
"en-u-ca-japanese-nu-thai"
});
}
LocalDate today = LocalDate.now();
LocalDate tomorrow = today.plusDays(1);
MonthDay birthday = null;
Stage stage;
DatePicker datePicker;
ChoiceBox<String> cssChoice;
CheckBox showWeekNumbersCB = new CheckBox();
CheckBox customCellFactoryCB = new CheckBox();
Button setTodayButton = new Button();
Button setBirthdayButton = new Button();
public static void main(String[] args) {
launch(args);
}
@Override public void start(Stage stage) {
this.stage = stage;
stage.setTitle("DatePicker");
initUI();
stage.show();
updateCSSChoice();
}
private void updateCSSChoice() {
String sheet = getUserAgentStylesheet();
if (sheet == null) {
sheet = STYLESHEET_MODENA;
}
cssChoice.setValue(sheet);
}
private void initUI() {
datePicker = new DatePicker();
VBox vbox = new VBox(20);
vbox.setStyle("-fx-padding: 10;");
datePicker.setOnAction((t) -> {
LocalDate isoDate = datePicker.getValue();
try {
ChronoLocalDate chronoDate =
((isoDate != null) ? datePicker.getChronology().date(isoDate) : null);
System.err.println("Selected date: " + chronoDate);
} catch (Exception ex) {
System.err.println(ex);
}
if (isoDate != null) {
setTodayButton.setDisable(isoDate.equals(today));
setBirthdayButton.setDisable(MonthDay.from(isoDate).equals(birthday));
} else {
setTodayButton.setDisable(true);
setBirthdayButton.setDisable(true);
}
});
final StringConverter<LocalDate> defaultConverter = datePicker.getConverter();
datePicker.setConverter(new StringConverter<LocalDate>() {
@Override public String toString(LocalDate value) {
return defaultConverter.toString(value);
}
@Override public LocalDate fromString(String text) {
try {
return defaultConverter.fromString(text);
} catch (DateTimeParseException ex) {
System.err.println("DatePicker: "+ex.getMessage());
throw ex;
}
}
});
final Callback<DatePicker, DateCell> dayCellFactory = (datePicker) -> {
return new DateCell() {
@Override public void updateItem(LocalDate item, boolean empty) {
super.updateItem(item, empty);
getStyleClass().remove("today");
if (item.equals(today)) {
getStyleClass().add("today");
}
getStyleClass().add(isWeekend(Locale.getDefault(), item) ? "weekend" : "weekday");
if (MonthDay.from(item).equals(birthday)) {
setTooltip(new Tooltip("Happy Birthday!"));
setStyle("-fx-background-color: #ff4444;");
}
if (item.equals(tomorrow)) {
setTooltip(new Tooltip("Tomorrow is not a good day"));
setDisable(true);
}
}
};
};
GridPane gridPane = new GridPane();
gridPane.setHgap(10);
gridPane.setVgap(10);
int row = 0;
{
Label label = new Label("Stylesheet:");
gridPane.add(label, 0, row);
GridPane.setHalignment(label, HPos.RIGHT);
cssChoice = new ChoiceBox<>();
cssChoice.getItems().addAll(STYLESHEET_MODENA, STYLESHEET_CASPIAN);
cssChoice.valueProperty().addListener((o) -> {
setUserAgentStylesheet(cssChoice.getValue());
});
gridPane.add(cssChoice, 1, row++);
}
{
Label label = new Label("Locale:");
gridPane.add(label, 0, row);
GridPane.setHalignment(label, HPos.RIGHT);
final ComboBox<String> localeComboBox = new ComboBox<>();
localeComboBox.getItems().addAll(locales);
localeComboBox.setEditable(true);
localeComboBox.setValue(Locale.getDefault().toLanguageTag());
localeComboBox.setOnAction((t) -> {
String str = localeComboBox.getValue();
if (str != null && !str.isEmpty()) {
Locale locale = Locale.forLanguageTag(str.replace('_', '-'));
Locale.setDefault(locale);
initUI();
updateCSSChoice();
showWeekNumbersCB.setSelected(datePicker.isShowWeekNumbers());
}
});
gridPane.add(localeComboBox, 1, row++);
}
{
Label label = new Label("DatePicker:");
gridPane.add(label, 0, row);
GridPane.setHalignment(label, HPos.RIGHT);
gridPane.add(datePicker, 1, row);
Button todayButton = new Button("Today");
todayButton.setOnAction((t) -> {
datePicker.setValue(today);
});
gridPane.add(todayButton, 2, row++);
}
{
Label label = new Label("Show week numbers: ");
gridPane.add(label, 0, row);
GridPane.setHalignment(label, HPos.RIGHT);
showWeekNumbersCB.setOnAction((t) -> {
datePicker.setShowWeekNumbers(showWeekNumbersCB.isSelected());
});
gridPane.add(showWeekNumbersCB, 1, row++);
}
{
Label label = new Label("Editable TextField: ");
gridPane.add(label, 0, row);
GridPane.setHalignment(label, HPos.RIGHT);
final CheckBox cb = new CheckBox();
cb.setSelected(true);
datePicker.editableProperty().bind(cb.selectedProperty());
gridPane.add(cb, 1, row++);
}
{
Label label = new Label("Custom cell factory: ");
gridPane.add(label, 0, row);
GridPane.setHalignment(label, HPos.RIGHT);
customCellFactoryCB.setOnAction((t) -> {
datePicker.setDayCellFactory(customCellFactoryCB.isSelected() ? dayCellFactory : null);
if (customCellFactoryCB.isSelected()) {
LocalDate isoDate = datePicker.getValue();
setTodayButton.setDisable(isoDate == null || isoDate.equals(today));
setBirthdayButton.setDisable(isoDate == null || MonthDay.from(isoDate).equals(birthday));
}
});
gridPane.add(customCellFactoryCB, 1, row++);
}
{
setTodayButton.setText("Set today");
setBirthdayButton.setText("Set birthday");
Label todayLabel = new Label();
Label birthdayLabel = new Label();
todayLabel.setText("Today = "+datePicker.getConverter().toString(today));
birthdayLabel.setText("Birthday = "+birthday);
setTodayButton.visibleProperty().bind(customCellFactoryCB.selectedProperty());
setBirthdayButton.visibleProperty().bind(customCellFactoryCB.selectedProperty());
todayLabel.visibleProperty().bind(customCellFactoryCB.selectedProperty());
birthdayLabel.visibleProperty().bind(customCellFactoryCB.selectedProperty());
setTodayButton.setOnAction((t) -> {
LocalDate date = datePicker.getValue();
if (date != null) {
today = date;
setTodayButton.setDisable(true);
todayLabel.setText("Today = "+datePicker.getConverter().toString(today));
}
});
setBirthdayButton.setOnAction((t) -> {
LocalDate date = datePicker.getValue();
if (date != null) {
birthday = MonthDay.from(date);
setBirthdayButton.setDisable(true);
birthdayLabel.setText("Birthday = "+birthday);
}
});
gridPane.add(todayLabel, 1, row);
gridPane.add(setTodayButton, 2, row++);
gridPane.add(birthdayLabel, 1, row);
gridPane.add(setBirthdayButton, 2, row++);
}
vbox.getChildren().add(gridPane);
Scene scene = new Scene(vbox, 600, 400);
scene.getStylesheets().add("hello/hello.css");
String lang = Locale.getDefault().getLanguage();
if (lang.equals("ar") || lang.equals(new Locale("he").getLanguage())) {
scene.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
} else {
scene.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
}
stage.setScene(scene);
datePicker.requestFocus();
}
private boolean isWeekend(Locale locale, LocalDate date) {
return (date.getDayOfWeek().getValue() >= 6);
}
}
