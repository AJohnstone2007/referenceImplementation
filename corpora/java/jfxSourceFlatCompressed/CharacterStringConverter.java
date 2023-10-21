package javafx.util.converter;
import javafx.util.StringConverter;
public class CharacterStringConverter extends StringConverter<Character> {
public CharacterStringConverter() {
}
@Override public Character fromString(String value) {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return Character.valueOf(value.charAt(0));
}
@Override public String toString(Character value) {
if (value == null) {
return "";
}
return value.toString();
}
}
