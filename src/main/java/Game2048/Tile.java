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

    public boolean hasEqualValue(Tile other) {
        return value == other.getValue();
    }

}
