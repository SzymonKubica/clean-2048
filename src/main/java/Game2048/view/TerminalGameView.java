package Game2048.view;

import Game2048.engine.Tile;
import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
public class TerminalGameView implements GameView {
  private final Terminal backend;

  @Override
  public void updateDisplay(int score, Tile[][] grid) {
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

  private void printGrid(Tile[][] grid) throws IOException {
    backend.printLine(" -------------------");
    for (Tile[] row : grid) {
      backend.printCharacter('|');
      for (Tile tile : row) {
        printTile(tile);
        backend.printCharacter('|');
      }
      backend.printNewLine();
      backend.printLine(" -------------------");
    }
  }

  private void printTile(Tile tile) throws IOException {
    backend.printString(tile.toString(), Color.getTileColor(tile.getValue()));
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
