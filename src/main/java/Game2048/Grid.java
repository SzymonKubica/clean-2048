package Game2048;

public class Grid {
    private final int dimension;
    final Tile[][] grid;

    public Grid(int dimension) {
        this.dimension = dimension;
        grid = initaliseGrid(this.dimension);
    }

    private Tile[][] initaliseGrid(int dimension) {
       Tile[][] grid = new Tile[dimension][dimension];
       for (int i = 0; i < dimension; i++) {
           for (int j = 0; j < dimension; j++) {
               grid[i][j] = new Tile(0);
           }
       }
       return grid;
    }
}
