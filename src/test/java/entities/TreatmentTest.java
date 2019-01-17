package entities;

import org.junit.Test;
import values.TreatmentPeriod;

import java.time.LocalDate;
import java.time.Month;

import static java.time.temporal.ChronoUnit.MONTHS;
import static org.junit.Assert.assertEquals;

public class TreatmentTest {

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

    //todo: implement setPatient(user);
}