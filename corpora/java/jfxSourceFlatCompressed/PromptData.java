package javafx.scene.web;
import javafx.beans.NamedArg;
public final class PromptData {
private final String message;
private final String defaultValue;
public PromptData(@NamedArg("message") String message, @NamedArg("defaultValue") String defaultValue) {
this.message = message;
this.defaultValue = defaultValue;
}
public final String getMessage() {
return message;
}
public final String getDefaultValue() {
return defaultValue;
}
}
