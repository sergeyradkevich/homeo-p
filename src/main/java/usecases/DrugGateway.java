package usecases;

import entities.Drug;

import java.util.List;

public interface DrugGateway {
    List<Drug> findAll();

    Drug save(Drug drug);
}
