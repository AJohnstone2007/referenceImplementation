package test.javafx.scene.text;
import javafx.scene.text.FontPosture;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
public class FontPostureTest {
@Test
public void testFindByName() {
assertEquals(FontPosture.REGULAR, FontPosture.findByName(""));
assertEquals(FontPosture.REGULAR, FontPosture.findByName("regular"));
assertEquals(FontPosture.ITALIC, FontPosture.findByName("italic"));
}
}
