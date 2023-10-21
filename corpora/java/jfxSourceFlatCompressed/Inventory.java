package ensemble.samples.controls.treetableview;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class Inventory {
private final StringProperty name;
private final ObjectProperty data;
private final StringProperty notes;
public Inventory(String name, Data data, String notes) {
this.name = new SimpleStringProperty(name);
this.data = new SimpleObjectProperty<>(data);
this.notes = new SimpleStringProperty(notes);
}
public StringProperty nameProperty() {
return name;
}
public StringProperty notesProperty() {
return notes;
}
public ObjectProperty<Data> dataProperty() {
return data;
}
}
