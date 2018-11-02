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
  static final int TILE_SIZE = 5;
  static ArrayList<Color> colorKey = new ArrayList<Color>(Arrays.asList(Color.blue, Color.cyan,
      Color.red, Color.green, Color.yellow, Color.magenta, Color.orange));

  Cell(int x, int y, int size, Random ran) {
    this.x = x;
    this.y = y;

    int col = ran.nextInt(8);
    this.color = colorKey.get(col);
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
  ArrayList<Cell> board;
  // Defines an int constant
  static final int BOARD_SIZE = 5;
  public Random ran;
  static final int colors = 4;

  FloodItWorld(Random ran) {
    this.ran = ran;

    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; i < BOARD_SIZE; j++) {
        board.add(new Cell(i, j, colors, this.ran));
      }
    }

    for (int j = 0; j < BOARD_SIZE; j++) {
      for (int i = 0; i < BOARD_SIZE; i++) {
        if (i == 0) {
          board.get(i * BOARD_SIZE + j).link("left", board.get(i * BOARD_SIZE + j - 1));
        }
        if (i != BOARD_SIZE - 1) {
          board.get(i * BOARD_SIZE + j).link("right", board.get(i * BOARD_SIZE + j + 1));
        }
        if (j != 0) {
          board.get(i * BOARD_SIZE + j).link("top", board.get((i - 1) * BOARD_SIZE + j));
        }
        if (j != BOARD_SIZE - 1) {
          board.get(i * BOARD_SIZE + j).link("bottom", board.get((i + 1) * BOARD_SIZE + j));
        }
      }
    }

  }

  @Override
  public WorldScene makeScene() {
    WorldScene mt = this.getEmptyScene();
    for (Cell e : board) {
      mt.placeImageXY(e.draw(), e.TILE_SIZE * e.x, e.TILE_SIZE * e.y);
    }
    return mt;
  }
}