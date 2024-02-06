package uk.ac.rhul.cs.csle.art.old.util.graphics;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/* 
 JPEGFX.java by Adrian Johnstone, V01.00 December 2014

 A decoder for sequential DCT-based JPEG images which renders using Javafx

 Javafx-specific code is highlighted so that you can convert to your preferred library

 The code is distributed under the permissive MIT license

 Copyright (c) 2014 Adrian Johnstone

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 copies of the Software, and to permit persons to whom the Software is furnished
 to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all 
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 Please send bug reports and comments to a.johnstone@rhul.ac.uk

 Understanding this implementation might be easier if you read these documents, probably in the order given.

 1. JPEG, concisely by Adrian Johnstone of Royal Holloway, University of London: 
 see pages below https://www.royalholloway.ac.uk/computerscience/research/csle/cslehome.aspx

 A short technical report which describes baseline JPEG processing and this code.

 2. The Wikipedia page for JPEG: see http://en.wikipedia.org/wiki/JPEG

 Very helpful and concise summary with quite a lot of technical detail.

 3. The JPEG still picture compression standard by Greg Wallace: see http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=125072

 Background to the standard published in IEEE Transactions on Consumer Electronics in 1992. Preprints are available online.

 4. JPEG File Interchange Format V1.02 by Eric Hamilton of C-Cube systems: see http://222.w3.org/Graphics/JPEG/jfif3.pdff

 JFIF is the de facto file format used by most JPEG tools; 

 5. JPEG standard (JPEG ISO/IEC 10918-1 ITU-T Recommendation T.81): see http://222.w3.org/Graphics/JPEG/itu-t81.pdf

 The full JPEG standard; much detail, including JPEG styles which are not implemented here
 */

public class ARTJPEGDecodeBaseline {
  private byte[] pixelBuffer;
  private int pixelBufferWidth;

  private static final boolean TRACE = false; // Set to true for verbose output of file markers
  private static final boolean DATAUNITTRACE = false; // Set to true for very verbose output of data unit processing
  private static final int DATAUNITTRACE_LO_X = 0; // if DATAUNITTRACE is true, then output data unit traces for the
  private static final int DATAUNITTRACE_LO_Y = 0; // subimage bounded by (LO_X, LO_Y) and (HI_X, HI_Y)
  private static final int DATAUNITTRACE_HI_X = 8; // Note: coordinates are pixels, not data unit blocks, so (0,0):(16,8) will trace the first two data units
  private static final int DATAUNITTRACE_HI_Y = 8;
  private boolean dataTracing = false; // This is a dynamic guard which allows scan data tracing to be locally enabled. It is always guarded by static
                                       // guard DATAUNITRACE so as to hensure that the code is suppressed by the compiler when tracing is not needed

  private static final int[] linearToRaster = { 0, 1, 8, 16, 9, 2, 3, 10, 17, 24, 32, 25, 18, 11, 4, 5, 12, 19, 26, 33, 40, 48, 41, 34, 27, 20, 13, 6, 7, 14,
      21, 28, 35, 42, 49, 56, 57, 50, 43, 36, 29, 22, 15, 23, 30, 37, 44, 51, 58, 59, 52, 45, 38, 31, 39, 46, 53, 60, 61, 54, 47, 55, 62, 63 };

  private static final int[] rasterToLinear = { 0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40,
      44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63 };

  // These constants are from Table B.1 of Appendix B of the JPEG standard
  private static final int JPG_marker_StartOfFrame_BaselineDCT = 0xFFC0;
  private static final int JPG_marker_DefineHuffmanTables = 0xFFC4;
  private static final int JPG_marker_StartOfImage = 0xFFD8;
  private static final int JPG_marker_EndOfImage = 0xFFD9;
  private static final int JPG_marker_StartOfScan = 0xFFDA;
  private static final int JPG_marker_DefineQuantisationTables = 0xFFDB;
  private static final int JPG_marker_DefineRestartInterval = 0xFFDD;
  private static final int JPG_marker_APP0 = 0xFFE0;

  private int imageWidth; // Set when processing JPG_marker_StartOfFrame
  private int imageHeight; // Set when processing JPG_marker_StartOfFrame

  private byte imageBuffer[]; // Holds a copy of the entire file contents
  private int imageBufferIndex; // address of next byte to be read from file buffer
  private int scanDataImageBufferIndex; // addressof start of scan data
  private int imageBits; // used by imageReadBit() to hold current byte
  private int imageBitIndex; // used by imageReadBit() to index into the current byte; when set to 8, then read next byte

  private int parameterLength; // Set when processing markers
  private int precision; // Sample precision - this implementation only supports 8-bit precision

  private int scanComponentCount;
  private int scanComponentSelectors[];
  private int DCEntropyEncodingTables[];
  private int ACEntropyEncodingTables[];
  private int componentCount; // The number of components in this image - this implementation expects three
  private int componentIdentifiers[]; // Indentifier numbers for each component - this implementation expects three
  private int horizontalSamplingFactors[]; // Horizontal subsampling factors - this implementation only supports h=v=1 and h=v=2
  private int verticalSamplingFactors[]; // Vertical subsampling factors - this implementation only supports h=v=1 and h=v=2
  private int quantisationTableDestinations[];

  private int restartInterval; // Set when processing JPG_marker_DefineRestartInterval
  private int restartCounter; // Used to count MCU's when restart processing is in use
  private int restartNumber; // Used to count the number of restart blocks processed

  private int[][] quantisationTables; // Set when processing JPG_marker_DefineQuantisationTables

  private int huffmanSymbolCounts[][][]; // Set when processing JPG_marker_DefineHuffmanTables - indexed by isACTable (0=>DC, 1=>AC), htNumber and length
  private int huffmanSymbols[][][][]; // Set when processing JPG_marker_DefineHuffmanTables - indexed by isACTable (0=>DC, 1=>AC), htNumber, length,
                                      // canonical
  // Huffman number

  private int luminanceDCvalue; // Maintains running luminance DC value
  private int chrominanceBDCvalue; // Maintains running chrominance B DC value
  private int chrominanceRDCvalue; // Maintains running chrominance R DC value

  private int[] luminanceDataUnitA; // Data unit for first luminance block
  private int[] luminanceDataUnitB; // Data unit for second luminance block in a 2:2 subsampled image
  private int[] luminanceDataUnitC; // Data unit for third luminance block in a 2:2 subsampled image
  private int[] luminanceDataUnitD;// Data unit for fourth luminance block in a 2:2 subsampled image
  private int[] chrominanceBDataUnit;// Data unit for chrominance B
  private int[] chrominanceRDataUnit;// Data unit for chrominance R
  private int[] luminanceDataUnitA_spatial; // Data unit for first luminance block after iDCT
  private int[] luminanceDataUnitB_spatial; // Data unit for second luminance block in a 2:2 subsampled image after iDCT
  private int[] luminanceDataUnitC_spatial; // Data unit for third luminance block in a 2:2 subsampled image after iDCT
  private int[] luminanceDataUnitD_spatial;// Data unit for fourth luminance block in a 2:2 subsampled image after iDCT
  private int[] chrominanceBDataUnit_spatial;// Data unit for chrominance B after iDCT
  private int[] chrominanceRDataUnit_spatial;// Data unit for chrominance R after iDCT

  private final int[] spreadMapSimple = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
      32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63 };

  private final int[] spreadMapA = { 0, 0, 1, 1, 2, 2, 3, 3, 0, 0, 1, 1, 2, 2, 3, 3, 8, 8, 9, 9, 10, 10, 11, 11, 8, 8, 9, 9, 10, 10, 11, 11, 16, 16, 17, 17,
      18, 18, 19, 19, 16, 16, 17, 17, 18, 18, 19, 19, 24, 24, 25, 25, 26, 26, 27, 27, 24, 24, 25, 25, 26, 26, 27, 27 };

  private final int[] spreadMapB = { 4, 4, 5, 5, 6, 6, 7, 7, 4, 4, 5, 5, 6, 6, 7, 7, 12, 12, 13, 13, 14, 14, 15, 15, 12, 12, 13, 13, 14, 14, 15, 15, 20, 20,
      21, 21, 22, 22, 23, 23, 20, 20, 21, 21, 22, 22, 23, 23, 28, 28, 29, 29, 30, 30, 31, 31, 28, 28, 29, 29, 30, 30, 31, 31 };

  private final int[] spreadMapC = { 32, 32, 33, 33, 34, 34, 35, 35, 32, 32, 33, 33, 34, 34, 35, 35, 40, 40, 41, 41, 42, 42, 43, 43, 40, 40, 41, 41, 42, 42,
      43, 43, 48, 48, 49, 49, 50, 50, 51, 51, 48, 48, 49, 49, 50, 50, 51, 51, 56, 56, 57, 57, 58, 58, 59, 59, 56, 56, 57, 57, 58, 58, 59, 59 };

  private final int[] spreadMapD = { 36, 36, 37, 37, 38, 38, 39, 39, 36, 36, 37, 37, 38, 38, 39, 39, 44, 44, 45, 45, 46, 46, 47, 47, 44, 44, 45, 45, 46, 46,
      47, 47, 52, 52, 53, 53, 54, 54, 55, 55, 52, 52, 53, 53, 54, 54, 55, 55, 60, 60, 61, 61, 62, 62, 63, 63, 60, 60, 61, 61, 62, 62, 63, 63 };

  private double iDCTlookup[][]; // A lookup table of pre-computed iDCT factors

  public int getImageWidth() {
    return imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  private byte saturate(double i) {
    return i > 255 ? (byte) 255 : i < 0 ? (byte) 0 : (byte) i;
  }

  private int imagePeekByte() {
    return imageBuffer[imageBufferIndex] & 0xFF;
  }

  private int imageReadByte() {
    return imageBuffer[imageBufferIndex++] & 0xFF;
  }

  private int imageReadDoubleByte() {
    int retValue = (imageBuffer[imageBufferIndex] & 0xFF) << 8 | (imageBuffer[imageBufferIndex + 1] & 0xFF);
    imageBufferIndex += 2;
    return retValue;
  }

  private int imageReadBit() {
    if (imageBitIndex > 7) {
      imageBits = imageReadByte();
      imageBitIndex = 0;
      if (imageBits == 0xFF) {
        if (imagePeekByte() == 0) {
          // if (TRACE) System.err.printf("Skipping 0x00 after 0xFF at offset 0x%X\n", imageBufferIndex); // usually commented out
          imageReadByte();
        } else if (imagePeekByte() >= 0xD0 && imagePeekByte() <= 0xD7) {
          if (TRACE) System.err.printf("Processing Restart #%d marker 0x%X at offset 0x%X\n", restartNumber, imagePeekByte(), imageBufferIndex);
          imageReadByte(); // Skip the Restart marker
          imageBits = imageReadByte();
          if (imageBits == 0xFF) {
            System.err.printf("Error: found 0xFF straight after Restart marker at offset 0x%X\n", imageBufferIndex);
            System.exit(1);
          }
        } else {
          System.err.printf("Error: found non-zero byte 0x%X after 0xFF at offset 0x%X\n", imagePeekByte(), imageBufferIndex);
          System.exit(1);
        }
      }
    }

    int retValue = (imageBits & 0x80) >> 7;
    imageBits <<= 1;
    imageBitIndex++;

    return retValue;
  }

  private int imageReadBits(int n) {
    int retValue = 0;

    while (n-- > 0)
      retValue = (retValue << 1) | imageReadBit();

    return retValue;
  }

  private int imageReadHuffmanCode(int huffmanSymbolCounts[], int huffmanSymbols[][]) {
    int huffmanBit = 0;
    int huffmanPrefix = 0;

    for (int huffmanLength = 0; huffmanLength < 16; huffmanLength++) {
      huffmanBit = huffmanBit << 1 | imageReadBit();
      int huffmanSymbolCountAtThisLength = huffmanSymbolCounts[huffmanLength];
      if (huffmanSymbolCountAtThisLength > 0) {
        int huffmanSymbolNumber = huffmanBit - huffmanPrefix;
        if (huffmanSymbolNumber < huffmanSymbolCountAtThisLength) {
          return huffmanSymbols[huffmanLength][huffmanSymbolNumber];
        }
        huffmanPrefix += huffmanSymbolCountAtThisLength;
      }
      huffmanPrefix <<= 1;
    }

    System.err.printf("Error: unable to find huffman code at offset 0x%X\n", imageBufferIndex);
    System.exit(1);
    return 0; // This is here to silence the compiler - it can never be executed
  }

  private int convertToSigned(int i, int length) {
    if (i >> (length - 1) == 0)
      return i - (1 << length) + 1;
    else
      return i;
  }

  public ARTJPEGDecodeBaseline(String filename) throws IOException {
    constructorCore(loadFile(new File(filename)));
  }

  public ARTJPEGDecodeBaseline(File f) throws IOException {
    constructorCore(loadFile(f));
  }

  private void constructorCore(byte imageBuffer[]) {
    restartInterval = -1;
    quantisationTables = new int[4][64];
    huffmanSymbolCounts = new int[2][4][16];
    huffmanSymbols = new int[2][4][16][];
    luminanceDataUnitA = new int[64];
    luminanceDataUnitB = new int[64];
    luminanceDataUnitC = new int[64];
    luminanceDataUnitD = new int[64];
    chrominanceBDataUnit = new int[64];
    chrominanceRDataUnit = new int[64];
    luminanceDataUnitA_spatial = new int[64];
    luminanceDataUnitB_spatial = new int[64];
    luminanceDataUnitC_spatial = new int[64];
    luminanceDataUnitD_spatial = new int[64];
    chrominanceBDataUnit_spatial = new int[64];
    chrominanceRDataUnit_spatial = new int[64];
    final double bias = 1 / Math.sqrt(2);
    iDCTlookup = new double[64][64];
    // Initialise a lookup table for the inverse DCT which precomputes all possible xyuv values
    for (int xy = 0; xy < 64; xy++)
      for (int uv = 0; uv < 64; uv++) {
        double x = xy & 0x7;
        double y = xy >> 3;
        double u = uv & 0x7;
        double v = uv >> 3;
        iDCTlookup[xy][uv] = (u == 0 ? bias : 1.0) * (v == 0 ? bias : 1.0) * Math.cos(((2.0 * x + 1.0) * u * Math.PI) / 16.0)
            * Math.cos(((2.0 * y + 1.0) * v * Math.PI) / 16.0);
      }
    this.imageBuffer = imageBuffer;
    processFile();
  }

  byte[] loadFile(File f) throws IOException {
    byte buffer[] = new byte[(int) f.length()];
    DataInputStream dis = new DataInputStream(new FileInputStream(f));
    dis.readFully(buffer);
    dis.close();
    if (TRACE) System.err.printf("JPEG file length %d bytes\n", buffer.length);
    return buffer;
  }

  public void processFile() {

    for (imageBufferIndex = 0;;) {
      int marker = imageReadDoubleByte();
      if (TRACE) System.err.printf("JPEG marker %04x at %04x: ", marker, imageBufferIndex - 2);

      switch (marker) {
      default:
        if (marker >= JPG_marker_APP0) {
          parameterLength = imageReadDoubleByte();
          if (TRACE) System.err.printf("APP%1X\n", marker & 0xF);
          imageBufferIndex += parameterLength - 2; // imageGet2() advances the index, but the length includes the length bytes
          break;
        }

        System.err.printf("Error: unexpected marker - parameter length 0x%04x\n", imageBufferIndex - 4, parameterLength);
        System.exit(1);

      case JPG_marker_StartOfImage:
        if (TRACE) System.err.printf("StartOfImage\n");
        break;

      case JPG_marker_EndOfImage:
        if (TRACE) System.err.printf("EndOfImage\n");
        return;

      case JPG_marker_StartOfFrame_BaselineDCT:
        parameterLength = imageReadDoubleByte();
        if (TRACE) System.err.printf("StartOfFrame ");
        precision = imageReadByte();
        imageHeight = imageReadDoubleByte();
        imageWidth = imageReadDoubleByte();
        componentCount = imageReadByte();
        componentIdentifiers = new int[componentCount];
        horizontalSamplingFactors = new int[componentCount];
        verticalSamplingFactors = new int[componentCount];
        quantisationTableDestinations = new int[componentCount];
        for (int tmp = 0; tmp < componentCount; tmp++) {
          componentIdentifiers[tmp] = imageReadByte();
          horizontalSamplingFactors[tmp] = imageReadByte();
          verticalSamplingFactors[tmp] = horizontalSamplingFactors[tmp] & 0x0F;
          horizontalSamplingFactors[tmp] >>= 4;
          quantisationTableDestinations[tmp] = imageReadByte();
        }
        if (TRACE) {
          System.err.printf("precision: %d, width: %d, height: %d, componentCount: %d\n", precision, imageWidth, imageHeight, componentCount);

          for (int tmp = 0; tmp < componentCount; tmp++)
            System.err.printf("Component %d, identifier %d, horizontalSamplingFactor %d, verticalSamplingFactor %d, quantisationTableDestination %d\n", tmp,
                componentIdentifiers[tmp], horizontalSamplingFactors[tmp], verticalSamplingFactors[tmp], quantisationTableDestinations[tmp]);
        }
        break;

      case JPG_marker_DefineHuffmanTables:
        parameterLength = imageReadDoubleByte();
        if (TRACE) System.err.printf("DefineHuffmanTables\n");
        while (imagePeekByte() != 0xFF) {
          int htNumber = imageReadByte();
          int isACTable = (htNumber & 0x10) == 0 ? 0 : 1;
          htNumber &= 0x0F;
          if (TRACE) System.err.printf("%s table %d\n", isACTable == 0 ? "DC" : "AC", htNumber);
          for (int length = 0; length < 16; length++) {
            huffmanSymbolCounts[isACTable][htNumber][length] = imageReadByte();
            if (TRACE)
              System.err.printf("%s table %d length %d has %d entries\n", isACTable == 0 ? "DC" : "AC", htNumber, length + 1,
                  huffmanSymbolCounts[isACTable][htNumber][length]);
          }
          for (int length = 0; length < 16; length++)
            if (huffmanSymbolCounts[isACTable][htNumber][length] > 0) {
              huffmanSymbols[isACTable][htNumber][length] = new int[huffmanSymbolCounts[isACTable][htNumber][length]];
              for (int symbol = 0; symbol < huffmanSymbolCounts[isACTable][htNumber][length]; symbol++) {
                huffmanSymbols[isACTable][htNumber][length][symbol] = imageReadByte();
                if (TRACE)
                  System.err.printf("%s table %d at length %d, symbol %d = %d (0x%X)\n", isACTable == 0 ? "DC" : "AC", htNumber, length + 1, symbol,
                      huffmanSymbols[isACTable][htNumber][length][symbol], huffmanSymbols[isACTable][htNumber][length][symbol]);
              }
            }
          if (TRACE) {
            System.err.printf("%s table %d\n", isACTable == 0 ? "DC" : "AC", htNumber);
            for (int length = 0; length < 16; length++) {
              System.err.printf("Length %d, count %d: ", length, huffmanSymbolCounts[isACTable][htNumber][length]);
              for (int symbol = 0; symbol < huffmanSymbolCounts[isACTable][htNumber][length]; symbol++)
                System.err.printf(" %d (0X%X)", huffmanSymbols[isACTable][htNumber][length][symbol], huffmanSymbols[isACTable][htNumber][length][symbol]);
              System.err.printf("\n");
            }
          }

        }
        break;

      case JPG_marker_DefineQuantisationTables:
        parameterLength = imageReadDoubleByte();
        if (TRACE) System.err.printf("DefineQuantisationTables\n");
        while (imagePeekByte() != 0xFF) {
          int qtNumber = imageReadByte();
          int precision = 1;
          if ((qtNumber & 0xF0) != 0) precision = 2;
          qtNumber &= 0x0F;
          if (TRACE) System.err.printf("Quantisation table %d is %d-bit precision\n", qtNumber, precision * 8);
          for (int tmp = 0; tmp < 64; tmp++) {
            quantisationTables[qtNumber][tmp] = precision == 1 ? imageReadByte() : imageReadDoubleByte();
            if (TRACE) System.err.printf("Quantisation table %d location %d is %d\n", qtNumber, tmp, quantisationTables[qtNumber][tmp]);
          }

          if (TRACE) {
            System.err.printf("Quantisation table %d is %d-bit precision\n", qtNumber, precision * 8);
            for (int y = 0; y < 8; y++) {
              System.err.printf("Row %d: ", y);
              for (int x = 0; x < 8; x++) {
                System.err.printf("   %d", quantisationTables[qtNumber][rasterToLinear[y << 3 | x]]);
              }
              System.err.printf("\n");
            }
          }
        }
        break;

      case JPG_marker_DefineRestartInterval:
        parameterLength = imageReadDoubleByte();
        restartCounter = restartInterval = imageReadDoubleByte();
        if (TRACE) System.err.printf("DefineRestartInterval %d\n", restartInterval);
        break;

      case JPG_marker_StartOfScan:
        restartNumber = 0;
        imageBitIndex = 8; // Force reloading of imageBits on first access
        parameterLength = imageReadDoubleByte();
        if (TRACE) System.err.printf("StartOfScan ");
        scanComponentCount = imageReadByte();
        scanComponentSelectors = new int[scanComponentCount];
        DCEntropyEncodingTables = new int[scanComponentCount];
        ACEntropyEncodingTables = new int[scanComponentCount];
        for (int tmp = 0; tmp < scanComponentCount; tmp++) {
          scanComponentSelectors[tmp] = imageReadByte();
          DCEntropyEncodingTables[tmp] = imageReadByte();
          ACEntropyEncodingTables[tmp] = DCEntropyEncodingTables[tmp] & 0x0F;
          DCEntropyEncodingTables[tmp] >>= 4;
        }
        int startOfSpectralSelection = imageReadByte(); // Should be zero for sequential DCT
        int endOfSpectralSelection = imageReadByte(); // Should be 63 for sequential DCT
        int successiveApproximationBitHigh = imageReadByte();
        int successiveApproximationBitLow = successiveApproximationBitHigh & 0x0F;
        successiveApproximationBitHigh >>= 4;

        if (TRACE) {
          System.err
              .printf(
                  "scanComponentCount: %d, startOfSpectralSelection: %d, endOfSpectralSelection: %d, successiveApproximationBitHigh: %d, successiveApproximationBitLow: %d\n",
                  scanComponentCount, startOfSpectralSelection, endOfSpectralSelection, successiveApproximationBitHigh, successiveApproximationBitLow);
          for (int tmp = 0; tmp < scanComponentCount; tmp++)
            System.err.printf("Component: %d, scanComponentSelector: %d, DCEntropyEncodingTable: %d, ACEntropyEncodingTables: %d\n", tmp,
                scanComponentSelectors[tmp], DCEntropyEncodingTables[tmp], ACEntropyEncodingTables[tmp]);
        }
        scanDataImageBufferIndex = imageBufferIndex;
        return;
      }
    }
  }

  public void processImageData(byte[] pixelBuffer, int xOrg, int yOrg, int width, int height, boolean dcOnly) {
    this.pixelBufferWidth = dcOnly ? width >> 3 : width;
    this.pixelBuffer = pixelBuffer;
    imageBufferIndex = scanDataImageBufferIndex;
    restartNumber = 0;
    imageBitIndex = 8; // Force reloading of imageBits on first access

    if (TRACE) System.err.printf("Processing image data: %d x %d pixels\n", imageWidth, imageHeight);

    luminanceDCvalue = chrominanceRDCvalue = chrominanceBDCvalue = 0;

    if (horizontalSamplingFactors[0] == 1 && verticalSamplingFactors[0] == 1 && horizontalSamplingFactors[1] == 1 && verticalSamplingFactors[1] == 1
        && horizontalSamplingFactors[2] == 1 && verticalSamplingFactors[2] == 1) {
      if (TRACE) System.err.printf("Using 1:1; 1:1; 1:1 processing\n");

      for (int y = 0; y < imageHeight; y += 8) {
        for (int x = 0; x < imageWidth; x += 8) {

          if (DATAUNITTRACE) dataTracing = (x >= DATAUNITTRACE_LO_X && x < DATAUNITTRACE_HI_X && y >= DATAUNITTRACE_LO_Y && y < DATAUNITTRACE_HI_Y);

          if (DATAUNITTRACE) if (dataTracing) System.err.printf("Processing data unit at (%d, %d)\n", x, y);

          luminanceDCvalue = processDataUnit(luminanceDCvalue, DCEntropyEncodingTables[0], ACEntropyEncodingTables[0],
              quantisationTables[quantisationTableDestinations[0]], luminanceDataUnitA);
          if (!dcOnly) inverseDCT(luminanceDataUnitA, luminanceDataUnitA_spatial);

          chrominanceBDCvalue = processDataUnit(chrominanceBDCvalue, DCEntropyEncodingTables[1], ACEntropyEncodingTables[1],
              quantisationTables[quantisationTableDestinations[1]], chrominanceBDataUnit);
          if (!dcOnly) inverseDCT(chrominanceBDataUnit, chrominanceBDataUnit_spatial);

          chrominanceRDCvalue = processDataUnit(chrominanceRDCvalue, DCEntropyEncodingTables[2], ACEntropyEncodingTables[2],
              quantisationTables[quantisationTableDestinations[2]], chrominanceRDataUnit);
          if (!dcOnly) inverseDCT(chrominanceRDataUnit, chrominanceRDataUnit_spatial);

          if (dcOnly)
            paintDataUnitDC(x >> 3, y >> 3, luminanceDataUnitA, chrominanceBDataUnit, chrominanceRDataUnit, spreadMapSimple);
          else
            paintDataUnit(x, y, luminanceDataUnitA_spatial, chrominanceBDataUnit_spatial, chrominanceRDataUnit_spatial, spreadMapSimple);

          processRestartCounter();
        }
      }
    } else if (horizontalSamplingFactors[0] == 2 && verticalSamplingFactors[0] == 2 && horizontalSamplingFactors[1] == 1 && verticalSamplingFactors[1] == 1
        && horizontalSamplingFactors[2] == 1 && verticalSamplingFactors[2] == 1) {
      if (TRACE) System.err.printf("Using 2:2; 1:1; 1:1 processing\n");

      for (int y = 0; y < imageHeight; y += 16) {
        for (int x = 0; x < imageWidth; x += 16) {

          boolean suppressPixelWrite = x < xOrg || x >= xOrg + width || y < yOrg || y >= yOrg + height;
          boolean performIDC = !(dcOnly || suppressPixelWrite);

          if (DATAUNITTRACE) dataTracing = (x >= DATAUNITTRACE_LO_X && x < DATAUNITTRACE_HI_X && y >= DATAUNITTRACE_LO_Y && y < DATAUNITTRACE_HI_Y);

          if (DATAUNITTRACE) if (dataTracing) System.err.printf("Processing data unit at (%d, %d)\n", x, y);

          luminanceDCvalue = processDataUnit(luminanceDCvalue, DCEntropyEncodingTables[0], ACEntropyEncodingTables[0],
              quantisationTables[quantisationTableDestinations[0]], luminanceDataUnitA);
          if (performIDC) inverseDCT(luminanceDataUnitA, luminanceDataUnitA_spatial);

          luminanceDCvalue = processDataUnit(luminanceDCvalue, DCEntropyEncodingTables[0], ACEntropyEncodingTables[0],
              quantisationTables[quantisationTableDestinations[0]], luminanceDataUnitB);
          if (performIDC) inverseDCT(luminanceDataUnitB, luminanceDataUnitB_spatial);

          luminanceDCvalue = processDataUnit(luminanceDCvalue, DCEntropyEncodingTables[0], ACEntropyEncodingTables[0],
              quantisationTables[quantisationTableDestinations[0]], luminanceDataUnitC);
          if (performIDC) inverseDCT(luminanceDataUnitC, luminanceDataUnitC_spatial);

          luminanceDCvalue = processDataUnit(luminanceDCvalue, DCEntropyEncodingTables[0], ACEntropyEncodingTables[0],
              quantisationTables[quantisationTableDestinations[0]], luminanceDataUnitD);
          if (performIDC) inverseDCT(luminanceDataUnitD, luminanceDataUnitD_spatial);

          chrominanceBDCvalue = processDataUnit(chrominanceBDCvalue, DCEntropyEncodingTables[1], ACEntropyEncodingTables[1],
              quantisationTables[quantisationTableDestinations[1]], chrominanceBDataUnit);
          if (performIDC) inverseDCT(chrominanceBDataUnit, chrominanceBDataUnit_spatial);

          chrominanceRDCvalue = processDataUnit(chrominanceRDCvalue, DCEntropyEncodingTables[2], ACEntropyEncodingTables[2],
              quantisationTables[quantisationTableDestinations[2]], chrominanceRDataUnit);
          if (performIDC) inverseDCT(chrominanceRDataUnit, chrominanceRDataUnit_spatial);

          if (!suppressPixelWrite) {
            int vpX = x - xOrg;
            int vpY = y - yOrg;

            if (!dcOnly) {
              paintDataUnit(vpX, vpY, luminanceDataUnitA_spatial, chrominanceBDataUnit_spatial, chrominanceRDataUnit_spatial, spreadMapA);
              paintDataUnit(vpX + 8, vpY, luminanceDataUnitB_spatial, chrominanceBDataUnit_spatial, chrominanceRDataUnit_spatial, spreadMapB);
              paintDataUnit(vpX, vpY + 8, luminanceDataUnitC_spatial, chrominanceBDataUnit_spatial, chrominanceRDataUnit_spatial, spreadMapC);
              paintDataUnit(vpX + 8, vpY + 8, luminanceDataUnitD_spatial, chrominanceBDataUnit_spatial, chrominanceRDataUnit_spatial, spreadMapD);
            } else {
              paintDataUnitDC(vpX >> 3, vpY >> 3, luminanceDataUnitA, chrominanceBDataUnit, chrominanceRDataUnit, spreadMapA);
              paintDataUnitDC((vpX + 8) >> 3, vpY >> 3, luminanceDataUnitB, chrominanceBDataUnit, chrominanceRDataUnit, spreadMapB);
              paintDataUnitDC(vpX >> 3, (vpY + 8) >> 3, luminanceDataUnitC, chrominanceBDataUnit, chrominanceRDataUnit, spreadMapC);
              paintDataUnitDC((vpX + 8) >> 3, (vpY + 8) >> 3, luminanceDataUnitD, chrominanceBDataUnit, chrominanceRDataUnit, spreadMapD);
            }
          }

          processRestartCounter();
        }
      }
    } else {
      System.err.printf("Error: unsupported subscan mode: exiting\n");
      System.exit(1);
    }
  }

  private int processDataUnit(int dcValue, int DCHuffmanTableNumber, int ACHuffmanTableNumber, int[] quantisationTable, int[] block) {

    if (DATAUNITTRACE) if (dataTracing) System.err.printf("\nprocessDataUnit() at offset: %X\n", imageBufferIndex);

    for (int xy = 0; xy < 64; xy++)
      block[xy] = 0;

    int huffmanResult = imageReadHuffmanCode(huffmanSymbolCounts[0][DCHuffmanTableNumber], huffmanSymbols[0][DCHuffmanTableNumber]);
    if (DATAUNITTRACE) if (dataTracing) System.err.printf("DC value huffman code: %d  (0x%X)\n", huffmanResult, huffmanResult);
    int bitString = imageReadBits(huffmanResult);
    if (DATAUNITTRACE) if (dataTracing) System.err.printf("Read bit string 0x%X\n", bitString);

    block[0] = dcValue + (convertToSigned(bitString, huffmanResult) * quantisationTable[0]);
    if (DATAUNITTRACE) if (dataTracing) System.err.printf("DC value: %d\n", block[0]);

    if (DATAUNITTRACE) if (dataTracing) System.err.printf("Loaded [0, 0] with DC element %d\n", block[0]);

    for (int i = 1; i < 64; i++) {
      huffmanResult = imageReadHuffmanCode(huffmanSymbolCounts[1][ACHuffmanTableNumber], huffmanSymbols[1][ACHuffmanTableNumber]);
      if (DATAUNITTRACE) if (dataTracing) System.err.printf("Element %d: found AC value Huffman code %d (0x%X); ", i, huffmanResult, huffmanResult);
      if (huffmanResult == 0) break; // End of block, so rest of block is zero

      i += huffmanResult >> 4;
      if (i < 64) {
        int dataLength = huffmanResult & 0xF;
        int acValue = convertToSigned(imageReadBits(dataLength), dataLength) * quantisationTable[i];
        int coordinate = linearToRaster[i];
        block[coordinate] = acValue;
        if (DATAUNITTRACE) if (dataTracing) System.err.printf("loaded [%d, %d] with AC element %d\n", coordinate & 7, coordinate >> 3, block[coordinate]);
      }
    }

    if (DATAUNITTRACE) if (dataTracing) {
      System.err.printf("\nprocessDataUnit() at offset: %X\n", imageBufferIndex);
      for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 8; x++)
          System.err.printf(" %5d ", block[y << 3 | x], block[y << 3 | x]);
        System.err.printf("\n");
      }
    }

    return block[0];
  }

  private void inverseDCT(int[] inputDataUnit, int[] outputDataUnit) {
    for (int xy = 0; xy < 64; xy++) {
      outputDataUnit[xy] = 0;
      for (int uv = 0; uv < 64; uv++)
        outputDataUnit[xy] += (int) (inputDataUnit[uv] * iDCTlookup[xy][uv]);
      outputDataUnit[xy] >>= 2;
    }

    if (DATAUNITTRACE) if (dataTracing) {
      System.err.printf("\nInverseDCT in hexadecimal\n");
      for (int y = 0; y < 8; y++) {
        for (int x = 0; x < 8; x++)
          System.err.printf(" %02X ", outputDataUnit[y << 3 | x] & 0xFF, outputDataUnit[y << 3 | x] & 0xFF);
        System.err.printf("\n");
      }
    }
  }

  private final double offset = 128;

  void writePixel(int x, int y, byte red, byte green, byte blue) {
    int i = (x + y * pixelBufferWidth) * 3;

    if (i > pixelBuffer.length - 4) return;

    pixelBuffer[i++] = red;
    pixelBuffer[i++] = green;
    pixelBuffer[i++] = blue;
  }

  void paintDataUnit(int x, int y, int[] luminanceDataUnitA, int[] chrominanceBDataUnit, int[] chrominanceRDataUnit, int[] spreadMap) {
    if (DATAUNITTRACE)
      if (dataTracing) {
        System.err.printf("\nPixel YCbCr data in hexadecimal\n");
        for (int yy = 0; yy < 8; yy++) {
          for (int xx = 0; xx < 8; xx++)
            System.err.printf("%02X%02X%02X  ", luminanceDataUnitA[yy << 3 | xx] & 0xFF, chrominanceBDataUnit[yy << 3 | xx] & 0xFF,
                chrominanceRDataUnit[yy << 3 | xx] & 0xFF);
          System.err.printf("\n");
        }
      }

    int xxyy = 0;
    for (int py = 0; py < 8; py++)
      for (int px = 0; px < 8; px++) {

        double Y = luminanceDataUnitA[xxyy] + offset;
        double Cb = chrominanceBDataUnit[spreadMap[xxyy]];
        double Cr = chrominanceRDataUnit[spreadMap[xxyy]];

        xxyy++;

        double rDiff = 1.402 * Cr;
        double gDiff = -0.3441 * Cb - 0.71414 * Cr;
        double bDiff = 1.772 * Cb;

        writePixel(x + px, y + py, saturate(Y + rDiff), saturate(Y + gDiff), saturate(Y + bDiff));
      }
  }

  void paintDataUnitDC(int x, int y, int[] luminanceDataUnitA, int[] chrominanceBDataUnit, int[] chrominanceRDataUnit, int[] spreadMap) {
    // System.err.printf("paintDataUnitDC %d, %d\n", x, y);
    double Y = offset + (luminanceDataUnitA[0] * iDCTlookup[0][0]) / 4;
    double Cb = (chrominanceBDataUnit[0] * iDCTlookup[0][0]) / 4;
    double Cr = (chrominanceRDataUnit[0] * iDCTlookup[0][0]) / 4;

    double rDiff = 1.402 * Cr;
    double gDiff = -0.3441 * Cb - 0.71414 * Cr;
    double bDiff = 1.772 * Cb;

    byte rDiffSaturatedValue = saturate(Y + rDiff);
    byte gDiffSaturatedValue = saturate(Y + gDiff);
    byte bDiffSaturatedValue = saturate(Y + bDiff);

    writePixel(x, y, rDiffSaturatedValue, gDiffSaturatedValue, bDiffSaturatedValue);
  }

  void processRestartCounter() {
    if (restartInterval > 0) if (--restartCounter == 0) {
      restartNumber++;
      restartCounter = restartInterval;
      imageBitIndex = 8; // Restart bit level input rountines
      luminanceDCvalue = chrominanceRDCvalue = chrominanceBDCvalue = 0;
    }
  }

}
