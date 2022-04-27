package Game2048;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

import static com.googlecode.lanterna.TextColor.ANSI.*;

public class LanternaBackendAdapter implements TerminalBackend {
    private final DefaultTerminalFactory defaultTerminalFactory;
    private Terminal terminal = null;

    public LanternaBackendAdapter() {
        this.defaultTerminalFactory = new DefaultTerminalFactory();
        startDisplay();
    }

    private void startDisplay() {
        try {
            terminal = defaultTerminalFactory.createTerminal();
            terminal.clearScreen();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TextColor translateColor(Color color) {
        return switch (color) {
            case GREY -> BLACK_BRIGHT;
            case CYAN -> CYAN;
            case RED -> RED;
        };
    }

    public Terminal getGameHostTerminal() {
        return this.terminal;
    }

    @Override
    public void resetCursorPosition() throws IOException {
        terminal.setCursorPosition(0, 0);
    }

    @Override
    public void printLine(String line) throws IOException {
        printLine(line, Color.GREY);
    }

    @Override
    public void printLine(String line, Color color) throws IOException {
        printString(line, color);
        printCharacter('\n');
    }

    @Override
    public void printNewLine() throws IOException {
        printLine("");
    }

    @Override
    public void printString(String string) throws IOException {
        printString(string, Color.GREY);
    }

    @Override
    public void printString(String string, Color color) throws IOException {
        terminal.setForegroundColor(translateColor(color));
        terminal.putString(string);
    }

    @Override
    public void printCharacter(char c) throws IOException {
        printCharacter(c, Color.GREY);
    }

    @Override
    public void printCharacter(char c, Color color) throws IOException {
        terminal.setForegroundColor(translateColor(color));
        terminal.putCharacter(c);
    }

    @Override
    public void flushChanges() throws IOException {
        terminal.flush();
    }
}