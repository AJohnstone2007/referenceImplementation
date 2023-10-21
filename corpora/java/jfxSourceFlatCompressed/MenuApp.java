package ensemble.samples.controls.menu;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class MenuApp extends Application {
private final Label sysMenuLabel = new Label("Using System Menu");
public Parent createContent() {
final String os = System.getProperty("os.name");
VBox vbox = new VBox(20);
vbox.setPrefSize(300, 100);
final MenuBar menuBar = new MenuBar();
MenuItem menu111 = new MenuItem("blah");
final MenuItem menu112 = new MenuItem("foo");
final CheckMenuItem menu113 = new CheckMenuItem("Show \"foo\" item");
menu113.setSelected(true);
menu113.selectedProperty().addListener((Observable valueModel) -> {
menu112.setVisible(menu113.isSelected());
});
final String INFO = "/ensemble/samples/shared-resources/menuInfo.png";
final Image INFO_MENU_IMAGE =
new Image(getClass().getResourceAsStream(INFO));
Menu menu11 = new Menu("Submenu 1", new ImageView(INFO_MENU_IMAGE));
menu11.getItems().addAll(menu111, menu112, menu113);
MenuItem menu121 = new MenuItem("Item 1");
MenuItem menu122 = new MenuItem("Item 2");
Menu menu12 = new Menu("Submenu 2");
menu12.getItems().addAll(menu121, menu122);
final String change[] = { "Change Text", "Change Back" };
final MenuItem menu13 = new MenuItem(change[0]);
menu13.setAccelerator(KeyCombination.keyCombination("Shortcut+C"));
menu13.setOnAction((ActionEvent t) -> {
final String menuText =
menu13.getText().equals(change[0]) ? change[1] : change[0];
menu13.setText(menuText);
});
Menu menu1 = new Menu("Options");
menu1.getItems().addAll(menu11, menu12, menu13);
menuBar.getMenus().addAll(menu1);
if (os != null && os.startsWith("Mac")) {
Menu systemMenuBarMenu = new Menu("MenuBar Options");
final String check = "Use System Menu Bar (Only works on Mac)";
final CheckMenuItem useSystemMenuBarCB = new CheckMenuItem(check);
useSystemMenuBarCB.setSelected(true);
BooleanProperty selectedCB = useSystemMenuBarCB.selectedProperty();
menuBar.useSystemMenuBarProperty().bindBidirectional(selectedCB);
systemMenuBarMenu.getItems().add(useSystemMenuBarCB);
menuBar.getMenus().add(systemMenuBarMenu);
HBox hbox = new HBox();
hbox.setAlignment(Pos.CENTER);
sysMenuLabel.setStyle("-fx-font-size: 24");
hbox.getChildren().add(sysMenuLabel);
vbox.getChildren().add(hbox);
sysMenuLabel.setVisible((menuBar.getHeight() == 0));
ChangeListener<? super Number> heightListener =
(ObservableValue<? extends Number> ov,
Number old, Number now) -> {
sysMenuLabel.setVisible((menuBar.getHeight() == 0));
};
menuBar.heightProperty().addListener(heightListener);
}
vbox.getChildren().addAll(menuBar);
return vbox;
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
