package clean2048.view;


public interface GameView {
  void updateDisplay(int Score, int[][] grid);

  void printGameOverMessage();
}
