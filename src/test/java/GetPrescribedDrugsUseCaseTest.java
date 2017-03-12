import doubles.DosageInMemoryGateway;
import doubles.DrugInMemoryGateway;
import entities.Dosage;
import entities.Dose;
import entities.Drug;
import org.junit.Before;
import org.junit.Test;
import usecases.DosageGateway;
import usecases.DrugGateway;
import usecases.GetPrescribedDrugsUseCase;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetPrescribedDrugsUseCaseTest {

    //todo: set up gateways through injection
    private DrugGateway drugGateway = new DrugInMemoryGateway();
    private DosageGateway dosageGateway = new DosageInMemoryGateway();

    private GetPrescribedDrugsUseCase useCase;

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
    public void givenOnePrescribedDrugReturnsOneDrug() {
        Drug drug = new Drug("Arsen Alb");
        drugGateway.save(drug);

        List<Drug> drugs = useCase.getPrescribedDrugs();

        assertEquals(drug, drugs.get(0));
        assertEquals(1, drugs.size());
    }

    @Test
    public void givenManyPrescribedDrugsReturnsThem() {
        String[] names = {"Arsen Alb 1", "Arsen Alb 2", "Arsen Alb 3"};

        List<Drug> persisted = Stream.of(names)
                .map(name -> drugGateway.save(new Drug(name)))
                .sorted(Comparator.comparing(Drug::getId))
                .collect(Collectors.toList());

        List<Drug> drugs = useCase.getPrescribedDrugs();

        assertEquals(persisted, drugs);
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
}