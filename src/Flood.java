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

  Cell(int x, int y, int color) {
    this.x = x;
    this.y = y;

    this.color = colorKey.get(color);
    this.flooded = false;
  }

  WorldImage draw() {
    return new RectangleImage(TILE_SIZE, TILE_SIZE, OutlineMode.SOLID, this.color);
  }

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

class FloodItWorld extends World {
  // All the cells of the game
  ArrayList<Cell> board = new ArrayList<Cell>(1);
  // Defines an int constant
  static final int BOARD_SIZE = 25;
  Random ran;
  static final int colors = 2;

  FloodItWorld(Random ran) {
    this.ran = ran;

    makeBoard(BOARD_SIZE);

    linkBoard(BOARD_SIZE);
    
    flood(board.get(0).color);
  }

  FloodItWorld() {
    this.ran = new Random();

    makeBoard(BOARD_SIZE);

    linkBoard(BOARD_SIZE);
    
    flood(board.get(0).color);
  }

  void makeBoard(int size) {
    this.board.clear();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        this.board.add(new Cell(i, j, this.ran.nextInt(colors)));
      }
    }
    board.get(0).flooded = true;

  }

  void linkBoard(int size) {
    for (int j = 0; j < size; j++) {
      for (int i = 0; i < size; i++) {
        if (i != 0) {
          this.board.get(i * size + j).link("top", this.board.get((i - 1) * size + j));
        }
        if (i != size - 1) {
          this.board.get(i * size + j).link("bottom",
              this.board.get((i + 1) * size + j));
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

  @Override
  public WorldScene makeScene() {
    WorldScene mt = this.getEmptyScene();
    for (Cell e : board) {
      mt.placeImageXY(e.draw(), e.TILE_SIZE * e.x + e.TILE_SIZE / 2,
          e.TILE_SIZE * e.y + e.TILE_SIZE / 2);
    }
    return mt;
  }

  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.makeBoard(BOARD_SIZE);
      this.linkBoard(BOARD_SIZE);
      flood(board.get(0).color);
    }
    return;
  }

  public void onMouseClicked(Posn pos) {
    for (Cell e : board) {
      if (pos.x >= e.x * 20 && pos.x < e.x * 20 + 20 && pos.y >= e.y * 20
          && pos.y < e.y * 20 + 20) {
        flood(e.color);
      }
    }
  }

  public void flood(Color color) {
    for (Cell e : board) {
      if (e.flooded) {
        e.color = color;
        if (e.top != null && e.top.color.equals(color)) {
          e.top.flooded = true;
        }
        if (e.left != null && e.left.color.equals(color)) {
          e.left.flooded = true;
        }
        if (e.right != null && e.right.color.equals(color)) {
          e.right.flooded = true;
        }
        if (e.bottom != null && e.bottom.color.equals(color)) {
          e.bottom.flooded = true;
        }
      }
    }
  }

  public boolean win() {
    boolean test = true;
    for (Cell e : board) {
      test = test && e.flooded;
    }
    return test;
  }

  public void onTick() {
    this.win();
  }

  public WorldEnd worldEnds() {
    if (win()) {
      WorldScene world = this.makeScene();
      world.placeImageXY(new TextImage("YOU WIN", 50, FontStyle.BOLD_ITALIC, Color.orange), 250,
          250);
      return new WorldEnd(true, world);
    } else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

class ExamplesFlood {
  
  
  //Examples for drawing
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
  
  //Examples for linking
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
  
  //examples for makeBoard
  Random ran = new Random(1234);
  FloodItWorld game = new FloodItWorld(ran);
  
  Cell make1 = new Cell(0,0,1);
  ArrayList<Cell> board1 = new ArrayList<Cell>();
  
  Cell make2 = new Cell(0,0,1);
  Cell make3 = new Cell(0,1,1);
  Cell make4 = new Cell(1,0,0);
  Cell make5 = new Cell(1,1,0);
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
  
  
  void testGame(Tester t) {
    t.checkExpect(game.win(), true);

    //game.bigBang(500, 500, .1);
  }
}
