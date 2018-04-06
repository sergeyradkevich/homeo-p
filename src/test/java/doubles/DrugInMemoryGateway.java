package doubles;

import entities.Drug;
import entities.Treatment;
import usecases.DrugGateway;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DrugInMemoryGateway extends InMemoryGateway<Drug> implements DrugGateway {

    private TreatmentInMemoryGateway drugSourceThroughTreatment;

    public DrugInMemoryGateway() {}

    public DrugInMemoryGateway(TreatmentInMemoryGateway treatmentGateway) {
        this.drugSourceThroughTreatment = treatmentGateway;
    }

    @Override
    public List<Drug> findPrescribedDrugs() {
        return drugSourceThroughTreatment.findAll()
                .stream()
                .map(Treatment::getDrug)
                .sorted(Comparator.comparing(Drug::getId))
                .collect(Collectors.toList());
    }
}
