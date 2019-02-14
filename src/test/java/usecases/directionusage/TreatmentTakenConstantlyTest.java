package usecases.directionusage;

import com.google.inject.Inject;
import entities.DirectionMode;
import entities.Treatment;
import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import testsetup.TreatmentDirectionalModeModule;
import values.TreatmentPeriod;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(TreatmentDirectionalModeModule.class)
public class TreatmentTakenConstantlyTest {

    @Inject
    private GetDirectionalMode modeFactory;

    private GetTreatmentUsageUseCase usageUseCase;

    private static final LocalDate CURRENT_DATE_APR_05 = LocalDate.parse("2018-04-05");
    private static final String MAY_16 = "2018-03-16";

    @Before
    public void setUp() {
        usageUseCase = new GetTreatmentUsageUseCase(modeFactory);
    }

    @Test
    public void isUsedDaily_WhenUsedOnDateBetweenStartAndEndOfTreatment() throws CloneNotSupportedException {
        Treatment t = givenTreatment(MAY_16, 2, MONTHS);

        Treatment t2 = (Treatment) t.clone();
        t2.setStartsOn(CURRENT_DATE_APR_05);

        Treatment t3 = (Treatment) t.clone();
        t3.setStartsOn(CURRENT_DATE_APR_05);

        assertTrue(usageUseCase.isUsedOn(t, CURRENT_DATE_APR_05));
        assertTrue(usageUseCase.isUsedOn(t2, CURRENT_DATE_APR_05));
        assertTrue(usageUseCase.isUsedOn(t3, CURRENT_DATE_APR_05));
    }

    @Test
    public void isNotUsedDaily_WhenUsedOnDateIsAfterEndOfTreatment() {
        Treatment t = givenTreatment(MAY_16, 2, DAYS);

        assertFalse(usageUseCase.isUsedOn(t, CURRENT_DATE_APR_05));
    }

    @Test
    public void isNotUsedDaily_WhenUsedOnDateIsBeforeStartOfTreatment() {
        Treatment t = givenTreatment(MAY_16, 2, DAYS);
        LocalDate date = LocalDate.parse(MAY_16).minusDays(1);

        assertFalse(usageUseCase.isUsedOn(t, date));
    }

    private Treatment givenTreatment(String startDate, Integer periodAmount, ChronoUnit duration) {
        Treatment result = new Treatment();

        LocalDate startsOn = LocalDate.parse(startDate);
        result.setStartsOn(startsOn);

        TreatmentPeriod period = new TreatmentPeriod(
                Integer.parseInt(periodAmount.toString()), duration
        );

        result.setPeriod(period);
        result.setStopsOn(period.calcEnd(startsOn));

        result.setDirectionMode(new DirectionMode());

        return result;
    }
}
