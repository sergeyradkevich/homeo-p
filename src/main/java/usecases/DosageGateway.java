package usecases;

import entities.Dosage;

public interface DosageGateway {
    Dosage findDrugDosageByTreatmentId(String drugId, String treatmentId);

    Dosage save(Dosage dosage);
}
