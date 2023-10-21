package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import org.junit.Test;
public class RT_25494_Cycle_DetectionTest {
@Test(expected=IOException.class)
public void test_dummy_cycle() throws Exception {
FXMLLoader.load(RT_25494_Cycle_DetectionTest.class.getResource("dummy-cycle.fxml"));
}
@Test(expected=IOException.class)
public void test_one_2_one_cycle() throws Exception {
FXMLLoader.load(RT_25494_Cycle_DetectionTest.class.getResource("one-2-one-cycle.fxml"));
}
@Test(expected=IOException.class)
public void test_cycle() throws Exception {
FXMLLoader.load(RT_25494_Cycle_DetectionTest.class.getResource("cycle.fxml"));
}
}
