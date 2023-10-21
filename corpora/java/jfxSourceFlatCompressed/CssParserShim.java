package javafx.css;
public class CssParserShim {
CssParser parser;
public CssParserShim(CssParser parser) {
this.parser = parser;
}
public CssParserShim() {
this.parser = new CssParser();
}
public ParsedValue parseExpr(String property, String expr) {
return parser.parseExpr(property, expr);
}
}
