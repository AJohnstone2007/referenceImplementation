package test.robot.javafx.embed.swing;
import test.robot.javafx.embed.swing.RT30650GUI;
import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assume.assumeTrue;
public class RT30650Test {
@Test(timeout = 15000)
public void test() {
assumeTrue(Boolean.getBoolean("unstable.test"));
Assert.assertTrue(RT30650GUI.test());
System.out.println("Passed.");
}
public static void main(String[] args) {
new RT30650Test().test();
}
}
