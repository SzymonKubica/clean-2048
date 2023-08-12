package clean2048.view;

import clean2048.lib.lanterna.LanternaTerminal;
import clean2048.user_data.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/*
Responsible for prompting the user to enter their username and password so that
they can edit the leaderboard.
*/
public class LoginView {
  private static final int MAX_LOGIN_ATTEMPTS = 3;
  private final LanternaTerminal terminal;
  private final Map<String, User> existingUsers;

  public LoginView(LanternaTerminal terminal, Map<String, User> existingUsers) {
    this.terminal = terminal;
    this.existingUsers = existingUsers;
  }

  public Optional<User> login() throws IOException {
    String username = promptForUsername();
    while (!existingUsers.containsKey(username)) {
      terminal.printLineCentered(
          "User %s doesn't exist, please try a different username. Available users: %s"
              .formatted(
                  username, existingUsers.keySet().stream().collect(Collectors.joining("\n"))));
      username = promptForUsername();
    }
    return loginAs(username);
  }

  public Optional<User> loginAs(String username) throws IOException {
    assert existingUsers.containsKey(username) : "User %s doesn't exist".formatted(username);

    User user = existingUsers.get(username);

    int attempts = 0;
    String password = promptForPassword();
    while (!user.password.equals(password) && attempts < MAX_LOGIN_ATTEMPTS) {
      attempts++;
      if (attempts == MAX_LOGIN_ATTEMPTS) {
        return Optional.empty();
      }
      terminal.printLineCentered("Incorrect password, please try again.");
      password = promptForPassword();
    }
    assert user.password.equals(password) && attempts < MAX_LOGIN_ATTEMPTS;
    return Optional.of(user);
  }

  public String promptForNewUserName() throws IOException {
    return readInput("Please enter your user name: ", null);
  }

  public String promptForUsername() throws IOException {
    return readInput("Please enter your user name: ", null);
  }

  public String promptForPassword() throws IOException {
    return readInput("Enter your password: ", '*');
  }

  private String readInput(String promptMessage, Character feedbackChar) throws IOException {
    terminal.clearScreen();
    terminal.printStringCentered(promptMessage);
    char input = terminal.readCharacter();
    List<Character> inputBuffer = new ArrayList<>();
    while (!inputConfirmed(input)) {
      if (backspaceWhenInputEmpty(input, inputBuffer)) {
        input = terminal.readCharacter();
        continue;
      }
      if (isBackspace(input)) {
        terminal.clearScreen();
        terminal.printStringCentered(promptMessage);
        inputBuffer.remove(inputBuffer.size() - 1);
        terminal.printString(
            inputBuffer.stream().map(String::valueOf).collect(Collectors.joining("")));
        input = terminal.readCharacter();
        continue;
      }

      char feedback = feedbackChar != null ? feedbackChar : input;
      terminal.printCharacter(feedback);
      inputBuffer.add(input);
      input = terminal.readCharacter();
    }
    terminal.printNewLine();
    return inputBuffer.stream().map(String::valueOf).collect(Collectors.joining(""));
  }

  private boolean backspaceWhenInputEmpty(char input, List<Character> inputBuffer) {
    return inputBuffer.isEmpty() && isBackspace(input);
  }

  private boolean isBackspace(char input) {
    return input == '\b';
  }

  private boolean inputConfirmed(char input) {
    return input != '\n';
  }
}
