package ensemble.compiletime.search;
public enum DocumentType {
SAMPLE("Samples"),
CLASS("Classes"),
PROPERTY("Properties"),
METHOD("Methods"),
FIELD("Fields"),
ENUM("Enums"),
DOC("Documentation");
private final String pluralDisplayName;
DocumentType(String pluralDisplayName) {
this.pluralDisplayName = pluralDisplayName;
}
public String getPluralDisplayName() {
return pluralDisplayName;
}
}
