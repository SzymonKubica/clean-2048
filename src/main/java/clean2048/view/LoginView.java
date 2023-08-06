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

    public LoginView(LanternaTerminal terminal) {
        this.terminal = terminal;
    }

    public Optional<User> login() throws IOException {
        String username = promptForUserName();
        while (!existingUsers.containsKey(username)) {
            terminal.printLineCentered("User %s doesn't exist, please try a different username. Available users: %s".formatted(username, existingUsers.keySet().stream().collect(Collectors.joining("\n"))));
            username = promptForUserName();
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


    private String promptForUserName() throws IOException {
        return readInput("Please enter your user name: ", null);
    }

    private String promptForPassword() throws IOException {
        return readInput("Enter your password: ", '*');
    }

    private String readInput(String promptMessage, Character feedbackChar) throws IOException {
        terminal.printStringCentered(promptMessage);
        char input = terminal.readCharacter();
        List<Character> userInput = new ArrayList<>();
        while (input != '\n') {
            if (userInput.isEmpty() && input == '\b') {
                input = terminal.readCharacter();
                continue;
            }
            if (input == '\b') {
                userInput.remove(userInput.size() - 1);
                terminal.clearScreen();
                updateDisplay(score, grid);
                printGameOverMessage();
                terminal.printStringCentered(promptMessage);
                terminal.printString(
                        userInput.stream().map(String::valueOf).collect(Collectors.joining("")));
                input = terminal.readCharacter();
                continue;
            }
            if (feedbackChar != null) {
                terminal.printCharacter(feedbackChar);
            } else {
                terminal.printCharacter(input);
            }
            userInput.add(input);
            input = terminal.readCharacter();
        }
        terminal.printLine("");
        return userInput.stream().map(String::valueOf).collect(Collectors.joining(""));
    }

}
