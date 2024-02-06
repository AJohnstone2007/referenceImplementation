/********************************************************************************
 * ARTPool.java - a pool based memory allocater that supports hashed maps of int-tuple to int-tuple
 *
 * General conventions
 *
 * This code is designed to be easily ported between Java and C. It is not object oriented, but does use overloading
 *
 * A pool is an allocatable array of integers. There is no de-allocation of garbage collection
 *
 * Pools are intended to be used to hold sets and maps which grow. Elements may be removed but the associated memory will not be re-used.
 *
 * Elements are described as being of type K_V where K and V are integers. K is the number of contiguous integers in the key, and V the number of contiguous integers in the value
 *
 * Functions include:
 *
 * mapLookup_K(map, k0, k1, ...) - looks up key (k0, k1, ...) in map; returns address of key or zero if not found
 * mapFind_K_V(map, k0, k1, ...) - looks up key (k0, k1, ...) in map; returns if found, otherwise creates element and sets value fields to zero
 * mapFind_K_V(map, k0, k1, ..., v0, v1, ...) - looks up key (k0, k1, ...) in map; returns if found, otherwise creates element and sets value fields to (V0, v1, ...)
 * mapCardinality(map) - returns number f elements in map
 * mapClear(map) - sets all buckets to zero
 * mapRemove(map) - locate the first element in the first non-empty bucket, unlink it from thetable and return its address. If map is empty return 0
 * mapAssign(map) - copy the bindings from one map to another. Note that the actual elements are reused, so are now aliased between maps. Mapsmust havethe same bucket count
 *
 * mapIteratorFirst<n>(map) - initialise global variables mapIteratorBucket<n>, mapIteratorElement<n> and mapIteratorTableAddress<n>; return address of first element
 * mapIteratorNext<n>() - return address of next element, or zero if there is no next element
 *
 ********************************************************************************/

// For C...
/*
#include<stdlib.h>
#include<stdio.h>
#define String const char*
#define public
#define private
#define static
#define final
#define boolean bool
*/
// For Java...
package uk.ac.rhul.cs.csle.art.old.util.pool;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ARTPool {
  // End of language customisation

  private final int largePrime = 99999989;
  private final int prime1 = 13;
  private final int prime2 = 241;
  private final int prime3 = 32029;

  private final int blockSizeExponent; // Blocks are 2^blockSizeExponent so as to allow rapid separation of block and offset
  private final int blockSize; // The block size computed by the constructor
  private final int blockSizeMask; // The computed mask for this block size
  private final int blockCount; // The maximumsize of this pool
  private int highWaterBlock = 0; // The currently extending block
  private int highWaterOffset = 1; // The first free location in the currently extending block: start at 1 because 0 means null

  // A map is a hash coded table comprising 'buckets' linked lists of integers: layout bucketCount, bucket_0, bucket_1, ..., bucket_(buckets-1)
  private final int bucketCountOffset = 0;
  private final int bucketsOffset = 1; // The address of bucket_0
  private final int elementNextOffset = 0; // the address of the pointer to the payload data in a list element
  private final int elementDataOffset = 1; // the address of the pointer to the payload data in a list element

  // For C...
  // int** blocks;
  // void error(String message) { printf("%s\n", message); exit(1); }
  // public void poolInit(int bSE, int bC) {
  // For Java...
  private final int[][] blocks; // The blocks themselves, allocated one at a time on demand
  public boolean found;

  private void error(String message) {
    System.err.printf("%s\n", message);
    System.exit(1);
  }

  public ARTPool(int bSE, int bC) {
    // prime1 = prime2 = prime3 = largePrime;

    // End of language customisation

    blockSizeExponent = bSE;
    blockCount = bC;
    blockSize = 1 << blockSizeExponent;
    blockSizeMask = blockSize - 1;
    // For C...
    // blocks = new int *[blockCount];
    // For Java...
    blocks = new int[blockCount][];
    // End of language customisation
    blocks[0] = new int[blockSize];
  }

  @Override
  public String toString() {
    String ret = "Pool: blockSizeExponent=" + blockSizeExponent + ", blockSize=" + blockSize + ", blockSizeMask=" + blockSizeMask + ", blockCount=" + blockCount
        + ", blocks=" + Arrays.toString(blocks) + ", highWaterBlock=" + highWaterBlock + ", highWaterOffset=" + highWaterOffset + "\n";
    for (int i = 0; i < 40; i++)
      ret += i + "-" + blocks[0][i] + " ";
    return ret;
  }

  public int poolAllocate(int count) {
    if (blockSize - highWaterOffset < count) {// allocate new block
      if (++highWaterBlock >= blockCount) error("Pool overflow"); // Note: we could resize the array here easily
      blocks[highWaterBlock] = new int[blockSize];
      highWaterOffset = 0;
    }
    int ret = (highWaterBlock << blockSizeExponent) + highWaterOffset;
    highWaterOffset += count;
    // System.out.println("Allocated " + count + " locations based at " + ret + "; highwater now " + highWaterOffset);
    return ret;
  }

  public int poolGet(int address) {
    return blocks[address >> blockSizeExponent][address & blockSizeMask];
  }

  public void poolPut(int address, int value) {
    blocks[address >> blockSizeExponent][address & blockSizeMask] = value;
  }

  public int listMake() {
    int ret = poolAllocate(1);
    poolPut(ret, 0);
    return ret;
  }

  public int listAdd_1(int list, int v0) {
    int ret = poolAllocate(2); // base pointer to first list element; 0 means null
    poolPut(ret, poolGet(list));
    poolPut(list, ret);
    poolPut(ret + 1, v0);

    return ret;
  }

  public int listAdd_2(int list, int v0, int v1) {
    int ret = poolAllocate(3); // base pointer to first list element; 0 means null
    poolPut(ret, poolGet(list)); // load next pointer with value in list variable
    poolPut(list, ret); // load list base with address of new element
    poolPut(ret + 1, v0); // load payload element
    poolPut(ret + 2, v1); // load payload element

    return ret;
  }

  public int listRemove(int list) {
    int ret = poolGet(list);

    if (ret == 0) return 0;

    poolPut(list, poolGet(ret)); // copy next pointer to list base

    return ret + 1; // return addressif first data element
  }

  public int mapMake(int bucketCount) { // Java will clear buckets; C will need to use calloc()
    int ret = poolAllocate(bucketCount + bucketsOffset);
    poolPut(ret, bucketCount);
    return ret;
  }

  public int mapFind_1_0(int map, int k0) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      if (found) return candidate;
    }
    found = false;

    // Step 3: not found: allocate and load
    int newElement = poolAllocate(1);
    poolPut(newElement, k0);

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapFind_1_1(int map, int k0, int v1) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      if (found) { // Found: update value and return
        poolPut(candidate + 1, v1);
        return candidate;
      }
    }
    found = false;

    // Step 3: not found: allocate and load
    int newElement = poolAllocate(2);
    poolPut(newElement, k0);
    poolPut(newElement + 1, v1);

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapFind_2_0(int map, int k0, int k1) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      found &= k1 == poolGet(candidate + 1);
      if (found) return candidate;
    }
    found = false;

    // Step 3: not found: allocate and load
    int newElement = poolAllocate(2);
    poolPut(newElement, k0);
    poolPut(newElement + 1, k1);

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapFind_2_2(int map, int k0, int k1) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      found &= k1 == poolGet(candidate + 1);
      if (found) return candidate;
    }
    found = false;

    // Step 3: not found: allocate and load
    int newElement = poolAllocate(4);

    poolPut(newElement + 0, k0);
    poolPut(newElement + 1, k1);
    poolPut(newElement + 2, 0); // Value field (2)
    poolPut(newElement + 3, 0); // Value field (3)

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapFind_3_0(int map, int k0, int k1, int k2) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    bucket += k2 * prime2;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      found &= k1 == poolGet(candidate + 1);
      found &= k2 == poolGet(candidate + 2);

      if (found) return candidate;
    }
    found = false;

    // Step 3: not found: allocate and load
    int newElement = poolAllocate(3);
    poolPut(newElement, k0);
    poolPut(newElement + 1, k1);
    poolPut(newElement + 2, k2);

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapFind_3_1(int map, int k0, int k1, int k2, int v1) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    bucket += k2 * prime2;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      found &= k1 == poolGet(candidate + 1);
      found &= k2 == poolGet(candidate + 2);
      if (found) {
        poolPut(candidate + 3, v1); // update value binding
        return candidate;
      }
    }
    found = false;
    // Step 3: not found: allocate and load
    int newElement = poolAllocate(4);

    poolPut(newElement + 0, k0);
    poolPut(newElement + 1, k1);
    poolPut(newElement + 2, k2);
    poolPut(newElement + 3, v1); // makevalue binding

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapFind_3_1(int map, int k0, int k1, int k2) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    bucket += k2 * prime2;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      found &= k1 == poolGet(candidate + 1);
      found &= k2 == poolGet(candidate + 2);
      if (found) return candidate;
    }
    found = false;
    // Step 3: not found: allocate and load
    int newElement = poolAllocate(4);

    poolPut(newElement + 0, k0);
    poolPut(newElement + 1, k1);
    poolPut(newElement + 2, k2);
    poolPut(newElement + 3, 0); // Value field initialised only for new element

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapFind_3_2(int map, int k0, int k1, int k2, int v0, int v1) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    bucket += k2 * prime2;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      found &= k1 == poolGet(candidate + 1);
      found &= k2 == poolGet(candidate + 2);
      if (found) {
        poolPut(candidate + 3, v0); // Value field
        poolPut(candidate + 4, v1); // Value field
        return candidate;
      }
    }
    found = false;
    // Step 3: not found: allocate and load
    int newElement = poolAllocate(5);

    poolPut(newElement + 0, k0);
    poolPut(newElement + 1, k1);
    poolPut(newElement + 2, k2);
    poolPut(newElement + 3, v0); // Value field
    poolPut(newElement + 4, v1); // Value field

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapFind_4_0(int map, int k0, int k1, int k2, int k3) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    bucket += k2 * prime2;
    bucket += k3 * prime3;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      found = true;
      found &= k0 == poolGet(candidate);
      found &= k1 == poolGet(candidate + 1);
      found &= k2 == poolGet(candidate + 2);
      found &= k3 == poolGet(candidate + 3);

      if (found) return candidate;
    }
    found = false;

    // Step 3: not found: allocate and load
    int newElement = poolAllocate(4);
    poolPut(newElement, k0);
    poolPut(newElement + 1, k1);
    poolPut(newElement + 2, k2);
    poolPut(newElement + 3, k3);

    // Step 4: create hash table element
    int tableElement = poolAllocate(2);
    poolPut(tableElement + elementDataOffset, newElement); // point at data element
    poolPut(tableElement, poolGet(map + bucketsOffset + bucket)); // head insertion
    poolPut(map + bucketsOffset + bucket, tableElement);
    return newElement;
  }

  public int mapLookup_1(int map, int k0) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      // System.out.println("Checking candidate at location " + candidate);
      found = true;
      found &= (k0 == poolGet(candidate));
      if (found) return candidate;
    }
    found = false;

    return 0;
  }

  public int mapLookup_2(int map, int k0, int k1) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      // System.out.println("Checking candidate at location " + candidate);
      found = true;
      found &= (k0 == poolGet(candidate));
      found &= (k1 == poolGet(candidate + 1));
      if (found) return candidate;
    }
    found = false;

    return 0;
  }

  public int mapLookup_3(int map, int k0, int k1, int k2) {
    // Step 1: compute the bucket by hashing the key
    int bucket = k0; // initial value for hash code is just the first element
    bucket += k1 * prime1;
    bucket += k2 * prime2;
    if (bucket < 0) bucket = -bucket;
    bucket %= poolGet(map + bucketCountOffset);

    // Step 2: search the list for this key
    for (int listElementAddress = poolGet(map + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress)) {
      int candidate = poolGet(listElementAddress + 1); // The data is at the address held in the data half of the list element
      // System.out.println("Checking candidate at location " + candidate);
      found = true;
      found &= (k0 == poolGet(candidate));
      found &= (k1 == poolGet(candidate + 1));
      found &= (k2 == poolGet(candidate + 2));

      if (found) return candidate;
    }
    found = false;

    return 0;
  }

  public int mapCardinality(int tableAddress) {
    int ret = 0;
    for (int bucket = 0; bucket < poolGet(tableAddress + bucketCountOffset); bucket++)
      for (int listElementAddress = poolGet(tableAddress + bucketsOffset + bucket); listElementAddress != 0; listElementAddress = poolGet(listElementAddress))
        ret++;
    return ret;
  }

  public void mapClear(int map) {
    for (int bucket = 0; bucket < poolGet(map + bucketCountOffset); bucket++)
      poolPut(map + bucketsOffset + bucket, 0);
  }

  // This is rather like iteratorFirst, except that we unlink the return value from the map, and we use local variables
  public int mapRemove(int map) {
    for (int bucket = 0; bucket < poolGet(map + bucketCountOffset); bucket++) {
      int element = poolGet(map + bucketsOffset + bucket);
      if (element != 0) { // Found an element
        // System.out.println("In remove: found element with address " + element + " that has next field " + poolGet(element + elementNextOffset)
        // + " and data field " + poolGet(element + elementDataOffset));
        poolPut(map + bucketsOffset + bucket, poolGet(element + elementNextOffset)); // Unlink from chain
        return poolGet(element + elementDataOffset);
      }
    }
    return 0;
  }

  // assign using a level two copy: the map element list is replicated, but the elements stay the same
  public void mapAssign(int dstMap, int srcMap) {
    if (poolGet(dstMap + bucketCountOffset) != poolGet(srcMap + bucketCountOffset))
      error("In pool, unsupported map assign between maps with different bucket counts");
    for (int bucket = 0; bucket < poolGet(dstMap + bucketCountOffset); bucket++) {
      poolPut(dstMap + bucketsOffset + bucket, 0); // Set chain to empty list
      for (int element = poolGet(srcMap + bucketsOffset + bucket); element != 0; element = poolGet(element + elementNextOffset)) {
        int newElement = poolAllocate(2);
        poolPut(newElement + elementDataOffset, poolGet(element + elementDataOffset)); // point at data element
        poolPut(newElement + elementNextOffset, poolGet(dstMap + bucketsOffset + bucket)); // head insertion
        poolPut(dstMap + bucketsOffset + bucket, newElement);
      }
    }
  }

  // Gosh, this is ugly. We want fast iteration without allocation. Thus we create a fixed static iterator, one per Pool
  // You want more iterators? clone this code
  int mapIteratorBucket1;
  int mapIteratorElement1;
  int mapIteratorTableAddress1;

  public int mapIteratorFirst1(int tableAddress) { // iterate to the first element of the table
    mapIteratorTableAddress1 = tableAddress;
    for (mapIteratorBucket1 = 0; mapIteratorBucket1 < poolGet(tableAddress + bucketCountOffset); mapIteratorBucket1++) {
      mapIteratorElement1 = poolGet(mapIteratorTableAddress1 + bucketsOffset + mapIteratorBucket1);
      if (mapIteratorElement1 != 0) {
        // System.out.printf("mapIteratorFirst1 returns %d\n", poolGet(mapIteratorElement1 + elementDataOffset));
        return poolGet(mapIteratorElement1 + elementDataOffset);
      }
    }
    // System.out.printf("mapIteratorFirst1 exhausted: returns 0\n");
    return 0;
  }

  public int mapIteratorNext1() {
    // Step 0: stick at end
    if (mapIteratorBucket1 >= poolGet(mapIteratorTableAddress1 + bucketCountOffset)) return 0;
    // Step 1: try the next list element
    mapIteratorElement1 = poolGet(mapIteratorElement1); // step to next element
    if (mapIteratorElement1 != 0) {
      // System.out.printf("mapIteratorFirst1 returns %d\n", poolGet(mapIteratorElement1 + elementDataOffset));
      return poolGet(mapIteratorElement1 + elementDataOffset);
    }
    // Step 2: find the next occupied bucket
    for (++mapIteratorBucket1; mapIteratorBucket1 < poolGet(mapIteratorTableAddress1 + bucketCountOffset); mapIteratorBucket1++) {
      mapIteratorElement1 = poolGet(mapIteratorTableAddress1 + bucketsOffset + mapIteratorBucket1);
      if (mapIteratorElement1 != 0) {
        // System.out.printf("mapIteratorFirst1 returns %d\n", poolGet(mapIteratorElement1 + elementDataOffset));
        return poolGet(mapIteratorElement1 + elementDataOffset);
      }
    }

    // System.out.printf("mapIteratorNext1 exhausted: returns 0\n");
    return 0;
  }

  int mapIteratorBucket2;
  int mapIteratorElement2;
  int mapIteratorTableAddress2;

  public int mapIteratorFirst2(int tableAddress) { // iterate to the first element of the table
    mapIteratorTableAddress2 = tableAddress;
    for (mapIteratorBucket2 = 0; mapIteratorBucket2 < poolGet(tableAddress + bucketCountOffset); mapIteratorBucket2++) {
      mapIteratorElement2 = poolGet(mapIteratorTableAddress2 + bucketsOffset + mapIteratorBucket2);
      if (mapIteratorElement2 != 0) return poolGet(mapIteratorElement2 + elementDataOffset);
    }
    return 0;
  }

  public int mapIteratorNext2() {
    // Step 0: stick at end
    if (mapIteratorBucket2 >= poolGet(mapIteratorTableAddress2 + bucketCountOffset)) return 0;
    // Step 2: try the next list element
    mapIteratorElement2 = poolGet(mapIteratorElement2); // step to next element
    if (mapIteratorElement2 != 0) return poolGet(mapIteratorElement2 + elementDataOffset);
    // Step 2: find the next occupied bucket
    for (++mapIteratorBucket2; mapIteratorBucket2 < poolGet(mapIteratorTableAddress2 + bucketCountOffset); mapIteratorBucket2++) {
      mapIteratorElement2 = poolGet(mapIteratorTableAddress2 + bucketsOffset + mapIteratorBucket2);
      if (mapIteratorElement2 != 0) return poolGet(mapIteratorElement2 + elementDataOffset);
    }

    return 0;
  }

  public String mapToString(int mapAddress, int keySize, int valueSize) {
    StringBuilder sb = new StringBuilder();
    sb.append("Unlinked map at base " + mapAddress + " with " + poolGet(mapAddress + bucketCountOffset) + " buckets\nStart of occupied buckets");
    for (int i = 0; i < poolGet(mapAddress + bucketCountOffset); i++)
      if (poolGet(mapAddress + bucketsOffset + i) != 0) {
        sb.append("\nbucket " + i + ":");
        for (int elementAddress = poolGet(mapAddress + bucketsOffset + i); elementAddress != 0; elementAddress = poolGet(elementAddress)) {
          int field = poolGet(elementAddress + elementDataOffset);
          sb.append("[" + field + "]");
          for (int f = 0; f < keySize; f++)
            sb.append((f == 0 ? "" : ",") + poolGet(field++));
          if (valueSize > 0) {
            sb.append("->");
            for (int f = 0; f < valueSize; f++)
              sb.append((f == 0 ? "" : ",") + poolGet(field++));
          }
        }
      }
    sb.append("\nEnd of occupied buckets");
    return sb.toString();
  }

  public String mapStatistics(int mapAddress) {
    boolean showAll = false;
    Map<Integer, Integer> histogram = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    int maxBucketCardinality = 0;
    sb.append("Pool statistics for map at base " + mapAddress + " with " + poolGet(mapAddress + bucketCountOffset) + " buckets\n");
    for (int i = 0; i < poolGet(mapAddress + bucketCountOffset); i++)
      if (poolGet(mapAddress + bucketsOffset + i) == 0) {
        if (histogram.get(0) == null) histogram.put(0, 0);
        histogram.put(0, histogram.get(0) + 1);
      } else {
        int bucketCardinality = 0;
        for (int elementAddress = poolGet(mapAddress + bucketsOffset + i); elementAddress != 0; elementAddress = poolGet(elementAddress))
          bucketCardinality++;
        if (showAll) sb.append("bucket " + i + ": " + bucketCardinality + "\n");
        if (histogram.get(bucketCardinality) == null) histogram.put(bucketCardinality, 0);
        histogram.put(bucketCardinality, histogram.get(bucketCardinality) + 1);
        if (bucketCardinality > maxBucketCardinality) maxBucketCardinality = bucketCardinality;
      }
    sb.append("Maximum bucket cardinality: " + maxBucketCardinality + "\nHistogram\n");
    for (Integer cardinality : histogram.keySet())
      sb.append(cardinality + ": " + histogram.get(cardinality) + "\n");
    return sb.toString();
  }

  public String mapArrayStatistics(int[] mapArray) {
    Map<Integer, Integer> histogram = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    int maxBucketCardinality = 0;

    sb.append("Pool statistics for map array at base " + mapArray[0] + " with " + poolGet(mapArray[0] + bucketCountOffset) + " buckets in the first map\n");
    for (int element = 0; element < mapArray.length; element++) {
      int mapAddress = mapArray[element];
      for (int i = 0; i < poolGet(mapAddress + bucketCountOffset); i++)
        if (poolGet(mapAddress + bucketsOffset + i) == 0) {
          if (histogram.get(0) == null) histogram.put(0, 0);
          histogram.put(0, histogram.get(0) + 1);
        } else {
          int bucketCardinality = 0;
          for (int elementAddress = poolGet(mapAddress + bucketsOffset + i); elementAddress != 0; elementAddress = poolGet(elementAddress))
            bucketCardinality++;
          if (histogram.get(bucketCardinality) == null) histogram.put(bucketCardinality, 0);
          histogram.put(bucketCardinality, histogram.get(bucketCardinality) + 1);
          if (bucketCardinality > maxBucketCardinality) maxBucketCardinality = bucketCardinality;
        }
    }
    sb.append("Maximum bucket cardinality: " + maxBucketCardinality + "\nHistogram\n");
    for (Integer cardinality : histogram.keySet())
      sb.append(cardinality + ": " + histogram.get(cardinality) + "\n");

    return sb.toString();
  }

  // Java test harness
  public static void main(final String[] args) {
    ARTPool pool = new ARTPool(11, 20); // 10 blocks of 2^11 locations
    int testMap = pool.mapMake(7), assignTarget = pool.mapMake(7);
    pool.mapFind_3_2(testMap, 1, 2, 3, 4, 5);
    pool.mapFind_3_2(testMap, 1, 2, 4, 6, 6);
    pool.mapFind_3_2(testMap, 1, 2, 3, 6, 6);
    pool.mapFind_3_2(testMap, 1, 2, 4, 7, 7);
    System.out.println("After initial load: expect 1,2,3->6,6 and 1,2,4->7,7");
    System.out.println(pool.mapToString(testMap, 3, 2));

    System.out.println("Iterator test");
    int iteratorCardinality = 0;
    for (int v = pool.mapIteratorFirst1(testMap); v != 0; v = pool.mapIteratorNext1()) {
      System.out.println("Iterator finds element based at " + v);
      iteratorCardinality++;
    }
    if (iteratorCardinality == pool.mapCardinality(testMap))
      System.out.println("Iterator cardinality good");
    else
      System.out.println("Iterator cardinality BAD");
    System.out.println("Lookup: " + pool.mapLookup_3(testMap, 1, 2, 4));
    System.out.println(pool.mapToString(testMap, 3, 2));
    System.out.println("Cardinality: " + pool.mapCardinality(testMap));
    pool.mapClear(testMap);
    System.out.println("After clear, cardinality: " + pool.mapCardinality(testMap));
    System.out.println(pool.mapToString(testMap, 3, 2));

    // Now overload the map and see if all is well
    System.out.println("Loading 20 elements");
    for (int i = 0; i < 20; i++) {
      System.out.println("Loading element" + i);
      pool.mapFind_3_2(testMap, i, i, i, 0, 0);
    }

    System.out.println("tableAddress table" + pool.mapToString(testMap, 3, 2));
    pool.mapAssign(assignTarget, testMap);
    System.out.println("assignTarget table" + pool.mapToString(testMap, 3, 2));

    System.out.println("Iterator test");
    iteratorCardinality = 0;
    for (int v = pool.mapIteratorFirst1(testMap); v != 0; v = pool.mapIteratorNext1()) {
      System.out.println("Iterator finds element based at " + v);
      iteratorCardinality++;
    }
    if (iteratorCardinality == pool.mapCardinality(testMap))
      System.out.println("Iterator cardinality good");
    else
      System.out.println("Iterator cardinality BAD");
    System.out.println("Cardinality: " + pool.mapCardinality(testMap));

    int removeCount = 0;
    while (pool.mapCardinality(testMap) != 0) {

      int address = pool.mapRemove(testMap);
      System.out.println("Removing element " + removeCount++ + " with address " + address + " and initial field " + pool.poolGet(address));
    }

    System.out.println("After removals, cardinality: " + pool.mapCardinality(testMap));
    System.out.println(pool.mapToString(testMap, 3, 2));

    System.out.println("assignTarget table" + pool.mapToString(testMap, 3, 2));
    pool.mapAssign(testMap, assignTarget);
    System.out.println("tableAddress table after assignment from assigntarget" + pool.mapToString(testMap, 3, 2));

    System.out.println("tableAddress final statistics\n" + pool.mapStatistics(testMap));

    System.out.println("\n*****************************************\n");

  }
}
