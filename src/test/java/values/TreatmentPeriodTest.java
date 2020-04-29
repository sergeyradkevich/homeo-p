package values;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TreatmentPeriodTest {
    private TreatmentPeriod fiveDays;

    //todo: What if the end of treatment is based on prescribed dosage and a certain duration is not given?
    //todo: Can it be like duration-based (temporal) and dosage-based?
    //todo: For dosage-based duration the total dosage for a whole treatment period should be known to calc end of the treatment
    // The difference can be done via Ending interface that implements TreatmentPeriod and Dosage classes

    @Before
    public void setUp() throws Exception {
        fiveDays = new TreatmentPeriod(5, ChronoUnit.DAYS);
    }

    @Test
    public void givenStartDateCalculatesTheEndOfTreatment_IncludingTheFirstDay() {
        TreatmentPeriod p;
        LocalDate startsOn = LocalDate.of(2017, Month.MARCH, 16);

        p = new TreatmentPeriod(10, DAYS);
        assertEquals(LocalDate.of(2017, Month.MARCH, 25), p.calcEnd(startsOn));

        p = new TreatmentPeriod(60, DAYS);
        assertEquals(LocalDate.of(2017, Month.MAY, 14), p.calcEnd(startsOn));

        p = new TreatmentPeriod(1, MONTHS);
        assertEquals(LocalDate.of(2017, Month.APRIL, 15), p.calcEnd(startsOn));

        p = new TreatmentPeriod(6, MONTHS);
        assertEquals(LocalDate.of(2017, Month.SEPTEMBER, 15), p.calcEnd(startsOn));

        p = new TreatmentPeriod(1, YEARS);
        assertEquals(LocalDate.of(2018, Month.MARCH, 15), p.calcEnd(startsOn));

        p = new TreatmentPeriod(3, YEARS);
        assertEquals(LocalDate.of(2020, Month.MARCH, 15), p.calcEnd(startsOn));
    }

    @Test
    public void periodsWithTheSameAmountAndUnitAreEqual() {
        assertTrue(fiveDays.equals(new TreatmentPeriod(5, DAYS)));

        assertFalse(fiveDays.equals(new TreatmentPeriod(3, DAYS)));
        assertFalse(fiveDays.equals(new TreatmentPeriod(5, MONTHS)));
    }

    @Test
    public void neverEqualsToEmptyPeriod() {
        assertFalse(fiveDays.equals(null));
    }

    @Test
    public void equalPeriodIsNotLonger() {
        assertFalse(fiveDays.isLonger(new TreatmentPeriod(5, DAYS)));
    }

    @Test
    public void periodWithGreaterDurationIsLonger() {
        TreatmentPeriod sixDays = new TreatmentPeriod(6, DAYS);
        assertTrue(sixDays.isLonger(fiveDays));


        TreatmentPeriod oneMonth = new TreatmentPeriod(1, MONTHS);
        assertTrue(oneMonth.isLonger(fiveDays));

        TreatmentPeriod threeYears = new TreatmentPeriod(3, YEARS);
        assertTrue(threeYears.isLonger(fiveDays));


        TreatmentPeriod forever = new TreatmentPeriod(1, FOREVER);
        assertFalse(threeYears.isLonger(forever));
    }

    @Test
    public void returnsSelfIfDurationIsNotLonger() {
        assertTrue(fiveDays == fiveDays.extendIfDurationLonger(3));
    }

    @Test
    public void returnsExtendedPeriodIfDurationIsLonger() {
        TreatmentPeriod sevenDays = fiveDays.extendIfDurationLonger(7);
        assertFalse(fiveDays == sevenDays);
        assertTrue(sevenDays.isLonger(fiveDays));
    }

}
