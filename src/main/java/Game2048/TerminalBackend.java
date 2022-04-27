package Game2048;

import java.io.IOException;

public interface TerminalBackend {
    void resetCursorPosition() throws IOException;
    void printLine(String line) throws IOException;
    void printLine(String line, Color color) throws IOException;
    void printNewLine() throws IOException;
    void printString(String string) throws IOException;
    void printString(String string, Color color) throws IOException;
    void printCharacter(char c) throws IOException;
    void printCharacter(char c, Color color) throws IOException;
    void flushChanges() throws IOException;
}
