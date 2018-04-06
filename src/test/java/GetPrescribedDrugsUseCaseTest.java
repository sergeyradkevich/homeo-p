import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import doubles.AlwaysValidUseCaseValidatorStub;
import entities.Dosage;
import entities.Dose;
import entities.Drug;
import net.lamberto.junit.GuiceJUnitRunner;
import net.lamberto.junit.GuiceJUnitRunner.GuiceModules;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import testsetup.CommonTestModule;
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

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ CommonTestModule.class, GetPrescribedDrugsUseCaseTest.TestModule.class })
public class GetPrescribedDrugsUseCaseTest {

    public static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(UseCaseValidator.class).to(AlwaysValidUseCaseValidatorStub.class);
        }

        @Provides
        GetPrescribedDrugsUseCase getPrescribedDrugsUseCase(DrugGateway drugGateway, DosageGateway dosageGateway) {
            return new GetPrescribedDrugsUseCase(drugGateway, dosageGateway);
        }

        @Provides
        PrescribeTreatmentUseCase prescribeTreatmentUseCase(TreatmentGateway treatmentGateway,
                                                            DrugGateway drugGateway,
                                                            DosageGateway dosageGateway,
                                                            UseCaseValidator validator) {
            return new PrescribeTreatmentUseCase(
                    treatmentGateway, drugGateway, dosageGateway, validator);
        }
    }

    @Inject
    private DosageGateway dosageGateway;
    @Inject
    private DrugGateway drugGateway;

    @Inject
    private GetPrescribedDrugsUseCase useCase;
    @Inject
    private PrescribeTreatmentUseCase prescribeTreatmentUseCase;

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

    @Ignore
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
}