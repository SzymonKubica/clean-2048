package Game2048;

import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;

public class LanternaGameController implements GameController {
    private final LanternaTerminalAdapter adapter;
    public LanternaGameController(LanternaTerminalAdapter adapter) {
        this.adapter = adapter;
    }
    @Override
    public Direction getMove() {
        KeyType key;
        try {
            key = adapter.getGameHostTerminal().readInput().getKeyType();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return switch (key) {
            case ArrowLeft -> Direction.LEFT;
            case ArrowUp -> Direction.UP;
            case ArrowDown -> Direction.DOWN;
            case ArrowRight -> Direction.RIGHT;
            default -> throw new IllegalStateException("Unexpected value: " + key);
        };

    }
}
