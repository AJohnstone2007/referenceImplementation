package test.com.sun.javafx.scene.control.test;
public class RT_22463_Person {
private Long id;
private String name;
public Long getId() {
return id;
}
public void setId(Long id) {
this.id = id;
}
public String getName() {
return name;
}
public void setName(String name) {
this.name = name;
}
@Override public String toString() {
return getName();
}
@Override public boolean equals(Object obj) {
if (obj == null) {
return false;
}
if (getClass() != obj.getClass()) {
return false;
}
final RT_22463_Person other = (RT_22463_Person) obj;
if (this.id != other.id) {
return false;
}
return true;
}
@Override public int hashCode() {
int hash = 7;
hash = 89 * hash + (int) (this.id ^ (this.id >>> 32));
return hash;
}
}
