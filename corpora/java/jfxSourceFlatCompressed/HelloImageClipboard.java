package hello;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class HelloImageClipboard extends Application {
private Button clearBtn, copyBtn, pasteBtn;
final ImageView imageView = new ImageView();
@Override public void start(Stage stage) {
stage.setTitle("Hello Image Clipboard");
Scene scene = new Scene(new Group(), 1024, 768);
scene.setFill(Color.LIGHTGREEN);
Rectangle overlay = new Rectangle();
overlay.setWidth(800);
overlay.setHeight(600);
overlay.setFill(Color.TRANSPARENT);
EventHandler<DragEvent> drop = de -> checkBoard(de.getDragboard(), de);
EventHandler<DragEvent> enter = de -> {
if (de != null && de.getDragboard() != null && de.getDragboard().hasImage()) {
de.acceptTransferModes(TransferMode.ANY);
}
};
EventHandler<DragEvent> dragged = de -> {
if (de != null && de.getDragboard() != null && de.getDragboard().hasImage()) {
de.acceptTransferModes(TransferMode.ANY);
}
};
overlay.setOnDragDropped(drop);
overlay.setOnDragEntered(enter);
overlay.setOnDragOver(dragged);
clearBtn = new Button("Clear");
clearBtn.setTranslateX(50);
clearBtn.setTranslateY(30);
copyBtn = new Button("Copy");
copyBtn.setTranslateX(125);
copyBtn.setTranslateY(30);
pasteBtn = new Button("Paste");
pasteBtn.setTranslateX(200);
pasteBtn.setTranslateY(30);
clearBtn.setOnAction(e -> clear());
copyBtn.setOnAction(e -> {
ClipboardContent content = new ClipboardContent();
content.putImage(imageView.getImage());
Clipboard.getSystemClipboard().setContent(content);
});
pasteBtn.setOnAction(e -> checkBoard(Clipboard.getSystemClipboard(), null));
Group root = (Group)scene.getRoot();
root.getChildren().add(overlay);
root.getChildren().add(imageView);
root.getChildren().add(clearBtn);
root.getChildren().add(copyBtn);
root.getChildren().add(pasteBtn);
stage.setScene(scene);
stage.show();
}
private void clear() {
}
private void checkBoard(Clipboard board, DragEvent de) {
clear();
if (board == null) {
System.out.println("HelloImageClipboard: sorry - null Clipboard");
}
if (board.hasImage()) {
if (de != null) de.acceptTransferModes(TransferMode.ANY);
imageView.setImage(board.getImage());
if (de != null) de.setDropCompleted(true);
System.out.println("HelloImageClipboard: single image");
} else {
if (de != null) de.setDropCompleted(false);
System.out.println("HelloImageClipboard: sorry - no images on the Clipboard");
}
}
public static void main(String[] args) {
Application.launch(args);
}
}
