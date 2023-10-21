package com.sun.webkit.dom;
import com.sun.webkit.Disposer;
import com.sun.webkit.DisposerRecord;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
final class EventListenerImpl implements EventListener {
private static final Map<EventListener, Long> EL2peer =
new WeakHashMap<EventListener, Long>();
private static final Map<Long, WeakReference<EventListener>> peer2EL =
new HashMap<Long, WeakReference<EventListener>>();
private static final class SelfDisposer implements DisposerRecord {
private final long peer;
private SelfDisposer(final long peer) {
this.peer = peer;
}
public void dispose() {
EventListenerImpl.dispose(peer);
EventListenerImpl.twkDisposeJSPeer(peer);
}
}
private final EventListener eventListener;
private final long jsPeer;
static long getPeer(EventListener eventListener) {
if (eventListener == null) {
return 0L;
}
Long peer = EL2peer.get(eventListener);
if (peer != null) {
return peer;
}
EventListenerImpl eli = new EventListenerImpl(eventListener, 0L);
peer = eli.twkCreatePeer();
EL2peer.put(eventListener, peer);
peer2EL.put(peer, new WeakReference<EventListener>(eventListener));
return peer;
}
private native long twkCreatePeer();
private static EventListener getELfromPeer(long peer) {
WeakReference<EventListener> wr = peer2EL.get(peer);
return wr == null ? null : wr.get();
}
static EventListener getImpl(long peer) {
if (peer == 0)
return null;
EventListener ev = getELfromPeer(peer);
if (ev != null) {
twkDisposeJSPeer(peer);
return ev;
}
EventListener el = new EventListenerImpl(null, peer);
EL2peer.put(el, peer);
peer2EL.put(peer, new WeakReference<EventListener>(el));
Disposer.addRecord(el, new SelfDisposer(peer));
return el;
}
public void handleEvent(Event evt) {
if (jsPeer != 0L && (evt instanceof EventImpl)) {
twkDispatchEvent(jsPeer, ((EventImpl)evt).getPeer() );
}
}
private native static void twkDispatchEvent(long eventListenerPeer, long eventPeer);
private EventListenerImpl(EventListener eventListener, long jsPeer) {
this.eventListener = eventListener;
this.jsPeer = jsPeer;
}
private static void dispose(long peer) {
EventListener ev = getELfromPeer(peer);
if (ev != null )
EL2peer.remove(ev);
peer2EL.remove(peer);
}
private native static void twkDisposeJSPeer(long peer);
private void fwkHandleEvent(long eventPeer) {
eventListener.handleEvent(EventImpl.getImpl(eventPeer));
}
}
