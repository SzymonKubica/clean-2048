package clean2048.lib.lanterna;

import static clean2048.view.Color.*;
import static com.googlecode.lanterna.TextColor.ANSI.*;
import static com.googlecode.lanterna.TextColor.ANSI.CYAN;
import static com.googlecode.lanterna.TextColor.ANSI.RED;

import clean2048.view.Color;
import clean2048.view.Terminal;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.TerminalFactory;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LanternaTerminalAdapter implements Terminal {
  private final TerminalFactory defaultTerminalFactory;
  private com.googlecode.lanterna.terminal.Terminal terminal = null;

  public LanternaTerminalAdapter() {
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
  public void printLineCentered(String line) throws IOException {
    printLineCentered(line, GREY);
  }

  @Override
  public void printLineCentered(String line, Color color) throws IOException {
    String margin = getCenteringMargin(line.length());
    printLine(margin + line, color);
  }

  @Override
  public void printStringCentered(String string) throws IOException {
    printStringCentered(string, GREY);
  }

  @Override
  public void printStringCentered(String string, Color color) throws IOException {
    String margin = getCenteringMargin(string.length());
    printString(margin + string, color);
  }

  @Override
  public int getHorizontalCenteringMargin(int textWidth) throws IOException {
    return (getTerminalWidth() - textWidth) / 2;
  }

  @Override
  public int getVerticalCenteringMargin(int textHeight) throws IOException {
    return (getTerminalHeight() - textHeight) / 2;
  }

  private String getCenteringMargin(int textLength) throws IOException {
    return getPaddingString((getTerminalWidth() - textLength) / 2);
  }

  private String getPaddingString(int length) {
    return IntStream.range(0, length).mapToObj(i -> " ").collect(Collectors.joining(""));
  }

  @Override
  public void flushChanges() throws IOException {
    terminal.flush();
  }

  @Override
  public KeyType getUserInput() throws IOException {
    return terminal.readInput().getKeyType();
  }

  @Override
  public Character readCharacter() throws IOException {
    return terminal.readInput().getCharacter();
  }

  @Override
  public int getTerminalWidth() throws IOException {
    return terminal.getTerminalSize().getColumns();
  }

  @Override
  public int getTerminalHeight() throws IOException {
    return terminal.getTerminalSize().getRows();
  }

  @Override
  public void addResizeListener(TerminalResizeListener listener) {
    terminal.addResizeListener(listener);
  }

  @Override
  public void setCursorVisible() throws IOException {
    terminal.setCursorVisible(true);
  }

  @Override
  public void clearScreen() throws IOException {
    terminal.clearScreen();

  }
}
