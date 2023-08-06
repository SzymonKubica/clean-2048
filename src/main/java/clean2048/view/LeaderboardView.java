package clean2048.view;

import clean2048.lib.lanterna.LanternaTerminal;
import clean2048.user_data.User;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeaderboardView {
  private static final String PLACE = "Place";
  private static final String USER_NAME = "User Name";
  private static final String SCORE = "Score";
  private final LanternaTerminal terminal;

  public LeaderboardView(LanternaTerminal terminal) {
    this.terminal = terminal;
  }

  public void printLeaderboard(Map<String, User> leaderboard) {
    List<String> places =
        IntStream.rangeClosed(1, leaderboard.size()).mapToObj(Integer::toString).toList();
    List<String> scores =
        leaderboard.values().stream().map(user -> user.highScore).map(Object::toString).toList();

    int placeColumnWidth = getColumnWidth(PLACE, places);
    int userNameColumnWidth = getColumnWidth(USER_NAME, leaderboard.keySet());
    int scoreColumnWidth = getColumnWidth(SCORE, scores);

    String leaderboardHeader =
        getCenteredLeaderboardHeader(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);

    String separatorLine = getSeparatorLine(leaderboardHeader.length());

    String rowTemplate = getRowTemplate(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);
    List<User> users = new ArrayList<>(leaderboard.values().stream().toList());
    Collections.sort(users);
    List<String> scoreRows =
        users.stream()
            .map(
                user ->
                    rowTemplate.formatted(users.indexOf(user) + 1, user.userName, user.highScore))
            .toList();

    try {
      terminal.printNewLine();
      terminal.printLineCentered("Leaderboard");
      terminal.printLineCentered(separatorLine);
      terminal.printLineCentered(leaderboardHeader);
      terminal.printLineCentered(separatorLine);
      for (String scoreRow : scoreRows) {
        terminal.printLineCentered(scoreRow);
      }
      terminal.printLineCentered(separatorLine);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private int getColumnWidth(String columnHeader, Collection<String> columnContents) {
    int maxEntryWidth = Collections.max(columnContents.stream().map(String::length).toList());
    return Math.max(columnHeader.length(), maxEntryWidth);
  }

  public void printLeaderboardHighlightingRow(Map<String, User> leaderboard, int row) {
    try {
      final String PLACE = "Place";
      final String USER_NAME = "User Name";
      final String SCORE = "Score";

      int maxPlaceIndexLength = String.valueOf(leaderboard.keySet().size()).length();
      int placeColumnWidth = Math.max(PLACE.length(), maxPlaceIndexLength);

      int maxUserNameLength =
          Collections.max(leaderboard.keySet().stream().map(String::length).toList());
      int userNameColumnWidth = Math.max(USER_NAME.length(), maxUserNameLength);

      int maxScoreLength =
          Collections.max(
              leaderboard.values().stream()
                  .map(user -> user.highScore)
                  .map(String::valueOf)
                  .map(String::length)
                  .toList());
      int scoreColumnWidth = Math.max(SCORE.length(), maxScoreLength);

      String leaderboardHeader =
          getCenteredLeaderboardHeader(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);
      String line = getSeparatorLine(leaderboardHeader.length());
      String rowTemplate = getRowTemplate(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);

      terminal.printNewLine();
      terminal.printLineCentered("Leaderboard");
      terminal.printLineCentered(line);

      terminal.printLineCentered(leaderboardHeader);
      terminal.printLineCentered(line);

      List<User> scores = new ArrayList<>(leaderboard.values().stream().toList());

      Collections.sort(scores);

      for (int i = 0; i < leaderboard.keySet().size(); i++) {
        int place = i + 1;
        User score = scores.get(i);
        String leaderboardRow = rowTemplate.formatted(place, score.userName, score.highScore);
        if (i == row) {
          terminal.printLineCentered(leaderboardRow, Color.CYAN);
        } else {
          terminal.printLineCentered(leaderboardRow);
        }
      }

      terminal.printLineCentered(line);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getRowTemplate(
      int placeColumnWidth, int userNameColumnWidth, int scoreColumnWidth) {
    final String template = "| Place. | UserName | Score |";
    return template
        .replace("Place", "%" + (placeColumnWidth - 1) + "s")
        .replace("UserName", "%-" + userNameColumnWidth + "s")
        .replace("Score", "%" + scoreColumnWidth + "s");
  }

  private String getSeparatorLine(int length) {
    return String.join("", IntStream.range(0, length).mapToObj(i -> "-").toList());
  }

  /* Widths of the columns are determined by the length of the longest item in each column. */
  public String getCenteredLeaderboardHeader(
      int placeColumnWidth, int userNameColumnWidth, int scoreColumnWidth) {
    final String template = "| PLACE | USER_NAME | SCORE |";
    final String PLACE = "Place";
    final String USER_NAME = "User Name";
    final String SCORE = "Score";

    String formattedTemplate =
        template
            .replace("PLACE", getFormatStringOfWidth(placeColumnWidth))
            .replace("USER_NAME", getFormatStringOfWidth(userNameColumnWidth))
            .replace("SCORE", getFormatStringOfWidth(scoreColumnWidth));

    return formattedTemplate.formatted(
        addPaddingTo(PLACE, getCenterPaddingWidth(PLACE, placeColumnWidth)),
        addPaddingTo(USER_NAME, getCenterPaddingWidth(USER_NAME, userNameColumnWidth)),
        addPaddingTo(SCORE, getCenterPaddingWidth(SCORE, scoreColumnWidth)));
  }

  /*
   It returns strings like "%5s" which can then later be used for centering the
   text inside of that format string.
  */
  private String getFormatStringOfWidth(int width) {
    return "%" + width + "s";
  }

  private String addPaddingTo(String source, int paddingWidth) {
    String padding = getPadding(paddingWidth);
    return padding + source + padding;
  }

  private String getPadding(int paddingLength) {
    return IntStream.range(0, paddingLength).mapToObj(i -> " ").collect(Collectors.joining(""));
  }

  private int getCenterPaddingWidth(String s, int columnWidth) {
    return (columnWidth - s.length()) / 2;
  }
}
