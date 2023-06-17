package Game2048;

import Game2048.controller.GameController;
import Game2048.controller.TerminalGameController;
import Game2048.engine.GameEngine;
import Game2048.lib.lanterna.LanternaTerminalAdapter;
import Game2048.view.GameView;
import Game2048.view.Terminal;
import Game2048.view.TerminalGameView;
import lombok.Builder;

@Builder(setterPrefix = "with")
public class Clean2048 {
  private final GameEngine engine;
  private final GameView view;
  private final GameController controller;

  private void run() {
    start();
    while (!engine.isGameOver()) {
      updateDisplay();
      engine.takeTurn(controller.getMove());
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

  public static void main(String[] args) {
    Terminal terminal = new LanternaTerminalAdapter();
    GameEngine engine = new GameEngine(4);
    GameView view = new TerminalGameView(terminal);
    GameController controller = new TerminalGameController(terminal);

    Clean2048 game =
        Clean2048.builder().withEngine(engine).withView(view).withController(controller).build();

    game.run();
  }
}
