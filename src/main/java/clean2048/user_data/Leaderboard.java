package clean2048.user_data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Leaderboard {
  public static final String XDG_DATA_HOME = "XDG_DATA_HOME";
  public static final String HOME = "HOME";
  public static final String GAME_DIRECTORY_NAME = "2048";
  public static final String LEADERBOARD_FILE_NAME = "leaderboard";
  public final Path leaderboardFilePath;

  public Leaderboard() throws IOException {
    this.leaderboardFilePath = Paths.get(getGameDataHome().toString(), LEADERBOARD_FILE_NAME);
    initialiseDataDir();
    initializeLeaderboardFile();
  }

  public void initialiseDataDir() throws IOException {
    Path gameDataHome = getGameDataHome();
    if (!Files.exists(gameDataHome)) {
      Files.createDirectory(gameDataHome);
    }
  }

  public void initializeLeaderboardFile() throws IOException {
    if (!Files.exists(leaderboardFilePath)) {
      Files.createFile(leaderboardFilePath);
      writeNewLeaderboard(new HashMap<>());
    }
  }

  public void updateLeaderboard(String username, int score) throws IOException {
    Map<String, Integer> leaderboard = readLeaderboard();
    leaderboard.put(username, score);
    writeNewLeaderboard(leaderboard);
  }

  public void writeNewLeaderboard(Map<String, Integer> leaderboard) throws IOException {
    Files.writeString(leaderboardFilePath, new Gson().toJson(leaderboard));
  }

  public Map<String, Integer> readLeaderboard() throws FileNotFoundException {
    BufferedReader br =
        new BufferedReader(
            new InputStreamReader(new FileInputStream(leaderboardFilePath.toString())));
    return new Gson().fromJson(br, new TypeToken<Map<String, Integer>>() {}.getType());
  }

  public static Path getGameDataHome() {
    Map<String, String> environment = System.getenv();

    String dataHome =
        (environment.containsKey(XDG_DATA_HOME))
            ? environment.get(XDG_DATA_HOME)
            : getDefaultDataHome(environment.get(HOME));

    return Path.of("%s/%s".formatted(dataHome, GAME_DIRECTORY_NAME));
  }

  public static String getDefaultDataHome(String userHome) {
    return "%s/.local/share".formatted(userHome);
  }
}
