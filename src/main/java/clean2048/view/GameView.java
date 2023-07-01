package clean2048.view;


import java.io.IOException;
import java.util.Map;

public interface GameView {
  void updateDisplay(int Score, int[][] grid);

  String promptForUserName() throws IOException;
  void printLeaderBoard(Map<String, Integer> leaderboard);

  void printGameOverMessage();
}
