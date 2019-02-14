package usecases.directionusage;

import entities.Treatment;

import java.time.LocalDate;

public class GetTreatmentUsageUseCase {
    private GetDirectionalMode modeFactory;

    public GetTreatmentUsageUseCase(GetDirectionalMode mode) {
        this.modeFactory = mode;
    }

    public boolean isUsedOn(Treatment treatment, LocalDate date) {
        if (!treatment.isWithinTreatmentPeriod(date))
            return false;

        Directional mode = modeFactory.getMode(treatment.getDirectionMode());
        return mode.isUsedOn(treatment.getStartsOn(), date);
    }
}
