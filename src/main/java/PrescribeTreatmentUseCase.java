import entities.Dosage;
import entities.Drug;
import entities.Treatment;
import entities.TreatmentPeriod;
import usecases.DosageGateway;
import usecases.DrugGateway;
import usecases.TreatmentGateway;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;

class PrescribeTreatmentUseCase {
    private TreatmentGateway treatmentGateway;
    private DrugGateway drugGateway;
    private DosageGateway dosageGateway;

    public PrescribeTreatmentUseCase(TreatmentGateway treatmentGateway,
                                     DrugGateway drugGateway,
                                     DosageGateway dosageGateway) {
        this.treatmentGateway = treatmentGateway;
        this.drugGateway = drugGateway;
        this.dosageGateway = dosageGateway;
    }

    public Treatment prescribe(Map<String, String> attributes ) {
        requireNonEmpty(attributes.get("startDate"), "Start Date");
        checkDateFormat(attributes.get("startDate"), "Start Date");

        requireNonEmpty(attributes.get("periodAmount"), "Amount of Treatment Period");
        requireNonEmpty(attributes.get("periodUnit"), "Unit of Treatment Period");
        requireNonEmpty(attributes.get("drugId"), "Drug Id");
        requireNonEmpty(attributes.get("dosageId"), "Dosage Id");

        checkIntegerFormat(attributes.get("periodAmount"), "Amount of Treatment Period");
        requirePositiveNumber(attributes.get("periodAmount"), "Amount of Treatment Period");
        requireNonZero(attributes.get("periodAmount"), "Amount of Treatment Period");

        Drug drug = drugGateway.findById(attributes.get("drugId"));

        if (Objects.isNull(drug))
            throw new PrescribeTreatmentException(
                    String.format("No drug found with '%s' id", attributes.get("drugId")));

        Dosage dosage = dosageGateway.findById(attributes.get("dosageId"));
        if (Objects.isNull(dosage))
            throw new PrescribeTreatmentException(
                    String.format("No dosage found with '%s' id", attributes.get("dosageId")));

        Treatment treatment = new Treatment();

        treatment.setDrug(drug);
        treatment.setDosage(dosage);

        LocalDate startsOn = LocalDate.parse(attributes.get("startDate"));
        treatment.setStartsOn(startsOn);

        TreatmentPeriod period = new TreatmentPeriod(
                Integer.parseInt(attributes.get("periodAmount")),
                ChronoUnit.valueOf(attributes.get("periodUnit").toUpperCase())
        );

        treatment.setPeriod(period);
        treatment.setStopsOn(period.calcEnd(startsOn));

        treatmentGateway.save(treatment);

        return treatment;
    }

    private void requireNonEmpty(String attrValue, String attrName) {
        if (Objects.isNull(attrValue) || attrValue.isEmpty())
            throw new PrescribeTreatmentException(
                    String.format("'%s' must be present", attrName));
    }

    private void checkDateFormat(String attrValue, String attrName) {
        try {
            LocalDate.parse(attrValue, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new PrescribeTreatmentException(
                    String.format("'%s' is malformed: '%s'. Accepted format is 'yyyy-MM-dd'",
                            attrName, attrValue));
        }
    }

    private void checkIntegerFormat(String attrValue, String attrName) {
        try {
            Integer.parseInt(attrValue);
        } catch (NumberFormatException e) {
            throw new PrescribeTreatmentException(
                    String.format("'%s' is malformed: '%s'", attrName, attrValue));
        }
    }

    private void requirePositiveNumber(String attrValue, String attrName) {
        long result = Long.parseLong(attrValue);
        if (result < 0)
            throw new PrescribeTreatmentException(
                String.format("'%s' must be a positive value", attrName));
    }

    private void requireNonZero(String attrValue, String attrName) {
        long result = Long.parseLong(attrValue);

        if (result == 0)
            throw new PrescribeTreatmentException(
                    String.format("'%s' must be greater than zero", attrName));
    }
}

class PrescribeTreatmentException extends RuntimeException {
    public PrescribeTreatmentException(String message) {
        super(message);
    }
}
