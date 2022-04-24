package Game2048;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.security.Key;

public class GameView {
    private final Grid grid;
    private final DefaultTerminalFactory defaultTerminalFactory;
    private Terminal terminal = null;

    public GameView(Grid grid) {
        this.grid = grid;
        this.defaultTerminalFactory = new DefaultTerminalFactory();
    }

    public void startDisplay() {
        try {
            terminal = defaultTerminalFactory.createTerminal();
            terminal.setCursorPosition(0, 0);
            terminal.clearScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateDisplay() {
        try {
            terminal.setCursorPosition(0, 0);
            terminal.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            terminal.putString("      Score: ");
            terminal.setForegroundColor(TextColor.ANSI.CYAN);
            terminal.putString(String.valueOf(grid.getScore()));
            terminal.putCharacter('\n');
            terminal.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            terminal.putString(" -------------------");
            terminal.putCharacter('\n');
            for (Tile[] row : grid.grid) {
                terminal.putCharacter('|');
                for (Tile tile : row) {
                    if (tile.isEmpty()) {
                        terminal.putString("    ");
                    } else {
                        terminal.setForegroundColor(TextColor.ANSI.CYAN);
                        terminal.putString(String.format("%4s", tile.getValue()));
                        terminal.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
                    }
                    terminal.putCharacter('|');
                }
                terminal.putCharacter('\n');
                terminal.putString(" -------------------");
                terminal.putCharacter('\n');
            }
            if (grid.isGameOver()) {
                terminal.setForegroundColor(TextColor.ANSI.RED);
                terminal.putString("      Game Over!");
                terminal.putCharacter('\n');
            }
            terminal.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public KeyStroke getInput() {
        KeyStroke stroke;
        try {
            stroke = terminal.readInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stroke;
    }
}
