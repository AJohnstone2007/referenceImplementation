class Sieve
{
  public static void main(String args[])
  {
    int upperbound = 50_000_000;
    int buffer[] = new int[upperbound];

    for (int base = 2; base < upperbound; base++)
      for (int multiple = 2 * base; 
           multiple < upperbound; 
           multiple = multiple + base)
        buffer[multiple] = 1;

    // Output results
    for (int base = 2; base < upperbound; base++)
      if (buffer[base] == 0)
        System.out.println(base);
  }
}
