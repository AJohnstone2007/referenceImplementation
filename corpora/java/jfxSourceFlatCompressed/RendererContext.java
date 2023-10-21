package com.sun.marlin;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Rectangle;
import com.sun.marlin.ArrayCacheConst.CacheStats;
import com.sun.marlin.TransformingPathConsumer2D.CurveBasicMonotonizer;
import com.sun.marlin.TransformingPathConsumer2D.CurveClipSplitter;
import com.sun.util.reentrant.ReentrantContext;
public final class RendererContext extends ReentrantContext implements MarlinConst {
private static final AtomicInteger CTX_COUNT = new AtomicInteger(1);
public static RendererContext createContext() {
return new RendererContext("ctx"
+ Integer.toString(CTX_COUNT.getAndIncrement()));
}
private final Object cleanerObj;
public boolean dirty = false;
public final float[] float6 = new float[6];
final Curve curve = new Curve();
public final TransformingPathConsumer2D transformerPC2D;
private WeakReference<Path2D> refPath2D = null;
public final Renderer renderer;
public final Stroker stroker;
public final CollinearSimplifier simplifier = new CollinearSimplifier();
public final PathSimplifier pathSimplifier = new PathSimplifier();
public final Dasher dasher;
int stroking = 0;
public boolean doClip = false;
boolean closedPath = false;
public final double[] clipRect = new double[4];
public double clipInvScale = 0.0d;
public final CurveBasicMonotonizer monotonizer;
final CurveClipSplitter curveClipSplitter;
final RendererSharedMemory rdrMem;
private RendererNoAA rendererNoAA = null;
public final Rectangle clip = new Rectangle();
public MaskMarlinAlphaConsumer consumer = null;
private final IntArrayCache cleanIntCache = new IntArrayCache(true, 5);
private final IntArrayCache dirtyIntCache = new IntArrayCache(false, 5);
private final DoubleArrayCache dirtyDoubleCache = new DoubleArrayCache(false, 4);
private final ByteArrayCache dirtyByteCache = new ByteArrayCache(false, 2);
final RendererStats stats;
RendererContext(final String name) {
if (LOG_CREATE_CONTEXT) {
MarlinUtils.logInfo("new RendererContext = " + name);
}
this.cleanerObj = new Object();
if (DO_STATS || DO_MONITORS) {
stats = RendererStats.createInstance(cleanerObj, name);
stats.cacheStats = new CacheStats[] { cleanIntCache.stats,
dirtyIntCache.stats, dirtyDoubleCache.stats, dirtyByteCache.stats
};
} else {
stats = null;
}
monotonizer = new CurveBasicMonotonizer(this);
curveClipSplitter = new CurveClipSplitter(this);
transformerPC2D = new TransformingPathConsumer2D(this);
rdrMem = new RendererSharedMemory(this);
renderer = new Renderer(this);
stroker = new Stroker(this);
dasher = new Dasher(this);
}
public void dispose() {
if (DO_STATS) {
if (stats.totalOffHeap > stats.totalOffHeapMax) {
stats.totalOffHeapMax = stats.totalOffHeap;
}
stats.totalOffHeap = 0L;
}
stroking = 0;
doClip = false;
closedPath = false;
clipInvScale = 0.0d;
if (dirty) {
this.dasher.dispose();
this.stroker.dispose();
dirty = false;
}
}
public Path2D getPath2D() {
Path2D p2d = (refPath2D != null) ? refPath2D.get() : null;
if (p2d == null) {
p2d = new Path2D(WIND_NON_ZERO, INITIAL_EDGES_COUNT);
refPath2D = new WeakReference<Path2D>(p2d);
}
p2d.reset();
return p2d;
}
public RendererNoAA getRendererNoAA() {
if (rendererNoAA == null) {
rendererNoAA = new RendererNoAA(this);
}
return rendererNoAA;
}
OffHeapArray newOffHeapArray(final long initialSize) {
if (DO_STATS) {
stats.totalOffHeapInitial += initialSize;
}
return new OffHeapArray(cleanerObj, initialSize);
}
IntArrayCache.Reference newCleanIntArrayRef(final int initialSize) {
return cleanIntCache.createRef(initialSize);
}
IntArrayCache.Reference newDirtyIntArrayRef(final int initialSize) {
return dirtyIntCache.createRef(initialSize);
}
DoubleArrayCache.Reference newDirtyDoubleArrayRef(final int initialSize) {
return dirtyDoubleCache.createRef(initialSize);
}
ByteArrayCache.Reference newDirtyByteArrayRef(final int initialSize) {
return dirtyByteCache.createRef(initialSize);
}
static final class RendererSharedMemory {
final OffHeapArray edges;
final IntArrayCache.Reference edgeBuckets_ref;
final IntArrayCache.Reference edgeBucketCounts_ref;
final IntArrayCache.Reference alphaLine_ref;
final IntArrayCache.Reference crossings_ref;
final IntArrayCache.Reference edgePtrs_ref;
final IntArrayCache.Reference aux_crossings_ref;
final IntArrayCache.Reference aux_edgePtrs_ref;
final IntArrayCache.Reference blkFlags_ref;
RendererSharedMemory(final RendererContext rdrCtx) {
edges = rdrCtx.newOffHeapArray(INITIAL_EDGES_CAPACITY);
edgeBuckets_ref = rdrCtx.newCleanIntArrayRef(INITIAL_BUCKET_ARRAY);
edgeBucketCounts_ref = rdrCtx.newCleanIntArrayRef(INITIAL_BUCKET_ARRAY);
alphaLine_ref = rdrCtx.newCleanIntArrayRef(INITIAL_AA_ARRAY);
crossings_ref = rdrCtx.newDirtyIntArrayRef(INITIAL_CROSSING_COUNT);
aux_crossings_ref = rdrCtx.newDirtyIntArrayRef(INITIAL_CROSSING_COUNT);
edgePtrs_ref = rdrCtx.newDirtyIntArrayRef(INITIAL_CROSSING_COUNT);
aux_edgePtrs_ref = rdrCtx.newDirtyIntArrayRef(INITIAL_CROSSING_COUNT);
blkFlags_ref = rdrCtx.newCleanIntArrayRef(INITIAL_ARRAY);
}
}
}
