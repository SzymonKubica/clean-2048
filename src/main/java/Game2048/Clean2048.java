package Game2048;

import java.util.Scanner;

public class Clean2048 {
    public static void main(String[] args) {
        Grid gameGrid = new Grid(4);

        Scanner sc = new Scanner(System.in);

        gameGrid.spawnTile();
        while (!gameGrid.isGameOver()) {
            System.out.println(gameGrid);
            String move = sc.nextLine();
            switch (move) {
                case "a" :
                    gameGrid.shift(Grid.Direction.LEFT);
                    break;
                case "w" :
                    gameGrid.shift(Grid.Direction.UP);
                    break;
                case "s" :
                    gameGrid.shift(Grid.Direction.DOWN);
                    break;
                case "d" :
                    gameGrid.shift(Grid.Direction.RIGHT);
                    break;
            }
            gameGrid.spawnTile();
        }
    }
}
