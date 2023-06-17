package Game2048.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class GridUtil {

    public static Tile[][] initialiseGrid(int dimension) {
        Tile[][] grid = new Tile[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                grid[j][i] = Tile.getEmptyTile();
            }
        }
        return grid;
    }


    public static boolean isEmptySequence(Tile[] sequence) {
        boolean isEmpty = true;
        for (Tile tile : sequence) {
            isEmpty &= tile.isEmpty();
        }
        return isEmpty;
    }

    public static boolean isFullSequence(Tile[] sequence) {
        boolean isFull = true;
        for (Tile tile : sequence) {
            isFull &= !tile.isEmpty();
        }
        return isFull;
    }

    public static Position getRandomEmptyCell(Tile[][] grid) {
        while (true) {
            int x = getRandomCoordinate(grid.length);
            int y = getRandomCoordinate(grid.length);
            if (grid[y][x].isEmpty()) {
                return new Position(x, y);
            }
        }
    }

    private static int getRandomCoordinate(int dimension) {
        return (int) (100 * Math.random()) % dimension;
    }

    @AllArgsConstructor
    static
    class Position {
        @Getter
        private final int x;
        @Getter private final int y;
    }
}
