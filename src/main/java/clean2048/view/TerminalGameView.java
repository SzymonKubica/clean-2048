package clean2048.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TerminalGameView implements GameView {
  private final Terminal terminal;
  private final int dimension;

  // We maintain the score and the latest copy of the grid as fields because
  // they are needed to redraw the screen on resize without
  // asking the engine for the latest state of the game board.
  private int score;
  private int[][] grid;

  public TerminalGameView(Terminal terminal, int dimension) {
    this.terminal = terminal;
    this.dimension = dimension;
    this.terminal.addResizeListener(new RedrawOnResize());
  }

  @Override
  public void updateDisplay(int score, int[][] grid) {
    this.score = score;
    this.grid = grid;

    try {
      terminal.resetCursorPosition();
      centerVertically();
      printScore();
      printGrid();
      terminal.flushChanges();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String promptForUserName() throws IOException {
    terminal.printStringCentered("Please enter your name: ");
    char input = terminal.readCharacter();
    List<Character> userInput = new ArrayList<>();
    while (input != '\n') {
      if (input == '\b') {
        userInput.remove(userInput.size() - 1);
        continue;
      }
      terminal.printCharacter(input);
      userInput.add(input);
      input = terminal.readCharacter();
    }
    return userInput.stream().map(String::valueOf).collect(Collectors.joining(""));
  }

  @Override
  public void printLeaderBoard(Map<String, Integer> leaderboard) {
    try {
      final String PLACE = "Place";
      final String USER_NAME = "User Name";
      final String SCORE = "Score";

      int maxPlaceIndexLength = String.valueOf(leaderboard.keySet().size()).length();
      int placeColumnWidth = Math.max(PLACE.length(), maxPlaceIndexLength);

      int maxUserNameLength =
          Collections.max(leaderboard.keySet().stream().map(String::length).toList());
      int userNameColumnWidth = Math.max(USER_NAME.length(), maxUserNameLength);

      int maxScoreLength =
          Collections.max(
              leaderboard.values().stream().map(String::valueOf).map(String::length).toList());
      int scoreColumnWidth = Math.max(SCORE.length(), maxScoreLength);

      String rowTemplate = getRowTemplate(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);
      int BORDERS_WIDTH = 10;

      String line =
          getSeparatorLine(
              placeColumnWidth + userNameColumnWidth + scoreColumnWidth + BORDERS_WIDTH);

      terminal.resetCursorPosition();
      terminal.printLineCentered(line);
      List<String> keys = leaderboard.keySet().stream().toList();

      String leaderboardHeader =
          getCenteredLeaderboardHeader(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);
      terminal.printLineCentered(leaderboardHeader);
      terminal.printLineCentered(line);
      for (int i = 0; i < keys.size(); i++) {
        int place = i + 1;
        String userName = keys.get(i);
        int score = leaderboard.get(userName);
        String leaderboardRow = rowTemplate.formatted(place, userName, score);
        terminal.printLineCentered(leaderboardRow);
      }

      terminal.printLineCentered(line);
      terminal.flushChanges();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getCenteredLeaderboardHeader(
      int placeColumnWidth, int userNameColumnWidth, int scoreColumnWidth) {
    final String PLACE = "Place";
    final String USER_NAME = "User Name";
    final String SCORE = "Score";
    final String template = "| PLACE | USER_NAME | SCORE |";

    int placeColumnMargin = (placeColumnWidth - PLACE.length()) / 2;
    int userNameColumnMargin = (userNameColumnWidth - USER_NAME.length()) / 2;
    int scoreColumnMargin = (scoreColumnWidth - SCORE.length()) / 2;

    int placeColumnWithLeftMarginWidth = placeColumnMargin + PLACE.length();
    int userNameColumnWithLeftMarginWidth = userNameColumnMargin + USER_NAME.length();
    int scoreColumnWithLeftMarginWidth = scoreColumnMargin + SCORE.length();

    String placeColumnRightPadding =
            placeColumnMargin == 0
                    ? "%s"
                    : "%-" + (placeColumnMargin + placeColumnWithLeftMarginWidth - 2) + "s";
    String userNameColumnRightPadding =
            userNameColumnMargin == 0
                    ? "%s"
                    : "%-" + (userNameColumnMargin + userNameColumnWithLeftMarginWidth - 2) + "s";
    String scoreColumnRightPadding =
            scoreColumnMargin == 0
                    ? "%s"
                    : "%-" + (scoreColumnMargin + scoreColumnWithLeftMarginWidth - 2) + "s";

    String placeColumnLeftPadding =
        placeColumnMargin == 0 ? "%s" : "%" + (placeColumnMargin + placeColumnRightPadding.length()) + "s";
    String userNameColumnLeftPadding =
        userNameColumnMargin == 0 ? "%s" : "%" + (userNameColumnMargin + userNameColumnRightPadding.length()) + "s";
    String scoreColumnLeftPadding =
        scoreColumnMargin == 0 ? "%s" : "%" + (scoreColumnMargin + scoreColumnRightPadding.length()) + "s";

    String rightPadded =
        template
            .replace("PLACE", placeColumnLeftPadding)
            .replace("USER_NAME", userNameColumnLeftPadding)
            .replace("SCORE", scoreColumnLeftPadding);
    System.out.println(rightPadded);

    String test = rightPadded.formatted("a", "b", "c");
    System.out.println(test);
    String centered =
        rightPadded.formatted(
            placeColumnRightPadding, userNameColumnRightPadding, scoreColumnRightPadding);
    System.out.println(centered);
    return centered.formatted(PLACE, USER_NAME, SCORE);
  }

  public String getRowTemplate(
      int placeColumnWidth, int userNameColumnWidth, int scoreColumnWidth) {
    final String template = "| Place | UserName | Score |";
    return template
        .replace("Place", "%" + placeColumnWidth + "s")
        .replace("UserName", "%-" + userNameColumnWidth + "s")
        .replace("Score", "%" + scoreColumnWidth + "s");
  }

  private void centerVertically() throws IOException {
    int topMargin = terminal.getVerticalCenteringMargin(calculateGridHeight());
    for (int i = 0; i < topMargin; i++) {
      terminal.printNewLine();
    }
  }

  private void printScore() throws IOException {
    terminal.printStringCentered("Score: ");
    terminal.printLine(String.valueOf(score), Color.CYAN);
  }

  private void printGrid() throws IOException {
    String line = getHorizontalLine(grid.length);
    terminal.printLineCentered(line);
    for (int[] row : grid) {
      printRow(row);
      terminal.printLineCentered(line);
    }
  }

  private String getHorizontalLine(int dimension) {
    return String.join("-", IntStream.range(0, dimension).mapToObj(i -> "----").toList());
  }

  private String getSeparatorLine(int length) {
    return String.join("", IntStream.range(0, length).mapToObj(i -> "-").toList());
  }

  private void printRow(int[] row) throws IOException {
    String margin =
        IntStream.range(0, terminal.getHorizontalCenteringMargin(calculateGridWidth()))
            .mapToObj(i -> " ")
            .collect(Collectors.joining(""));

    terminal.printString(margin);
    terminal.printCharacter('|');
    for (int tile : row) {
      printTile(tile);
      terminal.printCharacter('|');
    }
    terminal.printNewLine();
  }

  private void printTile(int tile) throws IOException {
    final String emptyCell = "    ";
    String tileString = (tile == 0) ? emptyCell : "%4s".formatted(tile);
    terminal.printString(tileString, Color.getTileColor(tile));
  }

  private int calculateGridWidth() {
    final int CELL_WIDTH = 4;
    final int SEPARATOR_COUNT = dimension + 1;
    return CELL_WIDTH * dimension + SEPARATOR_COUNT;
  }

  private int calculateGridHeight() {
    final int HORIZONTAL_BORDER_COUNT = dimension + 1;
    final int PLAIN_TEXT_LINE_COUNT = 1; // for displaying the score
    return dimension + HORIZONTAL_BORDER_COUNT + PLAIN_TEXT_LINE_COUNT;
  }

  @Override
  public void printGameOverMessage() {
    try {
      centerVertically();
      terminal.printLineCentered("Game Over!", Color.RED);
      terminal.flushChanges();
      terminal.setCursorVisible();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private class RedrawOnResize implements TerminalResizeListener {
    @Override
    public void onResized(
        com.googlecode.lanterna.terminal.Terminal terminal, TerminalSize terminalSize) {
      try {
        terminal.clearScreen();
        if (!terminal.getCursorPosition().equals(0, 0)) {
          // Prevents repeated updates without clearing the screen on application startup.
          // The problem was that when starting up the grid would get printed two times and those
          // left-overs wouldn't get cleared unless the user resized the window.
          updateDisplay(score, grid);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
