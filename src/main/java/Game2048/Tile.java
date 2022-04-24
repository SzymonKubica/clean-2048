package Game2048;

import java.util.Objects;

public class Tile {
    private int value;
    public Tile(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "value=" + value +
                '}';
    }

    public boolean hasEqualValue(Tile other) {
        return value == other.getValue();
    }

}
