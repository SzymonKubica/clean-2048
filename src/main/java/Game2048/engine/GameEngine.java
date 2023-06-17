package Game2048.engine;

import Game2048.controller.Direction;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class GameEngine {
  private final int dimension;
  Tile[][] grid;
  @Getter private int score;
  private int numOccupiedTiles;

  public GameEngine(int gridDimension) {
    this.dimension = gridDimension;
    grid = initialiseGrid(this.dimension);
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

  private Tile[][] initialiseGrid(int dimension) {
    Tile[][] grid = new Tile[dimension][dimension];
    for (int i = 0; i < dimension; i++) {
      for (int j = 0; j < dimension; j++) {
        grid[j][i] = Tile.getEmptyTile();
      }
    }
    return grid;
  }

  private void spawnTile() {
    Tile newTile = Tile.generateRandomTile();
    Position emptyCell = getRandomEmptyCell();
    grid[emptyCell.getY()][emptyCell.getX()] = newTile;
    numOccupiedTiles++;
  }

  private Position getRandomEmptyCell() {
    while (true) {
      int x = getRandomCoordinate();
      int y = getRandomCoordinate();
      if (grid[y][x].isEmpty()) {
        return new Position(x, y);
      }
    }
  }

  private int getRandomCoordinate() {
    return (int) (100 * Math.random()) % dimension;
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

  private Tile getSuccessor(Tile current, Tile[] row) {
    if (current == null) {
      return null;
    }
    Tile successor = getNext(current, row);
    while (successor != null && successor.isEmpty()) {
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
      if (currentTile.equals(successor)) {
        int sum = currentTile.getValue() + successor.getValue();
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
    while (mergedNum < dimension) {
      mergedRow[mergedNum] = Tile.getEmptyTile();
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

  public Tile[][] getGrid() {
    return grid.clone();
  }

  public boolean isGameOver() {
    return isBoardFull() && noMovePossible();
  }

  private boolean isBoardFull() {
    return numOccupiedTiles >= dimension * dimension;
  }

  private boolean noMovePossible() {
    // No move is possible when the grid is full and no tiles can be merged.
    // We know that no tiles can be merged if there is no row or column in which
    // two adjacent tiles have the same value.
    for (Tile[] row : grid) {
      if (canBeMerged(row)) {
        return false;
      }
    }

    for (int i = 0; i < dimension; i++) {
      if (canBeMerged(getColumn(i))) {
        return false;
      }
    }
    return true;
  }

  private Tile[] getColumn(int index) {
    Tile[] column = new Tile[dimension];
    for (int i = 0; i < dimension; i++) {
      column[i] = grid[i][index];
    }
    return column;
  }

  private boolean canBeMerged(Tile[] sequence) {
    for (int i = 0; i < dimension - 1; i++) {
      if (sequence[i].equals(sequence[i + 1])) {
        return true;
      }
    }
    return false;
  }

  public int[][] getSimplifiedGrid() {
    return Arrays.stream(grid)
        .map(row -> Arrays.stream(row).mapToInt(Tile::getValue).toArray())
        .toArray(int[][]::new);
  }

  @AllArgsConstructor
  private class Position {
    @Getter private final int x;
    @Getter private final int y;
  }
}
