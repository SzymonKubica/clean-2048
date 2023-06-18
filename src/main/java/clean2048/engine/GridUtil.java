package clean2048.engine;

import java.util.Arrays;
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
    public static Tile[] getColumn(int index, Tile[][] grid) {
        return Arrays.stream(grid).map(row -> row[index]).toArray(Tile[]::new);
    }

    public static boolean isEmptySequence(Tile[] sequence) {
        return Arrays.stream(sequence).allMatch(Tile::isEmpty);
    }

    public static boolean isFullSequence(Tile[] sequence) {
        return Arrays.stream(sequence).noneMatch(Tile::isEmpty);
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

    public static int[][] mapToIntGrid(Tile[][] grid) {
        return Arrays.stream(grid)
                .map(row -> Arrays.stream(row).mapToInt(Tile::getValue).toArray())
                .toArray(int[][]::new);
    }

    public static Tile[][] transpose(Tile[][] grid) {
        int dimension = grid.length;
        Tile[][] transpose = new Tile[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                transpose[i][j] = grid[j][i];
            }
        }
        return transpose;
    }

    public static Tile[] reverse(Tile[] row) {
        Tile[] reversed = new Tile[row.length];
        for (int i = 0; i < row.length; i++) {
            reversed[i] = row[row.length - 1 - i];
        }
        return reversed;
    }

    @AllArgsConstructor
    static
    class Position {
        @Getter
        private final int x;
        @Getter private final int y;
    }
}
