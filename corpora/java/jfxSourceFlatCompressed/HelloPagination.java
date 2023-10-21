package hello;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloPagination extends Application {
private Pagination pagination;
public static void main(String[] args) throws Exception {
launch(args);
}
public int itemsPerPage() {
return 10;
}
public VBox createPage(int pageIndex) {
VBox box = new VBox(5);
int page = pageIndex * itemsPerPage();
for (int i = page; i < page + itemsPerPage(); i++) {
Label l = new Label("PAGE INDEX " + pageIndex);
box.getChildren().add(l);
}
return box;
}
public VBox createPage2(int pageIndex) {
VBox box = new VBox(5);
int page = pageIndex * itemsPerPage();
for (int i = page; i < page + itemsPerPage(); i++) {
Label l = new Label("NEW PAGE INDEX " + pageIndex);
box.getChildren().add(l);
}
return box;
}
@Override
public void start(final Stage stage) throws Exception {
pagination = new Pagination(28, 0);
pagination.setStyle("-fx-border-color:red;");
pagination.setPageFactory(pageIndex -> createPage(pageIndex));
VBox toolbar = new VBox(10);
Button setMaxPageIndicatorCount = new Button("setMaxPageIndicatorCount = 5");
setMaxPageIndicatorCount.setOnAction(t -> {
System.out.println("setPageIndicatorCount = 5");
pagination.setMaxPageIndicatorCount(5);
});
Button getMaxPageIndicatorCount = new Button("getMaxPageIndicatorCount");
getMaxPageIndicatorCount.setOnAction(t -> System.out.println("getPageIndicatorCount = " + pagination.getMaxPageIndicatorCount()));
Button setCurrentPageIndex = new Button("setCurrentPageIndex = 19");
setCurrentPageIndex.setOnAction(t -> {
System.out.println("setCurrentPageIndex = 19");
pagination.setCurrentPageIndex(19);
});
Button setCurrentPageIndex2 = new Button("setCurrentPageIndex = 0");
setCurrentPageIndex2.setOnAction(t -> {
System.out.println("setCurrentPageIndex = 0");
pagination.setCurrentPageIndex(0);
});
Button setCurrentPageIndex3 = new Button("setCurrentPageIndex = 8");
setCurrentPageIndex3.setOnAction(t -> {
System.out.println("setCurrentPageIndex = 8");
pagination.setCurrentPageIndex(8);
});
Button setStyle = new Button("change style");
setStyle.setOnAction(t -> {
if (pagination.getStyleClass().contains(Pagination.STYLE_CLASS_BULLET)) {
pagination.getStyleClass().remove(Pagination.STYLE_CLASS_BULLET);
} else {
pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
}
});
Button getCurrentPageIndex = new Button("getCurrentPageIndex");
getCurrentPageIndex.setOnAction(t -> System.out.println("getCurrentPageIndex = " + pagination.getCurrentPageIndex()));
Button setOldPageFactory = new Button("set OLD PageFactory");
setOldPageFactory.setOnAction(t -> pagination.setPageFactory(pageIndex -> createPage(pageIndex)));
Button setNewPageFactory = new Button("set NEW PageFactory");
setNewPageFactory.setOnAction(t -> pagination.setPageFactory(pageIndex -> createPage2(pageIndex)));
Button setPageCount = new Button("setPageCount = 8");
setPageCount.setOnAction(t -> {
System.out.println("setPageCount = 8");
pagination.setPageCount(8);
});
Button setPageCount2 = new Button("setPageCount = 18");
setPageCount2.setOnAction(t -> {
System.out.println("setPageCount = 18");
pagination.setPageCount(18);
});
Button setPageCount3 = new Button("setPageCount = INDETERMINATE");
setPageCount3.setOnAction(t -> {
System.out.println("setPageCount = INDETERMINATE");
pagination.setPageCount(Pagination.INDETERMINATE);
});
toolbar.getChildren().addAll(
setMaxPageIndicatorCount,
setCurrentPageIndex, setCurrentPageIndex2, setCurrentPageIndex3,
setOldPageFactory, setNewPageFactory, setStyle,
setPageCount, setPageCount2, setPageCount3,
getMaxPageIndicatorCount,
getCurrentPageIndex);
HBox box = new HBox(10);
HBox.setHgrow(pagination, Priority.ALWAYS);
box.getChildren().addAll(toolbar, pagination);
Scene scene = new Scene(box, 800, 600);
stage.setScene(scene);
stage.show();
}
}
