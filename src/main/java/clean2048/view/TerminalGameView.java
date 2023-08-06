package clean2048.view;

import clean2048.lib.lanterna.LanternaTerminal;
import clean2048.user_data.User;
import clean2048.user_data.UserScoreStorage;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TerminalGameView {
  private final LanternaTerminal terminal;
  private final LeaderboardView leaderboardView;
  private final int dimension;

  // We maintain the score and the latest copy of the grid as fields because
  // they are needed to redraw the screen on resize without
  // asking the engine for the latest state of the game board.
  private int score;
  private int[][] grid;

  public TerminalGameView(
      LanternaTerminal terminal, LeaderboardView leaderboardView, int dimension) {
    this.terminal = terminal;
    this.leaderboardView = leaderboardView;
    this.dimension = dimension;
    this.terminal.addResizeListener(new RedrawOnResize());
  }

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

  public EndGameAction selectEndGameAction() throws IOException {
    terminal.printLineCentered("Select what you want to do: ");
    terminal.printLineCentered("Press %s to exit".formatted(EndGameAction.QUIT_KEY));
    terminal.printLineCentered(
        "Press %s to edit the leaderboard".formatted(EndGameAction.EDIT_LEADERBOARD_KEY));
    terminal.printLineCentered(
        "Press %s to save your score".formatted(EndGameAction.SAVE_SCORE_KEY));
    return inputEndGameAction();
  }

  private EndGameAction inputEndGameAction() throws IOException {
    char input = terminal.readCharacter();
    return switch (input) {
      case EndGameAction.QUIT_KEY -> EndGameAction.QUIT;
      case EndGameAction.EDIT_LEADERBOARD_KEY -> EndGameAction.EDIT_LEADERBOARD;
      case EndGameAction.SAVE_SCORE_KEY -> EndGameAction.SAVE_SCORE;
      default -> inputEndGameAction();
    };
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

  public void printGameOverMessage() {
    try {
      terminal.printLine("");
      terminal.printLineCentered("Game Over!", Color.RED);
      terminal.flushChanges();
      terminal.setCursorVisible(true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void editLeaderBoard() throws IOException {
    UserScoreStorage storage = new UserScoreStorage();
    Map<String, User> leaderboard = storage.readUserData();
    terminal.setCursorVisible(false);
    terminal.clearScreen();
    terminal.flushChanges();
    printEditingLeaderboardGuide();
    leaderboardView.printLeaderboardHighlightingRow(leaderboard, 0, Color.CYAN);
    int selectedRow = 0;
    KeyType input = null;
    while (input != KeyType.Enter) {
      input = terminal.getUserInput();
      switch (input) {
        case ArrowDown -> selectedRow = (selectedRow + 1) % leaderboard.size();
        case ArrowUp -> selectedRow = selectedRow == 0 ? leaderboard.size() - 1 : (selectedRow - 1);
        default -> {}
      }
      printEditingLeaderboardGuide();
      leaderboardView.printLeaderboardHighlightingRow(leaderboard, selectedRow, Color.CYAN);
    }
    List<User> scores = new ArrayList<>(leaderboard.values().stream().toList());
    User selectedUser = scores.get(selectedRow);
    terminal.printLineCentered("Editing the user: %s".formatted(selectedUser.userName));
    terminal.printLineCentered("Select action:");
    terminal.printLineCentered(
        "%s -> delete the user score".formatted(EditLeaderboardAction.DELETE.actionCharacter));
    terminal.printLineCentered(
        "%s -> edit the username".formatted(EditLeaderboardAction.EDIT_USERNAME.actionCharacter));
    terminal.printLineCentered(
        "%s -> quit the editing mode".formatted(EditLeaderboardAction.QUIT.actionCharacter));
    char selection = 'x';
    while (selection != EditLeaderboardAction.DELETE.actionCharacter
        && selection != EditLeaderboardAction.EDIT_USERNAME.actionCharacter
        && selection != EditLeaderboardAction.QUIT.actionCharacter) {
      selection = terminal.readCharacter();
    }

    EditLeaderboardAction action = EditLeaderboardAction.fromChar(selection);

    switch (action) {
      case DELETE -> runDelete(selectedUser, leaderboard);
      case EDIT_USERNAME -> runEditUsername(selectedUser, leaderboard);
      case QUIT -> terminal.printLineCentered("Exited the editing mode.");
    }
  }

  private enum EditLeaderboardAction {
    DELETE('d'),
    EDIT_USERNAME('e'),
    QUIT('q');

    public static final char DELETE_CHAR = 'd';
    public static final char EDIT_USERNAME_CHAR = 'e';
    public static final char QUIT_CHAR = 'q';
    public final char actionCharacter;

    EditLeaderboardAction(char actionCharacter) {
      this.actionCharacter = actionCharacter;
    }

    public static EditLeaderboardAction fromChar(char c) {
      return switch (c) {
        case DELETE_CHAR -> DELETE;
        case EDIT_USERNAME_CHAR -> EDIT_USERNAME;
        case QUIT_CHAR -> QUIT;
        default -> throw new IllegalStateException("Unexpected value: " + c);
      };
    }
  }

  private void printEditingLeaderboardGuide() throws IOException {
    terminal.resetCursorPosition();
    terminal.printLineCentered("Editing the leaderboard.");
    terminal.printLineCentered("Use arrows to select the row to edit.");
    terminal.printLineCentered("Press enter to confirm your selection.");
    terminal.flushChanges();
  }

  private void runEditUsername(User selectedUser, Map<String, User> leaderboard)
      throws IOException {
    String password = promptForPassword();
    if (!Objects.equals(password, selectedUser.password)) {
      terminal.printLineCentered("Incorrect password! Please try again", Color.RED);
      runEditUsername(selectedUser, leaderboard);
      return;
    }
    String newUserName = promptForUserName();
    while (leaderboard.containsKey(newUserName)) {
      terminal.printLineCentered("Username: %s is already taken, please try another one.");
      newUserName = promptForUserName();
    }
    leaderboard.remove(selectedUser.userName, selectedUser);
    String oldUsername = selectedUser.userName;
    selectedUser.userName = newUserName;
    leaderboard.put(newUserName, selectedUser);
    UserScoreStorage storage = new UserScoreStorage();
    storage.writeUserData(leaderboard);
    terminal.printLineCentered(
        "Successfully renamed the user: %s to %s".formatted(oldUsername, selectedUser.userName));
  }

  private void runDelete(User selectedUser, Map<String, User> leaderboard) throws IOException {
    String password = promptForPassword();
    if (!Objects.equals(password, selectedUser.password)) {
      terminal.printLineCentered("Incorrect password! Please try again", Color.RED);
      runDelete(selectedUser, leaderboard);
      return;
    }

    leaderboard.remove(selectedUser.userName, selectedUser);
    UserScoreStorage storage = new UserScoreStorage();
    storage.writeUserData(leaderboard);
    terminal.printLineCentered(
        "Successfully deleted the user: %s".formatted(selectedUser.userName));
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
