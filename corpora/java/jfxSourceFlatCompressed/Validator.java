package ensemble.samples.controls.text.textvalidator;
import javafx.scene.control.Control;
public interface Validator<C extends Control> {
public ValidationResult validate(C control);
}
