package hello;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
public class HelloLabelBorders extends Application {
public static void main(String[] args) {
Application.launch(HelloLabelBorders.class, args);
}
private static class LabelListCell extends ListCell<Data> {
LabelListCell() {
super();
super.setTooltip(tooltip);
}
@Override
protected void updateItem(Data item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
tooltip.setText(null);
} else {
super.setText(item.getText());
super.setStyle(item.getStyle());
tooltip.setText(item.getStyle());
}
}
private final Tooltip tooltip = new Tooltip();
}
private static class Data {
Data(String text, String style) {
this.text = text;
this.style = style;
}
String getText() {
return text;
}
String getStyle() {
return style;
}
private final String text;
private final String style;
}
private static ObservableList<Data> data = FXCollections.observableArrayList(
new Data(
"no-border",
"-fx-border-color: null;"
),
new Data(
"border-color",
"-fx-border-color: green blue cyan red;"
),
new Data(
"border-inset",
"-fx-border-color: red blue green cyan;\n" +
"-fx-border-radius: 5;\n" +
"-fx-border-insets: 5;"
),
new Data(
"border-style-dashed",
"-fx-border-style: dashed;\n" +
"-fx-border-insets: 0, -3;\n" +
"-fx-border-radius: 5;"
),
new Data(
"border-style-dotted",
"-fx-border-color: red blue green cyan;\n" +
"-fx-border-style: dotted;\n" +
"-fx-border-radius: 5;"
),
new Data(
"border-width",
"-fx-border-width: 1 2 1 2;\n" +
"-fx-border-color: red;"
),
new Data(
"border-width-dashed",
"-fx-border-width: 1 3 5 1;\n" +
"-fx-border-color: red blue green cyan;\n" +
"-fx-border-style: dashed;"
),
new Data(
"border-width-dotted",
"-fx-border-width: 1 3 5 1;\n" +
"-fx-border-color: red blue green cyan;\n" +
"-fx-border-style: dotted;"
),
new Data(
"image-border",
"-fx-border-image-source: url('/hello/border.png');\n" +
"-fx-border-image-slice: 28;\n" +
"-fx-border-image-width: 9;"
),
new Data(
"image-border-insets",
"-fx-border-image-source: url('/hello/heart_16.png');\n" +
"-fx-border-image-width: 10;\n" +
"-fx-border-image-insets: 1 5 10 15;"
),
new Data(
"image-border-no-repeat",
"-fx-border-image-source: url('/hello/border.png');\n" +
"-fx-border-image-repeat: no-repeat;\n" +
"-fx-border-image-slice: 28;\n" +
"-fx-border-image-width: 9;"
),
new Data(
"image-border-repeat-x",
"-fx-border-image-source: url('/hello/border.png');\n" +
"-fx-border-image-repeat: repeat-x;\n" +
"-fx-border-image-slice: 28;\n" +
"-fx-border-image-width: 9;"
),
new Data(
"image-border-repeat-y",
"-fx-border-image-source: url('/hello/border.png');\n" +
"-fx-border-image-repeat: repeat-y;\n" +
"-fx-border-image-slice: 28;\n" +
"-fx-border-image-width: 9;"
),
new Data(
"image-border-round",
"-fx-border-image-source: url('/hello/border.png');\n" +
"-fx-border-image-repeat: round;\n" +
"-fx-border-image-slice: 28;\n" +
"-fx-border-image-width: 9;"
),
new Data(
"image-border-space",
"-fx-border-image-source: url('/hello/border.png');\n" +
"-fx-border-image-repeat: space;\n" +
"-fx-border-image-slice: 28;\n" +
"-fx-border-image-width: 9;"
)
);
@Override
public void start(Stage primaryStage) {
ListView<Data> listView = new ListView<>(data);
listView.getStyleClass().add("hello-label-borders");
listView.setCellFactory(param -> new LabelListCell());
Scene scene = new Scene(listView);
scene.getStylesheets().add("/hello/hello.css");
primaryStage.setScene(scene);
primaryStage.show();
}
}
