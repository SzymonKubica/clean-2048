package Game2048.view;

public enum Color {
    WHITE(2),
    LIGHT_CYAN(4),
    CYAN(8),
    LIGHT_BLUE(16),
    LIGHT_GREEN(32),
    GREEN(64),
    LIGHT_YELLOW(128),
    YELLOW(256),
    LIGHT_RED(512),
    RED(1024),
    MAGENTA(2048),
    GREY(0);

    private final int value;
    Color(int value) {
        this.value = value;
    }

    public static Color getTileColor(int tileValue) {
        Color color = GREY;
        for (Color c : Color.values()) {
            if (c.value == tileValue) {
                color = c;
            }
        }
        return color;
    }
}
