package javafx.scene.transform;
import javafx.beans.NamedArg;
public class NonInvertibleTransformException extends java.lang.Exception {
public NonInvertibleTransformException(@NamedArg("message") String message) {
super (message);
}
}
