package Game2048;

import org.junit.Test;

import java.util.List;

public class GridTest {

    @Test
    public void initialisingTheGridSetsAllTilesTo0() {
        Grid grid = new Grid(4);
        for (List<Tile> row : grid.grid) {
            for (Tile tile : row) {
                assert (tile.getValue() == 0);
            }
        }

    }

    @Test
    public void mergingMergesConsecutiveTilesOfSameValueEvenIfNotAdjacent() {
        List<Tile> row = List.of(new Tile(2), new EmptyCell(), new Tile(2), new EmptyCell());
        Grid grid = new Grid(4);
        grid.grid.add(0, row);
        grid.shiftLeft();
        assert(grid.grid.get(0).get(0).getValue() == 4);
    }

    @Test
    public void unmergedTilesGetAddedToTheMergedList() {
        List<Tile> row = List.of(new Tile(2), new Tile(4), new Tile(2), new EmptyCell());
        Grid grid = new Grid(4);
        grid.grid.add(0, row);
        grid.shiftLeft();
        List<Tile> mergedRow = grid.grid.get(0);
        assert(mergedRow.get(0).getValue() == 2);
        assert(mergedRow.get(1).getValue() == 4);
        assert(mergedRow.get(2).getValue() == 2);
    }

    @Test
    public void shiftLeftFlushesTheContentsToTheLeft() {
        List<Tile> row = List.of(new EmptyCell(), new Tile(2), new Tile(4), new Tile(2));
        Grid grid = new Grid(4);
        grid.grid.add(0, row);
        grid.shiftLeft();
        List<Tile> mergedRow = grid.grid.get(0);
        assert(mergedRow.get(0).getValue() == 2);
        assert(mergedRow.get(1).getValue() == 4);
        assert(mergedRow.get(2).getValue() == 2);
        assert(mergedRow.get(3).getValue() == 0);

    }
}
