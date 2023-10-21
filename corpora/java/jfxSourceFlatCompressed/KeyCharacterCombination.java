package javafx.scene.input;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.NamedArg;
public final class KeyCharacterCombination extends KeyCombination {
private String character = "";
public final String getCharacter() {
return character;
}
public KeyCharacterCombination(final @NamedArg("character") String character,
final @NamedArg("shift") ModifierValue shift,
final @NamedArg("control") ModifierValue control,
final @NamedArg("alt") ModifierValue alt,
final @NamedArg("meta") ModifierValue meta,
final @NamedArg("shortcut") ModifierValue shortcut) {
super(shift, control, alt, meta, shortcut);
validateKeyCharacter(character);
this.character = character;
}
public KeyCharacterCombination(final @NamedArg("character") String character,
final @NamedArg("modifiers") Modifier... modifiers) {
super(modifiers);
validateKeyCharacter(character);
this.character = character;
}
@Override
public boolean match(final KeyEvent event) {
if (event.getCode() == KeyCode.UNDEFINED) {
return false;
}
return (event.getCode().getCode()
== Toolkit.getToolkit().getKeyCodeForChar(getCharacter()))
&& super.match(event);
}
@Override
public String getName() {
StringBuilder sb = new StringBuilder();
sb.append(super.getName());
if (sb.length() > 0) {
sb.append("+");
}
return sb.append('\'').append(character.replace("'", "\\'"))
.append('\'').toString();
}
@Override
public String getDisplayText() {
StringBuilder sb = new StringBuilder();
sb.append(super.getDisplayText());
sb.append(getCharacter());
return sb.toString();
}
@Override
public boolean equals(final Object obj) {
if (this == obj) {
return true;
}
if (!(obj instanceof KeyCharacterCombination)) {
return false;
}
return this.character.equals(((KeyCharacterCombination) obj).getCharacter())
&& super.equals(obj);
}
@Override
public int hashCode() {
return 23 * super.hashCode() + character.hashCode();
}
private static void validateKeyCharacter(final String keyCharacter) {
if (keyCharacter == null) {
throw new NullPointerException("Key character must not be null!");
}
}
}
