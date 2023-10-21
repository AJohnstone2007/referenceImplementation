package com.sun.webkit.dom;
import org.w3c.dom.html.HTMLHeadingElement;
public class HTMLHeadingElementImpl extends HTMLElementImpl implements HTMLHeadingElement {
HTMLHeadingElementImpl(long peer) {
super(peer);
}
static HTMLHeadingElement getImpl(long peer) {
return (HTMLHeadingElement)create(peer);
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
