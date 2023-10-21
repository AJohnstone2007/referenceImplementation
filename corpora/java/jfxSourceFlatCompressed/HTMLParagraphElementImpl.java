package com.sun.webkit.dom;
import org.w3c.dom.html.HTMLParagraphElement;
public class HTMLParagraphElementImpl extends HTMLElementImpl implements HTMLParagraphElement {
HTMLParagraphElementImpl(long peer) {
super(peer);
}
static HTMLParagraphElement getImpl(long peer) {
return (HTMLParagraphElement)create(peer);
}
public String getAlign() {
return getAlignImpl(getPeer());
}
native static String getAlignImpl(long peer);
public void setAlign(String value) {
setAlignImpl(getPeer(), value);
}
native static void setAlignImpl(long peer, String value);
}
