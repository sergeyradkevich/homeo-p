package usecases.directionusage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TreatmentTakenConstantlyTest.class,
        TreatmentTakenPeriodicallyTest.class,
        TreatmentTakenDecreasingTest.class
})

public class TreatmentDirectionUsageSuiteTest {
}
