package com.sun.javafx.geom;
final class ChainEnd {
CurveLink head;
CurveLink tail;
ChainEnd partner;
int etag;
public ChainEnd(CurveLink first, ChainEnd partner) {
this.head = first;
this.tail = first;
this.partner = partner;
this.etag = first.getEdgeTag();
}
public CurveLink getChain() {
return head;
}
public void setOtherEnd(ChainEnd partner) {
this.partner = partner;
}
public ChainEnd getPartner() {
return partner;
}
public CurveLink linkTo(ChainEnd that) {
if (etag == AreaOp.ETAG_IGNORE ||
that.etag == AreaOp.ETAG_IGNORE)
{
throw new InternalError("ChainEnd linked more than once!");
}
if (etag == that.etag) {
throw new InternalError("Linking chains of the same type!");
}
ChainEnd enter, exit;
if (etag == AreaOp.ETAG_ENTER) {
enter = this;
exit = that;
} else {
enter = that;
exit = this;
}
etag = AreaOp.ETAG_IGNORE;
that.etag = AreaOp.ETAG_IGNORE;
enter.tail.setNext(exit.head);
enter.tail = exit.tail;
if (partner == that) {
return enter.head;
}
ChainEnd otherenter = exit.partner;
ChainEnd otherexit = enter.partner;
otherenter.partner = otherexit;
otherexit.partner = otherenter;
if (enter.head.getYTop() < otherenter.head.getYTop()) {
enter.tail.setNext(otherenter.head);
otherenter.head = enter.head;
} else {
otherexit.tail.setNext(enter.head);
otherexit.tail = enter.tail;
}
return null;
}
public void addLink(CurveLink newlink) {
if (etag == AreaOp.ETAG_ENTER) {
tail.setNext(newlink);
tail = newlink;
} else {
newlink.setNext(head);
head = newlink;
}
}
public double getX() {
if (etag == AreaOp.ETAG_ENTER) {
return tail.getXBot();
} else {
return head.getXBot();
}
}
}
