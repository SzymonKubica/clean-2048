package Game2048;

public class Clean2048Builder {
    private GameEngine engine;
    private GameView view;
    private GameController controller;

    public Clean2048Builder usingEngine(GameEngine engine) {
        this.engine = engine;
        return this;
    }

    public Clean2048Builder displayedOn(GameView view) {
        this.view = view;
        return this;
    }

    public Clean2048Builder controlledBy(GameController controller) {
        this.controller = controller;
        return this;
    }

    public Clean2048 build() {
        return new Clean2048(engine, view, controller);
    }
}
