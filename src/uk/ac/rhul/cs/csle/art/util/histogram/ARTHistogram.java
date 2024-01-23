package uk.ac.rhul.cs.csle.art.util.histogram;

public class ARTHistogram {

  class HistogramNode {
    public long bucket;
    public long value;
    HistogramNode next;
  };

  private final HistogramNode base;

  public ARTHistogram() {
    base = new HistogramNode();
    base.bucket = base.value = 0;

    base.next = new HistogramNode();
    base.next.bucket = Long.MAX_VALUE;
    base.next.value = 0;
    base.next.next = null;
  }

  public void update(long value) {
    HistogramNode currentHistogramNode = base;
    HistogramNode previousHistogramNode;
    HistogramNode newHistogramNode;

    do {
      if (currentHistogramNode.bucket == value) {
        currentHistogramNode.value++;
        return;
      }

      previousHistogramNode = currentHistogramNode;
      currentHistogramNode = currentHistogramNode.next;
    } while (currentHistogramNode.bucket <= value);

    newHistogramNode = new HistogramNode();
    newHistogramNode.bucket = value;
    newHistogramNode.value = 1;
    newHistogramNode.next = currentHistogramNode;
    previousHistogramNode.next = newHistogramNode;
  }

  @Override
  public String toString() {
    String ret = "";

    long cardinality = weightedSumBuckets();

    HistogramNode currentHistogramNode = base;

    while (currentHistogramNode.next != null) {
      if (currentHistogramNode.bucket == 0)
        ret += String.format("%d:%d ", currentHistogramNode.bucket, currentHistogramNode.value);
      else
        ret += String.format("%d:%d(%.2f%%) ", currentHistogramNode.bucket, currentHistogramNode.value,
            (currentHistogramNode.bucket == 0 ? -1.0 : (100 * (double) currentHistogramNode.value)) / (cardinality == 0 ? 1.0 : (double) cardinality));

      currentHistogramNode = currentHistogramNode.next;
    }

    return ret;
  }

  public long bucketValue(long bucket) {
    HistogramNode currentHistogramNode = base;

    while (currentHistogramNode.next != null && currentHistogramNode.bucket != bucket)
      currentHistogramNode = currentHistogramNode.next;

    return currentHistogramNode.bucket == bucket ? currentHistogramNode.value : 0;
  }

  public long countNonemptyBuckets() {
    HistogramNode currentHistogramNode = base;
    long buckets = 0;

    while (currentHistogramNode.next != null) {
      if (currentHistogramNode.value != 0) buckets++;

      currentHistogramNode = currentHistogramNode.next;
    }

    return buckets;
  }

  public long countAllBuckets() {
    HistogramNode currentHistogramNode = base;
    long buckets = 0;

    while (currentHistogramNode.next != null) {
      buckets++;

      currentHistogramNode = currentHistogramNode.next;
    }

    return buckets;
  }

  public long sumBuckets() {
    HistogramNode currentHistogramNode = base;
    long sum = 0;

    while (currentHistogramNode.next != null) {
      sum += currentHistogramNode.value;

      currentHistogramNode = currentHistogramNode.next;
    }

    return sum;
  }

  public long weightedSumBuckets() {
    HistogramNode currentHistogramNode = base;
    long sum = 0;

    while (currentHistogramNode.next != null) {
      sum += currentHistogramNode.bucket * currentHistogramNode.value;

      currentHistogramNode = currentHistogramNode.next;
    }

    return sum;
  }

  public long sumBucketsFrom(int bucketBase) {
    HistogramNode currentHistogramNode = base;
    long sum = 0;

    while (currentHistogramNode.next != null) {
      if (currentHistogramNode.bucket >= bucketBase) sum += currentHistogramNode.value;

      currentHistogramNode = currentHistogramNode.next;
    }
    return sum;
  }
}
