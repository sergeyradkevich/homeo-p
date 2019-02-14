package usecases.directionusage;

import java.time.LocalDate;

public interface Directional {
    boolean isUsedOn(LocalDate startsOn, LocalDate date);
}
