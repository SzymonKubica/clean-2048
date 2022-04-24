package Game2048;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GridTest {

    @Test
    public void initialisingTheGridSetsAllTilesTo0() {
        Grid grid = new Grid(4);
        for (Tile[] row : grid.grid) {
            for (Tile tile : row) {
                assert (tile.getValue() == 0);
            }
        }

    }

    @Test
    public void mergingMergesConsecutiveTilesOfSameValueEvenIfNotAdjacent() {
        Tile[] row = {new Tile(2), new EmptyCell(), new Tile(2), new EmptyCell()};
        Grid grid = new Grid(4);
        grid.grid[0] = row;
        grid.shift(Grid.Direction.LEFT);
        assert (grid.grid[0][0].getValue() == 4);
    }

    @Test
    public void unmergedTilesGetAddedToTheMergedList() {
        Tile[] row = { new Tile(2), new Tile(4), new Tile(2), new EmptyCell() };
        Grid grid = new Grid(4);
        grid.grid[0] = row;
        grid.shift(Grid.Direction.LEFT);
        Tile[] mergedRow = grid.grid[0];
        assert (mergedRow[0].getValue() == 2);
        assert (mergedRow[1].getValue() == 4);
        assert (mergedRow[2].getValue() == 2);
    }

    @Test
    public void shiftLeftFlushesTheContentsToTheLeft() {
        Tile[] row = { new EmptyCell(), new Tile(2), new Tile(4), new Tile(2) };
        Grid grid = new Grid(4);
        grid.grid[0] = row;
        grid.shift(Grid.Direction.LEFT);
        Tile[] mergedRow = grid.grid[0];
        assert (mergedRow[0].getValue() == 2);
        assert (mergedRow[1].getValue() == 4);
        assert (mergedRow[2].getValue() == 2);
        assert (mergedRow[3].getValue() == 0);

    }

    @Test
    public void shiftRightFlushesTheContentsToTheRight() {
        Tile[] row = { new Tile(2), new Tile(4), new Tile(2), new EmptyCell() };
        Grid grid = new Grid(4);
        grid.grid[0] = row;
        grid.shift(Grid.Direction.RIGHT);
        Tile[] mergedRow = grid.grid[0];
        assert (mergedRow[0].getValue() == 0);
        assert (mergedRow[1].getValue() == 2);
        assert (mergedRow[2].getValue() == 4);
        assert (mergedRow[3].getValue() == 2);

    }

    @Test
    public void shiftDownFlushesTheContentsDownwards() {
        Tile[] row = { new Tile(2), new Tile(4), new Tile(2), new EmptyCell() };
        Grid grid = new Grid(4);
        grid.grid[0] = row;
        grid.shift(Grid.Direction.DOWN);
        Tile[] mergedRow = grid.grid[3];
        assert (mergedRow[0].getValue() == 2);
        assert (mergedRow[1].getValue() == 4);
        assert (mergedRow[2].getValue() == 2);
        assert (mergedRow[3].getValue() == 0);

    }

    @Test
    public void shiftUpFlushesTheContentsUpwards() {
        Tile[] row = { new Tile(2), new Tile(4), new Tile(2), new EmptyCell() };
        Grid grid = new Grid(4);
        grid.grid[3] = row;
        grid.shift(Grid.Direction.UP);
        Tile[] mergedRow = grid.grid[0];
        assert (mergedRow[0].getValue() == 2);
        assert (mergedRow[1].getValue() == 4);
        assert (mergedRow[2].getValue() == 2);
        assert (mergedRow[3].getValue() == 0);

    }
}
