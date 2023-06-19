package clean2048.controller;

import java.util.Optional;

public interface GameController {
    Optional<Direction> getMove();
}
