package javafx.scene.input;
import com.sun.javafx.tk.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
public abstract class KeyCombination {
public static final Modifier SHIFT_DOWN =
new Modifier(KeyCode.SHIFT, ModifierValue.DOWN);
public static final Modifier SHIFT_ANY =
new Modifier(KeyCode.SHIFT, ModifierValue.ANY);
public static final Modifier CONTROL_DOWN =
new Modifier(KeyCode.CONTROL, ModifierValue.DOWN);
public static final Modifier CONTROL_ANY =
new Modifier(KeyCode.CONTROL, ModifierValue.ANY);
public static final Modifier ALT_DOWN =
new Modifier(KeyCode.ALT, ModifierValue.DOWN);
public static final Modifier ALT_ANY =
new Modifier(KeyCode.ALT, ModifierValue.ANY);
public static final Modifier META_DOWN =
new Modifier(KeyCode.META, ModifierValue.DOWN);
public static final Modifier META_ANY =
new Modifier(KeyCode.META, ModifierValue.ANY);
public static final Modifier SHORTCUT_DOWN =
new Modifier(KeyCode.SHORTCUT, ModifierValue.DOWN);
public static final Modifier SHORTCUT_ANY =
new Modifier(KeyCode.SHORTCUT, ModifierValue.ANY);
private static final Modifier[] POSSIBLE_MODIFIERS = {
SHIFT_DOWN, SHIFT_ANY,
CONTROL_DOWN, CONTROL_ANY,
ALT_DOWN, ALT_ANY,
META_DOWN, META_ANY,
SHORTCUT_DOWN, SHORTCUT_ANY
};
public static final KeyCombination NO_MATCH = new KeyCombination() {
@Override
public boolean match(KeyEvent e) {
return false;
}
};
private final ModifierValue shift;
public final ModifierValue getShift() {
return shift;
}
private final ModifierValue control;
public final ModifierValue getControl() {
return control;
}
private final ModifierValue alt;
public final ModifierValue getAlt() {
return alt;
}
private final ModifierValue meta;
public final ModifierValue getMeta() {
return meta;
}
private final ModifierValue shortcut;
public final ModifierValue getShortcut() {
return shortcut;
}
protected KeyCombination(final ModifierValue shift,
final ModifierValue control,
final ModifierValue alt,
final ModifierValue meta,
final ModifierValue shortcut) {
if ((shift == null)
|| (control == null)
|| (alt == null)
|| (meta == null)
|| (shortcut == null)) {
throw new NullPointerException("Modifier value must not be null!");
}
this.shift = shift;
this.control = control;
this.alt = alt;
this.meta = meta;
this.shortcut = shortcut;
}
protected KeyCombination(final Modifier... modifiers) {
this(getModifierValue(modifiers, KeyCode.SHIFT),
getModifierValue(modifiers, KeyCode.CONTROL),
getModifierValue(modifiers, KeyCode.ALT),
getModifierValue(modifiers, KeyCode.META),
getModifierValue(modifiers, KeyCode.SHORTCUT));
}
public boolean match(final KeyEvent event) {
final KeyCode shortcutKey =
Toolkit.getToolkit().getPlatformShortcutKey();
return test(KeyCode.SHIFT, shift, shortcutKey, shortcut,
event.isShiftDown())
&& test(KeyCode.CONTROL, control, shortcutKey, shortcut,
event.isControlDown())
&& test(KeyCode.ALT, alt, shortcutKey, shortcut,
event.isAltDown())
&& test(KeyCode.META, meta, shortcutKey, shortcut,
event.isMetaDown());
}
public String getName() {
StringBuilder sb = new StringBuilder();
addModifiersIntoString(sb);
return sb.toString();
}
public String getDisplayText() {
StringBuilder stringBuilder = new StringBuilder();
if (com.sun.javafx.PlatformUtil.isMac()) {
if (getControl() == KeyCombination.ModifierValue.DOWN) {
stringBuilder.append("\u2303");
}
if (getAlt() == KeyCombination.ModifierValue.DOWN) {
stringBuilder.append("\u2325");
}
if (getShift() == KeyCombination.ModifierValue.DOWN) {
stringBuilder.append("\u21e7");
}
if (getMeta() == KeyCombination.ModifierValue.DOWN || getShortcut() == KeyCombination.ModifierValue.DOWN) {
stringBuilder.append("\u2318");
}
}
else {
if (getControl() == KeyCombination.ModifierValue.DOWN || getShortcut() == KeyCombination.ModifierValue.DOWN ) {
stringBuilder.append("Ctrl+");
}
if (getAlt() == KeyCombination.ModifierValue.DOWN) {
stringBuilder.append("Alt+");
}
if (getShift() == KeyCombination.ModifierValue.DOWN) {
stringBuilder.append("Shift+");
}
if (getMeta() == KeyCombination.ModifierValue.DOWN) {
stringBuilder.append("Meta+");
}
}
return stringBuilder.toString();
}
@Override
public boolean equals(final Object obj) {
if (!(obj instanceof KeyCombination)) {
return false;
}
final KeyCombination other = (KeyCombination) obj;
return (shift == other.shift)
&& (control == other.control)
&& (alt == other.alt)
&& (meta == other.meta)
&& (shortcut == other.shortcut);
}
@Override
public int hashCode() {
int hash = 7;
hash = 23 * hash + shift.hashCode();
hash = 23 * hash + control.hashCode();
hash = 23 * hash + alt.hashCode();
hash = 23 * hash + meta.hashCode();
hash = 23 * hash + shortcut.hashCode();
return hash;
}
@Override
public String toString() {
return getName();
}
public static KeyCombination valueOf(String value) {
final List<Modifier> modifiers = new ArrayList<Modifier>(4);
final String[] tokens = splitName(value);
KeyCode keyCode = null;
String keyCharacter = null;
for (String token : tokens) {
if ((token.length() > 2)
&& (token.charAt(0) == '\'')
&& (token.charAt(token.length() - 1) == '\'')) {
if ((keyCode != null) || (keyCharacter != null)) {
throw new IllegalArgumentException(
"Cannot parse key binding " + value);
}
keyCharacter = token.substring(1, token.length() - 1)
.replace("\\'", "'");
continue;
}
final String normalizedToken = normalizeToken(token);
final Modifier modifier = getModifier(normalizedToken);
if (modifier != null) {
modifiers.add(modifier);
continue;
}
if ((keyCode != null) || (keyCharacter != null)) {
throw new IllegalArgumentException(
"Cannot parse key binding " + value);
}
keyCode = KeyCode.getKeyCode(normalizedToken);
if (keyCode == null) {
keyCharacter = token;
}
}
if ((keyCode == null) && (keyCharacter == null)) {
throw new IllegalArgumentException(
"Cannot parse key binding " + value);
}
final Modifier[] modifierArray =
modifiers.toArray(new Modifier[modifiers.size()]);
return (keyCode != null)
? new KeyCodeCombination(keyCode, modifierArray)
: new KeyCharacterCombination(keyCharacter, modifierArray);
}
public static KeyCombination keyCombination(String name) {
return valueOf(name);
}
public static final class Modifier {
private final KeyCode key;
private final ModifierValue value;
private Modifier(final KeyCode key,
final ModifierValue value) {
this.key = key;
this.value = value;
}
public KeyCode getKey() {
return key;
}
public ModifierValue getValue() {
return value;
}
@Override
public String toString() {
return ((value == ModifierValue.ANY) ? "Ignore " : "")
+ key.getName();
}
}
public static enum ModifierValue {
DOWN,
UP,
ANY
}
private void addModifiersIntoString(final StringBuilder sb) {
addModifierIntoString(sb, KeyCode.SHIFT, shift);
addModifierIntoString(sb, KeyCode.CONTROL, control);
addModifierIntoString(sb, KeyCode.ALT, alt);
addModifierIntoString(sb, KeyCode.META, meta);
addModifierIntoString(sb, KeyCode.SHORTCUT, shortcut);
}
private static void addModifierIntoString(
final StringBuilder sb,
final KeyCode modifierKey,
final ModifierValue modifierValue) {
if (modifierValue == ModifierValue.UP) {
return;
}
if (sb.length() > 0) {
sb.append("+");
}
if (modifierValue == ModifierValue.ANY) {
sb.append("Ignore ");
}
sb.append(modifierKey.getName());
}
private static boolean test(final KeyCode testedModifierKey,
final ModifierValue testedModifierValue,
final KeyCode shortcutModifierKey,
final ModifierValue shortcutModifierValue,
final boolean isKeyDown) {
final ModifierValue finalModifierValue =
(testedModifierKey == shortcutModifierKey)
? resolveModifierValue(testedModifierValue,
shortcutModifierValue)
: testedModifierValue;
return test(finalModifierValue, isKeyDown);
}
private static boolean test(final ModifierValue modifierValue,
final boolean isDown) {
switch (modifierValue) {
case DOWN:
return isDown;
case UP:
return !isDown;
case ANY:
default:
return true;
}
}
private static ModifierValue resolveModifierValue(
final ModifierValue firstValue,
final ModifierValue secondValue) {
if ((firstValue == ModifierValue.DOWN)
|| (secondValue == ModifierValue.DOWN)) {
return ModifierValue.DOWN;
}
if ((firstValue == ModifierValue.ANY)
|| (secondValue == ModifierValue.ANY)) {
return ModifierValue.ANY;
}
return ModifierValue.UP;
}
static Modifier getModifier(final String name) {
for (final Modifier modifier: POSSIBLE_MODIFIERS) {
if (modifier.toString().equals(name)) {
return modifier;
}
}
return null;
}
private static ModifierValue getModifierValue(
final Modifier[] modifiers,
final KeyCode modifierKey) {
ModifierValue modifierValue = ModifierValue.UP;
for (final Modifier modifier: modifiers) {
if (modifier == null) {
throw new NullPointerException("Modifier must not be null!");
}
if (modifier.getKey() == modifierKey) {
if (modifierValue != ModifierValue.UP) {
throw new IllegalArgumentException(
(modifier.getValue() != modifierValue)
? "Conflicting modifiers specified!"
: "Duplicate modifiers specified!");
}
modifierValue = modifier.getValue();
}
}
return modifierValue;
}
private static String normalizeToken(final String token) {
final String[] words = token.split("\\s+");
final StringBuilder sb = new StringBuilder();
for (final String word: words) {
if (sb.length() > 0) {
sb.append(' ');
}
sb.append(word.substring(0, 1).toUpperCase(Locale.ROOT));
sb.append(word.substring(1).toLowerCase(Locale.ROOT));
}
return sb.toString();
}
private static String[] splitName(String name) {
List<String> tokens = new ArrayList<String>();
char[] chars = name.trim().toCharArray();
final int STATE_BASIC = 0;
final int STATE_WHITESPACE = 1;
final int STATE_SEPARATOR = 2;
final int STATE_QUOTED = 3;
int state = STATE_BASIC;
int tokenStart = 0;
int tokenEnd = -1;
for (int i = 0; i < chars.length; i++) {
char c = chars[i];
switch(state) {
case STATE_BASIC:
switch(c) {
case ' ':
case '\t':
case '\n':
case '\f':
case '\r':
case '\u000B':
tokenEnd = i;
state = STATE_WHITESPACE;
break;
case '+':
tokenEnd = i;
state = STATE_SEPARATOR;
break;
case '\'':
if (i == 0 || chars[i - 1] != '\\') {
state = STATE_QUOTED;
}
break;
default:
break;
}
break;
case STATE_WHITESPACE:
switch(c) {
case ' ':
case '\t':
case '\n':
case '\f':
case '\r':
case '\u000B':
break;
case '+':
state = STATE_SEPARATOR;
break;
case '\'':
state = STATE_QUOTED;
tokenEnd = -1;
break;
default:
state = STATE_BASIC;
tokenEnd = -1;
break;
}
break;
case STATE_SEPARATOR:
switch(c) {
case ' ':
case '\t':
case '\n':
case '\f':
case '\r':
case '\u000B':
break;
case '+':
throw new IllegalArgumentException(
"Cannot parse key binding " + name);
default:
if (tokenEnd <= tokenStart) {
throw new IllegalArgumentException(
"Cannot parse key binding " + name);
}
tokens.add(new String(chars,
tokenStart, tokenEnd - tokenStart));
tokenStart = i;
tokenEnd = -1;
state = (c == '\'' ? STATE_QUOTED : STATE_BASIC);
break;
}
break;
case STATE_QUOTED:
if (c == '\'' && chars[i - 1] != '\\') {
state = STATE_BASIC;
}
break;
}
}
switch(state) {
case STATE_BASIC:
case STATE_WHITESPACE:
tokens.add(new String(chars,
tokenStart, chars.length - tokenStart));
break;
case STATE_SEPARATOR:
case STATE_QUOTED:
throw new IllegalArgumentException(
"Cannot parse key binding " + name);
}
return tokens.toArray(new String[tokens.size()]);
}
}
