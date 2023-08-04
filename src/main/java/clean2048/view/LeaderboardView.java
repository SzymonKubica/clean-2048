package clean2048.view;

import clean2048.lib.lanterna.LanternaTerminal;
import clean2048.user_data.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeaderboardView {
    private final LanternaTerminal terminal;

    public LeaderboardView(LanternaTerminal terminal) {
        this.terminal = terminal;
    }

    public void printLeaderboard(Map<String, User> leaderboard) {
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

            String rowTemplate = getRowTemplate(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);
            int BORDERS_WIDTH = 10;

            String line =
                    getSeparatorLine(
                            placeColumnWidth + userNameColumnWidth + scoreColumnWidth + BORDERS_WIDTH);

            terminal.printNewLine();
            terminal.printLineCentered("Leaderboard");
            terminal.printLineCentered(line);
            List<String> keys = leaderboard.keySet().stream().toList();

            String leaderboardHeader =
                    getCenteredLeaderboardHeader(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);
            terminal.printLineCentered(leaderboardHeader);
            terminal.printLineCentered(line);

            List<User> scores = new ArrayList<>(leaderboard.values().stream().toList());

            Collections.sort(scores);

            for (int i = 0; i < keys.size(); i++) {
                int place = i + 1;
                User score = scores.get(i);
                String leaderboardRow = rowTemplate.formatted(place, score.userName, score.highScore);
                terminal.printLineCentered(leaderboardRow);
            }

            terminal.printLineCentered(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

            String rowTemplate = getRowTemplate(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);
            int BORDERS_WIDTH = 10;

            String line =
                    getSeparatorLine(
                            placeColumnWidth + userNameColumnWidth + scoreColumnWidth + BORDERS_WIDTH);

            terminal.printNewLine();
            terminal.printLineCentered("Leaderboard");
            terminal.printLineCentered(line);
            List<String> keys = leaderboard.keySet().stream().toList();

            String leaderboardHeader =
                    getCenteredLeaderboardHeader(placeColumnWidth, userNameColumnWidth, scoreColumnWidth);
            terminal.printLineCentered(leaderboardHeader);
            terminal.printLineCentered(line);

            List<User> scores = new ArrayList<>(leaderboard.values().stream().toList());

            Collections.sort(scores);

            for (int i = 0; i < keys.size(); i++) {
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

    private String getSeparatorLine(int length) {
        return String.join("", IntStream.range(0, length).mapToObj(i -> "-").toList());
    }

    public String getCenteredLeaderboardHeader(
            int placeColumnWidth, int userNameColumnWidth, int scoreColumnWidth) {
        final String PLACE = "Place";
        final String USER_NAME = "User Name";
        final String SCORE = "Score";
        final String template = "| PLACE | USER_NAME | SCORE |";

        int placeColumnMargin = (placeColumnWidth - PLACE.length()) / 2;
        int userNameColumnMargin = (userNameColumnWidth - USER_NAME.length()) / 2;
        int scoreColumnMargin = (scoreColumnWidth - SCORE.length()) / 2;

        String placeColumnPadding =
                IntStream.range(0, placeColumnMargin).mapToObj(i -> " ").collect(Collectors.joining(""));
        String userNameColumnPadding =
                IntStream.range(0, userNameColumnMargin).mapToObj(i -> " ").collect(Collectors.joining(""));
        String scoreColumnPadding =
                IntStream.range(0, scoreColumnMargin).mapToObj(i -> " ").collect(Collectors.joining(""));

        String formattedTemplate =
                template
                        .replace("PLACE", "%" + placeColumnWidth + "s")
                        .replace("USER_NAME", "%" + userNameColumnWidth + "s")
                        .replace("SCORE", "%" + scoreColumnWidth + "s");

        return formattedTemplate.formatted(
                placeColumnPadding + PLACE + placeColumnPadding,
                userNameColumnPadding + USER_NAME + userNameColumnPadding,
                scoreColumnPadding + SCORE + scoreColumnPadding);
    }

    public String getRowTemplate(
            int placeColumnWidth, int userNameColumnWidth, int scoreColumnWidth) {
        final String template = "| Place. | UserName | Score |";
        return template
                .replace("Place", "%" + (placeColumnWidth - 1) + "s")
                .replace("UserName", "%-" + userNameColumnWidth + "s")
                .replace("Score", "%" + scoreColumnWidth + "s");
    }

}
