package test.com.sun.javafx.fxml.builder;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.StageStyle;
public class ClassWithDefaultValues {
public double a;
public double b;
public List<Integer> list;
public Color color;
public Paint fill;
public StageStyle stageStyle;
public ClassWithDefaultValues(
@NamedArg(value="a", defaultValue="1.0") double a,
@NamedArg(value="b", defaultValue="2.0") double b,
@NamedArg(value="color", defaultValue="red") Color color,
@NamedArg(value="fill", defaultValue="GREEN") Paint fill,
@NamedArg(value="stageStyle", defaultValue="DECORATED") StageStyle stageStyle
) {
this.a = a;
this.b = b;
this.color = color;
this.fill = fill;
this.stageStyle = stageStyle;
}
}
