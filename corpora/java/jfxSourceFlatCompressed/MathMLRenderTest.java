package test.javafx.scene.web;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
public class MathMLRenderTest extends TestBase {
@Test public void testTokenHeight() throws Exception {
loadContent("<!doctype html><html><body><math><mo>=</mo></math></body></html>");
int height = (int) executeScript("document.getElementsByTagName('mo')[0].clientHeight");
assertTrue("MathML token height is lesser than expected " + height, height > 1);
}
}
