package uk.ac.rhul.cs.csle.art.old.util.graphics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;

public class ARTSTLASCIIParser extends ARTSTLParser {

  String filename;
  Scanner scanner;
  int facetCount = 0;

  public ARTSTLASCIIParser(String filename) {
    System.out.println("Opening ASCII STL " + filename);
    try {
      scanner = new Scanner(new File(filename));
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("Unable to open file " + filename);
    }
    ARTCoord tmp = new ARTCoord(); // A throwaway coordinate that we can use when running through the facet count

    scanner.next("solid");
    scanner.next(); // Skip mode name

    // Scan the entire file counting the facets
    while (scanner.hasNext("facet")) {
      facetCount++;
      readFacet(tmp, tmp, tmp, tmp);
    }

    if (scanner.next("endsolid") == null) throw new ARTUncheckedException("Ill-formed ASCII STL input");

    scanner.close();

    try {
      scanner = new Scanner(new File(filename));
    } catch (FileNotFoundException e) {
      throw new ARTUncheckedException("Unable to open ASCII STL file " + filename);
    }
    scanner.next("solid");
    scanner.next(); // Skip model name; we're now all set for external calls to readFacet();

  }

  @Override
  public int getFacetCount() {
    return facetCount;
  }

  @Override
  public void readFacet(ARTCoord normal, ARTCoord vertex1, ARTCoord vertex2, ARTCoord vertex3) {
    scanner.next("facet");
    scanner.next("normal");
    normal.setX(scanner.nextFloat());
    normal.setY(scanner.nextFloat());
    normal.setZ(scanner.nextFloat());

    scanner.next("outer");
    scanner.next("loop");

    scanner.next("vertex");
    vertex1.setX(scanner.nextFloat());
    vertex1.setY(scanner.nextFloat());
    vertex1.setZ(scanner.nextFloat());

    scanner.next("vertex");
    vertex2.setX(scanner.nextFloat());
    vertex2.setY(scanner.nextFloat());
    vertex2.setZ(scanner.nextFloat());

    scanner.next("vertex");
    vertex3.setX(scanner.nextFloat());
    vertex3.setY(scanner.nextFloat());
    vertex3.setZ(scanner.nextFloat());

    scanner.next("endloop");
    scanner.next("endfacet");
  }
}
