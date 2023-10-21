package com.sun.javafx.scene.control.behavior;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCombination;
import java.util.Objects;
public class MnemonicInfo {
private final char MNEMONIC_SYMBOL = '_';
private String sourceText = null;
private String text = null;
public String getText() {
return text;
}
private String mnemonic = null;
private KeyCombination mnemonicKeyCombination = null;
public String getMnemonic() {
return mnemonic;
}
public KeyCombination getMnemonicKeyCombination() {
if (mnemonic != null && mnemonicKeyCombination == null) {
mnemonicKeyCombination = new MnemonicKeyCombination(mnemonic);
}
return mnemonicKeyCombination;
}
private int mnemonicIndex = -1;
public int getMnemonicIndex() {
return mnemonicIndex;
}
private String extendedMnemonicText = null;
public String getExtendedMnemonicText() {
return extendedMnemonicText;
}
public MnemonicInfo(String s) {
update(s);
}
public void update(String s) {
if (!Objects.equals(sourceText, s)) {
sourceText = s;
mnemonic = null;
mnemonicKeyCombination = null;
mnemonicIndex = -1;
extendedMnemonicText = null;
parseAndSplit(s);
}
}
private void parseAndSplit(String s) {
if (s == null || s.length() == 0) {
text = s;
return;
}
StringBuilder builder = new StringBuilder(s.length());
int i = 0;
for (int length = s.length(); i < length; ++i) {
if (isEscapedMnemonicSymbol(s, i)) {
builder.append(s.charAt(i++));
} else if (isExtendedMnemonic(s, i)) {
mnemonic = String.valueOf(s.charAt(i + 2));
mnemonicIndex = i;
extendedMnemonicText = s.substring(i + 1, i + 4);
i += 4;
break;
} else if (isSimpleMnemonic(s, i)) {
char c = s.charAt(i + 1);
mnemonic = String.valueOf(c);
mnemonicIndex = i;
i += 1;
break;
} else {
builder.append(s.charAt(i));
}
}
if (s.length() > i) {
builder.append(s.substring(i));
}
text = builder.toString();
}
private boolean isEscapedMnemonicSymbol(String s, int position) {
return s.length() > position + 1
&& s.charAt(position) == MNEMONIC_SYMBOL
&& s.charAt(position + 1) == MNEMONIC_SYMBOL;
}
private boolean isSimpleMnemonic(String s, int position) {
return s.length() > position + 1
&& s.charAt(position) == MNEMONIC_SYMBOL
&& s.charAt(position + 1) != MNEMONIC_SYMBOL
&& !Character.isWhitespace(s.charAt(position + 1));
}
private boolean isExtendedMnemonic(String s, int position) {
return s.length() > position + 3
&& s.charAt(position) == MNEMONIC_SYMBOL
&& s.charAt(position + 1) == '('
&& !Character.isWhitespace(s.charAt(position + 2))
&& s.charAt(position + 3) == ')';
}
public static class MnemonicKeyCombination extends KeyCombination {
private String character = "";
public MnemonicKeyCombination(String character) {
super(com.sun.javafx.PlatformUtil.isMac()
? KeyCombination.META_DOWN
: KeyCombination.ALT_DOWN);
this.character = character;
}
public final String getCharacter() {
return character;
}
@Override public boolean match(final KeyEvent event) {
String text = event.getText();
return (text != null
&& !text.isEmpty()
&& text.equalsIgnoreCase(getCharacter())
&& super.match(event));
}
@Override public String getName() {
StringBuilder sb = new StringBuilder();
sb.append(super.getName());
if (sb.length() > 0) {
sb.append("+");
}
return sb.append('\'').append(character.replace("'", "\\'"))
.append('\'').toString();
}
@Override public boolean equals(final Object obj) {
if (this == obj) {
return true;
}
if (!(obj instanceof MnemonicKeyCombination)) {
return false;
}
return (this.character.equals(((MnemonicKeyCombination)obj).getCharacter())
&& super.equals(obj));
}
@Override public int hashCode() {
return 23 * super.hashCode() + character.hashCode();
}
}
}
