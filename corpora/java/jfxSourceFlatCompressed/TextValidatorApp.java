package ensemble.samples.controls.text.textvalidator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
public class TextValidatorApp extends Application {
public Parent createContent() {
TextField dateField = new TextField();
dateField.setPromptText("Enter a Large Number");
dateField.setMaxHeight(TextField.USE_PREF_SIZE);
TextInputValidatorPane<TextField> pane =
new TextInputValidatorPane<TextField>();
pane.setContent(dateField);
pane.setValidator((TextField control) -> {
try {
String text = control.getText();
if (text == null || text.trim().equals("")) {
return null;
}
double d = Double.parseDouble(text);
if (d < 1000) {
return new ValidationResult("Should be > 1000",
ValidationResult.Type.WARNING);
}
return null;
} catch (Exception e) {
return new ValidationResult("Bad number",
ValidationResult.Type.ERROR);
}
});
final StackPane rootSP = new StackPane();
rootSP.setPadding(new Insets(12));
rootSP.getChildren().add(pane);
final String validatorCss =
getClass().getResource("Validators.css").toExternalForm();
pane.getStylesheets().add(validatorCss);
return rootSP;
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
