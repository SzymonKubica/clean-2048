package clean2048.view;


import clean2048.user_data.User;
import java.io.IOException;
import java.util.Map;

public interface GameView {
  void updateDisplay(int Score, int[][] grid);

  String promptForUserName() throws IOException;
  String promptForPassword() throws IOException;
  void printLeaderboard(Map<String, User> leaderboard);

  void printGameOverMessage();

}
