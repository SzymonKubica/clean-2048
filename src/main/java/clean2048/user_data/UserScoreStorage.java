package clean2048.user_data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserScoreStorage {
  public static final String XDG_DATA_HOME = "XDG_DATA_HOME";
  public static final String HOME = "HOME";
  public static final String GAME_DIRECTORY_NAME = "2048";
  public static final String USER_SCORE_STORAGE_FILE_NAME = "user-scores";
  public final Path userScoreStorageFilePath;

  public UserScoreStorage() throws IOException {
    this.userScoreStorageFilePath =
        Paths.get(getGameDataHome().toString(), USER_SCORE_STORAGE_FILE_NAME);
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
    if (!Files.exists(userScoreStorageFilePath)) {
      Files.createFile(userScoreStorageFilePath);
      writeUserData(new HashMap<>());
    }
  }

  public void updateLeaderboard(String username, String password, int score) throws IOException {
    Map<String, User> userMap = readUserData();
    if (userMap.containsKey(username) && score > userMap.get(username).highScore) {
      userMap.get(username).highScore = score;
    } else {
      userMap.put(username, new User(username, password, score));
    }
    writeUserData(userMap);
  }

  public void writeUserData(Map<String, User> users) throws IOException {
    Files.writeString(userScoreStorageFilePath, new Gson().toJson(users));
  }

  public Map<String, User> readUserData() throws FileNotFoundException {
    BufferedReader br =
        new BufferedReader(
            new InputStreamReader(new FileInputStream(userScoreStorageFilePath.toString())));
    return new Gson().fromJson(br, new TypeToken<Map<String, User>>() {}.getType());
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

  public boolean verifyUser(String userName, String password) throws FileNotFoundException {
    Map<String, User> users = readUserData();
    return  !users.containsKey(userName) || Objects.equals(users.get(userName).password, password);
  }
}
