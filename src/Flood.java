import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;

import javalib.worldimages.*;
import java.util.Random;

//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;

  // color and is the cell flooded?
  Color color;
  boolean flooded;

  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  static final int TILE_SIZE = 20;
  static ArrayList<Color> colorKey = new ArrayList<Color>(Arrays.asList(Color.blue, Color.cyan,
      Color.red, Color.green, Color.yellow, Color.magenta, Color.orange));

  // constructor
  Cell(int x, int y, int color) {
    this.x = x;
    this.y = y;

    this.color = colorKey.get(color);
    this.flooded = false;
  }

  /*-
   *Fields:
   * ...this.x ... int
   * ...this.y ... int
   * ...this.color ... Color
   * ...this.flooded ... boolena
   * ...this.left ... Cell
   * ...this.right ... Cell
   * ...this.top ... Cell
   * ...this.bottom ... Cell
   *Methods:
   * ...this.draw() ... WorldImage
   * ...this.link(String, Cell) ... void
   * 
   */

  // draws the image of the cell
  WorldImage draw() {
    return new RectangleImage(TILE_SIZE, TILE_SIZE, OutlineMode.SOLID, this.color);
  }

  // links the adjacent cells
  void link(String loc, Cell other) {
    if (loc.equals("top")) {
      this.top = other;
    }
    if (loc.equals("left")) {
      this.left = other;
    }
    if (loc.equals("right")) {
      this.right = other;
    }
    if (loc.equals("bottom")) {
      this.bottom = other;
    }
  }

}

// represents the world
class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<Cell> board = new ArrayList<Cell>(1);

  // Defines an int constant
  int boardSize = 25;
  Random ran;
  int colors = 3;
  int maxMoves = 10;

  WorldScene previous = this.getEmptyScene();

  ArrayList<Cell> toBeDrawn = new ArrayList<Cell>(1);

  int numMoves = 0;

  boolean flooding = true;
  boolean start = true;
  boolean running = true;

  // set the size of the window
  int height;
  int width;

  // constructor
  FloodItWorld(Random ran, int height, int width, int colors, int size) {
    this.ran = ran;
    
    boardSize = size;
    this.colors = colors;

    makeBoard(boardSize);

    linkBoard(boardSize);

    flood(board.get(0).color);

    this.toBeDrawn.clear();

    this.flooding = false;
    this.start = true;

    // literally taken from source code
    maxMoves = (int) (Math.floor((25 * (2 * boardSize) * colors)) / (28 * 6));
  }

  FloodItWorld(int height, int width, int colors, int size) {
    this.ran = new Random();
    
    boardSize = size;
    this.colors = colors;

    makeBoard(boardSize);

    linkBoard(boardSize);

    flood(board.get(0).color);

    this.flooding = false;
    this.start = true;
    maxMoves = (int) (Math.floor((25 * (2 * boardSize) * colors)) / (28 * 6));
  }

  /*-
   *Fields:
   * ...this.board... ArrayList<T>
   * ...this.ran ... Random
   * ...this.toBeDrawn ... ArrayList<T>
   * ...this.flooding ... boolean
   * ...this.start ... boolean
   * ...this.running ... boolean
   * ...this.colors ... int
   * ...this.BOARD_SIZE ... int
   * ...this.numMoves ... int
   * ...this.maxMoves ... int
   *Methods:
   * ...this.makeBoard(int) ... void
   * ...this.linkBoard(int) ... void
   * ...this.makeScene() ... WorldScene
   * ...this.onKeyEvent(String) ... void
   * ...this.onMouseClicked(String) ... void
   * ...this.flood(Color) ... void
   * ...this.win() ... boolean
   *Methods for Fields:
   * ...this.board.add(Cell) ... ArrayList<T>
   * ...this.ran.nextInt(int) ... int
   * ...this.board.get(int).flooded ... boolean
   * 
   */

  // makes the board of the game with cells
  void makeBoard(int size) {
    this.board.clear();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        this.board.add(new Cell(i, j, this.ran.nextInt(colors)));
      }
    }
    board.get(0).flooded = true;

  }

  // links the cells of the board
  void linkBoard(int size) {
    for (int j = 0; j < size; j++) {
      for (int i = 0; i < size; i++) {
        if (i != 0) {
          this.board.get(i * size + j).link("top", this.board.get((i - 1) * size + j));
        }
        if (i != size - 1) {
          this.board.get(i * size + j).link("bottom", this.board.get((i + 1) * size + j));
        }
        if (j != 0) {
          this.board.get(i * size + j).link("left", this.board.get(i * size + j - 1));
        }
        if (j != size - 1) {
          this.board.get(i * size + j).link("right", this.board.get(i * size + j + 1));
        }
      }
    }
  }

  // makes the scene and draws it
  public WorldScene makeScene() {
    WorldScene mt = this.getEmpty();

    ArrayList<Cell> temp = (ArrayList<Cell>) this.board.clone();
    temp.removeAll(this.toBeDrawn);

    for (Cell e : temp) {
      mt.placeImageXY(e.draw(), e.TILE_SIZE * e.x + e.TILE_SIZE / 2,
          e.TILE_SIZE * e.y + e.TILE_SIZE / 2);
    }

    if (this.start) {
      for (Cell e : board) {
        mt.placeImageXY(e.draw(), e.TILE_SIZE * e.x + e.TILE_SIZE / 2,
            e.TILE_SIZE * e.y + e.TILE_SIZE / 2);
      }
      this.start = false;
    }

    if (this.toBeDrawn.size() == 0) {
      this.flooding = false;
    }

    if (flooding) {
      this.previous = mt;
      Cell e = this.toBeDrawn.get(0);
      previous.placeImageXY(e.draw(), e.TILE_SIZE * e.x + e.TILE_SIZE / 2,
          e.TILE_SIZE * e.y + e.TILE_SIZE / 2);
      this.toBeDrawn.remove(0);
      return previous;
    }

    if (this.win()) {
      this.running = false;
      mt.placeImageXY(new TextImage("YOU WIN IN " + this.numMoves + " MOVES", 40,
          FontStyle.BOLD_ITALIC, Color.orange), 250, 250);
    }

    if (this.numMoves > maxMoves) {
      this.running = false;
      mt.placeImageXY(new TextImage("Ran Out Of Moves", 40, FontStyle.BOLD_ITALIC, Color.orange),
          250, 250);
    }

    return mt;
  }

  WorldScene getEmpty() {
    WorldScene mt = this.getEmptyScene();
    mt.placeImageXY(new TextImage("MOVES: " + (this.maxMoves - this.numMoves) + "/" + this.maxMoves,
        40, FontStyle.BOLD, Color.black), 250, 550);
    return mt;

  }

  // registers key events and changes the world
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.numMoves = 0;
      this.start = true;
      this.running = true;
      this.makeBoard(boardSize);
      this.linkBoard(boardSize);
      this.previous = this.getEmptyScene();
      flood(board.get(0).color);
      this.toBeDrawn.clear();
    }
    return;
  }

  // registers location of mouse event and changes world
  public void onMouseClicked(Posn pos) {
    if (this.running) {
      for (Cell e : board) {
        if (pos.x >= e.x * 20 && pos.x < e.x * 20 + 20 && pos.y >= e.y * 20
            && pos.y < e.y * 20 + 20) {
          if (!(e.color.equals(board.get(0).color))) {
            this.numMoves++;
          }
          this.flooding = true;
          flood(e.color);
        }
      }
    }
  }

  // floods the adjacent cells if not flooded and same color
  public void flood(Color color) {
    for (Cell c : board) {
      if (c.flooded) {
        c.color = color;
        if (c.top != null && c.top.color.equals(color)) {
          c.top.flooded = true;
        }
        if (c.left != null && c.left.color.equals(color)) {
          c.left.flooded = true;
        }
        if (c.right != null && c.right.color.equals(color)) {
          c.right.flooded = true;
        }
        if (c.bottom != null && c.bottom.color.equals(color)) {
          c.bottom.flooded = true;
        }
        this.toBeDrawn.add(c);
      }
    }
  }

  // did the user win?
  public boolean win() {
    boolean test = true;
    for (Cell e : board) {
      test = test && e.flooded;
    }
    return test;
  }

}

// represents the examples class
class ExamplesFlood {

  // Examples for drawing
  Cell test1 = new Cell(5, 5, 1);
  Cell test2 = new Cell(5, 5, 2);
  Cell test3 = new Cell(5, 5, 3);
  Cell test4 = new Cell(5, 5, 4);
  Cell test5 = new Cell(5, 5, 5);
  Cell test6 = new Cell(5, 5, 6);
  Cell test7 = new Cell(5, 5, 0);

  RectangleImage draw1 = new RectangleImage(20, 20, OutlineMode.SOLID, Color.cyan);
  RectangleImage draw2 = new RectangleImage(20, 20, OutlineMode.SOLID, Color.red);
  RectangleImage draw3 = new RectangleImage(20, 20, OutlineMode.SOLID, Color.green);
  RectangleImage draw4 = new RectangleImage(20, 20, OutlineMode.SOLID, Color.yellow);
  RectangleImage draw5 = new RectangleImage(20, 20, OutlineMode.SOLID, Color.magenta);
  RectangleImage draw6 = new RectangleImage(20, 20, OutlineMode.SOLID, Color.orange);
  RectangleImage draw7 = new RectangleImage(20, 20, OutlineMode.SOLID, Color.BLUE);

  void testDraw(Tester t) {
    t.checkExpect(this.test1.draw(), this.draw1);
    t.checkExpect(this.test2.draw(), this.draw2);
    t.checkExpect(this.test3.draw(), this.draw3);
    t.checkExpect(this.test4.draw(), this.draw4);
    t.checkExpect(this.test5.draw(), this.draw5);
    t.checkExpect(this.test6.draw(), this.draw6);
    t.checkExpect(this.test7.draw(), this.draw7);
  }

  // Examples for linking
  Cell link1 = new Cell(5, 5, 0);
  Cell linkL = new Cell(5, 5, 0);
  Cell linkR = new Cell(5, 5, 0);
  Cell linkT = new Cell(5, 5, 0);
  Cell linkB = new Cell(5, 5, 0);

  void testLink(Tester t) {
    this.linkL.link("left", this.link1);
    this.linkR.link("right", this.link1);
    this.linkT.link("top", this.link1);
    this.linkB.link("bottom", this.link1);
    t.checkExpect(this.linkL.left, this.link1);
    t.checkExpect(this.linkR.right, this.link1);
    t.checkExpect(this.linkT.top, this.link1);
    t.checkExpect(this.linkB.bottom, this.link1);
  }

  // examples for makeBoard
  Random ran = new Random(1234);
  FloodItWorld game = new FloodItWorld(ran, 700, 500, 2, 1);

  Cell make1 = new Cell(0, 0, 0);
  ArrayList<Cell> board1 = new ArrayList<Cell>();

  
  FloodItWorld game2 = new FloodItWorld(ran, 700, 500, 2, 2);
  Cell make2 = new Cell(0, 0, 0);
  Cell make3 = new Cell(0, 1, 1);
  Cell make4 = new Cell(1, 0, 0);
  Cell make5 = new Cell(1, 1, 0);
  ArrayList<Cell> board2 = new ArrayList<Cell>();

  // fails every other time due to random
  // Test done with colors set to 2
  void testMakeBoard(Tester t) {
    game.makeBoard(1);
    this.make1.flooded = true;
    this.board1.add(this.make1);
    t.checkExpect(this.game.board, this.board1);

    game2.makeBoard(2);
    this.make2.flooded = true;
    this.board2.add(this.make2);
    this.board2.add(this.make3);
    this.board2.add(this.make4);
    this.board2.add(this.make5);

    t.checkExpect(this.game2.board, this.board2);
  }

  // tests MakeScene
  void testMakeScene(Tester t) {
    FloodItWorld game3 = new FloodItWorld(new Random(0), 600, 500, 1, 1);
    game3.makeBoard(1);
    WorldScene mtscene = new FloodItWorld(600, 500, 2, 2).getEmptyScene();
    t.checkExpect(game3.makeScene(), mtscene);

    FloodItWorld game2 = new FloodItWorld(new Random(4), 600, 500, 2, 2);
    game2.makeBoard(2);
    t.checkExpect(game2.makeScene(), game.makeScene());
  }
  
  // tests the method onMouseClick
  void testOnMouseClick(Tester t) {
    FloodItWorld game3 = new FloodItWorld(new Random(0), 500, 600, 2, 2);
    game3.makeBoard(1);
    t.checkExpect(game3.numMoves, 0);
    t.checkExpect(game3.flooding, false);
    t.checkExpect(game3.start, true);
    game3.onMouseClicked(new Posn(5,5));
    t.checkExpect(game3.numMoves, 0);
    t.checkExpect(game3.flooding, true);
    t.checkExpect(game3.start, true);
    game3.onMouseClicked(new Posn(50,50));
    t.checkExpect(game3.numMoves, 0);
    t.checkExpect(game3.flooding, true);
    t.checkExpect(game3.start, true);
    
    FloodItWorld game4 = new FloodItWorld(new Random(4), 600, 500, 2, 2);
    
    game4.makeBoard(2);
    t.checkExpect(game4.numMoves, 0);
    t.checkExpect(game4.flooding, false);
    game4.onMouseClicked(new Posn(0,0));
    t.checkExpect(game4.numMoves, 0);
    t.checkExpect(game4.flooding, true);
    game4.onMouseClicked(new Posn(30,30));
    t.checkExpect(game4.numMoves, 1);
    t.checkExpect(game4.flooding, true);
    //t.checkExpect(game.makeScene(), null);
  }
  
  // tests method onKeyEvent
  void testOnKeyEvent(Tester t) {
    FloodItWorld game4 = new FloodItWorld(new Random(1), 500, 600, 2, 2);
    game4.makeBoard(1);
    game4.numMoves = 5;
    game4.flooding = false;
    game4.start = false;
    game4.running = false;
    
    t.checkExpect(game4.numMoves, 5);
    t.checkExpect(game4.flooding, false);
    t.checkExpect(game4.start, false);
    t.checkExpect(game4.running, false);
    game4.onKeyEvent("r");
    t.checkExpect(game4.numMoves, 0);
    t.checkExpect(game4.flooding, false);
    t.checkExpect(game4.start, true);
    t.checkExpect(game4.running, true);
    
  }
  
  // tests the flood method
  void testFlood(Tester t) {
    FloodItWorld game5 = new FloodItWorld(new Random(2), 500, 600, 2 ,2);
    game5.makeBoard(2);
    t.checkExpect(game5.board.get(0).flooded, true);
    t.checkExpect(game5.board.get(0).color, Color.blue);
    t.checkExpect(game5.board.get(1).flooded, false);
    t.checkExpect(game5.board.get(1).color, Color.cyan);
    game5.flood(Color.red);
    t.checkExpect(game5.board.get(0).flooded, true);
    t.checkExpect(game5.board.get(1).flooded, false);
    game5.flood(Color.blue);    
    t.checkExpect(game5.board.get(0).flooded, true);
    t.checkExpect(game5.board.get(0).color, Color.blue);
    t.checkExpect(game5.board.get(1).flooded, false);
    t.checkExpect(game5.board.get(1).color, Color.cyan);
    game5.flood(Color.green);    
    t.checkExpect(game5.board.get(0).flooded, true);
    t.checkExpect(game5.board.get(0).color, Color.green);
    t.checkExpect(game5.board.get(1).color, Color.cyan);
    t.checkExpect(game5.board.get(1).flooded, false);
    
  }
  
  // tests the method win
  void testWin(Tester t) {
    FloodItWorld game6 = new FloodItWorld(new Random(2), 500, 600, 2, 2);
    game6.makeBoard(2);
    t.checkExpect(game6.win(), false);
    

    game6.board.get(0).flooded = true;
    game6.board.get(1).flooded = true;
    game6.board.get(2).flooded = true;
    game6.board.get(3).flooded = true;
    game6.board.get(0).color = Color.BLACK;
    game6.board.get(1).color = Color.BLACK;
    game6.board.get(2).color = Color.BLACK;
    game6.board.get(3).color = Color.BLACK;
    t.checkExpect(game6.win(), true);
    
    game6.makeBoard(1);
    game6.board.get(0).flooded = true;
    t.checkExpect(game6.win(), true);
    
  }



  void testGame(Tester t) {
    // t.checkExpect(game.win(), true);
    FloodItWorld playGame = new FloodItWorld(ran, 600, 500, 4, 25);
    //playGame.bigBang(500, 600, .01);
  }
}
