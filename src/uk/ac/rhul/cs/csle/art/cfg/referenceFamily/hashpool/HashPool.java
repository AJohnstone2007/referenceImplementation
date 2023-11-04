package uk.ac.rhul.cs.csle.art.cfg.referenceFamily.hashpool;

import uk.ac.rhul.cs.csle.art.cfg.referenceFamily.ReferenceParser;

/**
 * An expandable pool of integers which supports sequential allocation of small blocks. There is no facility to free memory once it has been allocated.
 *
 * We use a 2-D array of integers organised as an array of pointers to 1-D arrays of integers called poolBlocks. Initially, only one poolBlock is allocated.
 *
 * Further poolBlocks are allocated as needed. If the required number of poolBlocks exceeds the capacity of pool, it is resized by 150%
 */

// public class HashPool { // Uncomment this line for standalone HashPool
public class HashPool extends ReferenceParser { // Uncomment this line for HashPool that also inherits all of ReferenceParser

  private int[][] pool;
  private final int poolBlockInitialCount = 1024;
  private final int poolAddressOffset = 26; // poolAddressOffset must be a power of 2
  private final int poolBlockSize = 1 << poolAddressOffset;
  private final int poolAddressMask = poolBlockSize - 1;

  private int poolBlockCount; // Total number of available pool blocks
  private int poolBlockTop; // Current allocation point: block number
  private int poolOffsetTop; // Current allocation point: offset

  protected int getFirstUnusedElement() {
    return (poolBlockTop * poolBlockSize) + poolOffsetTop + 1;
  }

  // The allocation function: check to see if the current poolBlock has enough space; if not make a new one.
  protected void allocate(int size) {
    if (poolOffsetTop + size > poolBlockSize) { // need new poolBlock
      poolBlockTop++;

      if (poolBlockTop >= poolBlockCount) { // resize pointer array
        poolBlockCount += poolBlockCount / 2;
        int[][] newPool = new int[poolBlockCount][];

        for (int i = 0; i < poolBlockTop; i++)
          newPool[i] = pool[i]; // Copy old pointers

        pool = newPool;
      }
      pool[poolBlockTop] = new int[poolBlockSize];
      poolOffsetTop = 0;
    }
    poolOffsetTop += size; // Perform the actual allocation
  }

  protected int poolGet(int index) {
    return pool[index >> poolAddressOffset][index & poolAddressMask];
  }

  protected void poolSet(int index, int value) {
    pool[index >> poolAddressOffset][index & poolAddressMask] = value;
  }

  /**
   * Hash tables: array <name>Buckets contains the pool index of the first element in each hash list; each element in the hash list must have the pool index of
   * its successor in its first element.
   *
   * Future extension: rehash function when load factor exceeds threshold
   */

  protected int[] clean(int[] hashTable, int bucketCount) {
    if (hashTable == null) return new int[bucketCount];

    for (int i = 0; i < hashTable.length; i++)
      hashTable[i] = 0;

    return hashTable;
  }

  protected void initialisehashPool() {
    poolBlockCount = poolBlockInitialCount;
    if (pool == null) {
      pool = new int[poolBlockCount][];
      pool[0] = new int[poolBlockSize];
    } else
      for (int i = 0; i < poolBlockSize; i++)
        pool[0][i] = 0;

    poolBlockTop = 0;
    poolOffsetTop = 2; // Block 0, offsets 0 and 1 reserved for 'not found' and illegal values
    pool[0][1] = -1; // Defensive programming: make the first label element illegal to catch address zero errors

  }

  /* Low level hashpool functions *********************************************/

  private final int hashPrime = 1013; // Another large prime
  private int hashResult;

  // Knuth style multiplier hash functions for 32-bits - these functions have been inlined into the find functions below but are retained here for docuumntary
  // purposes
  private void hash(int hashBucketCount, int a, int b) {
    hashResult = (a + (b * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;
  }

  private void hash(int hashBucketCount, int a, int b, int c) {
    hashResult = ((a + (b * hashPrime)) + (c * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;
  }

  private void hash(int hashBucketCount, int a, int b, int c, int d) {
    hashResult = ((a + (b * hashPrime)) + (c * hashPrime) + (d * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;
  }

  /** Low level find functions: lookup an element and create if not present ***/

  /* The result of a find is left in these variables */
  protected boolean findMadeNew;
  protected int findIndex; // Combined index
  private int findBlockIndex; // top p of index
  private int findOffset; // bottom p of index
  private int findBlock[]; // reference to block containing this index
  private int findLoadOffset; // offset to first unused field

  /* Lookup key <a,b> on hashBuckets. If not found, allocate allocationSize and load <a,b> to offsets (1,2) */
  protected void find(int[] hashTable, int hashBucketCount, int allocationSize, int a, int b) {
    hashResult = (a + (b * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;

    findIndex = hashTable[hashResult];
    do {
      findBlockIndex = findIndex >> poolAddressOffset;
      findOffset = findIndex & poolAddressMask;
      findBlock = pool[findBlockIndex];

      if (a == findBlock[findOffset + 1] && b == findBlock[findOffset + 2]) {
        findMadeNew = false;
        return;
      }

      findIndex = findBlock[findOffset]; // Step to next
    } while (findIndex != 0);

    if (allocationSize != 0) { // Note: allocation size turns find into lookup...
      allocate(allocationSize);
      findOffset = poolOffsetTop - allocationSize;
      findBlockIndex = poolBlockTop;
      findBlock = pool[findBlockIndex];
      findIndex = findBlockIndex << poolAddressOffset | findOffset;
      findLoadOffset = findOffset;

      findBlock[findOffset] = hashTable[hashResult];
      hashTable[hashResult] = findIndex;

      findBlock[++findLoadOffset] = a;
      findBlock[++findLoadOffset] = b;
    }
    findMadeNew = true;
    return;
  }

  /* Lookup key <a,b,c> on hashBuckets. If not found, allocate allocationSize and load <a,b> to offsets (1,2,3) */
  protected void find(int[] hashTable, int hashBucketCount, int allocationSize, int a, int b, int c) {
    hashResult = ((a + (b * hashPrime)) + (c * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;

    findIndex = hashTable[hashResult];
    do {
      findBlockIndex = findIndex >> poolAddressOffset;
      findOffset = findIndex & poolAddressMask;
      findBlock = pool[findBlockIndex];

      if (a == findBlock[findOffset + 1] && b == findBlock[findOffset + 2] && c == findBlock[findOffset + 3]) {
        findMadeNew = false;
        return;
      }
      findIndex = findBlock[findOffset]; // Step to next
    } while (findIndex != 0);

    if (allocationSize != 0) {
      allocate(allocationSize);
      findOffset = poolOffsetTop - allocationSize;
      findBlockIndex = poolBlockTop;
      findBlock = pool[findBlockIndex];
      findIndex = findBlockIndex << poolAddressOffset | findOffset;
      findLoadOffset = findOffset;

      findBlock[findOffset] = hashTable[hashResult];
      hashTable[hashResult] = findIndex;

      findBlock[++findLoadOffset] = a;
      findBlock[++findLoadOffset] = b;
      findBlock[++findLoadOffset] = c;
    }
    findMadeNew = true;
    return;
  }

  /* Lookup key <a,b,c,d> on hashBuckets. If not found, allocate allocationSize and load <a,b> to offsets (1,2,3,4) */
  protected void find(int[] hashTable, int hashBucketCount, int allocationSize, int a, int b, int c, int d) {
    hashResult = ((a + (b * hashPrime)) + (c * hashPrime) + (d * hashPrime));
    hashResult %= hashBucketCount;
    if (hashResult < 0) hashResult = -hashResult;

    findIndex = hashTable[hashResult];
    do {
      findBlockIndex = findIndex >> poolAddressOffset;
      findOffset = findIndex & poolAddressMask;
      findBlock = pool[findBlockIndex];

      if (a == findBlock[findOffset + 1] && b == findBlock[findOffset + 2] && c == findBlock[findOffset + 3] && d == findBlock[findOffset + 4]) {
        findMadeNew = false;
        return;
      }

      findIndex = findBlock[findOffset]; // Step to next
    } while (findIndex != 0);

    if (allocationSize != 0) {
      allocate(allocationSize);
      findOffset = poolOffsetTop - allocationSize;
      findBlockIndex = poolBlockTop;
      findBlock = pool[findBlockIndex];
      findIndex = findBlockIndex << poolAddressOffset | findOffset;
      findLoadOffset = findOffset;

      findBlock[findOffset] = hashTable[hashResult];
      hashTable[hashResult] = findIndex;

      findBlock[++findLoadOffset] = a;
      findBlock[++findLoadOffset] = b;
      findBlock[++findLoadOffset] = c;
      findBlock[++findLoadOffset] = d;
    }
    findMadeNew = true;
    return;
  }

  protected int cardinality(int[] hashTable) {
    int count = 0;
    for (int bucket : hashTable)
      for (int chain = bucket; chain != 0; chain = poolGet(chain))
        count++;

    return count;
  }

  protected String occupancy(int[] hashTable) {
    int occupanciesMax = 5;
    int[] occupancies = new int[occupanciesMax];

    for (int bucket : hashTable) {
      int occupancy = 0;
      for (int chain = bucket; chain != 0; chain = poolGet(chain))
        occupancy++;
      if (occupancy > occupanciesMax - 1) occupancy = occupanciesMax - 1;
      occupancies[occupancy]++;
    }

    String res = "";
    for (int i = 0; i < occupanciesMax; i++)
      res += occupancies[i] + ",";
    return res;
  }
}