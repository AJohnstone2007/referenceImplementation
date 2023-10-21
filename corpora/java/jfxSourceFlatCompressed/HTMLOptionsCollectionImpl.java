package com.sun.webkit.dom;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLOptionElement;
public class HTMLOptionsCollectionImpl extends HTMLCollectionImpl {
HTMLOptionsCollectionImpl(long peer) {
super(peer);
}
static HTMLOptionsCollectionImpl getImpl(long peer) {
return (HTMLOptionsCollectionImpl)create(peer);
}
public int getSelectedIndex() {
return getSelectedIndexImpl(getPeer());
}
native static int getSelectedIndexImpl(long peer);
public void setSelectedIndex(int value) {
setSelectedIndexImpl(getPeer(), value);
}
native static void setSelectedIndexImpl(long peer, int value);
public int getLength() {
return getLengthImpl(getPeer());
}
native static int getLengthImpl(long peer);
public void setLength(int value) throws DOMException {
setLengthImpl(getPeer(), value);
}
native static void setLengthImpl(long peer, int value);
public Node namedItem(String name)
{
return NodeImpl.getImpl(namedItemImpl(getPeer()
, name));
}
native static long namedItemImpl(long peer
, String name);
public void add(HTMLOptionElement option
, int index) throws DOMException
{
addImpl(getPeer()
, HTMLOptionElementImpl.getPeer(option)
, index);
}
native static void addImpl(long peer
, long option
, int index);
public Node item(int index)
{
return NodeImpl.getImpl(itemImpl(getPeer()
, index));
}
native static long itemImpl(long peer
, int index);
}
