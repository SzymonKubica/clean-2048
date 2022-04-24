package Game2048;


public class Grid {
    private final int dimension;
    Tile[][] grid;

    public Grid(int dimension) {
        this.dimension = dimension;
        grid = initialiseGrid(this.dimension);
    }

    private Tile[][] initialiseGrid(int dimension) {
       Tile[][]  grid = new Tile[dimension][dimension];
       for (int i = 0; i < dimension; i++) {
           for (int j = 0; j < dimension; j++) {
               grid[i][j] = new EmptyCell();
           }
       }
       return grid;
    }

    private String printRows() {
        StringBuilder sb = new StringBuilder();
        for (Tile[] row : grid) {
            sb.append("[");
            for (Tile tile : row) {
                sb.append(tile);
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return printRows();
    }

    public void spawnTile() {
        Tile newTile;
        int seed = (int) (10 * Math.random()) % 2;
        if (seed == 1)  {
            newTile = new Tile(2);
        } else {
            newTile = new Tile(4);
        }

        boolean success = false;
        while(!success) {
            int xSeed = (int) (100 * Math.random()) % dimension;
            int ySeed = (int) (100 * Math.random()) % dimension;

            if (grid[xSeed][ySeed].isEmpty()) {
                grid[xSeed][ySeed] = newTile;
                success = true;
            }
        }
    }

    public boolean isGameOver() {
        boolean gameOver = true;
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                gameOver &= !tile.isEmpty();
            }
        }

        return gameOver;
    }

    private boolean isEmptyRow(Tile[] row) {
        boolean isEmpty = true;
        for (int i = 0; i < dimension; i++) {
           isEmpty &= row[i].isEmpty();
        }
        return isEmpty;
    }

    private Tile[] flush(Tile[] row, Direction direction) {
        if (!isEmptyRow(row) && (direction == Direction.RIGHT || direction == Direction.DOWN)) {
            while (row[row.length - 1].isEmpty()) {
                Tile[] shiftedRow = new Tile[dimension];
                shiftedRow[0] = row[row.length - 1];
                for (int i = 1; i < dimension; i++) {
                    shiftedRow[i] = row[i - 1];
                }
                row = shiftedRow;
            }
        }
        return row;

    }

    public void shift(Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            transpose();
        }
        for (int i = 0; i < dimension; i++) {
            Tile[] row = grid[i];
            Tile[] mergedRow = merge(row);
            grid[i] = flush(mergedRow, direction);
        }
        if (direction == Direction.UP || direction == Direction.DOWN) {
            transpose();
        }
    }

    private void transpose() {
        Tile[][] transpose = new Tile[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                transpose[i][j] = grid[j][i];
            }
        }
        grid = transpose;
    }

    private int indexOf(Tile tile, Tile[] row) {
        for (int i = 0; i < row.length; i++) {
            if (row[i] == tile) {
                return i;
            }
        }
        return -1;
    }

    private Tile getNext(Tile current, Tile[] row) {
        int currentIndex = indexOf(current, row);
        if (currentIndex < row.length - 1) {
            return row[currentIndex + 1];
        } else {
            return null;
        }
    }

    private Tile getSuccessor(Tile current,Tile[] row) {
        if (current == null) {
            return null;
        }
        Tile successor = getNext(current, row);
        while(successor != null && successor.isEmpty()) {
            successor = getNext(successor, row);
        }
        return successor;
    }

    private Tile[] merge(Tile[] row) {
        Tile[] mergedRow = new Tile[dimension];
        int mergedNum = 0;

        Tile currentTile = row[0];

        // Skip over empty tiles.
        while (currentTile != null && currentTile.isEmpty()) {
            currentTile = getNext(currentTile, row);
        }
        // Terminate if all tiles empty
        if (currentTile == null) {
            return row;
        }

        // Now the current tile is guaranteed to be non-empty, so we can perform non-trivial merging.

        while (currentTile != null) {
            Tile successor = getSuccessor(currentTile, row);
            if (successor == null) {
                mergedRow[mergedNum] = currentTile;
                mergedNum++;
                break;
            }
            if (currentTile.hasEqualValue(successor)) {
                mergedRow[mergedNum] = new Tile(currentTile.getValue() + successor.getValue());
                mergedNum++;
            } else {
                mergedRow[mergedNum] = currentTile;
                mergedNum++;
                mergedRow[mergedNum] = successor;
                mergedNum++;
            }
            currentTile = getNext(successor, row);
        }
        while(mergedNum < dimension) {
            mergedRow[mergedNum] = new EmptyCell();
            mergedNum++;
        }

        return mergedRow;
    }
    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
}
