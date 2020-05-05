package usecases.directionusage;

import com.google.inject.Inject;
import entities.DirectionMode;
import entities.DirectionModeType;
import entities.Treatment;
import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import testsetup.TreatmentDirectionalModeModule;
import values.TreatmentPeriod;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(TreatmentDirectionalModeModule.class)
public class TreatmentTakenDecreasingTest {

    private static final LocalDate CURRENT_DATE_JUN_10 = LocalDate.parse("2020-06-10");
    private static final String MAY_01 = "2020-05-01";

    @Inject
    private GetDirectionalMode modeFactory;
    GetTreatmentUsageUseCase usageUseCase;
    private Treatment t;

    @Before
    public void setUp() {
        usageUseCase = new GetTreatmentUsageUseCase(modeFactory);
        t = givenDecreaseTreatment(MAY_01, 2, ChronoUnit.MONTHS);
    }

    @Test
    public void isUsed_WhenUsedOnDateWithinStartAndEndOfTreatmentInclusive() {
        assertTrue(usageUseCase.isUsedOn(t, t.getStartsOn()));
        assertTrue(usageUseCase.isUsedOn(t, CURRENT_DATE_JUN_10));
        assertTrue(usageUseCase.isUsedOn(t, t.getStopsOn()));
    }

    @Test
    public void isNotUsed_WhenUsedBeforeStartOfTreatment() {
        LocalDate dayBefore = t.getStartsOn().minusDays(1);

        assertFalse(usageUseCase.isUsedOn(t, dayBefore));
    }

    @Test
    public void isNotUsed_WhenUsedAfterEndOfTreatment() {
        LocalDate dayAfter = t.getStopsOn().plusDays(1);

        assertFalse(usageUseCase.isUsedOn(t, dayAfter));
    }

    private Treatment givenDecreaseTreatment(String startDate, Integer periodAmount, ChronoUnit duration) {
        Treatment result = new Treatment();

        LocalDate startsOn = LocalDate.parse(startDate);
        result.setStartsOn(startsOn);

        TreatmentPeriod period = new TreatmentPeriod(
                Integer.parseInt(periodAmount.toString()), duration
        );

        result.setPeriod(period);
        result.setStopsOn(period.calcEnd(startsOn));

        DirectionMode mode = new DirectionMode(DirectionModeType.DECREASINGLY);
        mode.setDelta(1);
        mode.setLimit(2);
        result.setDirectionMode(mode);

        return result;
    }
}


/*

    Vocara:
    9 drops, 7 times a day for 1 month (for example, from 15 Apr to 14 May)
    decreasing 1 intake daily until 2 times.
    9 drops 7 times a day, then 6, 5, 4, 3, 2, 2 ... 2.
    Usage is the same as for daily


    DecreaseMode
    delta
    limit

    The source amount for subtraction a delta is Dosage.dailyIntakeAmount (int)
    Criteria for delta:
    - 1 by default
    - cannot be greater than input dailyIntakeAmount
    - interface to increase delta instead of passing/setting concrete value?

    Criteria for limit:
    - 1 by default
    - cannot be less than 1 and greater than dailyIntakeAmount
    - is there any schema to reach the limit by decreasing dailyIntakeAmount by delta, like
    7 minus 3 => 4, but limit is 3. Does it mean that the next decrease by 3 should yield 3 in result?

    General criteria:
    - Can be not just “-1” (or “-2”) decreasing, but some schema like 9, 7, 5, 3
    decreasing mode may switch to periodical
    - decreasing mode may switch to periodical, for example "Then 9 drops, 2 times a day 3 days is taken, then a pause for 3 days"
*/