package ensemble.samples.controls.tab;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.InputStream;
public class TabPaneApp extends Application {
private TabPane tabPane;
private Tab tab1;
private Tab tab2;
private Tab tab3;
private Tab internalTab;
public Parent createContent() {
tabPane = new TabPane();
tabPane.setPrefSize(400, 360);
tabPane.setMinSize(TabPane.USE_PREF_SIZE, TabPane.USE_PREF_SIZE);
tabPane.setMaxSize(TabPane.USE_PREF_SIZE, TabPane.USE_PREF_SIZE);
tab1 = new Tab();
tab2 = new Tab();
tab3 = new Tab();
internalTab = new Tab();
tabPane.setRotateGraphic(false);
tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
tabPane.setSide(Side.TOP);
final VBox vbox = new VBox();
vbox.setSpacing(10);
vbox.setTranslateX(10);
vbox.setTranslateY(10);
tab1.setText("Tab 1");
tab1.setTooltip(new Tooltip("Tab 1 Tooltip"));
final InputStream png = getClass().getResourceAsStream("tab_16.png");
final Image image = new Image(png);
tab1.setGraphic(new ImageView(image));
setUpControlButtons(vbox);
tab1.setContent(vbox);
tabPane.getTabs().add(tab1);
tab2.setText("Longer Tab");
final VBox vboxLongTab = new VBox();
vboxLongTab.setSpacing(10);
vboxLongTab.setTranslateX(10);
vboxLongTab.setTranslateY(10);
Label explainRadios = new Label("Closing policy for tabs:");
vboxLongTab.getChildren().add(explainRadios);
ToggleGroup closingPolicy = new ToggleGroup();
for (TabClosingPolicy policy : TabClosingPolicy.values()) {
final RadioButton radioButton = new RadioButton(policy.name());
radioButton.setMnemonicParsing(false);
radioButton.setToggleGroup(closingPolicy);
radioButton.setOnAction((ActionEvent event) -> {
final TabClosingPolicy radioPolicy =
TabClosingPolicy.valueOf(radioButton.getText());
tabPane.setTabClosingPolicy(radioPolicy);
});
if (policy.name().equals(TabClosingPolicy.SELECTED_TAB.name())) {
radioButton.setSelected(true);
}
vboxLongTab.getChildren().add(radioButton);
}
tab2.setContent(vboxLongTab);
tabPane.getTabs().add(tab2);
tab3.setText("Tab 3");
final VBox vboxTab3 = new VBox();
vboxTab3.setSpacing(10);
vboxTab3.setTranslateX(10);
vboxTab3.setTranslateY(10);
final CheckBox cb = new CheckBox("Show labels on original tabs");
cb.setSelected(true);
cb.setOnAction((ActionEvent event) -> {
if (cb.isSelected()) {
tab1.setText("Tab 1");
tab2.setText("Longer Tab");
tab3.setText("Tab 3");
internalTab.setText("Internal Tabs");
} else {
tab1.setText("");
tab2.setText("");
tab3.setText("");
internalTab.setText("");
}
});
vboxTab3.getChildren().add(cb);
tab3.setContent(vboxTab3);
tabPane.getTabs().add(tab3);
internalTab.setText("Internal Tabs");
setupInternalTab();
tabPane.getTabs().add(internalTab);
return tabPane;
}
private void toggleTabPosition(TabPane tabPane) {
Side pos = tabPane.getSide();
if (pos == Side.TOP) {
tabPane.setSide(Side.RIGHT);
} else if (pos == Side.RIGHT) {
tabPane.setSide(Side.BOTTOM);
} else if (pos == Side.BOTTOM) {
tabPane.setSide(Side.LEFT);
} else {
tabPane.setSide(Side.TOP);
}
}
private void toggleTabMode(TabPane tabPane) {
if (!tabPane.getStyleClass().contains(TabPane.STYLE_CLASS_FLOATING)) {
tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
} else {
tabPane.getStyleClass().remove(TabPane.STYLE_CLASS_FLOATING);
}
}
private void setupInternalTab() {
StackPane internalTabContent = new StackPane();
final TabPane internalTabPane = new TabPane();
internalTabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
internalTabPane.setSide(Side.LEFT);
internalTabPane.setPrefSize(Region.USE_COMPUTED_SIZE,
Region.USE_COMPUTED_SIZE);
final Tab innerTab = new Tab();
innerTab.setText("Tab 1");
final VBox innerVbox = new VBox();
innerVbox.setSpacing(10);
innerVbox.setTranslateX(10);
innerVbox.setTranslateY(10);
Button innerTabPosButton = new Button("Toggle Tab Position");
innerTabPosButton.setOnAction((ActionEvent e) -> {
toggleTabPosition(internalTabPane);
});
innerVbox.getChildren().add(innerTabPosButton);
{
Button innerTabModeButton = new Button("Toggle Tab Mode");
innerTabModeButton.setOnAction((ActionEvent e) -> {
toggleTabMode(internalTabPane);
});
innerVbox.getChildren().add(innerTabModeButton);
}
innerTab.setContent(innerVbox);
internalTabPane.getTabs().add(innerTab);
for (int i = 1; i < 5; i++) {
Tab tab = new Tab();
tab.setText("Tab " + i);
tab.setContent(new Region());
internalTabPane.getTabs().add(tab);
}
internalTabContent.getChildren().add(internalTabPane);
internalTab.setContent(internalTabContent);
}
private void setUpControlButtons(VBox vbox) {
final Button tabModeButton = new Button("Toggle Tab Mode");
tabModeButton.setOnAction((ActionEvent e) -> {
toggleTabMode(tabPane);
});
vbox.getChildren().add(tabModeButton);
final Button tabPositionButton = new Button("Toggle Tab Position");
tabPositionButton.setOnAction((ActionEvent e) -> {
toggleTabPosition(tabPane);
});
final Button newTabButton = new Button("Switch to New Tab");
newTabButton.setOnAction((ActionEvent e) -> {
Tab t = new Tab("Testing");
t.setContent(new Button("Howdy"));
tabPane.getTabs().add(t);
tabPane.getSelectionModel().select(t);
});
vbox.getChildren().add(newTabButton);
final Button addTabButton = new Button("Add Tab");
addTabButton.setOnAction((ActionEvent e) -> {
Tab t = new Tab("New Tab");
t.setContent(new Region());
tabPane.getTabs().add(t);
});
vbox.getChildren().add(addTabButton);
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
