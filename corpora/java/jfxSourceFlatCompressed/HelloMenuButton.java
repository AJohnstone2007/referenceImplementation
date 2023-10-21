package hello;
import javafx.application.Application;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import static javafx.geometry.NodeOrientation.*;
public class HelloMenuButton extends Application {
public static void main(String[] args) {
Application.launch(args);
}
@Override public void start(Stage stage) {
stage.setTitle("Hello MenuButton");
VBox root = new VBox(20);
root.setStyle("-fx-padding: 30;");
Scene scene = new Scene(root);
scene.setFill(Color.CHOCOLATE);
final VBox vBox1 = new VBox(10);
Button button = new Button("Simple Button");
button.setTooltip(new Tooltip("Tooltip for Simple Button"));
button.setOnAction(e -> System.out.println("Simple Button"));
vBox1.getChildren().add(button);
MenuButton mb = new MenuButton("MenuButton");
mb.setTooltip(new Tooltip("Tooltip for MenuButton"));
final MenuItem coke = new MenuItem("Coke");
coke.setOnAction(e -> System.out.println(coke.getText()));
mb.getItems().add(coke);
final MenuItem pepsi = new MenuItem("Pepsi");
pepsi.setOnAction(e -> System.out.println(pepsi.getText()));
mb.getItems().add(pepsi);
mb.getItems().addAll(new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"),
new MenuItem("Foo"));
vBox1.getChildren().add(mb);
SplitMenuButton smb = new SplitMenuButton();
smb.setText("SplitMenuButton1");
smb.setTooltip(new Tooltip("Tooltip for SplitMenuButton1"));
smb.setOnAction(e -> System.out.println("SplitMenuButton1"));
MenuItem mi = new MenuItem("Divide");
mi.setOnAction(e -> System.out.println("Divide"));
smb.getItems().add(mi);
mi = new MenuItem("Conquer");
mi.setOnAction(e -> System.out.println("Conquer"));
smb.getItems().add(mi);
vBox1.getChildren().add(smb);
final SplitMenuButton smb3 = new SplitMenuButton();
smb3.setTooltip(new Tooltip("Tooltip for SplitMenuButton2"));
smb3.setOnAction(e -> System.out.println("SplitMenuButton2"));
{
final MenuItem menuItem = new MenuItem("Land");
menuItem.setOnAction(e -> {
System.out.println("Land");
smb3.setText(menuItem.getText());
smb3.setOnAction(menuItem.getOnAction());
});
smb3.getItems().add(menuItem);
}
{
final MenuItem menuItem = new MenuItem("Sea");
menuItem.setOnAction(e -> {
System.out.println("Sea");
smb3.setText(menuItem.getText());
smb3.setOnAction(menuItem.getOnAction());
});
smb3.getItems().add(menuItem);
}
smb3.setText(smb3.getItems().get(0).getText());
smb3.setOnAction(smb3.getItems().get(0).getOnAction());
vBox1.getChildren().add(smb3);
VBox vBox2 = new VBox(10);
{
HBox hBox = new HBox(14);
hBox.getChildren().add(new Label("Popup Side:"));
ToggleGroup toggleGroup = new ToggleGroup();
for (final Side side : Side.class.getEnumConstants()) {
final RadioButton rb = new RadioButton(side.toString());
rb.selectedProperty().addListener(valueModel -> {
for (Node node : vBox1.getChildren()) {
if (node instanceof MenuButton) {
((MenuButton)node).setPopupSide(side);
}
}
});
rb.setToggleGroup(toggleGroup);
if (side == Side.BOTTOM) {
rb.setSelected(true);
}
hBox.getChildren().add(rb);
}
vBox2.getChildren().add(hBox);
}
{
HBox hBox = new HBox(10);
{
final CheckBox cb = new CheckBox("Disable");
cb.selectedProperty().addListener(valueModel -> {
boolean disabled = cb.isSelected();
for (Node node : vBox1.getChildren()) {
node.setDisable(disabled);
}
});
hBox.getChildren().addAll(cb);
}
{
final CheckBox cb = new CheckBox("RTL");
cb.selectedProperty().addListener(valueModel -> {
boolean rtl = cb.isSelected();
for (Node node : vBox1.getChildren()) {
node.setNodeOrientation(rtl ? RIGHT_TO_LEFT : LEFT_TO_RIGHT);
}
});
cb.setSelected(scene.getEffectiveNodeOrientation() == RIGHT_TO_LEFT);
hBox.getChildren().addAll(cb);
}
vBox2.getChildren().add(hBox);
}
root.getChildren().addAll(vBox1, vBox2);
stage.setScene(scene);
stage.show();
}
}
