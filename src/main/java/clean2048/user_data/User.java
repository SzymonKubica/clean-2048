package clean2048.user_data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class User implements Comparable<User> {
  public String userName;
  public String password;
  public int highScore;

  @Override
  public int compareTo(User user) {
    return user.highScore - highScore;
  }
}
