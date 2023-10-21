import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.io.*;
import java.util.Random;

class LifeGridVersion1
{
  private int generationLimit;
  private int generation = 1;
  int[][] grid;
  int[][] newGrid;
  boolean textMode;
  Random generator = new Random();

  LifeGridVersion1(int x, int y, int generationLimit, String filename, boolean textMode, boolean randomField) throws IOException, FileNotFoundException
  {
    grid = new int [x][y];
    newGrid = new int [x][y];

    this.generationLimit = generationLimit;
    readFile(filename);
    if (randomField)
      randomiseGrid();
    this.textMode = textMode;
  }

  private void randomiseGrid()
  {
    for (int y = 1; y < grid.length - 1; y++)
      for (int x = 1; x < grid[y].length - 1; x++)
	  if (generator.nextDouble() > 0.5)
	    grid[y][x] = 1;
  }

  private void readFile(String filename) throws IOException, FileNotFoundException
  {
    BufferedReader br = new BufferedReader(new FileReader(filename));

    for (int y = 0; y < grid.length; y++)
    {
      String line = br.readLine();
      if (line == null)
	  break;

      for (int i = 0; i < line.length() && i < grid[y].length; i++)
        if (line.charAt(i) == '*')
	  grid[y][i] = 1;
    }
  }

  private void show()
  {
    System.out.println("\nGeneration-" + generation);
    
    for (int y = 0; y < grid.length; y++)
    {
      for (int x = 0; x < grid[y].length; x++)
        System.out.print(grid[y][x] > 0 ? '*' : '_');

      System.out.println();  
    }
  }

  private int neighbours(int x, int y)
  {
    int count = 0;

    if (grid[y-1][x+1] != 0) count++;
    if (grid[y-1][x]   != 0) count++;
    if (grid[y-1][x-1] != 0) count++;
    if (grid[y+1][x+1] != 0) count++;
    if (grid[y+1][x]   != 0) count++;
    if (grid[y+1][x-1] != 0) count++;
    if (grid[y]  [x-1] != 0) count++;
    if (grid[y]  [x+1] != 0) count++;

    return count;
  }
  
  public int getX() {return grid[0].length; }
  public int getY() {return grid.length; }
  public int getGeneration() {return generation; }
  public int getCell(int x, int y) {return grid[y][x];}

  public void run(LifeFrame frame)
  {
    for (int y = 0; y < grid.length; y++)
      newGrid[y] = new int[grid[y].length];
        
    while (true)
    {
      if (generationLimit != 0 && generation > generationLimit)
        return;

      for (int y = 1; y < grid.length - 1; y++)
        for (int x = 1; x < grid[y].length - 1; x++)
        {
	  int neighbourCount = neighbours(x, y);

          if (grid[y][x] == 0)
          {
	    if (neighbourCount == 3)
	      newGrid[y][x] = generation;
            else 
	      newGrid[y][x] = grid[y][x];
	  }
	  else
          {
	    if (neighbourCount < 2 | neighbourCount > 3)
	      newGrid[y][x] = 0;
            else 
	      newGrid[y][x] = grid[y][x];
	  }
      } 

      int [][] swapGrid = grid;
      grid = newGrid;
      newGrid = swapGrid;

      frame.repaint();
      if (textMode)
        show();

      generation++;
    }  
  }
}

class LifeFrame extends JFrame 
{
  int squareSize;

  public LifeFrame(LifeGridVersion1 l, int squareSize) 
  {
    setTitle("Life");
    this.squareSize = squareSize;
    setContentPane(new LifePanel(l, squareSize));
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(l.getX() * squareSize, l.getY() * squareSize);
    setVisible(true);
  }
}

class LifePanel extends JPanel 
{
  LifeGridVersion1 l;
  int squareSize;
  Color map[] = {Color.RED, Color.GREEN, Color.CYAN, Color.BLUE};

  LifePanel(LifeGridVersion1 l, int squareSize) {this.l = l; this.squareSize = squareSize;}

  protected void paintComponent(Graphics g) 
  {
    super.paintComponent(g);

    for (int y = 0; y < l.getY(); y++)
      for (int x = 0; x < l.getX(); x++)
      {
	int cell = l.getCell(x,y);

        if (cell > 0)
	{
	  Color col = Color.BLACK;
	  
          if (l.getGeneration() - cell < map.length)
	      col = map[l.getGeneration() - cell];

          g.setColor(col);
          g.fillRect(x * squareSize,y * squareSize, squareSize, squareSize);
	}
      }
  }
}

class Life
{
  private static void helpMessage()
  {
    System.err.println("usage:-java-[options]-Life-<file>\n\nOptions\n\n-x-<num>-width-of-grid\n-y-<num>-height-of-grid\n-g-<num>-stop-after-<num>-generations\n-s-square-size-in-graphics-display\n-t-text-mode-toggle\n-r-use-random-field");
    System.exit(0);
  }

  public static void main(String[] args) throws IOException, FileNotFoundException
  {
    int argIndex = 0;
    int x = 20, y = 20, generations = 0, squareSize = 5;
    String filename;
    boolean textMode = false;
    boolean randomField = false;
 
    System.err.println("arg-count" + args.length);
    while (argIndex < args.length && args[argIndex].charAt(0) == '-')
      switch (args[argIndex].charAt(1))
      {
            case 'x': x = Integer.parseInt(args[argIndex + 1]); argIndex += 2; break;
            case 'y': y = Integer.parseInt(args[argIndex + 1]); argIndex += 2; break;
            case 'g': generations = Integer.parseInt(args[argIndex + 1]); argIndex += 2; break;
            case 's': squareSize = Integer.parseInt(args[argIndex + 1]); argIndex += 2; break;
            case 't': textMode = !textMode; argIndex += 1; break;
            case 'r': randomField = !randomField; argIndex += 1; break;
            default: helpMessage();
      }
    if (argIndex == args.length)
    	helpMessage();
    
        filename = args[argIndex];

        LifeGridVersion1 life = new LifeGridVersion1(x, y, generations, filename, textMode, randomField);
    LifeFrame frame = new LifeFrame(life, squareSize);
    life.run(frame);
  }
}
