package Game2048.engine;

import static Game2048.engine.GridUtil.*;

import Game2048.controller.Direction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
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
    /* The shift will change state if it will cause a merge in that direction,
     * or if there is enough empty space so that tiles will change their position.
     * An important edge case is when we are trying to shift in say vertical direction and all
     * columns are either empty or fully filled with a non-merge-able sequences of tiles.
     * In this case, no tiles will move around and the state will not change.
     * Hence, we don't spawn a new tile because the user needs to shift in the perpendicular
     * direction.
     */
    boolean mergeWillOccur =
        (direction.isVertical()) ? verticalMergePossible() : horizontalMergePossible();
    return isSpaceToMove(direction) || mergeWillOccur;
  }

  private boolean isSpaceToMove(Direction direction) {
    if (direction.isVertical()) {
      return IntStream.range(0, dimension)
          .anyMatch(i -> canBeShifted(getColumn(i, grid), direction));
    } else {
      return Arrays.stream(grid).anyMatch(row -> canBeShifted(row, direction));
    }
  }

  private boolean canBeShifted(Tile[] sequence, Direction direction) {
    /* A sequence can be shifted if it contains an empty tile followed by a non-empty tile which will
     * occupy its place after the shift. We need to be able to identify such sequences of
     * tiles to be able to determine if a shift requested by the user will change the state of
     * the game grid, and thus we'll execute the shift and spawn a new tile.
     */
    Tile[] orientedSeq = (needsBackwardsMerging(direction)) ? reverse(sequence) : sequence;
    return IntStream.range(0, sequence.length)
        .anyMatch(i -> orientedSeq[i].isEmpty() && hasNonEmptySuccessor(i, orientedSeq));
  }

  private boolean hasNonEmptySuccessor(int index, Tile[] sequence) {
    Tile[] successors = Arrays.copyOfRange(sequence, index + 1, sequence.length);
    return Arrays.stream(successors).anyMatch(Predicate.not(Tile::isEmpty));
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

  /* A direction requires 'backwards merging' when it is either DOWN or RIGHT.
   * The reason for that is that when you have a following row: [ _, 2, 2, 2 ]
   * And you swipe to the right, then the expected merge result that you want to get
   * is this: [ _, _, 2, 4 ]. However, if we were to use the default mergeLeft function,
   * the output would be: [ 4, 2, _, _ ]. The same happens when you consider a column
   * and try shifting downwards. In order to fix this, the row/column (in general 'sequence')
   * needs to be reversed, and then we can perform the usual mergeLeft and after reversing
   * again we will get the desired behaviour.
   */
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
        Tile mergedTile = Tile.merge(current, successor);
        merged.add(mergedTile);
        score += mergedTile.getValue();
        occupiedTiles--;
      } else {
        merged.add(current);
      }
    }

    // If there is space left in the row, we need to add empty tiles to it.
    while (merged.size() < dimension) {
      merged.add(Tile.getEmptyTile());
    }
    return merged.toArray(Tile[]::new);
  }

  private void spawnTile() {
    Tile newTile = Tile.generateRandomTile();
    GridUtil.Position emptyCell = GridUtil.getRandomEmptyCell(grid);
    grid[emptyCell.getY()][emptyCell.getX()] = newTile;
    occupiedTiles++;
    score += newTile.getValue();
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
    // A row/column can be merged if it contains two consecutive tiles of the same value
    // those tiles might be separated by an empty tile, therefore we need to remove those
    // before we perform the check.
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
    return grid;
  }
}
