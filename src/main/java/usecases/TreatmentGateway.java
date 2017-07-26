package usecases;

import entities.Treatment;

public interface TreatmentGateway {
    Treatment save(Treatment treatment);

    boolean doesTreatmentExist(Treatment treatment);
}
