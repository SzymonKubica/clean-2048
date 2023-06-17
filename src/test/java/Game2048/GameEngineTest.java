package Game2048;

import Game2048.controller.Direction;
import Game2048.engine.GameEngine;
import Game2048.engine.Tile;
import org.junit.Test;

public class GameEngineTest {

  @Test
  public void initialisingTheGridSetsAllTilesTo0() {
    GameEngine gameEngine = new GameEngine(4);
    for (Tile[] row : gameEngine.getGrid()) {
      for (Tile tile : row) {
        assert (tile.getValue() == 0);
      }
    }
  }

  @Test
  public void mergingMergesConsecutiveTilesOfSameValueEvenIfNotAdjacent() {
    Tile[] row = {new Tile(2), Tile.getEmptyTile(), new Tile(2), Tile.getEmptyTile()};
    GameEngine gameEngine = new GameEngine(4);
    gameEngine.getGrid()[0] = row;
    gameEngine.shift(Direction.LEFT);
    assert (gameEngine.getGrid()[0][0].getValue() == 4);
  }

  @Test
  public void unmergedTilesGetAddedToTheMergedList() {
    Tile[] row = {new Tile(2), new Tile(4), new Tile(2), Tile.getEmptyTile()};
    GameEngine gameEngine = new GameEngine(4);
    gameEngine.getGrid()[0] = row;
    gameEngine.shift(Direction.LEFT);
    Tile[] mergedRow = gameEngine.getGrid()[0];
    assert (mergedRow[0].getValue() == 2);
    assert (mergedRow[1].getValue() == 4);
    assert (mergedRow[2].getValue() == 2);
  }

  @Test
  public void shiftLeftFlushesTheContentsToTheLeft() {
    Tile[] row = {Tile.getEmptyTile(), new Tile(2), new Tile(4), new Tile(2)};
    GameEngine gameEngine = new GameEngine(4);
    gameEngine.getGrid()[0] = row;
    gameEngine.shift(Direction.LEFT);
    Tile[] mergedRow = gameEngine.getGrid()[0];
    assert (mergedRow[0].getValue() == 2);
    assert (mergedRow[1].getValue() == 4);
    assert (mergedRow[2].getValue() == 2);
    assert (mergedRow[3].getValue() == 0);
  }

  @Test
  public void shiftRightFlushesTheContentsToTheRight() {
    Tile[] row = {new Tile(2), new Tile(4), new Tile(2), Tile.getEmptyTile()};
    GameEngine gameEngine = new GameEngine(4);
    gameEngine.getGrid()[0] = row;
    gameEngine.shift(Direction.RIGHT);
    Tile[] mergedRow = gameEngine.getGrid()[0];
    assert (mergedRow[0].getValue() == 0);
    assert (mergedRow[1].getValue() == 2);
    assert (mergedRow[2].getValue() == 4);
    assert (mergedRow[3].getValue() == 2);
  }

  @Test
  public void shiftDownFlushesTheContentsDownwards() {
    Tile[] row = {new Tile(2), new Tile(4), new Tile(2), Tile.getEmptyTile()};
    GameEngine gameEngine = new GameEngine(4);
    gameEngine.getGrid()[0] = row;
    gameEngine.shift(Direction.DOWN);
    Tile[] mergedRow = gameEngine.getGrid()[3];
    assert (mergedRow[0].getValue() == 2);
    assert (mergedRow[1].getValue() == 4);
    assert (mergedRow[2].getValue() == 2);
    assert (mergedRow[3].getValue() == 0);
  }

  @Test
  public void shiftUpFlushesTheContentsUpwards() {
    Tile[] row = {new Tile(2), new Tile(4), new Tile(2), Tile.getEmptyTile()};
    GameEngine gameEngine = new GameEngine(4);
    gameEngine.getGrid()[3] = row;
    gameEngine.shift(Direction.UP);
    Tile[] mergedRow = gameEngine.getGrid()[0];
    assert (mergedRow[0].getValue() == 2);
    assert (mergedRow[1].getValue() == 4);
    assert (mergedRow[2].getValue() == 2);
    assert (mergedRow[3].getValue() == 0);
  }
}
