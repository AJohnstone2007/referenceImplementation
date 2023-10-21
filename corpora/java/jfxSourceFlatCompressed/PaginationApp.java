package ensemble.samples.controls.pagination;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class PaginationApp extends Application {
private static final Image[] images = new Image[7];
private static final String[] urls = {
"/ensemble/samples/shared-resources/Animal1.jpg",
"/ensemble/samples/shared-resources/Animal2.jpg",
"/ensemble/samples/shared-resources/Animal3.jpg",
"/ensemble/samples/shared-resources/Animal4.jpg",
"/ensemble/samples/shared-resources/Animal5.jpg",
"/ensemble/samples/shared-resources/Animal6.jpg",
"/ensemble/samples/shared-resources/Animal7.jpg"
};
private Pagination pagination;
public Parent createContent() {
VBox outerBox = new VBox();
outerBox.setAlignment(Pos.CENTER);
for (int i = 0; i < images.length; i++) {
String url = getClass().getResource(urls[i]).toExternalForm();
images[i] = new Image(url, false);
}
pagination = new Pagination(7);
pagination.setPageFactory((Integer pageIndex) ->
createAnimalPage(pageIndex));
Button styleButton = new Button("Toggle pagination style");
styleButton.setOnAction((ActionEvent me) -> {
ObservableList<String> styleClass = pagination.getStyleClass();
if (!styleClass.contains(Pagination.STYLE_CLASS_BULLET)) {
styleClass.add(Pagination.STYLE_CLASS_BULLET);
} else {
styleClass.remove(Pagination.STYLE_CLASS_BULLET);
}
});
outerBox.getChildren().addAll(pagination, styleButton);
return outerBox;
}
private VBox createAnimalPage(int pageIndex) {
VBox box = new VBox();
ImageView iv = new ImageView(images[pageIndex]);
box.setAlignment(Pos.CENTER);
Label desc = new Label("PAGE " + (pageIndex + 1));
box.getChildren().addAll(iv, desc);
return box;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
