package javafx.scene.input;
import java.io.Serializable;
import javafx.beans.NamedArg;
public class InputMethodTextRun implements Serializable {
public InputMethodTextRun(@NamedArg("text") String text,
@NamedArg("highlight") InputMethodHighlight highlight) {
this.text = text;
this.highlight = highlight;
}
private final String text;
public final String getText() {
return text;
}
private final InputMethodHighlight highlight;
public final InputMethodHighlight getHighlight() {
return highlight;
}
@Override public String toString() {
return "InputMethodTextRun text [" + getText()
+ "], highlight [" + getHighlight() + "]";
}
}
