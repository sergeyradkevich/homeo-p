package usecases.prescribetreatment;

import doubles.DosageInMemoryGateway;
import doubles.DrugInMemoryGateway;
import doubles.TreatmentInMemoryGateway;
import entities.Dosage;
import entities.Drug;
import entities.Treatment;
import entities.TreatmentPeriod;
import org.junit.Before;
import org.junit.Test;
import usecases.DosageGateway;
import usecases.DrugGateway;
import usecases.TreatmentGateway;
import usecases.UseCaseValidator;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.junit.Assert.*;

public class PrescribeTreatmentUseCaseTest {

    private TreatmentGateway treatmentGateway = new TreatmentInMemoryGateway();
    private DrugGateway drugGateway = new DrugInMemoryGateway();
    private DosageGateway dosageGateway = new DosageInMemoryGateway();
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

    @Test
    public void treatmentCanStartInTheFuture() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        PrescribeTreatmentRequest request = new PrescribeTreatmentRequest()
                .addStartDate(tomorrow.toString())
                .addPeriodAmount("2")
                .addPeriodUnit("Days")
                .addDrugId(drug.getId())
                .addDosageId(dosage.getId());

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

    private void setUpPrescribeTreatmentRequest() {
        // it ends on 2017-04-15
        request = new PrescribeTreatmentRequest()
            .addStartDate("2017-03-16")
            .addPeriodAmount("1")
            .addPeriodUnit("Months")
            .addDrugId(drug.getId())
            .addDosageId(dosage.getId());
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

