package Game2048;


import java.util.Scanner;

public class Clean2048 {
    public static void main(String[] args) {

        LanternaBackendAdapter adapter = new LanternaBackendAdapter();
        GameEngine engine = new GameEngine(4);
        GameView view = new TerminalGameView(engine, adapter);
        GameController controller = new LanternaGameController(adapter);

        Scanner sc = new Scanner(System.in);
        engine.spawnTile();
        while (!engine.isGameOver()) {
            view.updateDisplay();
            Tile[][] oldGrid = engine.grid.clone();
            engine.shift(controller.getMove());

            if (engine.theGridChangedFrom(oldGrid)) {
                engine.spawnTile();
            }
        }
        view.updateDisplay();
    }
}
