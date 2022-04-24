package Game2048;

import java.util.ArrayList;

public class Grid {
    private final int dimension;
    final  ArrayList<ArrayList<Tile>> grid;

    public Grid(int dimension) {
        this.dimension = dimension;
        grid = initialiseGrid(this.dimension);
    }

    private ArrayList<ArrayList<Tile>> initialiseGrid(int dimension) {
       ArrayList<ArrayList<Tile>>  grid = new ArrayList<>();
       for (int i = 0; i < dimension; i++) {
           ArrayList<Tile> row = new ArrayList<>();
           for (int j = 0; j < dimension; j++) {
               row.add(new EmptyCell());
           }
           grid.add(row);
       }
       return grid;
    }

    private void flush(ArrayList<Tile> row, Direction direction) {
        for (int i = 0; i < dimension - row.size(); i++) {
            if (direction == Direction.LEFT || direction == Direction.UP) {
                row.add(new EmptyCell());
            } else {
                row.add(0, new EmptyCell());
            }
        }

    }

    public void shift(Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            transpose();
        }
        System.out.println(grid);
        for (int i = 0; i < dimension; i++) {
            ArrayList<Tile> row = grid.get(i);
            ArrayList<Tile> mergedRow = merge(row);
            flush(mergedRow, direction);
            grid.remove(i);
            grid.add(i, mergedRow);
        }
        if (direction == Direction.UP || direction == Direction.DOWN) {
            transpose();
        }
    }

    private void swapIndices(int i, int j) {
        Tile temp = grid.get(i).get(j);
        grid.get(i).remove(j);
        grid.get(i).add(j, grid.get(j).get(i));
        grid.get(j).remove(i);
        grid.get(j).add(i, temp);
    }

    private void transpose() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i != j) {
                    swapIndices(i, j);
                }
            }
        }
    }

    private Tile getNext(Tile current,ArrayList<Tile> row) {
        int currentIndex = row.indexOf(current);
        if (currentIndex < row.size() - 1) {
            return row.get(currentIndex + 1);
        } else {
            return null;
        }
    }

    private Tile getSuccessor(Tile current,ArrayList<Tile> row) {
        if (current == null) {
            return null;
        }
        Tile successor = getNext(current, row);
        while(successor != null && successor.isEmpty()) {
            successor = getNext(successor, row);
        }
        return successor;
    }

    private ArrayList<Tile> merge(ArrayList<Tile> row) {
        ArrayList<Tile> mergedRow = new ArrayList<>();
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
    public enum Direction {
        LEFT, RIGHT, UP, DOWN
    }
}
