package usecases.prescribetreatment;

import entities.*;
import values.TreatmentPeriod;
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
        if (!validator.isValid()) throw new PrescribeTreatmentException(validator.errors().toString());

        Drug drug = drugGateway.findById(request.drugId());

        if (Objects.isNull(drug))
            throw new PrescribeTreatmentException(
                    String.format("No drug found with '%s' id", request.drugId()));

        Dosage dosage = dosageGateway.findById(request.dosageId());
        if (Objects.isNull(dosage))
            throw new PrescribeTreatmentException(
                    String.format("No dosage found with '%s' id", request.dosageId()));

        Treatment treatment = this.createTreatment(request, drug, dosage);

        if (treatmentGateway.doesTreatmentExist(treatment))
            throw new PrescribeTreatmentException(String.format(
                    "The treatment that is being creating overlaps with the already prescribed drug: start date %s end date %s",
                    treatment.getStartsOn(), treatment.getStopsOn()));

        treatmentGateway.save(treatment);

        return treatment;
    }

    private Treatment createTreatment(PrescribeTreatmentRequest request, Drug drug, Dosage dosage) {
        Treatment treatment = new Treatment();

        treatment.setDrug(drug);
        treatment.setDosage(dosage);

        LocalDate startsOn = LocalDate.parse(request.startDate());
        treatment.setStartsOn(startsOn);

        DirectionModeType type = DirectionModeType.of(request.directionModeType());
        DirectionMode directionMode = new DirectionMode(type);
        if (directionMode.isPeriodically()) {
            directionMode.setTaken(Integer.parseInt(request.directionModeTaken()));
            directionMode.setInterval(Integer.parseInt(request.directionModeInterval()));
        }
        if (directionMode.isDecreasing()) {
            directionMode.setDelta(Integer.parseInt(request.directionModeDelta()));
            directionMode.setLimit(Integer.parseInt(request.directionModeLimit()));
        }
        treatment.setDirectionMode(directionMode);

        TreatmentPeriod period = new TreatmentPeriod(
                Integer.parseInt(request.periodAmount()),
                ChronoUnit.valueOf(request.periodUnit().toUpperCase()));
        if (directionMode.isDecreasing()) {
            int duration = directionMode.calcDecreasingDaysAmountUntilLimitInclusive(dosage.getDailyIntakeAmount());
            period = period.extendIfDurationLonger(duration);
        }
        treatment.setPeriod(period);
        treatment.setStopsOn(period.calcEnd(startsOn));


        return treatment;
    }

}

