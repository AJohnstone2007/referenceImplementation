package com.sun.marlin;
import java.util.Arrays;
import static com.sun.marlin.MarlinUtils.logInfo;
public final class ArrayCacheConst implements MarlinConst {
static final int BUCKETS = 8;
static final int MIN_ARRAY_SIZE = 4096;
static final int MAX_ARRAY_SIZE;
static final int THRESHOLD_SMALL_ARRAY_SIZE = 4 * 1024 * 1024;
static final int THRESHOLD_ARRAY_SIZE;
static final long THRESHOLD_HUGE_ARRAY_SIZE;
static final int[] ARRAY_SIZES = new int[BUCKETS];
static {
int arraySize = MIN_ARRAY_SIZE;
int inc_lg = 2;
for (int i = 0; i < BUCKETS; i++, arraySize <<= inc_lg) {
ARRAY_SIZES[i] = arraySize;
if (DO_TRACE) {
logInfo("arraySize[" + i + "]: " + arraySize);
}
if (arraySize >= THRESHOLD_SMALL_ARRAY_SIZE) {
inc_lg = 1;
}
}
MAX_ARRAY_SIZE = arraySize >> inc_lg;
if (MAX_ARRAY_SIZE <= 0) {
throw new IllegalStateException("Invalid max array size !");
}
THRESHOLD_ARRAY_SIZE = 16 * 1024 * 1024;
THRESHOLD_HUGE_ARRAY_SIZE = 48L * 1024 * 1024;
if (DO_STATS || DO_MONITORS) {
logInfo("ArrayCache.BUCKETS        = " + BUCKETS);
logInfo("ArrayCache.MIN_ARRAY_SIZE = " + MIN_ARRAY_SIZE);
logInfo("ArrayCache.MAX_ARRAY_SIZE = " + MAX_ARRAY_SIZE);
logInfo("ArrayCache.ARRAY_SIZES = "
+ Arrays.toString(ARRAY_SIZES));
logInfo("ArrayCache.THRESHOLD_ARRAY_SIZE = "
+ THRESHOLD_ARRAY_SIZE);
logInfo("ArrayCache.THRESHOLD_HUGE_ARRAY_SIZE = "
+ THRESHOLD_HUGE_ARRAY_SIZE);
}
}
private ArrayCacheConst() {
}
static int getBucket(final int length) {
for (int i = 0; i < ARRAY_SIZES.length; i++) {
if (length <= ARRAY_SIZES[i]) {
return i;
}
}
return -1;
}
public static int getNewSize(final int curSize, final int needSize) {
if (needSize < 0) {
throw new ArrayIndexOutOfBoundsException(
"array exceeds maximum capacity !");
}
assert curSize >= 0;
final int initial = curSize;
int size;
if (initial > THRESHOLD_ARRAY_SIZE) {
size = initial + (initial >> 1);
} else {
size = (initial << 1);
}
if (size < needSize) {
size = ((needSize >> 12) + 1) << 12;
}
if (size < 0) {
size = Integer.MAX_VALUE;
}
return size;
}
public static long getNewLargeSize(final long curSize, final long needSize) {
if ((needSize >> 31L) != 0L) {
throw new ArrayIndexOutOfBoundsException(
"array exceeds maximum capacity !");
}
assert curSize >= 0L;
long size;
if (curSize > THRESHOLD_HUGE_ARRAY_SIZE) {
size = curSize + (curSize >> 2L);
} else if (curSize > THRESHOLD_ARRAY_SIZE) {
size = curSize + (curSize >> 1L);
} else if (curSize > THRESHOLD_SMALL_ARRAY_SIZE) {
size = (curSize << 1L);
} else {
size = (curSize << 2L);
}
if (size < needSize) {
size = ((needSize >> 12L) + 1L) << 12L;
}
if (size > Integer.MAX_VALUE) {
size = Integer.MAX_VALUE;
}
return size;
}
static final class CacheStats {
final String name;
final BucketStats[] bucketStats;
int resize = 0;
int oversize = 0;
long totalInitial = 0L;
CacheStats(final String name) {
this.name = name;
bucketStats = new BucketStats[BUCKETS];
for (int i = 0; i < BUCKETS; i++) {
bucketStats[i] = new BucketStats();
}
}
void reset() {
resize = 0;
oversize = 0;
for (int i = 0; i < BUCKETS; i++) {
bucketStats[i].reset();
}
}
long dumpStats() {
long totalCacheBytes = 0L;
if (DO_STATS) {
for (int i = 0; i < BUCKETS; i++) {
final BucketStats s = bucketStats[i];
if (s.maxSize != 0) {
totalCacheBytes += getByteFactor()
* (s.maxSize * ARRAY_SIZES[i]);
}
}
if (totalInitial != 0L || totalCacheBytes != 0L
|| resize != 0 || oversize != 0)
{
logInfo(name + ": resize: " + resize
+ " - oversize: " + oversize
+ " - initial: " + getTotalInitialBytes()
+ " bytes (" + totalInitial + " elements)"
+ " - cache: " + totalCacheBytes + " bytes"
);
}
if (totalCacheBytes != 0L) {
logInfo(name + ": usage stats:");
for (int i = 0; i < BUCKETS; i++) {
final BucketStats s = bucketStats[i];
if (s.getOp != 0) {
logInfo("  Bucket[" + ARRAY_SIZES[i] + "]: "
+ "get: " + s.getOp
+ " - put: " + s.returnOp
+ " - create: " + s.createOp
+ " :: max size: " + s.maxSize
);
}
}
}
}
return totalCacheBytes;
}
private int getByteFactor() {
int factor = 1;
if (name.contains("Int") || name.contains("Float")) {
factor = 4;
} else if (name.contains("Double")) {
factor = 8;
}
return factor;
}
long getTotalInitialBytes() {
return getByteFactor() * totalInitial;
}
}
static final class BucketStats {
int getOp = 0;
int createOp = 0;
int returnOp = 0;
int maxSize = 0;
void reset() {
getOp = 0;
createOp = 0;
returnOp = 0;
maxSize = 0;
}
void updateMaxSize(final int size) {
if (size > maxSize) {
maxSize = size;
}
}
}
}
