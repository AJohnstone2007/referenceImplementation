package com.sun.webkit.dom;
import org.w3c.dom.EntityReference;
public class EntityReferenceImpl extends NodeImpl implements EntityReference {
EntityReferenceImpl(long peer) {
super(peer);
}
static EntityReference getImpl(long peer) {
return (EntityReference)create(peer);
}
}
