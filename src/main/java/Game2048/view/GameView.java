package Game2048.view;

import Game2048.engine.Tile;

public interface GameView {
  void updateDisplay(int Score, Tile[][] grid);

  void printGameOverMessage();
}
