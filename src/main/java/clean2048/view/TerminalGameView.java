package clean2048.view;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TerminalGameView implements GameView {
  private final Terminal backend;
  private final int dimension;
  private int score;
  private int[][] grid;

  public TerminalGameView(Terminal backend, int dimension) {
    this.backend = backend;
    this.dimension = dimension;
    this.backend.addResizeListener(new RedrawOnResizeHandler());
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

  private void centerVertically() throws IOException {
    int topMargin = backend.getVerticalCenteringMargin(calculateGridHeight());
    for (int i = 0; i < topMargin; i++) {
      backend.printNewLine();
    }
  }

  private void printScore(int score) throws IOException {
    backend.printStringCentered("Score: ");
    backend.printLine(String.valueOf(score), Color.CYAN);
  }

  private void printGrid(int[][] grid) throws IOException {
    String line = getHorizontalLine(grid.length);
    backend.printLineCentered(line);
    for (int[] row : grid) {
      printRow(row);
      backend.printLineCentered(line);
    }
  }

  private String getHorizontalLine(int dimension) {
    return String.join("-", IntStream.range(0, dimension).mapToObj(i -> "----").toList());
  }

  private void printRow(int[] row) throws IOException {
    String margin =
        IntStream.range(0, backend.getHorizontalCenteringMargin(calculateGridWidth()))
            .mapToObj(i -> " ")
            .collect(Collectors.joining(""));

    backend.printString(margin);
    backend.printCharacter('|');
    for (int tile : row) {
      printTile(tile);
      backend.printCharacter('|');
    }
    backend.printNewLine();
  }

  private void printTile(int tile) throws IOException {
    final String emptyCell = "    ";
    String tileString = (tile == 0) ? emptyCell : "%4s".formatted(tile);
    backend.printString(tileString, Color.getTileColor(tile));
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
      backend.printLineCentered("Game Over!", Color.RED);
      backend.flushChanges();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private class RedrawOnResizeHandler implements TerminalResizeListener {
    @Override
    public void onResized(com.googlecode.lanterna.terminal.Terminal terminal, TerminalSize terminalSize) {
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
