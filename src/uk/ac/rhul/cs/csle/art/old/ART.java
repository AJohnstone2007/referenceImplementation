package uk.ac.rhul.cs.csle.art.old;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import uk.ac.rhul.cs.csle.art.old.core.ARTV4;
import uk.ac.rhul.cs.csle.art.old.core.ARTV4OLD;
import uk.ac.rhul.cs.csle.art.old.core.Version;
import uk.ac.rhul.cs.csle.art.old.v3.ARTV3;

public class ART {
  public static void main(String[] args) {
    new ART(args);
  }

  public ART(String[] args) {
    StringBuilder sb = new StringBuilder();
    if (args.length == 0) {
      System.err.println("ART " + Version.version() + ": no arguments supplied");
      System.exit(1);
    }

    int restOfLine = 0;
    if (args[0].charAt(0) != '!') {
      try {
        sb.append(Files.readString(Paths.get(args[0])));
      } catch (IOException e) {
        System.err.println("ART: unable to read file " + args[0]);
        System.exit(1);
      }
      restOfLine = 1;
      if (args.length > 1 && args[1].charAt(0) != '!') restOfLine = 2;
    }

    for (int i = restOfLine; i < args.length; i++) // Catenate the rest of the arguments
      sb.append(args[i] + " ");

    if (args.length > 1 && args[1].charAt(0) != '!') sb.append("!try \"" + args[1] + "\"\n");

    // System.out.println("ART specification string: " + sb);
    String specification = sb.toString();
    if (specification.contains("!v3"))
      new ARTV3(specification);
    else if (specification.contains("!v4old "))
      new ARTV4OLD(specification);
    else
      new ARTV4(specification);

  }
}
