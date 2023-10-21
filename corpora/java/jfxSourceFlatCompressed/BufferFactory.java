package com.sun.prism.es2;
import java.nio.*;
class BufferFactory {
static final int SIZEOF_BYTE = 1;
static final int SIZEOF_SHORT = 2;
static final int SIZEOF_CHAR = 2;
static final int SIZEOF_INT = 4;
static final int SIZEOF_FLOAT = 4;
static final int SIZEOF_LONG = 8;
static final int SIZEOF_DOUBLE = 8;
private static final boolean isLittleEndian;
static {
ByteBuffer tst_b = BufferFactory.newDirectByteBuffer(BufferFactory.SIZEOF_INT);
IntBuffer tst_i = tst_b.asIntBuffer();
ShortBuffer tst_s = tst_b.asShortBuffer();
tst_i.put(0, 0x0A0B0C0D);
isLittleEndian = 0x0C0D == tst_s.get(0) ;
}
static boolean isLittleEndian() {
return isLittleEndian;
}
static ByteBuffer newDirectByteBuffer(int size) {
return nativeOrder(ByteBuffer.allocateDirect(size));
}
static ByteBuffer nativeOrder(ByteBuffer buf) {
return buf.order(ByteOrder.nativeOrder());
}
static boolean isDirect(Object buf) {
if (buf == null) {
return true;
}
if (buf instanceof ByteBuffer) {
return ((ByteBuffer) buf).isDirect();
} else if (buf instanceof FloatBuffer) {
return ((FloatBuffer) buf).isDirect();
} else if (buf instanceof DoubleBuffer) {
return ((DoubleBuffer) buf).isDirect();
} else if (buf instanceof CharBuffer) {
return ((CharBuffer) buf).isDirect();
} else if (buf instanceof ShortBuffer) {
return ((ShortBuffer) buf).isDirect();
} else if (buf instanceof IntBuffer) {
return ((IntBuffer) buf).isDirect();
} else if (buf instanceof LongBuffer) {
return ((LongBuffer) buf).isDirect();
}
throw new RuntimeException("Unexpected buffer type " + buf.getClass().getName());
}
static int getDirectBufferByteOffset(Object buf) {
if(buf == null) {
return 0;
}
if(buf instanceof Buffer) {
int pos = ((Buffer)buf).position();
if(buf instanceof ByteBuffer) {
return pos;
} else if (buf instanceof FloatBuffer) {
return pos * SIZEOF_FLOAT;
} else if (buf instanceof IntBuffer) {
return pos * SIZEOF_INT;
} else if (buf instanceof ShortBuffer) {
return pos * SIZEOF_SHORT;
} else if (buf instanceof DoubleBuffer) {
return pos * SIZEOF_DOUBLE;
} else if (buf instanceof LongBuffer) {
return pos * SIZEOF_LONG;
} else if (buf instanceof CharBuffer) {
return pos * SIZEOF_CHAR;
}
}
throw new RuntimeException("Disallowed array backing store type in buffer "
+ buf.getClass().getName());
}
static Object getArray(Object buf) {
if (buf == null) {
return null;
}
if(buf instanceof ByteBuffer) {
return ((ByteBuffer) buf).array();
} else if (buf instanceof FloatBuffer) {
return ((FloatBuffer) buf).array();
} else if (buf instanceof IntBuffer) {
return ((IntBuffer) buf).array();
} else if (buf instanceof ShortBuffer) {
return ((ShortBuffer) buf).array();
} else if (buf instanceof DoubleBuffer) {
return ((DoubleBuffer) buf).array();
} else if (buf instanceof LongBuffer) {
return ((LongBuffer) buf).array();
} else if (buf instanceof CharBuffer) {
return ((CharBuffer) buf).array();
}
throw new RuntimeException("Disallowed array backing store type in buffer "
+ buf.getClass().getName());
}
static int getIndirectBufferByteOffset(Object buf) {
if(buf == null) {
return 0;
}
if (buf instanceof Buffer) {
int pos = ((Buffer)buf).position();
if(buf instanceof ByteBuffer) {
return (((ByteBuffer)buf).arrayOffset() + pos);
} else if(buf instanceof FloatBuffer) {
return (SIZEOF_FLOAT*(((FloatBuffer)buf).arrayOffset() + pos));
} else if(buf instanceof IntBuffer) {
return (SIZEOF_INT*(((IntBuffer)buf).arrayOffset() + pos));
} else if(buf instanceof ShortBuffer) {
return (SIZEOF_SHORT*(((ShortBuffer)buf).arrayOffset() + pos));
} else if(buf instanceof DoubleBuffer) {
return (SIZEOF_DOUBLE*(((DoubleBuffer)buf).arrayOffset() + pos));
} else if(buf instanceof LongBuffer) {
return (SIZEOF_LONG*(((LongBuffer)buf).arrayOffset() + pos));
} else if(buf instanceof CharBuffer) {
return (SIZEOF_CHAR*(((CharBuffer)buf).arrayOffset() + pos));
}
}
throw new RuntimeException("Unknown buffer type " + buf.getClass().getName());
}
}
