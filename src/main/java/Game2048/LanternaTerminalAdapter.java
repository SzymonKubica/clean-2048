package Game2048;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;

import java.io.IOException;

import static Game2048.Color.*;
import static com.googlecode.lanterna.TextColor.ANSI.*;
import static com.googlecode.lanterna.TextColor.ANSI.CYAN;
import static com.googlecode.lanterna.TextColor.ANSI.RED;

public class LanternaTerminalAdapter implements TerminalBackend {
    private final TerminalFactory defaultTerminalFactory;
    private Terminal terminal = null;

    public LanternaTerminalAdapter(TerminalFactory factory) {
        this.defaultTerminalFactory = factory;
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
            case CYAN -> CYAN;
            case WHITE -> ANSI.WHITE;
            case LIGHT_CYAN -> CYAN_BRIGHT;
            case LIGHT_BLUE -> BLUE_BRIGHT;
            case LIGHT_GREEN -> GREEN_BRIGHT;
            case GREY -> BLACK_BRIGHT;
            case GREEN -> ANSI.GREEN;
            case LIGHT_YELLOW -> YELLOW_BRIGHT;
            case YELLOW -> ANSI.YELLOW;
            case LIGHT_RED -> RED_BRIGHT;
            case RED -> RED;
            case MAGENTA -> ANSI.MAGENTA;
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
        printLine(line, GREY);
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
        printString(string, GREY);
    }

    @Override
    public void printString(String string, Color color) throws IOException {
        terminal.setForegroundColor(translateColor(color));
        terminal.putString(string);
    }

    @Override
    public void printCharacter(char c) throws IOException {
        printCharacter(c, GREY);
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