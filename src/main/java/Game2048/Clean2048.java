package Game2048;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.util.Scanner;

public class Clean2048 {
    public static void main(String[] args) {
        Grid gameGrid = new Grid(4);
        GameView view = new GameView(gameGrid);

        Scanner sc = new Scanner(System.in);
        view.startDisplay();
        gameGrid.spawnTile();
        while (!gameGrid.isGameOver()) {
            view.updateDisplay();
            Tile[][] oldGrid = gameGrid.grid.clone();
            KeyType key = view.getInput().getKeyType();
            switch (key) {
                case ArrowLeft -> gameGrid.shift(Grid.Direction.LEFT);
                case ArrowUp -> gameGrid.shift(Grid.Direction.UP);
                case ArrowDown -> gameGrid.shift(Grid.Direction.DOWN);
                case ArrowRight -> gameGrid.shift(Grid.Direction.RIGHT);
            }
            if (gameGrid.theGridChangedFrom(oldGrid)) {
                gameGrid.spawnTile();
            }
        }
        view.updateDisplay();
    }
}
