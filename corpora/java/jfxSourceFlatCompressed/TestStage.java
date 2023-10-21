package test.com.sun.javafx.test.objects;
import javafx.stage.Stage;
public class TestStage extends Stage {
private final String name;
public TestStage(final String name) {
this.name = name;
}
@Override
public String toString() {
return name;
}
}
