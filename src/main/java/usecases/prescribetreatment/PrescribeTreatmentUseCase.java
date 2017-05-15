package usecases.prescribetreatment;

import entities.Dosage;
import entities.Drug;
import entities.Treatment;
import entities.TreatmentPeriod;
import usecases.DosageGateway;
import usecases.DrugGateway;
import usecases.TreatmentGateway;
import usecases.UseCaseValidator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class PrescribeTreatmentUseCase {
    private final TreatmentGateway treatmentGateway;
    private final DrugGateway drugGateway;
    private final DosageGateway dosageGateway;
    private final UseCaseValidator validator;

    public PrescribeTreatmentUseCase(TreatmentGateway treatmentGateway,
                                     DrugGateway drugGateway,
                                     DosageGateway dosageGateway,
                                     UseCaseValidator validator) {
        this.treatmentGateway = treatmentGateway;
        this.drugGateway = drugGateway;
        this.dosageGateway = dosageGateway;
        this.validator = validator;
    }

    public Treatment prescribe(PrescribeTreatmentRequest request) {
        validator.validate(request);
        if (!validator.isValid()) throw new PrescribeTreatmentException();

        Drug drug = drugGateway.findById(request.drugId());

        if (Objects.isNull(drug))
            throw new PrescribeTreatmentException(
                    String.format("No drug found with '%s' id", request.drugId()));

        Dosage dosage = dosageGateway.findById(request.dosageId());
        if (Objects.isNull(dosage))
            throw new PrescribeTreatmentException(
                    String.format("No dosage found with '%s' id", request.dosageId()));

        Treatment treatment = new Treatment();

        treatment.setDrug(drug);
        treatment.setDosage(dosage);

        LocalDate startsOn = LocalDate.parse(request.startDate());
        treatment.setStartsOn(startsOn);

        TreatmentPeriod period = new TreatmentPeriod(
                Integer.parseInt(request.periodAmount()),
                ChronoUnit.valueOf(request.periodUnit().toUpperCase())
        );

        treatment.setPeriod(period);
        treatment.setStopsOn(period.calcEnd(startsOn));

        treatmentGateway.save(treatment);

        return treatment;
    }
}

