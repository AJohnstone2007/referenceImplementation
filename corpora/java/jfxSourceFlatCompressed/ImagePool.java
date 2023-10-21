package com.sun.scenario.effect.impl;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.sun.scenario.effect.Filterable;
public class ImagePool {
public static long numEffects;
static long numCreated;
static long pixelsCreated;
static long numAccessed;
static long pixelsAccessed;
static {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged((PrivilegedAction) () -> {
if (System.getProperty("decora.showstats") != null) {
Runtime.getRuntime().addShutdownHook(new Thread() {
@Override public void run() {
printStats();
}
});
}
return null;
});
}
static void printStats() {
System.out.println("effects executed:  " + numEffects);
System.out.println("images created:    " + numCreated);
System.out.println("pixels created:    " + pixelsCreated);
System.out.println("images accessed:   " + numAccessed);
System.out.println("pixels accessed:   " + pixelsAccessed);
if (numEffects != 0) {
double avgImgs = ((double) numAccessed) / numEffects;
double avgPxls = ((double) pixelsAccessed) / numEffects;
System.out.println("images per effect: " + avgImgs);
System.out.println("pixels per effect: " + avgPxls);
}
}
static final int QUANT = 32;
private final List<SoftReference<PoolFilterable>> unlocked =
new ArrayList<SoftReference<PoolFilterable>>();
private final List<SoftReference<PoolFilterable>> locked =
new ArrayList<SoftReference<PoolFilterable>>();
private final boolean usePurgatory = Boolean.getBoolean("decora.purgatory");
private final List<Filterable> hardPurgatory = new ArrayList<Filterable>();
private final List<SoftReference<PoolFilterable>> softPurgatory =
new ArrayList<SoftReference<PoolFilterable>>();
ImagePool() {
}
public synchronized PoolFilterable checkOut(Renderer renderer, int w, int h) {
if (w <= 0 || h <= 0) {
w = h = 1;
}
w = ((w + QUANT - 1) / QUANT) * QUANT;
h = ((h + QUANT - 1) / QUANT) * QUANT;
w = renderer.getCompatibleWidth(w);
h = renderer.getCompatibleHeight(h);
numAccessed++;
pixelsAccessed += ((long) w) * h;
SoftReference<PoolFilterable> chosenEntry = null;
PoolFilterable chosenImage = null;
int mindiff = Integer.MAX_VALUE;
Iterator<SoftReference<PoolFilterable>> entries = unlocked.iterator();
while (entries.hasNext()) {
SoftReference<PoolFilterable> entry = entries.next();
PoolFilterable eimg = entry.get();
if (eimg == null) {
entries.remove();
continue;
}
int ew = eimg.getMaxContentWidth();
int eh = eimg.getMaxContentHeight();
if (ew >= w && eh >= h && ew * eh / 2 <= w * h) {
int diff = (ew-w) * (eh-h);
if (chosenEntry == null || diff < mindiff) {
eimg.lock();
if (eimg.isLost()) {
entries.remove();
continue;
}
if (chosenImage != null) {
chosenImage.unlock();
}
chosenEntry = entry;
chosenImage = eimg;
mindiff = diff;
}
}
}
if (chosenEntry != null) {
unlocked.remove(chosenEntry);
locked.add(chosenEntry);
renderer.clearImage(chosenImage);
return chosenImage;
}
entries = locked.iterator();
while (entries.hasNext()) {
SoftReference<PoolFilterable> entry = entries.next();
Filterable eimg = entry.get();
if (eimg == null) {
entries.remove();
}
}
PoolFilterable img = null;
try {
img = renderer.createCompatibleImage(w, h);
} catch (OutOfMemoryError e) {}
if (img == null) {
pruneCache();
try {
img = renderer.createCompatibleImage(w, h);
} catch (OutOfMemoryError e) {}
}
if (img != null) {
img.setImagePool(this);
locked.add(new SoftReference<PoolFilterable>(img));
numCreated++;
pixelsCreated += ((long) w) * h;
}
return img;
}
public synchronized void checkIn(PoolFilterable img) {
SoftReference<PoolFilterable> chosenEntry = null;
Filterable chosenImage = null;
Iterator<SoftReference<PoolFilterable>> entries = locked.iterator();
while (entries.hasNext()) {
SoftReference<PoolFilterable> entry = entries.next();
Filterable eimg = entry.get();
if (eimg == null) {
entries.remove();
} else if (eimg == img) {
chosenEntry = entry;
chosenImage = eimg;
img.unlock();
break;
}
}
if (chosenEntry != null) {
locked.remove(chosenEntry);
if (usePurgatory) {
hardPurgatory.add(chosenImage);
softPurgatory.add(chosenEntry);
} else {
unlocked.add(chosenEntry);
}
}
}
public synchronized void releasePurgatory() {
if (usePurgatory && !softPurgatory.isEmpty()) {
unlocked.addAll(softPurgatory);
softPurgatory.clear();
hardPurgatory.clear();
}
}
private void pruneCache() {
for (SoftReference<PoolFilterable> r : unlocked) {
Filterable image = r.get();
if (image != null) {
image.flush();
}
}
unlocked.clear();
System.gc();
System.runFinalization();
System.gc();
System.runFinalization();
}
public synchronized void dispose() {
for (SoftReference<PoolFilterable> r : unlocked) {
Filterable image = r.get();
if (image != null) {
image.flush();
}
}
unlocked.clear();
locked.clear();
}
}
