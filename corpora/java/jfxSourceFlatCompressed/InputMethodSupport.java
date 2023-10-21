package javafx.embed.swing;
import com.sun.javafx.collections.ObservableListWrapper;
import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.input.InputMethodTextRun;
import java.awt.Rectangle;
import java.awt.event.InputMethodEvent;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;
class InputMethodSupport {
public static class InputMethodRequestsAdapter implements InputMethodRequests {
private final javafx.scene.input.InputMethodRequests fxRequests;
public InputMethodRequestsAdapter(javafx.scene.input.InputMethodRequests fxRequests) {
this.fxRequests = fxRequests;
}
@Override
public Rectangle getTextLocation(TextHitInfo offset) {
Point2D result = fxRequests.getTextLocation(offset.getInsertionIndex());
return new Rectangle((int)result.getX(), (int)result.getY(), 0, 0);
}
@Override
public TextHitInfo getLocationOffset(int x, int y) {
int result = fxRequests.getLocationOffset(x, y);
return TextHitInfo.afterOffset(result);
}
@Override
public int getInsertPositionOffset() {
if (fxRequests instanceof ExtendedInputMethodRequests) {
return ((ExtendedInputMethodRequests)fxRequests).getInsertPositionOffset();
}
return 0;
}
@Override
public AttributedCharacterIterator getCommittedText(int beginIndex, int endIndex, AttributedCharacterIterator.Attribute[] attributes) {
String result = null;
if (fxRequests instanceof ExtendedInputMethodRequests) {
result = ((ExtendedInputMethodRequests)fxRequests).getCommittedText(beginIndex, endIndex);
}
if (result == null) result = "";
return new AttributedString(result).getIterator();
}
@Override
public int getCommittedTextLength() {
if (fxRequests instanceof ExtendedInputMethodRequests) {
return ((ExtendedInputMethodRequests)fxRequests).getCommittedTextLength();
}
return 0;
}
@Override
public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] attributes) {
return null;
}
@Override
public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] attributes) {
String text = fxRequests.getSelectedText();
if (text == null) text = "";
return new AttributedString(text).getIterator();
}
}
public static ObservableList<InputMethodTextRun> inputMethodEventComposed(String text, int commitCount)
{
List<InputMethodTextRun> composed = new ArrayList<>();
if (commitCount < text.length()) {
composed.add(new InputMethodTextRun(
text.substring(commitCount),
InputMethodHighlight.UNSELECTED_RAW));
}
return new ObservableListWrapper<>(composed);
}
public static String getTextForEvent(InputMethodEvent e) {
AttributedCharacterIterator text = e.getText();
if (e.getText() != null) {
char c = text.first();
StringBuilder result = new StringBuilder();
while (c != CharacterIterator.DONE) {
result.append(c);
c = text.next();
}
return result.toString();
}
return "";
}
}
