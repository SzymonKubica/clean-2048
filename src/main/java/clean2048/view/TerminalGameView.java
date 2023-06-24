package clean2048.view;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TerminalGameView implements GameView {
  private final Terminal backend;
  private final int dimension;
  private int terminalWidth;
  private int terminalHeight;
  private int score;
  private int[][] grid;

  public TerminalGameView(Terminal backend, int dimension) {
    this.backend = backend;
    this.dimension = dimension;
    try {
      this.terminalWidth = backend.getTerminalWidth();
      this.terminalHeight = backend.getTerminalHeight();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.backend.addTerminalListener(
        (terminal, terminalSize) -> {
          try {
            terminalWidth = terminal.getTerminalSize().getColumns();
            terminalHeight = terminal.getTerminalSize().getRows();
            terminal.clearScreen();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
          updateDisplay(score, grid);
        });
  }

  @Override
  public void updateDisplay(int score, int[][] grid) {
    this.score = score;
    this.grid = grid;

    try {
      backend.resetCursorPosition();
      centerVertically();
      printScore(score);
      printGrid(grid);
      backend.flushChanges();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void printScore(int score) throws IOException {
    printCentered("Score: ", Color.GREY);
    backend.printLine(String.valueOf(score), Color.CYAN);
  }

  private void centerVertically() throws IOException {
    int topMargin = (terminalHeight - calculateGridHeight()) / 2;
    for (int i = 0; i < topMargin; i++) {
      backend.printNewLine();
    }
  }

  private void printGrid(int[][] grid) throws IOException {
    String line = getHorizontalLine(grid.length);
    printCenteredLine(line);
    for (int[] row : grid) {
      printRow(row);
      backend.printNewLine();
      printCenteredLine(line);
    }
  }

  private void printCentered(String line) throws IOException {
    printCentered(line, Color.GREY);
  }
  private void printCenteredLine(String line) throws IOException {
    printCenteredLine(line, Color.GREY);
  }

  private String getHorizontalLine(int dimension) {
    return String.join("-", IntStream.range(0, dimension).mapToObj(i -> "----").toList());
  }

  private void printRow(int[] row) throws IOException {
    String margin = getCenteringMargin(calculateGridWidth());
    backend.printString(margin);
    backend.printCharacter('|');
    for (int tile : row) {
      printTile(tile);
      backend.printCharacter('|');
    }
  }

  private String getCenteringMargin(int textLength) {
    return getPaddingString((terminalWidth - textLength) / 2);
  }

  private void printTile(int tile) throws IOException {
    String tileString = (tile == 0) ? "    " : String.format("%4s", tile);
    backend.printString(tileString, Color.getTileColor(tile));
  }

  private void printCentered(String text, Color color) throws IOException {
    String margin = getCenteringMargin(text.length());
    backend.printString(margin + text, color);
  }

  private void printCenteredLine(String text, Color color) throws IOException {
    String margin = getCenteringMargin(text.length());
    backend.printLine(margin + text, color);
  }

  private String getPaddingString(int length) {
    return IntStream.range(0, length).mapToObj(i -> " ").collect(Collectors.joining(""));
  }

  private int calculateGridWidth() {
    // each cell is 4 units long and there are dimension + 1 separators between cells.
    return 5 * dimension + 1;
  }

  private int calculateGridHeight() {
    return 2 * dimension + 1 + 1; // The additional +1 is for the score display
  }

  @Override
  public void printGameOverMessage() {
    try {
      centerVertically();
      printCentered("Game Over!", Color.RED);
      backend.flushChanges();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
