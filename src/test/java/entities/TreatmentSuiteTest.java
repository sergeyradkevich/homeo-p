package entities;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TreatmentTest.class,
        TreatmentTakenConstantlyTest.class,
        TreatmentTakenPeriodicallyTest.class
})

public class TreatmentSuiteTest {
}
