package clean2048.view;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TerminalGameView implements GameView {
  private final Terminal backend;
  private final int dimension;

  @Override
  public void updateDisplay(int score, int[][] grid) {
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

  private void printGrid(int[][] grid) throws IOException {
    String line = getHorizontalLine(grid.length);
    backend.printLine(line);
    for (int[] row : grid) {
      printRow(row);
      backend.printNewLine();
      backend.printLine(line);
    }
  }

  private String getHorizontalLine(int dimension) {
    return " " + String.join("-", IntStream.range(0, dimension).mapToObj(i -> "----").toList());
  }

  private void printRow(int[] row) throws IOException {
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
    int fullWidth = 5 * dimension + 1; // each cell is 4 units long and there are dimension + 1 separators between cells.
    int leftMargin = ((fullWidth - text.length()) / 2);
    String marginString = IntStream.range(0, leftMargin).mapToObj(i -> " ").collect(Collectors.joining(""));
    backend.printString(marginString + text, color);
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
