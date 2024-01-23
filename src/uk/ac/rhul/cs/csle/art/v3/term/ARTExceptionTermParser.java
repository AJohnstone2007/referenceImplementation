package uk.ac.rhul.cs.csle.art.v3.term;

public class ARTExceptionTermParser extends Exception {

  private final String errorMessage;
  private final int inputIndex;
  private final String input;

  public ARTExceptionTermParser(String message, int stringIndex, String input) {
    super(message + " at index " + stringIndex);
    this.errorMessage = message;
    this.inputIndex = stringIndex;
    this.input = input;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public int getInputIndex() {
    return inputIndex;
  }

  public String getInput() {
    return input;
  }

}
