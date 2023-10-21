package ensemble.samples.graphics2d.calc;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
public class Key extends Parent {
public enum Code {
DIGIT_0(0, "0"), DIGIT_1(1, "1"), DIGIT_2(2, "2"), DIGIT_3(3, "3"),
DIGIT_4(4, "4"), DIGIT_5(5, "5"), DIGIT_6(6, "6"), DIGIT_7(7, "7"),
DIGIT_8(8, "8"), DIGIT_9(9, "9"), M_PLUS(10, "M+"), M_MINUS(11, "M-"),
M(12, "M"), DIVIDE(13, "/"), MULTIPLY(14, "X"), ADD(15, "+"),
SUBTRACT(16, "-"), EQUALS(17, "="), CLEAR(18, "C"), DECIMAL(19, ".");
private String text;
private int value;
Code(int value, String text){
this.value = value;
this.text = text;
}
public String getText() {
return text;
}
public int getValue() {
return value;
}
}
public static final double WIDTH = 50;
public static final double HEIGHT = 37;
private static final Stop[] CELL_STOPS = new Stop[]{new Stop(0, Color.GRAY), new Stop(0.5, Color.BLACK)};
private static final LinearGradient CELL_FILL = new LinearGradient(0, 3, 0, 28, false, CycleMethod.NO_CYCLE, CELL_STOPS);
private static final Stop[] CELL_SEL_STOPS = new Stop[]{new Stop(0, Color.BLACK), new Stop(0.3, Color.GRAY), new Stop(0.99, Color.BLACK)};
private static final LinearGradient CELL_SEL_FILL = new LinearGradient(0, 0, 0, 28, false, CycleMethod.NO_CYCLE, CELL_SEL_STOPS);
private Code code;
private Rectangle background;
private Text text;
public Key(Code code) {
this.code = code;
init();
getChildren().addAll(background, text);
}
private void init() {
createBackground();
createText();
initListeners();
}
private void initListeners() {
setOnMouseEntered((MouseEvent me) -> {
background.setFill(CELL_SEL_FILL);
});
setOnMouseExited((MouseEvent me) -> {
background.setFill(CELL_FILL);
});
}
private void createBackground() {
background = new Rectangle(WIDTH, HEIGHT);
background.setFill(CELL_FILL);
}
private void createText() {
text = new Text();
text.setFont(new Font("Amble Bold Condensed", 12));
text.setTranslateX(21);
text.setTranslateY(17);
text.setFill(Color.WHITE);
text.setY(5);
text.setText(code.getText());
}
public void setCellStroke(boolean selected) {
background.setStroke(selected ? Color.WHITE : Color.TRANSPARENT);
}
public Code getCode() {
return code;
}
}
