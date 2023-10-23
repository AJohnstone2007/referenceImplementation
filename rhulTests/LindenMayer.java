package lindenmayer;

import java.util.HashMap;
import java.util.Scanner;

public class LindenMayerSimple {
  static HashMap rules = new HashMap();
  static String form;

  public static void main(String[] args) {

    Scanner kb = new Scanner(System.in);

    while (true) {
      System.out.print(" LHS character? ");
      Character lhs = kb.next().charAt(0);
      if (!Character.isLetter(lhs)) break;
      System.out.print(" RHS string? ");
      rules.put(lhs, kb.next());
    }

    System.out.print(" Start string? ");
    form = kb.next();
    kb.close();

    for (int step = 0; step < 10; step++) {
      String newForm = "";

      for (int ci = 0; ci < form.length(); ci++)
        if (rules.get(form.charAt(ci)) == null)
          newForm += form.charAt(ci);
        else
          newForm += rules.get(form.charAt(ci));

      System.out.println(form + " => " + newForm);
      form = newForm;
    }
  }
}
