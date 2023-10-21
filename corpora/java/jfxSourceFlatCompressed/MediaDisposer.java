package com.sun.media.jfxmediaimpl;
import com.sun.media.jfxmedia.logging.Logger;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class MediaDisposer {
public static interface Disposable {
public void dispose();
}
public static interface ResourceDisposer {
public void disposeResource(Object resource);
}
public static void addResourceDisposer(Object referent, Object resource, ResourceDisposer disposer) {
disposinator().implAddResourceDisposer(referent, resource, disposer);
}
public static void removeResourceDisposer(Object resource) {
disposinator().implRemoveResourceDisposer(resource);
}
public static void addDisposable(Object referent, Disposable disposable) {
disposinator().implAddDisposable(referent, disposable);
}
private final ReferenceQueue<Object> purgatory;
private final Map<Reference,Disposable> disposers;
private static MediaDisposer theDisposinator;
private static synchronized MediaDisposer disposinator() {
if (null == theDisposinator) {
theDisposinator = new MediaDisposer();
Thread disposerThread = new Thread(
() -> {
theDisposinator.disposerLoop();
},
"Media Resource Disposer");
disposerThread.setDaemon(true);
disposerThread.start();
}
return theDisposinator;
}
private MediaDisposer() {
purgatory = new ReferenceQueue();
disposers = new HashMap<Reference,Disposable>();
}
private void disposerLoop() {
while (true) {
try {
Reference denizen = purgatory.remove();
Disposable disposer;
synchronized (disposers) {
disposer = disposers.remove(denizen);
}
denizen.clear();
if (null != disposer) {
disposer.dispose();
}
denizen = null;
disposer = null;
} catch (InterruptedException ex) {
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, MediaDisposer.class.getName(),
"disposerLoop", "Disposer loop interrupted, terminating");
}
}
}
}
private void implAddResourceDisposer(Object referent, Object resource, ResourceDisposer disposer) {
Reference denizen = new PhantomReference(referent, purgatory);
synchronized (disposers) {
disposers.put(denizen, new ResourceDisposerRecord(resource, disposer));
}
}
private void implRemoveResourceDisposer(Object resource) {
Reference resourceKey = null;
synchronized (disposers) {
for (Map.Entry<Reference, Disposable> entry : disposers.entrySet()) {
Disposable disposer = entry.getValue();
if (disposer instanceof ResourceDisposerRecord) {
ResourceDisposerRecord rd = (ResourceDisposerRecord)disposer;
if (rd.resource.equals(resource)) {
resourceKey = entry.getKey();
break;
}
}
}
if (null != resourceKey) {
disposers.remove(resourceKey);
}
}
}
private void implAddDisposable(Object referent, Disposable disposer) {
Reference denizen = new PhantomReference(referent, purgatory);
synchronized (disposers) {
disposers.put(denizen, disposer);
}
}
private static class ResourceDisposerRecord implements Disposable {
Object resource;
ResourceDisposer disposer;
public ResourceDisposerRecord(Object resource, ResourceDisposer disposer) {
this.resource = resource;
this.disposer = disposer;
}
public void dispose() {
disposer.disposeResource(resource);
}
}
}
