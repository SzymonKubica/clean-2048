package clean2048.view;

import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import java.io.IOException;

public interface Terminal {
  void resetCursorPosition() throws IOException;

  void printLine(String line) throws IOException;

  void printLine(String line, Color color) throws IOException;

  void printNewLine() throws IOException;

  void printString(String string) throws IOException;

  void printString(String string, Color color) throws IOException;

  void printCharacter(char c) throws IOException;

  void printCharacter(char c, Color color) throws IOException;

  void printLineCentered(String line) throws IOException;

  void printLineCentered(String line, Color color) throws IOException;

  void printStringCentered(String string) throws IOException;

  void printStringCentered(String string, Color color) throws IOException;

  int getHorizontalCenteringMargin(int textWidth) throws IOException;
  int getVerticalCenteringMargin(int textHeight) throws IOException;

  void flushChanges() throws IOException;

  KeyType getUserInput() throws IOException;
  Character readCharacter() throws IOException;

  int getTerminalWidth() throws IOException;
  int getTerminalHeight() throws IOException;
  void addResizeListener(TerminalResizeListener listener);

  void setCursorVisible() throws IOException;

}
