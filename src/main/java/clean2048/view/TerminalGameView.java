package clean2048.view;

import java.io.IOException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TerminalGameView implements GameView {
  private final Terminal backend;

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
    backend.printString("      Score: ", Color.GREY);
    backend.printLine(String.valueOf(score), Color.CYAN);
  }

  private void printGrid(int[][] grid) throws IOException {
    backend.printLine(" -------------------");
    for (int[] row : grid) {
      backend.printCharacter('|');
      for (int tile : row) {
        printTile(tile);
        backend.printCharacter('|');
      }
      backend.printNewLine();
      backend.printLine(" -------------------");
    }
  }

  private void printTile(int tile) throws IOException {
    String tileString = (tile == 0) ? "    " : String.format("%4s", tile);
    backend.printString(tileString, Color.getTileColor(tile));
  }

  @Override
  public void printGameOverMessage() {
    try {
      backend.printLine("      Game Over!", Color.RED);
      backend.flushChanges();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
