package ensemble.samples.language.stringbinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class StringBindingApp extends Application {
public Parent createContent() {
final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
format.setLenient(false);
final TextField dateField = new TextField();
dateField.setPromptText("Enter a birth date");
dateField.setMaxHeight(TextField.USE_PREF_SIZE);
dateField.setMaxWidth(TextField.USE_PREF_SIZE);
Label label = new Label();
label.setWrapText(true);
label.textProperty().bind(new StringBinding() {
{
bind(dateField.textProperty());
}
@Override
protected String computeValue() {
try {
Date date = format.parse(dateField.getText());
Calendar c = Calendar.getInstance();
c.setTime(date);
Date today = new Date();
Calendar c2 = Calendar.getInstance();
c2.setTime(today);
if (c.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR) - 1
&& c.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
return "You were born yesterday";
} else if (c.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
&& c.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
return "You were born today";
} else {
return "You were born " + format.format(date);
}
} catch (Exception e) {
return "Enter your valid birth date (mm/dd/yyyy)";
}
}
});
VBox vBox = new VBox(7);
vBox.setPadding(new Insets(12));
vBox.getChildren().addAll(label, dateField);
vBox.setAlignment(Pos.CENTER);
return vBox;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
