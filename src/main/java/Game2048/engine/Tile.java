package Game2048.engine;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Tile {
  @Getter @Setter private int value;

  public boolean isEmpty() {
    return value == 0;
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "    ";
    } else {
      return String.format("%4s", getValue());
    }
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
}
