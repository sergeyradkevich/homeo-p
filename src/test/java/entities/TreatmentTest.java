package entities;

import org.junit.Test;
import values.TreatmentPeriod;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TreatmentTest {

    private static final LocalDate CURRENT_DATE_APR_05 = LocalDate.parse("2018-04-05");
    private static final String MAY_16 = "2018-03-16";

    @Test
    public void creation() {
        LocalDate startDate = LocalDate.of(2017, Month.MARCH, 16);
        TreatmentPeriod period = new TreatmentPeriod(3, MONTHS);

        Treatment t = new Treatment();
        t.setId("tr#1");

        t.setDrug(new Drug());
        t.setDosage(new Dosage());

        t.setStartsOn(startDate);
        t.setPeriod(period);
        t.setStopsOn(period.calcEnd(startDate));

        assertEquals(LocalDate.of(2017, Month.JUNE, 15), t.getStopsOn());
    }

    @Test
    public void treatmentTakenConstantlyIsUsedDaily_WhenUsedOnDateBetweenStartAndEndOfTreatment() throws CloneNotSupportedException {
        Treatment t = givenTreatment(MAY_16, 2, MONTHS);

        Treatment t2 = (Treatment) t.clone();
        t2.setStartsOn(CURRENT_DATE_APR_05);

        Treatment t3 = (Treatment) t.clone();
        t3.setStartsOn(CURRENT_DATE_APR_05);

        assertTrue(t.isUsedOn(CURRENT_DATE_APR_05));
        assertTrue(t2.isUsedOn(CURRENT_DATE_APR_05));
        assertTrue(t3.isUsedOn(CURRENT_DATE_APR_05));
    }

    @Test
    public void treatmentTakenConstantlyIsNotUsedDaily_WhenUsedOnDateIsAfterEndOfTreatment() {
        Treatment t = givenTreatment(MAY_16, 2, DAYS);

        assertFalse(t.isUsedOn(CURRENT_DATE_APR_05));
    }

    @Test
    public void treatmentTakenConstantlyIsNotUsedDaily_WhenUsedOnDateIsBeforeStartOfTreatment() {
        Treatment t = givenTreatment(MAY_16, 2, DAYS);
        LocalDate date = LocalDate.parse(MAY_16).minusDays(1);

        assertFalse(t.isUsedOn(date));
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

        return result;
    }
    //todo: implement setPatient(user);
}