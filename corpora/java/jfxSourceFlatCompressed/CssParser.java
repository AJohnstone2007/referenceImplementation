package javafx.css;
import com.sun.javafx.css.Combinator;
import com.sun.javafx.css.FontFaceImpl;
import com.sun.javafx.css.ParsedValueImpl;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.util.Utils;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.DurationConverter;
import javafx.css.converter.EffectConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.FontConverter;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.SizeConverter.SequenceConverter;
import javafx.css.converter.StringConverter;
import javafx.css.converter.URLConverter;
import javafx.css.converter.DeriveColorConverter;
import javafx.css.converter.LadderConverter;
import javafx.css.converter.StopConverter;
import com.sun.javafx.css.parser.Token;
import com.sun.javafx.scene.layout.region.BackgroundPositionConverter;
import com.sun.javafx.scene.layout.region.BackgroundSizeConverter;
import com.sun.javafx.scene.layout.region.BorderImageSliceConverter;
import com.sun.javafx.scene.layout.region.BorderImageSlices;
import com.sun.javafx.scene.layout.region.BorderImageWidthConverter;
import com.sun.javafx.scene.layout.region.BorderImageWidthsSequenceConverter;
import com.sun.javafx.scene.layout.region.BorderStrokeStyleSequenceConverter;
import com.sun.javafx.scene.layout.region.BorderStyleConverter;
import com.sun.javafx.scene.layout.region.CornerRadiiConverter;
import com.sun.javafx.scene.layout.region.LayeredBackgroundPositionConverter;
import com.sun.javafx.scene.layout.region.LayeredBackgroundSizeConverter;
import com.sun.javafx.scene.layout.region.LayeredBorderPaintConverter;
import com.sun.javafx.scene.layout.region.LayeredBorderStyleConverter;
import com.sun.javafx.scene.layout.region.Margins;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import com.sun.javafx.scene.layout.region.RepeatStructConverter;
import com.sun.javafx.scene.layout.region.SliceSequenceConverter;
import com.sun.javafx.scene.layout.region.StrokeBorderPaintConverter;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.Effect;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
final public class CssParser {
public CssParser() {
properties = new HashMap<String,String>();
}
private String stylesheetAsText;
private String sourceOfStylesheet;
private Styleable sourceOfInlineStyle;
private void setInputSource(String url, String str) {
stylesheetAsText = str;
sourceOfStylesheet = url;
sourceOfInlineStyle = null;
}
private void setInputSource(String str) {
stylesheetAsText = str;
sourceOfStylesheet = null;
sourceOfInlineStyle = null;
}
private void setInputSource(Styleable styleable) {
stylesheetAsText = styleable != null ? styleable.getStyle() : null;
sourceOfStylesheet = null;
sourceOfInlineStyle = styleable;
}
private static final PlatformLogger LOGGER = com.sun.javafx.util.Logging.getCSSLogger();
private static final class ParseException extends Exception {
ParseException(String message) {
this(message,null,null);
}
ParseException(String message, Token tok, CssParser parser) {
super(message);
this.tok = tok;
if (parser.sourceOfStylesheet != null) {
source = parser.sourceOfStylesheet;
} else if (parser.sourceOfInlineStyle != null) {
source = parser.sourceOfInlineStyle.toString();
} else if (parser.stylesheetAsText != null) {
source = parser.stylesheetAsText;
} else {
source = "?";
}
}
@Override public String toString() {
StringBuilder builder = new StringBuilder(super.getMessage());
builder.append(source);
if (tok != null) builder.append(": ").append(tok.toString());
return builder.toString();
}
private final Token tok;
private final String source;
}
public Stylesheet parse(final String stylesheetText) {
final Stylesheet stylesheet = new Stylesheet();
if (stylesheetText != null && !stylesheetText.trim().isEmpty()) {
setInputSource(stylesheetText);
try (Reader reader = new CharArrayReader(stylesheetText.toCharArray())) {
parse(stylesheet, reader);
} catch (IOException ioe) {
}
}
return stylesheet;
}
public Stylesheet parse(final String docbase, final String stylesheetText) throws IOException {
final Stylesheet stylesheet = new Stylesheet(docbase);
if (stylesheetText != null && !stylesheetText.trim().isEmpty()) {
setInputSource(docbase, stylesheetText);
try (Reader reader = new CharArrayReader(stylesheetText.toCharArray())) {
parse(stylesheet, reader);
}
}
return stylesheet;
}
public Stylesheet parse(final URL url) throws IOException {
final String path = url != null ? url.toExternalForm() : null;
final Stylesheet stylesheet = new Stylesheet(path);
if (url != null) {
setInputSource(path, null);
try (Reader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
parse(stylesheet, reader);
}
}
return stylesheet;
}
private void parse(final Stylesheet stylesheet, final Reader reader) {
CssLexer lex = new CssLexer();
lex.setReader(reader);
try {
this.parse(stylesheet, lex);
} catch (Exception ex) {
reportException(ex);
}
}
public Stylesheet parseInlineStyle(final Styleable node) {
Stylesheet stylesheet = new Stylesheet();
final String stylesheetText = (node != null) ? node.getStyle() : null;
if (stylesheetText != null && !stylesheetText.trim().isEmpty()) {
setInputSource(node);
final List<Rule> rules = new ArrayList<Rule>();
try (Reader reader = new CharArrayReader(stylesheetText.toCharArray())) {
final CssLexer lexer = new CssLexer();
lexer.setReader(reader);
currentToken = nextToken(lexer);
final List<Declaration> declarations = declarations(lexer);
if (declarations != null && !declarations.isEmpty()) {
final Selector selector = Selector.getUniversalSelector();
final Rule rule = new Rule(
Collections.singletonList(selector),
declarations
);
rules.add(rule);
}
} catch (IOException ioe) {
} catch (Exception ex) {
reportException(ex);
}
stylesheet.getRules().addAll(rules);
}
setInputSource((Styleable) null);
return stylesheet;
}
ParsedValue parseExpr(String property, String expr) {
if (property == null || expr == null) return null;
ParsedValueImpl value = null;
setInputSource(null, property + ": " + expr);
char buf[] = new char[expr.length() + 1];
System.arraycopy(expr.toCharArray(), 0, buf, 0, expr.length());
buf[buf.length-1] = ';';
try (Reader reader = new CharArrayReader(buf)) {
CssLexer lex = new CssLexer();
lex.setReader(reader);
currentToken = nextToken(lex);
CssParser.Term term = this.expr(lex);
value = valueFor(property, term, lex);
} catch (IOException ioe) {
} catch (ParseException e) {
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning("\"" +property + ": " + expr + "\" " + e.toString());
}
} catch (Exception ex) {
reportException(ex);
}
return value;
}
private final Map<String,String> properties;
static class Term {
final Token token;
Term nextInSeries;
Term nextLayer;
Term firstArg;
Term nextArg;
Term(Token token) {
this.token = token;
this.nextLayer = null;
this.nextInSeries = null;
this.firstArg = null;
this.nextArg = null;
}
Term() {
this(null);
}
@Override public String toString() {
StringBuilder buf = new StringBuilder();
if (token != null) buf.append(String.valueOf(token.getText()));
if (nextInSeries != null) {
buf.append("<nextInSeries>");
buf.append(nextInSeries.toString());
buf.append("</nextInSeries>\n");
}
if (nextLayer != null) {
buf.append("<nextLayer>");
buf.append(nextLayer.toString());
buf.append("</nextLayer>\n");
}
if (firstArg != null) {
buf.append("<args>");
buf.append(firstArg.toString());
if (nextArg != null) {
buf.append(nextArg.toString());
}
buf.append("</args>");
}
return buf.toString();
}
}
private ParseError createError(String msg) {
ParseError error = null;
if (sourceOfStylesheet != null) {
error = new ParseError.StylesheetParsingError(sourceOfStylesheet, msg);
} else if (sourceOfInlineStyle != null) {
error = new ParseError.InlineStyleParsingError(sourceOfInlineStyle, msg);
} else {
error = new ParseError.StringParsingError(stylesheetAsText, msg);
}
return error;
}
private void reportError(ParseError error) {
List<ParseError> errors = null;
if ((errors = StyleManager.getErrors()) != null) {
errors.add(error);
}
}
private void error(final Term root, final String msg) throws ParseException {
final Token token = root != null ? root.token : null;
final ParseException pe = new ParseException(msg,token,this);
reportError(createError(pe.toString()));
throw pe;
}
private void reportException(Exception exception) {
if (LOGGER.isLoggable(Level.WARNING)) {
final StackTraceElement[] stea = exception.getStackTrace();
if (stea.length > 0) {
final StringBuilder buf =
new StringBuilder("Please report ");
buf.append(exception.getClass().getName())
.append(" at:");
int end = 0;
while(end < stea.length) {
if (!getClass().getName().equals(stea[end].getClassName())) {
break;
}
buf.append("\n\t")
.append(stea[end++].toString());
}
LOGGER.warning(buf.toString());
}
}
}
private String formatDeprecatedMessage(final Term root, final String syntax) {
final StringBuilder buf =
new StringBuilder("Using deprecated syntax for ");
buf.append(syntax);
if (sourceOfStylesheet != null){
buf.append(" at ")
.append(sourceOfStylesheet)
.append("[")
.append(root.token.getLine())
.append(',')
.append(root.token.getOffset())
.append("]");
}
buf.append(". Refer to the CSS Reference Guide.");
return buf.toString();
}
private ParsedValueImpl<Color,Color> colorValueOfString(String str) {
if(str.startsWith("#") || str.startsWith("0x")) {
double a = 1.0f;
String c = str;
final int prefixLength = (str.startsWith("#")) ? 1 : 2;
final int len = c.length();
if ( (len-prefixLength) == 4) {
a = Integer.parseInt(c.substring(len-1), 16) / 15.0f;
c = c.substring(0,len-1);
} else if ((len-prefixLength) == 8) {
a = Integer.parseInt(c.substring(len-2), 16) / 255.0f;
c = c.substring(0,len-2);
}
return new ParsedValueImpl<Color,Color>(Color.web(c,a), null);
}
try {
return new ParsedValueImpl<Color,Color>(Color.web(str), null);
} catch (final IllegalArgumentException e) {
} catch (final NullPointerException e) {
}
return null;
}
private String stripQuotes(String string) {
return com.sun.javafx.util.Utils.stripQuotes(string);
}
private double clamp(double min, double val, double max) {
if (val < min) return min;
if (max < val) return max;
return val;
}
private boolean isSize(Token token) {
final int ttype = token.getType();
switch (ttype) {
case CssLexer.NUMBER:
case CssLexer.PERCENTAGE:
case CssLexer.EMS:
case CssLexer.EXS:
case CssLexer.PX:
case CssLexer.CM:
case CssLexer.MM:
case CssLexer.IN:
case CssLexer.PT:
case CssLexer.PC:
case CssLexer.DEG:
case CssLexer.GRAD:
case CssLexer.RAD:
case CssLexer.TURN:
return true;
default:
return token.getType() == CssLexer.IDENT;
}
}
private Size size(final Token token) throws ParseException {
SizeUnits units = SizeUnits.PX;
int trim = 2;
final String sval = token.getText().trim();
final int len = sval.length();
final int ttype = token.getType();
switch (ttype) {
case CssLexer.NUMBER:
units = SizeUnits.PX;
trim = 0;
break;
case CssLexer.PERCENTAGE:
units = SizeUnits.PERCENT;
trim = 1;
break;
case CssLexer.EMS:
units = SizeUnits.EM;
break;
case CssLexer.EXS:
units = SizeUnits.EX;
break;
case CssLexer.PX:
units = SizeUnits.PX;
break;
case CssLexer.CM:
units = SizeUnits.CM;
break;
case CssLexer.MM:
units = SizeUnits.MM;
break;
case CssLexer.IN:
units = SizeUnits.IN;
break;
case CssLexer.PT:
units = SizeUnits.PT;
break;
case CssLexer.PC:
units = SizeUnits.PC;
break;
case CssLexer.DEG:
units = SizeUnits.DEG;
trim = 3;
break;
case CssLexer.GRAD:
units = SizeUnits.GRAD;
trim = 4;
break;
case CssLexer.RAD:
units = SizeUnits.RAD;
trim = 3;
break;
case CssLexer.TURN:
units = SizeUnits.TURN;
trim = 4;
break;
case CssLexer.SECONDS:
units = SizeUnits.S;
trim = 1;
break;
case CssLexer.MS:
units = SizeUnits.MS;
break;
default:
if (LOGGER.isLoggable(Level.FINEST)) {
LOGGER.finest("Expected \'<number>\'");
}
ParseException re = new ParseException("Expected \'<number>\'",token, this);
reportError(createError(re.toString()));
throw re;
}
return new Size(
Double.parseDouble(sval.substring(0,len-trim)),
units
);
}
private int numberOfTerms(final Term root) {
if (root == null) return 0;
int nTerms = 0;
Term term = root;
do {
nTerms += 1;
term = term.nextInSeries;
} while (term != null);
return nTerms;
}
private int numberOfLayers(final Term root) {
if (root == null) return 0;
int nLayers = 0;
Term term = root;
do {
nLayers += 1;
while (term.nextInSeries != null) {
term = term.nextInSeries;
}
term = term.nextLayer;
} while (term != null);
return nLayers;
}
private int numberOfArgs(final Term root) {
if (root == null) return 0;
int nArgs = 0;
Term term = root.firstArg;
while (term != null) {
nArgs += 1;
term = term.nextArg;
}
return nArgs;
}
private Term nextLayer(final Term root) {
if (root == null) return null;
Term term = root;
while (term.nextInSeries != null) {
term = term.nextInSeries;
}
return term.nextLayer;
}
ParsedValueImpl valueFor(String property, Term root, CssLexer lexer) throws ParseException {
final String prop = property.toLowerCase(Locale.ROOT);
properties.put(prop, prop);
if (root == null || root.token == null) {
error(root, "Expected value for property \'" + prop + "\'");
}
if (root.token.getType() == CssLexer.IDENT) {
final String txt = root.token.getText();
if ("inherit".equalsIgnoreCase(txt)) {
return new ParsedValueImpl<String,String>("inherit", null);
} else if ("null".equalsIgnoreCase(txt)
|| "none".equalsIgnoreCase(txt)) {
return new ParsedValueImpl<String,String>("null", null);
}
}
if ("-fx-fill".equals(prop)) {
ParsedValueImpl pv = parse(root);
if (pv.getConverter() == StyleConverter.getUrlConverter()) {
pv = new ParsedValueImpl(new ParsedValue[] {pv},PaintConverter.ImagePatternConverter.getInstance());
}
return pv;
}
else if ("-fx-background-color".equals(prop)) {
return parsePaintLayers(root);
} else if ("-fx-background-image".equals(prop)) {
return parseURILayers(root);
} else if ("-fx-background-insets".equals(prop)) {
return parseInsetsLayers(root);
} else if ("-fx-opaque-insets".equals(prop)) {
return parseInsetsLayer(root);
} else if ("-fx-background-position".equals(prop)) {
return parseBackgroundPositionLayers(root);
} else if ("-fx-background-radius".equals(prop)) {
return parseCornerRadius(root);
} else if ("-fx-background-repeat".equals(prop)) {
return parseBackgroundRepeatStyleLayers(root);
} else if ("-fx-background-size".equals(prop)) {
return parseBackgroundSizeLayers(root);
} else if ("-fx-border-color".equals(prop)) {
return parseBorderPaintLayers(root);
} else if ("-fx-border-insets".equals(prop)) {
return parseInsetsLayers(root);
} else if ("-fx-border-radius".equals(prop)) {
return parseCornerRadius(root);
} else if ("-fx-border-style".equals(prop)) {
return parseBorderStyleLayers(root);
} else if ("-fx-border-width".equals(prop)) {
return parseMarginsLayers(root);
} else if ("-fx-border-image-insets".equals(prop)) {
return parseInsetsLayers(root);
} else if ("-fx-border-image-repeat".equals(prop)) {
return parseBorderImageRepeatStyleLayers(root);
} else if ("-fx-border-image-slice".equals(prop)) {
return parseBorderImageSliceLayers(root);
} else if ("-fx-border-image-source".equals(prop)) {
return parseURILayers(root);
} else if ("-fx-border-image-width".equals(prop)) {
return parseBorderImageWidthLayers(root);
} else if ("-fx-padding".equals(prop)) {
ParsedValueImpl<?,Size>[] sides = parseSize1to4(root);
return new ParsedValueImpl<ParsedValue[],Insets>(sides, InsetsConverter.getInstance());
} else if ("-fx-label-padding".equals(prop)) {
ParsedValueImpl<?,Size>[] sides = parseSize1to4(root);
return new ParsedValueImpl<ParsedValue[],Insets>(sides, InsetsConverter.getInstance());
} else if (prop.endsWith("font-family")) {
return parseFontFamily(root);
} else if (prop.endsWith("font-size")) {
ParsedValueImpl fsize = parseFontSize(root);
if (fsize == null) error(root, "Expected \'<font-size>\'");
return fsize;
} else if (prop.endsWith("font-style")) {
ParsedValueImpl fstyle = parseFontStyle(root);
if (fstyle == null) error(root, "Expected \'<font-style>\'");
return fstyle;
} else if (prop.endsWith("font-weight")) {
ParsedValueImpl fweight = parseFontWeight(root);
if (fweight == null) error(root, "Expected \'<font-style>\'");
return fweight;
} else if (prop.endsWith("font")) {
return parseFont(root);
} else if ("-fx-stroke-dash-array".equals(prop)) {
Term term = root;
int nArgs = numberOfTerms(term);
ParsedValueImpl<?,Size>[] segments = new ParsedValueImpl[nArgs];
int segment = 0;
while(term != null) {
segments[segment++] = parseSize(term);
term = term.nextInSeries;
}
return new ParsedValueImpl<ParsedValue[],Number[]>(segments,SequenceConverter.getInstance());
} else if ("-fx-stroke-line-join".equals(prop)) {
ParsedValueImpl[] values = parseStrokeLineJoin(root);
if (values == null) error(root, "Expected \'miter', \'bevel\' or \'round\'");
return values[0];
} else if ("-fx-stroke-line-cap".equals(prop)) {
ParsedValueImpl value = parseStrokeLineCap(root);
if (value == null) error(root, "Expected \'square', \'butt\' or \'round\'");
return value;
} else if ("-fx-stroke-type".equals(prop)) {
ParsedValueImpl value = parseStrokeType(root);
if (value == null) error(root, "Expected \'centered', \'inside\' or \'outside\'");
return value;
} else if ("-fx-font-smoothing-type".equals(prop) || "-fx-blend-mode".equals(prop)) {
String str = null;
int ttype = -1;
final Token token = root.token;
if (root.token == null
|| ((ttype = root.token.getType()) != CssLexer.STRING
&& ttype != CssLexer.IDENT)
|| (str = root.token.getText()) == null
|| str.isEmpty()) {
error(root, "Expected STRING or IDENT");
}
return new ParsedValueImpl<String, String>(stripQuotes(str), null, false);
}
return parse(root);
}
private ParsedValueImpl parse(Term root) throws ParseException {
if (root.token == null) error(root, "Parse error");
final Token token = root.token;
ParsedValueImpl value = null;
final int ttype = token.getType();
switch (ttype) {
case CssLexer.NUMBER:
case CssLexer.PERCENTAGE:
case CssLexer.EMS:
case CssLexer.EXS:
case CssLexer.PX:
case CssLexer.CM:
case CssLexer.MM:
case CssLexer.IN:
case CssLexer.PT:
case CssLexer.PC:
case CssLexer.DEG:
case CssLexer.GRAD:
case CssLexer.RAD:
case CssLexer.TURN:
if (root.nextInSeries == null) {
ParsedValueImpl sizeValue = new ParsedValueImpl<Size,Number>(size(token), null);
value = new ParsedValueImpl<ParsedValue<?,Size>, Number>(sizeValue, SizeConverter.getInstance());
} else {
ParsedValueImpl<Size,Size>[] sizeValue = parseSizeSeries(root);
value = new ParsedValueImpl<ParsedValue[],Number[]>(sizeValue, SizeConverter.SequenceConverter.getInstance());
}
break;
case CssLexer.SECONDS:
case CssLexer.MS: {
ParsedValue<Size, Size> sizeValue = new ParsedValueImpl<Size, Size>(size(token), null);
value = new ParsedValueImpl<ParsedValue<?, Size>, Duration>(sizeValue, DurationConverter.getInstance());
break;
}
case CssLexer.STRING:
case CssLexer.IDENT:
boolean isIdent = ttype == CssLexer.IDENT;
final String str = stripQuotes(token.getText());
final String text = str.toLowerCase(Locale.ROOT);
if ("ladder".equals(text)) {
value = ladder(root);
} else if ("linear".equals(text) && (root.nextInSeries) != null) {
value = linearGradient(root);
} else if ("radial".equals(text) && (root.nextInSeries) != null) {
value = radialGradient(root);
} else if ("infinity".equals(text)) {
Size size = new Size(Double.MAX_VALUE, SizeUnits.PX);
ParsedValueImpl sizeValue = new ParsedValueImpl<Size,Number>(size, null);
value = new ParsedValueImpl<ParsedValue<?,Size>,Number>(sizeValue, SizeConverter.getInstance());
} else if ("indefinite".equals(text)) {
Size size = new Size(Double.POSITIVE_INFINITY, SizeUnits.PX);
ParsedValueImpl<Size,Size> sizeValue = new ParsedValueImpl<>(size, null);
value = new ParsedValueImpl<ParsedValue<?,Size>,Duration>(sizeValue, DurationConverter.getInstance());
} else if ("true".equals(text)) {
value = new ParsedValueImpl<String,Boolean>("true",BooleanConverter.getInstance());
} else if ("false".equals(text)) {
value = new ParsedValueImpl<String,Boolean>("false",BooleanConverter.getInstance());
} else {
boolean needsLookup = isIdent && properties.containsKey(text);
if (needsLookup || ((value = colorValueOfString(str)) == null )) {
value = new ParsedValueImpl<String,String>(needsLookup ? text : str, null, isIdent || needsLookup);
}
}
break;
case CssLexer.HASH:
final String clr = token.getText();
try {
value = new ParsedValueImpl<Color,Color>(Color.web(clr), null);
} catch (final IllegalArgumentException e) {
error(root, e.getMessage());
}
break;
case CssLexer.FUNCTION:
return parseFunction(root);
case CssLexer.URL:
return parseURI(root);
default:
final String msg = "Unknown token type: \'" + ttype + "\'";
error(root, msg);
}
return value;
}
private ParsedValueImpl<?,Size> parseSize(final Term root) throws ParseException {
if (root.token == null || !isSize(root.token)) error(root, "Expected \'<size>\'");
ParsedValueImpl<?,Size> value = null;
if (root.token.getType() != CssLexer.IDENT) {
Size size = size(root.token);
value = new ParsedValueImpl<Size,Size>(size, null);
} else {
String key = root.token.getText();
value = new ParsedValueImpl<String,Size>(key, null, true);
}
return value;
}
private ParsedValueImpl<?,Color> parseColor(final Term root) throws ParseException {
ParsedValueImpl<?,Color> color = null;
if (root.token != null &&
(root.token.getType() == CssLexer.IDENT ||
root.token.getType() == CssLexer.HASH ||
root.token.getType() == CssLexer.FUNCTION)) {
color = parse(root);
} else {
error(root, "Expected \'<color>\'");
}
return color;
}
private ParsedValueImpl rgb(Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (fn == null || !"rgb".regionMatches(true, 0, fn, 0, 3)) {
final String msg = "Expected \'rgb\' or \'rgba\'";
error(root, msg);
}
Term arg = root;
Token rtok, gtok, btok, atok;
if ((arg = arg.firstArg) == null) error(root, "Expected \'<number>\' or \'<percentage>\'");
if ((rtok = arg.token) == null ||
(rtok.getType() != CssLexer.NUMBER &&
rtok.getType() != CssLexer.PERCENTAGE)) error(arg, "Expected \'<number>\' or \'<percentage>\'");
root = arg;
if ((arg = arg.nextArg) == null) error(root, "Expected \'<number>\' or \'<percentage>\'");
if ((gtok = arg.token) == null ||
(gtok.getType() != CssLexer.NUMBER &&
gtok.getType() != CssLexer.PERCENTAGE)) error(arg, "Expected \'<number>\' or \'<percentage>\'");
root = arg;
if ((arg = arg.nextArg) == null) error(root, "Expected \'<number>\' or \'<percentage>\'");
if ((btok = arg.token) == null ||
(btok.getType() != CssLexer.NUMBER &&
btok.getType() != CssLexer.PERCENTAGE)) error(arg, "Expected \'<number>\' or \'<percentage>\'");
root = arg;
if ((arg = arg.nextArg) != null) {
if ((atok = arg.token) == null ||
atok.getType() != CssLexer.NUMBER) error(arg, "Expected \'<number>\'");
} else {
atok = null;
}
int argType = rtok.getType();
if (argType != gtok.getType() || argType != btok.getType() ||
(argType != CssLexer.NUMBER && argType != CssLexer.PERCENTAGE)) {
error(root, "Argument type mistmatch");
}
final String rtext = rtok.getText();
final String gtext = gtok.getText();
final String btext = btok.getText();
double rval = 0;
double gval = 0;
double bval = 0;
if (argType == CssLexer.NUMBER) {
rval = clamp(0.0f, Double.parseDouble(rtext) / 255.0f, 1.0f);
gval = clamp(0.0f, Double.parseDouble(gtext) / 255.0f, 1.0f);
bval = clamp(0.0f, Double.parseDouble(btext) / 255.0f, 1.0f);
} else {
rval = clamp(0.0f, Double.parseDouble(rtext.substring(0,rtext.length()-1)) / 100.0f, 1.0f);
gval = clamp(0.0f, Double.parseDouble(gtext.substring(0,gtext.length()-1)) / 100.0f, 1.0f);
bval = clamp(0.0f, Double.parseDouble(btext.substring(0,btext.length()-1)) / 100.0f, 1.0f);
}
final String atext = (atok != null) ? atok.getText() : null;
final double aval = (atext != null) ? clamp(0.0f, Double.parseDouble(atext), 1.0f) : 1.0;
return new ParsedValueImpl<Color,Color>(Color.color(rval,gval,bval,aval), null);
}
private ParsedValueImpl hsb(Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (fn == null || !"hsb".regionMatches(true, 0, fn, 0, 3)) {
final String msg = "Expected \'hsb\' or \'hsba\'";
error(root, msg);
}
Term arg = root;
Token htok, stok, btok, atok;
if ((arg = arg.firstArg) == null) error(root, "Expected \'<number>\'");
if ((htok = arg.token) == null || htok.getType() != CssLexer.NUMBER) error(arg, "Expected \'<number>\'");
root = arg;
if ((arg = arg.nextArg) == null) error(root, "Expected \'<percent>\'");
if ((stok = arg.token) == null || stok.getType() != CssLexer.PERCENTAGE) error(arg, "Expected \'<percent>\'");
root = arg;
if ((arg = arg.nextArg) == null) error(root, "Expected \'<percent>\'");
if ((btok = arg.token) == null || btok.getType() != CssLexer.PERCENTAGE) error(arg, "Expected \'<percent>\'");
root = arg;
if ((arg = arg.nextArg) != null) {
if ((atok = arg.token) == null || atok.getType() != CssLexer.NUMBER) error(arg, "Expected \'<number>\'");
} else {
atok = null;
}
final Size hval = size(htok);
final Size sval = size(stok);
final Size bval = size(btok);
final double hue = hval.pixels();
final double saturation = clamp(0.0f, sval.pixels(), 1.0f);
final double brightness = clamp(0.0f, bval.pixels(), 1.0f);
final Size aval = (atok != null) ? size(atok) : null;
final double opacity = (aval != null) ? clamp(0.0f, aval.pixels(), 1.0f) : 1.0;
return new ParsedValueImpl<Color,Color>(Color.hsb(hue, saturation, brightness, opacity), null);
}
private ParsedValueImpl<ParsedValue[],Color> derive(final Term root)
throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (fn == null || !"derive".regionMatches(true, 0, fn, 0, 6)) {
final String msg = "Expected \'derive\'";
error(root, msg);
}
Term arg = root;
if ((arg = arg.firstArg) == null) error(root, "Expected \'<color>\'");
final ParsedValueImpl<?,Color> color = parseColor(arg);
final Term prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<percent\'");
final ParsedValueImpl<?,Size> brightness = parseSize(arg);
ParsedValueImpl[] values = new ParsedValueImpl[] { color, brightness };
return new ParsedValueImpl<ParsedValue[],Color>(values, DeriveColorConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[],Color> ladder(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (fn == null || !"ladder".regionMatches(true, 0, fn, 0, 6)) {
final String msg = "Expected \'ladder\'";
error(root, msg);
}
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(formatDeprecatedMessage(root, "ladder"));
}
Term term = root;
if ((term = term.nextInSeries) == null) error(root, "Expected \'<color>\'");
final ParsedValueImpl<?,Color> color = parse(term);
Term prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'stops\'");
if (term.token == null ||
term.token.getType() != CssLexer.IDENT ||
!"stops".equalsIgnoreCase(term.token.getText())) error(term, "Expected \'stops\'");
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'(<number>, <color>)\'");
int nStops = 0;
Term temp = term;
do {
nStops += 1;
} while (((temp = temp.nextInSeries) != null) &&
((temp.token != null) && (temp.token.getType() == CssLexer.LPAREN)));
ParsedValueImpl[] values = new ParsedValueImpl[nStops+1];
values[0] = color;
int stopIndex = 1;
do {
ParsedValueImpl<ParsedValue[],Stop> stop = stop(term);
if (stop != null) values[stopIndex++] = stop;
prev = term;
} while(((term = term.nextInSeries) != null) &&
(term.token.getType() == CssLexer.LPAREN));
if (term != null) {
root.nextInSeries = term;
}
else {
root.nextInSeries = null;
root.nextLayer = prev.nextLayer;
}
return new ParsedValueImpl<ParsedValue[], Color>(values, LadderConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[],Color> parseLadder(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (fn == null || !"ladder".regionMatches(true, 0, fn, 0, 6)) {
final String msg = "Expected \'ladder\'";
error(root, msg);
}
Term term = root;
if ((term = term.firstArg) == null) error(root, "Expected \'<color>\'");
final ParsedValueImpl<?,Color> color = parse(term);
Term prev = term;
if ((term = term.nextArg) == null)
error(prev, "Expected \'<color-stop>[, <color-stop>]+\'");
ParsedValueImpl<ParsedValue[],Stop>[] stops = parseColorStops(term);
ParsedValueImpl[] values = new ParsedValueImpl[stops.length+1];
values[0] = color;
System.arraycopy(stops, 0, values, 1, stops.length);
return new ParsedValueImpl<ParsedValue[], Color>(values, LadderConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[], Stop> stop(final Term root)
throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (fn == null || !"(".equals(fn)) {
final String msg = "Expected \'(\'";
error(root, msg);
}
Term arg = null;
if ((arg = root.firstArg) == null) error(root, "Expected \'<number>\'");
ParsedValueImpl<?,Size> size = parseSize(arg);
Term prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<color>\'");
ParsedValueImpl<?,Color> color = parseColor(arg);
ParsedValueImpl[] values = new ParsedValueImpl[] { size, color };
return new ParsedValueImpl<ParsedValue[],Stop>(values, StopConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[], Stop>[] parseColorStops(final Term root)
throws ParseException {
int nArgs = 1;
Term temp = root;
while(temp != null) {
if (temp.nextArg != null) {
nArgs += 1;
temp = temp.nextArg;
} else if (temp.nextInSeries != null) {
temp = temp.nextInSeries;
} else {
break;
}
}
if (nArgs < 2) {
error(root, "Expected \'<color-stop>\'");
}
ParsedValueImpl<?,Color>[] colors = new ParsedValueImpl[nArgs];
Size[] positions = new Size[nArgs];
java.util.Arrays.fill(positions, null);
Term stop = root;
Term prev = root;
SizeUnits units = null;
for (int n = 0; n<nArgs; n++) {
colors[n] = parseColor(stop);
prev = stop;
Term term = stop.nextInSeries;
if (term != null) {
if (isSize(term.token)) {
positions[n] = size(term.token);
if (units != null) {
if (units != positions[n].getUnits()) {
error(term, "Parser unable to handle mixed \'<percent>\' and \'<length>\'");
}
}
} else {
error(prev, "Expected \'<percent>\' or \'<length>\'");
}
prev = term;
stop = term.nextArg;
} else {
prev = stop;
stop = stop.nextArg;
}
}
if (positions[0] == null) positions[0] = new Size(0, SizeUnits.PERCENT);
if (positions[nArgs-1] == null) positions[nArgs-1] = new Size(100, SizeUnits.PERCENT);
Size max = null;
for (int n = 1 ; n<nArgs; n++) {
Size pos0 = positions[n-1];
if (pos0 == null) continue;
if (max == null || max.getValue() < pos0.getValue()) {
max = pos0;
}
Size pos1 = positions[n];
if (pos1 == null) continue;
if (pos1.getValue() < max.getValue()) positions[n] = max;
}
Size preceding = null;
int withoutIndex = -1;
for (int n = 0 ; n<nArgs; n++) {
Size pos = positions[n];
if (pos == null) {
if (withoutIndex == -1) withoutIndex = n;
} else {
if (withoutIndex > -1) {
int nWithout = n - withoutIndex;
double precedingValue = preceding.getValue();
double delta =
(pos.getValue() - precedingValue) / (nWithout + 1);
while(withoutIndex < n) {
precedingValue += delta;
positions[withoutIndex++] =
new Size(precedingValue, pos.getUnits());
}
withoutIndex = -1;
preceding = pos;
} else {
preceding = pos;
}
}
}
ParsedValueImpl<ParsedValue[],Stop>[] stops = new ParsedValueImpl[nArgs];
for (int n=0; n<nArgs; n++) {
stops[n] = new ParsedValueImpl<ParsedValue[],Stop>(
new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(positions[n], null),
colors[n]
},
StopConverter.getInstance()
);
}
return stops;
}
private ParsedValueImpl[] point(final Term root) throws ParseException {
if (root.token == null ||
root.token.getType() != CssLexer.LPAREN) error(root, "Expected \'(<number>, <number>)\'");
final String fn = root.token.getText();
if (fn == null || !"(".equalsIgnoreCase(fn)) {
final String msg = "Expected \'(\'";
error(root, msg);
}
Term arg = null;
if ((arg = root.firstArg) == null) error(root, "Expected \'<number>\'");
final ParsedValueImpl<?,Size> ptX = parseSize(arg);
final Term prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
final ParsedValueImpl<?,Size> ptY = parseSize(arg);
return new ParsedValueImpl[] { ptX, ptY };
}
private ParsedValueImpl parseFunction(final Term root) throws ParseException {
final String fcn = (root.token != null) ? root.token.getText() : null;
if (fcn == null) {
error(root, "Expected function name");
} else if ("rgb".regionMatches(true, 0, fcn, 0, 3)) {
return rgb(root);
} else if ("hsb".regionMatches(true, 0, fcn, 0, 3)) {
return hsb(root);
} else if ("derive".regionMatches(true, 0, fcn, 0, 6)) {
return derive(root);
} else if ("innershadow".regionMatches(true, 0, fcn, 0, 11)) {
return innershadow(root);
} else if ("dropshadow".regionMatches(true, 0, fcn, 0, 10)) {
return dropshadow(root);
} else if ("linear-gradient".regionMatches(true, 0, fcn, 0, 15)) {
return parseLinearGradient(root);
} else if ("radial-gradient".regionMatches(true, 0, fcn, 0, 15)) {
return parseRadialGradient(root);
} else if ("image-pattern".regionMatches(true, 0, fcn, 0, 13)) {
return parseImagePattern(root);
} else if ("repeating-image-pattern".regionMatches(true, 0, fcn, 0, 23)) {
return parseRepeatingImagePattern(root);
} else if ("ladder".regionMatches(true, 0, fcn, 0, 6)) {
return parseLadder(root);
} else if ("region".regionMatches(true, 0, fcn, 0, 6)) {
return parseRegion(root);
} else {
error(root, "Unexpected function \'" + fcn + "\'");
}
return null;
}
private ParsedValueImpl<String,BlurType> blurType(final Term root) throws ParseException {
if (root == null) return null;
if (root.token == null ||
root.token.getType() != CssLexer.IDENT ||
root.token.getText() == null ||
root.token.getText().isEmpty()) {
final String msg = "Expected \'gaussian\', \'one-pass-box\', \'two-pass-box\', or \'three-pass-box\'";
error(root, msg);
}
final String blurStr = root.token.getText().toLowerCase(Locale.ROOT);
BlurType blurType = BlurType.THREE_PASS_BOX;
if ("gaussian".equals(blurStr)) {
blurType = BlurType.GAUSSIAN;
} else if ("one-pass-box".equals(blurStr)) {
blurType = BlurType.ONE_PASS_BOX;
} else if ("two-pass-box".equals(blurStr)) {
blurType = BlurType.TWO_PASS_BOX;
} else if ("three-pass-box".equals(blurStr)) {
blurType = BlurType.THREE_PASS_BOX;
} else {
final String msg = "Expected \'gaussian\', \'one-pass-box\', \'two-pass-box\', or \'three-pass-box\'";
error(root, msg);
}
return new ParsedValueImpl<String,BlurType>(blurType.name(), new EnumConverter<BlurType>(BlurType.class));
}
private ParsedValueImpl innershadow(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (!"innershadow".regionMatches(true, 0, fn, 0, 11)) {
final String msg = "Expected \'innershadow\'";
error(root, msg);
}
Term arg;
if ((arg = root.firstArg) == null) error(root, "Expected \'<blur-type>\'");
ParsedValueImpl<String,BlurType> blurVal = blurType(arg);
Term prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<color>\'");
ParsedValueImpl<?,Color> colorVal = parseColor(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
ParsedValueImpl<?,Size> radiusVal = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
ParsedValueImpl<?,Size> chokeVal = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
ParsedValueImpl<?,Size> offsetXVal = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
ParsedValueImpl<?,Size> offsetYVal = parseSize(arg);
ParsedValueImpl[] values = new ParsedValueImpl[] {
blurVal,
colorVal,
radiusVal,
chokeVal,
offsetXVal,
offsetYVal
};
return new ParsedValueImpl<ParsedValue[],Effect>(values, EffectConverter.InnerShadowConverter.getInstance());
}
private ParsedValueImpl dropshadow(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (!"dropshadow".regionMatches(true, 0, fn, 0, 10)) {
final String msg = "Expected \'dropshadow\'";
error(root, msg);
}
Term arg;
if ((arg = root.firstArg) == null) error(root, "Expected \'<blur-type>\'");
ParsedValueImpl<String,BlurType> blurVal = blurType(arg);
Term prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<color>\'");
ParsedValueImpl<?,Color> colorVal = parseColor(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
ParsedValueImpl<?,Size> radiusVal = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
ParsedValueImpl<?,Size> spreadVal = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
ParsedValueImpl<?,Size> offsetXVal = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<number>\'");
ParsedValueImpl<?,Size> offsetYVal = parseSize(arg);
ParsedValueImpl[] values = new ParsedValueImpl[] {
blurVal,
colorVal,
radiusVal,
spreadVal,
offsetXVal,
offsetYVal
};
return new ParsedValueImpl<ParsedValue[],Effect>(values, EffectConverter.DropShadowConverter.getInstance());
}
private ParsedValueImpl<String, CycleMethod> cycleMethod(final Term root) {
CycleMethod cycleMethod = null;
if (root != null && root.token.getType() == CssLexer.IDENT) {
final String text = root.token.getText().toLowerCase(Locale.ROOT);
if ("repeat".equals(text)) {
cycleMethod = CycleMethod.REPEAT;
} else if ("reflect".equals(text)) {
cycleMethod = CycleMethod.REFLECT;
} else if ("no-cycle".equals(text)) {
cycleMethod = CycleMethod.NO_CYCLE;
}
}
if (cycleMethod != null)
return new ParsedValueImpl<String,CycleMethod>(cycleMethod.name(), new EnumConverter<CycleMethod>(CycleMethod.class));
else
return null;
}
private ParsedValueImpl<ParsedValue[],Paint> linearGradient(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (fn == null || !"linear".equalsIgnoreCase(fn)) {
final String msg = "Expected \'linear\'";
error(root, msg);
}
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(formatDeprecatedMessage(root, "linear gradient"));
}
Term term = root;
if ((term = term.nextInSeries) == null) error(root, "Expected \'(<number>, <number>)\'");
final ParsedValueImpl<?,Size>[] startPt = point(term);
Term prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'to\'");
if (term.token == null ||
term.token.getType() != CssLexer.IDENT ||
!"to".equalsIgnoreCase(term.token.getText())) error(root, "Expected \'to\'");
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'(<number>, <number>)\'");
final ParsedValueImpl<?,Size>[] endPt = point(term);
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'stops\'");
if (term.token == null ||
term.token.getType() != CssLexer.IDENT ||
!"stops".equalsIgnoreCase(term.token.getText())) error(term, "Expected \'stops\'");
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'(<number>, <number>)\'");
int nStops = 0;
Term temp = term;
do {
nStops += 1;
} while (((temp = temp.nextInSeries) != null) &&
((temp.token != null) && (temp.token.getType() == CssLexer.LPAREN)));
ParsedValueImpl[] stops = new ParsedValueImpl[nStops];
int stopIndex = 0;
do {
ParsedValueImpl<ParsedValue[],Stop> stop = stop(term);
if (stop != null) stops[stopIndex++] = stop;
prev = term;
} while(((term = term.nextInSeries) != null) &&
(term.token.getType() == CssLexer.LPAREN));
ParsedValueImpl<String,CycleMethod> cycleMethod = cycleMethod(term);
if (cycleMethod == null) {
cycleMethod = new ParsedValueImpl<String,CycleMethod>(CycleMethod.NO_CYCLE.name(), new EnumConverter<CycleMethod>(CycleMethod.class));
if (term != null) {
root.nextInSeries = term;
}
else {
root.nextInSeries = null;
root.nextLayer = prev.nextLayer;
}
} else {
root.nextInSeries = term.nextInSeries;
root.nextLayer = term.nextLayer;
}
ParsedValueImpl[] values = new ParsedValueImpl[5 + stops.length];
int index = 0;
values[index++] = (startPt != null) ? startPt[0] : null;
values[index++] = (startPt != null) ? startPt[1] : null;
values[index++] = (endPt != null) ? endPt[0] : null;
values[index++] = (endPt != null) ? endPt[1] : null;
values[index++] = cycleMethod;
for (int n=0; n<stops.length; n++) values[index++] = stops[n];
return new ParsedValueImpl<ParsedValue[], Paint>(values, PaintConverter.LinearGradientConverter.getInstance());
}
private ParsedValueImpl parseLinearGradient(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (!"linear-gradient".regionMatches(true, 0, fn, 0, 15)) {
final String msg = "Expected \'linear-gradient\'";
error(root, msg);
}
Term arg;
if ((arg = root.firstArg) == null ||
arg.token == null ||
arg.token.getText().isEmpty()) {
error(root,
"Expected \'from <point> to <point>\' or \'to <side-or-corner>\' " +
"or \'<cycle-method>\' or \'<color-stop>\'");
}
Term prev = arg;
ParsedValueImpl<?,Size>[] startPt = null;
ParsedValueImpl<?,Size>[] endPt = null;
if ("from".equalsIgnoreCase(arg.token.getText())) {
prev = arg;
if ((arg = arg.nextInSeries) == null) error(prev, "Expected \'<point>\'");
ParsedValueImpl<?,Size> ptX = parseSize(arg);
prev = arg;
if ((arg = arg.nextInSeries) == null) error(prev, "Expected \'<point>\'");
ParsedValueImpl<?,Size> ptY = parseSize(arg);
startPt = new ParsedValueImpl[] { ptX, ptY };
prev = arg;
if ((arg = arg.nextInSeries) == null) error(prev, "Expected \'to\'");
if (arg.token == null ||
arg.token.getType() != CssLexer.IDENT ||
!"to".equalsIgnoreCase(arg.token.getText())) error(prev, "Expected \'to\'");
prev = arg;
if ((arg = arg.nextInSeries) == null) error(prev, "Expected \'<point>\'");
ptX = parseSize(arg);
prev = arg;
if ((arg = arg.nextInSeries) == null) error(prev, "Expected \'<point>\'");
ptY = parseSize(arg);
endPt = new ParsedValueImpl[] { ptX, ptY };
prev = arg;
arg = arg.nextArg;
} else if("to".equalsIgnoreCase(arg.token.getText())) {
prev = arg;
if ((arg = arg.nextInSeries) == null ||
arg.token == null ||
arg.token.getType() != CssLexer.IDENT ||
arg.token.getText().isEmpty()) {
error (prev, "Expected \'<side-or-corner>\'");
}
int startX = 0;
int startY = 0;
int endX = 0;
int endY = 0;
String sideOrCorner1 = arg.token.getText().toLowerCase(Locale.ROOT);
if ("top".equals(sideOrCorner1)) {
startY = 100;
endY = 0;
} else if ("bottom".equals(sideOrCorner1)) {
startY = 0;
endY = 100;
} else if ("right".equals(sideOrCorner1)) {
startX = 0;
endX = 100;
} else if ("left".equals(sideOrCorner1)) {
startX = 100;
endX = 0;
} else {
error(arg, "Invalid \'<side-or-corner>\'");
}
prev = arg;
if (arg.nextInSeries != null) {
arg = arg.nextInSeries;
if (arg.token != null &&
arg.token.getType() == CssLexer.IDENT &&
!arg.token.getText().isEmpty()) {
String sideOrCorner2 = arg.token.getText().toLowerCase(Locale.ROOT);
if ("right".equals(sideOrCorner2) &&
startX == 0 && endX == 0) {
startX = 0;
endX = 100;
} else if ("left".equals(sideOrCorner2) &&
startX == 0 && endX == 0) {
startX = 100;
endX = 0;
} else if("top".equals(sideOrCorner2) &&
startY == 0 && endY == 0) {
startY = 100;
endY = 0;
} else if ("bottom".equals(sideOrCorner2) &&
startY == 0 && endY == 0) {
startY = 0;
endY = 100;
} else {
error(arg, "Invalid \'<side-or-corner>\'");
}
} else {
error (prev, "Expected \'<side-or-corner>\'");
}
}
startPt = new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(startX, SizeUnits.PERCENT), null),
new ParsedValueImpl<Size,Size>(new Size(startY, SizeUnits.PERCENT), null)
};
endPt = new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(endX, SizeUnits.PERCENT), null),
new ParsedValueImpl<Size,Size>(new Size(endY, SizeUnits.PERCENT), null)
};
prev = arg;
arg = arg.nextArg;
}
if (startPt == null && endPt == null) {
startPt = new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(0, SizeUnits.PERCENT), null),
new ParsedValueImpl<Size,Size>(new Size(0, SizeUnits.PERCENT), null)
};
endPt = new ParsedValueImpl[] {
new ParsedValueImpl<Size,Size>(new Size(0, SizeUnits.PERCENT), null),
new ParsedValueImpl<Size,Size>(new Size(100, SizeUnits.PERCENT), null)
};
}
if (arg == null ||
arg.token == null ||
arg.token.getText().isEmpty()) {
error(prev, "Expected \'<cycle-method>\' or \'<color-stop>\'");
}
CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
if ("reflect".equalsIgnoreCase(arg.token.getText())) {
cycleMethod = CycleMethod.REFLECT;
prev = arg;
arg = arg.nextArg;
} else if ("repeat".equalsIgnoreCase(arg.token.getText())) {
cycleMethod = CycleMethod.REPEAT;
prev = arg;
arg = arg.nextArg;
}
if (arg == null ||
arg.token == null ||
arg.token.getText().isEmpty()) {
error(prev, "Expected \'<color-stop>\'");
}
ParsedValueImpl<ParsedValue[],Stop>[] stops = parseColorStops(arg);
ParsedValueImpl[] values = new ParsedValueImpl[5 + stops.length];
int index = 0;
values[index++] = (startPt != null) ? startPt[0] : null;
values[index++] = (startPt != null) ? startPt[1] : null;
values[index++] = (endPt != null) ? endPt[0] : null;
values[index++] = (endPt != null) ? endPt[1] : null;
values[index++] = new ParsedValueImpl<String,CycleMethod>(cycleMethod.name(), new EnumConverter<CycleMethod>(CycleMethod.class));
for (int n=0; n<stops.length; n++) values[index++] = stops[n];
return new ParsedValueImpl<ParsedValue[], Paint>(values, PaintConverter.LinearGradientConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[], Paint> radialGradient(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (fn == null || !"radial".equalsIgnoreCase(fn)) {
final String msg = "Expected \'radial\'";
error(root, msg);
}
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(formatDeprecatedMessage(root, "radial gradient"));
}
Term term = root;
Term prev = root;
if ((term = term.nextInSeries) == null) error(root, "Expected \'focus-angle <number>\', \'focus-distance <number>\', \'center (<number>,<number>)\' or \'<size>\'");
if (term.token == null) error(term, "Expected \'focus-angle <number>\', \'focus-distance <number>\', \'center (<number>,<number>)\' or \'<size>\'");
ParsedValueImpl<?,Size> focusAngle = null;
if (term.token.getType() == CssLexer.IDENT) {
final String keyword = term.token.getText().toLowerCase(Locale.ROOT);
if ("focus-angle".equals(keyword)) {
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'<number>\'");
if (term.token == null) error(prev, "Expected \'<number>\'");
focusAngle = parseSize(term);
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'focus-distance <number>\', \'center (<number>,<number>)\' or \'<size>\'");
if (term.token == null) error(term, "Expected \'focus-distance <number>\', \'center (<number>,<number>)\' or \'<size>\'");
}
}
ParsedValueImpl<?,Size> focusDistance = null;
if (term.token.getType() == CssLexer.IDENT) {
final String keyword = term.token.getText().toLowerCase(Locale.ROOT);
if ("focus-distance".equals(keyword)) {
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'<number>\'");
if (term.token == null) error(prev, "Expected \'<number>\'");
focusDistance = parseSize(term);
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected  \'center (<number>,<number>)\' or \'<size>\'");
if (term.token == null) error(term, "Expected  \'center (<number>,<number>)\' or \'<size>\'");
}
}
ParsedValueImpl<?,Size>[] centerPoint = null;
if (term.token.getType() == CssLexer.IDENT) {
final String keyword = term.token.getText().toLowerCase(Locale.ROOT);
if ("center".equals(keyword)) {
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'(<number>,<number>)\'");
if (term.token == null ||
term.token.getType() != CssLexer.LPAREN) error(term, "Expected \'(<number>,<number>)\'");
centerPoint = point(term);
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'<size>\'");
if (term.token == null) error(term, "Expected \'<size>\'");
}
}
ParsedValueImpl<?,Size> radius = parseSize(term);
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'stops\' keyword");
if (term.token == null ||
term.token.getType() != CssLexer.IDENT) error(term, "Expected \'stops\' keyword");
if (!"stops".equalsIgnoreCase(term.token.getText())) error(term, "Expected \'stops\'");
prev = term;
if ((term = term.nextInSeries) == null) error(prev, "Expected \'(<number>, <number>)\'");
int nStops = 0;
Term temp = term;
do {
nStops += 1;
} while (((temp = temp.nextInSeries) != null) &&
((temp.token != null) && (temp.token.getType() == CssLexer.LPAREN)));
ParsedValueImpl[] stops = new ParsedValueImpl[nStops];
int stopIndex = 0;
do {
ParsedValueImpl<ParsedValue[],Stop> stop = stop(term);
if (stop != null) stops[stopIndex++] = stop;
prev = term;
} while(((term = term.nextInSeries) != null) &&
(term.token.getType() == CssLexer.LPAREN));
ParsedValueImpl<String,CycleMethod> cycleMethod = cycleMethod(term);
if (cycleMethod == null) {
cycleMethod = new ParsedValueImpl<String,CycleMethod>(CycleMethod.NO_CYCLE.name(), new EnumConverter<CycleMethod>(CycleMethod.class));
if (term != null) {
root.nextInSeries = term;
}
else {
root.nextInSeries = null;
root.nextLayer = prev.nextLayer;
}
} else {
root.nextInSeries = term.nextInSeries;
root.nextLayer = term.nextLayer;
}
ParsedValueImpl[] values = new ParsedValueImpl[6 + stops.length];
int index = 0;
values[index++] = focusAngle;
values[index++] = focusDistance;
values[index++] = (centerPoint != null) ? centerPoint[0] : null;
values[index++] = (centerPoint != null) ? centerPoint[1] : null;
values[index++] = radius;
values[index++] = cycleMethod;
for (int n=0; n<stops.length; n++) values[index++] = stops[n];
return new ParsedValueImpl<ParsedValue[], Paint>(values, PaintConverter.RadialGradientConverter.getInstance());
}
private ParsedValueImpl parseRadialGradient(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (!"radial-gradient".regionMatches(true, 0, fn, 0, 15)) {
final String msg = "Expected \'radial-gradient\'";
error(root, msg);
}
Term arg;
if ((arg = root.firstArg) == null ||
arg.token == null ||
arg.token.getText().isEmpty()) {
error(root,
"Expected \'focus-angle <angle>\' " +
"or \'focus-distance <percentage>\' " +
"or \'center <point>\' " +
"or \'radius [<length> | <percentage>]\'");
}
Term prev = arg;
ParsedValueImpl<?,Size>focusAngle = null;
ParsedValueImpl<?,Size>focusDistance = null;
ParsedValueImpl<?,Size>[] centerPoint = null;
ParsedValueImpl<?,Size>radius = null;
if ("focus-angle".equalsIgnoreCase(arg.token.getText())) {
prev = arg;
if ((arg = arg.nextInSeries) == null ||
!isSize(arg.token)) error(prev, "Expected \'<angle>\'");
Size angle = size(arg.token);
switch(angle.getUnits()) {
case DEG:
case RAD:
case GRAD:
case TURN:
case PX:
break;
default:
error(arg, "Expected [deg | rad | grad | turn ]");
}
focusAngle = new ParsedValueImpl<Size,Size>(angle, null);
prev = arg;
if ((arg = arg.nextArg) == null)
error(prev, "Expected \'focus-distance <percentage>\' " +
"or \'center <point>\' " +
"or \'radius [<length> | <percentage>]\'");
}
if ("focus-distance".equalsIgnoreCase(arg.token.getText())) {
prev = arg;
if ((arg = arg.nextInSeries) == null ||
!isSize(arg.token)) error(prev, "Expected \'<percentage>\'");
Size distance = size(arg.token);
switch(distance.getUnits()) {
case PERCENT:
break;
default:
error(arg, "Expected \'%\'");
}
focusDistance = new ParsedValueImpl<Size,Size>(distance, null);
prev = arg;
if ((arg = arg.nextArg) == null)
error(prev, "Expected \'center <center>\' " +
"or \'radius <length>\'");
}
if ("center".equalsIgnoreCase(arg.token.getText())) {
prev = arg;
if ((arg = arg.nextInSeries) == null) error(prev, "Expected \'<point>\'");
ParsedValueImpl<?,Size> ptX = parseSize(arg);
prev = arg;
if ((arg = arg.nextInSeries) == null) error(prev, "Expected \'<point>\'");
ParsedValueImpl<?,Size> ptY = parseSize(arg);
centerPoint = new ParsedValueImpl[] { ptX, ptY };
prev = arg;
if ((arg = arg.nextArg) == null)
error(prev, "Expected \'radius [<length> | <percentage>]\'");
}
if ("radius".equalsIgnoreCase(arg.token.getText())) {
prev = arg;
if ((arg = arg.nextInSeries) == null ||
!isSize(arg.token)) error(prev, "Expected \'[<length> | <percentage>]\'");
radius = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null)
error(prev, "Expected \'radius [<length> | <percentage>]\'");
}
CycleMethod cycleMethod = CycleMethod.NO_CYCLE;
if ("reflect".equalsIgnoreCase(arg.token.getText())) {
cycleMethod = CycleMethod.REFLECT;
prev = arg;
arg = arg.nextArg;
} else if ("repeat".equalsIgnoreCase(arg.token.getText())) {
cycleMethod = CycleMethod.REPEAT;
prev = arg;
arg = arg.nextArg;
}
if (arg == null ||
arg.token == null ||
arg.token.getText().isEmpty()) {
error(prev, "Expected \'<color-stop>\'");
}
ParsedValueImpl<ParsedValue[],Stop>[] stops = parseColorStops(arg);
ParsedValueImpl[] values = new ParsedValueImpl[6 + stops.length];
int index = 0;
values[index++] = focusAngle;
values[index++] = focusDistance;
values[index++] = (centerPoint != null) ? centerPoint[0] : null;
values[index++] = (centerPoint != null) ? centerPoint[1] : null;
values[index++] = radius;
values[index++] = new ParsedValueImpl<String,CycleMethod>(cycleMethod.name(), new EnumConverter<CycleMethod>(CycleMethod.class));
for (int n=0; n<stops.length; n++) values[index++] = stops[n];
return new ParsedValueImpl<ParsedValue[], Paint>(values, PaintConverter.RadialGradientConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[], Paint> parseImagePattern(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (!"image-pattern".regionMatches(true, 0, fn, 0, 13)) {
final String msg = "Expected \'image-pattern\'";
error(root, msg);
}
Term arg;
if ((arg = root.firstArg) == null ||
arg.token == null ||
arg.token.getText().isEmpty()) {
error(root,
"Expected \'<uri-string>\'");
}
Term prev = arg;
final String uri = arg.token.getText();
ParsedValueImpl[] uriValues = new ParsedValueImpl[] {
new ParsedValueImpl<String,String>(uri, StringConverter.getInstance()),
null
};
ParsedValueImpl parsedURI = new ParsedValueImpl<ParsedValue[],String>(uriValues, URLConverter.getInstance());
if (arg.nextArg == null) {
ParsedValueImpl[] values = new ParsedValueImpl[1];
values[0] = parsedURI;
return new ParsedValueImpl<ParsedValue[], Paint>(values, PaintConverter.ImagePatternConverter.getInstance());
}
Token token;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<size>\'");
ParsedValueImpl<?, Size> x = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<size>\'");
ParsedValueImpl<?, Size> y = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<size>\'");
ParsedValueImpl<?, Size> w = parseSize(arg);
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<size>\'");
ParsedValueImpl<?, Size> h = parseSize(arg);
if (arg.nextArg == null) {
ParsedValueImpl[] values = new ParsedValueImpl[5];
values[0] = parsedURI;
values[1] = x;
values[2] = y;
values[3] = w;
values[4] = h;
return new ParsedValueImpl<ParsedValue[], Paint>(values, PaintConverter.ImagePatternConverter.getInstance());
}
prev = arg;
if ((arg = arg.nextArg) == null) error(prev, "Expected \'<boolean>\'");
if ((token = arg.token) == null || token.getText() == null) error(arg, "Expected \'<boolean>\'");
ParsedValueImpl[] values = new ParsedValueImpl[6];
values[0] = parsedURI;
values[1] = x;
values[2] = y;
values[3] = w;
values[4] = h;
values[5] = new ParsedValueImpl<Boolean, Boolean>(Boolean.parseBoolean(token.getText()), null);
return new ParsedValueImpl<ParsedValue[], Paint>(values, PaintConverter.ImagePatternConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[], Paint> parseRepeatingImagePattern(final Term root) throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (!"repeating-image-pattern".regionMatches(true, 0, fn, 0, 23)) {
final String msg = "Expected \'repeating-image-pattern\'";
error(root, msg);
}
Term arg;
if ((arg = root.firstArg) == null ||
arg.token == null ||
arg.token.getText().isEmpty()) {
error(root,
"Expected \'<uri-string>\'");
}
final String uri = arg.token.getText();
ParsedValueImpl[] uriValues = new ParsedValueImpl[] {
new ParsedValueImpl<String,String>(uri, StringConverter.getInstance()),
null
};
ParsedValueImpl parsedURI = new ParsedValueImpl<ParsedValue[],String>(uriValues, URLConverter.getInstance());
ParsedValueImpl[] values = new ParsedValueImpl[1];
values[0] = parsedURI;
return new ParsedValueImpl<ParsedValue[], Paint>(values, PaintConverter.RepeatingImagePatternConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<?,Paint>[],Paint[]> parsePaintLayers(Term root)
throws ParseException {
int nPaints = numberOfLayers(root);
ParsedValueImpl<?,Paint>[] paints = new ParsedValueImpl[nPaints];
Term temp = root;
int paint = 0;
do {
if (temp.token == null ||
temp.token.getText() == null ||
temp.token.getText().isEmpty()) error(temp, "Expected \'<paint>\'");
paints[paint++] = (ParsedValueImpl<?,Paint>)parse(temp);
temp = nextLayer(temp);
} while (temp != null);
return new ParsedValueImpl<ParsedValue<?,Paint>[],Paint[]>(paints, PaintConverter.SequenceConverter.getInstance());
}
private ParsedValueImpl<?,Size>[] parseSize1to4(final Term root)
throws ParseException {
Term temp = root;
ParsedValueImpl<?,Size>[] sides = new ParsedValueImpl[4];
int side = 0;
while (side < 4 && temp != null) {
sides[side++] = parseSize(temp);
temp = temp.nextInSeries;
}
if (side < 2) sides[1] = sides[0];
if (side < 3) sides[2] = sides[0];
if (side < 4) sides[3] = sides[1];
return sides;
}
private ParsedValueImpl<ParsedValue<ParsedValue[],Insets>[], Insets[]> parseInsetsLayers(Term root)
throws ParseException {
int nLayers = numberOfLayers(root);
Term temp = root;
int layer = 0;
ParsedValueImpl<ParsedValue[],Insets>[] layers = new ParsedValueImpl[nLayers];
while(temp != null) {
ParsedValueImpl<?,Size>[] sides = parseSize1to4(temp);
layers[layer++] = new ParsedValueImpl<ParsedValue[],Insets>(sides, InsetsConverter.getInstance());
while(temp.nextInSeries != null) {
temp = temp.nextInSeries;
}
temp = nextLayer(temp);
}
return new ParsedValueImpl<ParsedValue<ParsedValue[],Insets>[], Insets[]>(layers, InsetsConverter.SequenceConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[],Insets> parseInsetsLayer(Term root)
throws ParseException {
Term temp = root;
ParsedValueImpl<ParsedValue[],Insets> layer = null;
while(temp != null) {
ParsedValueImpl<?,Size>[] sides = parseSize1to4(temp);
layer = new ParsedValueImpl<ParsedValue[],Insets>(sides, InsetsConverter.getInstance());
while(temp.nextInSeries != null) {
temp = temp.nextInSeries;
}
temp = nextLayer(temp);
}
return layer;
}
private ParsedValueImpl<ParsedValue<ParsedValue[],Margins>[], Margins[]> parseMarginsLayers(Term root)
throws ParseException {
int nLayers = numberOfLayers(root);
Term temp = root;
int layer = 0;
ParsedValueImpl<ParsedValue[],Margins>[] layers = new ParsedValueImpl[nLayers];
while(temp != null) {
ParsedValueImpl<?,Size>[] sides = parseSize1to4(temp);
layers[layer++] = new ParsedValueImpl<ParsedValue[],Margins>(sides, Margins.Converter.getInstance());
while(temp.nextInSeries != null) {
temp = temp.nextInSeries;
}
temp = nextLayer(temp);
}
return new ParsedValueImpl<ParsedValue<ParsedValue[],Margins>[], Margins[]>(layers, Margins.SequenceConverter.getInstance());
}
private ParsedValueImpl<Size, Size>[] parseSizeSeries(Term root)
throws ParseException {
if (root.token == null) error(root, "Parse error");
List<ParsedValueImpl<Size,Size>> sizes = new ArrayList<>();
Term term = root;
while(term != null) {
Token token = term.token;
final int ttype = token.getType();
switch (ttype) {
case CssLexer.NUMBER:
case CssLexer.PERCENTAGE:
case CssLexer.EMS:
case CssLexer.EXS:
case CssLexer.PX:
case CssLexer.CM:
case CssLexer.MM:
case CssLexer.IN:
case CssLexer.PT:
case CssLexer.PC:
case CssLexer.DEG:
case CssLexer.GRAD:
case CssLexer.RAD:
case CssLexer.TURN:
ParsedValueImpl sizeValue = new ParsedValueImpl<Size, Size>(size(token), null);
sizes.add(sizeValue);
break;
default:
error (root, "expected series of <size>");
}
term = term.nextInSeries;
}
return sizes.toArray(new ParsedValueImpl[sizes.size()]);
}
private ParsedValueImpl<ParsedValue<ParsedValue<?,Size>[][],CornerRadii>[], CornerRadii[]> parseCornerRadius(Term root)
throws ParseException {
int nLayers = numberOfLayers(root);
Term term = root;
int layer = 0;
ParsedValueImpl<ParsedValue<?,Size>[][],CornerRadii>[] layers = new ParsedValueImpl[nLayers];
while(term != null) {
int nHorizontalTerms = 0;
Term temp = term;
while (temp != null) {
if (temp.token.getType() == CssLexer.SOLIDUS) {
temp = temp.nextInSeries;
break;
}
nHorizontalTerms += 1;
temp = temp.nextInSeries;
};
int nVerticalTerms = 0;
while (temp != null) {
if (temp.token.getType() == CssLexer.SOLIDUS) {
error(temp, "unexpected SOLIDUS");
break;
}
nVerticalTerms += 1;
temp = temp.nextInSeries;
}
if ((nHorizontalTerms == 0 || nHorizontalTerms > 4) || nVerticalTerms > 4) {
error(root, "expected [<length>|<percentage>]{1,4} [/ [<length>|<percentage>]{1,4}]?");
}
int orientation = 0;
ParsedValueImpl<?,Size>[][] radii = new ParsedValueImpl[2][4];
ParsedValueImpl<?,Size> zero = new ParsedValueImpl<Size,Size>(new Size(0,SizeUnits.PX), null);
for (int r=0; r<4; r++) { radii[0][r] = zero; radii[1][r] = zero; }
int hr = 0;
int vr = 0;
Term lastTerm = term;
while ((hr <= 4) && (vr <= 4) && (term != null)) {
if (term.token.getType() == CssLexer.SOLIDUS) {
orientation += 1;
} else {
ParsedValueImpl<?,Size> parsedValue = parseSize(term);
if (orientation == 0) {
radii[orientation][hr++] = parsedValue;
} else {
radii[orientation][vr++] = parsedValue;
}
}
lastTerm = term;
term = term.nextInSeries;
}
if (hr != 0) {
if (hr < 2) radii[0][1] = radii[0][0];
if (hr < 3) radii[0][2] = radii[0][0];
if (hr < 4) radii[0][3] = radii[0][1];
} else {
assert(false);
}
if (vr != 0) {
if (vr < 2) radii[1][1] = radii[1][0];
if (vr < 3) radii[1][2] = radii[1][0];
if (vr < 4) radii[1][3] = radii[1][1];
} else {
radii[1][0] = radii[0][0];
radii[1][1] = radii[0][1];
radii[1][2] = radii[0][2];
radii[1][3] = radii[0][3];
}
if (zero.equals(radii[0][0]) || zero.equals(radii[1][0])) { radii[1][0] = radii[0][0] = zero; }
if (zero.equals(radii[0][1]) || zero.equals(radii[1][1])) { radii[1][1] = radii[0][1] = zero; }
if (zero.equals(radii[0][2]) || zero.equals(radii[1][2])) { radii[1][2] = radii[0][2] = zero; }
if (zero.equals(radii[0][3]) || zero.equals(radii[1][3])) { radii[1][3] = radii[0][3] = zero; }
layers[layer++] = new ParsedValueImpl<ParsedValue<?,Size>[][],CornerRadii>(radii, null);
term = nextLayer(lastTerm);
}
return new ParsedValueImpl<ParsedValue<ParsedValue<?,Size>[][],CornerRadii>[], CornerRadii[]>(layers, CornerRadiiConverter.getInstance());
}
private final static ParsedValueImpl<Size,Size> ZERO_PERCENT =
new ParsedValueImpl<Size,Size>(new Size(0f, SizeUnits.PERCENT), null);
private final static ParsedValueImpl<Size,Size> FIFTY_PERCENT =
new ParsedValueImpl<Size,Size>(new Size(50f, SizeUnits.PERCENT), null);
private final static ParsedValueImpl<Size,Size> ONE_HUNDRED_PERCENT =
new ParsedValueImpl<Size,Size>(new Size(100f, SizeUnits.PERCENT), null);
private static boolean isPositionKeyWord(String value) {
return "center".equalsIgnoreCase(value) || "top".equalsIgnoreCase(value) || "bottom".equalsIgnoreCase(value) || "left".equalsIgnoreCase(value) || "right".equalsIgnoreCase(value);
}
private ParsedValueImpl<ParsedValue[], BackgroundPosition> parseBackgroundPosition(Term term)
throws ParseException {
if (term.token == null ||
term.token.getText() == null ||
term.token.getText().isEmpty()) error(term, "Expected \'<bg-position>\'");
Term termOne = term;
Token valueOne = term.token;
Term termTwo = termOne.nextInSeries;
Token valueTwo = (termTwo != null) ? termTwo.token : null;
Term termThree = (termTwo != null) ? termTwo.nextInSeries : null;
Token valueThree = (termThree != null) ? termThree.token : null;
Term termFour = (termThree != null) ? termThree.nextInSeries : null;
Token valueFour = (termFour != null) ? termFour.token : null;
if( valueOne != null && valueTwo != null && valueThree == null && valueFour == null ) {
String v1 = valueOne.getText();
String v2 = valueTwo.getText();
if( ("top".equals(v1) || "bottom".equals(v1))
&& ("left".equals(v2) || "right".equals(v2) || "center".equals(v2)) ) {
{
Token tmp = valueTwo;
valueTwo = valueOne;
valueOne = tmp;
}
{
Term tmp = termTwo;
termTwo = termOne;
termOne = tmp;
}
}
} else if( valueOne != null && valueTwo != null && valueThree != null ) {
Term[] termArray = null;
Token[] tokeArray = null;
if( valueFour != null ) {
if( ("top".equals(valueOne.getText()) || "bottom".equals(valueOne.getText()))
&& ("left".equals(valueThree.getText()) || "right".equals(valueThree.getText())) ) {
termArray = new Term[] { termThree, termFour, termOne, termTwo };
tokeArray = new Token[] { valueThree, valueFour, valueOne, valueTwo };
}
} else {
if( ("top".equals(valueOne.getText()) || "bottom".equals(valueOne.getText())) ) {
if( ("left".equals(valueTwo.getText()) || "right".equals(valueTwo.getText())) ) {
termArray = new Term[] { termTwo, termThree, termOne, null };
tokeArray = new Token[] { valueTwo, valueThree, valueOne, null };
} else {
termArray = new Term[] { termThree, termOne, termTwo, null };
tokeArray = new Token[] { valueThree, valueOne, valueTwo, null };
}
}
}
if( termArray != null ) {
termOne = termArray[0];
termTwo = termArray[1];
termThree = termArray[2];
termFour = termArray[3];
valueOne = tokeArray[0];
valueTwo = tokeArray[1];
valueThree = tokeArray[2];
valueFour = tokeArray[3];
}
}
ParsedValueImpl<?,Size> top, right, bottom, left;
top = right = bottom = left = ZERO_PERCENT;
{
if(valueOne == null && valueTwo == null && valueThree == null && valueFour == null) {
error(term, "No value found for background-position");
} else if( valueOne != null && valueTwo == null && valueThree == null && valueFour == null ) {
String v1 = valueOne.getText();
if( "center".equals(v1) ) {
left = FIFTY_PERCENT;
right = ZERO_PERCENT;
top = FIFTY_PERCENT;
bottom = ZERO_PERCENT;
} else if("left".equals(v1)) {
left = ZERO_PERCENT;
right = ZERO_PERCENT;
top = FIFTY_PERCENT;
bottom = ZERO_PERCENT;
} else if( "right".equals(v1) ) {
left = ONE_HUNDRED_PERCENT;
right = ZERO_PERCENT;
top = FIFTY_PERCENT;
bottom = ZERO_PERCENT;
} else if( "top".equals(v1) ) {
left = FIFTY_PERCENT;
right = ZERO_PERCENT;
top = ZERO_PERCENT;
bottom = ZERO_PERCENT;
} else if( "bottom".equals(v1) ) {
left = FIFTY_PERCENT;
right = ZERO_PERCENT;
top = ONE_HUNDRED_PERCENT;
bottom = ZERO_PERCENT;
} else {
left = parseSize(termOne);
right = ZERO_PERCENT;
top = FIFTY_PERCENT;
bottom = ZERO_PERCENT;
}
} else if( valueOne != null && valueTwo != null && valueThree == null && valueFour == null ) {
String v1 = valueOne.getText().toLowerCase(Locale.ROOT);
String v2 = valueTwo.getText().toLowerCase(Locale.ROOT);
if( ! isPositionKeyWord(v1) ) {
left = parseSize(termOne);
right = ZERO_PERCENT;
if( "top".equals(v2) ) {
top = ZERO_PERCENT;
bottom = ZERO_PERCENT;
} else if( "bottom".equals(v2) ) {
top = ONE_HUNDRED_PERCENT;
bottom = ZERO_PERCENT;
} else if( "center".equals(v2) ) {
top = FIFTY_PERCENT;
bottom = ZERO_PERCENT;
} else if( !isPositionKeyWord(v2) ) {
top = parseSize(termTwo);
bottom = ZERO_PERCENT;
} else {
error(termTwo,"Expected 'top', 'bottom', 'center' or <size>");
}
} else if( v1.equals("left") || v1.equals("right") ) {
left = v1.equals("right") ? ONE_HUNDRED_PERCENT : ZERO_PERCENT;
right = ZERO_PERCENT;
if( ! isPositionKeyWord(v2) ) {
top = parseSize(termTwo);
bottom = ZERO_PERCENT;
} else if( v2.equals("top") || v2.equals("bottom") || v2.equals("center") ) {
if( v2.equals("top") ) {
top = ZERO_PERCENT;
bottom = ZERO_PERCENT;
} else if(v2.equals("center")) {
top = FIFTY_PERCENT;
bottom = ZERO_PERCENT;
} else {
top = ONE_HUNDRED_PERCENT;
bottom = ZERO_PERCENT;
}
} else {
error(termTwo,"Expected 'top', 'bottom', 'center' or <size>");
}
} else if( v1.equals("center") ) {
left = FIFTY_PERCENT;
right = ZERO_PERCENT;
if( v2.equals("top") ) {
top = ZERO_PERCENT;
bottom = ZERO_PERCENT;
} else if( v2.equals("bottom") ) {
top = ONE_HUNDRED_PERCENT;
bottom = ZERO_PERCENT;
} else if( v2.equals("center") ) {
top = FIFTY_PERCENT;
bottom = ZERO_PERCENT;
} else if( ! isPositionKeyWord(v2) ) {
top = parseSize(termTwo);
bottom = ZERO_PERCENT;
} else {
error(termTwo,"Expected 'top', 'bottom', 'center' or <size>");
}
}
} else if( valueOne != null && valueTwo != null && valueThree != null && valueFour == null ) {
String v1 = valueOne.getText().toLowerCase(Locale.ROOT);
String v2 = valueTwo.getText().toLowerCase(Locale.ROOT);
String v3 = valueThree.getText().toLowerCase(Locale.ROOT);
if( ! isPositionKeyWord(v1) || "center".equals(v1) ) {
if( "center".equals(v1) ) {
left = FIFTY_PERCENT;
} else {
left = parseSize(termOne);
}
right = ZERO_PERCENT;
if( !isPositionKeyWord(v3) ) {
if( "top".equals(v2) ) {
top = parseSize(termThree);
bottom = ZERO_PERCENT;
} else if( "bottom".equals(v2) ) {
top = ZERO_PERCENT;
bottom = parseSize(termThree);
} else {
error(termTwo,"Expected 'top' or 'bottom'");
}
} else {
error(termThree,"Expected <size>");
}
} else if( "left".equals(v1) || "right".equals(v1) ) {
if( ! isPositionKeyWord(v2) ) {
if( "left".equals(v1) ) {
left = parseSize(termTwo);
right = ZERO_PERCENT;
} else {
left = ZERO_PERCENT;
right = parseSize(termTwo);
}
if( "top".equals(v3) ) {
top = ZERO_PERCENT;
bottom = ZERO_PERCENT;
} else if( "bottom".equals(v3) ) {
top = ONE_HUNDRED_PERCENT;
bottom = ZERO_PERCENT;
} else if( "center".equals(v3) ) {
top = FIFTY_PERCENT;
bottom = ZERO_PERCENT;
} else {
error(termThree,"Expected 'top', 'bottom' or 'center'");
}
} else {
if( "left".equals(v1) ) {
left = ZERO_PERCENT;
right = ZERO_PERCENT;
} else {
left = ONE_HUNDRED_PERCENT;
right = ZERO_PERCENT;
}
if( ! isPositionKeyWord(v3) ) {
if( "top".equals(v2) ) {
top = parseSize(termThree);
bottom = ZERO_PERCENT;
} else if( "bottom".equals(v2) ) {
top = ZERO_PERCENT;
bottom = parseSize(termThree);
} else {
error(termTwo,"Expected 'top' or 'bottom'");
}
} else {
error(termThree,"Expected <size>");
}
}
}
} else {
String v1 = valueOne.getText().toLowerCase(Locale.ROOT);
String v2 = valueTwo.getText().toLowerCase(Locale.ROOT);
String v3 = valueThree.getText().toLowerCase(Locale.ROOT);
String v4 = valueFour.getText().toLowerCase(Locale.ROOT);
if( (v1.equals("left") || v1.equals("right")) && (v3.equals("top") || v3.equals("bottom") ) && ! isPositionKeyWord(v2) && ! isPositionKeyWord(v4) ) {
if( v1.equals("left") ) {
left = parseSize(termTwo);
right = ZERO_PERCENT;
} else {
left = ZERO_PERCENT;
right = parseSize(termTwo);
}
if( v3.equals("top") ) {
top = parseSize(termFour);
bottom = ZERO_PERCENT;
} else {
top = ZERO_PERCENT;
bottom = parseSize(termFour);
}
} else {
error(term,"Expected 'left' or 'right' followed by <size> followed by 'top' or 'bottom' followed by <size>");
}
}
}
ParsedValueImpl<?,Size>[] values = new ParsedValueImpl[] {top, right, bottom, left};
return new ParsedValueImpl<ParsedValue[], BackgroundPosition>(values, BackgroundPositionConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]>
parseBackgroundPositionLayers(final Term root) throws ParseException {
int nLayers = numberOfLayers(root);
ParsedValueImpl<ParsedValue[], BackgroundPosition>[] layers = new ParsedValueImpl[nLayers];
int layer = 0;
Term term = root;
while (term != null) {
layers[layer++] = parseBackgroundPosition(term);
term = nextLayer(term);
}
return new ParsedValueImpl<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]>(layers, LayeredBackgroundPositionConverter.getInstance());
}
private ParsedValueImpl<String, BackgroundRepeat>[] parseRepeatStyle(final Term root)
throws ParseException {
BackgroundRepeat xAxis, yAxis;
xAxis = yAxis = BackgroundRepeat.NO_REPEAT;
Term term = root;
if (term.token == null ||
term.token.getType() != CssLexer.IDENT ||
term.token.getText() == null ||
term.token.getText().isEmpty()) error(term, "Expected \'<repeat-style>\'");
String text = term.token.getText().toLowerCase(Locale.ROOT);
if ("repeat-x".equals(text)) {
xAxis = BackgroundRepeat.REPEAT;
yAxis = BackgroundRepeat.NO_REPEAT;
} else if ("repeat-y".equals(text)) {
xAxis = BackgroundRepeat.NO_REPEAT;
yAxis = BackgroundRepeat.REPEAT;
} else if ("repeat".equals(text)) {
xAxis = BackgroundRepeat.REPEAT;
yAxis = BackgroundRepeat.REPEAT;
} else if ("space".equals(text)) {
xAxis = BackgroundRepeat.SPACE;
yAxis = BackgroundRepeat.SPACE;
} else if ("round".equals(text)) {
xAxis = BackgroundRepeat.ROUND;
yAxis = BackgroundRepeat.ROUND;
} else if ("no-repeat".equals(text)) {
xAxis = BackgroundRepeat.NO_REPEAT;
yAxis = BackgroundRepeat.NO_REPEAT;
} else if ("stretch".equals(text)) {
xAxis = BackgroundRepeat.NO_REPEAT;
yAxis = BackgroundRepeat.NO_REPEAT;
} else {
error(term, "Expected  \'<repeat-style>\' " + text);
}
if ((term = term.nextInSeries) != null &&
term.token != null &&
term.token.getType() == CssLexer.IDENT &&
term.token.getText() != null &&
!term.token.getText().isEmpty()) {
text = term.token.getText().toLowerCase(Locale.ROOT);
if ("repeat-x".equals(text)) {
error(term, "Unexpected \'repeat-x\'");
} else if ("repeat-y".equals(text)) {
error(term, "Unexpected \'repeat-y\'");
} else if ("repeat".equals(text)) {
yAxis = BackgroundRepeat.REPEAT;
} else if ("space".equals(text)) {
yAxis = BackgroundRepeat.SPACE;
} else if ("round".equals(text)) {
yAxis = BackgroundRepeat.ROUND;
} else if ("no-repeat".equals(text)) {
yAxis = BackgroundRepeat.NO_REPEAT;
} else if ("stretch".equals(text)) {
yAxis = BackgroundRepeat.NO_REPEAT;
} else {
error(term, "Expected  \'<repeat-style>\'");
}
}
return new ParsedValueImpl[] {
new ParsedValueImpl<String,BackgroundRepeat>(xAxis.name(), new EnumConverter<BackgroundRepeat>(BackgroundRepeat.class)),
new ParsedValueImpl<String,BackgroundRepeat>(yAxis.name(), new EnumConverter<BackgroundRepeat>(BackgroundRepeat.class))
};
}
private ParsedValueImpl<ParsedValue<String, BackgroundRepeat>[][],RepeatStruct[]>
parseBorderImageRepeatStyleLayers(final Term root) throws ParseException {
int nLayers = numberOfLayers(root);
ParsedValueImpl<String, BackgroundRepeat>[][] layers = new ParsedValueImpl[nLayers][];
int layer = 0;
Term term = root;
while (term != null) {
layers[layer++] = parseRepeatStyle(term);
term = nextLayer(term);
}
return new ParsedValueImpl<ParsedValue<String, BackgroundRepeat>[][],RepeatStruct[]>(layers, RepeatStructConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<String, BackgroundRepeat>[][], RepeatStruct[]>
parseBackgroundRepeatStyleLayers(final Term root) throws ParseException {
int nLayers = numberOfLayers(root);
ParsedValueImpl<String, BackgroundRepeat>[][] layers = new ParsedValueImpl[nLayers][];
int layer = 0;
Term term = root;
while (term != null) {
layers[layer++] = parseRepeatStyle(term);
term = nextLayer(term);
}
return new ParsedValueImpl<ParsedValue<String, BackgroundRepeat>[][], RepeatStruct[]>(layers, RepeatStructConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[], BackgroundSize> parseBackgroundSize(final Term root)
throws ParseException {
ParsedValueImpl<?,Size> height = null, width = null;
boolean cover = false, contain = false;
Term term = root;
if (term.token == null) error(term, "Expected \'<bg-size>\'");
if (term.token.getType() == CssLexer.IDENT) {
final String text =
(term.token.getText() != null) ? term.token.getText().toLowerCase(Locale.ROOT) : null;
if ("auto".equals(text)) {
} else if ("cover".equals(text)) {
cover = true;
} else if ("contain".equals(text)) {
contain = true;
} else if ("stretch".equals(text)) {
width = ONE_HUNDRED_PERCENT;
height = ONE_HUNDRED_PERCENT;
} else {
error(term, "Expected \'auto\', \'cover\', \'contain\', or  \'stretch\'");
}
} else if (isSize(term.token)) {
width = parseSize(term);
height = null;
} else {
error(term, "Expected \'<bg-size>\'");
}
if ((term = term.nextInSeries) != null) {
if (cover || contain) error(term, "Unexpected \'<bg-size>\'");
if (term.token.getType() == CssLexer.IDENT) {
final String text =
(term.token.getText() != null) ? term.token.getText().toLowerCase(Locale.ROOT) : null;
if ("auto".equals(text)) {
height = null;
} else if ("cover".equals(text)) {
error(term, "Unexpected \'cover\'");
} else if ("contain".equals(text)) {
error(term, "Unexpected \'contain\'");
} else if ("stretch".equals(text)) {
height = ONE_HUNDRED_PERCENT;
} else {
error(term, "Expected \'auto\' or \'stretch\'");
}
} else if (isSize(term.token)) {
height = parseSize(term);
} else {
error(term, "Expected \'<bg-size>\'");
}
}
ParsedValueImpl[] values = new ParsedValueImpl[] {
width,
height,
new ParsedValueImpl<String,Boolean>((cover ? "true" : "false"), BooleanConverter.getInstance()),
new ParsedValueImpl<String,Boolean>((contain ? "true" : "false"), BooleanConverter.getInstance())
};
return new ParsedValueImpl<ParsedValue[], BackgroundSize>(values, BackgroundSizeConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<ParsedValue[], BackgroundSize>[], BackgroundSize[]>
parseBackgroundSizeLayers(final Term root) throws ParseException {
int nLayers = numberOfLayers(root);
ParsedValueImpl<ParsedValue[], BackgroundSize>[] layers = new ParsedValueImpl[nLayers];
int layer = 0;
Term term = root;
while (term != null) {
layers[layer++] = parseBackgroundSize(term);
term = nextLayer(term);
}
return new ParsedValueImpl<ParsedValue<ParsedValue[], BackgroundSize>[], BackgroundSize[]>(layers, LayeredBackgroundSizeConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<?,Paint>[], Paint[]> parseBorderPaint(final Term root)
throws ParseException {
Term term = root;
ParsedValueImpl<?,Paint>[] paints = new ParsedValueImpl[4];
int paint = 0;
while(term != null) {
if (term.token == null || paints.length <= paint) error(term, "Expected \'<paint>\'");
paints[paint++] = parse(term);
term = term.nextInSeries;
}
if (paint < 2) paints[1] = paints[0];
if (paint < 3) paints[2] = paints[0];
if (paint < 4) paints[3] = paints[1];
return new ParsedValueImpl<ParsedValue<?,Paint>[], Paint[]>(paints, StrokeBorderPaintConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<ParsedValue<?,Paint>[],Paint[]>[], Paint[][]> parseBorderPaintLayers(final Term root)
throws ParseException {
int nLayers = numberOfLayers(root);
ParsedValueImpl<ParsedValue<?,Paint>[],Paint[]>[] layers = new ParsedValueImpl[nLayers];
int layer = 0;
Term term = root;
while (term != null) {
layers[layer++] = parseBorderPaint(term);
term = nextLayer(term);
}
return new ParsedValueImpl<ParsedValue<ParsedValue<?,Paint>[],Paint[]>[], Paint[][]>(layers, LayeredBorderPaintConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<ParsedValue[],BorderStrokeStyle>[],BorderStrokeStyle[]> parseBorderStyleSeries(final Term root)
throws ParseException {
Term term = root;
ParsedValueImpl<ParsedValue[],BorderStrokeStyle>[] borders = new ParsedValueImpl[4];
int border = 0;
while (term != null) {
borders[border++] = parseBorderStyle(term);
term = term.nextInSeries;
}
if (border < 2) borders[1] = borders[0];
if (border < 3) borders[2] = borders[0];
if (border < 4) borders[3] = borders[1];
return new ParsedValueImpl<ParsedValue<ParsedValue[],BorderStrokeStyle>[],BorderStrokeStyle[]>(borders, BorderStrokeStyleSequenceConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<ParsedValue<ParsedValue[],BorderStrokeStyle>[],BorderStrokeStyle[]>[], BorderStrokeStyle[][]>
parseBorderStyleLayers(final Term root) throws ParseException {
int nLayers = numberOfLayers(root);
ParsedValueImpl<ParsedValue<ParsedValue[],BorderStrokeStyle>[],BorderStrokeStyle[]>[] layers = new ParsedValueImpl[nLayers];
int layer = 0;
Term term = root;
while (term != null) {
layers[layer++] = parseBorderStyleSeries(term);
term = nextLayer(term);
}
return new ParsedValueImpl<ParsedValue<ParsedValue<ParsedValue[],BorderStrokeStyle>[],BorderStrokeStyle[]>[], BorderStrokeStyle[][]>(layers, LayeredBorderStyleConverter.getInstance());
}
private String getKeyword(final Term term) {
if (term != null &&
term.token != null &&
term.token.getType() == CssLexer.IDENT &&
term.token.getText() != null &&
!term.token.getText().isEmpty()) {
return term.token.getText().toLowerCase(Locale.ROOT);
}
return null;
}
private ParsedValueImpl<ParsedValue[],BorderStrokeStyle> parseBorderStyle(final Term root)
throws ParseException {
ParsedValue<ParsedValue[],Number[]> dashStyle = null;
ParsedValue<ParsedValue<?,Size>,Number> dashPhase = null;
ParsedValue<String,StrokeType> strokeType = null;
ParsedValue<String,StrokeLineJoin> strokeLineJoin = null;
ParsedValue<ParsedValue<?,Size>,Number> strokeMiterLimit = null;
ParsedValue<String,StrokeLineCap> strokeLineCap = null;
Term term = root;
dashStyle = dashStyle(term);
Term prev = term;
term = term.nextInSeries;
String keyword = getKeyword(term);
if ("phase".equals(keyword)) {
prev = term;
if ((term = term.nextInSeries) == null ||
term.token == null ||
!isSize(term.token)) error(term, "Expected \'<size>\'");
ParsedValueImpl<?,Size> sizeVal = parseSize(term);
dashPhase = new ParsedValueImpl<ParsedValue<?,Size>,Number>(sizeVal,SizeConverter.getInstance());
prev = term;
term = term.nextInSeries;
}
strokeType = parseStrokeType(term);
if (strokeType != null) {
prev = term;
term = term.nextInSeries;
}
keyword = getKeyword(term);
if ("line-join".equals(keyword)) {
prev = term;
term = term.nextInSeries;
ParsedValueImpl[] lineJoinValues = parseStrokeLineJoin(term);
if (lineJoinValues != null) {
strokeLineJoin = lineJoinValues[0];
strokeMiterLimit = lineJoinValues[1];
} else {
error(term, "Expected \'miter <size>?\', \'bevel\' or \'round\'");
}
prev = term;
term = term.nextInSeries;
keyword = getKeyword(term);
}
if ("line-cap".equals(keyword)) {
prev = term;
term = term.nextInSeries;
strokeLineCap = parseStrokeLineCap(term);
if (strokeLineCap == null) {
error(term, "Expected \'square\', \'butt\' or \'round\'");
}
prev = term;
term = term.nextInSeries;
}
if (term != null) {
root.nextInSeries = term;
} else {
root.nextInSeries = null;
root.nextLayer = prev.nextLayer;
}
final ParsedValue[] values = new ParsedValue[]{
dashStyle,
dashPhase,
strokeType,
strokeLineJoin,
strokeMiterLimit,
strokeLineCap
};
return new ParsedValueImpl(values, BorderStyleConverter.getInstance());
}
private ParsedValue<ParsedValue[],Number[]> dashStyle(final Term root) throws ParseException {
if (root.token == null) error(root, "Expected \'<dash-style>\'");
final int ttype = root.token.getType();
ParsedValue<ParsedValue[],Number[]> segments = null;
if (ttype == CssLexer.IDENT) {
segments = borderStyle(root);
} else if (ttype == CssLexer.FUNCTION) {
segments = segments(root);
} else {
error(root, "Expected \'<dash-style>\'");
}
return segments;
}
private ParsedValue<ParsedValue[],Number[]> borderStyle(Term root)
throws ParseException {
if (root.token == null ||
root.token.getType() != CssLexer.IDENT ||
root.token.getText() == null ||
root.token.getText().isEmpty()) error(root, "Expected \'<border-style>\'");
final String text = root.token.getText().toLowerCase(Locale.ROOT);
if ("none".equals(text)) {
return BorderStyleConverter.NONE;
} else if ("hidden".equals(text)) {
return BorderStyleConverter.NONE;
} else if ("dotted".equals(text)) {
return BorderStyleConverter.DOTTED;
} else if ("dashed".equals(text)) {
return BorderStyleConverter.DASHED;
} else if ("solid".equals(text)) {
return BorderStyleConverter.SOLID;
} else if ("double".equals(text)) {
error(root, "Unsupported <border-style> \'double\'");
} else if ("groove".equals(text)) {
error(root, "Unsupported <border-style> \'groove\'");
} else if ("ridge".equals(text)) {
error(root, "Unsupported <border-style> \'ridge\'");
} else if ("inset".equals(text)) {
error(root, "Unsupported <border-style> \'inset\'");
} else if ("outset".equals(text)) {
error(root, "Unsupported <border-style> \'outset\'");
} else {
error(root, "Unsupported <border-style> \'" + text + "\'");
}
return BorderStyleConverter.SOLID;
}
private ParsedValueImpl<ParsedValue[],Number[]> segments(Term root)
throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (!"segments".regionMatches(true, 0, fn, 0, 8)) {
error(root,"Expected \'segments\'");
}
Term arg = root.firstArg;
if (arg == null) error(null, "Expected \'<size>\'");
int nArgs = numberOfArgs(root);
ParsedValueImpl<?,Size>[] segments = new ParsedValueImpl[nArgs];
int segment = 0;
while(arg != null) {
segments[segment++] = parseSize(arg);
arg = arg.nextArg;
}
return new ParsedValueImpl<ParsedValue[],Number[]>(segments,SizeConverter.SequenceConverter.getInstance());
}
private ParsedValueImpl<String,StrokeType> parseStrokeType(final Term root)
throws ParseException {
final String keyword = getKeyword(root);
if ("centered".equals(keyword) ||
"inside".equals(keyword) ||
"outside".equals(keyword)) {
return new ParsedValueImpl<String,StrokeType>(keyword, new EnumConverter(StrokeType.class));
}
return null;
}
private ParsedValueImpl[] parseStrokeLineJoin(final Term root)
throws ParseException {
final String keyword = getKeyword(root);
if ("miter".equals(keyword) ||
"bevel".equals(keyword) ||
"round".equals(keyword)) {
ParsedValueImpl<String,StrokeLineJoin> strokeLineJoin =
new ParsedValueImpl<String,StrokeLineJoin>(keyword, new EnumConverter(StrokeLineJoin.class));
ParsedValueImpl<ParsedValue<?,Size>,Number> strokeMiterLimit = null;
if ("miter".equals(keyword)) {
Term next = root.nextInSeries;
if (next != null &&
next.token != null &&
isSize(next.token)) {
root.nextInSeries = next.nextInSeries;
ParsedValueImpl<?,Size> sizeVal = parseSize(next);
strokeMiterLimit = new ParsedValueImpl<ParsedValue<?,Size>,Number>(sizeVal,SizeConverter.getInstance());
}
}
return new ParsedValueImpl[] { strokeLineJoin, strokeMiterLimit };
}
return null;
}
private ParsedValueImpl<String,StrokeLineCap> parseStrokeLineCap(final Term root)
throws ParseException {
final String keyword = getKeyword(root);
if ("square".equals(keyword) ||
"butt".equals(keyword) ||
"round".equals(keyword)) {
return new ParsedValueImpl<String,StrokeLineCap>(keyword, new EnumConverter(StrokeLineCap.class));
}
return null;
}
private ParsedValueImpl<ParsedValue[],BorderImageSlices> parseBorderImageSlice(final Term root)
throws ParseException {
Term term = root;
if (term.token == null || !isSize(term.token))
error(term, "Expected \'<size>\'");
ParsedValueImpl<?,Size>[] insets = new ParsedValueImpl[4];
Boolean fill = Boolean.FALSE;
int inset = 0;
while (inset < 4 && term != null) {
insets[inset++] = parseSize(term);
if ((term = term.nextInSeries) != null &&
term.token != null &&
term.token.getType() == CssLexer.IDENT) {
if("fill".equalsIgnoreCase(term.token.getText())) {
fill = Boolean.TRUE;
break;
}
}
}
if (inset < 2) insets[1] = insets[0];
if (inset < 3) insets[2] = insets[0];
if (inset < 4) insets[3] = insets[1];
ParsedValueImpl[] values = new ParsedValueImpl[] {
new ParsedValueImpl<ParsedValue[],Insets>(insets, InsetsConverter.getInstance()),
new ParsedValueImpl<Boolean,Boolean>(fill, null)
};
return new ParsedValueImpl<ParsedValue[], BorderImageSlices>(values, BorderImageSliceConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<ParsedValue[],BorderImageSlices>[],BorderImageSlices[]>
parseBorderImageSliceLayers(final Term root) throws ParseException {
int nLayers = numberOfLayers(root);
ParsedValueImpl<ParsedValue[], BorderImageSlices>[] layers = new ParsedValueImpl[nLayers];
int layer = 0;
Term term = root;
while (term != null) {
layers[layer++] = parseBorderImageSlice(term);
term = nextLayer(term);
}
return new ParsedValueImpl<ParsedValue<ParsedValue[],BorderImageSlices>[],BorderImageSlices[]> (layers, SliceSequenceConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[], BorderWidths> parseBorderImageWidth(final Term root)
throws ParseException {
Term term = root;
if (term.token == null || !isSize(term.token))
error(term, "Expected \'<size>\'");
ParsedValueImpl<?,Size>[] insets = new ParsedValueImpl[4];
int inset = 0;
while (inset < 4 && term != null) {
insets[inset++] = parseSize(term);
if ((term = term.nextInSeries) != null &&
term.token != null &&
term.token.getType() == CssLexer.IDENT) {
}
}
if (inset < 2) insets[1] = insets[0];
if (inset < 3) insets[2] = insets[0];
if (inset < 4) insets[3] = insets[1];
return new ParsedValueImpl<ParsedValue[], BorderWidths>(insets, BorderImageWidthConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<ParsedValue[],BorderWidths>[],BorderWidths[]>
parseBorderImageWidthLayers(final Term root) throws ParseException {
int nLayers = numberOfLayers(root);
ParsedValueImpl<ParsedValue[], BorderWidths>[] layers = new ParsedValueImpl[nLayers];
int layer = 0;
Term term = root;
while (term != null) {
layers[layer++] = parseBorderImageWidth(term);
term = nextLayer(term);
}
return new ParsedValueImpl<ParsedValue<ParsedValue[],BorderWidths>[],BorderWidths[]> (layers, BorderImageWidthsSequenceConverter.getInstance());
}
private static final String SPECIAL_REGION_URL_PREFIX = "SPECIAL-REGION-URL:";
private ParsedValueImpl<String,String> parseRegion(Term root)
throws ParseException {
final String fn = (root.token != null) ? root.token.getText() : null;
if (!"region".regionMatches(true, 0, fn, 0, 6)) {
error(root,"Expected \'region\'");
}
Term arg = root.firstArg;
if (arg == null) error(root, "Expected \'region(\"<styleclass-or-id-string>\")\'");
if (arg.token == null ||
arg.token.getType() != CssLexer.STRING ||
arg.token.getText() == null ||
arg.token.getText().isEmpty()) error(root, "Expected \'region(\"<styleclass-or-id-string>\")\'");
final String styleClassOrId = SPECIAL_REGION_URL_PREFIX+ Utils.stripQuotes(arg.token.getText());
return new ParsedValueImpl<String,String>(styleClassOrId, StringConverter.getInstance());
}
private ParsedValueImpl<ParsedValue[],String> parseURI(Term root)
throws ParseException {
if (root == null) error(root, "Expected \'url(\"<uri-string>\")\'");
if (root.token == null ||
root.token.getType() != CssLexer.URL ||
root.token.getText() == null ||
root.token.getText().isEmpty()) error(root, "Expected \'url(\"<uri-string>\")\'");
final String uri = root.token.getText();
ParsedValueImpl[] uriValues = new ParsedValueImpl[] {
new ParsedValueImpl<String,String>(uri, StringConverter.getInstance()),
null
};
return new ParsedValueImpl<ParsedValue[],String>(uriValues, URLConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<ParsedValue[],String>[],String[]> parseURILayers(Term root)
throws ParseException {
int nLayers = numberOfLayers(root);
Term temp = root;
int layer = 0;
ParsedValueImpl<ParsedValue[],String>[] layers = new ParsedValueImpl[nLayers];
while(temp != null) {
layers[layer++] = parseURI(temp);
temp = nextLayer(temp);
}
return new ParsedValueImpl<ParsedValue<ParsedValue[],String>[],String[]>(layers, URLConverter.SequenceConverter.getInstance());
}
private ParsedValueImpl<ParsedValue<?,Size>,Number> parseFontSize(final Term root) throws ParseException {
if (root == null) return null;
final Token token = root.token;
if (token == null || !isSize(token)) error(root, "Expected \'<font-size>\'");
Size size = null;
if (token.getType() == CssLexer.IDENT) {
final String ident = token.getText().toLowerCase(Locale.ROOT);
double value = -1;
if ("inherit".equals(ident)) {
value = 100;
} else if ("xx-small".equals(ident)) {
value = 60;
} else if ("x-small".equals(ident)) {
value = 75;
} else if ("small".equals(ident)) {
value = 80;
} else if ("medium".equals(ident)) {
value = 100;
} else if ("large".equals(ident)) {
value = 120;
} else if ("x-large".equals(ident)) {
value = 150;
} else if ("xx-large".equals(ident)) {
value = 200;
} else if ("smaller".equals(ident)) {
value = 80;
} else if ("larger".equals(ident)) {
value = 120;
}
if (value > -1) {
size = new Size(value, SizeUnits.PERCENT);
}
}
if (size == null) {
size = size(token);
}
ParsedValueImpl<?,Size> svalue = new ParsedValueImpl<Size,Size>(size, null);
return new ParsedValueImpl<ParsedValue<?,Size>,Number>(svalue, FontConverter.FontSizeConverter.getInstance());
}
private ParsedValueImpl<String,FontPosture> parseFontStyle(Term root) throws ParseException {
if (root == null) return null;
final Token token = root.token;
if (token == null ||
token.getType() != CssLexer.IDENT ||
token.getText() == null ||
token.getText().isEmpty()) error(root, "Expected \'<font-style>\'");
final String ident = token.getText().toLowerCase(Locale.ROOT);
String posture = FontPosture.REGULAR.name();
if ("normal".equals(ident)) {
posture = FontPosture.REGULAR.name();
} else if ("italic".equals(ident)) {
posture = FontPosture.ITALIC.name();
} else if ("oblique".equals(ident)) {
posture = FontPosture.ITALIC.name();
} else if ("inherit".equals(ident)) {
posture = "inherit";
} else {
return null;
}
return new ParsedValueImpl<String,FontPosture>(posture, FontConverter.FontStyleConverter.getInstance());
}
private ParsedValueImpl<String, FontWeight> parseFontWeight(Term root) throws ParseException {
if (root == null) return null;
final Token token = root.token;
if (token == null ||
token.getText() == null ||
token.getText().isEmpty()) error(root, "Expected \'<font-weight>\'");
final String ident = token.getText().toLowerCase(Locale.ROOT);
String weight = FontWeight.NORMAL.name();
if ("inherit".equals(ident)) {
weight = FontWeight.NORMAL.name();
} else if ("normal".equals(ident)) {
weight = FontWeight.NORMAL.name();
} else if ("bold".equals(ident)) {
weight = FontWeight.BOLD.name();
} else if ("bolder".equals(ident)) {
weight = FontWeight.BOLD.name();
} else if ("lighter".equals(ident)) {
weight = FontWeight.LIGHT.name();
} else if ("100".equals(ident)) {
weight = FontWeight.findByWeight(100).name();
} else if ("200".equals(ident)) {
weight = FontWeight.findByWeight(200).name();
} else if ("300".equals(ident)) {
weight = FontWeight.findByWeight(300).name();
} else if ("400".equals(ident)) {
weight = FontWeight.findByWeight(400).name();
} else if ("500".equals(ident)) {
weight = FontWeight.findByWeight(500).name();
} else if ("600".equals(ident)) {
weight = FontWeight.findByWeight(600).name();
} else if ("700".equals(ident)) {
weight = FontWeight.findByWeight(700).name();
} else if ("800".equals(ident)) {
weight = FontWeight.findByWeight(800).name();
} else if ("900".equals(ident)) {
weight = FontWeight.findByWeight(900).name();
} else {
error(root, "Expected \'<font-weight>\'");
}
return new ParsedValueImpl<String,FontWeight>(weight, FontConverter.FontWeightConverter.getInstance());
}
private ParsedValueImpl<String,String> parseFontFamily(Term root) throws ParseException {
if (root == null) return null;
final Token token = root.token;
String text = null;
if (token == null ||
(token.getType() != CssLexer.IDENT &&
token.getType() != CssLexer.STRING) ||
(text = token.getText()) == null ||
text.isEmpty()) error(root, "Expected \'<font-family>\'");
final String fam = stripQuotes(text.toLowerCase(Locale.ROOT));
if ("inherit".equals(fam)) {
return new ParsedValueImpl<String,String>("inherit", StringConverter.getInstance());
} else if ("serif".equals(fam) ||
"sans-serif".equals(fam) ||
"cursive".equals(fam) ||
"fantasy".equals(fam) ||
"monospace".equals(fam)) {
return new ParsedValueImpl<String,String>(fam, StringConverter.getInstance());
} else {
return new ParsedValueImpl<String,String>(token.getText(), StringConverter.getInstance());
}
}
private ParsedValueImpl<ParsedValue[],Font> parseFont(Term root) throws ParseException {
Term next = root.nextInSeries;
root.nextInSeries = null;
while (next != null) {
Term temp = next.nextInSeries;
next.nextInSeries = root;
root = next;
next = temp;
}
Token token = root.token;
int ttype = token.getType();
if (ttype != CssLexer.IDENT &&
ttype != CssLexer.STRING) error(root, "Expected \'<font-family>\'");
ParsedValueImpl<String,String> ffamily = parseFontFamily(root);
Term term = root;
if ((term = term.nextInSeries) == null) error(root, "Expected \'<size>\'");
if (term.token == null || !isSize(term.token)) error(term, "Expected \'<size>\'");
Term temp;
if (((temp = term.nextInSeries) != null) &&
(temp.token != null && temp.token.getType() == CssLexer.SOLIDUS)) {
root = temp;
if ((term = temp.nextInSeries) == null) error(root, "Expected \'<size>\'");
if (term.token == null || !isSize(term.token)) error(term, "Expected \'<size>\'");
token = term.token;
}
ParsedValueImpl<ParsedValue<?,Size>,Number> fsize = parseFontSize(term);
if (fsize == null) error(root, "Expected \'<size>\'");
ParsedValueImpl<String,FontPosture> fstyle = null;
ParsedValueImpl<String,FontWeight> fweight = null;
String fvariant = null;
while ((term = term.nextInSeries) != null) {
if (term.token == null ||
term.token.getType() != CssLexer.IDENT ||
term.token.getText() == null ||
term.token.getText().isEmpty())
error(term, "Expected \'<font-weight>\', \'<font-style>\' or \'<font-variant>\'");
if (fstyle == null && ((fstyle = parseFontStyle(term)) != null)) {
;
} else if (fvariant == null && "small-caps".equalsIgnoreCase(term.token.getText())) {
fvariant = term.token.getText();
} else if (fweight == null && ((fweight = parseFontWeight(term)) != null)) {
;
}
}
ParsedValueImpl[] values = new ParsedValueImpl[]{ ffamily, fsize, fweight, fstyle };
return new ParsedValueImpl<ParsedValue[],Font>(values, FontConverter.getInstance());
}
Token currentToken = null;
private Token nextToken(CssLexer lexer) {
Token token = null;
do {
token = lexer.nextToken();
} while ((token != null) &&
(token.getType() == CssLexer.WS) ||
(token.getType() == CssLexer.NL));
if (LOGGER.isLoggable(Level.FINEST)) {
LOGGER.finest(token.toString());
}
return token;
}
private static Stack<String> imports;
private void parse(Stylesheet stylesheet, CssLexer lexer) {
currentToken = nextToken(lexer);
while((currentToken != null) &&
(currentToken.getType() == CssLexer.AT_KEYWORD)) {
currentToken = nextToken(lexer);
if (currentToken == null || currentToken.getType() != CssLexer.IDENT) {
ParseException parseException = new ParseException("Expected IDENT", currentToken, this);
final String msg = parseException.toString();
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
do {
currentToken = lexer.nextToken();
} while ((currentToken != null) &&
(currentToken.getType() == CssLexer.SEMI) ||
(currentToken.getType() == CssLexer.WS) ||
(currentToken.getType() == CssLexer.NL));
continue;
}
String keyword = currentToken.getText().toLowerCase(Locale.ROOT);
if ("font-face".equals(keyword)) {
FontFace fontFace = fontFace(lexer);
if (fontFace != null) stylesheet.getFontFaces().add(fontFace);
currentToken = nextToken(lexer);
continue;
} else if ("import".equals(keyword)) {
if (CssParser.imports == null) {
CssParser.imports = new Stack<>();
}
if (!imports.contains(sourceOfStylesheet)) {
imports.push(sourceOfStylesheet);
Stylesheet importedStylesheet = handleImport(lexer);
if (importedStylesheet != null) {
stylesheet.importStylesheet(importedStylesheet);
}
imports.pop();
if (CssParser.imports.isEmpty()) {
CssParser.imports = null;
}
} else {
final int line = currentToken.getLine();
final int pos = currentToken.getOffset();
final String msg =
MessageFormat.format("Recursive @import at {2} [{0,number,#},{1,number,#}]",
line, pos, imports.peek());
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
}
do {
currentToken = lexer.nextToken();
} while ((currentToken != null) &&
(currentToken.getType() == CssLexer.SEMI) ||
(currentToken.getType() == CssLexer.WS) ||
(currentToken.getType() == CssLexer.NL));
continue;
}
}
while ((currentToken != null) &&
(currentToken.getType() != Token.EOF)) {
List<Selector> selectors = selectors(lexer);
if (selectors == null) return;
if ((currentToken == null) ||
(currentToken.getType() != CssLexer.LBRACE)) {
final int line = currentToken != null ? currentToken.getLine() : -1;
final int pos = currentToken != null ? currentToken.getOffset() : -1;
final String msg =
MessageFormat.format("Expected LBRACE at [{0,number,#},{1,number,#}]",
line, pos);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
currentToken = null;
return;
}
currentToken = nextToken(lexer);
List<Declaration> declarations = declarations(lexer);
if (declarations == null) return;
if ((currentToken != null) &&
(currentToken.getType() != CssLexer.RBRACE)) {
final int line = currentToken.getLine();
final int pos = currentToken.getOffset();
final String msg =
MessageFormat.format("Expected RBRACE at [{0,number,#},{1,number,#}]",
line, pos);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
currentToken = null;
return;
}
stylesheet.getRules().add(new Rule(selectors, declarations));
currentToken = nextToken(lexer);
}
currentToken = null;
}
private FontFace fontFace(CssLexer lexer) {
final Map<String,String> descriptors = new HashMap<String,String>();
final List<FontFaceImpl.FontFaceSrc> sources = new ArrayList<FontFaceImpl.FontFaceSrc>();
while(true) {
currentToken = nextToken(lexer);
if (currentToken.getType() == CssLexer.IDENT) {
String key = currentToken.getText();
currentToken = nextToken(lexer);
currentToken = nextToken(lexer);
if ("src".equalsIgnoreCase(key)) {
while(true) {
if((currentToken != null) &&
(currentToken.getType() != CssLexer.SEMI) &&
(currentToken.getType() != CssLexer.RBRACE) &&
(currentToken.getType() != Token.EOF)) {
if (currentToken.getType() == CssLexer.IDENT) {
sources.add(new FontFaceImpl.FontFaceSrc(FontFaceImpl.FontFaceSrcType.REFERENCE,currentToken.getText()));
} else if (currentToken.getType() == CssLexer.URL) {
ParsedValueImpl[] uriValues = new ParsedValueImpl[] {
new ParsedValueImpl<String,String>(currentToken.getText(), StringConverter.getInstance()),
new ParsedValueImpl<String,String>(sourceOfStylesheet, null)
};
ParsedValue<ParsedValue[], String> parsedValue =
new ParsedValueImpl<ParsedValue[], String>(uriValues, URLConverter.getInstance());
String urlStr = parsedValue.convert(null);
URL url = null;
try {
URI fontUri = new URI(urlStr);
url = fontUri.toURL();
} catch (URISyntaxException | MalformedURLException malf) {
final int line = currentToken.getLine();
final int pos = currentToken.getOffset();
final String msg = MessageFormat.format("Could not resolve @font-face url [{2}] at [{0,number,#},{1,number,#}]", line, pos, urlStr);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
while(currentToken != null) {
int ttype = currentToken.getType();
if (ttype == CssLexer.RBRACE ||
ttype == Token.EOF) {
return null;
}
currentToken = nextToken(lexer);
}
}
String format = null;
while(true) {
currentToken = nextToken(lexer);
final int ttype = (currentToken != null) ? currentToken.getType() : Token.EOF;
if (ttype == CssLexer.FUNCTION) {
if ("format(".equalsIgnoreCase(currentToken.getText())) {
continue;
} else {
break;
}
} else if (ttype == CssLexer.IDENT ||
ttype == CssLexer.STRING) {
format = Utils.stripQuotes(currentToken.getText());
} else if (ttype == CssLexer.RPAREN) {
continue;
} else {
break;
}
}
sources.add(new FontFaceImpl.FontFaceSrc(FontFaceImpl.FontFaceSrcType.URL,url.toExternalForm(), format));
} else if (currentToken.getType() == CssLexer.FUNCTION) {
if ("local(".equalsIgnoreCase(currentToken.getText())) {
currentToken = nextToken(lexer);
final StringBuilder localSb = new StringBuilder();
while(true) {
if((currentToken != null) && (currentToken.getType() != CssLexer.RPAREN) &&
(currentToken.getType() != Token.EOF)) {
localSb.append(currentToken.getText());
} else {
break;
}
currentToken = nextToken(lexer);
}
int start = 0, end = localSb.length();
if (localSb.charAt(start) == '\'' || localSb.charAt(start) == '\"') start ++;
if (localSb.charAt(end-1) == '\'' || localSb.charAt(end-1) == '\"') end --;
final String local = localSb.substring(start,end);
sources.add(new FontFaceImpl.FontFaceSrc(FontFaceImpl.FontFaceSrcType.LOCAL,local));
} else {
final int line = currentToken.getLine();
final int pos = currentToken.getOffset();
final String msg = MessageFormat.format("Unknown @font-face src type [" + currentToken.getText() + ")] at [{0,number,#},{1,number,#}]", line, pos);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
}
} else if (currentToken.getType() == CssLexer.COMMA) {
} else {
final int line = currentToken.getLine();
final int pos = currentToken.getOffset();
final String msg = MessageFormat.format("Unexpected TOKEN [" + currentToken.getText() + "] at [{0,number,#},{1,number,#}]", line, pos);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
}
} else {
break;
}
currentToken = nextToken(lexer);
}
} else {
StringBuilder descriptorVal = new StringBuilder();
while(true) {
if((currentToken != null) && (currentToken.getType() != CssLexer.SEMI) &&
(currentToken.getType() != Token.EOF)) {
descriptorVal.append(currentToken.getText());
} else {
break;
}
currentToken = nextToken(lexer);
}
descriptors.put(key,descriptorVal.toString());
}
}
if ((currentToken == null) ||
(currentToken.getType() == CssLexer.RBRACE) ||
(currentToken.getType() == Token.EOF)) {
break;
}
}
return new FontFaceImpl(descriptors, sources);
}
private Stylesheet handleImport(CssLexer lexer) {
currentToken = nextToken(lexer);
if (currentToken == null || currentToken.getType() == Token.EOF) {
return null;
}
int ttype = currentToken.getType();
String fname = null;
if (ttype == CssLexer.STRING || ttype == CssLexer.URL) {
fname = currentToken.getText();
}
Stylesheet importedStylesheet = null;
final String _sourceOfStylesheet = sourceOfStylesheet;
if (fname != null) {
ParsedValueImpl[] uriValues = new ParsedValueImpl[] {
new ParsedValueImpl<String,String>(fname, StringConverter.getInstance()),
new ParsedValueImpl<String,String>(sourceOfStylesheet, null)
};
ParsedValue<ParsedValue[], String> parsedValue =
new ParsedValueImpl<ParsedValue[], String>(uriValues, URLConverter.getInstance());
String urlString = parsedValue.convert(null);
importedStylesheet = StyleManager.loadStylesheet(urlString);
sourceOfStylesheet = _sourceOfStylesheet;
}
if (importedStylesheet == null) {
final String msg =
MessageFormat.format("Could not import {0}", fname);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
}
return importedStylesheet;
}
private List<Selector> selectors(CssLexer lexer) {
List<Selector> selectors = new ArrayList<Selector>();
while(true) {
Selector selector = selector(lexer);
if (selector == null) {
while ((currentToken != null) &&
(currentToken.getType() != CssLexer.RBRACE) &&
(currentToken.getType() != Token.EOF)) {
currentToken = nextToken(lexer);
}
currentToken = nextToken(lexer);
if (currentToken == null || currentToken.getType() == Token.EOF) {
currentToken = null;
return null;
}
continue;
}
selectors.add(selector);
if ((currentToken != null) &&
(currentToken.getType() == CssLexer.COMMA)) {
currentToken = nextToken(lexer);
continue;
}
break;
}
return selectors;
}
private Selector selector(CssLexer lexer) {
List<Combinator> combinators = null;
List<SimpleSelector> sels = null;
SimpleSelector ancestor = simpleSelector(lexer);
if (ancestor == null) return null;
while (true) {
Combinator comb = combinator(lexer);
if (comb != null) {
if (combinators == null) {
combinators = new ArrayList<Combinator>();
}
combinators.add(comb);
SimpleSelector descendant = simpleSelector(lexer);
if (descendant == null) return null;
if (sels == null) {
sels = new ArrayList<SimpleSelector>();
sels.add(ancestor);
}
sels.add(descendant);
} else {
break;
}
}
if (currentToken != null && currentToken.getType() == CssLexer.NL) {
currentToken = nextToken(lexer);
}
if (sels == null) {
return ancestor;
} else {
return new CompoundSelector(sels,combinators);
}
}
private SimpleSelector simpleSelector(CssLexer lexer) {
String esel = "*";
String isel = "";
List<String> csels = null;
List<String> pclasses = null;
while (true) {
final int ttype =
(currentToken != null) ? currentToken.getType() : Token.INVALID;
switch(ttype) {
case CssLexer.STAR:
case CssLexer.IDENT:
esel = currentToken.getText();
break;
case CssLexer.DOT:
currentToken = nextToken(lexer);
if (currentToken != null &&
currentToken.getType() == CssLexer.IDENT) {
if (csels == null) {
csels = new ArrayList<String>();
}
csels.add(currentToken.getText());
} else {
currentToken = Token.INVALID_TOKEN;
return null;
}
break;
case CssLexer.HASH:
isel = currentToken.getText().substring(1);
break;
case CssLexer.COLON:
currentToken = nextToken(lexer);
if (currentToken != null && pclasses == null) {
pclasses = new ArrayList<String>();
}
if (currentToken.getType() == CssLexer.IDENT) {
pclasses.add(currentToken.getText());
} else if (currentToken.getType() == CssLexer.FUNCTION){
String pclass = functionalPseudo(lexer);
pclasses.add(pclass);
} else {
currentToken = Token.INVALID_TOKEN;
}
if (currentToken.getType() == Token.INVALID) {
return null;
}
break;
case CssLexer.NL:
case CssLexer.WS:
case CssLexer.COMMA:
case CssLexer.GREATER:
case CssLexer.LBRACE:
case Token.EOF:
return new SimpleSelector(esel, csels, pclasses, isel);
default:
return null;
}
currentToken = lexer.nextToken();
if (LOGGER.isLoggable(Level.FINEST)) {
LOGGER.finest(currentToken.toString());
}
}
}
private String functionalPseudo(CssLexer lexer) {
StringBuilder pclass = new StringBuilder(currentToken.getText());
while(true) {
currentToken = nextToken(lexer);
switch(currentToken.getType()) {
case CssLexer.STRING:
case CssLexer.IDENT:
pclass.append(currentToken.getText());
break;
case CssLexer.RPAREN:
pclass.append(')');
return pclass.toString();
default:
currentToken = Token.INVALID_TOKEN;
return null;
}
}
}
private Combinator combinator(CssLexer lexer) {
Combinator combinator = null;
while (true) {
final int ttype =
(currentToken != null) ? currentToken.getType() : Token.INVALID;
switch(ttype) {
case CssLexer.WS:
if (combinator == null && " ".equals(currentToken.getText())) {
combinator = Combinator.DESCENDANT;
}
break;
case CssLexer.GREATER:
combinator = Combinator.CHILD;
break;
case CssLexer.STAR:
case CssLexer.IDENT:
case CssLexer.DOT:
case CssLexer.HASH:
case CssLexer.COLON:
return combinator;
default:
return null;
}
currentToken = lexer.nextToken();
if (LOGGER.isLoggable(Level.FINEST)) {
LOGGER.finest(currentToken.toString());
}
}
}
private List<Declaration> declarations(CssLexer lexer) {
List<Declaration> declarations = new ArrayList<Declaration>();
while (true) {
Declaration decl = declaration(lexer);
if (decl != null) {
declarations.add(decl);
} else {
while ((currentToken != null) &&
(currentToken.getType() != CssLexer.SEMI) &&
(currentToken.getType() != CssLexer.RBRACE) &&
(currentToken.getType() != Token.EOF)) {
currentToken = nextToken(lexer);
}
if (currentToken != null &&
currentToken.getType() != CssLexer.SEMI)
return declarations;
}
while ((currentToken != null) &&
(currentToken.getType() == CssLexer.SEMI)) {
currentToken = nextToken(lexer);
}
if ((currentToken != null) &&
(currentToken.getType() == CssLexer.IDENT)) {
continue;
}
break;
}
return declarations;
}
private Declaration declaration(CssLexer lexer) {
final int ttype =
(currentToken != null) ? currentToken.getType() : Token.INVALID;
if ((currentToken == null) ||
(currentToken.getType() != CssLexer.IDENT)) {
return null;
}
String property = currentToken.getText();
currentToken = nextToken(lexer);
if ((currentToken == null) ||
(currentToken.getType() != CssLexer.COLON)) {
final int line = currentToken.getLine();
final int pos = currentToken.getOffset();
final String msg =
MessageFormat.format("Expected COLON at [{0,number,#},{1,number,#}]",
line, pos);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
return null;
}
currentToken = nextToken(lexer);
Term root = expr(lexer);
ParsedValueImpl value = null;
try {
value = (root != null) ? valueFor(property, root, lexer) : null;
} catch (ParseException re) {
Token badToken = re.tok;
final int line = badToken != null ? badToken.getLine() : -1;
final int pos = badToken != null ? badToken.getOffset() : -1;
final String msg =
MessageFormat.format("{2} while parsing ''{3}'' at [{0,number,#},{1,number,#}]",
line,pos,re.getMessage(),property);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
return null;
}
boolean important = currentToken.getType() == CssLexer.IMPORTANT_SYM;
if (important) currentToken = nextToken(lexer);
Declaration decl = (value != null)
? new Declaration(property.toLowerCase(Locale.ROOT), value, important) : null;
return decl;
}
private Term expr(CssLexer lexer) {
final Term expr = term(lexer);
Term current = expr;
while(true) {
final int ttype =
(current != null && currentToken != null)
? currentToken.getType() : Token.INVALID;
if (ttype == Token.INVALID) {
skipExpr(lexer);
return null;
} else if (ttype == CssLexer.SEMI ||
ttype == CssLexer.IMPORTANT_SYM ||
ttype == CssLexer.RBRACE ||
ttype == Token.EOF) {
return expr;
} else if (ttype == CssLexer.COMMA) {
currentToken = nextToken(lexer);
current = current.nextLayer = term(lexer);
} else {
current = current.nextInSeries = term(lexer);
}
}
}
private void skipExpr(CssLexer lexer) {
while(true) {
currentToken = nextToken(lexer);
final int ttype =
(currentToken != null) ? currentToken.getType() : Token.INVALID;
if (ttype == CssLexer.SEMI ||
ttype == CssLexer.RBRACE ||
ttype == Token.EOF) {
return;
}
}
}
private Term term(CssLexer lexer) {
final int ttype =
(currentToken != null) ? currentToken.getType() : Token.INVALID;
switch (ttype) {
case CssLexer.NUMBER:
case CssLexer.CM:
case CssLexer.EMS:
case CssLexer.EXS:
case CssLexer.IN:
case CssLexer.MM:
case CssLexer.PC:
case CssLexer.PT:
case CssLexer.PX:
case CssLexer.DEG:
case CssLexer.GRAD:
case CssLexer.RAD:
case CssLexer.TURN:
case CssLexer.PERCENTAGE:
case CssLexer.SECONDS:
case CssLexer.MS:
break;
case CssLexer.STRING:
break;
case CssLexer.IDENT:
break;
case CssLexer.HASH:
break;
case CssLexer.FUNCTION:
case CssLexer.LPAREN:
Term function = new Term(currentToken);
currentToken = nextToken(lexer);
Term arg = term(lexer);
function.firstArg = arg;
while(true) {
final int operator =
currentToken != null ? currentToken.getType() : Token.INVALID;
if (operator == CssLexer.RPAREN) {
currentToken = nextToken(lexer);
return function;
} else if (operator == CssLexer.COMMA) {
currentToken = nextToken(lexer);
arg = arg.nextArg = term(lexer);
} else {
arg = arg.nextInSeries = term(lexer);
}
}
case CssLexer.URL:
break;
case CssLexer.SOLIDUS:
break;
default:
final int line = currentToken != null ? currentToken.getLine() : -1;
final int pos = currentToken != null ? currentToken.getOffset() : -1;
final String text = currentToken != null ? currentToken.getText() : "";
final String msg =
MessageFormat.format("Unexpected token {0}{1}{0} at [{2,number,#},{3,number,#}]",
"\'",text,line,pos);
ParseError error = createError(msg);
if (LOGGER.isLoggable(Level.WARNING)) {
LOGGER.warning(error.toString());
}
reportError(error);
return null;
}
Term term = new Term(currentToken);
currentToken = nextToken(lexer);
return term;
}
public static ObservableList<ParseError> errorsProperty() {
return StyleManager.errorsProperty();
}
public static class ParseError {
public final String getMessage() {
return message;
}
public ParseError(String message) {
this.message = message;
}
final String message;
@Override public String toString() {
return "CSS Error: " + message;
}
public final static class StylesheetParsingError extends ParseError {
StylesheetParsingError(String url, String message) {
super(message);
this.url = url;
}
String getURL() {
return url;
}
private final String url;
@Override public String toString() {
final String path = url != null ? url : "?";
return "CSS Error parsing " + path + ": " + message;
}
}
public final static class InlineStyleParsingError extends ParseError {
InlineStyleParsingError(Styleable styleable, String message) {
super(message);
this.styleable = styleable;
}
Styleable getStyleable() {
return styleable;
}
private final Styleable styleable;
@Override public String toString() {
final String inlineStyle = styleable.getStyle();
final String source = styleable.toString();
return "CSS Error parsing in-line style \'" + inlineStyle +
"\' from " + source + ": " + message;
}
}
public final static class StringParsingError extends ParseError {
private final String style;
StringParsingError(String style, String message) {
super(message);
this.style = style;
}
String getStyle() {
return style;
}
@Override public String toString() {
return "CSS Error parsing \'" + style + ": " + message;
}
}
public final static class PropertySetError extends ParseError {
private final CssMetaData styleableProperty;
private final Styleable styleable;
public PropertySetError(CssMetaData styleableProperty,
Styleable styleable, String message) {
super(message);
this.styleableProperty = styleableProperty;
this.styleable = styleable;
}
Styleable getStyleable() {
return styleable;
}
CssMetaData getProperty() {
return styleableProperty;
}
@Override public String toString() {
return "CSS Error parsing \'" + styleableProperty + ": " + message;
}
}
}
}
