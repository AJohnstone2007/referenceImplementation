package test.com.sun.webkit.network;
import com.sun.webkit.network.PublicSuffixesShim;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class PublicSuffixesTest {
@Test
public void testSelectedDomains() {
test("oracle.com", false);
test("google.com", false);
test("gmail.com", false);
test("yahoo.com", false);
test("facebook.com", false);
test("linkedin.com", false);
test("com", true);
test("co.uk", true);
test("org", true);
test("gov", true);
test("info", true);
test("cn", true);
test("ru", true);
test("spb.ru", true);
}
@Test
public void testSimpleRule() {
test("us.com", true);
test("foo.us.com", false);
}
@Test
public void testWildcardRule() {
test("mm", true);
test("foo.mm", true);
test("bar.foo.mm", false);
}
@Test
public void testExceptionRule() {
test("metro.tokyo.jp", false);
test("foo.metro.tokyo.jp", false);
test("tokyo.jp", true);
test("jp", true);
}
@Test
public void testIdnRule() {
test("xn--p1ai", true);
test("xn--80afoajeqg5e.xn--p1ai", false);
}
private static void test(String domain, boolean expectedResult) {
assertEquals("Unexpected result, domain: [" + domain + "],",
expectedResult, PublicSuffixesShim.isPublicSuffix(domain));
}
}
