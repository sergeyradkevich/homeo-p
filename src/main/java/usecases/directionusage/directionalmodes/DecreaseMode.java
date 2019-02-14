package usecases.directionusage.directionalmodes;

import usecases.directionusage.Directional;

import java.time.LocalDate;

public class DecreaseMode implements Directional {

    @Override
    public boolean isUsedOn(LocalDate startsOn, LocalDate date) {
        throw new RuntimeException("DecreaseMode not implemented");
    }
}
