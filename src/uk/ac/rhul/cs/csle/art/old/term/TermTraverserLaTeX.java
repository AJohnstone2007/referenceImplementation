package uk.ac.rhul.cs.csle.art.old.term;

public class TermTraverserLaTeX extends TermTraverserText {

  public TermTraverserLaTeX(ITerms iTerms, String name) {
    super(iTerms, name);
  }

  public String renderSymbolAsLatexString(String string) {
    // If the string has any alphanumeric content then
    // 1 Two leading underscores means special symbol \artSpecial{}
    // 2 One leading underscore means variable \artVariable{}
    // 3 Leading backquote means \artCharacterLiteral
    // 4 Leading quote means \artCaseSensitiveliteral
    // 5 Leading double quote means \artCaseInsensitiveliteral
    // 6 Otherwise \artconstructor

    // a trailing P means add a prime, and one trailing digit means a subscript
    String opening = "", closing = "}";
    boolean hasPrime = false;
    boolean hasSubscript = false;
    char subscript = '?';
    int start = 0, end = string.length();
    boolean hasAlpha = false;
    // if the string starts with a backslash, we assume it is already LaTeX
    if (string.charAt(0) == '\\') return string;

    for (int i = 0; i < string.length(); i++)
      if (Character.isAlphabetic(string.charAt(i))) {
        hasAlpha = true;
        break;
      }

    if (string.charAt(0) == '`') {
      opening = "\\artCaseInsensitiveLiteral{";
      start = 1;
    }
    if (string.charAt(0) == '\'' && string.charAt(string.length() - 1) == '\'') {
      opening = "\\artCaseSensitiveLiteral{";
      start = 1;
      end = string.length() - 1;
    } else if (string.charAt(0) == '"' && string.charAt(string.length() - 1) == '"') {
      opening = "\\artCaseInsensitiveLiteral{";
      start = 1;
      end = string.length() - 1;
    } else if (hasAlpha) {
      if (string.charAt(0) == '_') {
        if (string.charAt(1) == '_') {
          opening = "\\artSpecial{";
          start = 2;
        } else {
          opening = "\\artVariable{";
          start = 1;

          if (string.charAt(end - 1) == 'P') {
            hasPrime = true;
            end--;
          }

          if (Character.isDigit(subscript = string.charAt(end - 1))) {
            hasSubscript = true;
            end--;
          }

        }
      } else
        opening = "\\artConstructor{\\sf ";
    } else
      closing = "";

    string = string.substring(start, end);
    String ret = "";
    for (int i = 0; i < string.length(); i++)
      switch (string.charAt(i)) {
      case '{':
        ret += "\\{";
        break;
      case '}':
        ret += "\\}";
        break;
      case '%':
        ret += "\\%";
        break;
      case '&':
        ret += "\\&";
        break;
      case '#':
        ret += "\\#";
        break;
      case '\\':
        ret += "\\\\";
        break;
      case '_':
        ret += "\\_";
        break;
      case '^':
        ret += "\\^";
        break;
      case '$':
        ret += "\\$";
        break;
      case '`':
        ret += "\\`{}";
        break;
      default:
        ret += string.charAt(i);
      }

    if (ret.equals("sig"))
      ret = "$\\sigma$";
    else if (ret.equals("rho"))
      ret = "$\\rho$";
    else if (ret.equals("phi"))
      ret = "$\\phi$";
    else if (ret.equals("delta"))
      ret = "$\\delta$";
    else if (ret.equals("nu")) ret = "$\\nu$";

    ret = opening + ret;
    if (hasSubscript) ret += ("$_{" + subscript + "}$");
    if (hasPrime) ret += ("\\/$^\\prime$");
    ret += closing;

    return ret;
  }
}
