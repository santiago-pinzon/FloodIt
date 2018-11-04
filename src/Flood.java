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
  static final int BOARD_SIZE = 5;
  Random ran;
  static final int colors = 1;

  FloodItWorld(Random ran) {
    this.ran = ran;

    makeBoard();

    linkBoard();
    flood(board.get(0).color);
  }

  FloodItWorld() {
    this.ran = new Random();

    makeBoard();

    linkBoard();
    flood(board.get(0).color);
  }

  void makeBoard() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        this.board.add(new Cell(i, j, this.ran.nextInt(colors)));
      }
    }
    board.get(0).flooded = true;
   
  }

  void linkBoard() {
    for (int j = 0; j < BOARD_SIZE; j++) {
      for (int i = 0; i < BOARD_SIZE; i++) {
        if (i != 0) {
          this.board.get(i * BOARD_SIZE + j).link("top", this.board.get((i - 1) * BOARD_SIZE + j));
        }
        if (i != BOARD_SIZE - 1) {
          this.board.get(i * BOARD_SIZE + j).link("bottom",
              this.board.get((i + 1) * BOARD_SIZE + j));
        }
        if (j != 0) {
          this.board.get(i * BOARD_SIZE + j).link("left", this.board.get(i * BOARD_SIZE + j - 1));
        }
        if (j != BOARD_SIZE - 1) {
          this.board.get(i * BOARD_SIZE + j).link("right", this.board.get(i * BOARD_SIZE + j + 1));
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
      this.board.clear();
      this.makeBoard();
      this.linkBoard();
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
    Color check = board.get(0).color;
    boolean test = true;
    for(Cell e : board) {
      test = test && e.color.equals(check);
    }
    return test;
  }
  
  public WorldEnd worldEnds() {
    if(win()) {
      WorldScene world = this.getEmptyScene();
      world.placeImageXY(new TextImage("YOU WIN", 40, FontStyle.BOLD_ITALIC, Color.black), 150, 150);
      return new WorldEnd(true, world);
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

class ExamplesFlood {

  Random ran = new Random(1234);
  FloodItWorld game = new FloodItWorld();
  Cell test = new Cell(5, 5, 3);

  void testGame(Tester t) {
    t.checkExpect(game.win(), true);
    

    game.bigBang(500, 500);
  }
}