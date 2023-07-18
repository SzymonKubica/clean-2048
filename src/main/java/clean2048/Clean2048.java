package clean2048;

import clean2048.controller.Direction;
import clean2048.controller.GameController;
import clean2048.controller.InterruptGameException;
import clean2048.controller.TerminalGameController;
import clean2048.engine.GameEngine;
import clean2048.lib.lanterna.LanternaTerminalAdapter;
import clean2048.user_data.UserScoreStorage;
import clean2048.view.EndGameAction;
import clean2048.view.Terminal;
import clean2048.view.TerminalGameView;
import java.io.IOException;
import java.util.Optional;
import lombok.Builder;

@Builder(setterPrefix = "with")
public class Clean2048 {
  private static final int BOARD_DIMENSION = 4;
  private final GameEngine engine;
  private final TerminalGameView view;
  private final GameController controller;
  private final UserScoreStorage userScoreStorage;

  public static void main(String[] args) throws IOException {
    Terminal terminal = new LanternaTerminalAdapter();
    int boardDimension = getBoardDimensionFromCommandLine(args);
    GameEngine engine = new GameEngine(boardDimension);
    TerminalGameView view = new TerminalGameView(terminal, boardDimension);
    GameController controller = new TerminalGameController(terminal);
    UserScoreStorage userScoreStorage = new UserScoreStorage();

    Clean2048 game =
        Clean2048.builder()
            .withEngine(engine)
            .withView(view)
            .withController(controller)
            .withUserScoreStorage(userScoreStorage)
            .build();

    try {
      game.run();
    } catch (InterruptGameException e) {
      view.printGameOverMessage();
      game.endGameMenu();
    }
  }

  private void endGameMenu() throws IOException {
    EndGameAction selectedAction = view.selectEndGameAction();
    switch (selectedAction) {
      case SAVE_SCORE -> updateAndShowLeaderboard();
      case EXIT -> {}
      case EDIT_LEADERBOARD -> view.editLeaderBoard();
    }
  }

  private void updateAndShowLeaderboard() throws IOException {
    String userName = view.promptForUserName();
    String password = view.promptForPassword();
    if (userScoreStorage.verifyUser(userName, password)) {
      userScoreStorage.updateLeaderboard(userName, password, engine.getScore());
    }
    view.printLeaderboard(userScoreStorage.readUserData());
  }

  private static int getBoardDimensionFromCommandLine(String[] args) {
    try {
      return (args.length == 1) ? Integer.parseInt(args[0]) : BOARD_DIMENSION;
    } catch (NumberFormatException e) {
      return BOARD_DIMENSION;
    }
  }

  private void run() throws InterruptGameException, IOException {
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
  }

  private void updateDisplay() {
    view.updateDisplay(engine.getScore(), engine.getSimplifiedGrid());
  }

  private void end() throws IOException {
    updateDisplay();
    view.printGameOverMessage();
    updateAndShowLeaderboard();
  }
}
