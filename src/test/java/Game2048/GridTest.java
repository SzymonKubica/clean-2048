package Game2048;

import org.junit.Test;

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
}
