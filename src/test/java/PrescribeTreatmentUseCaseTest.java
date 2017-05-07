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

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

public class PrescribeTreatmentUseCaseTest {

    private TreatmentGateway treatmentGateway = new TreatmentInMemoryGateway();
    private DrugGateway drugGateway = new DrugInMemoryGateway();
    private DosageGateway dosageGateway = new DosageInMemoryGateway();
    private PrescribeTreatmentUseCase prescribeTreatmentUseCase;

    private Drug drug;
    private Dosage dosage;

    @Before
    public void setUp() {
        prescribeTreatmentUseCase = new PrescribeTreatmentUseCase(treatmentGateway, drugGateway, dosageGateway);

        if (Objects.isNull(drug)) {
            drug = new Drug("Arsen Alb");
            drugGateway.save(drug);
        }

        if (Objects.isNull(dosage)) {
            dosage = new Dosage();
            dosageGateway.save(dosage);
        }
    }

    @Test
    public void shouldYieldPersistedTreatment() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        Treatment t = prescribeTreatmentUseCase.prescribe(attributes);

        assertNotNull(t.getId());
        assertNotEquals("", t.getId());
    }

    @Test
    public void assignsDrugAndDurationAttributes() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        Treatment t = prescribeTreatmentUseCase.prescribe(attributes);

        assertEquals(LocalDate.of(2017, Month.MARCH, 16), t.getStartsOn());
        assertEquals(LocalDate.of(2017, Month.APRIL, 15), t.getStopsOn());
        assertEquals(new TreatmentPeriod(1, ChronoUnit.MONTHS), t.getPeriod());

        assertEquals(drug, t.getDrug());
        assertEquals(dosage, t.getDosage());
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void zeroTreatmentPeriodIsNotAllowed() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "0");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void negativeTreatmentPeriodIsNotAllowed() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "-1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void treatmentPeriodFormatToBeAnInteger() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "one");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_StartOfTreatmentShouldBePresent() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_startOfTreatmentShouldNotBeEmpty() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    // TODO: + ShouldNotBeEmpty
    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_amountOfTreatmentPeriodShouldBePresent() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    // TODO: + ShouldNotBeEmpty
    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_unitOfTreatmentPeriodShouldBePresent() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugIdShouldBePresent() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugIdShouldNotBeEmpty() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", "");
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugIdShouldExist() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", "nonExistingDrugId");
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugDosageIdShouldBePresent() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugDosageIdShouldNotBeEmpty() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", "");

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void inputs_drugDosageShouldExist() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017-03-16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", "nonExistingDosageId");

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    @Test
    public void treatmentCanStartInTheFuture() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", tomorrow.toString());
        attributes.put("periodAmount", "2");
        attributes.put("periodUnit", "Days");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        Treatment t = prescribeTreatmentUseCase.prescribe(attributes);

        assertEquals(tomorrow, t.getStartsOn());
        assertEquals(tomorrow.plusDays(1), t.getStopsOn());
    }

    @Test(expected = PrescribeTreatmentException.class)
    public void treatmentStartDateMatches_yyyy_MM_dd_Format() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("startDate", "2017 03 16");
        attributes.put("periodAmount", "1");
        attributes.put("periodUnit", "Months");
        attributes.put("drugId", drug.getId());
        attributes.put("dosageId", dosage.getId());

        prescribeTreatmentUseCase.prescribe(attributes);
    }

    // todo: print date pattern in exception message when input date is malformed

    // todo: test -> attributes for "prescribe" are duplicated
    // todo: code -> duplicated access to attributes
    // todo: should the result be Treatment or respond model/object?

    // todo: is it possible that created treatment's start date is after end date?

    //todo: Treatment period long vs int - to accept Long.MAX_VALUE
    // acceptsLongAmountOfTreatmentPeriod

    // note: Dosage correctness should be covered by a prospective use case

}

