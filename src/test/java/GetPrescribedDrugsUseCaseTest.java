import doubles.AlwaysValidUseCaseValidatorStub;
import doubles.DosageInMemoryGateway;
import doubles.DrugInMemoryGateway;
import doubles.TreatmentInMemoryGateway;
import entities.Dosage;
import entities.Dose;
import entities.Drug;
import org.junit.Before;
import org.junit.Test;
import usecases.*;
import usecases.prescribetreatment.PrescribeTreatmentRequest;
import usecases.prescribetreatment.PrescribeTreatmentUseCase;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetPrescribedDrugsUseCaseTest {

    private TreatmentGateway treatmentGateway = new TreatmentInMemoryGateway();
    //todo: set up gateways through injection
    private DrugGateway drugGateway = new DrugInMemoryGateway((TreatmentInMemoryGateway) treatmentGateway);
    private DosageGateway dosageGateway = new DosageInMemoryGateway();

    private GetPrescribedDrugsUseCase useCase;

    //todo: set up use case through injection/setting up a context
    private PrescribeTreatmentUseCase prescribeTreatmentUseCase = new PrescribeTreatmentUseCase(
            treatmentGateway, drugGateway, dosageGateway, new AlwaysValidUseCaseValidatorStub());

    @Before
    public void setUp() {
        useCase = new usecases.GetPrescribedDrugsUseCase(drugGateway, dosageGateway);
    }

    @Test
    public void givenNoDrugsReturnsNoneDrugs() {
        List<Drug> drugs = useCase.getPrescribedDrugs();
        assertTrue(drugs.isEmpty());
    }

    @Test
    public void givenOnePrescribedForTreatmentDrugReturnsOneDrug() {
        givenNonPrescribedDrug();
        Drug prescribedDrug = givenPrescribedDrugs("Arsen Alb").get(0);

        List<Drug> drugs = useCase.getPrescribedDrugs();

        assertEquals(prescribedDrug, drugs.get(0));
        assertEquals(1, drugs.size());
    }

    @Test
    public void givenManyPrescribedDrugsReturnsThem() {
        givenNonPrescribedDrug();
        List<Drug> prescribed = givenPrescribedDrugs("Arsen Alb 1", "Arsen Alb 2", "Arsen Alb 3");

        List<Drug> drugs = useCase.getPrescribedDrugs();

        assertEquals(prescribed, drugs);
    }

    @Test
    public void returnsDrugDosageByTreatmentId() {
        Drug drug = new Drug("Arsen Alb");
        drugGateway.save(drug);

        Dose dose = new Dose();
        dose.setQuantity(3);
        dose.setForm("pill");

        String treatmentId = "treatment1";

        Dosage dosage = new Dosage();
        dosage.setDose(dose);
        dosage.setDailyIntakeAmount(2);
        dosage.setTreatmentId(treatmentId);

        dosageGateway.save(dosage);

        Dosage d = useCase.getDrugDosage(drug.getId(), treatmentId);

        assertEquals(dosage, d);

        assertEquals(3, d.getDose().getQuantity());
        assertEquals("pill", d.getDose().getForm());
        assertEquals(2, d.getDailyIntakeAmount());
    }

    private Drug givenNonPrescribedDrug() {
        Drug nonPrescribed = new Drug("Vocara");
        drugGateway.save(nonPrescribed);

        return nonPrescribed;
    }

    private List<Drug> givenPrescribedDrugs(String... names) {
        List<Drug> prescribed = Stream.of(names)
                .map(name -> drugGateway.save(new Drug(name)))
                .sorted(Comparator.comparing(Drug::getId))
                .collect(Collectors.toList());

        Dose dose = new Dose();
        dose.setQuantity(3);
        dose.setForm("pill");

        Dosage dosage = new Dosage();
        dosage.setDose(dose);
        dosage.setDailyIntakeAmount(2);
        dosageGateway.save(dosage);

        prescribed.forEach(
                drug -> prescribeTreatmentUseCase.prescribe(
                        new PrescribeTreatmentRequest()
                                .addDrugId(drug.getId())
                                .addDosageId(dosage.getId())
                                .addStartDate(LocalDate.now().toString())
                                .addPeriodAmount("6")
                                .addPeriodUnit("Months"))
        );

        return prescribed;
    }

    // todo: switch returnsDrugDosageByTreatmentId to treatment source
    // todo: check why in-memory gateway has no affect on different test even if there is no data truncation
}