package usecases;

import entities.Dosage;
import entities.Drug;

import java.util.List;

public class GetPrescribedDrugsUseCase {
    private DrugGateway drugGateway;
    private DosageGateway dosageGateway;

    public GetPrescribedDrugsUseCase(DrugGateway drugGateway, DosageGateway dosageGateway) {
        this.drugGateway = drugGateway;
        this.dosageGateway = dosageGateway;
    }

    public List<Drug> getPrescribedDrugs() {
        return drugGateway.findPrescribedDrugs();
    }

    public Dosage getDrugDosage(String drugId, String treatmentId) {
        return dosageGateway.findDrugDosageByTreatmentId(drugId, treatmentId);
    }
}
