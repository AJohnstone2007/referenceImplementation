package ensemble.samples.controls.datepicker;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Locale;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
public class DatePickerApp extends Application {
private final static ObservableList<String> locales =
FXCollections.observableArrayList();
private DatePicker datePicker;
private MenuBar datePickerMenuBar;
private final LocalDate today = LocalDate.now();
private final LocalDate tomorrow = today.plusDays(1);
private Locale originalLocale;
private HBox hbox;
static {
locales.addAll(new String[]{
"en-US",
"ar-SA",
"en-GB",
"cs-CZ",
"el-GR",
"he-IL",
"hi-IN",
"ja-JP",
"ja-JP-u-ca-japanese",
"ru-RU",
"sv-SE",
"th-TH",
"th-TH-u-ca-buddhist",
"th-TH-u-ca-buddhist-nu-thai",
"zh-CN",
"en-US-u-ca-islamic-umalqura",
"ar-SA-u-ca-islamic-umalqura",
"en-u-ca-japanese-nu-thai"
});
}
public Parent createContent() {
Text datePickerText = new Text("Date:");
hbox = new HBox(18);
hbox.setAlignment(Pos.CENTER);
hbox.getChildren().add(datePickerText);
datePicker = createDatePicker();
VBox vbox = new VBox(22);
vbox.getChildren().addAll(datePickerMenuBar, hbox);
vbox.setPrefSize(300, 200);
vbox.setMinSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
return vbox;
}
private DatePicker createDatePicker() {
hbox.getChildren().remove(datePicker);
LocalDate value = null;
if (datePicker != null) {
value = datePicker.getValue();
}
DatePicker picker = new DatePicker();
final Callback<DatePicker, DateCell> dayCellFactory =
new Callback<DatePicker, DateCell>() {
@Override
public DateCell call(final DatePicker datePicker) {
return new DateCell() {
@Override
public void updateItem(LocalDate item, boolean empty) {
super.updateItem(item, empty);
if (item.isBefore(today)) {
setStyle("-fx-background-color: #8099ff;");
} else {
if (item.equals(tomorrow)) {
setTooltip(new Tooltip("Tomorrow is important"));
}
}
}
};
}
};
datePickerMenuBar = createMenuBar(dayCellFactory);
picker.setOnAction((ActionEvent t) -> {
LocalDate isoDate = picker.getValue();
if ((isoDate != null) && (!isoDate.equals(LocalDate.now()))) {
for (Menu menu : datePickerMenuBar.getMenus()) {
if (menu.getText().equals("Options for Locale")) {
for (MenuItem menuItem : menu.getItems()) {
if (menuItem.getText().equals("Set date to today")) {
if ((menuItem instanceof CheckMenuItem) &&
((CheckMenuItem)menuItem).isSelected()) {
((CheckMenuItem)menuItem).setSelected(false);
}
}
}
}
}
}
});
hbox.getChildren().add(picker);
if (value != null) {
picker.setValue(value);
}
return picker;
}
private MenuBar createMenuBar(final Callback<DatePicker, DateCell> dayCellFac) {
final MenuBar menuBar = new MenuBar();
final ToggleGroup localeToggleGroup = new ToggleGroup();
Menu localeMenu = new Menu("Locales");
Iterator<String> localeIterator = locales.iterator();
while (localeIterator.hasNext()) {
RadioMenuItem localeMenuItem = new RadioMenuItem(localeIterator.next());
localeMenuItem.setToggleGroup(localeToggleGroup);
localeMenu.getItems().add(localeMenuItem);
}
Menu optionsMenu = new Menu("Options for Locale");
final String MSG =
"Use cell factory to color past days and add tooltip to tomorrow";
final CheckMenuItem cellFactoryMenuItem = new CheckMenuItem(MSG);
optionsMenu.getItems().add(cellFactoryMenuItem);
cellFactoryMenuItem.setOnAction((ActionEvent t) -> {
if (cellFactoryMenuItem.isSelected()) {
datePicker.setDayCellFactory(dayCellFac);
} else {
datePicker.setDayCellFactory(null);
}
});
final CheckMenuItem todayMenuItem =
new CheckMenuItem("Set date to today");
optionsMenu.getItems().add(todayMenuItem);
todayMenuItem.setOnAction((ActionEvent t) -> {
if (todayMenuItem.isSelected()) {
datePicker.setValue(today);
}
});
final CheckMenuItem showWeekNumMenuItem =
new CheckMenuItem("Show week numbers");
optionsMenu.getItems().add(showWeekNumMenuItem);
showWeekNumMenuItem.setOnAction((ActionEvent t) -> {
datePicker.setShowWeekNumbers(showWeekNumMenuItem.isSelected());
});
final ChangeListener<Toggle> listener =
(ObservableValue<? extends Toggle> observable,
Toggle old, Toggle now) -> {
if (localeToggleGroup.getSelectedToggle() != null) {
RadioMenuItem item =
(RadioMenuItem)localeToggleGroup.getSelectedToggle();
String selectedLocale = item.getText().replace('_', '-');
Locale locale = Locale.forLanguageTag(selectedLocale);
Locale.setDefault(locale);
datePicker = createDatePicker();
final boolean showWeek = showWeekNumMenuItem.isSelected();
datePicker.setShowWeekNumbers(showWeek);
}
};
localeToggleGroup.selectedToggleProperty().addListener(listener);
menuBar.getMenus().addAll(localeMenu, optionsMenu);
return menuBar;
}
public void play() {
originalLocale = Locale.getDefault();
}
@Override
public void stop() {
Locale.setDefault(originalLocale);
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
