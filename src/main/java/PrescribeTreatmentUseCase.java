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
import java.util.*;
import java.util.function.Consumer;

class PrescribeTreatmentUseCase {
    private final TreatmentGateway treatmentGateway;
    private final DrugGateway drugGateway;
    private final DosageGateway dosageGateway;
    private final UseCaseValidator validator;

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

    public PrescribeTreatmentUseCase(TreatmentGateway treatmentGateway,
                                     DrugGateway drugGateway,
                                     DosageGateway dosageGateway,
                                     UseCaseValidator validator) {
        this.treatmentGateway = treatmentGateway;
        this.drugGateway = drugGateway;
        this.dosageGateway = dosageGateway;
        this.validator = validator;
    }
}

interface UseCaseValidator {
    void validate(UseCaseRequest request);
    boolean isValid();
    List<String> errors();
}

abstract class BaseUseCaseValidator implements UseCaseValidator {
    protected Map<String, String> attributes = new HashMap<>();
    protected Map<String, Consumer<String>> rules = new HashMap<>();
    protected List<String> errors = new ArrayList<>();
    protected UseCaseRequest request;

    public BaseUseCaseValidator() {
        fillRequiredAttributes();
        initializeRules();
    }

    @Override
    public void validate(UseCaseRequest request) {
        this.request = request;
        errors.clear();
        rules.forEach((attribute, rule) -> rule.accept(attribute));
    }

    @Override
    public boolean isValid() {
        return errors.isEmpty();
    }

    // todo: List of attribute + its errors
    @Override
    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }

    protected abstract void fillRequiredAttributes();
    protected abstract void initializeRules();

    protected void addRule(String attribute, Consumer<String> rule) {
        rules.put(attribute, rule);
    }

    protected void requireNonEmpty(String attribute) {
        String attrValue = request.getParameter(attribute);
        if (Objects.isNull(attrValue) || attrValue.isEmpty()) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' must be present", attrName);
        }
    }

    protected void checkDateFormat(String attribute) {
        if (request.isParameterMissing(attribute)) return;

        String attrValue = request.getParameter(attribute);
        try {
            LocalDate.parse(attrValue, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' is malformed: '%s'. Accepted format is 'yyyy-MM-dd'", attrName, attrValue);
        }
    }

    protected void checkIntegerFormat(String attribute) {
        if (request.isParameterMissing(attribute)) return;

        String attrValue = request.getParameter(attribute);
        try {
            Integer.parseInt(attrValue);
        } catch (NumberFormatException e) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' is malformed: '%s'", attrName, attrValue);
        }
    }

    protected void requirePositiveNumber(String attribute) {
        if (request.isParameterMissing(attribute)) return;

        String attrValue = request.getParameter(attribute);
        try {
            long result = Long.parseLong(attrValue);
            if (result < 0) {
                String attrName = attributes.get(attribute);
                failCheck("'%s' must be a positive value", attrName);
            }
        } catch (NumberFormatException e) {
            skipCheck();
        }
    }

    protected void requireNonZero(String attribute) {
        if (request.isParameterMissing(attribute)) return;

        String attrValue = request.getParameter(attribute);
        try {
            long result = Long.parseLong(attrValue);
            if (result == 0) {
                String attrName = attributes.get(attribute);
                failCheck("'%s' must be greater than zero", attrName);
            }
        } catch (NumberFormatException e) {
            skipCheck();
        }
    }

    private void failCheck(String message, Object... args) {
        errors.add(String.format(message, args));
    }

    private void skipCheck() {}
}

class Rule <T> implements Consumer<T> {
    private Collection<Consumer<T>> checks = new ArrayList<>();

    public Rule <T> add(Consumer<T> check) {
        checks.add(check);
        return this;
    }

    @Override
    public void accept(T t) {
        checks.forEach(check -> check.accept(t));
    }
}

class PrescribeTreatmentValidator extends BaseUseCaseValidator {

    @Override
    protected void fillRequiredAttributes() {
        attributes.put("startDate", "Start Date");
        attributes.put("periodAmount", "Amount of Treatment Period");
        attributes.put("periodUnit", "Unit of Treatment Period");
        attributes.put("drugId", "Drug Id");
        attributes.put("dosageId", "Dosage Id");
    }

    @Override
    protected void initializeRules() {
        Consumer<String> starDateRule = new Rule<String>()
                .add(this::requireNonEmpty)
                .add(this::checkDateFormat);

        Consumer<String> periodAmountRule = new Rule<String>()
                .add(this::requireNonEmpty)
                .add(this::checkIntegerFormat)
                .add(this::requirePositiveNumber)
                .add(this::requireNonZero);

        addRule("startDate", starDateRule);
        addRule("periodAmount", periodAmountRule);
        addRule("periodUnit", this::requireNonEmpty);
        addRule("drugId", this::requireNonEmpty);
        addRule("dosageId", this::requireNonEmpty);
    }
}

abstract class UseCaseRequest <T extends UseCaseRequest> {
    private Map<String, String> parameters = new HashMap<>();

    protected String getParameter(String attribute) {
        return parameters.get(attribute);
    }

    protected boolean isParameterMissing(String attribute) {
        return Objects.isNull(getParameter(attribute));
    }

    protected T buildParameterAndReturnSelf(String parameter, String value) {
        setParameter(parameter, value);
        return self();
    }

    abstract protected T self();

    private void setParameter(String parameter, String value) {
        parameters.put(parameter, value);
    }
}

class PrescribeTreatmentRequest extends UseCaseRequest<PrescribeTreatmentRequest> {

    public PrescribeTreatmentRequest addStartDate(String value) {
        return buildParameterAndReturnSelf("startDate", value);
    }
    public PrescribeTreatmentRequest addPeriodAmount(String value) {
        return buildParameterAndReturnSelf("periodAmount", value);
    }
    public PrescribeTreatmentRequest addPeriodUnit(String value) {
        return buildParameterAndReturnSelf("periodUnit", value);
    }
    public PrescribeTreatmentRequest addDrugId(String value) {
        return buildParameterAndReturnSelf("drugId", value);
    }
    public PrescribeTreatmentRequest addDosageId(String value) {
        return buildParameterAndReturnSelf("dosageId", value);
    }

    String startDate() {
        return getParameter("startDate");
    }
    String periodAmount() {
        return getParameter("periodAmount");
    }
    String periodUnit() {
        return getParameter("periodUnit");
    }
    String drugId() {
        return getParameter("drugId");
    }
    String dosageId() {
        return getParameter("dosageId");
    }

    @Override
    protected PrescribeTreatmentRequest self() {
        return this;
    }
}

class PrescribeTreatmentException extends RuntimeException {

    public PrescribeTreatmentException() {
        super();
    }

    public PrescribeTreatmentException(String message) {
        super(message);
    }
}
