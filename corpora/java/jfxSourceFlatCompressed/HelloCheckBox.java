package hello;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
public class HelloCheckBox extends Application {
private CheckBox createSteadyStateControl(String text) {
CheckBox checkBox = new CheckBox(text);
for(String pseudoClass : text.split(" ")) {
checkBox.pseudoClassStateChanged(PseudoClass.getPseudoClass(pseudoClass), true);
}
checkBox.setFocusTraversable(false);
checkBox.setMouseTransparent(true);
return checkBox;
}
@Override public void start(Stage stage) {
CheckBox[] steadyStateControls = new CheckBox[] {
createSteadyStateControl("selected focused hover"),
createSteadyStateControl("selected focused"),
createSteadyStateControl("selected hover"),
createSteadyStateControl("indeterminate focused hover"),
createSteadyStateControl("indeterminate focused"),
createSteadyStateControl("indeterminate hover"),
createSteadyStateControl("hover focused"),
createSteadyStateControl("focused"),
createSteadyStateControl("hover")
};
VBox steadyState = new VBox(7);
steadyState.setTranslateX(20);
steadyState.getChildren().add(new Label("Steady pseudo-class state samples"));
steadyState.getChildren().addAll(steadyStateControls);
CheckBox cbox = new CheckBox("Indeterminate CheckBox");
cbox.setIndeterminate(true);
cbox.setAllowIndeterminate(true);
Label label = new Label();
label.textProperty().bind(
Bindings.when(cbox.indeterminateProperty()).
then("The check box is indeterminate").
otherwise(
Bindings.when(cbox.selectedProperty()).
then("The check box is selected").
otherwise("The check box is not selected"))
);
VBox vbox = new VBox(7);
vbox.setAlignment(Pos.CENTER);
vbox.getChildren().addAll(label, cbox);
CheckBox twoStateCheckBox = new CheckBox("Two-state check box");
twoStateCheckBox.setAllowIndeterminate(false);
twoStateCheckBox.setIndeterminate(false);
twoStateCheckBox.setSelected(true);
Label twoStateLabel = new Label();
twoStateLabel.textProperty().bind(Bindings.when(twoStateCheckBox.selectedProperty()).
then("Selected"). otherwise("Not selected"));
VBox vbox2 = new VBox(7);
vbox2.setAlignment(Pos.CENTER);
vbox2.getChildren().addAll(twoStateLabel, twoStateCheckBox);
CheckBox focusCheckBox = new CheckBox("Focus indicator");
focusCheckBox.setAllowIndeterminate(false);
focusCheckBox.setIndeterminate(false);
Label focusLabel = new Label();
StringExpression s = Bindings.concat(Bindings.when(focusCheckBox.focusedProperty()).
then("Focused"). otherwise("Not focused"),
" and ",
Bindings.when(focusCheckBox.selectedProperty()).
then("Selected").otherwise("Not selected")
);
focusLabel.textProperty().bind(s);
VBox vbox3 = new VBox(7);
vbox3.setAlignment(Pos.CENTER);
vbox3.getChildren().addAll(focusLabel, focusCheckBox);
VBox mainbox = new VBox(7);
mainbox.setAlignment(Pos.CENTER);
mainbox.getChildren().addAll(vbox, new Separator(),
vbox2, new Separator(),
vbox3, new Separator(),
steadyState);
Scene scene = new Scene(mainbox, 400, 450);
scene.setFill(Color.SKYBLUE);
stage.setTitle("Hello CheckBox");
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
