package com.sun.webkit.network;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
final class ByteBufferPool {
private final Queue<ByteBuffer> byteBuffers =
new ConcurrentLinkedQueue<ByteBuffer>();
private final int bufferSize;
private ByteBufferPool(int bufferSize) {
this.bufferSize = bufferSize;
}
static ByteBufferPool newInstance(int bufferSize) {
return new ByteBufferPool(bufferSize);
}
ByteBufferAllocator newAllocator(int maxBufferCount) {
return new ByteBufferAllocatorImpl(maxBufferCount);
}
private final class ByteBufferAllocatorImpl implements ByteBufferAllocator {
private final Semaphore semaphore;
private ByteBufferAllocatorImpl(int maxBufferCount) {
semaphore = new Semaphore(maxBufferCount);
}
@Override
public ByteBuffer allocate() throws InterruptedException {
semaphore.acquire();
ByteBuffer byteBuffer = byteBuffers.poll();
if (byteBuffer == null) {
byteBuffer = ByteBuffer.allocateDirect(bufferSize);
}
return byteBuffer;
}
@Override
public void release(ByteBuffer byteBuffer) {
byteBuffer.clear();
byteBuffers.add(byteBuffer);
semaphore.release();
}
}
}
interface ByteBufferAllocator {
ByteBuffer allocate() throws InterruptedException;
void release(ByteBuffer byteBuffer);
}
