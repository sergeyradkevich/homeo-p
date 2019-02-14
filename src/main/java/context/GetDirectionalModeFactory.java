package context;

import entities.DirectionMode;
import usecases.directionusage.Directional;
import usecases.directionusage.GetDirectionalMode;
import usecases.directionusage.directionalmodes.*;

public class GetDirectionalModeFactory implements GetDirectionalMode {

    @Override
    public Directional getMode(DirectionMode mode) {
        if (mode.isDaily())
            return new DailyMode();
        if (mode.isPeriodically())
            return new PeriodicalMode(mode.getTaken(), mode.getInterval());

        return new DecreaseMode();
    }
}
