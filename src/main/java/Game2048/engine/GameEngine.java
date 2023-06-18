package Game2048.engine;

import static Game2048.engine.GridUtil.*;

import Game2048.controller.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import lombok.Getter;

public class GameEngine {
  private final int dimension;
  Tile[][] grid;
  @Getter private int score;
  private int occupiedTiles;

  public GameEngine(int gridDimension) {
    this.dimension = gridDimension;
    grid = GridUtil.initialiseGrid(this.dimension);
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
    boolean mergeWillOccur =
        (direction.isVertical()) ? verticalMergePossible() : horizontalMergePossible();
    return isSpaceToMove(direction) || mergeWillOccur;
  }

  private boolean isSpaceToMove(Direction direction) {
    if (direction.isVertical()) {
      return IntStream.range(0, dimension)
          .anyMatch(i -> isShiftableSequence(getColumn(i, grid), direction));
    } else {
      return Arrays.stream(grid).anyMatch(row -> isShiftableSequence(row, direction));
    }
  }

  private boolean isShiftableSequence(Tile[] sequence, Direction direction) {
    // A sequence is shiftable if it contains an empty tile followed by a non-empty tile which will
    // occupy its place after the shift.
    Tile[] sequenceCopy = sequence.clone();
    if (needsBackwardsMerging(direction)) {
      sequenceCopy = reverse(sequenceCopy);
    }
    for (int i = 0; i < sequenceCopy.length - 1; i++) {
      if (sequenceCopy[i].isEmpty()) {
        for (int j = i + 1; j < sequenceCopy.length; j++) {
          if (!sequenceCopy[j].isEmpty()) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private void spawnTile() {
    Tile newTile = Tile.generateRandomTile();
    GridUtil.Position emptyCell = GridUtil.getRandomEmptyCell(grid);
    grid[emptyCell.getY()][emptyCell.getX()] = newTile;
    occupiedTiles++;
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
    grid = GridUtil.transpose(grid);
    shiftHorizontally(direction);
    grid = GridUtil.transpose(grid);
  }

  private void shiftHorizontally(Direction direction) {
    grid = Arrays.stream(grid).map(row -> merge(row, direction)).toArray(Tile[][]::new);
  }

  private Tile[] merge(Tile[] row, Direction direction) {
    return (needsBackwardsMerging(direction)) ? reverse(mergeLeft(reverse(row))) : mergeLeft(row);
  }

  private boolean needsBackwardsMerging(Direction direction) {
    return direction == Direction.DOWN || direction == Direction.RIGHT;
  }

  public Tile[] mergeLeft(Tile[] row) {
    List<Tile> merged = new ArrayList<>();
    List<Tile> nonEmptyTiles =
        new ArrayList<>(Arrays.stream(row).filter(tile -> !tile.isEmpty()).toList());

    while (!nonEmptyTiles.isEmpty()) {
      if (nonEmptyTiles.size() == 1) {
        merged.add(nonEmptyTiles.get(0));
        break;
      }

      Tile current = nonEmptyTiles.remove(0);
      Tile successor = nonEmptyTiles.get(0);

      if (current.equals(successor)) {
        nonEmptyTiles.remove(0);
        merged.add(Tile.merge(current, successor));
        occupiedTiles--;
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

  public boolean isGameOver() {
    return isBoardFull() && noMovePossible();
  }

  private boolean isBoardFull() {
    return occupiedTiles >= dimension * dimension;
  }

  private boolean noMovePossible() {
    return !horizontalMergePossible() && !verticalMergePossible();
  }

  private boolean horizontalMergePossible() {
    return Arrays.stream(grid).anyMatch(this::canBeMerged);
  }

  private boolean verticalMergePossible() {
    return IntStream.range(0, dimension).anyMatch(i -> canBeMerged(GridUtil.getColumn(i, grid)));
  }

  private boolean canBeMerged(Tile[] sequence) {
    List<Tile> nonEmptyTiles =
        new ArrayList<>(Arrays.stream(sequence).filter(tile -> !tile.isEmpty()).toList());

    for (int i = 0; i < nonEmptyTiles.size() - 1; i++) {
      if (nonEmptyTiles.get(i).equals(nonEmptyTiles.get(i + 1))) {
        return true;
      }
    }
    return false;
  }

  public int[][] getSimplifiedGrid() {
    return GridUtil.mapToIntGrid(grid);
  }

  public Tile[][] getGrid() {
    return grid.clone();
  }
}
