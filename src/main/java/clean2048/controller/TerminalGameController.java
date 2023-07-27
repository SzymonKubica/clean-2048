package clean2048.controller;

import clean2048.lib.lanterna.LanternaTerminal;
import com.googlecode.lanterna.input.KeyType;
import java.io.IOException;
import java.util.Optional;

public class TerminalGameController implements GameController {
  private final LanternaTerminal terminal;

  public TerminalGameController(LanternaTerminal terminal) {
    this.terminal = terminal;
  }

  @Override
  public Optional<Direction> getMove() throws InterruptGameException {
    KeyType key;
    try {
      key = terminal.getUserInput();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return switch (key) {
      case ArrowLeft -> Optional.of(Direction.LEFT);
      case ArrowUp -> Optional.of(Direction.UP);
      case ArrowDown -> Optional.of(Direction.DOWN);
      case ArrowRight -> Optional.of(Direction.RIGHT);
      case Escape -> throw new InterruptGameException();
      default -> Optional.empty();
    };
  }
}
