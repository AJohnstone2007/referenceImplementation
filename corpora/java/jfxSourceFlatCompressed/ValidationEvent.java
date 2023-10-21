package ensemble.samples.controls.text.textvalidator;
import javafx.event.Event;
import javafx.event.EventType;
public class ValidationEvent extends Event {
public static final EventType<ValidationEvent> ANY_EVENT =
new EventType<ValidationEvent>(Event.ANY, "VALIDATION");
private final ValidationResult result;
public ValidationEvent(ValidationResult result) {
super(ANY_EVENT);
this.result = result;
}
public final ValidationResult getResult() {
return result;
}
}
