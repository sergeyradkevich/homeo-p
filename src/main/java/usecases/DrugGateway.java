package usecases;

import entities.Drug;

import java.util.List;

public interface DrugGateway {
    List<Drug> findAll();

    List<Drug> findPrescribedDrugs();

    Drug save(Drug drug);

    Drug findById(String drugId);
}
