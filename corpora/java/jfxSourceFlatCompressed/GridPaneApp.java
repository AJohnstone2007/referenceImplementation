package ensemble.samples.layout.gridpane;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class GridPaneApp extends Application {
public Parent createContent() {
VBox vbox = new VBox();
String percent =
"Content placement by influencing row and column percentages.";
Label gridPerCaption = new Label(percent);
gridPerCaption.setWrapText(true);
GridPane gridPer = createGridPanePercentage();
String them =
"Content placement by influencing the rows and columns themselves:";
Label gridRCInfoCaption = new Label(them);
gridRCInfoCaption.setWrapText(true);
GridPane gridRCInfo = createGridPaneRCInfo();
String specify =
"Content placement by specifying rows and columns:";
Label gridConstCaption = new Label(specify);
gridConstCaption.setWrapText(true);
GridPane gridConst = createGridPaneConst();
vbox.getChildren().addAll(gridPerCaption, gridPer, new Separator());
vbox.getChildren().addAll(gridRCInfoCaption, gridRCInfo, new Separator());
vbox.getChildren().addAll(gridConstCaption, gridConst);
return vbox;
}
private GridPane createGridPanePercentage() {
GridPane grid = new GridPane();
grid.setPadding(new Insets(8, 8, 8, 8));
RowConstraints rowinfo3 = new RowConstraints();
rowinfo3.setPercentHeight(50);
ColumnConstraints colInfo2 = new ColumnConstraints();
colInfo2.setPercentWidth(25);
ColumnConstraints colInfo3 = new ColumnConstraints();
colInfo3.setPercentWidth(50);
grid.getRowConstraints().add(rowinfo3);
grid.getRowConstraints().add(rowinfo3);
grid.getColumnConstraints().add(colInfo2);
grid.getColumnConstraints().add(colInfo3);
grid.getColumnConstraints().add(colInfo2);
Label condLabel = new Label(" Member Name:");
GridPane.setHalignment(condLabel, HPos.RIGHT);
GridPane.setConstraints(condLabel, 0, 0);
Label condValue = new Label("MyName");
GridPane.setMargin(condValue, new Insets(0, 0, 0, 10));
GridPane.setConstraints(condValue, 1, 0);
Label acctLabel = new Label("Member Number:");
GridPane.setHalignment(acctLabel, HPos.RIGHT);
GridPane.setConstraints(acctLabel, 0, 1);
TextField textBox = new TextField("Your number");
GridPane.setMargin(textBox, new Insets(10, 10, 10, 10));
GridPane.setConstraints(textBox, 1, 1);
Button button = new Button("Help");
GridPane.setConstraints(button, 2, 1);
GridPane.setMargin(button, new Insets(10, 10, 10, 10));
GridPane.setHalignment(button, HPos.CENTER);
GridPane.setConstraints(condValue, 1, 0);
grid.getChildren().addAll(condLabel, condValue, button, acctLabel, textBox);
return grid;
}
private GridPane createGridPaneRCInfo() {
GridPane grid = new GridPane();
grid.setPadding(new Insets(8, 8, 8, 8));
RowConstraints rowinfo = new RowConstraints(40, 40, 40);
ColumnConstraints colinfo = new ColumnConstraints(90, 90, 90);
for (int i = 0; i <= 2; i++) {
grid.getRowConstraints().add(rowinfo);
}
for (int j = 0; j <= 2; j++) {
grid.getColumnConstraints().add(colinfo);
}
Label category = new Label("Category:");
GridPane.setHalignment(category, HPos.RIGHT);
Label categoryValue = new Label("Coffee");
Label company = new Label("Type:");
GridPane.setHalignment(company, HPos.RIGHT);
Label companyValue = new Label("Kona");
Label rating = new Label("Rating:");
GridPane.setHalignment(rating, HPos.RIGHT);
Label ratingValue = new Label("Excellent");
String IMAGE = "/ensemble/samples/shared-resources/icon-48x48.png";
Image ICON_48 = new Image(getClass().getResourceAsStream(IMAGE));
ImageView imageView = new ImageView(ICON_48);
GridPane.setHalignment(imageView, HPos.CENTER);
GridPane.setConstraints(category, 0, 0);
GridPane.setConstraints(categoryValue, 1, 0);
GridPane.setConstraints(company, 0, 1);
GridPane.setConstraints(companyValue, 1, 1);
GridPane.setConstraints(imageView, 2, 1);
GridPane.setConstraints(rating, 0, 2);
GridPane.setConstraints(ratingValue, 1, 2);
grid.getChildren().addAll(category, categoryValue, company,
companyValue, imageView, rating, ratingValue);
return grid;
}
private GridPane createGridPaneConst() {
GridPane grid = new GridPane();
grid.setHgap(4);
grid.setVgap(6);
grid.setPadding(new Insets(8, 8, 8, 8));
grid.setGridLinesVisible(true);
ObservableList<Node> content = grid.getChildren();
Label label = new Label("Name:");
GridPane.setConstraints(label, 0, 0);
GridPane.setHalignment(label, HPos.RIGHT);
content.add(label);
label = new Label("John Q. Public");
GridPane.setConstraints(label, 1, 0, 2, 1);
GridPane.setHalignment(label, HPos.LEFT);
content.add(label);
label = new Label("Address:");
GridPane.setConstraints(label, 0, 1);
GridPane.setHalignment(label, HPos.RIGHT);
content.add(label);
label = new Label("12345 Main Street, Some City, CA");
GridPane.setConstraints(label, 1, 1, 5, 1);
GridPane.setHalignment(label, HPos.LEFT);
content.add(label);
return grid;
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
