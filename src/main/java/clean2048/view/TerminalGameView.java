package clean2048.view;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TerminalGameView implements GameView {
  private final Terminal backend;
  private final int dimension;
  private int terminalWidth;
  private int score;
  private int[][] grid;

  public TerminalGameView(Terminal backend, int dimension) {
    this.backend = backend;
    this.dimension = dimension;
    try {
      this.terminalWidth = backend.getTerminalWidth();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    this.backend.addTerminalListener(
        (terminal, terminalSize) -> {
          try {
            terminalWidth = terminal.getTerminalSize().getColumns();
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

  private String getLeftMarginForCentering() {
    return getPaddingString((terminalWidth - calculateGridWidth()) / 2);
  }

  private void printGrid(int[][] grid) throws IOException {
    String padding = getLeftMarginForCentering();
    String line = getHorizontalLine(grid.length);
    backend.printLine(padding + line);
    for (int[] row : grid) {
      printRow(row);
      backend.printNewLine();
      backend.printLine(padding + line);
    }
  }

  private String getHorizontalLine(int dimension) {
    return " " + String.join("-", IntStream.range(0, dimension).mapToObj(i -> "----").toList());
  }

  private void printRow(int[] row) throws IOException {
    String padding = getLeftMarginForCentering();
    backend.printString(padding);
    backend.printCharacter('|');
    for (int tile : row) {
      printTile(tile);
      backend.printCharacter('|');
    }
  }

  private void printTile(int tile) throws IOException {
    String tileString = (tile == 0) ? "    " : String.format("%4s", tile);
    backend.printString(tileString, Color.getTileColor(tile));
  }

  private void printCentered(String text, Color color) throws IOException {
    String padding = getLeftMarginForCentering();
    int fullWidth = calculateGridWidth();
    int leftMargin = ((fullWidth - text.length()) / 2);
    String marginString = getPaddingString(leftMargin);
    backend.printString(padding + marginString + text, color);
  }

  private String getPaddingString(int length) {
    return IntStream.range(0, length).mapToObj(i -> " ").collect(Collectors.joining(""));
  }

  private int calculateGridWidth() {
    return 5 * dimension
        + 1; // each cell is 4 units long and there are dimension + 1 separators between cells.
  }

  @Override
  public void printGameOverMessage() {
    try {
      printCentered("Game Over!", Color.RED);
      backend.flushChanges();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
