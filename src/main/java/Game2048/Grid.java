package Game2048;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

public class Grid {
    private final int dimension;
    final List<List<Tile>> grid;

    public Grid(int dimension) {
        this.dimension = dimension;
        grid = initaliseGrid(this.dimension);
    }

    private List<List<Tile>> initaliseGrid(int dimension) {
       List<List<Tile>> grid = new ArrayList<>();
       for (int i = 0; i < dimension; i++) {
           List<Tile> row = new ArrayList<>();
           for (int j = 0; j < dimension; j++) {
               row.add(new EmptyCell());
           }
       }
       return grid;
    }

    public void flushLeft(List<Tile> row, int dimension) {
        for (int i = 0; i < dimension - row.size(); i++) {
            row.add(new EmptyCell());
        }
    }
    public void shiftLeft() {
        for (List<Tile> row : grid) {
            int index = grid.indexOf(row);
            List<Tile> mergedRow = merge(row);
            flushLeft(mergedRow, dimension);
            grid.remove(index);
            grid.add(index, mergedRow);
        }
    }

    private Tile getNext(Tile current, List<Tile> row) {
        int currentIndex = row.indexOf(current);
        if (currentIndex < row.size() - 1) {
            return row.get(currentIndex + 1);
        } else {
            return null;
        }
    }

    private Tile getSuccessor(Tile current, List<Tile> row) {
        if (current == null) {
            return null;
        }
        Tile successor = getNext(current, row);
        while(successor != null && successor.isEmpty()) {
            successor = getNext(successor, row);
        }
        return successor;
    }

    private List<Tile> merge(List<Tile> row) {
        List<Tile> mergedRow = new ArrayList<>();
        Tile currentTile = row.get(0);

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
                mergedRow.add(currentTile);
                break;
            }
            if (currentTile.hasEqualValue(successor)) {
                mergedRow.add(new Tile(currentTile.getValue() + successor.getValue()));
            } else {
                mergedRow.add(currentTile);
                mergedRow.add(successor);
            }
            currentTile = getNext(successor, row);
        }

        return mergedRow;
    }
}
