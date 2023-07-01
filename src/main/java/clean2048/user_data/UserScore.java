package clean2048.user_data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class UserScore implements Comparable<UserScore>{
    public String userName;
    public int score;

    @Override
    public int compareTo(UserScore userScore) {
        return userScore.score - score;
    }
}
