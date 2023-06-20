package clean2048;

import clean2048.controller.Direction;
import clean2048.controller.GameController;
import clean2048.controller.InterruptGameException;
import clean2048.controller.TerminalGameController;
import clean2048.engine.GameEngine;
import clean2048.lib.lanterna.LanternaTerminalAdapter;
import clean2048.view.GameView;
import clean2048.view.Terminal;
import clean2048.view.TerminalGameView;
import java.util.Optional;
import lombok.Builder;

@Builder(setterPrefix = "with")
public class Clean2048 {
  private static final int BOARD_DIMENSION = 4;
  private final GameEngine engine;
  private final GameView view;
  private final GameController controller;

  public static void main(String[] args) {
    Terminal terminal = new LanternaTerminalAdapter();
    int boardDimension = getBoardDimensionFromCommandLine(args);
    GameEngine engine = new GameEngine(boardDimension);
    GameView view = new TerminalGameView(terminal, boardDimension);
    GameController controller = new TerminalGameController(terminal);

    Clean2048 game =
            Clean2048.builder().withEngine(engine).withView(view).withController(controller).build();

    try {
      game.run();
    } catch (InterruptGameException e) {
      System.out.println("Thank you for playing the game!");
    }
  }

  private static int getBoardDimensionFromCommandLine(String[] args) {
    try {
      return (args.length == 1) ? Integer.parseInt(args[0]) : BOARD_DIMENSION;
    } catch (NumberFormatException e) {
      return BOARD_DIMENSION;
    }
  }

  private void run() throws InterruptGameException {
    start();
    while (!engine.isGameOver()) {
      updateDisplay();
      Optional<Direction> move = controller.getMove();
      move.ifPresent(engine::takeTurn);
    }
    end();
  }

  private void start() {
    engine.startGame();
    updateDisplay();
  }

  private void updateDisplay() {
    view.updateDisplay(engine.getScore(), engine.getSimplifiedGrid());
  }

  private void end() {
    updateDisplay();
    view.printGameOverMessage();
  }

}
