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
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;
import static org.junit.Assert.*;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(TreatmentDirectionalModeModule.class)
public class TreatmentTakenPeriodicallyTest {

    @Inject
    private GetDirectionalMode modeFactory;

    private GetTreatmentUsageUseCase usageUseCase;

    @Before
    public void setUp() {
        usageUseCase = new GetTreatmentUsageUseCase(modeFactory);
    }

    @Test
    public void isUsedOnTheFirstDay_OfTheFirstTakenPeriod() {
        LocalDate date = LocalDate.now(), startsOn = LocalDate.now();

        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 10, DAYS);

        assertTrue(usageUseCase.isUsedOn(t, date));
    }

    @Test
    public void isUsedOnTheNextDay_OfTheFirstTakenPeriod() {
        LocalDate startsOn = LocalDate.now(), date = startsOn.plusDays(1);

        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 10, DAYS);

        assertTrue(usageUseCase.isUsedOn(t, date));
    }

    @Test
    public void isUsedOnTheLastDay_OfTheFirstTakenPeriod() {
        LocalDate startsOn = LocalDate.now(), date = startsOn.plusDays(2);

        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 10, DAYS);

        assertTrue(usageUseCase.isUsedOn(t, date));
    }

    @Test
    public void isNotUsedOnTheFirstDayOfInterval_OfTheFirstTakenPeriod() {
        LocalDate startsOn = LocalDate.now(), date = startsOn.plusDays(3);

        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 10, DAYS);

        assertFalse(usageUseCase.isUsedOn(t, date));
    }

    @Test
    public void isNotUsedOnTheLastDayOfInterval__OfTheFirstTakenPeriod() {
        LocalDate startsOn = LocalDate.now(), date = startsOn.plusDays(4);

        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 10, DAYS);

        assertFalse(usageUseCase.isUsedOn(t, date));
    }

    @Test
    public void isUsedOnTheFirstDay_OfTheSecondTakenPeriod() {
        LocalDate startsOn = LocalDate.now(), date = startsOn.plusDays(5);
        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 10, DAYS);

        assertTrue(usageUseCase.isUsedOn(t, date));
    }

    @Test
    public void isUsedOnTheTakenDays_OfTheSecondTakenPeriod() {
        LocalDate startsOn = LocalDate.now();
        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 10, DAYS);

        IntStream.rangeClosed(5, 7).forEach((day) -> {
            LocalDate date = startsOn.plusDays(day);
            assertTrue(String.format("Should be used on %d day since the start", day),
                    usageUseCase.isUsedOn(t, date));
        });
    }


    @Test
    public void isNotUsedOnIntervalDays_OfTheSecondTakenPeriod() {
        LocalDate startsOn = LocalDate.now();
        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 10, DAYS);

        IntStream.rangeClosed(8, 9).forEach((day) -> {
            LocalDate date = startsOn.plusDays(day);
            assertFalse(String.format("Should not be used on %d day since the start", day),
                    usageUseCase.isUsedOn(t, date));
        });
    }


    /*
        20 Mar 2018 - the last day of the pause of a one-year treatment, so should't be used on that day
        21 Mar 2018 - the first day of usage since a year, so should be used on that day
    */
    @Test
    public void edgeUsagesSinceOneYear() {
        LocalDate startsOn = LocalDate.of(2017, Month.MARCH, 21);

        Treatment t = givenPeriodicalTreatment(startsOn.toString(), 2, YEARS);

        LocalDate theLastDayInTheYearOfThePauseWhenNotUsed = LocalDate.of(2018, Month.MARCH, 20);
        boolean result = usageUseCase.isUsedOn(t, theLastDayInTheYearOfThePauseWhenNotUsed);
        String message = String.format(
                "StartsOn: '%s', On the last day of a one year-treatment when shouldn't be used: '%s', isUsed: '%s'",
                startsOn, theLastDayInTheYearOfThePauseWhenNotUsed, result);
        assertFalse(message, result);

        LocalDate daySinceYearWhenIsUsed = LocalDate.of(2018, Month.MARCH, 21);
        result = usageUseCase.isUsedOn(t, daySinceYearWhenIsUsed);
        assertTrue(String.format("StartsOn: '%s', Day since year when should be used: '%s', isUsed: '%s'",
                startsOn, daySinceYearWhenIsUsed, result), result);

    }

    private Treatment givenPeriodicalTreatment(String startDate, Integer periodAmount, ChronoUnit duration) {
        Treatment result = new Treatment();

        LocalDate startsOn = LocalDate.parse(startDate);
        result.setStartsOn(startsOn);

        TreatmentPeriod period = new TreatmentPeriod(
                Integer.parseInt(periodAmount.toString()), duration
        );

        result.setPeriod(period);
        result.setStopsOn(period.calcEnd(startsOn));

        DirectionMode mode = new DirectionMode(DirectionModeType.PERIODICALLY);
        mode.setTaken(3);
        mode.setInterval(2);

        result.setDirectionMode(mode);

        return result;
    }
}