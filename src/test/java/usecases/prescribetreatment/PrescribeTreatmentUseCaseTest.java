package usecases.prescribetreatment;

import com.google.inject.Inject;
import entities.*;
import net.lamberto.junit.GuiceJUnitRunner;
import net.lamberto.junit.GuiceJUnitRunner.GuiceModules;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import testsetup.*;
import usecases.DosageGateway;
import usecases.DrugGateway;
import usecases.TreatmentGateway;
import usecases.UseCaseValidator;
import values.TreatmentPeriod;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules(CommonTestModule.class)
public class PrescribeTreatmentUseCaseTest {

    @Inject
    private TreatmentGateway treatmentGateway;
    @Inject
    private DrugGateway drugGateway;
    @Inject
    private DosageGateway dosageGateway;
    private UseCaseValidator validator = new PrescribeTreatmentValidator();

    private PrescribeTreatmentUseCase prescribeTreatmentUseCase;

    private PrescribeTreatmentRequest request;

    private Drug drug, anotherDrug;
    private Dosage dosage;

    @Before
    public void setUp() {
        prescribeTreatmentUseCase = new PrescribeTreatmentUseCase(
                treatmentGateway, drugGateway, dosageGateway, validator);

        if (Objects.isNull(drug)) {
            drug = new Drug("Arsen Alb");
            drugGateway.save(drug);
        }

        if (Objects.isNull(anotherDrug)) {
            anotherDrug = new Drug("Vocara");
            drugGateway.save(anotherDrug);
        }

        if (Objects.isNull(dosage)) {
            dosage = new Dosage();
            dosageGateway.save(dosage);
        }

        setUpPrescribeTreatmentRequest();
    }

    @Test
    public void shouldYieldPersistedTreatment() {
        Treatment t = prescribeTreatmentUseCase.prescribe(request);

        assertNotNull(t.getId());
        assertNotEquals("", t.getId());
    }

    @Test
    public void assignsDrugAndDurationAttributes() {
        Treatment t = prescribeTreatmentUseCase.prescribe(request);

        assertEquals(LocalDate.of(2017, Month.MARCH, 16), t.getStartsOn());
        assertEquals(LocalDate.of(2017, Month.APRIL, 15), t.getStopsOn());
        assertEquals(new TreatmentPeriod(1, ChronoUnit.MONTHS), t.getPeriod());

        assertEquals(drug, t.getDrug());
        assertEquals(dosage, t.getDosage());
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void zeroTreatmentPeriodIsNotAllowed() {
        request.addPeriodAmount("0");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void negativeTreatmentPeriodIsNotAllowed() {
        request.addPeriodAmount("-1");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void treatmentPeriodFormatToBeAnInteger() {
        request.addPeriodAmount("one");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_StartOfTreatmentShouldBePresent() {
        PrescribeTreatmentRequest request = new PrescribeTreatmentRequest()
                .addPeriodAmount("1")
                .addPeriodUnit("Months")
                .addDrugId(drug.getId())
                .addDosageId(dosage.getId());

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_startOfTreatmentShouldNotBeEmpty() {
        request.addStartDate("");

        prescribeTreatmentUseCase.prescribe(request);
    }

    // TODO: + ShouldNotBeEmpty
    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_amountOfTreatmentPeriodShouldBePresent() {
        request.addPeriodAmount(null);

        prescribeTreatmentUseCase.prescribe(request);
    }

    // TODO: + ShouldNotBeEmpty
    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_unitOfTreatmentPeriodShouldBePresent() {
        request.addPeriodUnit(null);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugIdShouldBePresent() {
        PrescribeTreatmentRequest request = new PrescribeTreatmentRequest()
                .addStartDate("2017-03-16")
                .addPeriodAmount("1")
                .addPeriodUnit("Months")
                .addDosageId(dosage.getId());

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugIdShouldNotBeEmpty() {
        request.addDrugId("");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugIdShouldExist() {
        request.addDrugId("nonExistingDrugId");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugDosageIdShouldBePresent() {
        PrescribeTreatmentRequest request = new PrescribeTreatmentRequest()
                .addStartDate("2017-03-16")
                .addPeriodAmount("1")
                .addPeriodUnit("Months")
                .addDrugId(drug.getId());

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugDosageIdShouldNotBeEmpty() {
        request.addDosageId("");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugDosageShouldExist() {
        request.addDosageId("nonExistingDosageId");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_directionModeTypeShouldBePresent() {
        request.addDirectionMode(givenEmptyPeriodicalParameters());

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_directionModeTypeShouldBeWithinValidValues() {
        request.addDirectionMode(givenNonexistentDirectionType());

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_takenAmountShouldBePresentForPeriodicalModeType() {
        Map<String, String> parameters = givenPeriodicalModeWithoutTakenAndIntervalParams();
        request.addDirectionMode(parameters);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_takenAmountOfPeriodicalModeShouldBeNumeric() {
        Map<String, String> parameters = givenPeriodicalParameters();
        parameters.put("directionModeTaken", "tree");
        request.addDirectionMode(parameters);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_takenAmountOfPeriodicalModeShouldBePositiveNumber() {
        Map<String, String> parameters = givenPeriodicalParameters();
        parameters.put("directionModeTaken", "-3");
        request.addDirectionMode(parameters);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_takenAmountOfPeriodicalModeShouldBeNonZero() {
        Map<String, String> parameters = givenPeriodicalParameters();
        parameters.put("directionModeTaken", "0");
        request.addDirectionMode(parameters);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_intervalAmountShouldBePresentForPeriodicalModeType() {
        Map<String, String> parameters = givenPeriodicalModeWithoutIntervalParam();
        request.addDirectionMode(parameters);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_intervalAmountOfPeriodicalModeShouldBeNumeric() {
        Map<String, String> parameters = givenPeriodicalParameters();
        parameters.put("directionModeInterval", "two");
        request.addDirectionMode(parameters);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_intervalAmountOfPeriodicalModeShouldBePositiveNumber() {
        Map<String, String> parameters = givenPeriodicalParameters();
        parameters.put("directionModeInterval", "-2");
        request.addDirectionMode(parameters);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_intervalAmountOfPeriodicalModeShouldBeNonZero() {
        Map<String, String> parameters = givenPeriodicalParameters();
        parameters.put("directionModeInterval", "0");
        request.addDirectionMode(parameters);

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test
    public void treatmentCanStartInTheFuture() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        PrescribeTreatmentRequest request = new PrescribeTreatmentRequest()
                .addStartDate(tomorrow.toString())
                .addPeriodAmount("2")
                .addPeriodUnit("Days")
                .addDrugId(drug.getId())
                .addDosageId(dosage.getId())
                .addDirectionMode(givenDailyModeParameters());

        Treatment t = prescribeTreatmentUseCase.prescribe(request);

        assertEquals(tomorrow, t.getStartsOn());
        assertEquals(tomorrow.plusDays(1), t.getStopsOn());
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void treatmentStartDateMatches_yyyy_MM_dd_Format() {
        request.addStartDate("2017 03 16");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_sameStartDateOfTreatmentPeriod() {
        prescribeTreatmentUseCase.prescribe(request);
        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_sameEndDateOfTreatmentPeriod() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-04-14");
        request.addPeriodAmount("2");
        request.addPeriodUnit("Days");

        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      (s) after .(s) but before .(e)
                 .(s)----------.(e)
                    .(s)----------.(e)
    */
    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_whenNewStartDateIsAfterExisting() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-03-17");
        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      (s) after .(s) but before .(e)
                 .(s)----------.(e)
                    .(s)----.(e)
    */
    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_whenNewStartDateIsAfterExistingStartAndEndDateIsBeforeExistingEnd() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-03-17");
        request.addPeriodAmount("2");
        request.addPeriodUnit("Days");

        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      (s) after .(s) but before .(e)
                 .(s)----------.(e)
                    .(s)------------.(e)
    */
    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_whenNewStartDateIsAfterExistingStartAndEndDateIsAfterExistingEnd() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-03-17");
        request.addPeriodAmount("2");
        request.addPeriodUnit("Months");

        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      when (s) after .(e)
                 .(s)----------.(e)
                                    .(s)----------.(e)
    */
    @Test
    public void noOverlap_whenNewTreatmentStartsAfterEndOfExistingTreatment() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-04-17");
        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      when (e) before .(s)
                 .(s)----------.(e)
      .(s)--.(e)
    */
    @Test
    public void noOverlap_whenTreatmentEndsBeforeStartOfTreatment() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-03-14");
        request.addPeriodAmount("1");
        request.addPeriodUnit("Days");

        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      (s) before .(s) but (e) after .(s)
                 .(s)----------.(e)
        .(s)-------------.(e)
    */
    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_whenTreatmentStartsBeforeStartButEndsAfterStart() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-03-14");

        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      (s) after .(s) but before .(e)
             .(s)----------.(e)
                           .(s)----.(e)
    */
    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_whenNewStartsOnDateWhenExistingEnds() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-04-15");
        request.addPeriodAmount("2");
        request.addPeriodUnit("Days");

        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      (s) after .(s) but before .(e)
             .(s)----------.(e)
      .(s)---.(e)
   */
    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_whenNewEndsOnDateWhenExistingStarts() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-03-15");
        request.addPeriodAmount("2");
        request.addPeriodUnit("Days");

        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      (s) after .(s) but before .(e)
             .(s)----------.(e)
      .(s)-----------------.(e)
   */
    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_whenNewEndsOnDateWhenExistingEnds() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-03-15");
        request.addPeriodAmount("32");
        request.addPeriodUnit("Days");

        prescribeTreatmentUseCase.prescribe(request);
    }

    /*
      (s) after .(s) but before .(e)
             .(s)----------.(e)
      .(s)----------------------.(e)
   */
    @Test(expected = PrescribeTreatmentException.class)
    public void overlapIsNotAllowed_whenNewStartsBeforeExistingStatAndEndsAfterExistingEnds() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addStartDate("2017-03-15");
        request.addPeriodAmount("60");
        request.addPeriodUnit("Days");

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test
    public void noOverlap_whenPeriodIsSameButDrugsAreDifferent() {
        prescribeTreatmentUseCase.prescribe(request);

        request.addDrugId(anotherDrug.getId());

        prescribeTreatmentUseCase.prescribe(request);
    }


    @Test
    public void treatmentDirectionModeIsDailyByDefault() {
        Treatment t = prescribeTreatmentUseCase.prescribe(request);

        assertTrue(t.getDirectionMode().isDaily());
    }

    @Test
    public void treatmentDirectionModeIsPeriodical() {
        request.addDirectionMode(givenPeriodicalParameters());

        Treatment t = prescribeTreatmentUseCase.prescribe(request);

        assertTrue(t.getDirectionMode().isPeriodically());
    }

    @Test
    public void periodicalModeSetCorrectly() {
        request.addDirectionMode(givenPeriodicalParameters());

        Treatment t = prescribeTreatmentUseCase.prescribe(request);
        DirectionMode m = t.getDirectionMode();

        assertEquals(3, m.getTaken());
        assertEquals(2, m.getInterval());
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void decreaseModeDeltaShouldBeNumerical() {
        request.addDirectionMode(givenDecreaseModeWithInvalidDelta());

        prescribeTreatmentUseCase.prescribe(request);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void decreaseModeLimitShouldBeNumerical() {
        request.addDirectionMode(givenDecreaseModeWithInvalidLimit());

        prescribeTreatmentUseCase.prescribe(request);
    }

    // ToDo Tests on decrease mode delta & limit validations
    // see TreatmentTakenDecreasingTes

    @Test
    public void decreaseModeSetCorrectly() {
        request.addDirectionMode(givenDecreaseModeParameters1IntakeUntil2Times());

        Treatment t = prescribeTreatmentUseCase.prescribe(request);
        DirectionMode m = t.getDirectionMode();

        assertEquals(1, m.getDelta());
        assertEquals(2, m.getLimit());
    }

    /*
        End date of a treatment should not be before the date
        when the limit of daily intake amount is came,
        thus the duration of treatment should be prolonged
        so that it is equal or longer than "the date of the limit".
    */
    @Test
    public void shouldProlongTreatmentDuration_OfDecreasingDirectionMode_IfLimitComes_AfterTheSpecifiedDuration() {
        PrescribeTreatmentRequest r = givenDecreasingDirectionModeRequest();

        Treatment t = prescribeTreatmentUseCase.prescribe(r);

        assertEquals("Should not be equal to original/passed period, but to the prolonged one",
                LocalDate.of(2018, Month.MARCH, 13), t.getStopsOn());
        assertEquals(new TreatmentPeriod(6, ChronoUnit.DAYS), t.getPeriod());
    }

    private void setUpPrescribeTreatmentRequest() {
        // it ends on 2017-04-15
        request = new PrescribeTreatmentRequest()
            .addStartDate("2017-03-16")
            .addPeriodAmount("1")
            .addPeriodUnit("Months")
            .addDrugId(drug.getId())
            .addDosageId(dosage.getId())
            .addDirectionMode(givenDailyModeParameters());
    }

    private Map<String, String> givenDailyModeParameters() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", DirectionModeType.CONSTANTLY.name());

        return modeOptions;
    }

    private Map<String, String> givenPeriodicalParameters() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", DirectionModeType.PERIODICALLY.name());
        modeOptions.put("directionModeTaken", "3");
        modeOptions.put("directionModeInterval", "2");

        return modeOptions;
    }

    private Map<String, String> givenEmptyPeriodicalParameters() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", "");

        return modeOptions;
    }

    private Map<String, String> givenNonexistentDirectionType() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", "Nonexistent Direction Type");

        return modeOptions;
    }

    private Map<String, String> givenPeriodicalModeWithoutTakenAndIntervalParams() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", DirectionModeType.PERIODICALLY.name());

        return modeOptions;
    }

    private Map<String, String> givenPeriodicalModeWithoutIntervalParam() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", DirectionModeType.PERIODICALLY.name());
        modeOptions.put("directionModeTaken", "3");

        return modeOptions;
    }


    private Map<String,String> givenDecreaseModeParameters1IntakeUntil2Times() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", DirectionModeType.DECREASINGLY.name());
        modeOptions.put("directionModeDelta", "1");
        modeOptions.put("directionModeLimit", "2");

        return modeOptions;
    }

    private Map<String,String> givenDecreaseModeWithInvalidDelta() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", DirectionModeType.DECREASINGLY.name());
        modeOptions.put("directionModeDelta", "one");
        modeOptions.put("directionModeLimit", "2");

        return modeOptions;
    }

    private Map<String,String> givenDecreaseModeWithInvalidLimit() {
        Map<String, String> modeOptions = new HashMap<>();
        modeOptions.put("directionModeType", DirectionModeType.DECREASINGLY.name());
        modeOptions.put("directionModeDelta", "1");
        modeOptions.put("directionModeLimit", "two");

        return modeOptions;
    }

    /*
        9 drops, 7 times a day, decreasing 1 intake daily until 2 times
     */
    private PrescribeTreatmentRequest givenDecreasingDirectionModeRequest() {
        Drug vocara = new Drug("Vocara");
        drugGateway.save(vocara);

        Dose dose = new Dose();
        dose.setQuantity(9);
        dose.setForm("Drops");

        Dosage d = new Dosage();
        d.setDailyIntakeAmount(7);
        d.setDose(dose);

        dosageGateway.save(d);

        return new PrescribeTreatmentRequest()
                    .addStartDate("2018-03-08")
                    .addPeriodAmount("3")
                    .addPeriodUnit("Days")
                    .addDrugId(vocara.getId())
                    .addDosageId(d.getId())
                    .addDirectionMode(givenDecreaseModeParameters1IntakeUntil2Times());
    }

    // todo: spaces for String parametes
    // todo: PeriodUnit - valid units like Months, Days and so on

    // todo: print date pattern in exception message when input date is malformed

    // todo: should the result be Treatment or respond model/object?

    // todo: is it possible that created treatment's start date is after end date?

    //todo: Treatment period long vs int - to accept Long.MAX_VALUE
    // acceptsLongAmountOfTreatmentPeriod

    // note: Dosage correctness should be covered by a prospective use case

}

