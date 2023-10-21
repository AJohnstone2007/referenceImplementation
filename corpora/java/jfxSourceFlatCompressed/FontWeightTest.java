package test.javafx.scene.text;
import javafx.scene.text.FontWeight;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;
public class FontWeightTest {
@Test
public void testFindByName() {
Object[] map = new Object[] {
FontWeight.LIGHT, "Light",
FontWeight.BOLD, "Bold",
FontWeight.EXTRA_BOLD, "Extra Bold",
FontWeight.BLACK, "Black"
};
for (int i = 0; i < map.length; i += 2) {
assertEquals(map[i], FontWeight.findByName((String) map[i + 1]));
}
}
}
