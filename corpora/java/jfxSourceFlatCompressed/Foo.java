package test.javafx.beans;
import com.sun.javafx.property.PropertyReference;
public interface Foo {
public static final PropertyReference<String> NAME = new PropertyReference<String>(Foo.class, "name");
public void setName(String name);
public String getName();
}
