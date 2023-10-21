package ensemble.samples.controls.text.textvalidator;
import javafx.scene.Parent;
public class ValidationResult extends Parent {
public enum Type { ERROR, WARNING, SUCCESS }
private final String message;
private final Type type;
public ValidationResult(String message, Type type) {
this.message = message;
this.type = type;
}
public final String getMessage() {
return message;
}
public final Type getType() {
return type;
}
}
