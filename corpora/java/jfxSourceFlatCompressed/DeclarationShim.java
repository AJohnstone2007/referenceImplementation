package javafx.css;
public class DeclarationShim {
public static ParsedValue get_parsedValue(Declaration d) {
return d.parsedValue;
}
public static String get_property(Declaration d) {
return d.property;
}
public static Declaration getDeclaration(final String propertyName, final ParsedValue parsedValue,
final boolean important) {
return new Declaration(propertyName, parsedValue, important);
}
}
