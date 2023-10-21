package hello;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;
import javafx.scene.shape.Path;
import javafx.scene.text.*;
import javafx.stage.Stage;
import static javafx.scene.paint.Color.*;
public class HelloTextFlow extends Application {
final Path caret = new Path();
final Path highlight = new Path();
int caretPos = -1;
int anchorPos = -1;
@Override public void start(Stage stage) {
stage.setTitle("Hello TextFlow");
Text text1 = mkText(0, 3);
Text text2 = mkText(3, 7);
TextFlow textFlow =
new TextFlow(text1,
new ImageView(new Image("hello/duke.jpg", 30f, 30f, true, true, false)),
text2) {
{
setCursor(Cursor.TEXT);
setOnMousePressed(e -> {
HitInfo hit = hitTest(new Point2D(e.getX(), e.getY()));
caretPos = anchorPos = hit.getInsertionIndex();
caret.getElements().clear();
highlight.getElements().clear();
text1.setSelectionStart(-1);
text1.setSelectionEnd(-1);
text2.setSelectionStart(-1);
text2.setSelectionEnd(-1);
caret.getElements().addAll(caretShape(hit.getCharIndex(), hit.isLeading()));
});
setOnMouseDragged(e -> {
HitInfo hit = hitTest(new Point2D(e.getX(), e.getY()));
caretPos = hit.getInsertionIndex();
caret.getElements().clear();
highlight.getElements().clear();
if (anchorPos >= 0 && caretPos != anchorPos) {
int i1 = Math.min(caretPos, anchorPos);
int i2 = Math.max(caretPos, anchorPos);
int len1 = text1.getText().length();
if (i1 < len1) {
text1.setSelectionStart(i1);
if (i2 < len1) {
text1.setSelectionEnd(i2);
} else {
text1.setSelectionEnd(len1);
}
} else {
text1.setSelectionStart(-1);
text1.setSelectionEnd(-1);
}
if (i2 > len1 + 1) {
if (i1 < len1 + 1) {
text2.setSelectionStart(0);
} else if (i1 >= len1 + 1) {
text2.setSelectionStart(i1 - len1 - 1);
}
text2.setSelectionEnd(i2 - len1 - 1);
} else {
text2.setSelectionStart(-1);
text2.setSelectionEnd(-1);
}
highlight.getElements().addAll(rangeShape(i1, i2));
} else {
caret.getElements().addAll(caretShape(hit.getCharIndex(), hit.isLeading()));
}
});
caret.setStrokeWidth(1);
caret.setFill(BLACK);
caret.setStroke(BLACK);
highlight.setStroke(null);
highlight.setFill(LIGHTBLUE);
}
@Override public void layoutChildren() {
super.layoutChildren();
caret.getElements().clear();
highlight.getElements().clear();
if (anchorPos >= 0 && caretPos != anchorPos) {
highlight.getElements().addAll(rangeShape(Math.min(caretPos, anchorPos),
Math.max(caretPos, anchorPos)));
} else {
caret.getElements().addAll(caretShape(caretPos, true));
}
}
};
Scene scene = new Scene(new Group(highlight, textFlow, caret), 600, 400);
textFlow.prefWidthProperty().bind(scene.widthProperty());
stage.setScene(scene);
stage.show();
}
private Text mkText(int start, int end) {
StringBuilder sb = new StringBuilder();
for (int i = start; i < end; i++) {
sb.append(i + ". Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\n");
}
Text t = new Text(sb.toString());
return t;
}
}
