package com.sun.glass.ui.monocle;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
public class FramebufferY8SuperShim extends Framebuffer {
public FramebufferY8SuperShim(ByteBuffer bb, int width, int height, int depth, boolean clear) {
super(bb, width, height, depth, clear);
}
@Override
public void write(WritableByteChannel out) throws IOException {
super.write(out);
}
@Override
public void copyToBuffer(ByteBuffer out) {
super.copyToBuffer(out);
}
}
