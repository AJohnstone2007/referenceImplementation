package robottest;
import java.io.*;
public class BMPOutputStream extends FilterOutputStream{
BMPOutputStream(OutputStream out, int [] arr, int width, int height) {
super(out);
try {
int lineByteWidth = ((width * 3 + 3) >> 2) << 2;
out.write(0x42);
out.write(0x4d);
writeBMPInt(out, lineByteWidth * height + 0x36);
writeBMPInt(out, 0);
writeBMPInt(out, 0x36);
writeBMPInt(out, 0x28);
writeBMPInt(out, width);
writeBMPInt(out, height);
writeBMPShort(out, 0x01);
writeBMPShort(out, 0x18);
writeBMPInt(out, 0);
writeBMPInt(out, lineByteWidth * height);
writeBMPInt(out, 0xb13);
writeBMPInt(out, 0xb13);
writeBMPInt(out, 0);
writeBMPInt(out, 0);
out.flush();
int yIncrement = height;
byte[] line = new byte[lineByteWidth];
for (int i = yIncrement - 1; i >= 0; i--) {
java.util.Arrays.fill(line,(byte)0);
int pixelRowStart = i * width;
int byteOffsetInLine = 0;
for (int imgX = 0; imgX < width; imgX++) {
int rgb = arr[pixelRowStart + imgX];
line[byteOffsetInLine++] = (byte) (rgb & 0xff);
line[byteOffsetInLine++] = (byte) ((rgb >> 8) & 0xff);
line[byteOffsetInLine++] = (byte) ((rgb >> 16) & 0xff);
}
out.write(line);
}
out.flush();
out.close();
} catch (Exception e) {}
}
private static void writeBMPInt(OutputStream out, int i) throws IOException {
out.write(i & 0xff);
out.write((i >> 8) & 0xff);
out.write((i >> 16) & 0xff);
out.write(i >> 24);
}
private static void writeBMPShort(OutputStream out, int i) throws IOException {
out.write(i & 0xff);
out.write((i >> 8) & 0xff);
}
}
