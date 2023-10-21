package test.robot.javafx.scene.layout;
import org.junit.Test;
public class RegionBorderImageUITest extends RegionUITestBase {
@Test public void dummy() {
}
@Test(timeout = 20000)
public void test4() {
setStyle(
"-fx-border-image-source: url('test/robot/javafx/scene/layout/border-stretch.png');" +
"-fx-border-image-slice: 14;" +
"-fx-border-image-width: 14;" +
"-fx-border-image-repeat: stretch;");
System.out.println("WHAT");
}
}
