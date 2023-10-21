package layout;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
public class PaneTab extends Tab {
public PaneTab(String text) {
this.setText(text);
init();
}
public void init() {
Rectangle rect1 = new Rectangle(450, 450);
rect1.setLayoutX(150);
rect1.setLayoutY(80);
rect1.setFill(Color.BURLYWOOD);
Rectangle rect2 = new Rectangle(180, 200, 350, 200);
rect2.setFill(Color.CORAL);
Circle circle = new Circle(350, 300, 150, Color.GREEN);
Button okBtn = new Button("OK");
Button cancelBtn = new Button("Cancel");
okBtn.relocate(250, 250);
cancelBtn.relocate(300, 250);
Pane root = new Pane();
root.getChildren().addAll(rect1, rect2, circle, okBtn, cancelBtn);
root.getStyleClass().add("layout");
this.setContent(root);
}
}
