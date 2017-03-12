package doubles;

import entities.Dosage;
import usecases.DosageGateway;

import java.util.List;
import java.util.Optional;

public class DosageInMemoryGateway extends InMemoryGateway<Dosage> implements DosageGateway {
    public Dosage findDrugDosageByTreatmentId(String drugId, String treatmentId) {
        List<Dosage> dosages = findAll();
        Optional<Dosage> result = dosages.stream()
                .filter(d -> d.getTreatmentId().equals(treatmentId))
                .findFirst();
        return result.get();
    }
}

