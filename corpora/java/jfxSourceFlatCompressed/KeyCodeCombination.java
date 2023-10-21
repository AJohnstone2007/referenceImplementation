package javafx.scene.input;
import javafx.beans.NamedArg;
public final class KeyCodeCombination extends KeyCombination {
private KeyCode code;
public final KeyCode getCode() {
return code;
}
public KeyCodeCombination(final @NamedArg("code") KeyCode code,
final @NamedArg("shift") ModifierValue shift,
final @NamedArg("control") ModifierValue control,
final @NamedArg("alt") ModifierValue alt,
final @NamedArg("meta") ModifierValue meta,
final @NamedArg("shortcut") ModifierValue shortcut) {
super(shift, control, alt, meta, shortcut);
validateKeyCode(code);
this.code = code;
}
public KeyCodeCombination(final @NamedArg("code") KeyCode code,
final @NamedArg("modifiers") Modifier... modifiers) {
super(modifiers);
validateKeyCode(code);
this.code = code;
}
@Override
public boolean match(final KeyEvent event) {
return (event.getCode() == getCode()) && super.match(event);
}
@Override
public String getName() {
StringBuilder sb = new StringBuilder();
sb.append(super.getName());
if (sb.length() > 0) {
sb.append("+");
}
return sb.append(code.getName()).toString();
}
@Override
public String getDisplayText() {
StringBuilder sb = new StringBuilder();
sb.append(super.getDisplayText());
final int initialLength = sb.length();
char c = getSingleChar(code);
if (c != 0) {
sb.append(c);
return sb.toString();
}
String name = code.toString();
String[] words = com.sun.javafx.util.Utils.split(name, "_");
for (String word : words) {
if (sb.length() > initialLength) {
sb.append(' ');
}
sb.append(word.charAt(0));
sb.append(word.substring(1).toLowerCase());
}
return sb.toString();
}
@Override
public boolean equals(final Object obj) {
if (this == obj) {
return true;
}
if (!(obj instanceof KeyCodeCombination)) {
return false;
}
return (this.getCode() == ((KeyCodeCombination) obj).getCode())
&& super.equals(obj);
}
@Override
public int hashCode() {
return 23 * super.hashCode() + code.hashCode();
}
private static void validateKeyCode(final KeyCode keyCode) {
if (keyCode == null) {
throw new NullPointerException("Key code must not be null!");
}
if (getModifier(keyCode.getName()) != null) {
throw new IllegalArgumentException(
"Key code must not match modifier key!");
}
if (keyCode == KeyCode.UNDEFINED) {
throw new IllegalArgumentException(
"Key code must differ from undefined value!");
}
}
private static char getSingleChar(KeyCode code) {
switch (code) {
case ENTER: return '\u21B5';
case LEFT: return '\u2190';
case UP: return '\u2191';
case RIGHT: return '\u2192';
case DOWN: return '\u2193';
case COMMA: return ',';
case MINUS: return '-';
case PERIOD: return '.';
case SLASH: return '/';
case SEMICOLON: return ';';
case EQUALS: return '=';
case OPEN_BRACKET: return '[';
case BACK_SLASH: return '\\';
case CLOSE_BRACKET: return ']';
case MULTIPLY: return '*';
case ADD: return '+';
case SUBTRACT: return '-';
case DECIMAL: return '.';
case DIVIDE: return '/';
case BACK_QUOTE: return '`';
case QUOTE: return '"';
case AMPERSAND: return '&';
case ASTERISK: return '*';
case LESS: return '<';
case GREATER: return '>';
case BRACELEFT: return '{';
case BRACERIGHT: return '}';
case AT: return '@';
case COLON: return ':';
case CIRCUMFLEX: return '^';
case DOLLAR: return '$';
case EURO_SIGN: return '\u20AC';
case EXCLAMATION_MARK: return '!';
case LEFT_PARENTHESIS: return '(';
case NUMBER_SIGN: return '#';
case PLUS: return '+';
case RIGHT_PARENTHESIS: return ')';
case UNDERSCORE: return '_';
case DIGIT0: return '0';
case DIGIT1: return '1';
case DIGIT2: return '2';
case DIGIT3: return '3';
case DIGIT4: return '4';
case DIGIT5: return '5';
case DIGIT6: return '6';
case DIGIT7: return '7';
case DIGIT8: return '8';
case DIGIT9: return '9';
default:
break;
}
if (com.sun.javafx.PlatformUtil.isMac()) {
switch (code) {
case BACK_SPACE: return '\u232B';
case ESCAPE: return '\u238B';
case DELETE: return '\u2326';
default:
break;
}
}
return 0;
}
}
