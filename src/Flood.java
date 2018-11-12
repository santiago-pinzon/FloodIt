import java.util.ArrayList;
import java.util.Arrays;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.math.BigInteger;

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
  static final int BOARD_SIZE = 25;
  Random ran;
  static final int colors = 2;

  WorldScene previous = this.getEmptyScene();

  ArrayList<Cell> toBeDrawn = new ArrayList<Cell>(1);

  int numMoves = 0;

  boolean flooding = true;
  boolean start = true;
  boolean running = true;

  // constructor
  FloodItWorld(Random ran) {
    this.ran = ran;

    makeBoard(BOARD_SIZE);

    linkBoard(BOARD_SIZE);

    flood(board.get(0).color);

    this.toBeDrawn = (ArrayList<Cell>) this.board.clone();

    this.flooding = false;
    this.start = true;
  }

  FloodItWorld() {
    this.ran = new Random();

    makeBoard(BOARD_SIZE);

    linkBoard(BOARD_SIZE);

    flood(board.get(0).color);

    this.toBeDrawn = (ArrayList<Cell>) this.board.clone();

    this.flooding = false;
    this.start = true;
  }

  /*-
   *Fields:
   * ...this.board... ArrayList<T>
   * ...this.ran ... Random
   * ...this.toBeDrawn ... ArrayList<T>
   * ...this.flooding ... boolean
   * ...this.start ... boolean
   *Methods:
   * ...this.makeBoard(int) ... void
   * ...this.linkBoard(int) ... void
   * ...this.makeScene() ... WorldScene
   * ...this.onKeyEvent(String) ... void
   * ...this.onMouseClicked(String) ... void
   * ...this.flood(Color) ... void
   * ...this.win() ... boolean
   * ...this.onTick() ... void
   * ...this.worldEnds() ... WorldEnd
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
    if (this.toBeDrawn.size() == 0) {
      this.flooding = false;
    }
    if (this.start) {
      for (Cell e : board) {
        previous.placeImageXY(e.draw(), e.TILE_SIZE * e.x + e.TILE_SIZE / 2,
            e.TILE_SIZE * e.y + e.TILE_SIZE / 2);
      }
      this.start = false;

      return previous;

    }
    if (flooding) {
      Cell e = this.toBeDrawn.get(0);
      previous.placeImageXY(e.draw(), e.TILE_SIZE * e.x + e.TILE_SIZE / 2,
          e.TILE_SIZE * e.y + e.TILE_SIZE / 2);
      this.toBeDrawn.remove(0);
    }
    if (this.win()) {
      this.running = false;
      previous.placeImageXY(new TextImage("YOU WIN IN " + this.numMoves + " MOVES", 40,
          FontStyle.BOLD_ITALIC, Color.orange), 250, 250);
    }
    return previous;
  }

  // registers key events and changes the world
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.numMoves = 0;
      this.start = true;
      this.running = true;
      this.makeBoard(BOARD_SIZE);
      this.linkBoard(BOARD_SIZE);
      flood(board.get(0).color);
      this.toBeDrawn = (ArrayList<Cell>) this.board.clone();
    }
    return;
  }

  // registers location of mouse event and changes world
  public void onMouseClicked(Posn pos) {
    this.flooding = true;
    this.numMoves++;
    if (this.running) {
      for (Cell e : board) {
        if (pos.x >= e.x * 20 && pos.x < e.x * 20 + 20 && pos.y >= e.y * 20
            && pos.y < e.y * 20 + 20) {
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

  // ticks the world to check for win condition
  public void onTick() {
    this.win();
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
  FloodItWorld game = new FloodItWorld(ran);

  Cell make1 = new Cell(0, 0, 1);
  ArrayList<Cell> board1 = new ArrayList<Cell>();

  Cell make2 = new Cell(0, 0, 1);
  Cell make3 = new Cell(0, 1, 1);
  Cell make4 = new Cell(1, 0, 0);
  Cell make5 = new Cell(1, 1, 0);
  ArrayList<Cell> board2 = new ArrayList<Cell>();

  // Test done with colors set to 2
  void testMakeBoard(Tester t) {
    game.makeBoard(1);
    this.make1.flooded = true;
    this.board1.add(this.make1);
    t.checkExpect(this.game.board, this.board1);

    game.makeBoard(2);
    this.make2.flooded = true;
    this.board2.add(this.make2);
    this.board2.add(this.make3);
    this.board2.add(this.make4);
    this.board2.add(this.make5);

    t.checkExpect(this.game.board, this.board2);
  }

  // tests MakeScene
  void testMakeScene(Tester t) {
    FloodItWorld game3 = new FloodItWorld(new Random(0));
    game3.makeBoard(1);
    WorldScene mtscene = new FloodItWorld().getEmptyScene();
    t.checkExpect(game3.makeScene(), mtscene);

    FloodItWorld game2 = new FloodItWorld(new Random(4));
    game2.makeBoard(2);
    t.checkExpect(game2.makeScene(), game.makeScene());
  }

  void testGame(Tester t) {
    // t.checkExpect(game.win(), true);
    FloodItWorld playGame = new FloodItWorld(ran);
    playGame.bigBang(500, 500, 0.00000001);
  }
}
