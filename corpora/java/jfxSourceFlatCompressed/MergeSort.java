package com.sun.marlin;
final class MergeSort {
public static final int INSERTION_SORT_THRESHOLD = 14;
static void mergeSortNoCopy(final int[] x, final int[] y,
final int[] auxX, final int[] auxY,
final int toIndex,
final int insertionSortIndex)
{
if ((toIndex > x.length) || (toIndex > y.length)
|| (toIndex > auxX.length) || (toIndex > auxY.length)) {
throw new ArrayIndexOutOfBoundsException("bad arguments: toIndex="
+ toIndex);
}
mergeSort(x, y, x, auxX, y, auxY, insertionSortIndex, toIndex);
if ((insertionSortIndex == 0)
|| (auxX[insertionSortIndex - 1] <= auxX[insertionSortIndex])) {
System.arraycopy(auxX, 0, x, 0, toIndex);
System.arraycopy(auxY, 0, y, 0, toIndex);
return;
}
for (int i = 0, p = 0, q = insertionSortIndex; i < toIndex; i++) {
if ((q >= toIndex) || ((p < insertionSortIndex)
&& (auxX[p] <= auxX[q]))) {
x[i] = auxX[p];
y[i] = auxY[p];
p++;
} else {
x[i] = auxX[q];
y[i] = auxY[q];
q++;
}
}
}
private static void mergeSort(final int[] refX, final int[] refY,
final int[] srcX, final int[] dstX,
final int[] srcY, final int[] dstY,
final int low, final int high)
{
final int length = high - low;
if (length <= INSERTION_SORT_THRESHOLD) {
dstX[low] = refX[low];
dstY[low] = refY[low];
for (int i = low + 1, j = low, x, y; i < high; j = i++) {
x = refX[i];
y = refY[i];
while (dstX[j] > x) {
dstX[j + 1] = dstX[j];
dstY[j + 1] = dstY[j];
if (j-- == low) {
break;
}
}
dstX[j + 1] = x;
dstY[j + 1] = y;
}
return;
}
final int mid = (low + high) >> 1;
mergeSort(refX, refY, dstX, srcX, dstY, srcY, low, mid);
mergeSort(refX, refY, dstX, srcX, dstY, srcY, mid, high);
if (srcX[high - 1] <= srcX[low]) {
final int left = mid - low;
final int right = high - mid;
final int off = (left != right) ? 1 : 0;
System.arraycopy(srcX, low, dstX, mid + off, left);
System.arraycopy(srcX, mid, dstX, low, right);
System.arraycopy(srcY, low, dstY, mid + off, left);
System.arraycopy(srcY, mid, dstY, low, right);
return;
}
if (srcX[mid - 1] <= srcX[mid]) {
System.arraycopy(srcX, low, dstX, low, length);
System.arraycopy(srcY, low, dstY, low, length);
return;
}
for (int i = low, p = low, q = mid; i < high; i++) {
if ((q >= high) || ((p < mid) && (srcX[p] <= srcX[q]))) {
dstX[i] = srcX[p];
dstY[i] = srcY[p];
p++;
} else {
dstX[i] = srcX[q];
dstY[i] = srcY[q];
q++;
}
}
}
private MergeSort() {
}
}
