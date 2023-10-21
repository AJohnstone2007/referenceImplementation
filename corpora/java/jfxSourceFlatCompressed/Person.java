package test.com.sun.javafx.scene.control.test;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public class Person {
private final SimpleBooleanProperty invited;
private final SimpleStringProperty firstName;
private final SimpleStringProperty lastName;
private final SimpleStringProperty email;
private final ReadOnlyIntegerWrapper age;
public Person(String fName, int age) {
this(fName, null, null, age);
}
public Person(String fName, String lName, String email) {
this(fName, lName, email, 0);
}
public Person(String fName, String lName, String email, int age) {
this.firstName = new SimpleStringProperty(fName);
this.lastName = new SimpleStringProperty(lName);
this.email = new SimpleStringProperty(email);
this.age = new ReadOnlyIntegerWrapper(age);
this.invited = null;
}
public Person(String fName, String lName, String email, boolean invited) {
this.firstName = new SimpleStringProperty(fName);
this.lastName = new SimpleStringProperty(lName);
this.email = new SimpleStringProperty(email);
this.age = null;
this.invited = new SimpleBooleanProperty(invited);
}
public String getFirstName() {
return firstName.get();
}
public void setFirstName(String fName) {
firstName.set(fName);
}
public StringProperty firstNameProperty() {
return firstName;
}
public String getLastName() {
return lastName.get();
}
public void setLastName(String fName) {
lastName.set(fName);
}
public StringProperty lastNameProperty() {
return lastName;
}
public String getEmail() {
return email.get();
}
public void setEmail(String fName) {
email.set(fName);
}
public StringProperty emailProperty() {
return email;
}
public final int getAge() {return age.get();}
public ReadOnlyIntegerProperty ageProperty() {return age.getReadOnlyProperty();}
public final boolean isInvited() { return invited.get(); }
public BooleanProperty invitedProperty() { return invited; }
@Override public String toString() {
return getFirstName() + " " + getLastName();
}
public static ObservableList<Person> persons() {
return FXCollections.observableArrayList(
new Person("Jacob", "Smith", "jacob.smith@example.com"),
new Person("Isabella", "Johnson", "isabella.johnson@example.com"),
new Person("Ethan", "Williams", "ethan.williams@example.com"),
new Person("Emma", "Jones", "emma.jones@example.com"),
new Person("Michael", "Brown", "michael.brown@example.com")
);
}
}
