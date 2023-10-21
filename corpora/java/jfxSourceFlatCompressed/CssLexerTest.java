package test.javafx.css;
import com.sun.javafx.css.parser.Token;
import com.sun.javafx.css.parser.TokenShim;
import java.io.CharArrayReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.css.CssLexerShim;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class CssLexerTest {
public CssLexerTest() {
}
private void checkTokens(List<TokenShim> resultTokens, TokenShim... expectedTokens)
throws org.junit.ComparisonFailure {
if (expectedTokens.length != resultTokens.size()) {
throw new org.junit.ComparisonFailure(
"lengths do not match",
Arrays.toString(expectedTokens),
resultTokens.toString()
);
}
for (int n = 0; n<expectedTokens.length; n++) {
final TokenShim result = resultTokens.get(n);
final TokenShim expected = expectedTokens[n];
if (expected.getType() != result.getType()) {
throw new org.junit.ComparisonFailure(
"token " + n + " types do not match",
Arrays.toString(expectedTokens),
resultTokens.toString()
);
}
final String expectedText = expected.getText();
final String resultText = result.getText();
if (expectedText == null ? resultText != null : !expectedText.equals(resultText)) {
throw new org.junit.ComparisonFailure(
"token " + n + " text does not match",
Arrays.toString(expectedTokens),
resultTokens.toString()
);
}
}
}
List<TokenShim> getTokens(String string) {
Reader reader = new CharArrayReader(string.toCharArray());
final CssLexerShim lexer = new CssLexerShim();
lexer.setReader(reader);
final List<TokenShim> tokens = new ArrayList<TokenShim>();
TokenShim token = null;
do {
token = lexer.nextToken();
tokens.add(token);
} while (token.getType() != Token.EOF);
return Collections.unmodifiableList(tokens);
}
private void lexDigitsWithUnits(String units, int type) throws org.junit.ComparisonFailure {
checkTokens(getTokens("123"+units), new TokenShim(type, "123"+units), TokenShim.EOF_TOKEN);
checkTokens(getTokens("123.45"+units), new TokenShim(type, "123.45"+units), TokenShim.EOF_TOKEN);
checkTokens(getTokens(".45"+units), new TokenShim(type, ".45"+units), TokenShim.EOF_TOKEN);
checkTokens(getTokens("-123"+units), new TokenShim(type, "-123"+units), TokenShim.EOF_TOKEN);
checkTokens(getTokens("-.45"+units), new TokenShim(type, "-.45"+units), TokenShim.EOF_TOKEN);
checkTokens(getTokens("+123"+units), new TokenShim(type, "+123"+units), TokenShim.EOF_TOKEN);
checkTokens(getTokens("+.45"+units), new TokenShim(type, "+.45"+units), TokenShim.EOF_TOKEN);
}
@Test
public void testLexValidDigits() {
lexDigitsWithUnits("", CssLexerShim.NUMBER);
}
@Test
public void testLexValidDigitsWithCM() {
lexDigitsWithUnits("cm", CssLexerShim.CM);
lexDigitsWithUnits("cM", CssLexerShim.CM);
}
@Test
public void testLexValidDigitsWithDEG() {
lexDigitsWithUnits("deg", CssLexerShim.DEG);
lexDigitsWithUnits("dEg", CssLexerShim.DEG);
}
@Test
public void testLexValidDigitsWithEM() {
lexDigitsWithUnits("em", CssLexerShim.EMS);
lexDigitsWithUnits("Em", CssLexerShim.EMS);
}
@Test
public void testLexValidDigitsWithEX() {
lexDigitsWithUnits("ex", CssLexerShim.EXS);
lexDigitsWithUnits("Ex", CssLexerShim.EXS);
}
@Test
public void testLexValidDigitsWithGRAD() {
lexDigitsWithUnits("grad", CssLexerShim.GRAD);
lexDigitsWithUnits("gRad", CssLexerShim.GRAD);
}
@Test
public void testLexValidDigitsWithIN() {
lexDigitsWithUnits("in", CssLexerShim.IN);
lexDigitsWithUnits("In", CssLexerShim.IN);
}
@Test
public void testLexValidDigitsWithMM() {
lexDigitsWithUnits("mm", CssLexerShim.MM);
lexDigitsWithUnits("mM", CssLexerShim.MM);
}
@Test
public void testLexValidDigitsWithPC() {
lexDigitsWithUnits("pc", CssLexerShim.PC);
lexDigitsWithUnits("Pc", CssLexerShim.PC);
}
@Test
public void testLexValidDigitsWithPT() {
lexDigitsWithUnits("pt", CssLexerShim.PT);
lexDigitsWithUnits("PT", CssLexerShim.PT);
}
@Test
public void testLexValidDigitsWithPX() {
lexDigitsWithUnits("px", CssLexerShim.PX);
lexDigitsWithUnits("Px", CssLexerShim.PX);
}
@Test
public void testLexValidDigitsWithRAD() {
lexDigitsWithUnits("rad", CssLexerShim.RAD);
lexDigitsWithUnits("RaD", CssLexerShim.RAD);
}
@Test
public void testLexValidDigitsWithTURN() {
lexDigitsWithUnits("turn", CssLexerShim.TURN);
lexDigitsWithUnits("TurN", CssLexerShim.TURN);
}
@Test
public void testLexValidDigitsWithS() {
lexDigitsWithUnits("s", CssLexerShim.SECONDS);
lexDigitsWithUnits("S", CssLexerShim.SECONDS);
}
@Test
public void testLexValidDigitsWithMS() {
lexDigitsWithUnits("ms", CssLexerShim.MS);
lexDigitsWithUnits("mS", CssLexerShim.MS);
}
@Test
public void testLexValidDigitsWithPCT() {
lexDigitsWithUnits("%", CssLexerShim.PERCENTAGE);
}
@Test
public void testLexValidDigitsWithBadUnits() {
lexDigitsWithUnits("xyzzy", Token.INVALID);
}
@Test
public void textLexValidDigitsValidDigits() {
checkTokens(
getTokens("foo: 10pt; bar: 20%;"),
new TokenShim(CssLexerShim.IDENT, "foo"),
new TokenShim(CssLexerShim.COLON, ":"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.PT, "10pt"),
new TokenShim(CssLexerShim.SEMI, ";"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.IDENT, "bar"),
new TokenShim(CssLexerShim.COLON, ":"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.PERCENTAGE, "20%"),
new TokenShim(CssLexerShim.SEMI, ";"),
TokenShim.EOF_TOKEN
);
}
@Test
public void textLexInvalidDigitsValidDigits() {
checkTokens(
getTokens("foo: 10pz; bar: 20%;"),
new TokenShim(CssLexerShim.IDENT, "foo"),
new TokenShim(CssLexerShim.COLON, ":"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(Token.INVALID, "10pz"),
new TokenShim(CssLexerShim.SEMI, ";"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.IDENT, "bar"),
new TokenShim(CssLexerShim.COLON, ":"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.PERCENTAGE, "20%"),
new TokenShim(CssLexerShim.SEMI, ";"),
TokenShim.EOF_TOKEN
);
}
@Test
public void textLexValidDigitsBangImportant() {
checkTokens(
getTokens("foo: 10pt !important;"),
new TokenShim(CssLexerShim.IDENT, "foo"),
new TokenShim(CssLexerShim.COLON, ":"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.PT, "10pt"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.IMPORTANT_SYM, "!important"),
new TokenShim(CssLexerShim.SEMI, ";"),
TokenShim.EOF_TOKEN
);
}
@Test
public void textLexInvalidDigitsBangImportant() {
checkTokens(
getTokens("foo: 10pz !important;"),
new TokenShim(CssLexerShim.IDENT, "foo"),
new TokenShim(CssLexerShim.COLON, ":"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(Token.INVALID, "10pz"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.IMPORTANT_SYM, "!important"),
new TokenShim(CssLexerShim.SEMI, ";"),
TokenShim.EOF_TOKEN
);
}
@Test
public void textLexValidDigitsInSequence() {
checkTokens(
getTokens("-1 0px 1pt .5em;"),
new TokenShim(CssLexerShim.NUMBER, "-1"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.PX, "0px"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.PT, "1pt"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.EMS, ".5em"),
new TokenShim(CssLexerShim.SEMI, ";"),
TokenShim.EOF_TOKEN
);
}
@Test
public void textLexInvalidDigitsInSequence() {
checkTokens(
getTokens("-1 0px 1pz .5em;"),
new TokenShim(CssLexerShim.NUMBER, "-1"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.PX, "0px"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(Token.INVALID, "1pz"),
new TokenShim(CssLexerShim.WS, " "),
new TokenShim(CssLexerShim.EMS, ".5em"),
new TokenShim(CssLexerShim.SEMI, ";"),
TokenShim.EOF_TOKEN
);
}
@Test
public void testTokenOffset() {
String str = "a: b;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.IDENT, "b", 1, 3),
new TokenShim(CssLexerShim.SEMI, ";", 1, 4),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenLineAndOffsetWithCR() {
String str = "a: b;\rc: d;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.IDENT, "b", 1, 3),
new TokenShim(CssLexerShim.SEMI, ";", 1, 4),
new TokenShim(CssLexerShim.NL, "\\r", 1, 5),
new TokenShim(CssLexerShim.IDENT, "c", 2, 0),
new TokenShim(CssLexerShim.COLON, ":", 2, 1),
new TokenShim(CssLexerShim.WS, " ", 2, 2),
new TokenShim(CssLexerShim.IDENT, "d", 2, 3),
new TokenShim(CssLexerShim.SEMI, ";", 2, 4),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenLineAndOffsetWithLF() {
String str = "a: b;\nc: d;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.IDENT, "b", 1, 3),
new TokenShim(CssLexerShim.SEMI, ";", 1, 4),
new TokenShim(CssLexerShim.NL, "\\n", 1, 5),
new TokenShim(CssLexerShim.IDENT, "c", 2, 0),
new TokenShim(CssLexerShim.COLON, ":", 2, 1),
new TokenShim(CssLexerShim.WS, " ", 2, 2),
new TokenShim(CssLexerShim.IDENT, "d", 2, 3),
new TokenShim(CssLexerShim.SEMI, ";", 2, 4),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenLineAndOffsetWithCRLF() {
String str = "a: b;\r\nc: d;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.IDENT, "b", 1, 3),
new TokenShim(CssLexerShim.SEMI, ";", 1, 4),
new TokenShim(CssLexerShim.NL, "\\r\\n", 1, 5),
new TokenShim(CssLexerShim.IDENT, "c", 2, 0),
new TokenShim(CssLexerShim.COLON, ":", 2, 1),
new TokenShim(CssLexerShim.WS, " ", 2, 2),
new TokenShim(CssLexerShim.IDENT, "d", 2, 3),
new TokenShim(CssLexerShim.SEMI, ";", 2, 4),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenOffsetWithEmbeddedComment() {
String str = "a: /*comment*/b;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.IDENT, "b", 1, 14),
new TokenShim(CssLexerShim.SEMI, ";", 1, 15),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenLineAndOffsetWithLeadingComment() {
String str = "/*comment*/\na: b;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.NL, "\\n", 1, 11),
new TokenShim(CssLexerShim.IDENT, "a", 2, 0),
new TokenShim(CssLexerShim.COLON, ":", 2, 1),
new TokenShim(CssLexerShim.WS, " ", 2, 2),
new TokenShim(CssLexerShim.IDENT, "b", 2, 3),
new TokenShim(CssLexerShim.SEMI, ";", 2, 4),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenOffsetWithFunction() {
String str = "a: b(arg);";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.IDENT, "b", 1, 3),
new TokenShim(CssLexerShim.LPAREN, "(", 1, 4),
new TokenShim(CssLexerShim.IDENT, "arg", 1, 5),
new TokenShim(CssLexerShim.RPAREN, ")", 1, 8),
new TokenShim(CssLexerShim.SEMI, ";", 1, 9),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenOffsetWithHash() {
String str = "a: #012345;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.HASH, "#012345", 1, 3),
new TokenShim(CssLexerShim.SEMI, ";", 1, 10),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenOffsetWithDigits() {
String str = "a: 123.45;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.NUMBER, "123.45", 1, 3),
new TokenShim(CssLexerShim.SEMI, ";", 1, 9),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenOffsetWithBangImportant() {
String str = "a: b !important;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.IDENT, "b", 1, 3),
new TokenShim(CssLexerShim.WS, " ", 1, 4),
new TokenShim(CssLexerShim.IMPORTANT_SYM, "!important", 1, 5),
new TokenShim(CssLexerShim.SEMI, ";", 1, 15),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenOffsetWithSkip() {
String str = "a: b !imporzant;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(CssLexerShim.IDENT, "b", 1, 3),
new TokenShim(CssLexerShim.WS, " ", 1, 4),
new TokenShim(Token.SKIP, "!imporz", 1, 5),
new TokenShim(CssLexerShim.SEMI, ";", 1, 15),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenOffsetWithInvalid() {
String str = "a: 1pz;";
TokenShim[] expected = {
new TokenShim(CssLexerShim.IDENT, "a", 1, 0),
new TokenShim(CssLexerShim.COLON, ":", 1, 1),
new TokenShim(CssLexerShim.WS, " ", 1, 2),
new TokenShim(Token.INVALID, "1pz", 1, 3),
new TokenShim(CssLexerShim.SEMI, ";", 1, 6),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testTokenLineAndOffsetMoreFully() {
String str = "/*comment*/\n*.foo#bar:baz {\n\ta: 1em;\n}";
TokenShim[] expected = {
new TokenShim(CssLexerShim.NL, "\\n", 1, 11),
new TokenShim(CssLexerShim.STAR, "*", 2, 0),
new TokenShim(CssLexerShim.DOT, ".", 2, 1),
new TokenShim(CssLexerShim.IDENT, "foo", 2, 2),
new TokenShim(CssLexerShim.HASH, "#bar", 2, 5),
new TokenShim(CssLexerShim.COLON, ":", 2, 9),
new TokenShim(CssLexerShim.IDENT, "baz", 2, 10),
new TokenShim(CssLexerShim.WS, " ", 2, 13),
new TokenShim(CssLexerShim.LBRACE, "{", 2, 14),
new TokenShim(CssLexerShim.NL, "\\n", 2, 15),
new TokenShim(CssLexerShim.WS, "\t", 3, 0),
new TokenShim(CssLexerShim.IDENT, "a", 3, 1),
new TokenShim(CssLexerShim.COLON, ":", 3, 2),
new TokenShim(CssLexerShim.WS, " ", 3, 3),
new TokenShim(CssLexerShim.EMS, "1em", 3, 4),
new TokenShim(CssLexerShim.SEMI, ";", 3, 7),
new TokenShim(CssLexerShim.NL, "\\n", 3, 8),
new TokenShim(CssLexerShim.RBRACE, "}", 4, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testScanUrl() {
String str = "url(http://foo.bar.com/fonts/serif/fubar.ttf)";
TokenShim[] expected = new TokenShim[]{
new TokenShim(CssLexerShim.URL, "http://foo.bar.com/fonts/serif/fubar.ttf", 1, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testScanUrlWithWhiteSpace() {
String str = "url(    http://foo.bar.com/fonts/serif/fubar.ttf\t)";
TokenShim[] expected = new TokenShim[]{
new TokenShim(CssLexerShim.URL, "http://foo.bar.com/fonts/serif/fubar.ttf", 1, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testScanQuotedUrlWithWhiteSpace() {
String str = "url(    'http://foo.bar.com/fonts/serif/fubar.ttf'\t)";
TokenShim[] expected = new TokenShim[]{
new TokenShim(CssLexerShim.URL, "http://foo.bar.com/fonts/serif/fubar.ttf", 1, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testScanQuotedUrl() {
String str = "url(\"http://foo.bar.com/fonts/serif/fubar.ttf\")";
TokenShim[] expected = new TokenShim[]{
new TokenShim(CssLexerShim.URL, "http://foo.bar.com/fonts/serif/fubar.ttf", 1, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testScanUrlWithEscapes() {
String str = "url(http://foo.bar.com/fonts/true\\ type/fubar.ttf)";
TokenShim[] expected = new TokenShim[]{
new TokenShim(CssLexerShim.URL, "http://foo.bar.com/fonts/true type/fubar.ttf", 1, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testScanQuotedUrlWithEscapes() {
String str = "url(\"http://foo.bar.com/fonts/true\\ type/fubar.ttf\")";
TokenShim[] expected = new TokenShim[]{
new TokenShim(CssLexerShim.URL, "http://foo.bar.com/fonts/true type/fubar.ttf", 1, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testScanUrlWithSyntaxError() {
String str = "url(http://foo.bar.com/fonts/true'type/fubar.ttf)";
TokenShim[] expected = new TokenShim[]{
new TokenShim(Token.INVALID, "http://foo.bar.com/fonts/true", 1, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
@Test
public void testScanQuotedUrlWithSyntaxError() {
String str = "url('http://foo.bar.com/fonts/true\rtype/fubar.ttf')";
TokenShim[] expected = new TokenShim[]{
new TokenShim(Token.INVALID, "http://foo.bar.com/fonts/true", 2, 0),
TokenShim.EOF_TOKEN
};
List<TokenShim> tlist = getTokens(str);
checkTokens(tlist, expected);
for(int n=0; n<tlist.size(); n++) {
TokenShim tok = tlist.get(n);
assertEquals("bad line. tok="+tok, expected[n].getLine(), tok.getLine());
assertEquals("bad offset. tok="+tok, expected[n].getOffset(), tok.getOffset());
}
}
}
