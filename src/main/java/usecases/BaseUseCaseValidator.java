package usecases;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Predicate;

public abstract class BaseUseCaseValidator implements UseCaseValidator {
    private Map<String, String> attributes = new HashMap<>();
    private List<ValidationRule> rules = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    protected UseCaseRequest request;

    public BaseUseCaseValidator() {
        fillAttributes();
        initializeRules();
    }

    @Override
    public void validate(UseCaseRequest request) {
        this.request = request;
        errors.clear();
        rules.forEach((rule) -> rule.validate());
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

    protected abstract void fillAttributes();
    protected abstract void initializeRules();

    protected void addAttribute(String attribute, String attributeName) {
        attributes.put(attribute, attributeName);
    }

    protected void addRule(ValidationRule rule) {
        rules.add(rule);
    }

    // todo: Checks - are separate class?
    // todo: logging errors - some kind of Logger that is accessible by Checks and Validatior

    protected boolean requireNonEmpty(String attribute) {
        String attrValue = request.getParameter(attribute);
        if (Objects.isNull(attrValue) || attrValue.isEmpty()) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' must be present", attrName);
            return false;
        }
        return true;
    }

    protected boolean checkDateFormat(String attribute) {
        if (request.isParameterMissing(attribute)) return false;

        String attrValue = request.getParameter(attribute);
        try {
            LocalDate.parse(attrValue, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' is malformed: '%s'. Accepted format is 'yyyy-MM-dd'", attrName, attrValue);
            return false;
        }
        return true;
    }

    protected boolean checkIntegerFormat(String attribute) {
        if (request.isParameterMissing(attribute)) return false;

        String attrValue = request.getParameter(attribute);
        try {
            Integer.parseInt(attrValue);
        } catch (NumberFormatException e) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' is malformed: '%s'", attrName, attrValue);
            return false;
        }
        return true;
    }

    protected boolean requirePositiveNumber(String attribute) {
        if (request.isParameterMissing(attribute)) return false;

        String attrValue = request.getParameter(attribute);
        try {
            long result = Long.parseLong(attrValue);
            if (result < 0) {
                String attrName = attributes.get(attribute);
                failCheck("'%s' must be a positive value", attrName);
                return false;
            }
        } catch (NumberFormatException e) {
            skipCheck();
            return false;
        }
        return true;
    }

    protected boolean requireNonZero(String attribute) {
        if (request.isParameterMissing(attribute)) return false;

        String attrValue = request.getParameter(attribute);
        try {
            long result = Long.parseLong(attrValue);
            if (result == 0) {
                String attrName = attributes.get(attribute);
                failCheck("'%s' must be greater than zero", attrName);
                return false;
            }
        } catch (NumberFormatException e) {
            skipCheck();
            return false;
        }
        return true;
    }

    protected boolean assertTruthCondition(String attribute, Predicate<String> condition) {
        if (request.isParameterMissing(attribute)) return false;

        String attrValue = request.getParameter(attribute);

        if (!condition.test(attrValue)) {
            String attrName = attributes.get(attribute);
            failCheck("'%s' has illegal value: '%s'", attrName, attrValue);
            return false;
        }
        return true;
    }

    protected void failCheck(String message, Object... args) {
        errors.add(String.format(message, args));
    }

    protected void skipCheck() {}
}
