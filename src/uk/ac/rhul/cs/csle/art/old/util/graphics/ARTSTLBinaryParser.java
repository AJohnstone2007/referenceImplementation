package uk.ac.rhul.cs.csle.art.old.util.graphics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

public class ARTSTLBinaryParser extends ARTSTLParser {
  InputStream inputStream;
  byte[] buffer;
  int facetCount = 0;

  public ARTSTLBinaryParser(String filename) {
    System.out.println("Opening binary STL " + filename);
    try {
      this.inputStream = new FileInputStream(new File(filename));
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("Unable to open binary STL file " + filename);
    }
    readSTLBinary();
  }

  public ARTSTLBinaryParser(FileInputStream inputStream) {
    this.inputStream = inputStream;
    readSTLBinary();
  }

  private void readSTLBinary() {
    try {
      buffer = new byte[4];
      for (int tmp = 0; tmp < 20; tmp++)
        readInt(); // Skip the 80 byte header

      facetCount = readInt();
    } catch (IOException e) {
      throw new ARTUncheckedException("I/O error in ARTSTLBinaryParser");
    }
  }

  @Override
  public int getFacetCount() {
    return facetCount;
  }

  private void readBuffer4() throws IOException {
    inputStream.read(buffer, 0, 4);
  }

  private void readBuffer2() throws IOException {
    inputStream.read(buffer, 0, 2);
  }

  private int readInt() throws IOException {
    readBuffer4();
    return ((0xFF & buffer[3]) << 24) | ((0xFF & buffer[2]) << 16) | ((0xFF & buffer[1]) << 8) | ((0xFF & buffer[0]));
  }

  private float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }

  @Override
  public void readFacet(ARTCoord normal, ARTCoord vertex1, ARTCoord vertex2, ARTCoord vertex3) {
    try {
      normal.setX(readFloat());
      normal.setY(readFloat());
      normal.setZ(readFloat());

      vertex1.setX(readFloat());
      vertex1.setY(readFloat());
      vertex1.setZ(readFloat());

      vertex2.setX(readFloat());
      vertex2.setY(readFloat());
      vertex2.setZ(readFloat());

      vertex3.setX(readFloat());
      vertex3.setY(readFloat());
      vertex3.setZ(readFloat());

      readBuffer2();
    } catch (IOException e) {
      throw new ARTUncheckedException("I/O error in ARTSTLBinaryParser");
    }
  }
}
