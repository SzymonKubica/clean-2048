package clean2048.engine;

import java.util.Objects;
import lombok.Getter;

public class Tile {
  @Getter private final int value;

  public Tile(int value) {
    this.value = value;
  }

  public boolean isEmpty() {
    return value == 0;
  }

  public static Tile getEmptyTile() {
    return new Tile(0);
  }

  public static Tile generateRandomTile() {
    int seed = (int) (10 * Math.random()) % InitialTileValues.values().length;
    return new Tile(InitialTileValues.values()[seed].getValue());
  }

  public static Tile merge(Tile tile1, Tile tile2) {
    return new Tile(tile1.getValue() + tile2.getValue());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tile tile = (Tile) o;
    return value == tile.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  private enum InitialTileValues {
    TWO(2),
    FOUR(4);

    @Getter
    private final int value;

    InitialTileValues(int value) {
      this.value = value;
    }
  }
}
