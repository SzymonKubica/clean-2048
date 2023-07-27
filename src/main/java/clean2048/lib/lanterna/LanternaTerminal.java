package clean2048.lib.lanterna;

import static clean2048.view.Color.*;
import static com.googlecode.lanterna.TextColor.ANSI.*;
import static com.googlecode.lanterna.TextColor.ANSI.CYAN;
import static com.googlecode.lanterna.TextColor.ANSI.RED;

import clean2048.view.Color;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.TerminalFactory;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LanternaTerminal {
  private final TerminalFactory defaultTerminalFactory;
  private com.googlecode.lanterna.terminal.Terminal terminal = null;

  public LanternaTerminal() {
    this.defaultTerminalFactory = new DefaultTerminalFactory();
    startDisplay();
  }

  private void startDisplay() {
    try {
      terminal = defaultTerminalFactory.createTerminal();
      terminal.setCursorVisible(false);
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

  public void resetCursorPosition() throws IOException {
    terminal.setCursorPosition(0, 0);
  }

  public void printLine(String line) throws IOException {
    printLine(line, GREY);
  }

  public void printLine(String line, Color color) throws IOException {
    printString(line, color);
    printCharacter('\n');
  }

  public void printNewLine() throws IOException {
    printLine("");
  }

  public void printString(String string) throws IOException {
    printString(string, GREY);
  }

  public void printString(String string, Color color) throws IOException {
    terminal.setForegroundColor(translateColor(color));
    terminal.putString(string);
  }

  public void printCharacter(char c) throws IOException {
    printCharacter(c, GREY);
  }

  public void printCharacter(char c, Color color) throws IOException {
    terminal.setForegroundColor(translateColor(color));
    terminal.putCharacter(c);
  }

  public void printLineCentered(String line) throws IOException {
    printLineCentered(line, GREY);
  }

  public void printLineCentered(String line, Color color) throws IOException {
    String margin = getCenteringMargin(line.length());
    printLine(margin + line, color);
  }

  public void printStringCentered(String string) throws IOException {
    printStringCentered(string, GREY);
  }

  public void printStringCentered(String string, Color color) throws IOException {
    String margin = getCenteringMargin(string.length());
    printString(margin + string, color);
  }

  public int getHorizontalCenteringMargin(int textWidth) throws IOException {
    return (getTerminalWidth() - textWidth) / 2;
  }

  public int getVerticalCenteringMargin(int textHeight) throws IOException {
    return (getTerminalHeight() - textHeight) / 2;
  }

  private String getCenteringMargin(int textLength) throws IOException {
    return getPaddingString((getTerminalWidth() - textLength) / 2);
  }

  private String getPaddingString(int length) {
    return IntStream.range(0, length).mapToObj(i -> " ").collect(Collectors.joining(""));
  }

  public void flushChanges() throws IOException {
    terminal.flush();
  }

  public KeyType getUserInput() throws IOException {
    return terminal.readInput().getKeyType();
  }

  public Character readCharacter() throws IOException {
    return terminal.readInput().getCharacter();
  }

  public int getTerminalWidth() throws IOException {
    return terminal.getTerminalSize().getColumns();
  }

  public int getTerminalHeight() throws IOException {
    return terminal.getTerminalSize().getRows();
  }

  public void addResizeListener(TerminalResizeListener listener) {
    terminal.addResizeListener(listener);
  }

  public void setCursorVisible(boolean isVisible) throws IOException {
    terminal.setCursorVisible(isVisible);
  }

  public void clearScreen() throws IOException {
    terminal.clearScreen();
  }
}
