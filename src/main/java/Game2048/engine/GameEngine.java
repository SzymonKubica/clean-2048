package Game2048.engine;

import static Game2048.engine.GridUtil.*;

import Game2048.controller.Direction;
import java.util.Arrays;
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
    if (shiftWillChangeState(direction)) {
      shift(direction);
      spawnTile();
    }
  }

  private boolean shiftWillChangeState(Direction direction) {
    // The shift will change state if it will cause a merge in that direction,
    // or if there is enough empty space so that tiles will change their position.
    // An important edge case is when we are trying to shift in say vertical direction and all
    // columns are either empty or fully filled with unmergeable sequences of tiles.
    // In this case, no tiles will move around and the state will not change.
    // Hence we don't spawn a new tile because the user needs to shift in the perpendicular
    // direction.
    if (isSpaceToShift(direction)) {
      return true;
    }
    return (direction.isVertical()) ? verticalMergePossible() : horizontalMergePossible();
  }

  private boolean isSpaceToShift(Direction direction) {
    if (direction.isVertical()) {
      for (int i = 0; i < dimension; i++) {
        Tile[] column = getColumn(i);
        if (willTilesMove(column)) {
          return true;
        }
      }
    } else {
      for (Tile[] row : grid) {
        if (willTilesMove(row)) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean willTilesMove(Tile[] sequence) {
    // When the sequence is neigher full nor empty and the tiles there will change their
    // position after shifting.
    return !isEmptySequence(sequence) && !isFullSequence(sequence);
  }

  private void spawnTile() {
    Tile newTile = Tile.generateRandomTile();
    GridUtil.Position emptyCell = getRandomEmptyCell(grid);
    grid[emptyCell.getY()][emptyCell.getX()] = newTile;
    numOccupiedTiles++;
  }

  public void shift(Direction direction) {
    if (direction.isVertical()) {
      shiftVertically(direction);
    } else {
      shiftHorizontally(direction);
    }
  }

  private void shiftVertically(Direction direction) {
    assert (direction.isVertical());
    transpose();
    for (int i = 0; i < dimension; i++) {
      Tile[] row = grid[i];
      Tile[] mergedRow = merge(row, direction);
      grid[i] = mergedRow;
    }
    transpose();
  }

  private void shiftHorizontally(Direction direction) {
    assert (direction.isHorizontal());
    for (int i = 0; i < dimension; i++) {
      Tile[] row = grid[i];
      Tile[] mergedRow = merge(row, direction);
      grid[i] = mergedRow;
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
    return !horizontalMergePossible() && !verticalMergePossible();
  }

  private boolean horizontalMergePossible() {
    for (Tile[] row : grid) {
      if (canBeMerged(row)) {
        return true;
      }
    }
    return false;
  }

  private boolean verticalMergePossible() {
    for (int i = 0; i < dimension; i++) {
      if (canBeMerged(getColumn(i))) {
        return true;
      }
    }
    return false;
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
}
