package entities;

public enum DirectionModeType {
    CONSTANTLY,
    PERIODICALLY,
    DECREASINGLY;

    public static DirectionModeType byDefault() {
        return CONSTANTLY;
    }

    public static boolean isExistent(String name) {
        try {
            valueOf(name);
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }
}