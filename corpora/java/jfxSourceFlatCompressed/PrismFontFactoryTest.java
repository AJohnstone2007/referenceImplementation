package test.com.sun.javafx.font;
import com.sun.javafx.font.PrismFontFactory;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class PrismFontFactoryTest {
public PrismFontFactoryTest() {
}
@BeforeClass
public static void setUpClass() throws Exception {
}
@AfterClass
public static void tearDownClass() throws Exception {
}
@Before
public void setUp() {
}
@After
public void tearDown() {
}
@Test
public void testGetFontFactory() {
PrismFontFactory expResult = null;
PrismFontFactory result = PrismFontFactory.getFontFactory();
assertNotNull("Should never turn null", result);
expResult = PrismFontFactory.getFontFactory();
assertEquals("Creates different instance of FontFactory", expResult, result);
}
}
