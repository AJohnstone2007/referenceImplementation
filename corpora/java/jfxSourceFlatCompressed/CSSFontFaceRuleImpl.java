package com.sun.webkit.dom;
import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSStyleDeclaration;
public class CSSFontFaceRuleImpl extends CSSRuleImpl implements CSSFontFaceRule {
CSSFontFaceRuleImpl(long peer) {
super(peer);
}
static CSSFontFaceRule getImpl(long peer) {
return (CSSFontFaceRule)create(peer);
}
public CSSStyleDeclaration getStyle() {
return CSSStyleDeclarationImpl.getImpl(getStyleImpl(getPeer()));
}
native static long getStyleImpl(long peer);
}
