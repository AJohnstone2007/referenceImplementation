import java.io.BufferedReader;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.*;
import java.io.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Color;



class Ex2 {
    public static void ex2( )
     {
       int x = 7 -- 9 ;
       System.out.println(x);
  }
}

public class Chuck {
	public static String extractJoke(String json){
		int i = json.indexOf("\'joke\'");
		String end = json.substring(i+9);
		i = end.indexOf("\'");
		String joke = end.substring(0, i);
		return joke;
	}
	
	private static String callService (String arg) throws Exception {
		URL url = new URL("http://api.icndb.com"+arg);
		URLConnection connection = url.openConnection();
		BufferedReader buffer = 
		      new BufferedReader(
                          new InputStreamReader(connection.getInputStream()));	
		String temp;
		StringBuffer response = new StringBuffer();
		while ((temp = buffer.readLine()) != null) {
			response.append(temp);
		}
		buffer.close();
		return response.toString();
	}
	
	public static void main(String[] args) throws Exception {
	    Scanner keyboard = new Scanner(System.in);
		System.out.println("Welcome-to-the-Internet-Chuck-Norris-database!");
		System.out.println("Joke-of-the-day");

		String json = callService("/jokes/random/");
		String joke = extractJoke(json);
		System.out.println(joke);

		System.out.println("(-1)_Quit_(0)_Ramdom_joke_(n)_Joke_number_n");
		System.out.println("What_do_you_want_to_do?");

	  int value = keyboard.nextInt();
          while (value != -1) {
	     if ((value < 0) || (value > 344)){
       	 System.out.println("Please_enter_an_integer_between_-1_and_344"); }
	     else { 
                 if (value == 0) {
		    json = callService("/jokes/random/");
		    joke = extractJoke(json);
		    System.out.println(joke); }
                 else {
                    json = callService("/jokes/value");
                    joke = extractJoke(json);
		    System.out.println(joke); } } ;
             System.out.println("(-1)_Quit_(0)_Ramdom_joke_(n)_Joke_number_n");
	     System.out.println("What_do_you_want_to_do?");
	     value = keyboard.nextInt(); } ;
	   int x = dBTR(); 
           System.out.println("x_=" + x); 
    Ex2.ex2() ;
    AddDoubles.dob() ;
    Ex53.dBTR();  }
}

class AddDoubles
{
  public static void dob()
  {
    Scanner scanner = new Scanner(System.in);
    double firstDouble, secondDouble, sum;

    System.out.print("First_number?-");
    firstDouble = scanner.nextDouble();
    System.out.print("Second_number?-");
    secondDouble = scanner.nextDouble();
    sum = firstDouble + secondDouble;
    System.out.println("Sum_is_" + sum);
  }
}

class Extra
{
public static void extra()
    {
	Scanner br = new Scanner(System.in);

	int x;
	double interestRate = 0.07, carBought = 50, carCost = 7000, 
           carPrice = 8500, salaryCost = 40000;
	double profit=0, total=0, debt=0, paid=0, interest, income=0;
	int  needToSell;


	for(int i=1; i<4; i++) {
	  if (i>1) 
            System.out.println("interest_rate_for_year_" + i + "_will_be_" + interestRate);
  	  System.out.print("Enter_number_of_cars_sold_in_year_" + i + "_");
 	  x = br.nextInt();

          debt = debt - paid + carBought*carCost - profit;
  	  interest = debt * interestRate;

          paid = x*carCost;
	  income  = x*carPrice - paid - salaryCost;
	  profit = income - interest;
	  total += profit; 

	  if (profit < 0) { interestRate = 0.1; }
	  else { interestRate = 0.06; }
	}

	System.out.println("final_3_year_profit_" + total);
    }
}


class Ex53{

static int dBTR( ) {
    int stop = 1,x, z=-1;
    Scanner input = new Scanner(System.in);
    while (stop==1) {
      System.out.print("enter_an_integer_");
      x = input.nextInt();

      if ( x>=0 && x<=300) {
	 if(x%3==0) { z= x/3; }
         else { if (x%3==1) { z= (x-1)/3; }
	        else { z= (x+1)/3; } 
              } 
       }
      else { if ( x<=-1 && x >= -300) { z= -x; }
             else  System.out.println("sorry_value_out_of_range");
      } 
      System.out.print("enter_0_to_terminate_or_1_to_repeat_"); 
      stop = input.nextInt(); }
    return z;
}
}

class Ex47{ 
 public static void ex47() {
   int[] intArr = new int[30];
   for ( int x = 0; x < 30; x++ ) {intArr[x] = 1;};
   Scanner input = new Scanner(System.in);
   System.out.print("enter_an_integer_");
   int i = input.nextInt();    
   while ( i>=0 && i<30) {
        intArr[i] = 2; 
        System.out.print("enter_an_integer_");
        i = input.nextInt(); }; 
   for ( int x = 0; x < 30; x++ ) { System.out.println(intArr[x]); } } }


class Ex41{ 
 public static void ex41() {
     int x = 171, y = 2;
     while ( x>=0 ){ 
          y+=5; x-=6; 
          System.out.println("x=_" + x); };
     System.out.println("y=_" + y); } }


public class Graduate extends Student {
    private DegreeClass result;
    Graduate(String name, int i, ProgrammeName p, DegreeClass c) {
        super(name, i, p, 7); 
        result = c; }
    public void setClass(DegreeClass c) { result = c; }
    public void setStudyYear(int n) {
         System.out.println("Year_of_study_cannot_be_reset"); }
    public DegreeClass getDegreeClass(){ return this.result; }  }


public class Student
{

private String studentName;
private int studentNumber;
private ProgrammeName degreeTitle;
private int studyYear;

Student(String name, int number, ProgrammeName p, int year) {
    studentName = name;
    studentNumber = number;
    degreeTitle = p;
    studyYear = year; }

public void setName(String s) { studentName = s; }

public void setNumber(int i) { studentNumber = i; }

public void setProgramme(ProgrammeName n) { degreeTitle = n; }

public void setStudyYear(int n) { studyYear = n; }

public String getName(){ return studentName; }

public int getNumber(){ return studentNumber; }

public ProgrammeName getProgramme(){ return degreeTitle; }

public int getStudyYear(){ return studyYear; }
}



class TelephoneEntry {
    String name;
    int number;
    TelephoneEntry(String n, int num) { name = n; number = num; }
    public String getName(){ return name; }
    public int getNumber() { return number; }
    public String toString() { return  name + "___telephone:_" + number ; } }


class TestTelephoneGUI {
  public static void testTel() { 
    TelephoneEntry t = new TelephoneEntry("Henry_Brown", 2084713303);
    javax.swing.JOptionPane.showMessageDialog(null, t.toString()); } }

public class Points{
    double xCoord, yCoord;
    Points(double x, double y) { xCoord = x; yCoord = y; }
    public static void line(Points p, Points q, double[] a) {
        if(p.xCoord != q.xCoord) {
            a[0] = (p.yCoord - q.yCoord)/(p.xCoord-q.xCoord);
            a[1] = p.yCoord - a[0]*p.xCoord; }
        else {
            a[0] = Double.POSITIVE_INFINITY;
            a[1] = p.xCoord; }; } }

class FindLine
{
    public static void findLine()
    {
      Scanner input = new Scanner(System.in);
      Points p1, p2;
      double[] lineValues = new double[2];
      double[] coords = new double[2];

      System.out.print("Enter-x-then-y-coordinates_of_first_point:_");
      coords[0] = input.nextDouble();
      coords[1] = input.nextDouble();

      p1 = new Points(coords[0], coords[1]);

      System.out.print("Enter-x-then-y-coordinates-of-second-point:-");
      coords[0] = input.nextDouble();
      coords[1] = input.nextDouble();

      p2 = new Points(coords[0], coords[1]);

      Points.line(p1,p2,lineValues);

      if(lineValues[0]!=Double.POSITIVE_INFINITY)    
        System.out.println("The-equation-of-the-line-is:-y-=-" +
		       lineValues[0]+"x_+_"+lineValues[1]);
      else
        System.out.println("The-equation-of-the-line-is:-x-=-" + lineValues[1]);
    }
}

class Point{
  private double x, y;
  private String label;

  Point(double iX, double iY, String iLabel)
  { x= iX; y= iY; label = iLabel; }

  Point(double iX, double iY)
  { x= iX; y= iY; label = "";}

  public void set(double iX, double iY) { x = iX; y = iY; }
  public void set(double iX, double iY, String iLabel) 
  { x = iX; y = iY; label = iLabel;}
  public double getX() { return x; }
  public double getY() { return y; }
  public String getLabel() { return label; }

  public String toString() 
    { return "(" + x +",-" + y + ")-\'" + label + "'"; }
}

class ComplexPolygon{
    protected Point[] points;
    protected int maxPoints;
    protected int nextFreePoint = 0;

    ComplexPolygon(int vertexCount) 
    {
      points = new Point[vertexCount]; maxPoints = vertexCount; 

	for (int i = 0; i < maxPoints; i++)
	    points[i] = new Point(0,0, "");
    }

    void addPoint(double x, double y)
    { points[nextFreePoint++].set(x, y); }

    void addPoint(double x, double y, String label)
    { points[nextFreePoint++].set(x, y, label); }

    Point getPoint(int index)
    { return points[index]; }

    int getPointCount() { return nextFreePoint; }
}

class ConstrainedComplexPolygon extends ComplexPolygon {
    double minX, minY, maxX, maxY;

    ConstrainedComplexPolygon(int vertexCount, double x1, double y1, double x2, double y2) 
    {
	super(vertexCount);
	minX = x1;
	minY = y1;
	maxX = x2;
	maxY = y2;

    }

    public void addPoint(double iX, double iY) {
	if (iX < minX ) {
	    System.out.println("Warning-x-value-too-small-setting-to-" + minX);
	    iX = minX; }
	else if (iX > maxX ) {
	    System.out.println("Warning-x-value-too-large-setting-to-" + maxX);
	    iX = maxX; };
	if (iY < minY ) {
	    System.out.println("Warning-y-value-too-small-setting-to-" + minY);
	    iY = minY; }
	else if (iY > maxY ) {
	    System.out.println("Warning-y-value-too-large-setting-to-" + maxY);
	    iY = maxY; };
	 super.addPoint(iX, iY);
	}

    public void addPoint(double iX, double iY, String iLabel) {
	if (iX < minX ) {
	    System.out.println("Warning-x-value-too-small-setting-to-" + minX);
	    iX = minX; }
	else if (iX > maxX ) {
	    System.out.println("Warning-x-value-too-large-setting-to-" + maxX);
	    iX = maxX; };
	if (iY < minY ) {
	    System.out.println("Warning-y-value-too-small-setting-to-" + minY);
	    iY = minY; }
	else if (iY > maxY ) {
	    System.out.println("Warning-y-value-too-large-setting-to-" + maxY);
	    iY = maxY; };
	super.addPoint(iX, iY, iLabel);
	}
}

class TestConstrainedComplexPolygonEas {
    public static void testPoly() {
      ConstrainedComplexPolygon poly = new
	  ConstrainedComplexPolygon(5, 0.0, 2.5, 8.1, 34);
      poly.addPoint(0,4,"pt1");
      poly.addPoint(27,-3,"pt2");
      poly.addPoint(1.1,36);
      poly.addPoint(1,1,"pt1"); } }

class DoubleLinkedList{
    private Double payload;
    private DoubleLinkedList next;

    DoubleLinkedList(Double payload, DoubleLinkedList next) {
	this.payload = payload; this.next = next; }
    DoubleLinkedList getNext() { return next; }
    Double getPayload() { return payload; }
    void setPayload() { this.payload = payload;} }

class LoadDoubles {
    public static void main(String[] args) {
	DoubleLinkedList myList ; 
	myList = new DoubleLinkedList(1.0, null);
        myList = new DoubleLinkedList(3.4, myList);  
        myList = new DoubleLinkedList(9.0, myList);  
        myList = new DoubleLinkedList(-15.3, myList);  
        myList = new DoubleLinkedList(25.0, myList);  

        DoubleLinkedList current = myList;
        while ( current != null) {
	    System.out.println(current.getPayload());
	    current = current.getNext(); }; } }
 
class StringTree
{
  private StringTree leftChild;
  private StringTree rightChild;
  private String payload;

  StringTree (String payload) { this.payload = payload; }

  StringTree getLeftChild() { return leftChild; }
  StringTree getRightChild() { return rightChild; }
  String getPayload() { return payload; }

  StringTree addLeftChild(String payload) { 
                return leftChild = new StringTree(payload); }
  StringTree addRightChild(String payload) { 
                return rightChild = new StringTree(payload); }

  void printInorder()
    {
      if (leftChild != null) leftChild.printInorder();
      System.out.println(payload);
      if (rightChild != null) rightChild.printInorder();
    }

  void printPreorder()
    {
      System.out.println(payload);
      if (leftChild != null) leftChild.printPreorder();
      if (rightChild != null) rightChild.printPreorder();
    }

  void printPostorder()
    {
      if (leftChild != null) leftChild.printPostorder();
      if (rightChild != null) rightChild.printPostorder();
      System.out.println(payload);
    }
}

class Lab6StringTree
{
  public static void testTree()
  {
    StringTree aTree = new StringTree("root"), temp = aTree;

    temp = temp.addLeftChild("+");
    temp.addLeftChild("15");
    temp = temp.addRightChild("*");
    temp.addLeftChild("x");
    temp.addRightChild("y");

    System.out.println("Preorder-traversal:");
    aTree.printPreorder();
    System.out.println("Inorder-traversal:");
    aTree.printInorder();
    System.out.println("Postorder-traversal:");
    aTree.printPostorder();
  }
}

class StringLinkedList
{
  protected StringLinkedList next;  
  protected String payload;

  StringLinkedList(String payload, StringLinkedList next)
  {
    this.payload = payload;
    this.next = next;
  }

  StringLinkedList getNext() { return next; }
  String getPayload() { return payload; }

  public String toString() {return (String) payload; }   
    
  void printReverse()
  {
    System.out.println(this); 
    if (next != null)
      next.printReverse();
  }

  void printForward()
  {
    if (next != null)
      next.printForward();
    System.out.println(this);
  }

  void findValue(String value)
  {
    if (payload.equals(value))
      System.out.println("element-on-the-list");
    else 
    {
      if (next != null)
        next.findValue(value);
      else
	System.out.println("element-not-found");
    }
  }
}

class NumberedStringLinkedList extends StringLinkedList
{
  static int count = 1;   
  int number;             

  NumberedStringLinkedList(String payload, NumberedStringLinkedList next)
  { 
    super(payload, next); 
    number = count++; 
  }

  public String toString() {return (String) payload + "-at-element-" + number; }
}

class LoadStringList
{
  static String getString(Scanner sc, String prompt)
  {
      System.out.print(prompt);
      return sc.nextLine();
  }

  public static void loadStrTest()
  {
    Scanner keyboard = new Scanner(System.in);

    NumberedStringLinkedList aList = null;

    System.out.println("Empty-string-terminates-input");

    while (true)
    {
      String value = getString(keyboard, "String:-");

      if (value.equals(""))
        break;

      aList = new NumberedStringLinkedList(value, aList);
    }

    System.out.println("Forward-listing\n");
    aList.printForward();

    System.out.println("Reverse-listing\n");
    aList.printReverse();

    System.out.println("\nSearch-for-\"Adrian\"\n");
    aList.findValue("Adrian");
  }
}

public class Count {
    public static void count() throws FileNotFoundException{
	File inputFile = new File("scanTest.txt");
	Scanner reader = new Scanner(inputFile);
	int numTok = 0, numInt = 0, numFloat = 0, numStr = 0;
	while (reader.hasNext()) {
	    numTok++;
	    if(reader.hasNextInt()) {
		numInt++; reader.nextInt(); }
	    else if(reader.hasNextDouble()) {
		numFloat++; reader.nextDouble(); }
	    else {
		numStr++; reader.next(); }
	}
	System.out.printf("The-number-of-tokens-is-%d\n", numTok);
	System.out.printf("The-number-of-integers-is-%d\n", numInt);
	System.out.printf("The-number-of-decimals-is-%d\n", numFloat);
	System.out.printf("The-number-of-strings-is-%d\n", numStr);
    }
}

class ClickData
{
  int[][] grid;

  ClickData(int x, int y) {grid = new int[y][x];}

  public int getX() {return grid[0].length; }
  public int getY() {return grid.length; }
  public int getCell(int x, int y) {return grid[y][x];}
  public void setCell(int x, int y, int v) {grid[y][x] = v;}
}

class Click
{
  public static void clickRun() throws IOException, FileNotFoundException
  {
    int squareSize = 10, x = 30, y = 40;
    String file = "out.lif";

    if(args.length>0) {
	for(int i=0; i<args.length; i++) {
            if(args[i].equals("-x")) {i++; x=Integer.parseInt(args[i]); }
	    else if(args[i].equals("-y")){ i++; y=Integer.parseInt(args[i]); }
		 else file = args[i]; } }
                      

    ClickData click = new ClickData(x, y);
    ClickFrame frame = new ClickFrame();
    ClickComponent component = new ClickComponent(click, squareSize);
    PrintWriter outFile = new PrintWriter(file);



    JButton button = new JButton("Clear");
    button.addActionListener(new ClearListener(click, frame));

    JButton writeButton = new JButton("Write-File");
    writeButton.addActionListener(new WriteListener(click, frame,outFile));
    frame.add(component, BorderLayout.CENTER);
    frame.add(button, BorderLayout.SOUTH);
    frame.add(writeButton, BorderLayout.NORTH);
    frame.pack();
  }
}

class ClearListener implements ActionListener
{
  ClickFrame f;
  ClickData c;

  ClearListener(ClickData c, ClickFrame f)
  { this.c = c; this.f = f; }
  
  public void actionPerformed(ActionEvent e)
  {  
    for (int y = 0; y < c.getY(); y++)
      for (int x = 0; x < c.getX(); x++)
        c.setCell(x, y, 0);    
    f.repaint();
  }
}

class WriteListener implements ActionListener
{
  ClickFrame f;
  ClickData c;
  PrintWriter outFile;

  WriteListener(ClickData c, ClickFrame f, PrintWriter file)
  { this.c = c; this.f = f; this.outFile = file;}
  
  public void actionPerformedQu7(ActionEvent e)
  {  
      for (int i = 0; i<c.getY() ; i++) {
	  for (int j = 0; j<c.getX() ; j++) {
	      if(c.getCell(j,i) == 1 ) System.out.print("*");
	      else System.out.print("_"); }
	   System.out.print("\n"); }
    f.repaint();
  }

  public void actionPerformed(ActionEvent e)
  {  
      for (int i = 0; i<c.getY() ; i++) {
	  for (int j = 0; j<c.getX() ; j++) {
	      if(c.getCell(j,i) == 1 ) outFile.print("*");
	      else outFile.print("_"); }
	   outFile.print("\n"); }
    f.repaint();
    outFile.close();
  }
}

class ClickFrame extends JFrame
{
  ClickFrame() 
  {
    setTitle("Click-me");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }
}

class ClickComponent extends JComponent implements MouseInputListener
{
  private ClickData c;
  private int squareSize;
    private Color map[] = {Color.WHITE, Color.RED, Color.GREEN, Color.CYAN, 
                                     Color.BLUE, Color.BLACK, Color.MAGENTA};

  ClickComponent(ClickData c, int squareSize) 
  {
    this.c = c; this.squareSize = squareSize;

    setPreferredSize(new Dimension(c.getX() * squareSize, c.getY() * squareSize));
    addMouseMotionListener(this);
    addMouseListener(this);
  }

  protected void paintComponent(Graphics g) 
  {
    super.paintComponent(g);

    for (int y = 0; y < c.getY(); y++)
      for (int x = 0; x < c.getX(); x++)
      {
        g.setColor(map[c.getCell(x,y) % map.length]);
        g.fillRect(x * squareSize,y * squareSize, squareSize, squareSize);
      }
  }

  public void mousePressed(MouseEvent e) {}
  public void mouseDragged(MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e) 
  {
    int x = e.getX(), y = e.getY();

    System.out.println("Click-(" + x + ",_" + y + ")");

    x /= squareSize; y /= squareSize;

    c.setCell(x, y, (c.getCell(x,y)+1)%2);

    this.repaint();
  }
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}
  public void mouseMoved(MouseEvent e) {}
}
