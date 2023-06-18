package Game2048.engine;

import static Game2048.engine.GridUtil.*;

import Game2048.controller.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    // Hence, we don't spawn a new tile because the user needs to shift in the perpendicular
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
    // When the sequence is neither full nor empty then the tiles can
    // change their position after shifting. That change can occur if there is at least
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
    shiftHorizontally(direction);
    transpose();
  }

  private void shiftHorizontally(Direction direction) {
    grid = Arrays.stream(grid).map(row -> merge(row, direction)).toArray(Tile[][]::new);
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

  private Tile[] reverse(Tile[] row) {
    Tile[] reversed = new Tile[dimension];
    for (int i = 0; i < dimension; i++) {
      reversed[i] = row[dimension - 1 - i];
    }
    return reversed;
  }

  public Tile[] merge2(Tile[] row) {
    List<Tile> merged = new ArrayList<>();
    List<Tile> nonEmptyTiles =
        new ArrayList<>(Arrays.stream(row).filter(tile -> !tile.isEmpty()).toList());
    while (!nonEmptyTiles.isEmpty()) {
      Tile current = nonEmptyTiles.remove(0);
      if (nonEmptyTiles.isEmpty()) {
        merged.add(current);
        break;
      }
      Tile successor = nonEmptyTiles.get(0);
      if (current.equals(successor)) {
        nonEmptyTiles.remove(0);
        merged.add(Tile.merge(current, successor));
      } else {
        merged.add(current);
      }
    }

    // If there is space left in the row, we add empty tiles to it.
    while (merged.size() < dimension) {
      merged.add(Tile.getEmptyTile());
    }
    return merged.toArray(Tile[]::new);
  }


  private Tile[] merge(Tile[] row, Direction direction) {

    if (direction == Direction.DOWN || direction == Direction.RIGHT) {
      row = reverse(row);
    }

    Tile[] mergedRow = merge2(row);

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
