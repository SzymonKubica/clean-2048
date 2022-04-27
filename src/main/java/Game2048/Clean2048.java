package Game2048;


public class Clean2048 {
    private final GameEngine engine;
    private final GameView view;
    private final GameController controller;

    public Clean2048(GameEngine engine, GameView view, GameController controller) {
        this.engine = engine;
        this.view = view;
        this.controller = controller;
    }

    private void start() {
        engine.startGame();
        view.updateDisplay();
    }

    private void end() {
        view.updateDisplay();
        view.printGameOverMessage();
    }

    public static void main(String[] args) {

        LanternaBackendAdapter adapter = new LanternaBackendAdapter();
        GameEngine engine = new GameEngine(4);
        GameView view = new TerminalGameView(engine, adapter);
        GameController controller = new LanternaGameController(adapter);

        Clean2048 game = new Clean2048Builder()
                .usingEngine(engine)
                .displayedOn(view)
                .controlledBy(controller)
                .build();

        game.start();
        while (!engine.isGameOver()) {
            view.updateDisplay();
            engine.takeTurn(game.controller.getMove());
        }
        game.end();
    }
}
