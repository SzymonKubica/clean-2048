package Game2048.controller;

import Game2048.view.Terminal;
import com.googlecode.lanterna.input.KeyType;
import java.io.IOException;

public class TerminalGameController implements GameController {
  private final Terminal terminal;

  public TerminalGameController(Terminal terminal) {
    this.terminal = terminal;
  }

  @Override
  public Direction getMove() {
    KeyType key;
    try {
      key = terminal.getUserInput();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return switch (key) {
      case ArrowLeft -> Direction.LEFT;
      case ArrowUp -> Direction.UP;
      case ArrowDown -> Direction.DOWN;
      case ArrowRight -> Direction.RIGHT;
      default -> throw new IllegalStateException("Unexpected value: " + key);
    };
  }
}
