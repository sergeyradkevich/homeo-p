package usecases.directionusage.directionalmodes;

import usecases.directionusage.Directional;

import java.time.LocalDate;

public class DailyMode implements Directional {
    @Override
    public boolean isUsedOn(LocalDate startsOn, LocalDate date) {
        return true;
    }
}
