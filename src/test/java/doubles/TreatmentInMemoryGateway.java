package doubles;

import entities.Treatment;
import usecases.TreatmentGateway;

import java.util.List;
import java.util.Optional;

public class TreatmentInMemoryGateway extends InMemoryGateway<Treatment> implements TreatmentGateway {
    @Override
    public boolean doesTreatmentExist(Treatment treatment) {
        List<Treatment> treatments = findAll();

        Optional<Treatment> result = treatments.stream()
                .filter(t -> t.doesOverlap(treatment))
                .findFirst();

        return result.isPresent();
    }
}
