package com.sun.webkit.dom;
import org.w3c.dom.html.HTMLTitleElement;
public class HTMLTitleElementImpl extends HTMLElementImpl implements HTMLTitleElement {
HTMLTitleElementImpl(long peer) {
super(peer);
}
static HTMLTitleElement getImpl(long peer) {
return (HTMLTitleElement)create(peer);
}
public String getText() {
return getTextImpl(getPeer());
}
native static String getTextImpl(long peer);
public void setText(String value) {
setTextImpl(getPeer(), value);
}
native static void setTextImpl(long peer, String value);
}
