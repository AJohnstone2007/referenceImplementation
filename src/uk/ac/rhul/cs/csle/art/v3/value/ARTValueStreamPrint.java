package uk.ac.rhul.cs.csle.art.v3.value;

import java.io.PrintStream;
import java.util.Map;

public class ARTValueStreamPrint extends ARTValue {
  public ARTValueStreamPrint(PrintStream stream, String name) {
    this.name = name;
    this.payload = stream;
  }

  String name;
  PrintStream payload;

  @Override
  public String toString() {
    return "Output stream: " + name;
  }

  @Override
  public String toLatexString(Map<String, String> map) {
    return "Output stream: " + name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof ARTValueStreamPrint)) return false;
    ARTValueStreamPrint other = (ARTValueStreamPrint) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }

  @Override
  public ARTValue output(ARTValue v) {
    payload.print(v.toString());
    return this;
  }

  @Override
  public Object getPayload() {
    return null;
  }

}
