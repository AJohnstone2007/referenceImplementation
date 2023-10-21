package javafx.scene.text;
import java.text.BreakIterator;
public class HitInfo {
private int charIndex;
private boolean leading;
private int insertionIndex;
private String text;
HitInfo(int charIndex, int insertionIndex, boolean leading, String text) {
this.charIndex = charIndex;
this.leading = leading;
this.insertionIndex = insertionIndex;
this.text = text;
}
public int getCharIndex() { return charIndex; }
public boolean isLeading() { return leading; }
private static BreakIterator charIterator = BreakIterator.getCharacterInstance();
public int getInsertionIndex() {
if (insertionIndex == -1) {
insertionIndex = charIndex;
if (!leading) {
if (text != null) {
int next;
synchronized(charIterator) {
charIterator.setText(text);
next = charIterator.following(insertionIndex);
}
if (next == BreakIterator.DONE) {
insertionIndex += 1;
} else {
insertionIndex = next;
}
} else {
insertionIndex += 1;
}
}
}
return insertionIndex;
}
@Override public String toString() {
return "charIndex: " + charIndex + ", isLeading: " + leading + ", insertionIndex: " + getInsertionIndex();
}
}
