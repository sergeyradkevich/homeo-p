package entities;

import java.util.Objects;

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

    public static DirectionModeType of(String type) {
        if (Objects.isNull(type))
            return byDefault();

        return valueOf(type);
    }
}