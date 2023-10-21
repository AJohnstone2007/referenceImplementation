package com.sun.marlin;
import static com.sun.marlin.ArrayCacheConst.ARRAY_SIZES;
import static com.sun.marlin.ArrayCacheConst.BUCKETS;
import static com.sun.marlin.ArrayCacheConst.MAX_ARRAY_SIZE;
import static com.sun.marlin.MarlinUtils.logInfo;
import static com.sun.marlin.MarlinUtils.logException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import com.sun.marlin.ArrayCacheConst.BucketStats;
import com.sun.marlin.ArrayCacheConst.CacheStats;
public final class DoubleArrayCache implements MarlinConst {
final boolean clean;
private final int bucketCapacity;
private WeakReference<Bucket[]> refBuckets = null;
final CacheStats stats;
DoubleArrayCache(final boolean clean, final int bucketCapacity) {
this.clean = clean;
this.bucketCapacity = bucketCapacity;
this.stats = (DO_STATS) ?
new CacheStats(getLogPrefix(clean) + "DoubleArrayCache") : null;
}
Bucket getCacheBucket(final int length) {
final int bucket = ArrayCacheConst.getBucket(length);
return getBuckets()[bucket];
}
private Bucket[] getBuckets() {
Bucket[] buckets = (refBuckets != null) ? refBuckets.get() : null;
if (buckets == null) {
buckets = new Bucket[BUCKETS];
for (int i = 0; i < BUCKETS; i++) {
buckets[i] = new Bucket(clean, ARRAY_SIZES[i], bucketCapacity,
(DO_STATS) ? stats.bucketStats[i] : null);
}
refBuckets = new WeakReference<Bucket[]>(buckets);
}
return buckets;
}
Reference createRef(final int initialSize) {
return new Reference(this, initialSize);
}
static final class Reference {
final double[] initial;
private final boolean clean;
private final DoubleArrayCache cache;
Reference(final DoubleArrayCache cache, final int initialSize) {
this.cache = cache;
this.clean = cache.clean;
this.initial = createArray(initialSize);
if (DO_STATS) {
cache.stats.totalInitial += initialSize;
}
}
double[] getArray(final int length) {
if (length <= MAX_ARRAY_SIZE) {
return cache.getCacheBucket(length).getArray();
}
if (DO_STATS) {
cache.stats.oversize++;
}
if (DO_LOG_OVERSIZE) {
logInfo(getLogPrefix(clean) + "DoubleArrayCache: "
+ "getArray[oversize]: length=\t" + length);
}
return createArray(length);
}
double[] widenArray(final double[] array, final int usedSize,
final int needSize)
{
final int length = array.length;
if (DO_CHECKS && length >= needSize) {
return array;
}
if (DO_STATS) {
cache.stats.resize++;
}
final double[] res = getArray(ArrayCacheConst.getNewSize(usedSize, needSize));
System.arraycopy(array, 0, res, 0, usedSize);
putArray(array, 0, usedSize);
if (DO_LOG_WIDEN_ARRAY) {
logInfo(getLogPrefix(clean) + "DoubleArrayCache: "
+ "widenArray[" + res.length
+ "]: usedSize=\t" + usedSize + "\tlength=\t" + length
+ "\tneeded length=\t" + needSize);
}
return res;
}
double[] putArray(final double[] array)
{
return putArray(array, 0, array.length);
}
double[] putArray(final double[] array, final int fromIndex,
final int toIndex)
{
if (array.length <= MAX_ARRAY_SIZE) {
if ((clean || DO_CLEAN_DIRTY) && (toIndex != 0)) {
fill(array, fromIndex, toIndex, 0.0d);
}
if (array != initial) {
cache.getCacheBucket(array.length).putArray(array);
}
}
return initial;
}
}
static final class Bucket {
private int tail = 0;
private final int arraySize;
private final boolean clean;
private final double[][] arrays;
private final BucketStats stats;
Bucket(final boolean clean, final int arraySize,
final int capacity, final BucketStats stats)
{
this.arraySize = arraySize;
this.clean = clean;
this.stats = stats;
this.arrays = new double[capacity][];
}
double[] getArray() {
if (DO_STATS) {
stats.getOp++;
}
if (tail != 0) {
final double[] array = arrays[--tail];
arrays[tail] = null;
return array;
}
if (DO_STATS) {
stats.createOp++;
}
return createArray(arraySize);
}
void putArray(final double[] array)
{
if (DO_CHECKS && (array.length != arraySize)) {
logInfo(getLogPrefix(clean) + "DoubleArrayCache: "
+ "bad length = " + array.length);
return;
}
if (DO_STATS) {
stats.returnOp++;
}
if (arrays.length > tail) {
arrays[tail++] = array;
if (DO_STATS) {
stats.updateMaxSize(tail);
}
} else if (DO_CHECKS) {
logInfo(getLogPrefix(clean) + "DoubleArrayCache: "
+ "array capacity exceeded !");
}
}
}
static double[] createArray(final int length) {
return new double[length];
}
static void fill(final double[] array, final int fromIndex,
final int toIndex, final double value)
{
Arrays.fill(array, fromIndex, toIndex, value);
if (DO_CHECKS) {
check(array, fromIndex, toIndex, value);
}
}
public static void check(final double[] array, final int fromIndex,
final int toIndex, final double value)
{
if (DO_CHECKS) {
for (int i = 0; i < array.length; i++) {
if (array[i] != value) {
logException("Invalid value at: " + i + " = " + array[i]
+ " from: " + fromIndex + " to: " + toIndex + "\n"
+ Arrays.toString(array), new Throwable());
Arrays.fill(array, value);
return;
}
}
}
}
static String getLogPrefix(final boolean clean) {
return (clean) ? "Clean" : "Dirty";
}
}
