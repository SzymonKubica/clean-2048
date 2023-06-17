package Game2048.engine;


import Game2048.controller.Direction;

public class GameEngine {
    private final int dimension;
    Tile[][] grid;
    private int score;
    private int numOccupiedTiles;

    public GameEngine(int gridDimension) {
        this.dimension = gridDimension;
        grid = initialiseGrid(this.dimension);
        score = 0;
        numOccupiedTiles = 0;
    }

    public int getScore() {
        return score;
    }

    public void startGame() {
        spawnTile();
    }

    public void takeTurn(Direction direction) {
        Tile[][] oldGrid = grid.clone();
        shift(direction);

        if (theGridChangedFrom(oldGrid)) {
            spawnTile();
        }
    }

    public boolean isGameOver() {
        return isBoardFull() && noMovePossible();
    }

    private boolean isBoardFull() {
        return  numOccupiedTiles >= dimension * dimension;
    }

    private boolean noMovePossible() {
        // Save the state
        Tile[][] currentState = grid.clone();
        int currentScore = score;
        int currentOccupied = numOccupiedTiles;

        boolean noMoves = true;
        for (Direction direction : Direction.values()) {
            shift(direction);
            noMoves &= !theGridChangedFrom(currentState);
            grid = currentState;
        }
        // Restore the state
        score = currentScore;
        numOccupiedTiles = currentOccupied;
        return noMoves;
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

    private Tile generateTile() {
        int seed = (int) (10 * Math.random()) % 2;
        if (seed == 1)  {
            return new Tile(2);
        } else {
            return new Tile(4);
        }
    }

    private int getRandomCoordinate() {
       return (int) (100 * Math.random()) % dimension;
    }

    private void spawnTile() {
        Tile newTile = generateTile();

        boolean success = false;
        while(!success) {
            int x = getRandomCoordinate();
            int y = getRandomCoordinate();

            if (grid[x][y].isEmpty()) {
                grid[x][y] = newTile;
                success = true;
            }
        }
        numOccupiedTiles++;
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
                System.arraycopy(row, 0, shiftedRow, 1, dimension - 1);
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
            Tile[] mergedRow = merge(row, direction);
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

    private Tile[] reverse(Tile[] row) {
        Tile[] reversed = new Tile[dimension];
        for (int i = 0; i < dimension; i++) {
            reversed[i] = row[dimension - 1 - i];
        }
        return reversed;
    }

    private Tile[] merge(Tile[] row, Direction direction) {
        Tile[] mergedRow = new Tile[dimension];
        int mergedNum = 0;

        if (direction == Direction.DOWN || direction == Direction.RIGHT) {
           row = reverse(row);
        }

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
                int sum =  currentTile.getValue() + successor.getValue();
                score += sum;
                numOccupiedTiles--;
                mergedRow[mergedNum] = new Tile(sum);
                mergedNum++;
                currentTile = getNext(successor, row);
            } else {
                mergedRow[mergedNum] = currentTile;
                mergedNum++;
                currentTile = successor;
            }
        }
        while(mergedNum < dimension) {
            mergedRow[mergedNum] = new EmptyCell();
            mergedNum++;
        }

        if (direction == Direction.DOWN || direction == Direction.RIGHT) {
            mergedRow = reverse(mergedRow);
        }

        return mergedRow;
    }

    private boolean theGridChangedFrom(Tile[][] oldGrid) {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (grid[i][j].getValue() != oldGrid[i][j].getValue()) {
                   return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return printRows();
    }

    public Tile[][] getGrid() {
        return grid.clone();
    }
}
